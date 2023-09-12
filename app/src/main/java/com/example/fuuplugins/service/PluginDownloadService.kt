package com.example.fuuplugins.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.R
import com.example.fuuplugins.activity.mainActivity.repositories.PluginRepository.downloadPlugin
import com.example.fuuplugins.util.catchWithMassage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class PluginDownloadService : Service() {
    val scope = CoroutineScope(Job())
    private val binder = LocalBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch(Dispatchers.IO) {
            if(intent == null){
                return@launch
            }
            val id = intent.getStringExtra("id") ?: return@launch
            File(FuuApplication.pluginsPathForWeb,"plugin_${id}").let {
                if(it.exists()){
                    it.delete()
                }
            }
            File(FuuApplication.pluginsPathForApk,"plugin_${id}").let {
                if(it.exists()){
                    it.delete()
                }
            }
            val isApk = (id.toInt()%2) == 0
            val file = File(if(isApk) FuuApplication.pluginsPathForApk else FuuApplication.pluginsPathForWeb,"plugin_0")
            flow {
                emit(file.mkdirs())
            }.flatMapConcat {
                if(it){
                    downloadPlugin(id)
                }
                else{
                    throw Throwable("创建失败")
                }
            }
                .catchWithMassage {

                }
                .collect{
                    if(!it.isSuccessful){
                        return@collect
                    }
                    val data = it.body() ?: return@collect

                    val totalLength = data.contentLength().toDouble()
                    //写文件
                    file.outputStream().run {
                        val input = data.byteStream()
                        input.copyTo(this){ currentLength ->
                            //当前下载进度
                            val process = currentLength / totalLength * 100

                        }
                    }
                }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): PluginDownloadService = this@PluginDownloadService
    }

//    private fun createNotificationForProgress() {
//        val progressMax = 100
//        val progressCurrent = 30
//        val mBuilder = NotificationCompat.Builder(this@PluginDownloadService, mProgressChannelId)
//            .setContentTitle("进度通知")
//            .setContentText("下载中：$progressCurrent%")
////            .setSmallIcon(R.mipmap.ic_launcher)
////            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_avatar))
//            // 第3个参数indeterminate，false表示确定的进度，比如100，true表示不确定的进度，会一直显示进度动画，直到更新状态下载完成，或删除通知
//            .setProgress(progressMax, progressCurrent, false)
//
//        mManager.notify(mProgressNotificationId, mBuilder.build())
//    }
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


