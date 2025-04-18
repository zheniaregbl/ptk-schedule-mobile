package com.syndicate.ptkscheduleapp.feature.schedule.domain.use_case

import com.skydoves.sandwich.ApiResponse
import com.syndicate.ptkscheduleapp.core.common.util.ScheduleUtil
import com.syndicate.ptkscheduleapp.core.data.mapper.toDTO
import com.syndicate.ptkscheduleapp.core.domain.model.PairItem
import com.syndicate.ptkscheduleapp.core.domain.repository.PreferencesRepository
import com.syndicate.ptkscheduleapp.core.domain.repository.ScheduleRepository
import com.syndicate.ptkscheduleapp.core.domain.use_case.CaseResult
import com.syndicate.ptkscheduleapp.core.domain.use_case.UserIdentifier
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class GetScheduleCase(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        userIdentifier: UserIdentifier,
        lastUpdateTime: LocalDateTime?
    ): CaseResult<List<List<PairItem>>> {

        return when (val response = scheduleRepository.getSchedule(userIdentifier)) {

            is ApiResponse.Failure.Error -> CaseResult.Error("Error getSchedule")

            is ApiResponse.Failure.Exception -> CaseResult.Error("Exception getSchedule")

            is ApiResponse.Success<List<PairItem>> -> {

                if (preferencesRepository.getLastUpdateScheduleTime() != lastUpdateTime) {

                    preferencesRepository
                        .saveLocalSchedule(
                            Json.encodeToString(response.data.map { it.toDTO() })
                        )

                    lastUpdateTime?.let {
                        preferencesRepository.saveLastUpdateScheduleTime(it)
                    }
                }

                CaseResult.Success(ScheduleUtil.getWeekSchedule(response.data))
            }
        }
    }
}