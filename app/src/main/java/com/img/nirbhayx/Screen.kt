package com.img.nirbhayx

import androidx.annotation.DrawableRes

open class Screen(val title: String, val route: String) {

    open class DrawerScreen(val dITitle: String, val dRoute: String, @DrawableRes val icon: Int) :
        Screen(dITitle, dRoute) {

        object Home : DrawerScreen("Home", "home", R.drawable.ic_home)
        object Profile : DrawerScreen("Profile", "profile", R.drawable.ic_profile)
        object EmergencySharing :
            DrawerScreen("Emergency Sharing", "emergency_sharing", R.drawable.ic_emergency_sharing)

        object EmergencyContacts : DrawerScreen(
            "Emergency Contacts",
            "emergency_contacts",
            R.drawable.ic_emergency_contacts
        )

        object Community : DrawerScreen("Community", "community", R.drawable.ic_community)
        object SafetyTips : DrawerScreen("Safety Tips", "safety_tips", R.drawable.ic_safety_tips)
        object Settings : DrawerScreen("Settings", "settings", R.drawable.ic_settings)
        object AboutUs : DrawerScreen("About", "about", R.drawable.ic_about)
        object Help : DrawerScreen("Help", "help", R.drawable.ic_help)
    }

    companion object {
        val screenInDrawerScreen = listOf(
            DrawerScreen.Home,
            DrawerScreen.EmergencySharing,
            DrawerScreen.Profile,
            DrawerScreen.EmergencyContacts,
            DrawerScreen.Community,
            DrawerScreen.SafetyTips
        )

        val screensInModelSheetState = listOf(
            DrawerScreen.Settings,
            DrawerScreen.AboutUs,
            DrawerScreen.Help
        )
    }
}