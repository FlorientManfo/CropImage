package net.tipam2022.cropimage

import android.R.attr.bitmap
import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.canhub.cropper.CropImageView
import java.io.ByteArrayOutputStream


@Composable
fun EditView(navController: NavController, uri: String?){

    val context: Context = LocalContext.current
    var cropImageView: CropImageView? = null
    var photoUri = uri?.toUri()
    println("-------------------->$photoUri")
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.Black)){
        Row(horizontalArrangement = Arrangement.SpaceBetween
            , modifier = Modifier
                .background(colorResource(id = R.color.gray))
                .fillMaxWidth()){

            TextButton(onClick = { navController.navigate("CameraPreView") }) {
                Text(text = "Retake",
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Right,
                        fontSize = 20.sp)
                )
            }
            TextButton(onClick = { navController.navigate("WelCome") }) {
                Text(text = "Close",
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Right,
                        fontSize = 20.sp)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.Center
            , modifier = Modifier.fillMaxWidth()){
            AndroidView(modifier = Modifier.layoutId("cropArea"),factory = {
                    ctx -> cropImageView =  CropImageView(ctx)
                cropImageView!!.apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    setImageUriAsync(photoUri)
                    cropShape = CropImageView.CropShape.OVAL
                    setFixedAspectRatio(true)
                }
            }){cropImageView = it
            }
        }
        Row{
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 15.dp, 20.dp, 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "Pinch and drag to adjust your photo",
                    style = TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp)
                )
                MyButton("Photo library", Color.Black, 17, Color.White){
                    try{
                        save(context = context, cropImageView!!.croppedImage)
                        navController.navigate("Home")
                    }catch (e: Exception){
                        println("------------>${e.message}")
                    }
                }
            }
        }
    }
}

private fun save(context: Context, bitmap:Bitmap?){
    val sharedPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()

    val imageString = bitmap?.toBase64()
    editor.putString("currentBadge", "$imageString")
    editor.commit()
}