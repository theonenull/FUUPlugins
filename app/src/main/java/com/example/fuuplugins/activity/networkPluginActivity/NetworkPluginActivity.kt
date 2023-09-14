package com.example.fuuplugins.activity.networkPluginActivity

import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fuuplugins.activity.BaseActivity
import com.example.fuuplugins.ui.theme.FUUPluginsTheme

class NetworkPluginActivity : BaseActivity() {
    val webViewChromeClient = object: com.tencent.smtt.sdk.WebChromeClient(){

    }

    val webViewClient = object: com.tencent.smtt.sdk.WebViewClient(){
        override fun onReceivedSslError(
            p0: com.tencent.smtt.sdk.WebView?,
            p1: com.tencent.smtt.export.external.interfaces.SslErrorHandler?,
            p2: com.tencent.smtt.export.external.interfaces.SslError?
        ) {
            super.onReceivedSslError(p0, p1, p2)
            p1?.proceed()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myUrl = intent.getStringExtra("url") ?: "www.baidu.com"
        setContent {
            FUUPluginsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                    ,
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                        ,factory = { ctx ->
                            com.tencent.smtt.sdk.WebView(ctx,null).apply {
                                settings.apply {
                                    savePassword = false // Webview 明文存储密码风险
                                    allowFileAccess = false // Webview File同源策略绕过漏洞
                                    loadWithOverviewMode = true
                                    javaScriptCanOpenWindowsAutomatically = true
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    useWideViewPort = true
                                    allowFileAccess = false
                                    javaScriptCanOpenWindowsAutomatically = true
                                    safeBrowsingEnabled = true
                                    domStorageEnabled = true;
                                    setBackgroundColor(0)
                                    //开启本地文件读取（默认为true，不设置也可以）
                                    allowFileAccess = true
                                    webChromeClient = webViewChromeClient
                                    setWebViewClient(webViewClient)
                                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                                }
                                loadUrl(myUrl)
                            }

                    })

                }
            }
        }
    }

}

