package com.example.fuuplugins.activity.mainActivity.data

import okhttp3.Cookie

object CookieUtil {
    var id: String = ""
    var jwchCookies = mutableMapOf<String, Cookie>()
    var newJwchCookies = mutableMapOf<String, Cookie>()
    var newJwchLastUpdateTime: Long = 0
    var eHallCookies = mutableMapOf<String, Cookie>()
    var graduateCookies = mutableMapOf<String, Cookie>()
    var lastUpdateTime: Long = 0
    var jwtLastUpdateTime: Long = 0
    var graduateLastUpdateTime: Long = 0
    var emptyRoomViewState: String = ""
    var emptyRoomEventValidation: String = ""
    var yiBanCookie: String = ""
    var libCookie: String = ""
    var experimentCookie: String = ""
    var eCardCookies = mutableMapOf<String, Cookie>()

    fun clear() {
        id = ""
        jwchCookies.clear()
        newJwchCookies.clear()
        newJwchLastUpdateTime = 0
        eHallCookies.clear()
        graduateCookies.clear()
        jwtLastUpdateTime = 0
        graduateLastUpdateTime = 0
        lastUpdateTime = 0
        yiBanCookie = ""
        libCookie = ""
        experimentCookie = ""
        eCardCookies.clear()
    }
}