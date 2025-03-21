package com.syndicate.ptkscheduleapp.feature.schedule.data.repository

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendMapSuccess
import com.syndicate.ptkscheduleapp.feature.schedule.data.mapper.toModel
import com.syndicate.ptkscheduleapp.core.data.network.RemoteScheduleDataSource
import com.syndicate.ptkscheduleapp.feature.schedule.domain.model.PairItem
import com.syndicate.ptkscheduleapp.feature.schedule.domain.model.ScheduleInfo
import com.syndicate.ptkscheduleapp.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.serialization.json.JsonObject

internal class DefaultScheduleRepository(
    private val remoteScheduleDataSource: RemoteScheduleDataSource
): ScheduleRepository {

    override suspend fun getSchedule(group: String): ApiResponse<List<PairItem>> {
        return remoteScheduleDataSource
            .getSchedule(group)
            .suspendMapSuccess { listPair!!.map { it.toModel() } }
    }

    override suspend fun getReplacement(
        dateStart: String,
        dateEnd: String
    ): ApiResponse<JsonObject> {
        return remoteScheduleDataSource
            .getReplacement(dateStart, dateEnd)
            .suspendMapSuccess { replacements!! }
    }

    override suspend fun getScheduleInfo(): ApiResponse<ScheduleInfo> {
        return remoteScheduleDataSource
            .getScheduleInfo()
            .suspendMapSuccess { scheduleInfoDTO!!.toModel() }
    }
}