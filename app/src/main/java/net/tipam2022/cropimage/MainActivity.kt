package net.tipam2022.cropimage
import CameraView
import activity
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import net.tipam2022.cropimage.ui.theme.CropImageTheme
import java.io.ByteArrayOutputStream
import java.lang.Exception


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContent {
            CropImageTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {Greeting()
                }
            }
        }
    }
}


@Composable
fun Greeting() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "WelCome"){
        composable("WelCome"){ WelCome(navController = navController)}
        composable("CameraPreview"){ CameraPreview(navController = navController)}
        composable("EditPhoto?uri={uri}", arguments = listOf(navArgument("uri"){type = NavType.StringType}))
        {backStackEntry ->
            EditPhoto(navController = navController,
            backStackEntry.arguments?.getString("uri")?:null)}
        composable("Home?savedImage={savedImage}"){Home(navController = navController)}
    }
}

@Composable
fun WelCome(navController: NavController){
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.Black)){
        Row{
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,){
                Text(
                    text = "Add your badge photo",
                    modifier=Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp),
                    style= TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "We will need your permission to access to your camera or photo album to add a badge photo",
                    modifier=Modifier.padding(0.dp, 15.dp, 0.dp, 0.dp),
                    style= TextStyle(
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp)
                )
            }
        }
        Row{
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp, 20.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally){
                MyButton("Add your photo", Color.Black, 17, Color.White){
                    navController.navigate("CameraPreView")
                }
                TextButton(onClick = { /*TODO*/ },
                    modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 0.dp),) {
                    Text(text = "Later",
                        style = TextStyle(
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 17.sp))
                }
            }
        }
    }
}

@Composable
fun CameraPreview(navController: NavController){

    CameraView(navController= navController,onImageCaptured = { uri, fromGallery ->
        Log.d(TAG, "Image Uri Captured from Camera View")
        val mainHandler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
             run(){
                try{
                    navController.navigate("EditPhoto?uri=$uri")
                }catch (e: Exception){
                    println("--------------->${e.message}")
                }
            }
        }
        mainHandler.post(runnable)
    }, onError = { imageCaptureException ->
        println("${imageCaptureException.message}")
    })
}


@Composable
fun EditPhoto(navController: NavController, uri: String?){
    EditView(navController, uri)
}

@Composable
fun Home(navController: NavController){
    HomeView(navController = navController)
}
@Composable
fun MyButton(text: String, textColor: Color, textSize: Int, backgroundColor: Color,callBack: ()->Unit){
    Button(onClick = callBack,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)) {
        Text(
            text = text,
            style = TextStyle(
                color = textColor,
                textAlign = TextAlign.Center,
                fontSize = textSize.sp
            )
        )
    }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CropImageTheme {
        Greeting()
    }
}

fun String?.toBitmap(): Bitmap?{
    var imageBytes = Base64.decode(this, 0)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun Bitmap.toBase64(): String? {
    if(this!=null){
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,60,this)
            var byteArray = this.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }
    return null
}
