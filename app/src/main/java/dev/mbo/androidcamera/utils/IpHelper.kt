package dev.mbo.androidcamera.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address
import java.net.InetAddress

object IpHelper {

    fun getWifiIpAddress(context: Context): InetAddress? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            val linkProperties = connectivityManager.getLinkProperties(network)
            for (linkAddress in linkProperties?.linkAddresses ?: emptyList()) {
                val inetAddress = linkAddress.address
                if (inetAddress is InetAddress && !inetAddress.isLoopbackAddress && inetAddress.isInet4Address) {
                    return inetAddress
                }
            }
        }
        return null
    }

    private val InetAddress.isInet4Address: Boolean
        get() = this is Inet4Address

}