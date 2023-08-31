package com.example.fuuplugins.activity.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.fuuplugins.activity.BaseActivity
import com.example.fuuplugins.activity.mainActivity.ui.MainActivityUi
import com.example.fuuplugins.activity.mainActivity.ui.MainFramework
import com.example.fuuplugins.ui.theme.FUUPluginsTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FUUPluginsTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                LaunchedEffect(systemUiController, useDarkIcons) {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons,
                        isNavigationBarContrastEnforced = false,
                    )
                }
                MainActivityUi()
            }
        }
    }
}

// 全屏隐藏系统栏，如：你看视频或者玩游戏的时候，就可以通过此种方式，体验是一样的
private fun hideSystemUI() {

}

// 从全屏隐藏状态下，恢复系统栏的显示
private fun showSystemUI() {

}

