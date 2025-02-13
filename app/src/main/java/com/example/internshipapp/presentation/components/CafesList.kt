package com.example.internshipapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.internshipapp.R
import com.example.internshipapp.domain.models.AddedData

fun LazyListScope.CafesList(
    sectiontitle: String,
    dataList: List<AddedData>,
    onDestClick:(AddedData?)->Unit,
    emptyListMsg:String="No Cafes Added.\nClick the + button to add new cafe.",
) {
    item {
        Text(
            text = sectiontitle,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(12.dp)
        )
    }
    if (dataList.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(R.drawable.nodata),
                    contentDescription = "nodata"
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyListMsg,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    else{
        items(dataList){ dest->
            EachListCard(
                title = dest.title?:"No Title",
                onClick = {
                    onDestClick(dest)
                },
                desc = dest.desc?:"No Description",
                mapsLink = dest.mapsLink?:"No Maps Link",
                date = dest.date?:"No date",
            )
        }
    }
}