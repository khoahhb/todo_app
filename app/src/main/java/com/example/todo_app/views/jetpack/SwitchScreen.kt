@file:Suppress("UNUSED_EXPRESSION")

package com.example.todo_app.views.jetpack

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.todo_app.views.java.activities.FrameActivity


@Composable
fun SwitchScreen(navController: NavHostController) {

    val mContext = LocalContext.current

    Scaffold {
        it
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {

            Text(
                    text = "Pick a Project",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.scrim,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 40.dp)
            )
            Button(
                    modifier = Modifier
                            .width(250.dp)
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
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Button(
                    modifier = Modifier
                            .width(250.dp)
                            .height(76.dp)
                            .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        navController.navigate("todos_page")
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
            )
            {
                Text(
                        text = "Jetpack - Material",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Button(
                    modifier = Modifier
                            .width(250.dp)
                            .height(76.dp)
                            .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        navController.navigate("todos_page_bs")
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
            )
            {
                Text(
                        text = "Jetpack - bottom sheet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}