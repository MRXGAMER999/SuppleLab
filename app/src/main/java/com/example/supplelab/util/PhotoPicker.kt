package com.example.supplelab.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


class PhotoPicker {
    private var openPhotoPicker by mutableStateOf(false)

    @Composable
    fun InitializePhotoPicker(
        onImageSelect: (Uri?) -> Unit
    ){
        val openPhotoPickerState = remember { openPhotoPicker }
        val pickMedia = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            onImageSelect(uri)
            openPhotoPicker = false
        }
        LaunchedEffect(openPhotoPickerState) {
            if (openPhotoPickerState) {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }

    }

    fun open(){
        openPhotoPicker = true
    }
}