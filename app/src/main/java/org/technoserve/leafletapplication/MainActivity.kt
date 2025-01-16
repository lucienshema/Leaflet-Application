package org.technoserve.leafletapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.technoserve.leafletapplication.map.PlotData
import org.technoserve.leafletapplication.map.PlotDatabase
import org.technoserve.leafletapplication.map.PlotEntity
import org.technoserve.leafletapplication.map.PlotViewModel
import java.io.File
import java.net.URLConnection

var loadURL = "file:///android_asset/leaflet_map.html"


// JavaScript Interface
class JavaScriptInterface(
    private val context: Context,
    private val farmerName: () -> String,
    private val plotAddress: () -> String
) {


    @JavascriptInterface
    fun receivePlotData(plotDataJson: String) {
        val gson = Gson()
        val plotData = gson.fromJson(plotDataJson, PlotData::class.java)

        Log.d("JavaScriptInterface", "Received Plot Data: $plotData")

        // Retrieve the form data
        val farmer = farmerName.invoke()
        val address = plotAddress.invoke()

        Log.d("JavaScriptInterface", "Form Data: Farmer Name - $farmer, Plot Address - $address")

        (context as? MainActivity)?.lifecycleScope?.launch {
            val plotDao = PlotDatabase.getDatabase(context).plotDao()

            // Combine form data with plot data
            val plotEntity = PlotEntity(
                name = plotData.name,
                area = plotData.area,
                centroidLat = plotData.centroidLat,
                centroidLng = plotData.centroidLng,
                points = gson.toJson(plotData.points), // Convert points to JSON string
                accuracy = plotData.accuracy,
                farmerName = farmer,
                plotAddress = address
            )

            // Save to the database
            plotDao.insertPlot(plotEntity)
            Log.d("JavaScriptInterface", "Plot Data with Form Details saved to database.")
        }
    }

    @JavascriptInterface
    fun saveDataToRoom(plotDataJson: String) {
        val plotData = Gson().fromJson(plotDataJson, PlotData::class.java)
        Log.d("JavaScriptInterface", "Saving Plot Data: $plotData")

//        (context as? MainActivity)?.lifecycleScope?.launch {
//            val plotDao = PlotDatabase.getDatabase(context).plotDao()
//            val plotEntity = PlotEntity(
//                name = plotData.name,
//                area = plotData.area,
//                centroidLat = plotData.centroidLat,
//                centroidLng = plotData.centroidLng,
//                points = Gson().toJson(plotData.points), // Convert points back to JSON string
//                accuracy = plotData.accuracy,
//                farmerName = plotData.farmerName,
//                plotAddress = plotData.plotAddress
//            )
//            plotDao.insertPlot(plotEntity)
//            Log.d("JavaScriptInterface", "Plot saved to database")
//        }
    }

    @JavascriptInterface
    fun getPlots(): String {
        return Gson().toJson(PlotDatabase.getDatabase(context).plotDao().getAllPlots())
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var plotViewModel: PlotViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable WebView debugging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        plotViewModel = ViewModelProvider(this)[PlotViewModel::class.java]




        setContent {
            var showVisualization by remember { mutableStateOf(false) }

            // WebViewPage(loadURL) //OFFLINE
            Column {
                // Toggle Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showVisualization = false }) {
                        Text(text = "Load WebViewPage")
                    }
                    Button(onClick = { showVisualization = true }) {
                        Text(text = "Load Visualization")
                    }
                }

                // Load the appropriate view based on the toggle
                if (showVisualization) {
                    // PlotVisualizationApp(plotViewModel)
                } else {
                    // WebViewPage(loadURL)
                    WebViewPageWithForm(loadURL)
                }
            }

        }
    }


}

/**
 * Implementation of caching mechanism for performance
 *
 */

//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//fun WebViewPage(url: String) {
//    val context = LocalContext.current
//    var backEnabled by remember { mutableStateOf(false) }
//    var webView: WebView? = null
//
//    // Define cache directory
//    val cachePath = File(context.cacheDir, "webview-cache")
//    if (!cachePath.exists()) {
//        cachePath.mkdirs()
//    }
//
//    AndroidView(
//        factory = {
//            WebView(it).apply {
//                layoutParams = ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//
//                // Enable JavaScript and required settings
//                settings.apply {
//                    javaScriptEnabled = true
//                    domStorageEnabled = true
//                    setGeolocationEnabled(true)
//                    builtInZoomControls = true
//                    displayZoomControls = false
//                    allowFileAccess = true
//                    allowContentAccess = true
//                    loadWithOverviewMode = true
//                    useWideViewPort = true
//
//                    // Cache settings
//                    cacheMode =
//                        WebSettings.LOAD_CACHE_ELSE_NETWORK // Use cached resources when available
//                    databaseEnabled = true
//                }
//
//                webChromeClient = object : WebChromeClient() {
//                    override fun onGeolocationPermissionsShowPrompt(
//                        origin: String,
//                        callback: GeolocationPermissions.Callback
//                    ) {
//                        callback.invoke(origin, true, false)
//                    }
//                }
//
//                // Attach JavaScript interface
//                addJavascriptInterface(JavaScriptInterface(context), "Android")
//
//                webViewClient = object : WebViewClient() {
//                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
//                        super.onPageStarted(view, url, favicon)
//                    }
//
//                    override fun shouldInterceptRequest(
//                        view: WebView?,
//                        request: WebResourceRequest?
//                    ): WebResourceResponse? {
//                        val url = request?.url.toString()
//                        val cachedResponse = getCachedResponse(context, url)
//                        if (cachedResponse != null) {
//                            return cachedResponse
//                        }
//                        return super.shouldInterceptRequest(view, request)
//                    }
//
//                    override fun onPageFinished(view: WebView?, url: String?) {
//                        super.onPageFinished(view, url)
//                        view?.evaluateJavascript("if(map){map.invalidateSize(true);}", null)
//                    }
//                }
//
//                // Load URL with fallback to cached content
//                if (isNetworkAvailable(context)) {
//                    loadUrl(url)
//                } else {
//                    val cachedFile = File(cachePath, "${Uri.parse(url).host}.mht")
//                    if (cachedFile.exists()) {
//                        loadUrl("file://${cachedFile.absolutePath}")
//                    } else {
//                        loadUrl(url)
//                    }
//                }
//                webView = this
//            }
//        },
//        modifier = Modifier
//            .fillMaxSize()
//            .semantics { contentDescription = "Web View" },
//        update = { webView ->
//            webView.evaluateJavascript("if(map){map.invalidateSize(true);}", null)
//        }
//    )
//
//    BackHandler(enabled = backEnabled) {
//        webView?.goBack()
//    }
//}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPageWithForm(url: String) {
    val context = LocalContext.current
    var farmerName by remember { mutableStateOf("") }
    var plotAddress by remember { mutableStateOf("") }
    var webView: WebView? = null
    var backEnabled by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // WebView for the map
        AndroidView(
            factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Enable JavaScript and required settings
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setGeolocationEnabled(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        allowFileAccess = true
                        allowContentAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true

                        cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // Use cached resources
                        databaseEnabled = true
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onGeolocationPermissionsShowPrompt(
                            origin: String,
                            callback: GeolocationPermissions.Callback
                        ) {
                            callback.invoke(origin, true, false)
                        }
                    }

                    // Attach JavaScript interface with form data lambdas
                    addJavascriptInterface(
                        JavaScriptInterface(
                            context = context,
                            farmerName = { farmerName },
                            plotAddress = { plotAddress }
                        ),
                        "Android"
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            view?.evaluateJavascript("if(map){map.invalidateSize(true);}", null)
                        }
                    }

                    // Load the provided URL
                    loadUrl(url)
                    webView = this
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "Web View" },
            update = { webView ->
                webView.evaluateJavascript("if(map){map.invalidateSize(true);}", null)
            }
        )

        BackHandler(enabled = backEnabled) {
            webView?.goBack()
        }

        // Overlay form for adding plot details
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

            fun String.escapeJavaScript(): String {
                return this.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
            }

            Button(
                onClick = {
                    if (farmerName.isNotEmpty() && plotAddress.isNotEmpty()) {
                        Log.d("Farmer Info", "Farmer Name: $farmerName, Plot Address: $plotAddress")

                        webView?.evaluateJavascript(
                            """
                    Android.setFarmerDetails("${farmerName.escapeJavaScript()}", "${plotAddress.escapeJavaScript()}");
                    notifyAndroid("Details sent: Farmer Name - ${farmerName.escapeJavaScript()}, Plot Address - ${plotAddress.escapeJavaScript()}");
                    """.trimIndent()
                        ) { result ->
                            Log.d("WebView", "JavaScript execution result: $result")
                        }
                        Log.d("EvaluateJavascript", "Command sent to WebView: Android.setFarmerDetails('$farmerName', '$plotAddress')")
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }

                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Submit")
            }
        }

    }
}


// Utility function to check network availability
private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
}

// Function to get cached response
private fun getCachedResponse(context: Context, url: String): WebResourceResponse? {
    val cache = context.cacheDir
    val cachedFile = File(cache, Uri.parse(url).lastPathSegment ?: return null)

    if (cachedFile.exists()) {
        try {
            val mimeType = URLConnection.guessContentTypeFromName(url)
            return WebResourceResponse(
                mimeType ?: "text/plain",
                "UTF-8",
                cachedFile.inputStream()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return null
}

//@Composable
//fun WebViewWithVisualization(dataJson: String) {
//    val context = LocalContext.current
//    var backEnabled by remember { mutableStateOf(false) }
//    var webView: WebView? = null
//
//    println("Data JSON: $dataJson")
//
//    AndroidView(
//        factory = { ctx ->
//            WebView(ctx).apply {
//                layoutParams = ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//
//                // Enable JavaScript and other settings
//                settings.apply {
//                    javaScriptEnabled = true
//                    domStorageEnabled = true
//                    setGeolocationEnabled(true)
//                    builtInZoomControls = true
//                    displayZoomControls = false
//                    allowFileAccess = true
//                    allowContentAccess = true
//                    loadWithOverviewMode = true
//                    useWideViewPort = true
//                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//                    databaseEnabled = true
//                }
//
//                webChromeClient = object : WebChromeClient() {
//                    override fun onGeolocationPermissionsShowPrompt(
//                        origin: String,
//                        callback: GeolocationPermissions.Callback
//                    ) {
//                        callback.invoke(origin, true, false)
//                    }
//                }
//
//                addJavascriptInterface(JavaScriptInterface(context), "Android")
//
//                webViewClient = object : WebViewClient() {
//                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
//                        super.onPageStarted(view, url, favicon)
//                        backEnabled = view.canGoBack()
//                    }
//
//                    override fun shouldInterceptRequest(
//                        view: WebView?,
//                        request: WebResourceRequest?
//                    ): WebResourceResponse? {
//                        val url = request?.url.toString()
//                        val cachedResponse = getCachedResponse(context, url)
//                        return cachedResponse ?: super.shouldInterceptRequest(view, request)
//                    }
//
//                    override fun onPageFinished(view: WebView?, url: String?) {
//                        super.onPageFinished(view, url)
//                        view?.evaluateJavascript(
//                            """
//        if (typeof visualizeData === 'function') {
//            visualizeData(${dataJson});
//        } else {
//            console.error('visualizeData is not defined');
//        }
//        """.trimIndent(),
//                            null
//                        )
//                    }
//
//                }
//
//                // Load the URL
//                loadUrl("file:///android_asset/index.html")
//                webView = this
//            }
//        },
//        modifier = Modifier.fillMaxSize(),
//        update = { webView ->
//            // Optionally reinject data
//            webView.evaluateJavascript(
//                "if (typeof visualizeData === 'function') { visualizeData(${dataJson}); } else { console.error('visualizeData is not defined'); }",
//                null
//            )
//        }
//    )
//
//    // Handle back navigation
//    BackHandler(enabled = backEnabled) {
//        webView?.goBack()
//    }
//}


//@Composable
//fun PlotVisualizationApp(plotViewModel: PlotViewModel) {
//    val plots by plotViewModel.allPlots.observeAsState(emptyList())
//
//    println("Plots: $plots")
//
//    if (plots.isNotEmpty()) {
//        val plotsJson = Gson().toJson(plots.map {
//            mapOf(
//                "area" to it.area,
//                "centroidLat" to it.centroidLat,
//                "centroidLng" to it.centroidLng,
//                "points" to Gson().fromJson(it.points, List::class.java),
//                "accuracy" to it.accuracy,
////                "farmerName" to it.farmerName,
////                "plotAddress" to it.plotAddress
//            )
//        })
//        println("Plots JSON: $plotsJson")
//
//        WebViewWithVisualization(dataJson = plotsJson)
//    } else {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = "Loading plots...", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}


