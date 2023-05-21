package com.example.todo_app.views.jetpack

import androidx.compose.runtime.Composable

typealias ComposableFun = @Composable () -> Unit

class TabItem(var title: String, var screen: ComposableFun)