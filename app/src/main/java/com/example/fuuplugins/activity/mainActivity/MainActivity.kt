package com.example.fuuplugins.activity.mainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fuuplugins.activity.BaseActivity
import com.example.fuuplugins.activity.mainActivity.ui.MainFramework
import com.example.fuuplugins.ui.theme.FUUPluginsTheme

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FUUPluginsTheme {
                MainFramework()
            }
        }
    }
}
