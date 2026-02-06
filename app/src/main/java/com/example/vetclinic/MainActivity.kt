package com.example.vetclinic

import android.R.attr.onClick
import android.R.attr.text
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.vetclinic.ui.theme.BeigeText
import com.example.vetclinic.ui.theme.BgBlack
import com.example.vetclinic.ui.theme.VetclinicTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key.Companion.W
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun OpenScreen(navController: NavController) {
    Box(
        Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(25.dp))

            Image(
                painter = painterResource(id = R.drawable.bord),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(240.dp, 480.dp)
                    .clip(RoundedCornerShape(50))
            )

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Здоровье \nВашего Питомца\nВ Наших Руках",
                color = BeigeText,
                fontFamily = FontFamily.Default,
                fontSize = 39.sp,
                lineHeight = 50.sp,
                letterSpacing = 0.7.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
        Button(
            onClick = { navController.navigate("add_pet") },
            colors = ButtonDefaults.buttonColors(Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "\uD83D\uDC3E Добавить питомца  ",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}




@Composable
fun AddPet(navController: NavController, petId: Int? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val dao = db.petDao()

    var selectedType by remember { mutableStateOf("dog") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoPath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(petId) {
        if (petId != null) {
            val pet = withContext(Dispatchers.IO) { dao.getPetById(petId) }
            if (pet != null) {
                name = pet.name
                selectedType = pet.selectedType
                age = pet.age.toString()
                currentPhotoPath = pet.photoLocation
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) currentPhotoPath = null
    }

    Box(
        Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = "Расскажите\nО Своем Питомце",
                color = BeigeText,
                fontFamily = FontFamily.Default,
                fontSize = 38.sp,
                lineHeight = 50.sp,
                letterSpacing = 0.7.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF9D7054),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Фото питомца",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!currentPhotoPath.isNullOrEmpty()) {
                    AsyncImage(
                        model = currentPhotoPath,
                        contentDescription = "Фото питомца",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = null,
                            tint = BeigeText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Добавить фото",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Кто у вас?",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(15.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedType = "dog" }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                if (selectedType == "dog") Color(0xFF9D7054) else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (selectedType == "dog") Color(0xFF9D7054) else Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐶", fontSize = 32.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Собака",
                        color = if (selectedType == "dog") BeigeText else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = if (selectedType == "dog") FontWeight.Bold else FontWeight.Normal
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedType = "cat" }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                if (selectedType == "cat") Color(0xFF9D7054) else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (selectedType == "cat") Color(0xFF9D7054) else Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐱", fontSize = 32.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Кошка",
                        color = if (selectedType == "cat") BeigeText else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = if (selectedType == "cat") FontWeight.Bold else FontWeight.Normal
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { selectedType = "rodent" }
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                if (selectedType == "rodent") Color(0xFF9D7054) else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (selectedType == "rodent") Color(0xFF9D7054) else Color.Gray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐹", fontSize = 32.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Грызун",
                        color = if (selectedType == "rodent") BeigeText else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = if (selectedType == "rodent") FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Кличка",
                    color = BeigeText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, bottom = 8.dp)
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E),
                        focusedIndicatorColor = Color(0xFF9D7054),
                        unfocusedIndicatorColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF9D7054)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Spacer(Modifier.height(15.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Возраст",
                    color = BeigeText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 10.dp, bottom = 8.dp)
                )
                TextField(
                    value = age,
                    onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E),
                        focusedIndicatorColor = Color(0xFF9D7054),
                        unfocusedIndicatorColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF9D7054)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    var finalPhotoPath = currentPhotoPath

                    if (imageUri != null) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(imageUri!!)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            val fileName = "pet_${System.currentTimeMillis()}.jpg"
                            val file = File(context.filesDir, fileName)
                            val outputStream = FileOutputStream(file)
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush()
                            outputStream.close()
                            inputStream?.close()
                            finalPhotoPath = file.absolutePath
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    val pet = Pet(
                        id = petId ?: 0,
                        selectedType = selectedType,
                        name = name,
                        age = age.toIntOrNull() ?: 0,
                        photoLocation = finalPhotoPath
                    )

                    dao.upsertPet(pet)

                    withContext(Dispatchers.Main) {
                        navController.navigate("profile")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "\uD83D\uDC3E Сохранить  ",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Profile(navController: NavController) {
    Box(
        Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {}
}

@Composable
fun Appointments(navController: NavController) {
    Box(
        Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {}
}

@Composable
fun SOS(navController: NavController) {
    Box(
        Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {}
}