package com.example.fuuplugins.activity.markDownActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp
import com.example.fuuplugins.FuuApplication
import com.example.fuuplugins.activity.BaseActivity

import com.example.fuuplugins.activity.mainActivity.ui.Markdown
import com.example.fuuplugins.plugin.PluginManager
import com.example.fuuplugins.ui.theme.FUUPluginsTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.io.File

class MarkdownActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val markdown = intent.getStringExtra("markdown")?:""
        setContent {
            FUUPluginsTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                var markdown by remember{
                    mutableStateOf("")
                }
                LaunchedEffect(systemUiController, useDarkIcons) {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons,
                        isNavigationBarContrastEnforced = false,
                    )
                    markdown = PluginManager.loadMarkDown(File(FuuApplication.pluginsPathForApk,"west2.md"))
                }

                // A surface container using the 'background' color from the theme
                MarkdownText(
                    modifier = Modifier
                        .systemBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    markdown = markdown
                )
            }
        }
    }
}