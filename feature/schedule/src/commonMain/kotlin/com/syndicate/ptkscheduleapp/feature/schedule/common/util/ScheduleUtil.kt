package com.syndicate.ptkscheduleapp.feature.schedule.common.util

import com.syndicate.ptkscheduleapp.feature.schedule.data.dto.PairDTO
import com.syndicate.ptkscheduleapp.feature.schedule.data.mapper.toModel
import com.syndicate.ptkscheduleapp.feature.schedule.domain.model.PairItem
import com.syndicate.ptkscheduleapp.feature.schedule.domain.model.ReplacementItem
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal object ScheduleUtil {

    fun groupDailyScheduleBySubgroup(
        dailySchedule: List<PairItem>
    ): List<List<PairItem>> {

        var listSeveralPair = ArrayList<PairItem>()
        val schedule = ArrayList<List<PairItem>>()

        dailySchedule.forEachIndexed { index, pairItem ->

            if (pairItem.subgroupNumber == 0)
                schedule.add(listOf(pairItem))
            else {

                listSeveralPair.add(pairItem)

                if (index != dailySchedule.lastIndex && dailySchedule[index + 1].subgroupNumber == 0
                    || index == dailySchedule.lastIndex && dailySchedule.isNotEmpty()
                    || index != dailySchedule.lastIndex && dailySchedule[index + 1].pairNumber != pairItem.pairNumber
                ) {
                    schedule.add(listSeveralPair)
                    listSeveralPair = ArrayList()
                }
            }
        }

        return schedule
    }

    fun getWeekScheduleByWeekType(
        inputSchedule: List<List<PairItem>>,
        isUpperWeek: Boolean
    ): List<List<PairItem>> {

        val schedule = ArrayList<List<PairItem>>()

        inputSchedule.forEach { daySchedule ->
            schedule.add(filterScheduleByWeekType(daySchedule, isUpperWeek))
        }

        return schedule
    }

    private fun getDayOfWeek(number: Int): String {
        return when (number) {
            0 -> "понедельник"
            1 -> "вторник"
            2 -> "среда"
            3 -> "четверг"
            4 -> "пятница"
            5 -> "суббота"
            else -> "воскресенье"
        }
    }

    fun getWeekSchedule(listPair: List<PairItem>): List<List<PairItem>> {

        if (listPair.isEmpty())
            return emptyList()

        val schedule = mutableListOf<List<PairItem>>()

        (0..6).forEach { dayNumber ->
            val dailyPair = listPair.filter { pairItem ->
                pairItem.dayOfWeek.lowercase() == getDayOfWeek(dayNumber)
            }
            schedule.add(dailyPair)
        }

        return schedule
    }

    private fun filterScheduleByWeekType(
        weekSchedule: List<PairItem>,
        isUpperWeek: Boolean
    ): List<PairItem> {

        if (weekSchedule.isEmpty())
            return emptyList()

        val schedule = ArrayList<PairItem>()

        weekSchedule.forEach {
            if (it.isUpper == null || it.isUpper == isUpperWeek)
                schedule.add(it)
        }

        schedule.sortBy { it.pairNumber }

        return schedule
    }

    fun getWeeksFromStartDate(
        startDate: LocalDate,
        weeksCount: Int
    ): List<List<LocalDate>> {
        val weeks = mutableListOf<List<LocalDate>>()
        var currentStartOfWeek = startDate

        while (currentStartOfWeek.dayOfWeek != DayOfWeek.MONDAY) {
            currentStartOfWeek = currentStartOfWeek.minus(1, DateTimeUnit.DAY)
        }

        repeat(weeksCount) {
            val week = (0 until 7)
                .map { currentStartOfWeek.plus(it, DateTimeUnit.DAY) }
            weeks.add(week)
            currentStartOfWeek = currentStartOfWeek.plus(1, DateTimeUnit.WEEK)
        }

        return weeks
    }

    fun getCurrentWeek(weeks: List<List<LocalDate>>, currentDate: LocalDate): Int {
        for (i in weeks.indices) {
            for (j in weeks[i].indices) {
                if (weeks[i][j] == currentDate) {
                    return i
                }
            }
        }

        return 0
    }

    fun getCurrentTypeWeek(
        typeWeekNow: Boolean,
        prevPage: Int,
        currentPage: Int
    ) = if (prevPage % 2 == currentPage % 2) typeWeekNow else !typeWeekNow

    fun getReplacementFromJson(
        jsonReplacement: JsonObject,
        group: String
    ): List<ReplacementItem> {

        if (jsonReplacement.toString() == "")
            return emptyList()

        val listReplacement = ArrayList<ReplacementItem>()
        val listDate = jsonObjectToMap(jsonReplacement).keys.toList()
        val formatter = LocalDate.Format {
            dayOfMonth()
            char('.')
            monthNumber()
            char('.')
            year()
        }

        val parser = Json { ignoreUnknownKeys = true }

        listDate.forEach { date ->

            var dailyReplacementList = ArrayList<List<PairItem>>()
            var pairReplacement = ArrayList<PairItem>()
            var lastPairNumber = 0

            try {

                val dailyReplacementJson = jsonReplacement[date]!!.jsonObject[group]!!.jsonArray

                for (jsonElement in dailyReplacementJson) {

                    val pairItem = parser
                        .decodeFromJsonElement<PairDTO>(jsonElement)
                        .toModel()

                    if (pairItem.pairNumber == -1) {
                        dailyReplacementList.add(listOf(pairItem))
                        continue
                    }

                    if (lastPairNumber == 0) {
                        lastPairNumber = pairItem.pairNumber
                    }

                    if (lastPairNumber != pairItem.pairNumber) {
                        dailyReplacementList.add(pairReplacement)
                        lastPairNumber = pairItem.pairNumber
                        pairReplacement = ArrayList()
                    }

                    lastPairNumber = pairItem.pairNumber
                    pairReplacement.add(pairItem)
                }

                if (pairReplacement.isNotEmpty()) {
                    dailyReplacementList.add(pairReplacement)
                }

            } catch (e: Exception) {
                println(e)
            }

            if (dailyReplacementList.isNotEmpty()) {

                listReplacement.add(
                    ReplacementItem(
                        date = formatter.parse(date),
                        listReplacement = dailyReplacementList
                    )
                )

                dailyReplacementList = ArrayList()
            }
        }

        return listReplacement
    }

    private fun jsonObjectToMap(jsonObject: JsonObject): Map<String, JsonObject> {
        return jsonObject.mapValues { it.value.jsonObject }
    }
}