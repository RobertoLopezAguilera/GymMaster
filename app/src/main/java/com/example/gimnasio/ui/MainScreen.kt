package com.example.gimnasio.ui

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.example.gimnasio.R
import com.example.gimnasio.ui.theme.*
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val context = LocalContext.current

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
        gesturesEnabled = false, //Deshabilita deslizamiento
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = GymDarkBlue
            ) {
                // BOTÓN DE CERRAR DRAWER (parte superior)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = { scope.launch { drawerState.close() } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Cerrar Drawer",
                            tint = GymWhite
                        )
                    }
                }

                // HEADER
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
                        Text("Gym Manager",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = GymWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                //  ITEM: EstadisticasUsuario
                NavigationDrawerItem(
                    label = {
                        Text("Datos sobre Usuarios", style = MaterialTheme.typography.bodyLarge.copy(color = GymWhite))
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("estadisticas_usuarios")
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stats),
                            contentDescription = "Usuarios",
                            tint = GymWhite
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )

                //  ITEM: EstadisticasUsuario
                NavigationDrawerItem(
                    label = {
                        Text("Datos sobre Inscripciones", style = MaterialTheme.typography.bodyLarge.copy(color = GymWhite))
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("estadisticas_inscripciones")
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stats_iscripciones),
                            contentDescription = "Inscripciones",
                            tint = GymWhite
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )

                //  ITEM: Lista de inscripciones
                NavigationDrawerItem(
                    label = {
                        Text("Lista de Inscripciones", style = MaterialTheme.typography.bodyLarge.copy(color = GymWhite))
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("inscripciones_lista")
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lista_inscripciones),
                            contentDescription = "Inscripciones Lista",
                            tint = GymWhite
                        )
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
                )

                //  ITEM: Ajustes
                NavigationDrawerItem(
                    label = {
                        Text("Ajustes", style = MaterialTheme.typography.bodyLarge.copy(color = GymWhite))
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Lógica de navegación o ajustes
                    },
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

                //  ITEM: Cerrar sesión
                NavigationDrawerItem(
                    label = {
                        Text("Cerrar sesión", style = MaterialTheme.typography.bodyLarge.copy(color = GymWhite))
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        FirebaseAuth.getInstance().signOut()
                        LoginManager.getInstance().logOut() // <-- Cierre de sesión de Facebook

                        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        sharedPreferences.edit().remove("USER_EMAIL").apply()

                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    },
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
                composable("editar_usuario/{usuarioId}",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("usuarioId") ?: return@composable
                    EditarUsuarioScreen(usuarioId = id, navController = navController)
                }

                composable("usuario_detalle/{usuarioId}",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("usuarioId") ?: return@composable
                    UsuarioDetalleScreen(usuarioId = id, navController = navController)
                }

                composable("asignar_membresia/{usuarioId}",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: return@composable
                    AsignarMembresiaScreen(usuarioId = usuarioId, navController = navController)
                }

                composable("historial_usuario/{usuarioId}",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: return@composable
                    HistorialUsuarioScreen(usuarioId = usuarioId, navController = navController)
                }

                composable("agregar_membresia") {
                    AgregarMembresiaScreen(navController = navController)
                }
                composable("inscripciones_lista") {
                    InscripcionesScreen(navController = navController)
                }
                composable("editar_membresia/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: return@composable
                    EditarMembresiaScreen(membresiaId = id, navController = navController)
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

                // Agrega esta nueva ruta para InscripcionesDiaScreen
                composable(
                    "inscripciones/{fecha}/{tipoVisualizacion}",
                    arguments = listOf(
                        navArgument("fecha") { type = NavType.StringType },
                        navArgument("tipoVisualizacion") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val fechaStr = backStackEntry.arguments?.getString("fecha") ?: ""
                    val tipo = backStackEntry.arguments?.getString("tipoVisualizacion") ?: ""
                    val fecha = try {
                        LocalDate.parse(fechaStr)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }

                    InscripcionesDiaScreen(
                        fecha = fecha,
                        tipoVisualizacion = tipo,
                        viewModel = viewModel(),
                        usuarioViewModel = viewModel(),
                        navController = navController,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
