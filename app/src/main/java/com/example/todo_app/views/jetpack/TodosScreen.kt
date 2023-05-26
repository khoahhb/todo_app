package com.example.todo_app.views.jetpack

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay
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
                    var temp: MutableList<Todo> = TodoViewModel.checkedList.value as MutableList<Todo>

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
                                .deleteCheckedTodos()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(Action {
                                    Toast.makeText(
                                        mContext,
                                        "Todos has been deleted successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.resetUncheckedList()
                                    viewModel.resetCheckedList()
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

    val todoAllList = remember {
        mutableStateListOf<Todo>()
    }
    val todoMainList = remember {
        mutableStateListOf<Todo>()
    }

    var numberItemPerPage: Int = remember { 10 }
    var startIndex: Int = remember { 0 }
    var endtIndex: Int = remember { startIndex + numberItemPerPage }

    viewModel.getKeyTranfer().observe(owner) {
        viewModel.listTodoAll
            .observe(owner) { item ->
                startIndex = 0
                endtIndex = 0
                todoAllList.clear()
                todoMainList.clear()
                todoAllList.addAll(item)
//                if (todoAllList.size < 10)
//                    endtIndex = todoAllList.size
//                todoMainList.addAll(todoAllList.slice(startIndex until endtIndex))
                todoMainList.addAll(todoAllList)
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier) {
            items(count = todoMainList.size, key = {
                todoMainList[it].id
            }, itemContent = { index ->
                val todo = todoMainList.get(index)
                TodoItem(
                    viewModel,
                    owner,
                    navController,
                    todo,
                )
            })
//            item {
//                if (todoAllList.size > todoMainList.size) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                    LaunchedEffect(Unit) {
//                        delay(1000)
//                        var tempList: MutableList<Todo>
//                        startIndex = endtIndex
//                        endtIndex += numberItemPerPage
//                        if (todoAllList.size - 1 < endtIndex)
//                            endtIndex = todoAllList.size
//                        tempList =
//                            todoAllList.slice(startIndex until endtIndex) as MutableList<Todo>
//                        todoMainList.addAll(tempList)
//                    }
//                }
//            }
        }
    }
}

@Composable
fun CompletedFragment(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController
) {

    val todoAllList = remember {
        mutableStateListOf<Todo>()
    }
    val todoMainList = remember {
        mutableStateListOf<Todo>()
    }

    var numberItemPerPage: Int = remember { 10 }
    var startIndex: Int = remember { 0 }
    var endtIndex: Int = remember { startIndex + numberItemPerPage }

    viewModel.getKeyTranfer().observe(owner) {
        viewModel.getListTodoByStatus("Completed")
            .observe(owner) { item ->
                startIndex = 0
                endtIndex = 0
                todoAllList.clear()
                todoMainList.clear()
                todoAllList.addAll(item)
                if (todoAllList.size < 10)
                    endtIndex = todoAllList.size
                todoMainList.addAll(todoAllList.slice(startIndex until endtIndex))
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier) {
            items(count = todoMainList.size, key = {
                todoMainList[it].id
            }, itemContent = { index ->
                val todo = todoMainList.get(index)
                TodoItem(
                    viewModel,
                    owner,
                    navController,
                    todo,
                )
            })
            item {
                if (todoAllList.size > todoMainList.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    LaunchedEffect(Unit) {
                        delay(1000)
                        var tempList: MutableList<Todo>
                        startIndex = endtIndex
                        endtIndex += numberItemPerPage
                        if (todoAllList.size - 1 < endtIndex)
                            endtIndex = todoAllList.size
                        tempList =
                            todoAllList.slice(startIndex until endtIndex) as MutableList<Todo>
                        todoMainList.addAll(tempList)
                    }
                }
            }
        }
    }
}

@Composable
fun PendingFragment(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController
) {

    val todoAllList = remember {
        mutableStateListOf<Todo>()
    }
    val todoMainList = remember {
        mutableStateListOf<Todo>()
    }

    var numberItemPerPage: Int = remember { 10 }
    var startIndex: Int = remember { 0 }
    var endtIndex: Int = remember { startIndex + numberItemPerPage }

    viewModel.getKeyTranfer().observe(owner) {
        viewModel.getListTodoByStatus("Pending")
            .observe(owner) { item ->
                startIndex = 0
                endtIndex = 0
                todoAllList.clear()
                todoMainList.clear()
                todoAllList.addAll(item)
                if (todoAllList.size < 10)
                    endtIndex = todoAllList.size
                todoMainList.addAll(todoAllList.slice(startIndex until endtIndex))
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier) {
            items(count = todoMainList.size, key = {
                todoMainList[it].id
            }, itemContent = { index ->
                val todo = todoMainList.get(index)
                TodoItem(
                    viewModel,
                    owner,
                    navController,
                    todo,
                )
            })
            item {
                if (todoAllList.size > todoMainList.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    LaunchedEffect(Unit) {
                        delay(1000)
                        var tempList: MutableList<Todo>
                        startIndex = endtIndex
                        endtIndex += numberItemPerPage
                        if (todoAllList.size - 1 < endtIndex)
                            endtIndex = todoAllList.size
                        tempList =
                            todoAllList.slice(startIndex until endtIndex) as MutableList<Todo>
                        todoMainList.addAll(tempList)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodoItem(
    viewModel: TodoViewModel,
    owner: LifecycleOwner,
    navController: NavHostController,
    todoItem: Todo
) {
    val isChecked = remember { mutableStateOf(false) }

    viewModel.checkedList.observe(owner) {
        if (it.contains(todoItem)) {
            isChecked.value = true
        }
    }

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight(align = Alignment.CenterVertically),
        elevation = 8.dp,
        onClick =
        {
            isChecked.value = !isChecked.value
            if (isChecked.value) {
                viewModel.setCheckItem(todoItem, true)
            } else {
                viewModel.setCheckItem(todoItem, false)
            }
        }
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

                    if (isChecked.value) {
                        viewModel.setCheckItem(todoItem, true)
                    } else {
                        viewModel.setCheckItem(todoItem, false)

                    }

                }
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(fraction = 0.5f),
                text = todoItem.title,
                style = if (isChecked.value) TextStyle(
                    textDecoration = TextDecoration.LineThrough
                ) else TextStyle(textDecoration = TextDecoration.None)
            )
            Text(
                fontSize = 11.sp,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .fillMaxWidth(fraction = 0.45f),
                text = todoItem.createdDate,
                style = if (isChecked.value) TextStyle(
                    textDecoration = TextDecoration.LineThrough
                ) else TextStyle(textDecoration = TextDecoration.None)
            )
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

