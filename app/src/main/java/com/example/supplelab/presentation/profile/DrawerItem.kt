package com.example.supplelab.presentation.profile

import com.example.supplelab.R

enum class DrawerItem(
    val title: String,
    val icon: Int
) {
    Profile(
        title = "Profile",
        icon = R.drawable.user
    ),
    Blog(
        title = "Blogs",
        icon = R.drawable.book
    ),
    Locations(
        title = "Logcatio",
        icon = R.drawable.map_pin
    ),
    ContactUs(
        title = "Contact Us",
        icon = R.drawable.edit
    ),
    SignOut(
        title = "Sign Out",
        icon = R.drawable.log_out
    ),
    Admin(
        title = "Admin Panel",
        icon = R.drawable.unlock
    )
}