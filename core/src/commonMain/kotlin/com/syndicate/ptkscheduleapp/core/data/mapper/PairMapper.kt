package com.syndicate.ptkscheduleapp.core.data.mapper

import com.syndicate.ptkscheduleapp.core.data.dto.PairDTO
import com.syndicate.ptkscheduleapp.core.data.dto.ReplacementInfoDTO
import com.syndicate.ptkscheduleapp.core.data.dto.RoomDTO
import com.syndicate.ptkscheduleapp.core.data.dto.SubjectDTO
import com.syndicate.ptkscheduleapp.core.data.dto.TeacherDTO
import com.syndicate.ptkscheduleapp.core.domain.model.PairItem

fun PairDTO.toModel() = PairItem(
    dayOfWeek = dayOfWeek,
    isUpper = isUpper,
    pairNumber = pairNumber,
    subject = subject.name,
    place = room.place,
    room = room.number,
    teacher = teacher.fullName,
    subgroupNumber = subgroupNumber,
    time = time,
    previousPairNumber = replacementInfoDTO?.previousPairNumber ?: -1,
    isNewPair = replacementInfoDTO?.isNewPair ?: false,
    swapPair = replacementInfoDTO?.swapPair ?: false,
    isReplacement = replacementInfoDTO?.isReplacement ?: false
)

fun PairItem.toDTO() = PairDTO(
    dayOfWeek = dayOfWeek,
    isUpper = isUpper,
    pairNumber = pairNumber,
    subject = SubjectDTO(subject),
    room = RoomDTO(room, place),
    teacher = TeacherDTO(teacher),
    subgroupNumber = subgroupNumber,
    time = time
)

fun PairItem.toLocalDTO() = PairDTO(
    dayOfWeek = dayOfWeek,
    isUpper = isUpper,
    pairNumber = pairNumber,
    subject = SubjectDTO(subject),
    room = RoomDTO(room, place),
    teacher = TeacherDTO(teacher),
    subgroupNumber = subgroupNumber,
    time = time,
    replacementInfoDTO = ReplacementInfoDTO(
        isReplacement = isReplacement,
        previousPairNumber = previousPairNumber,
        isNewPair = isNewPair,
        swapPair = swapPair
    )
)