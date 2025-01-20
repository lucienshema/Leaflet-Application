package org.technoserve.leafletapplication.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DisplaySavedPlots(viewModel: PlotViewModel) {
    val plots by viewModel.allPlots.observeAsState(emptyList())

    LazyColumn {
        items(plots) { plot ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Farmer Name: ${plot.farmerName}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Plot Address: ${plot.plotAddress}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Area: ${plot.area}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Centroid: (${plot.centroidLat}, ${plot.centroidLng})", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
