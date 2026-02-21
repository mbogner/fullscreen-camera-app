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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.camera.core.CameraSelector
import dev.mbo.androidcamera.R
import dev.mbo.androidcamera.ui.viewmodels.StartViewModel
import androidx.core.net.toUri

@Composable
fun StartScreen(viewModel: StartViewModel) {
    val context = LocalContext.current
    val useFrontCamera = viewModel.selectedLensFacing == CameraSelector.LENS_FACING_FRONT

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "logo",
            modifier = Modifier
                .size(300.dp)
                .padding(0.dp, 30.dp, 0.dp, 0.dp),
            alignment = Alignment.TopCenter
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(0.dp, 30.dp, 0.dp, 0.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(24.dp))

            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = !useFrontCamera,
                    onClick = { viewModel.onCameraLensFacingChanged(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text(text = stringResource(R.string.start_camera_selector_back))
                }
                SegmentedButton(
                    selected = useFrontCamera,
                    onClick = { viewModel.onCameraLensFacingChanged(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text(text = stringResource(R.string.start_camera_selector_front))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.startCameraButtonClicked() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(R.string.start_camera_button))
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 32.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f)) // pull to bottom

            Text(
                text = stringResource(R.string.start_useful_links),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val annotatedText = buildAnnotatedString {
                // First link
                pushStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
                withStyle(style = SpanStyle()) {
                    append(stringResource(R.string.start_privacy_policy))
                    addStringAnnotation(
                        "URL",
                        "https://mbo.dev/policies/android-fullscreen-camera.html",
                        0,
                        13
                    )
                }
                pop()

                append("\n")
                append("\n")

                // Second link
                pushStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline))
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append(stringResource(R.string.start_coffee))
                    addStringAnnotation(
                        "URL",
                        "https://paypal.me/mbogner85",
                        18,
                        33
                    )
                }
                pop()
            }

            ClickableText(text = annotatedText, onClick = { offset ->
                offset.let { clickedOffset ->
                    annotatedText.getStringAnnotations("URL", clickedOffset, clickedOffset)
                        .firstOrNull()?.let { annotation ->
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    annotation.item.toUri()
                                )
                            )
                        }
                }
            })
        }
    }
}
