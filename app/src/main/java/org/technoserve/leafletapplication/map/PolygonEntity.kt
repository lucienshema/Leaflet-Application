package org.technoserve.leafletapplication.map

data class PlotData(
    val name: String,
    val area: Double,
    val centroidLat: Double,
    val centroidLng: Double,
    val points: List<Point>, // Assuming points is a list of custom objects
    val accuracy: Float,
    val farmerName: String,
    val plotAddress: String
)

data class Point(
    val lat: Double,
    val lng: Double
)

