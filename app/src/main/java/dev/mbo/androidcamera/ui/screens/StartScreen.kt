package dev.mbo.androidcamera.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.mbo.androidcamera.R
import dev.mbo.androidcamera.ui.viewmodels.StartViewModel

@Composable
fun StartScreen(viewModel: StartViewModel) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Image(
            painter = painterResource(id = R.drawable.icon), // Use the image resource
            contentDescription = null, // Provide a content description if necessary
            modifier = Modifier.size(200.dp) // Adjust the size as needed
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .wrapContentHeight(align = Alignment.CenterVertically), // Center vertically
        verticalArrangement = Arrangement.Center, // Center horizontally
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Fullscreen Camera", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.startCameraButtonClicked() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Start Camera")
        }
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Useful Links", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(12.dp))

        val annotatedText = buildAnnotatedString {
            pushStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
            withStyle(style = SpanStyle()) {
                append("Privacy Policy")
                addStringAnnotation(
                    "URL", "https://mbo.dev/assets/policies/android-fullscreen-camera.html", 0, 13
                )
            }
            pop()
        }

        ClickableText(text = annotatedText, onClick = { offset ->
            annotatedText.getStringAnnotations("URL", offset, offset).firstOrNull()!!
                .let { annotation ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item)))
                }
        })

        Spacer(modifier = Modifier.height(32.dp))
    }
}

