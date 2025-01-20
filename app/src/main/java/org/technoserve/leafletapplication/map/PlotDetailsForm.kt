package org.technoserve.leafletapplication.map

import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson

@Composable
fun PlotDetailsForm(
    webView: WebView?,
    plotViewModel: PlotViewModel
) {
    val context = LocalContext.current

    var farmerName by remember { mutableStateOf("") }
    var plotAddress by remember { mutableStateOf("") }
    var plotData by remember { mutableStateOf<PlotData?>(null) } // For data from the map

    // Function to fetch plot data from WebView
    fun fetchPlotDataFromMap() {
        webView?.evaluateJavascript(
            """
            if (typeof Android.getPlotData === 'function') {
                Android.getPlotData();
            }
            """.trimIndent()
        ) { result ->
            // Parse the result from the map
            if (result != null && result != "null") {
                val gson = Gson()
                val data = gson.fromJson(result, PlotData::class.java)
                plotData = data
                farmerName = "Auto-filled Name" // Optional: Populate with a default
                plotAddress = "Auto-filled Address" // Optional: Populate with a default
                Log.d("PlotDetailsForm", "Plot Data loaded: $data")
            } else {
                Log.e("PlotDetailsForm", "Failed to fetch plot data from map")
            }
        }
    }

    // Fetch data when the form loads
    LaunchedEffect(Unit) {
        fetchPlotDataFromMap()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Add Plot Details",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = farmerName,
            onValueChange = { farmerName = it },
            label = { Text("Farmer Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = plotAddress,
            onValueChange = { plotAddress = it },
            label = { Text("Plot Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (farmerName.isNotEmpty() && plotAddress.isNotEmpty() && plotData != null) {
                    // Combine form data with map data
                    val combinedPlotData = plotData!!.copy(
                        farmerName = farmerName,
                        plotAddress = plotAddress
                    )

                    // Save to Room database via ViewModel
                    plotViewModel.insertPlot(
                        PlotEntity(
                            name = combinedPlotData.name,
                            area = combinedPlotData.area,
                            centroidLat = combinedPlotData.centroidLat,
                            centroidLng = combinedPlotData.centroidLng,
                            points = Gson().toJson(combinedPlotData.points),
                            accuracy = combinedPlotData.accuracy,
                            farmerName = combinedPlotData.farmerName,
                            plotAddress = combinedPlotData.plotAddress
                        )
                    )

                    Toast.makeText(context, "Data saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please complete all fields and map data.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
        }
    }
}
