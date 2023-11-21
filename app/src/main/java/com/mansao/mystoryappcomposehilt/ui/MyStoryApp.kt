package com.mansao.mystoryappcomposehilt.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mansao.mystoryappcompose.data.local.model.LocationModel
import com.mansao.mystoryappcomposehilt.ui.navigation.Screen
import com.mansao.mystoryappcomposehilt.ui.screen.add.AddScreen
import com.mansao.mystoryappcomposehilt.ui.screen.add.AddViewModel
import com.mansao.mystoryappcomposehilt.ui.screen.detail.DetailScreen
import com.mansao.mystoryappcomposehilt.ui.screen.home.HomeScreen
import com.mansao.mystoryappcomposehilt.ui.screen.home.HomeViewModel
import com.mansao.mystoryappcomposehilt.ui.screen.login.LoginScreen
import com.mansao.mystoryappcomposehilt.ui.screen.login.LoginViewModel
import com.mansao.mystoryappcomposehilt.ui.screen.maps.MapScreen
import com.mansao.mystoryappcomposehilt.ui.screen.maps.MapsViewModel
import com.mansao.mystoryappcomposehilt.ui.screen.register.RegisterScreen
import com.mansao.mystoryappcomposehilt.ui.screen.register.RegisterViewModel
import com.mansao.mystoryappcomposehilt.ui.screen.setting.SettingScreen

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStoryApp(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    location: LocationModel,
    locationEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val sharedViewModel: SharedViewModel = viewModel()
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                uiState = loginViewModel.uiState,
                navigateToHome = {
                    navController.navigate(Screen.Home.route)
                    navController.popBackStack()
                },
                navigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                uiState = homeViewModel.uiState,
                scrollBehavior = scrollBehavior,
                navigateToLogin = {
                    navController.navigate(Screen.Login.route)
                    navController.popBackStack()
                },
                navigateToAdd = {
                    navController.navigate(Screen.Add.route)
                },
                sharedViewModel = sharedViewModel,
                navigateToDetail = {
                    navController.navigate(Screen.Detail.route)
                },
                navigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                navigateSetting = {
                    navController.navigate(Screen.Setting.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                uiState = registerViewModel.uiState,
                navigateToLogin = {
                    navController.navigate(Screen.Login.route)
                })
        }

        composable(Screen.Add.route) {
            val addViewModel: AddViewModel = hiltViewModel()
            AddScreen(
                uiState = addViewModel.uiState,
                navigateToHome = {
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                },
                scrollBehavior = scrollBehavior,
                location = location
            )
        }

        composable(Screen.Detail.route) {
            DetailScreen(
                sharedViewModel = sharedViewModel,
                navigateToHome = {
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                }, scrollBehavior = scrollBehavior
            )
        }

        composable(Screen.Map.route) {
            val mapViewModel: MapsViewModel = hiltViewModel()
            MapScreen(
                uiState = mapViewModel.uiState,
                location = location,
                locationEnabled = locationEnabled,
                navigateToHome = {
                    navController.popBackStack()
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        composable(Screen.Setting.route) {
            SettingScreen(onDarkModeChange = onDarkModeChange, navigateToHome = {
                navController.navigate(Screen.Home.route)
            })
        }
    }

}