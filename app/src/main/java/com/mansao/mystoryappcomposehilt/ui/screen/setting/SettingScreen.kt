package com.mansao.mystoryappcomposehilt.ui.screen.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingScreen(
    settingViewModel: SettingViewModel = hiltViewModel(),
    onDarkModeChange: (Boolean) -> Unit,
    navigateToHome: () -> Unit

) {
    Scaffold(
        topBar = { SettingScreenTopBar( navigate = navigateToHome) }
    ) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            SettingContent(settingViewModel = settingViewModel, onDarkModeChange = onDarkModeChange)
        }

    }


}

@Composable
fun SettingContent(
    modifier: Modifier = Modifier,
    settingViewModel: SettingViewModel,
    onDarkModeChange: (Boolean) -> Unit
) {
    val uiState by settingViewModel.uiState.collectAsState()


    val icon: (@Composable () -> Unit) = {
        Icon(
            imageVector = uiState.icon,
            contentDescription = null,
            modifier = Modifier.size(SwitchDefaults.IconSize)
        )
    }
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = uiState.title, modifier = Modifier.padding(top = 4.dp), fontSize = 20.sp)
        Switch(
            checked = uiState.isDarkMode,
            onCheckedChange = { isChecked ->
                settingViewModel.selectedTheme(isChecked)
                onDarkModeChange(isChecked)
            },
            thumbContent = icon
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenTopBar(
    navigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = "Setting") },
        navigationIcon = {
            IconButton(onClick = { navigate() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }

        },
        modifier = modifier
    )

}