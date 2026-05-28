package dev.mbo.androidcamera.ui.screens

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.mbo.androidcamera.R
import dev.mbo.androidcamera.utils.CameraSizeUtil.BackCameraInfo
import dev.mbo.androidcamera.ui.viewmodels.StartViewModel

@Composable
fun StartScreen(viewModel: StartViewModel, onStartCamera: () -> Unit) {
    val useFrontCamera = viewModel.selectedLensFacing == CameraSelector.LENS_FACING_FRONT
    val showBackCameraPicker = !useFrontCamera && viewModel.availableBackCameras.size > 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = stringResource(R.string.start_logo_description),
            modifier = Modifier
                .size(300.dp)
                .padding(top = 30.dp),
            alignment = Alignment.TopCenter
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 30.dp),
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

            if (showBackCameraPicker) {
                Spacer(modifier = Modifier.height(16.dp))
                BackCameraDropdown(
                    cameras = viewModel.availableBackCameras,
                    selectedKey = viewModel.selectedBackCamera?.key,
                    onSelected = { viewModel.onBackCameraSelected(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartCamera,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(R.string.start_camera_button))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.start_useful_links),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val linkStyle = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
            val privacyUrl = stringResource(R.string.start_privacy_policy_url)
            val coffeeUrl = stringResource(R.string.start_coffee_url)
            val annotatedText = buildAnnotatedString {
                withLink(LinkAnnotation.Url(privacyUrl)) {
                    withStyle(linkStyle) {
                        append(stringResource(R.string.start_privacy_policy))
                    }
                }
                append("\n\n")
                withLink(LinkAnnotation.Url(coffeeUrl)) {
                    withStyle(linkStyle) {
                        append(stringResource(R.string.start_coffee))
                    }
                }
            }

            Text(text = annotatedText)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackCameraDropdown(
    cameras: List<BackCameraInfo>,
    selectedKey: String?,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCamera = cameras.firstOrNull { it.key == selectedKey } ?: cameras.first()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.width(220.dp)
    ) {
        OutlinedTextField(
            value = backCameraLabel(selectedCamera),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.start_back_camera_picker_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cameras.forEach { camera ->
                DropdownMenuItem(
                    text = { Text(backCameraLabel(camera)) },
                    onClick = {
                        onSelected(camera.key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun backCameraLabel(camera: BackCameraInfo): String {
    val focal = camera.focalLengthMm
    return if (focal != null) {
        stringResource(R.string.start_back_camera_focal, focal)
    } else {
        stringResource(R.string.start_back_camera_unknown_focal, camera.physicalCameraId ?: camera.logicalCameraId)
    }
}
