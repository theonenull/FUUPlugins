package com.example.fuuplugins.util

import android.util.Log
import com.example.fuuplugins.config.BuildConfig

private val isDebug = BuildConfig.DEBUG //调试模式
private const val TAG = "fzuhelper"
fun Any.verbose(msg: String) {
    if (isDebug) {
        Log.v(javaClass.simpleName, msg)
    }
}

fun Any.verbose(msg: Int) {
    if (isDebug) {
        Log.v(javaClass.simpleName, msg.toString())
    }
}

fun Any.debug(msg: String) {
    if (isDebug) {
        Log.d(javaClass.simpleName, msg)
    }
}

fun Any.debug(msg: Int) {
    if (isDebug) {
        Log.d(javaClass.simpleName, msg.toString())
    }
}

fun Any.info(msg: String) {
    if (isDebug) {
        Log.i(javaClass.simpleName, msg)
    }
}

fun Any.info(msg: Int) {
    if (isDebug) {
        Log.i(javaClass.simpleName, msg.toString())
    }
}

fun Any.warn(msg: String) {
    if (isDebug) {
        Log.w(javaClass.simpleName, msg)
    }
}

fun Any.warn(msg: Int) {
    if (isDebug) {
        Log.w(javaClass.simpleName, msg.toString())
    }
}

fun Any.error(msg: String) {
    if (isDebug) {
        Log.e(javaClass.simpleName, msg)
    }
}

fun Any.error(msg: Int) {
    if (isDebug) {
        Log.e(javaClass.simpleName, msg.toString())
    }
}

fun verbose(tag: String = TAG, msg: String) {
    if (isDebug) {
        Log.v(tag, msg)
    }
}

fun verbose(tag: String = TAG, msg: Int) {
    if (isDebug) {
        Log.v(tag, msg.toString())
    }
}

fun debug(tag: String = TAG, msg: String) {
    if (isDebug) {
        Log.d(tag, msg)
    }
}

fun debug(tag: String = TAG, msg: Int) {
    if (isDebug) {
        Log.d(tag, msg.toString())
    }
}

fun info(tag: String = TAG, msg: String) {
    if (isDebug) {
        Log.i(tag, msg)
    }
}

fun info(tag: String = TAG, msg: Int) {
    if (isDebug) {
        Log.i(tag, msg.toString())
    }
}

fun warn(tag: String = TAG, msg: String) {
    if (isDebug) {
        Log.w(tag, msg)
    }
}

fun warn(tag: String = TAG, msg: Int) {
    if (isDebug) {
        Log.w(tag, msg.toString())
    }
}

fun error(tag: String = TAG, msg: String) {
    if (isDebug) {
        Log.e(tag, msg)
    }
}

fun error(tag: String = TAG, msg: Int) {
    if (isDebug) {
        Log.e(tag, msg.toString())
    }
}

fun error(msg: String, e: Throwable) {
    if (isDebug) {
        Log.e(TAG, msg, e)
    }
}