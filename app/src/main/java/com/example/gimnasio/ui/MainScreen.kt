package com.example.gimnasio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.gimnasio.R
import com.example.gimnasio.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Estado para controlar el título dinámico
    val currentScreenTitle = remember { mutableStateOf("Calabozo Gym") }

    val navItems = listOf(
        BottomNavItem.Usuarios,
        BottomNavItem.Calendario,
        BottomNavItem.Membresias,
        BottomNavItem.Perfil
    )

    // Actualiza el título según la pantalla actual
    LaunchedEffect(currentRoute) {
        currentScreenTitle.value = when(currentRoute) {
            BottomNavItem.Usuarios.route -> "Usuarios"
            BottomNavItem.Calendario.route -> "Calendario"
            BottomNavItem.Membresias.route -> "Membresías"
            BottomNavItem.Perfil.route -> "Mi Perfil"
            else -> "Calabozo Gym"
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = GymDarkBlue
            ) {
                // Header del drawer barra lateral
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(GymBrightRed),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pesas),
                            contentDescription = "Logo",
                            modifier = Modifier.size(80.dp),
                            tint = GymWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Calabozo Gym",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = GymWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Opciones del menú
                NavigationDrawerItem(
                    label = {
                        Text("Ajustes",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = GymWhite
                            )
                        )
                    },
                    selected = false,
                    onClick = { /* Lógica */ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Ajustes",
                            tint = GymWhite
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )
                NavigationDrawerItem(
                    label = {
                        Text("Cerrar sesión",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = GymWhite
                            )
                        )
                    },
                    selected = false,
                    onClick = { /* Lógica */ },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Cerrar sesión",
                            tint = GymWhite
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            currentScreenTitle.value,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GymDarkBlue
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_menu),
                                contentDescription = "Menú",
                                tint = GymDarkBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = GymLightGray,
                        navigationIconContentColor = GymDarkBlue,
                        titleContentColor = GymDarkBlue
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = GymDarkBlue,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(id = item.iconResId),
                                    contentDescription = item.label,
                                    tint = if (currentRoute == item.route) GymBrightRed else GymWhite
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (currentRoute == item.route) GymBrightRed else GymWhite
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                currentScreenTitle.value = item.label
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = GymDarkBlue,
                                selectedIconColor = GymBrightRed,
                                selectedTextColor = GymBrightRed,
                                unselectedIconColor = GymWhite,
                                unselectedTextColor = GymWhite
                            )
                        )
                    }
                }
            },
		//Prueba
            floatingActionButton = {
                when (currentRoute) {
                    BottomNavItem.Usuarios.route -> {
                        FloatingActionButton(
                            onClick = { navController.navigate("agregar_usuario") },
                            containerColor = GymBrightRed,
                            contentColor = GymWhite
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Agregar Usuario"
                            )
                        }
                    }
                    BottomNavItem.Membresias.route -> {
                        FloatingActionButton(
                            onClick = { navController.navigate("agregar_membresia") },
                            containerColor = GymBrightRed,
                            contentColor = GymWhite
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Agregar Membresia"
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Usuarios.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(BottomNavItem.Usuarios.route) {
                    UsuariosScreen(navController = navController)
                }
                composable(BottomNavItem.Calendario.route) {
                    CalendarioScreen(navController = navController)
                }
                composable(BottomNavItem.Membresias.route) {
                    MembresiasScreen(navController = navController)
                }
                composable("agregar_usuario") {
                    AgregarUsuarioScreen(navController = navController)
                }
                composable("editar_usuario/{usuarioId}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull()
                    id?.let {
                        EditarUsuarioScreen(usuarioId = it, navController = navController)
                    }
                }
                composable("usuario_detalle/{usuarioId}") { backStackEntry ->
                    backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull()?.let { id ->
                        UsuarioDetalleScreen(usuarioId = id, navController = navController)
                    }
                }
                composable("asignar_membresia/{usuarioId}") { backStackEntry ->
                    val usuarioId = backStackEntry.arguments?.getString("usuarioId")?.toIntOrNull() ?: return@composable
                    AsignarMembresiaScreen(usuarioId = usuarioId, navController = navController)
                }
                composable("agregar_membresia") {
                    AgregarMembresiaScreen(navController = navController)
                }
                composable("editar_membresia/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                    id?.let {
                        EditarMembresiaScreen(membresiaId = it, navController = navController)
                    }
                }
                composable("perfil") {
                    PerfilScreen(navController = navController)
                }
                composable("estadisticas_usuarios") {
                    EstadisticasUsuariosScreen(navController = navController)
                }
                composable("estadisticas_inscripciones") {
                    EstadisticasInscripcionesScreen(navController = navController)
                }

            }
        }
    }
}
