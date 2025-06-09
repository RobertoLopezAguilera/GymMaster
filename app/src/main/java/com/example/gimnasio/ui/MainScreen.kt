package com.example.gimnasio.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.gimnasio.data.model.Inscripcion
import com.example.gimnasio.data.model.Membresia
import com.example.gimnasio.viewmodel.InscripcionViewModel
import com.example.gimnasio.viewmodel.MembresiasViewModel
import com.example.gimnasio.viewmodel.UsuarioDetalleViewModel
import com.example.gimnasio.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import com.example.gimnasio.R
import com.example.gimnasio.data.model.Usuario
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
                // Header del drawer
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
                composable("agregar_membresia") {
                    PerfilScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun FabMenu(
    modifier: Modifier = Modifier,
    onEditarClick: () -> Unit,
    onAsignarMembresiaClick: () -> Unit,
    onPagarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val transition = updateTransition(isMenuOpen, label = "fabMenuTransition")

    // Animaciones para los botones
    val button1Position by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "button1"
    ) { if (it) 0.dp else (-56).dp }

    val button2Position by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "button2"
    ) { if (it) 0.dp else (-112).dp }

    val button3Position by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "button3"
    ) { if (it) 0.dp else (-168).dp }

    val button4Position by transition.animateDp(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "button4"
    ) { if (it) 0.dp else (-224).dp }

    val rotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "rotation"
    ) { if (it) 45f else 0f }

    Box(
        modifier = modifier
    ) {
        // Botón Editar
        ExtendedFloatingActionButton(
            onClick = {
                isMenuOpen = false
                onEditarClick()
            },
            modifier = Modifier
                .offset(y = button1Position)
                .alpha(if (isMenuOpen) 1f else 0f),
            containerColor = GymMediumBlue,
            contentColor = GymWhite,
            text = { Text("Editar") },
            icon = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar"
                )
            }
        )

        // Botón Asignar Membresía
        ExtendedFloatingActionButton(
            onClick = {
                isMenuOpen = false
                onAsignarMembresiaClick()
            },
            modifier = Modifier
                .offset(y = button2Position)
                .alpha(if (isMenuOpen) 1f else 0f),
            containerColor = GymMediumBlue,
            contentColor = GymWhite,
            text = { Text("Membresía") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendario),
                    contentDescription = "Asignar Membresía"
                )
            }
        )

        // Botón Pagar
        ExtendedFloatingActionButton(
            onClick = {
                isMenuOpen = false
                onPagarClick()
            },
            modifier = Modifier
                .offset(y = button3Position)
                .alpha(if (isMenuOpen) 1f else 0f),
            containerColor = Color(0xFF4AC250),
            contentColor = GymWhite,
            text = { Text("Pagar") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payments),
                    contentDescription = "Pagar"
                )
            }
        )

        // Botón Eliminar
        ExtendedFloatingActionButton(
            onClick = {
                isMenuOpen = false
                onEliminarClick()
            },
            modifier = Modifier
                .offset(y = button4Position)
                .alpha(if (isMenuOpen) 1f else 0f),
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = GymWhite,
            text = { Text("Eliminar") },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar"
                )
            }
        )

        // Botón principal
        FloatingActionButton(
            onClick = { isMenuOpen = !isMenuOpen },
            containerColor = GymBrightRed,
            contentColor = GymWhite
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Más opciones",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

