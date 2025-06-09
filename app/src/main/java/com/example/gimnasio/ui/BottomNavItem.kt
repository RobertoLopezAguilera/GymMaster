package com.example.gimnasio.ui

import androidx.annotation.DrawableRes
import com.example.gimnasio.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val iconResId: Int
) {
    object Usuarios : BottomNavItem("usuarios", "Usuarios", R.drawable.ic_usuario)
    object Calendario : BottomNavItem("calendario", "Calendario", R.drawable.ic_calendario)
    object Membresias : BottomNavItem("membresias", "Membresías", R.drawable.ic_membresia)
    object Perfil : BottomNavItem("perfil", "Perfil", R.drawable.ic_person_perfil)
}
