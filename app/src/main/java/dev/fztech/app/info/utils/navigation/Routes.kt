package dev.fztech.app.info.utils.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Detail : Routes("detail")
}