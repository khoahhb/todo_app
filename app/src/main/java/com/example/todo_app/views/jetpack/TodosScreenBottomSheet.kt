@file:Suppress("UNUSED_EXPRESSION")

package com.example.todo_app.views.jetpack

import android.widget.Toast
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LeadingIconTab
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun TodosScreenBottomSheet(
        viewModel: TodoViewModel,
        owner: LifecycleOwner,
        navController: NavHostController
) {

    viewModel.isShimmer = true

    val scope = rememberCoroutineScope()

    val bottomSheetStateAdd = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden, animationSpec = tween(
            durationMillis = 200,
            delayMillis = 0,
            easing = FastOutLinearInEasing,
    ), skipHalfExpanded = true
    )


    val bottomSheetStateEdit = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden, animationSpec = tween(
            durationMillis = 200,
            delayMillis = 0,
            easing = FastOutLinearInEasing,
    ), skipHalfExpanded = true
    )

    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val historySearchItem = remember { mutableStateListOf("") }
    historySearchItem.remove("")
    val pagerState = rememberPagerState()
    val tabs: MutableList<TabItem> = ArrayList<TabItem>().toMutableList()

    tabs += TabItem("All") {
        AllFragmentBS(viewModel, owner)
    }
    tabs += TabItem("Pending") {
        PendingFragmentBS(viewModel, owner)
    }
    tabs += TabItem("Completed") {
        CompletedFragmentBS(viewModel, owner)
    }

    viewModel.scope = scope
    viewModel.modalAddState = bottomSheetStateAdd
    viewModel.modalEditState = bottomSheetStateEdit

    viewModel.todoEditItem.observe(owner) { item ->
        if (item.id > 0) {
            viewModel.scope.launch { viewModel.modalEditState.show() }
        }
    }

    Scaffold(
            modifier = Modifier,
            topBar = { CustomTopAppBar(true, {navController.navigateUp()},{navController.navigate("switch")},{},{})  },
            floatingActionButton = {
                FloatingActionButton(
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick =
                        {
                            scope.launch {
                                bottomSheetStateAdd.show()
                            }
                        })
                {
                    Icon(Icons.Filled.Add, "")
                }
            }) { it ->
        it
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                    text = "Main screen",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 16.dp, top = 0.dp),
            )
            SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = {
                        if (!historySearchItem.contains(text))
                            historySearchItem.add(text)
                        active = false
                        viewModel.getKeyTransfer()
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
                                            viewModel.getKeyTransfer()
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
                                                    .getKeyTransfer()
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
            TabsBS(tabs, pagerState, viewModel)
            TabContentBS(tabs = tabs, pagerState = pagerState)
        }
        CustomBottomSheet(
                bottomSheetState = bottomSheetStateAdd,
                sheetContent = { TodoAddScreenBottomSheet(viewModel) })
        CustomBottomSheet(
                bottomSheetState = bottomSheetStateEdit,
                sheetContent = { TodoEditScreenBottomSheet(viewModel, owner) })
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsBS(
        tabs: List<TabItem>,
        pagerState: PagerState,
        viewModel: TodoViewModel,
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
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                            modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                    )
                },
        )
        {
            tabs.forEachIndexed { index, tabItem ->
                LeadingIconTab(
                        selected = pagerState.currentPage == index,
                        onClick =
                        {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(tabItem.title) },
                        icon = {},
                        selectedContentColor = MaterialTheme.colorScheme.primary,
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
                        val temp: MutableList<Todo> =
                                TodoViewModel.checkedList.value as MutableList<Todo>

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
                                            .subscribe {
                                                Toast.makeText(
                                                        mContext,
                                                        "Todos has been deleted successfully!",
                                                        Toast.LENGTH_SHORT
                                                ).show()
                                                viewModel.resetUncheckedList()
                                                viewModel.resetCheckedList()
                                                compositeDisposable.dispose()
                                            }
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
                onClick =
                {
                    dialogOpen = true
                },
                shape = RoundedCornerShape(8.dp))
        {
            Text(
                    text = "Clear",
                    color = colorResource(R.color.white),
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContentBS(
        tabs: List<TabItem>,
        pagerState: PagerState,
) {
    HorizontalPager(count = tabs.size, state = pagerState)
    { page ->
        tabs[page].screen()
    }
}

@Composable
fun AllFragmentBS(
        viewModel: TodoViewModel,
        owner: LifecycleOwner,
) {

    val todoAllList = remember {
        mutableStateListOf<Todo>()
    }

    val todoMainList = remember {
        mutableStateListOf<Todo>()
    }

    val numberItemPerPage: Int = remember { 20 }
    var startIndex: Int
    var endtIndex: Int = remember { viewModel.endIndexAll }

    var isLoading by remember {
        mutableStateOf(viewModel.isShimmer)
    }

    viewModel.getKeyTransfer().observe(owner) {
        viewModel.listTodoAll
                .observe(owner) { item ->
                    startIndex = 0
                    todoAllList.clear()
                    todoMainList.clear()
                    todoAllList.addAll(item)
                    if (todoAllList.size < 20)
                        endtIndex = todoAllList.size
                    todoMainList.addAll(todoAllList.slice(startIndex until endtIndex))
                }
    }

    LaunchedEffect(key1 = true) {
        delay(2000)
        isLoading = false
        viewModel.isShimmer = false
    }

    Column(
            modifier = Modifier.fillMaxSize())
    {
        LazyColumn(modifier = Modifier)
        {

            items(
                    count = todoMainList.size,
                    key =
                    {
                        todoMainList[it].id
                    },
                    itemContent =
                    { index ->
                        val todo = todoMainList[index]
                        TodoItemBS(
                                isLoading,
                                viewModel,
                                owner,
                                todo,
                        )
                    })

            if (todoAllList.size > todoMainList.size) {
                item {
                    Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            horizontalArrangement = Arrangement.Center)
                    {
                        CircularProgressIndicator()
                    }
                    LaunchedEffect(Unit) {
                        delay(1000)
                        startIndex = endtIndex
                        endtIndex += numberItemPerPage
                        if (todoAllList.size - 1 < endtIndex)
                            endtIndex = todoAllList.size
                        viewModel.endIndexAll = endtIndex
                        val tempList: MutableList<Todo> = todoAllList.slice(startIndex until endtIndex) as MutableList<Todo>
                        todoMainList.addAll(tempList)
                    }
                }
            }
        }
    }
}

@Composable
fun PendingFragmentBS(
        viewModel: TodoViewModel,
        owner: LifecycleOwner,
) {

    val todoAllList = remember {
        mutableStateListOf<Todo>()
    }

    val todoMainList = remember {
        mutableStateListOf<Todo>()
    }

    val numberItemPerPage: Int = remember { 20 }
    var startIndex: Int
    var endtIndex: Int = remember { viewModel.endIndexPending }

    viewModel.getKeyTransfer().observe(owner) {
        viewModel.getListTodoByStatus("Pending")
                .observe(owner) { item ->
                    startIndex = 0
                    todoAllList.clear()
                    todoMainList.clear()
                    todoAllList.addAll(item)
                    if (todoAllList.size < 20)
                        endtIndex = todoAllList.size
                    todoMainList.addAll(todoAllList.slice(startIndex until endtIndex))
                }
    }

    Column(
            modifier = Modifier.fillMaxSize())
    {
        LazyColumn(modifier = Modifier)
        {

            items(
                    count = todoMainList.size,
                    key =
                    {
                        todoMainList[it].id
                    },
                    itemContent =
                    { index ->
                        val todo = todoMainList[index]
                        TodoItemBS(
                                false,
                                viewModel,
                                owner,
                                todo,
                        )
                    })

            if (todoAllList.size > todoMainList.size) {
                item {
                    Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            horizontalArrangement = Arrangement.Center)
                    {
                        CircularProgressIndicator()
                    }
                    LaunchedEffect(Unit) {
                        delay(1000)
                        startIndex = endtIndex
                        endtIndex += numberItemPerPage
                        if (todoAllList.size - 1 < endtIndex)
                            endtIndex = todoAllList.size
                        viewModel.endIndexPending = endtIndex
                        val tempList: MutableList<Todo> = todoAllList.slice(startIndex until endtIndex) as MutableList<Todo>
                        todoMainList.addAll(tempList)
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedFragmentBS(
        viewModel: TodoViewModel,
        owner: LifecycleOwner,
) {

    val todoAllList = remember {
        mutableStateListOf<Todo>()
    }

    val todoMainList = remember {
        mutableStateListOf<Todo>()
    }

    val numberItemPerPage: Int = remember { 20 }
    var startIndex: Int
    var endtIndex: Int = remember { viewModel.endIndexCompleted }

    viewModel.getKeyTransfer().observe(owner) {
        viewModel.getListTodoByStatus("Completed")
                .observe(owner) { item ->
                    startIndex = 0
                    todoAllList.clear()
                    todoMainList.clear()
                    todoAllList.addAll(item)
                    if (todoAllList.size < 20)
                        endtIndex = todoAllList.size
                    todoMainList.addAll(todoAllList.slice(startIndex until endtIndex))
                }
    }

    Column(
            modifier = Modifier.fillMaxSize())
    {
        LazyColumn(modifier = Modifier)
        {

            items(
                    count = todoMainList.size,
                    key =
                    {
                        todoMainList[it].id
                    },
                    itemContent =
                    { index ->
                        val todo = todoMainList[index]
                        TodoItemBS(
                                false,
                                viewModel,
                                owner,
                                todo,
                        )
                    })

            if (todoAllList.size > todoMainList.size) {
                item {
                    Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            horizontalArrangement = Arrangement.Center)
                    {
                        CircularProgressIndicator()
                    }
                    LaunchedEffect(Unit) {
                        delay(1000)
                        startIndex = endtIndex
                        endtIndex += numberItemPerPage
                        if (todoAllList.size - 1 < endtIndex)
                            endtIndex = todoAllList.size
                        viewModel.endIndexCompleted = endtIndex
                        val tempList: MutableList<Todo> = todoAllList.slice(startIndex until endtIndex) as MutableList<Todo>
                        todoMainList.addAll(tempList)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodoItemBS(
        isLoading: Boolean,
        viewModel: TodoViewModel,
        owner: LifecycleOwner,
        todoItem: Todo,
) {

    val isChecked = remember { mutableStateOf(false) }

    var isVisible by remember { mutableStateOf(false) }

    val contentScale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.9f,
            animationSpec = tween(durationMillis = 300)
    )

    viewModel.checkedList.observe(owner) {
        if (it.contains(todoItem)) {
            isChecked.value = true
        }
    }

    if (isLoading) {
        Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically),
                elevation = 8.dp,
                onClick =
                {

                }
        )
        {
            Row(
                    modifier = Modifier
                            .height(64.dp)
                            .shimmerEffect(),
                    verticalAlignment = Alignment.CenterVertically
            )
            {
                Checkbox(
                        modifier = Modifier
                                .fillMaxWidth(fraction = 0.1f)
                                .padding(start = 8.dp),
                        checked = false,
                        onCheckedChange =
                        {

                        }
                )
                Text(
                        modifier = Modifier
                                .padding(start = 16.dp)
                                .fillMaxWidth(fraction = 0.5f),
                        text = "",
                )
                Text(
                        fontSize = 11.sp,
                        modifier = Modifier
                                .padding(start = 6.dp)
                                .fillMaxWidth(fraction = 0.45f),
                        text = "",
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
                                }
                                .size(32.dp)
                                .fillMaxWidth(fraction = 0.2f),
                )
            }
        }
    } else {
        Card(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .scale(contentScale),
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
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
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
                        style =
                        if (isChecked.value)
                            TextStyle(textDecoration = TextDecoration.LineThrough
                            ) else
                            TextStyle(textDecoration = TextDecoration.None)
                )
                Text(
                        fontSize = 11.sp,
                        modifier = Modifier
                                .padding(start = 6.dp)
                                .fillMaxWidth(fraction = 0.45f),
                        text = todoItem.createdDate,
                        style =
                        if (isChecked.value)
                            TextStyle(textDecoration = TextDecoration.LineThrough
                            ) else
                            TextStyle(textDecoration = TextDecoration.None)
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
                                    viewModel.todoEditItem.postValue(todoItem)
                                }
                                .size(32.dp)
                                .fillMaxWidth(fraction = 0.2f),
                )
            }
        }
        LaunchedEffect(todoItem) {
            isVisible = true
        }
    }
}