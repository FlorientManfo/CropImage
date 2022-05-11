package net.tipam2022.cropimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun HomeView(navController: NavController){

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("profile", Context.MODE_PRIVATE)
    var croppedBitmap = sharedPref.getString("currentBadge", null)?.toBitmap()
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.Black)){
        Row(horizontalArrangement = Arrangement.Center
            , modifier = Modifier.fillMaxWidth()){
            CircularImage(image = croppedBitmap!!)
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
                }
            }
        }
    }
}

@Composable
fun CircularImage(image: Bitmap){
    Column(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier
            .size(300.dp)
            .testTag(tag = "circle"),
        shape = CircleShape,
        elevation = 12.dp) {
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Saved Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}
private fun save(context: Context, bitmap: Bitmap?){
    val sharedPref = context.getSharedPreferences("name", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()

    val imageString = bitmap?.toBase64()
    editor.putString("currentBadge", "$imageString")
    editor.commit()
}