package dev.mbo.androidcamera.ui.screens

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mbo.androidcamera.ui.viewmodels.HomeViewModel
import dev.mbo.androidcamera.utils.IpHelper

@Composable
fun HomeScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val streaming = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { previewView }
    ) {
        viewModel.initializeCamera(streaming.value, previewView, context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = IpHelper.getWifiIpAddress(context)?.hostAddress ?: "not connected",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            color = Color.White
        )
        if (streaming.value) {
            Text(
                text = "streaming...",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = Color.Red
            )
        } else {
            Text(
                text = "ready",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = Color.White
            )
        }

        IconButton(
            onClick = {
                viewModel.toggleStream(coroutineScope, context, streaming)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            if (streaming.value) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Stop Stream",
                    tint = Color.Red,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Start Stream",
                    tint = Color.White,
                )
            }
        }
    }
}

