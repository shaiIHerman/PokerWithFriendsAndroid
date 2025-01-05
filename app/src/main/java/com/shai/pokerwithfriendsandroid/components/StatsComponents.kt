package com.shai.pokerwithfriendsandroid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shai.pokerwithfriendsandroid.domain.models.Statistics


@Composable
fun StatsTable(
    cellWidth: (Int) -> Dp,
    headerCellTitle: @Composable (Int) -> Unit,
    cellText: @Composable (Int, Pair<String, Any>) -> Unit,
    data: List<Pair<String, Any>>,
    columnCount: Int
) {
    Table(
        columnCount = columnCount,
        cellWidth = cellWidth,
        data = data,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(Color.Transparent),
        headerCellContent = headerCellTitle,
        cellContent = cellText
    )
}

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(4.dp),
        minLines = 2,
        maxLines = 2,
        overflow = TextOverflow.Clip,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline
    )
}

@Composable
fun CellText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp),
        maxLines = 1,
        color = Color.White,
        overflow = TextOverflow.Ellipsis,
    )
}