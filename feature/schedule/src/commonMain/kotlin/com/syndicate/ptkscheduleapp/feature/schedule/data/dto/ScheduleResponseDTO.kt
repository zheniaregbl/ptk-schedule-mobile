package com.syndicate.ptkscheduleapp.feature.schedule.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ScheduleResponseDTO(
    @SerialName("status") val statusResponse: String,
    @SerialName("message") val message: String,
    @SerialName("data") val listPair: List<PairDTO>? = null
)
