package com.syndicate.ptkscheduleapp.feature.groups.presentation.components

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syndicate.ptkscheduleapp.ui_kit.foundations.element.choice_control.AnimatedRadioButton

@Composable
internal fun CourseSection(
    modifier: Modifier = Modifier,
    courseList: List<String> = listOf("1 курс", "2 курс", "3 курс", "4 курс"),
    courseProvider: () -> Int = { 0 },
    onCourseClick: (Int) -> Unit = { }
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        courseList.forEachIndexed { index, label ->

            CourseItem(
                modifier = Modifier
                    .height(30.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onCourseClick(index) },
                label = label,
                isSelected = index == courseProvider(),
                onClick = { onCourseClick(index) }
            )
        }
    }
}

@Composable
fun CourseItem(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AnimatedRadioButton(
            selected = isSelected,
            isOutline = false,
            size = 30.dp,
            color = Color(0xFF4B71FF),
            animationSpec = tween(
                durationMillis = 200,
                easing = EaseOut
            ),
            onClick = onClick
        )

        Text(
            text = label,
            style = LocalTextStyle.current,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            color = Color.Black
        )
    }
}