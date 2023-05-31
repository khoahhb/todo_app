@file:Suppress("UNUSED_EXPRESSION")

package com.example.todo_app.views.jetpack

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

typealias ComposableFun = @Composable () -> Unit

class TabItem(var title: String, var screen: ComposableFun)

@Composable
fun CustomTextField(
        label: String,
        value: String,
        onValueChanged: (String) -> Unit) {

    var error: String by remember { mutableStateOf("") }
    var intialState: Number = 0

    OutlinedTextField(
            isError = error.isNotBlank(),
            modifier = Modifier.onFocusChanged {
                it
                if (!it.isFocused && value.isEmpty() && intialState == 1) {
                    error = "This field can not be empty!"
                } else {
                    error = ""
                    intialState = 1
                }
            },
            value = value,
            singleLine = true,
            onValueChange = onValueChanged,
            label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 19.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            trailingIcon = {
                if (error.isNotBlank()) {
                    Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red
                    )
                }
            },
    )
    Text(text = error, color = Color.Red)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
        label: String,
        value: String,
        onDateChangeFunc: (LocalDate) -> Unit) {

    var error: String by remember { mutableStateOf("") }
    var intialState: Number = 0
    val calendarState = rememberSheetState()

    CalendarDialog(
            state = calendarState,
            selection = CalendarSelection.Date { date -> onDateChangeFunc(date) }
    )
    OutlinedTextField(
            isError = error.isNotBlank(),
            modifier = Modifier
                    .onFocusChanged {
                        it
                        if (!it.isFocused) {
                            if (value.isEmpty() && intialState == 1)
                                error = "This field can not be empty!"
                        } else {
                            error = ""
                            calendarState.show()
                        }
                    }
                    .clickable(
                            onClick = {
                                intialState = 1

                            }),
            value = value,
            singleLine = true,
            readOnly = true,
            onValueChange = {},
            label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 19.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
            trailingIcon = {
                if (error.isNotBlank()) {
                    Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red
                    )
                }
            },
    )
    Text(text = error, color = Color.Red)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomDropdown(
        label: String,
        onSelectionChange: (String) -> Unit,
        editStatus: String) {

    var selectedText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val todoStatus = arrayOf("Pending", "Completed")
    var index = 0
    if (todoStatus[1] == editStatus) index = 1

    selectedText = todoStatus[index]

    ExposedDropdownMenuBox(
            modifier = Modifier.padding(bottom = 20.dp),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
    ) {
        OutlinedTextField(
                label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 19.sp) },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                singleLine = true,
                value = selectedText,
                onValueChange = onSelectionChange,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
        ) {
            todoStatus.forEach { item ->
                DropdownMenuItem(
                        text = { Text(text = item, fontSize = 18.sp) },
                        onClick = {
                            selectedText = item
                            onSelectionChange(item)
                            expanded = false
                        }
                )
            }
        }
    }
}

@Composable
fun CustomAlertDialog(
        isOpen: Boolean,
        tile: String,
        content: String,
        handleConfirmFunc: () -> Unit,
        handleDismissFunc: () -> Unit
) {

    val openDialog = remember { mutableStateOf(isOpen) }

    if (openDialog.value) {
        AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                    handleDismissFunc()
                },
                title = {
                    Text(text = tile)
                },
                text = {
                    Text(text = content)
                },
                confirmButton = {
                    TextButton(
                            onClick = {
                                openDialog.value = false
                                handleConfirmFunc()
                            }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(
                            onClick = {
                                openDialog.value = false
                                handleDismissFunc()
                            }
                    ) {
                        Text("Dismiss")
                    }
                }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomBottomSheet(
        bottomSheetState: ModalBottomSheetState,
        sheetContent: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                sheetContent()
            },
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            sheetElevation = 16.dp
    ) {
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1200, delayMillis = 100, easing = LinearOutSlowInEasing)
            )
    )

    background(
            brush = Brush.linearGradient(
                    colors = listOf(
                            Color(0xFFB8B5B5),
                            Color(0xFF8F8B8B),
                            Color(0xFFB8B5B5),
                    ),
                    start = Offset(startOffsetX, 0f),
                    end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
            )
    )
            .onGloballyPositioned {
                size = it.size
            }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
        isBS: Boolean,
        navBack: () -> Unit,
        goSwitch: () -> Unit,
        goMain: () -> Unit,
        goAdd: () -> Unit,
) {

    var showMenu by remember { mutableStateOf(false) }
    LocalContext.current

    TopAppBar(
            modifier = Modifier
                    .fillMaxHeight(0.08f)
                    .padding(bottom = 0.dp),
            title = {
                Text(text = "", color = Color.Black)
            },
            actions = {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(onClick = {goSwitch() }, text = {  Text(text = "Switch")}, leadingIcon = {Icon(Icons.Default.Menu, "")})
                    if(!isBS){
                        DropdownMenuItem(onClick = {goMain() }, text = {  Text(text = "Main")}, leadingIcon = {Icon(Icons.Default.Home, "")})
                        DropdownMenuItem(onClick = { goAdd()}, text = {  Text(text = "Add")}, leadingIcon = {Icon(Icons.Default.Add, "")})
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = navBack, modifier = Modifier.padding(start = 4.dp, top = 8.dp)) {
                    Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "backIcon",
                            modifier = Modifier.size(40.dp))
                }
            },
            colors = topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
            )
    )
}
