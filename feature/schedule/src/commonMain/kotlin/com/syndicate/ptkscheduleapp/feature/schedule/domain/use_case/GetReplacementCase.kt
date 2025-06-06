package com.syndicate.ptkscheduleapp.feature.schedule.domain.use_case

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ktor.statusCode
import com.skydoves.sandwich.message
import com.syndicate.ptkscheduleapp.core.common.util.ScheduleUtil
import com.syndicate.ptkscheduleapp.core.domain.model.ReplacementItem
import com.syndicate.ptkscheduleapp.core.domain.repository.PreferencesRepository
import com.syndicate.ptkscheduleapp.core.domain.repository.ScheduleRepository
import com.syndicate.ptkscheduleapp.core.domain.use_case.CaseResult
import com.syndicate.ptkscheduleapp.core.domain.use_case.UserIdentifier
import com.syndicate.ptkscheduleapp.feature.schedule.common.util.Logger
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.JsonObject

internal class GetReplacementCase(
    private val scheduleRepository: ScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        userIdentifier: UserIdentifier,
        lastUpdateTime: LocalDateTime?,
        dateStart: String = "",
        dateEnd: String = "",
    ): CaseResult<List<ReplacementItem>> {

        return when (val response = scheduleRepository.getReplacement(dateStart, dateEnd, userIdentifier)) {

            is ApiResponse.Failure.Error -> {
                Logger.error(response.message())
                CaseResult.Error("Ошибка ${response.statusCode} при попытке получения замен")
            }

            is ApiResponse.Failure.Exception -> {
                Logger.error(response.message())
                CaseResult.Error("Ошибка при попытке получения замен")
            }

            is ApiResponse.Success<JsonObject> -> {

                lastUpdateTime?.let {
                    if (preferencesRepository.getLastUpdateReplacementTime() != lastUpdateTime) {
                        preferencesRepository.saveLocalReplacement(response.data.toString())
                        preferencesRepository.saveLastUpdateReplacementTime(lastUpdateTime)
                    }
                }

                val replacement = when (userIdentifier) {
                    is UserIdentifier.Student ->
                        ScheduleUtil.getReplacementFromJsonForStudent(
                            response.data,
                            userIdentifier.group
                        )
                    is UserIdentifier.Teacher ->
                        ScheduleUtil.getReplacementFromJsonForTeacher(
                            response.data,
                            userIdentifier.name
                        )
                }

                CaseResult.Success(replacement)
            }
        }
    }
}