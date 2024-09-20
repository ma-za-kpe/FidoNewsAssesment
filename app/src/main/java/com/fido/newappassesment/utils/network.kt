package com.fido.newappassesment.utils

import com.fido.newappassesment.data.api.ConnectionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val connectionManager: ConnectionManager
) {
    private val _networkStatus = MutableStateFlow(false)
    val networkStatus: StateFlow<Boolean> = _networkStatus.asStateFlow()

    init {
        connectionManager.registerNetworkCallback { isConnected ->
            _networkStatus.value = isConnected
        }
    }
}
