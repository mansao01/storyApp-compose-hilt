package com.mansao.mystoryappcompose.ui.component

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable

@Composable
fun MToast( context: Context, message:String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}