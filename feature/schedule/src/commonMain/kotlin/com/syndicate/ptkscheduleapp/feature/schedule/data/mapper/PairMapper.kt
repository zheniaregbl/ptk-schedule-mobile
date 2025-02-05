package com.syndicate.ptkscheduleapp.feature.schedule.data.mapper

import com.syndicate.ptkscheduleapp.feature.schedule.data.dto.PairDTO
import com.syndicate.ptkscheduleapp.feature.schedule.domain.model.PairItem

internal fun PairDTO.toModel() = PairItem(
    dayOfWeek,
    isUpper,
    pairNumber,
    subject.name,
    room.place,
    room.number,
    teacher.fullName,
    subgroupNumber,
    time
)