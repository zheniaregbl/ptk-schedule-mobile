package com.syndicate.ptkscheduleapp.feature.groups.di

import org.koin.dsl.module
import com.syndicate.ptkscheduleapp.feature.groups.domain.use_case.GetGroupListCase
import com.syndicate.ptkscheduleapp.feature.groups.presentation.GroupViewModel
import com.syndicate.ptkscheduleapp.feature.groups.data.network.KtorRemoteGroupDataSource
import com.syndicate.ptkscheduleapp.feature.groups.data.network.RemoteGroupDataSource
import com.syndicate.ptkscheduleapp.feature.groups.data.repository.DefaultGroupRepository
import com.syndicate.ptkscheduleapp.feature.groups.domain.repository.GroupRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val featureGroupsModule = module {
    singleOf(::KtorRemoteGroupDataSource).bind<RemoteGroupDataSource>()
    singleOf(::DefaultGroupRepository).bind<GroupRepository>()
    singleOf(::GetGroupListCase)
    factoryOf(::GroupViewModel)
}