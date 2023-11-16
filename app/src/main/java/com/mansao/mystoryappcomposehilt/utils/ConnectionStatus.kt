package com.mansao.mystoryappcomposehilt.utils

sealed class ConnectionStatus{
    object  Available: ConnectionStatus()
    object  UnAvailable: ConnectionStatus()
}