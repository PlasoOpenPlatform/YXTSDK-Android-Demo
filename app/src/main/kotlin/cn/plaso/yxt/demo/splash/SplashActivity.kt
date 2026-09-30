package cn.plaso.yxt.demo.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.plaso.yxt.demo.auth.AuthActivity
import cn.plaso.yxt.demo.main.MainActivity

class SplashActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val destination by viewModel.destination.collectAsStateWithLifecycle()
            LaunchedEffect(destination) {
                when (destination) {
                    SplashDestination.Auth -> open(AuthActivity::class.java)
                    SplashDestination.Main -> open(MainActivity::class.java)
                    SplashDestination.Pending, SplashDestination.SigningIn -> Unit
                }
            }
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF006E5C))) {
                SplashScreen(signingIn = destination == SplashDestination.SigningIn)
            }
        }
        viewModel.start()
    }

    private fun open(target: Class<*>) {
        startActivity(Intent(this, target))
        finish()
    }
}

@Composable
private fun SplashScreen(signingIn: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "YXT SDK Demo",
                color = Color(0xFF006E5C),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            Text("伯索云课堂 SDK 接入示例", color = Color.Gray, fontSize = 14.sp)
            if (signingIn) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }
}
