package com.syndicate.ptkscheduleapp.feature.schedule.domain.model

import com.syndicate.ptkscheduleapp.feature.schedule.util.randomHsl

internal data class PairItem(
    val dayOfWeek: String = "Понедельник",
    val isUpper: Boolean? = false,
    val pairNumber: Int = 1,
    val subject: String = "Математика",
    val place: String = "ПТК",
    val room: String = "410",
    val teacher: String = "Ширина",
    val subgroupNumber: Int = 0,
    val time: String = "8.30-10.10",
    val previousPairNumber: Int = -1,
    val isNewPair: Boolean = false,
    val swapPair: Boolean = false,
    val isReplacement: Boolean = false,
    val color: HslColor = randomHsl()
)