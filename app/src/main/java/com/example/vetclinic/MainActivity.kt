package com.example.vetclinic

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.vetclinic.ui.theme.BeigeText
import com.example.vetclinic.ui.theme.BgBlack
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val dao = db.petDao()

    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }

    LaunchedEffect(Unit) {
        dao.getAllPets().collect { list ->
            pets = list
        }
    }

    Box(
        modifier = Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, top = 40.dp)
        ) {
            Text(
                text = "Мои питомцы",
                color = BeigeText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (pets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Список пуст.\nДобавьте своего первого питомца!",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pets) { pet ->
                        PetItem(
                            pet = pet,
                            onClick = {
                                navController.navigate("add_pet/${pet.id}")
                            },
                            onDelete = {
                                scope.launch(Dispatchers.IO) {
                                    dao.deletePet(pet)
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                navController.navigate("add_pet")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                .align(Alignment.BottomCenter)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Добавить питомца",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getPetTypeInRu(type: String): String {
    return when (type) {
        "dog" -> "Собака"
        "cat" -> "Кошка"
        "rodent" -> "Грызун"
        else -> type
    }
}
@Composable
fun PetItem(pet: Pet, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() }
            .padding(end = 15.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pet.photoLocation != null) {
                AsyncImage(
                    model = pet.photoLocation,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🐾", fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pet.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${getPetTypeInRu(pet.selectedType)}, ${pet.age} лет",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = Color(0xFFEF5350),
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onDelete()
                    }
                    .padding(5.dp)
            )
        }
    }
}


@Composable
fun Appointments(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val appointmentDao = db.appointmentDao()
    val petDao = db.petDao()

    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }


    LaunchedEffect(Unit) {
        launch {
            petDao.getAllPets().collect { list -> pets = list }
        }
        launch {
            appointmentDao.getAllAppointments().collect { list -> appointments = list }
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 100.dp)
        ) {
            Text(
                text = "Записи на прием",
                color = BeigeText,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp, bottom = 30.dp)
            )

            if (appointments.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "У вас пока нет записей",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    items(appointments) { appointment ->
                        val pet = pets.find { it.id == appointment.petId }
                        AppointmentItem(
                            petName = pet?.name ?: "Неизвестный питомец",
                            procedure = appointment.procedure,
                            doctor = appointment.doctor,
                            date = appointment.date,
                            onDelete = {
                                scope.launch(Dispatchers.IO) {
                                    appointmentDao.deleteAppointment(appointment)
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                if (pets.isNotEmpty()) {
                    showAddDialog = true
                }
            },
            enabled = pets.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (pets.isNotEmpty()) Color.White else Color(0xFF333333)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = if (pets.isEmpty()) "Сначала добавьте питомца" else "+ Записаться на прием",
                color = if (pets.isNotEmpty()) Color.Black else Color.Gray,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showAddDialog) {
        AddAppointmentDialog(
            pets = pets,
            onDismiss = { showAddDialog = false },
            onConfirm = { appointment ->
                scope.launch(Dispatchers.IO) {
                    appointmentDao.upsertAppointment(appointment)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AppointmentItem(petName: String, procedure: String, doctor: String, date: Long, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("ru", "RU")) }
    val dateString = dateFormat.format(Date(date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = petName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = procedure, color = BeigeText, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Врач: $doctor", color = Color.Gray, fontSize = 14.sp)
                Text(text = dateString, color = Color(0xFF9D7054), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color(0xFFEF5350)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentDialog(pets: List<Pet>, onDismiss: () -> Unit, onConfirm: (Appointment) -> Unit) {
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var expandedPet by remember { mutableStateOf(false) }

    var selectedDoctor by remember { mutableStateOf("") }
    var expandedDoctor by remember { mutableStateOf(false) }
    val doctors = listOf("Иванов А.А.", "Петрова Б.Б.", "Сидоров В.В.", "Кузнецова Е.Е.")

    var selectedProcedure by remember { mutableStateOf("") }
    var expandedProcedure by remember { mutableStateOf(false) }
    val procedures = listOf("Первичный осмотр", "Вакцинация", "Чистка зубов", "Стрижка", "УЗИ")

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF2C2C2C)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Новая запись",
                    color = BeigeText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text(text = "Питомец", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp, bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedPet,
                    onExpandedChange = { expandedPet = it }
                ) {
                    OutlinedTextField(
                        value = selectedPet?.name ?: "Выберите питомца",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = BeigeText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF9D7054),
                            unfocusedIndicatorColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF9D7054)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPet,
                        onDismissRequest = { expandedPet = false },
                        containerColor = Color(0xFF3C3C3C)
                    ) {
                        pets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(text = pet.name, color = Color.White) },
                                onClick = {
                                    selectedPet = pet
                                    expandedPet = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))


                Text(text = "Процедура", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp, bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedProcedure,
                    onExpandedChange = { expandedProcedure = it }
                ) {
                    OutlinedTextField(
                        value = selectedProcedure.ifEmpty { "Выберите процедуру" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = BeigeText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF9D7054),
                            unfocusedIndicatorColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF9D7054)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProcedure,
                        onDismissRequest = { expandedProcedure = false },
                        containerColor = Color(0xFF3C3C3C)
                    ) {
                        procedures.forEach { procedure ->
                            DropdownMenuItem(
                                text = { Text(text = procedure, color = Color.White) },
                                onClick = {
                                    selectedProcedure = procedure
                                    expandedProcedure = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))


                Text(text = "Врач", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp, bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedDoctor,
                    onExpandedChange = { expandedDoctor = it }
                ) {
                    OutlinedTextField(
                        value = selectedDoctor.ifEmpty { "Выберите врача" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = BeigeText) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF9D7054),
                            unfocusedIndicatorColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF9D7054)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDoctor,
                        onDismissRequest = { expandedDoctor = false },
                        containerColor = Color(0xFF3C3C3C)
                    ) {
                        doctors.forEach { doctor ->
                            DropdownMenuItem(
                                text = { Text(text = doctor, color = Color.White) },
                                onClick = {
                                    selectedDoctor = doctor
                                    expandedDoctor = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(text = "Дата", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(start = 10.dp, bottom = 8.dp))

                Box(Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = selectedDate?.let {
                            SimpleDateFormat("dd MMM yyyy", java.util.Locale("ru", "RU")).format(Date(it))
                        } ?: "Выберите дату",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        trailingIcon = { Icon(Icons.Default.DateRange, null, tint = BeigeText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.DarkGray,
                            disabledTextColor = Color.White,
                            disabledTrailingIconColor = BeigeText,
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )


                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }


                Spacer(modifier = Modifier.height(30.dp))


                Button(
                    onClick = {
                        if (selectedPet != null && selectedProcedure.isNotEmpty() && selectedDoctor.isNotEmpty() && selectedDate != null) {
                            val finalDate = selectedDate!! + (12 * 60 * 60 * 1000)
                            onConfirm(
                                Appointment(
                                    petId = selectedPet!!.id,
                                    doctor = selectedDoctor,
                                    procedure = selectedProcedure,
                                    date = finalDate
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Сохранить", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                    }
                ) { Text("OK", color = Color(0xFF9D7054)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOS(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val dao = db.petDao()

    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var selectedPet by remember { mutableStateOf<Pet?>(null) }
    var expandedPet by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val sosRed = Color(0xFFE53935)

    LaunchedEffect(Unit) {
        dao.getAllPets().collect { list -> pets = list }
    }

    Box(
        modifier = Modifier
            .background(BgBlack)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = "Экстренная помощь",
                color = BeigeText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Кнопка экстренной помощи вашему питомцу. Напишите что случилось и мы подберем ближайшего ветеринара.",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))

            Text(
                text = "Выберите питомца",
                color = BeigeText,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp, bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedPet,
                onExpandedChange = { expandedPet = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPet?.name ?: "Питомец не выбран",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = BeigeText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF9D7054),
                        unfocusedIndicatorColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF9D7054)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedPet,
                    onDismissRequest = { expandedPet = false },
                    containerColor = Color(0xFF3C3C3C),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (pets.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Нет питомцев", color = Color.Gray) },
                            onClick = { expandedPet = false },
                            enabled = false
                        )
                    } else {
                        pets.forEach { pet ->
                            DropdownMenuItem(
                                text = { Text(text = pet.name, color = Color.White) },
                                onClick = {
                                    selectedPet = pet
                                    expandedPet = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Описание ситуации",
                color = BeigeText,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 10.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Опишите симптомы или ситуацию...", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF9D7054),
                    unfocusedIndicatorColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF9D7054)
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = {
                    if (selectedPet != null && description.isNotEmpty()) {
                        isSearching = true
                        scope.launch {
                            kotlinx.coroutines.delay(3000)
                            withContext(kotlinx.coroutines.Dispatchers.Main) {
                                isSearching = false
                                Toast.makeText(context, "Ветеринар найден!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Выберите питомца и опишите проблему", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isSearching && selectedPet != null && description.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(2.dp, if (selectedPet != null && description.isNotEmpty()) sosRed else Color.DarkGray),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = sosRed,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (selectedPet != null && description.isNotEmpty()) sosRed else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isSearching) "Идет поиск..." else "Искать ветеринара",
                    color = if (selectedPet != null && description.isNotEmpty()) sosRed else Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}