package com.syndicate.ptkscheduleapp.feature.groups.presentation

internal sealed interface GroupAction {
    data class OnChangeCourse(val course: Int) : GroupAction
    data object GetGroupList : GroupAction
    data class OnSelectGroup(val group: String) : GroupAction
    data object HideErrorMessage : GroupAction
}