package dev.mbo.androidcamera.ui.viewmodels

import androidx.camera.core.CameraSelector
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartViewModelTest {

    private lateinit var viewModel: StartViewModel

    @Before
    fun setUp() {
        viewModel = StartViewModel()
    }

    @Test
    fun defaultLensFacingIsBack() {
        assertEquals(CameraSelector.LENS_FACING_BACK, viewModel.selectedLensFacing)
    }

    @Test
    fun selectFrontCamera() {
        viewModel.onCameraLensFacingChanged(useFrontCamera = true)
        assertEquals(CameraSelector.LENS_FACING_FRONT, viewModel.selectedLensFacing)
    }

    @Test
    fun selectBackCamera() {
        viewModel.onCameraLensFacingChanged(useFrontCamera = false)
        assertEquals(CameraSelector.LENS_FACING_BACK, viewModel.selectedLensFacing)
    }

    @Test
    fun switchFromFrontToBack() {
        viewModel.onCameraLensFacingChanged(useFrontCamera = true)
        assertEquals(CameraSelector.LENS_FACING_FRONT, viewModel.selectedLensFacing)

        viewModel.onCameraLensFacingChanged(useFrontCamera = false)
        assertEquals(CameraSelector.LENS_FACING_BACK, viewModel.selectedLensFacing)
    }

    @Test
    fun doubleSelectRemainsStable() {
        viewModel.onCameraLensFacingChanged(useFrontCamera = true)
        viewModel.onCameraLensFacingChanged(useFrontCamera = true)
        assertEquals(CameraSelector.LENS_FACING_FRONT, viewModel.selectedLensFacing)
    }
}
