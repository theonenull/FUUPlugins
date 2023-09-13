package com.example.fuuplugins.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.R
import com.example.fuuplugins.activity.mainActivity.repositories.PluginRepository.downloadPlugin
import com.example.fuuplugins.util.info
import com.tencent.smtt.sdk.stat.MttLoader.CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class PluginDownloadService : Service(){
    val scope = CoroutineScope(Job())

    override fun onBind(intent: Intent?): IBinder {
        return DownloadBinder(this)
    }

    fun startDownload(
        id:String,
        startCallBack:suspend ()->Unit = {},
        endCallBack:suspend ()->Unit = {},
        errorCallBack : suspend (Throwable)->Unit = {},
    ){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("插件 Download")
            setContentText("Download in progress")
            setSmallIcon(R.drawable.fuu)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
        }
        val notificationId = 0

        val name = "download"
        val descriptionText = "null"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        NotificationManagerCompat.from(FuuApplication.instance).apply {
            // Issue the initial notification with zero progress
            builder.setProgress(100, 0, false)
            if (ActivityCompat.checkSelfPermission(
                    FuuApplication.instance,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@apply
            }
            notificationManager.notify(notificationId, builder.build())
        }
        if (id.isNotEmpty()) {
            scope.launch(Dispatchers.IO){
                startCallBack()
                downloadPlugin(id)
                    .collect { responseBodyResponse ->
                        try {
                            if (!responseBodyResponse.isSuccessful) {
                                return@collect
                            }
                            val data = responseBodyResponse.body() ?: return@collect
                            val isApk = responseBodyResponse.headers()["type"] == "apk"
                            val file =
                                File(if (isApk) FuuApplication.pluginsPathForApk else FuuApplication.pluginsPathForWeb,"plugin_${id}.zip")
                            saveToFile(data, file = file) {
                                if(it >= 95){
                                    builder.setContentText("下载完成")
                                        .setProgress(0, 0, false)

                                    endCallBack()
                                }else{
                                    builder.setContentText("下载完成")
                                        .setProgress(100, it, false)
                                }
                                notificationManager.notify(notificationId, builder.build())
                            }
                        } catch (e: Exception) {
                            Log.e("download",e.toString())
                            errorCallBack(e)
                            notificationManager.cancel(notificationId)
                            withContext(Dispatchers.Main){
                                Toast.makeText(FuuApplication.instance,"下載失敗",Toast.LENGTH_SHORT).show()

                            }
                        }
                        this@PluginDownloadService.stopSelf()
                    }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("ssssssssss","_______________")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        Log.d("destorp","_______________")
    }

    class DownloadBinder(val service: PluginDownloadService) : Binder() {

    }

}

inline fun InputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE, progress: (Long)-> Unit): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
        progress(bytesCopied)
    }
    return bytesCopied
}




private inline fun saveToFile(responseBody: ResponseBody, file: File, progressListener: (Int) -> Unit) {
    val total = responseBody.contentLength()
    var bytesCopied = 0
    var emittedProgress = 0
    file.outputStream().use { output ->
        val input = responseBody.byteStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var bytes = input.read(buffer)
        while (bytes > 0) {
            output.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = input.read(buffer)
            val progress = (bytesCopied * 100.0 / total).toInt()
            if (progress - emittedProgress >= 0) {
                progressListener(progress)
                emittedProgress = progress
            }
        }
    }
}


