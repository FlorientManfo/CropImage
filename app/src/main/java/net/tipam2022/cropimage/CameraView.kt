import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.size
import androidx.navigation.NavController
import net.tipam2022.cropimage.*
import net.tipam2022.cropimage.R

private val PERMISSION_CODE = 1001;
lateinit var activity: Activity
@Composable
fun CameraView(navController: NavController, onImageCaptured: (Uri, Boolean) -> Unit, onError: (ImageCaptureException) -> Unit) {
    Permission()
    val context = LocalContext.current
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageCaptured(uri, true)}
    }

    CameraPreviewView(
        navController,
        imageCapture,
        lensFacing
    ) { cameraUIAction ->
        when (cameraUIAction) {
            is CameraUIAction.OnCameraClick -> {
                imageCapture.takePicture(context, lensFacing, onImageCaptured, onError)
            }
            is CameraUIAction.OnSwitchCameraClick -> {
                lensFacing =
                    if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
            }
            is CameraUIAction.OnGalleryViewClick -> {
                if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                    galleryLauncher.launch("image/*")
                }
            }
        }
    }
}
@SuppressLint("RestrictedApi")
@Composable
fun CameraPreviewView(
    navController: NavController,
    imageCapture: ImageCapture,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    cameraUIAction: (CameraUIAction) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val previewView = remember { PreviewView(context) }
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .background(colorResource(id = R.color.gray))
            .fillMaxWidth()
        ) {

            CameraControl(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_flip_camera_android_24),
                contentDescId = R.string.camera_mode
            ) {
                cameraUIAction(CameraUIAction.OnSwitchCameraClick)
            }
            TextButton(onClick = {navController.navigate("WelCome")},) {
                Text(
                    text = "Close",
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Right,
                        fontSize = 20.sp
                    )
                )
            }
        }
        Box(modifier = Modifier.fillMaxHeight()){

            Column(modifier = Modifier.fillMaxHeight()){
                AndroidView({ previewView }) {
                }
                Column(modifier = Modifier
                    .background(Color.Black)
                    .padding(20.dp, 0.dp, 20.dp, 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MyButton("Take photo", Color.Black, 17, Color.White) {
                        cameraUIAction(CameraUIAction.OnCameraClick)
                    }
                    MyButton("Photo library", Color.White, 17, colorResource(id = R.color.gray)
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if (checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED){
                                //permission denied
                                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                                //show popup to request runtime permission
                                requestPermissions(activity, permissions, PERMISSION_CODE);
                            }
                            else{
                                //permission already granted
                                cameraUIAction(CameraUIAction.OnGalleryViewClick)
                            }
                        }
                        else{
                            //system OS is < Marshmallow
                            cameraUIAction(CameraUIAction.OnGalleryViewClick)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CameraControl(
    imageVector: ImageVector,
    contentDescId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {


    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            contentDescription = stringResource(id = contentDescId),
            modifier = modifier,
            tint = Color.White
        )
    }

}
