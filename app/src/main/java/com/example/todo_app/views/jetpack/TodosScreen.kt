package com.example.todo_app.views.jetpack

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LeadingIconTab
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.todo_app.R
import com.example.todo_app.models.Todo
import com.example.todo_app.view_models.TodoViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun TodosScreen(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController
) {
    val mContext = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var historySearchItem = remember { mutableStateListOf("") }
    historySearchItem.remove("")
    val pagerState = rememberPagerState()
    val tabs: MutableList<TabItem> = ArrayList<TabItem>().toMutableList()
    tabs += TabItem("All") {
        AllFragment(viewModel, owner, navController)
    }
    tabs += TabItem("Pending") {
        PendingFragment(viewModel, owner, navController)
    }
    tabs += TabItem("Completed") {
        CompletedFragment(viewModel, owner, navController)
    }


    Scaffold(floatingActionButton = {
        FloatingActionButton(
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick =
            {
                navController.navigate("add_todo_page")
            })
        {
            Icon(Icons.Filled.Add, "")
        }
    }) {
        it
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = text,
                onQueryChange = { text = it },
                onSearch = {
                    if (!historySearchItem.contains(text))
                        historySearchItem.add(text)
                    active = false
                    viewModel.getKeyTranfer()
                        .postValue(
                            Objects
                                .requireNonNull(
                                    text
                                ).toString()
                        )
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = { Text("Search todo") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (active)
                        Icon(
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                    active = false
                                    viewModel.getKeyTranfer()
                                        .postValue(
                                            Objects
                                                .requireNonNull(
                                                    text
                                                ).toString()
                                        )
                                } else
                                    active = false
                            },
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close search bar"
                        )
                }
            ) {
                if (historySearchItem.isNotEmpty()) {
                    historySearchItem.forEach {
                        Row(
                            modifier = Modifier
                                .padding(all = 14.dp)
                                .clickable
                                {
                                    text = it
                                    active = false
                                    viewModel
                                        .getKeyTranfer()
                                        .setValue(
                                            Objects
                                                .requireNonNull(
                                                    text
                                                )
                                                .toString()
                                        )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_history_24),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(text = it)
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.size(16.dp))
            Tabs(tabs, pagerState, viewModel, navController)
            TabContent(tabs = tabs, pagerState = pagerState, viewModel, navController)
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(
    tabs: List<TabItem>,
    pagerState: PagerState,
    viewModel: TodoViewModel,
    navController: NavHostController
) {
    var dialogOpen by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val mContext = LocalContext.current

    Row {
        ScrollableTabRow(
            modifier = Modifier.weight(0.5f),
            edgePadding = 0.dp,
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.White,
            contentColor = colorResource(id = R.color.md_theme_dark_onPrimary),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    height = 3.dp,
                    color = Color.Blue
                )
            },

            ) {
            tabs.forEachIndexed { index, tabItem ->

                LeadingIconTab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            viewModel.resetCheckedList()
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(tabItem.title) },
                    icon = {},
                    selectedContentColor = Color.Blue,
                    unselectedContentColor = Color.White,
                    enabled = true,
                )

            }
        }
        if (dialogOpen) {
            CustomAlertDialog(dialogOpen,
                "Confirmation dialog",
                "Do you really want to delete these todo?",
                {
                    var temp: MutableList<Todo> = viewModel.checkedList.value as MutableList<Todo>

                    if (temp.size <= 0)
                        Toast.makeText(
                            mContext,
                            "Selected list is empty!",
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        val compositeDisposable = CompositeDisposable()
                        compositeDisposable.add(
                            viewModel
                                .deleteSelectedTodos()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(Action {
                                    Toast.makeText(
                                        mContext,
                                        "Todos has been deleted successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    compositeDisposable.dispose()
                                })
                        )
                    }
                    dialogOpen = false
                },
                {
                    dialogOpen = false
                })
        }
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.md_theme_light_error)),
            onClick = {
                dialogOpen = true
            }, shape = RoundedCornerShape(8.dp)

        ) {
            Text(
                text = "Clear",
                color = colorResource(R.color.white),
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContent(
    tabs: List<TabItem>,
    pagerState: PagerState,
    viewModel: TodoViewModel,
    navController: NavHostController
) {
    HorizontalPager(count = tabs.size, state = pagerState) { page ->
        tabs[page].screen()
    }
}

@Composable
fun AllFragment(viewModel: TodoViewModel, owner: LifecycleOwner, navController: NavHostController) {
    var todos by remember { mutableStateOf(ArrayList<Todo>()) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn {
            viewModel.getKeyTranfer().observe(owner) {
                viewModel.getListTodoAll(viewModel.getKeyTranfer().value)
                    .observe(owner) { item: List<Todo> ->
                        todos = item as ArrayList<Todo>
                    }
            }
            items(count = todos.size, key = {
                todos[it].id
            }, itemContent = { index ->
                val todo = todos[index]
                TodoItem(
                    viewModel,
                    owner,
                    navController,
                    todo,
                )
            })
        }
    }
}

@Composable
fun CompletedFragment(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController
) {
    var todos by remember { mutableStateOf(ArrayList<Todo>()) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn {
            viewModel.getKeyTranfer().observe(owner) {
                viewModel.getListTodoByStatus("Completed")
                    .observe(owner) { item: List<Todo> ->
                        todos = item as ArrayList<Todo>
                    }
            }
            items(count = todos.size, key = {
                todos[it].id
            }, itemContent = { index ->
                val todo = todos[index]
                TodoItem(
                    viewModel,
                    owner,
                    navController,
                    todo,
                )
            })
        }
    }
}

@Composable
fun PendingFragment(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController
) {
    var todos by remember { mutableStateOf(ArrayList<Todo>()) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn {
            viewModel.getKeyTranfer().observe(owner) {
                viewModel.getListTodoByStatus("Pending")
                    .observe(owner) { item: List<Todo> ->
                        todos = item as ArrayList<Todo>
                    }
            }
            items(count = todos.size, key = {
                todos[it].id
            }, itemContent = { index ->
                val todo = todos[index]
                TodoItem(
                    viewModel,
                    owner,
                    navController,
                    todo,
                )
            })
        }
    }
}


@Composable
fun TodoItem(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController,
    todoItem: Todo
) {
    val isChecked = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(align = Alignment.CenterVertically),
        elevation = 8.dp
    )
    {
        Row(
            modifier = Modifier.height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Checkbox(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.1f)
                    .padding(start = 8.dp),
                checked = isChecked.value,
                onCheckedChange =
                {
                    isChecked.value = it

                    var temp: MutableList<Todo> = viewModel.checkedList.value as MutableList<Todo>


                    if (temp != null) {
                        if (isChecked.value) {
                            if (!temp.contains(todoItem))
                                temp.add(todoItem)
                        } else {
                            if ((temp.contains(todoItem)))
                                temp.remove(todoItem)
                        }
                        viewModel.checkedList.postValue(temp)
                    }

                }
            )
            if (isChecked.value) {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth(fraction = 0.5f),
                    text = todoItem.title,
                    style = TextStyle(
                        textDecoration = TextDecoration.LineThrough
                    )
                )
                Text(
                    fontSize = 1.sp,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .fillMaxWidth(fraction = 0.45f), text = todoItem.createdDate,
                    style = TextStyle(
                        textDecoration = TextDecoration.LineThrough
                    )
                )
            } else {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth(fraction = 0.5f),
                    text = todoItem.title,
                )
                Text(
                    fontSize = 11.sp,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .fillMaxWidth(fraction = 0.45f),
                    text = todoItem.createdDate,
                )
            }
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_detail),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable
                    {
                        val moshi = Moshi
                            .Builder()
                            .build()
                        val jsonAdapter = moshi
                            .adapter(Todo::class.java)
                            .lenient()
                        val todoItemJson = jsonAdapter.toJson(todoItem)
                        navController.navigate("edit_todo_page/" + todoItemJson)
                    }
                    .size(32.dp)
                    .fillMaxWidth(fraction = 0.2f),
            )
        }
    }
}

