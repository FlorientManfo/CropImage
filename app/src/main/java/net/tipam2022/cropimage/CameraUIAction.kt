package net.tipam2022.cropimage

sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}