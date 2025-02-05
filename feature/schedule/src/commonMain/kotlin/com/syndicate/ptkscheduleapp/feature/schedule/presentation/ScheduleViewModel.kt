package com.syndicate.ptkscheduleapp.feature.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.syndicate.ptkscheduleapp.core.domain.repository.SettingsRepository
import com.syndicate.ptkscheduleapp.feature.schedule.common.util.ScheduleUtil
import com.syndicate.ptkscheduleapp.feature.schedule.common.util.extension.nowDate
import com.syndicate.ptkscheduleapp.feature.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

internal class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleState())
    val state = _state
        .onStart {
            viewModelScope.launch {
                getScheduleInfo()
                getReplacement()
                getSchedule()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(10_000L),
            _state.value
        )

    private val _errorMessage = MutableSharedFlow<String?>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _initWeekType = MutableStateFlow(false)

    init { getUserGroup() }

    fun onAction(action: ScheduleAction) {

        when (action) {

            is ScheduleAction.ChangeSchedulePage ->
                _state.update { it.copy(selectedSchedulePage = action.page) }

            is ScheduleAction.ChangeSelectedDate ->
                _state.update { it.copy(selectedDate = action.date) }

            is ScheduleAction.UpdateDailyWeekState -> {

                val weekNumber = ScheduleUtil
                    .getCurrentWeek(weeks, action.currentDate)

                _state.update {
                    it.copy(
                        selectedDateWeekType = if (weekNumber % 2 == currentWeekNumber % 2)
                            _initWeekType.value else !_initWeekType.value
                    )
                }
            }

            ScheduleAction.UpdateScheduleInfo -> {
                viewModelScope.launch {
                    _errorMessage.emit(null)
                    getScheduleInfo()
                    getReplacement()
                    getSchedule()
                }
            }
        }
    }

    private fun getUserGroup() = viewModelScope.launch {
        settingsRepository.userGroup
            .collect { group ->
                _state.update { it.copy(currentGroupNumber = group) }
            }
    }

    private suspend fun getScheduleInfo() {

        _state.update { it.copy(isLoading = true) }

        scheduleRepository
            .getScheduleInfo()
            .onSuccess {
                if (_state.value.scheduleInfo.isUpperWeek == null) {
                    _initWeekType.update { data.isUpperWeek!! }
                    _state.update { it.copy(selectedDateWeekType = data.isUpperWeek!!) }
                } else {

                    val startWeekType = ScheduleUtil
                        .getCurrentTypeWeek(data.isUpperWeek!!, currentWeekNumber, 0)
                    val currentWeekType = if (_state.value.selectedSchedulePage / 7 % 2 == 0)
                        startWeekType else !startWeekType

                    _state.update { it.copy(selectedDateWeekType = currentWeekType) }
                }

                _state.update { it.copy(scheduleInfo = data) }
            }
            .suspendOnError {
                _errorMessage.emit("Error getScheduleInfo")
            }
            .suspendOnException {
                _errorMessage.emit("Exception getScheduleInfo")
            }
    }

    private suspend fun getReplacement() {

        scheduleRepository
            .getReplacement(_state.value.currentGroupNumber)
            .onSuccess {
                _state.update { it.copy(replacement = data) }
            }
            .onError {

            }
            .suspendOnException {
                _errorMessage.emit("Exception getReplacement")
            }
    }

    private suspend fun getSchedule() {

        scheduleRepository
            .getSchedule(_state.value.currentGroupNumber)
            .onSuccess {
                _state.update {
                    it.copy(
                        isLoading = false,
                        schedule = ScheduleUtil
                            .getWeekSchedule(data)
                    )
                }
            }
            .onError {
                _state.update { it.copy(isLoading = false) }
            }
            .onException {
                _state.update { it.copy(isLoading = false) }
            }
    }

    companion object {

        val weeks = ScheduleUtil.getWeeksFromStartDate(
            LocalDate(Clock.System.nowDate().year, Month.JANUARY, 1),
            78
        )
        val currentWeekNumber = ScheduleUtil.getCurrentWeek(
            weeks,
            Clock.System.nowDate()
        )
    }
}