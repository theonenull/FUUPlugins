package com.example.fuuplugins.plugin

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import dalvik.system.DexClassLoader
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Array.newInstance
import java.lang.reflect.Field


class PluginManager private constructor() {

    companion object {

        var pluginClassLoader: DexClassLoader? = null

        fun loadPlugin(context: Context, file: File) {
            val inputStream = FileInputStream(File(file.path, "plugin.apk"))
            val filesDir = context.externalCacheDir
            val apkFile = File(filesDir?.absolutePath, "plugin.apk")
            apkFile.writeBytes(inputStream.readBytes())
            val dexFile = File(filesDir, "dex")
            if (!dexFile.exists()) dexFile.mkdirs()
            println("输出dex路径: $dexFile")
            pluginClassLoader = DexClassLoader(
                apkFile.absolutePath,
                dexFile.absolutePath,
                null,
                this.javaClass.classLoader
            )
        }

        fun loadClass(className: String): Class<*>? {
            try {
                if (pluginClassLoader == null) {
                    println("pluginClassLoader is null")
                }
                return pluginClassLoader?.loadClass(className)
            } catch (e: ClassNotFoundException) {
                println("loadClass ClassNotFoundException: $className")
            }
            return null
        }

        /**
         * 合并DexElement数组: 宿主新dexElements = 宿主原始dexElements + 插件dexElements
         * 1、创建插件的 DexClassLoader 类加载器，然后通过反射获取插件的 dexElements 值。
         * 2、获取宿主的 PathClassLoader 类加载器，然后通过反射获取宿主的 dexElements 值。
         * 3、合并宿主的 dexElements 与 插件的 dexElements，生成新的 Element[]。
         * 4、最后通过反射将新的 Element[] 赋值给宿主的 dexElements。
         */
        @SuppressLint("DiscouragedPrivateApi")
        fun mergeDexElement(context: Context): Boolean {
            try {
                val clazz = Class.forName("dalvik.system.BaseDexClassLoader")
                val pathListField: Field = clazz.getDeclaredField("pathList")
                pathListField.isAccessible = true

                val dexPathListClass = Class.forName("dalvik.system.DexPathList")
                val dexElementsField = dexPathListClass.getDeclaredField("dexElements")
                dexElementsField.isAccessible = true

                // 宿主的 类加载器
                val pathClassLoader: ClassLoader = context.classLoader
                // DexPathList类的对象
                val hostPathListObj = pathListField[pathClassLoader]
                // 宿主的 dexElements
                val hostDexElements = dexElementsField[hostPathListObj] as Array<*>

                // 插件的 类加载器
                val dexClassLoader = pluginClassLoader ?: return false
                // DexPathList类的对象
                val pluginPathListObj = pathListField[dexClassLoader]
                // 插件的 dexElements
                val pluginDexElements = dexElementsField[pluginPathListObj] as Array<*>


                val hostDexSize = hostDexElements.size
                val pluginDexSize = pluginDexElements.size
                // 宿主dexElements = 宿主dexElements + 插件dexElements
                // 创建一个新数组
                val newDexElements = hostDexElements.javaClass.componentType?.let {
                    newInstance(it, hostDexSize + pluginDexSize)
                } as Array<*>
                System.arraycopy(hostDexElements, 0, newDexElements, 0, hostDexSize)
                System.arraycopy(pluginDexElements, 0, newDexElements, hostDexSize, pluginDexSize)

                // 赋值 hostDexElements = newDexElements
                dexElementsField[hostPathListObj] = newDexElements

                return true
            } catch (e: Exception) {
                println("mergeDexElement: $e")
            }
            return false
        }


        fun loadJson(file: File): PluginConfig {
            val inputStreamReader = FileReader(
                file
            ) // 使用IO流读取json文件内容
            val br = BufferedReader(inputStreamReader)
            var line: String?
            val builder = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                builder.append(line)
            }
            br.close()
            inputStreamReader.close()
            return jsonToObject(builder.toString(), PluginConfig::class.java)
        }

        inline fun <reified T>loadJson(file: File): T {
            val inputStreamReader = FileReader(
                file
            ) // 使用IO流读取json文件内容
            val br = BufferedReader(inputStreamReader)
            var line: String?
            val builder = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                builder.append(line)
            }
            br.close()
            inputStreamReader.close()
            return jsonToObject(builder.toString(), T::class.java)
        }

        inline fun <reified T>loadJson(inputStream: InputStream): T {
            val inputStreamReader = InputStreamReader(inputStream)
            val br = BufferedReader(inputStreamReader)
            var line: String?
            val builder = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                builder.append(line + "\n")
            }
            br.close()
            inputStreamReader.close()
            return jsonToObject(builder.toString(), T::class.java)
        }

//        inline fun <reified T> jsonToObject(json: String, clazz: Class<T>): T {
//            val gson = Gson()
//            return gson.fromJson(json, clazz)
//        }

        fun <T> jsonToObject(json: String?, type: Class<T>?): T {
            val gson = Gson()
            return gson.fromJson(json, type)
        }

        fun loadMarkDown(file: File): String {
            val inputStreamReader = FileReader(
                file
            ) // 使用IO流读取json文件内容
            val br = BufferedReader(inputStreamReader)
            var line: String?
            val builder = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                builder.append(line+"\n")
            }
            br.close()
            inputStreamReader.close()
            return builder.toString().trimIndent()
        }

        fun loadMarkDown(inputStream: InputStream): String {
            val inputStreamReader = InputStreamReader(inputStream)
            val br = BufferedReader(inputStreamReader)
            var line: String?
            val builder = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                builder.append(line + "\n")
            }
            br.close()
            inputStreamReader.close()
            return builder.toString().trimIndent()
        }
    }
}

interface PluginConfig {
    val version: String?
    val minFuuVersion: String?
    val maxFuuVersion: String?
    val pluginName: String?
    val developer: String?
    val description: String?
    val id : String?
    data class WebPluginConfig(
        override val version: String? = null,
        override val minFuuVersion: String? = null,
        override val maxFuuVersion: String? = null,
        override val pluginName: String? = null,
        override val developer: String? = null,
        var url : String = "",
        override val id: String? = null,
        override val description: String? = null
    ) : PluginConfig

    data class ApkPluginConfig(
        override val id: String?,
        override val version: String? = null,
        override val minFuuVersion: String? = null,
        override val maxFuuVersion: String? = null,
        override val pluginName: String? = null,
        override val developer: String? = null,
        override val description: String? = null
    ) : PluginConfig
}
