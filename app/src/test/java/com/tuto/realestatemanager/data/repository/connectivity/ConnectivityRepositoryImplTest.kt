package com.tuto.realestatemanager.data.repository.connectivity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.N], manifest = Config.NONE)
class ConnectivityRepositoryImplTest {

    @Test
    fun isInternetAvailable_whenNetworkHasInternetAndIsValidated_returnsTrue() = runTest {
        val context = mockk<Application>()
        val connectivityManager = mockk<ConnectivityManager>(relaxed = true)
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager

        every {
            connectivityManager.activeNetwork
        } returns network

        every {
            connectivityManager.getNetworkCapabilities(network)
        } returns capabilities

        every {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true

        every {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } returns true

        every {
            connectivityManager.registerDefaultNetworkCallback(any())
        } just Runs

        every {
            connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
        } just Runs

        val repository = ConnectivityRepositoryImpl(context)

        val result = repository.isInternetAvailable().first()

        assertTrue(result)
    }

    @Test
    fun isInternetAvailable_whenNetworkHasInternetButIsNotValidated_returnsFalse() = runTest {
        val context = mockk<Application>()
        val connectivityManager = mockk<ConnectivityManager>(relaxed = true)
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager

        every {
            connectivityManager.activeNetwork
        } returns network

        every {
            connectivityManager.getNetworkCapabilities(network)
        } returns capabilities

        every {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true

        every {
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } returns false

        every {
            connectivityManager.registerDefaultNetworkCallback(any())
        } just Runs

        every {
            connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
        } just Runs

        val repository = ConnectivityRepositoryImpl(context)

        val result = repository.isInternetAvailable().first()

        assertFalse(result)
    }

    @Test
    fun isInternetAvailable_whenThereIsNoActiveNetwork_returnsFalse() = runTest {
        val context = mockk<Application>()
        val connectivityManager = mockk<ConnectivityManager>(relaxed = true)

        every {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
        } returns connectivityManager

        every {
            connectivityManager.activeNetwork
        } returns null

        every {
            connectivityManager.getNetworkCapabilities(null)
        } returns null

        every {
            connectivityManager.registerDefaultNetworkCallback(any())
        } just Runs

        every {
            connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>())
        } just Runs

        val repository = ConnectivityRepositoryImpl(context)

        val result = repository.isInternetAvailable().first()

        assertFalse(result)
    }
}