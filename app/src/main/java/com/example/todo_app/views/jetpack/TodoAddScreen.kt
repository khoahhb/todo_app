package com.example.todo_app.views.jetpack

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.todo_app.R
import com.example.todo_app.models.Todo
import com.example.todo_app.view_models.TodoViewModel
import com.example.todo_app.views.jetpack.ui.theme.color_success
import com.google.accompanist.pager.ExperimentalPagerApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun TodoAddScreen(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController
) {
    var snackbarColor: Color = color_success
    var statusText: String by remember { mutableStateOf("Pending") }
    var tileText: String by remember { mutableStateOf("") }
    var descText: String by remember { mutableStateOf("") }
    var createdDateText: String by remember { mutableStateOf("") }
    var completedDateText: String by remember { mutableStateOf("") }
    val mContext = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier, scaffoldState = scaffoldState) {
        it
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add todo screen",
                color = colorResource(R.color.md_theme_light_primary),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            CustomDropdown("Status", statusText, { text ->
                statusText = text
            }, statusText)

            CustomTextField("Tile", tileText) { text ->
                tileText = text
            }

            CustomTextField("Description", descText) { text ->
                descText = text
            }

            CustomDatePicker("Created date", createdDateText) { date ->
                createdDateText = date.toString()
            }

            CustomDatePicker("Completed date", completedDateText) { date ->
                completedDateText = date.toString()
            }

            Button(modifier = Modifier
                .width(200.dp)
                .height(62.dp)
                .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = {

                    var todo: Todo =
                        Todo(tileText, descText, statusText, createdDateText, completedDateText)

                    val compositeDisposable = CompositeDisposable()
                    compositeDisposable.add(
                        viewModel
                            .insert(todo)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Action {
                                try {
                                    coroutineScope.launch {
                                        Toast.makeText(
                                            mContext,
                                            "Data has been saved successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack(
                                            route = "todos_page",
                                            inclusive = false,
                                            saveState = true
                                        )
                                    }
                                } catch (e: Exception) {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            message = "Data added fail!",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }

                                compositeDisposable.dispose()
                            })
                    )


                })
            {
                Text(
                    text = "Add todo",
                    style = MaterialTheme.typography.h6,
                    color = Color.White
                )
            }
        }
    }
}