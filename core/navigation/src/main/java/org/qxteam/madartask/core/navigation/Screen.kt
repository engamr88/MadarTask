package org.qxteam.madartask.core.navigation

sealed class Screen(val route: String) {
    object Input : Screen("input")
    object Display : Screen("display")
}
