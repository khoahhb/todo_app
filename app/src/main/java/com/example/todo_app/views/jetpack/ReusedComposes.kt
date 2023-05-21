package com.example.todo_app.views.jetpack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun CustomTextField(label: String, value: String, onValueChanged: (String) -> Unit) {
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
        label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 21.sp) },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun CustomDatePicker(label: String, value: String, onDateChangeFunc: (LocalDate) -> Unit) {
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
        label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 21.sp) },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun CustomDropdown(
    label: String,
    value: String,
    onSelectionChange: (String) -> Unit,
    editStatus: String
) {

    val todoStatus = arrayOf("Pending", "Completed")
    var expanded by remember { mutableStateOf(false) }
    var index = 0
    if (todoStatus[1].equals(editStatus)) index = 1
    var selectedText by remember { mutableStateOf(todoStatus[index]) }

    ExposedDropdownMenuBox(
        modifier = Modifier.padding(bottom = 24.dp),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            label = { Text(text = label, fontWeight = FontWeight.Bold, fontSize = 21.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 20.sp),
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

