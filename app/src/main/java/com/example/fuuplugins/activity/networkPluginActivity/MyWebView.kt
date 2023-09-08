package com.example.fuuplugins.activity.networkPluginActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AttributeSet
import android.util.Base64
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Create by NOSAE on 2020/11/24
 */
@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("SetJavaScriptEnabled")
class MyWebView(ctx: Context, attrs: AttributeSet?) : WebView(ctx, attrs) {

    var progressBar: ProgressBar? = null

    init {
        settings.apply {
            savePassword = false // Webview明文存储密码风险
            allowFileAccess = false // Webview File同源策略绕过漏洞
            javaScriptEnabled = true
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            useWideViewPort = true
            domStorageEnabled = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            loadWithOverviewMode = true
            allowFileAccess = false
            javaScriptEnabled = true
        }

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }
        }

        webChromeClient = MyWebChromeClient()

//        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
//            GlobalScope.launch(Dispatchers.Main, tryBlock = {
//                //目前只能存图片 todo
//                val byte = Base64.decode(url.split(",")[1], Base64.DEFAULT)
//                val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
//                FileUtil.savePhotoAlbum(bitmap, "${System.currentTimeMillis()}", mimetype)
//                toastInfo("保存成功")
//            }, catchBlock = { e ->
//                e.printStackTrace()
//                if (e.message == "没有储存权限") toastError("保存失败，请先授予储存权限") else toastError("保存失败")
//            })
//        }
    }

    internal inner class MyWebChromeClient : WebChromeClient() {
//        override fun onReceivedTitle(view: WebView, title: String) {
//            super.onReceivedTitle(view, title)
//            (context as AppCompatActivity).supportActionBar?.title = title
//        }

//        override fun onProgressChanged(view: WebView?, newProgress: Int) {
//            super.onProgressChanged(view, newProgress)
//            if (newProgress == 100) {
//                progressBar?.gone()
//            } else {
//                progressBar?.visible()
//                progressBar?.progress = newProgress
//            }
//        }

//        override fun onJsAlert(
//            view: WebView,
//            url: String,
//            message: String,
//            result: JsResult
//        ): Boolean {
//            MyDialogBuilder(context as Activity)
//                .setMode(DialogMode.CENTER)
//                .setTitle("提示")
//                .setContentText(message)
//                .onCancel {
//                    result.cancel()
//                    it.dismiss()
//                }.onConfirm {
//                    result.confirm()
//                    it.dismiss()
//                }.onOutsideClick {
//                    result.confirm()
//                }.create()
//                .show()
//            return true
//        }
//
//        private val filePickerLauncher =
//            (context as ComponentActivity).registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { list ->
//                filePickerCallback?.onReceiveValue(list?.toTypedArray())
//            }
//
//        private var filePickerCallback: ValueCallback<Array<Uri>>? = null
//
//        override fun onShowFileChooser(
//            webView: WebView?,
//            filePathCallback: ValueCallback<Array<Uri>>?,
//            fileChooserParams: FileChooserParams?
//        ): Boolean {
//            filePickerCallback = filePathCallback
//            fileChooserParams?.let {
//                val types = it.acceptTypes.filter { str ->
//                    str.isNotEmpty()
//                }.toTypedArray()
//                filePickerLauncher.launch(if (types.isEmpty()) arrayOf("*/*") else types)
//            }
//            return true
//        }
    }
}