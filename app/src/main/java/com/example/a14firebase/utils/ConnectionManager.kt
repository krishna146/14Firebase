package com.example.a14firebase.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {
    companion object {
        fun checkInternet(context: Context): Boolean {
            //this will give info about current active Network
            val connectionManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork: NetworkInfo? = connectionManager.activeNetworkInfo
            if (activeNetwork?.isConnected != null) {
                return activeNetwork.isConnected
            } else {
                return false
            }


        }
    }
}