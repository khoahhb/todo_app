package com.example.todo_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todo_app.views.java.activities.FrameActivity
import com.example.todo_app.views.jetpack.NavActivity

class SwitchFrameworkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwitchScreen()
        }
    }
}

@Composable
fun SwitchScreen() {
    val mContext = LocalContext.current

    Scaffold() {
        it
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Pick a Framework",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.scrim,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 40.dp)
            )
            Button(
                modifier = Modifier
                    .width(200.dp)
                    .height(76.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                onClick =
                {
                    mContext.startActivity(Intent(mContext, FrameActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
            )
            {
                Text(
                    text = "Java - Material",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Button(
                modifier = Modifier
                    .width(200.dp)
                    .height(76.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = {
                    mContext.startActivity(Intent(mContext, NavActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
            )
            {
                Text(
                    text = "Jetpack - Material",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

}