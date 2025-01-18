package com.syndicate.ptkscheduleapp.feature.groups.data.repository

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendMapSuccess
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendThen
import com.syndicate.ptkscheduleapp.feature.groups.data.mapper.toModel
import com.syndicate.ptkscheduleapp.feature.groups.data.network.RemoteGroupDataSource
import com.syndicate.ptkscheduleapp.feature.groups.domain.model.AllGroups
import com.syndicate.ptkscheduleapp.feature.groups.domain.repository.GroupRepository

internal class DefaultGroupRepository(
    private val remoteGroupDataSource: RemoteGroupDataSource
): GroupRepository {
    override suspend fun getGroupList(): ApiResponse<AllGroups> {
        return remoteGroupDataSource.getGroupList()
            .suspendMapSuccess { toModel() }
    }
}