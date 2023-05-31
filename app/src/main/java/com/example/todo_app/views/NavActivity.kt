@file:Suppress("UNUSED_EXPRESSION")

package com.example.todo_app.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.todo_app.models.Todo
import com.example.todo_app.view_models.TodoViewModel
import com.example.todo_app.views.jetpack.SwitchScreen
import com.example.todo_app.views.jetpack.TodoAddScreen
import com.example.todo_app.views.jetpack.TodoEditScreen
import com.example.todo_app.views.jetpack.TodosScreen
import com.example.todo_app.views.jetpack.TodosScreenBottomSheet
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.squareup.moshi.Moshi

class NavActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: TodoViewModel =
            ViewModelProvider(this)[TodoViewModel::class.java]

        setContent {
            val navController = rememberAnimatedNavController()
            MainFrame(navController, this, viewModel)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainFrame(
    navController: NavHostController,
    owner: LifecycleOwner,
    viewModel: TodoViewModel,
) {
    viewModel.isShimmer = true

    MaterialTheme {
        AnimatedNavHost(navController = navController, startDestination = "switch") {
            composable("switch",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "todos_page", "todos_page_bs" ->
                                slideIntoContainer(
                                        AnimatedContentScope.SlideDirection.Right,
                                        animationSpec = tween(550)
                                )

                            else -> null
                        }
                    },) {
                SwitchScreen(
                        navController = navController
                )
            }
            composable("todos_page",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "switch" ->
                                slideIntoContainer(
                                        AnimatedContentScope.SlideDirection.Left,
                                        animationSpec = tween(550)
                                )

                            else -> null
                        }
                        when (initialState.destination.route) {
                            "add_todo_page", "edit_todo_page/{todo}" ->
                                slideIntoContainer(
                                        AnimatedContentScope.SlideDirection.Right,
                                        animationSpec = tween(550)
                                )

                            else -> null
                        }
                    },) {
                TodosScreen(
                        viewModel = viewModel,
                        owner = owner,
                        navController = navController
                )
            }
            composable("todos_page_bs",
                enterTransition = {
                    when (initialState.destination.route) {
                        "switch" ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(550)
                            )

                        else -> null
                    }
                },) {
                TodosScreenBottomSheet(
                    viewModel = viewModel,
                    owner = owner,
                    navController = navController
                )
            }
            composable("add_todo_page",
                enterTransition = {
                    when (initialState.destination.route) {
                        "todos_page" ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(550)
                            )

                        else -> null
                    }
                }) {
                TodoAddScreen(
                        viewModel = viewModel,
                        navController = navController
                )
            }
            composable("edit_todo_page/{todo}",
                arguments = listOf(navArgument("todo") { type = NavType.StringType }),
                enterTransition = {
                    when (initialState.destination.route) {
                        "todos_page" ->
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Left,
                                animationSpec = tween(550)
                            )

                        else -> null
                    }
                }) { backStackEntry ->

                val todoJson = backStackEntry.arguments?.getString("todo")
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter(Todo::class.java).lenient()
                val todoObjet = todoJson?.let { jsonAdapter.fromJson(it) }

                if (todoObjet != null) {
                    TodoEditScreen(
                            viewModel = viewModel,
                            todo = todoObjet,
                            navController = navController
                    )
                }
            }
        }
    }
}

