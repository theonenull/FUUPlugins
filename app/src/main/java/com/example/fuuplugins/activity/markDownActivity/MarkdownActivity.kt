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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp
import com.example.fuuplugins.activity.BaseActivity

import com.example.fuuplugins.activity.mainActivity.ui.Markdown
import com.example.fuuplugins.ui.theme.FUUPluginsTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.jeziellago.compose.markdowntext.MarkdownText

class MarkdownActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val markdown = intent.getStringExtra("markdown")?:""
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