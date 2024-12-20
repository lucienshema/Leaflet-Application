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
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URLConnection

var loadURL = "file:///android_asset/leaflet_map.html"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable WebView debugging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        setContent {
            WebViewPage(loadURL) //OFFLINE
        }
    }
}



//@SuppressLint("SetJavaScriptEnabled")
//@Composable
//fun WebViewPage(url: String) {
//    val context = LocalContext.current
//    //.................................................
//    // Compose WebView Part 9 | Removes or Stop Ad in web
//    val adServers = StringBuilder()
//    var line: String? = ""
//    val inputStream = context.resources.openRawResource(R.raw.adblockserverlist)
//    val br = BufferedReader(InputStreamReader(inputStream))
//    try {
//        while (br.readLine().also { line = it } != null) {
//            adServers.append(line)
//            adServers.append("\n")
//        }
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//
//    var backEnabled by remember { mutableStateOf(false) }
//    var webView: WebView? = null
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
//                    // Add these new settings
//                    cacheMode = WebSettings.LOAD_NO_CACHE
//                    setRenderPriority(WebSettings.RenderPriority.HIGH)
//                    setEnableSmoothTransition(true)
//
//                    // Enable hardware acceleration
//                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
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
//                webViewClient = object : WebViewClient() {
//                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
//                        super.onPageStarted(view, url, favicon)
//                    }
//
//                    override fun onPageFinished(view: WebView?, url: String?) {
//                        super.onPageFinished(view, url)
//                        // Force map refresh after page load
//                        view?.evaluateJavascript("if(map){map.invalidateSize(true);}", null)
//                    }
//                }
//
//                loadUrl(url)
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
//    BackHandler(enabled = backEnabled) {
//        removeElement(webView!!)
//        webView?.goBack()
//    }
//
//}

//fun removeElement(webView: WebView) {
//
//    // hide element by id
//    webView.loadUrl("javascript:(function() { document.getElementById('blog-pager').style.display='none';})()");
//
//    // we can also hide class name
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[0].style.display='none';})()")
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[1].style.display='none';})()")
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[2].style.display='none';})()")
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[3].style.display='none';})()")
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[4].style.display='none';})()")
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[5].style.display='none';})()")
//    webView.loadUrl("javascript:(function() { document.getElementsByClassName('btn')[6].style.display='none';})()")
//}


/**
 * Implementation of caching mechanism for performance
 *
 */

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPage(url: String) {
    val context = LocalContext.current
    var backEnabled by remember { mutableStateOf(false) }
    var webView: WebView? = null

    // Define cache directory
    val cachePath = File(context.cacheDir, "webview-cache")
    if (!cachePath.exists()) {
        cachePath.mkdirs()
    }

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

                    // Cache settings
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK // Use cached resources when available
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

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val url = request?.url.toString()
                        val cachedResponse = getCachedResponse(context, url)
                        if (cachedResponse != null) {
                            return cachedResponse
                        }
                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.evaluateJavascript("if(map){map.invalidateSize(true);}", null)
                    }
                }

                // Load URL with fallback to cached content
                if (isNetworkAvailable(context)) {
                    loadUrl(url)
                } else {
                    val cachedFile = File(cachePath, "${Uri.parse(url).host}.mht")
                    if (cachedFile.exists()) {
                        loadUrl("file://${cachedFile.absolutePath}")
                    } else {
                        loadUrl(url)
                    }
                }
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
}

// Utility function to check network availability
private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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



