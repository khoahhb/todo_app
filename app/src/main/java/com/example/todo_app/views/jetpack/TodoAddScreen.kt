@file:Suppress("UNUSED_EXPRESSION")

package com.example.todo_app.views.jetpack

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.todo_app.models.Todo
import com.example.todo_app.view_models.TodoViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

@Composable
fun TodoAddScreen(
        viewModel: TodoViewModel,
        navController: NavHostController
) {

    var statusText: String by remember { mutableStateOf("Pending") }
    var tileText: String by remember { mutableStateOf("") }
    var descText: String by remember { mutableStateOf("") }
    var createdDateText: String by remember { mutableStateOf("") }
    var completedDateText: String by remember { mutableStateOf("") }
    val mContext = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
            modifier = Modifier, scaffoldState = scaffoldState,
            topBar = { CustomTopAppBar(false,
                    { navController.navigateUp() },
                    { navController.navigate("switch") },
                    { navController.navigate("todos_page") },
                    { navController.navigate("add_todo_page") }) },
    ) {
        it
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                    text = "Add todo screen",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 24.dp, top = 16.dp),
            )

            CustomDropdown("Status", { text ->
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

                        val todo =
                                Todo(tileText, descText, statusText, createdDateText, completedDateText)

                        val compositeDisposable = CompositeDisposable()
                        compositeDisposable.add(
                                viewModel
                                        .insertTodo(todo)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe {
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
                                        }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodoAddScreenBottomSheet(
        viewModel: TodoViewModel,
) {

    var statusText: String by remember { mutableStateOf("Pending") }
    var tileText: String by remember { mutableStateOf("") }
    var descText: String by remember { mutableStateOf("") }
    var createdDateText: String by remember { mutableStateOf("") }
    var completedDateText: String by remember { mutableStateOf("") }

    val mContext = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxHeight(fraction = 0.85f), scaffoldState = scaffoldState) {
        it
        Column(
                modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                    text = "Add todo screen",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 24.dp, top = 16.dp),
            )

            CustomDropdown("Status", { text ->
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

            Button(
                    modifier = Modifier
                            .width(200.dp)
                            .height(62.dp)
                            .padding(top = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {

                        val todo =
                                Todo(tileText, descText, statusText, createdDateText, completedDateText)

                        val compositeDisposable = CompositeDisposable()
                        compositeDisposable.add(
                                viewModel
                                        .insertTodo(todo)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe {
                                            try {
                                                coroutineScope.launch {
                                                    Toast.makeText(
                                                            mContext,
                                                            "Data has been saved successfully!",
                                                            Toast.LENGTH_SHORT
                                                    ).show()
                                                    statusText = ""
                                                    tileText = ""
                                                    descText = ""
                                                    createdDateText = ""
                                                    completedDateText = ""
                                                    viewModel.scope.launch { viewModel.modalAddState.hide() }
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
                                        }
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