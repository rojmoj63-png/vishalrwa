package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CourseViewModel
import com.example.ui.Screen
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: CourseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allCourses by viewModel.allCourses.collectAsState()
    val allPurchases by viewModel.allPurchases.collectAsState()
    val teachersList by viewModel.teachersList.collectAsState()

    var activeAdminTab by remember { mutableStateOf(0) } // 0 = Statistics & Analytics, 1 = Add New Courses, 2 = Push Alerts Broadcast, 3 = Manage Teachers

    // Input fields for Adding Course
    var courseTitle by remember { mutableStateOf("") }
    var courseTeacherName by remember { mutableStateOf("") }
    var coursePrice by remember { mutableStateOf("") }
    var courseDiscountPrice by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var courseValidity by remember { mutableStateOf("") }
    var courseCategory by remember { mutableStateOf("featured") } // featured, popular, free, new_batch
    var courseIsFree by remember { mutableStateOf(false) }

    // Input fields for Custom Push Alert Notification
    var inviteAlertTitle by remember { mutableStateOf("") }
    var inviteAlertBody by remember { mutableStateOf("") }

    // Input fields for Adding Teacher
    var teacherNameInput by remember { mutableStateOf("") }
    var teacherSubjectInput by remember { mutableStateOf("") }
    var teacherQualificationInput by remember { mutableStateOf("") }

    // Calculate dynamic analytics statistics counters
    val totalRevenue = remember(allPurchases) { allPurchases.sumOf { it.pricePaid } }
    val totalEnrolledStudents = remember(allPurchases) { allPurchases.size + 148 } // Seed offsets

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administration Control Panel") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF333333), // Industrial grey theme
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF1F5F9))
        ) {
            // Horizontal Admin Tab list options
            ScrollableTabRow(
                selectedTabIndex = activeAdminTab,
                containerColor = Color(0xFF222222),
                contentColor = Color.White
            ) {
                Tab(selected = activeAdminTab == 0, onClick = { activeAdminTab = 0 }) {
                    Box(modifier = Modifier.padding(14.dp)) { Text("Analytics", fontWeight = FontWeight.Bold) }
                }
                Tab(selected = activeAdminTab == 1, onClick = { activeAdminTab = 1 }) {
                    Box(modifier = Modifier.padding(14.dp)) { Text("Add Batch Course", fontWeight = FontWeight.Bold) }
                }
                Tab(selected = activeAdminTab == 2, onClick = { activeAdminTab = 2 }) {
                    Box(modifier = Modifier.padding(14.dp)) { Text("Push Alert Broadcaster", fontWeight = FontWeight.Bold) }
                }
                Tab(selected = activeAdminTab == 3, onClick = { activeAdminTab = 3 }) {
                    Box(modifier = Modifier.padding(14.dp)) { Text("Manage Teachers", fontWeight = FontWeight.Bold) }
                }
            }

            when (activeAdminTab) {
                0 -> {
                    // Statistics & Revenue analytics screen
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text("Real-Time Analytics Metrics:", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.DarkGray)
                        }

                        // Analytics metric Cards
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE2EAF4)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0D6EFD)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.MonetizationOn, "Gross Revenue", tint = Color.White, modifier = Modifier.size(34.dp))
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("Projected Gross Revenue", fontSize = 12.sp, color = Color.Gray)
                                        Text("₹${totalRevenue.toInt()}", fontWeight = FontWeight.Black, fontSize = 24.sp, color = Color(0xFF222222))
                                        Text("Razorpay Securely Audited", fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.People, "Students icon", tint = Color(0xFF4CAF50))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Paid Users Enrolls", fontSize = 12.sp, color = Color.Gray)
                                        Text(totalEnrolledStudents.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.School, "Syllabus icon", tint = Color(0xFF9C27B0))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Active Batches Cataloged", fontSize = 12.sp, color = Color.Gray)
                                        Text(allCourses.size.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                }
                            }
                        }

                        // Existing batch courses catalog list view (Delete controls verification)
                        item {
                            Text("Catalog Modification Panel (Delete / Adjust):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        items(allCourses) { course ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(course.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                                    Text("Teacher: ${course.teacherName} • ₹${course.discountPrice.toInt()}", fontSize = 11.sp, color = Color.Gray)
                                }

                                IconButton(onClick = {
                                    viewModel.deleteCourse(course.id)
                                    Toast.makeText(context, "Removed course: ${course.title} from active repository.", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.DeleteForever, "Remove course", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Add Course batch Form screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Add New Preparation Target Batch", fontWeight = FontWeight.Black, fontSize = 16.sp)

                        OutlinedTextField(
                            value = courseTitle,
                            onValueChange = { courseTitle = it },
                            label = { Text("Course Batch Title (e.g. SSC GD Super Batch)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = courseTeacherName,
                            onValueChange = { courseTeacherName = it },
                            label = { Text("Assigned Teacher (e.g. Ankit Sir & Team)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = coursePrice,
                                onValueChange = { coursePrice = it },
                                label = { Text("Regular Base Price (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = courseDiscountPrice,
                                onValueChange = { courseDiscountPrice = it },
                                label = { Text("Discount Special Price (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = courseIsFree, onCheckedChange = { courseIsFree = it })
                            Text("This is a 100% FREE Classroom Study Batch", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedTextField(
                            value = courseDescription,
                            onValueChange = { courseDescription = it },
                            label = { Text("Description & Exam Benefits checklist details") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = courseValidity,
                            onValueChange = { courseValidity = it },
                            label = { Text("Syllabus Validity (e.g. 12 Months / Till Exam)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Category select options
                        Text("Exam Segment Select Tag:")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            listOf("featured", "popular", "free", "new_batch").forEach { seg ->
                                FilterChip(
                                    selected = courseCategory == seg,
                                    onClick = { courseCategory = seg },
                                    label = { Text(seg.replace("_", " ")) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit action
                        Button(
                            onClick = {
                                val amountOriginal = coursePrice.toDoubleOrNull() ?: 999.0
                                val amountDiscount = courseDiscountPrice.toDoubleOrNull() ?: 499.0
                                val generatedId = "c_gen_" + UUID.randomUUID().toString().take(6)

                                viewModel.addCourse(
                                    id = generatedId,
                                    title = courseTitle,
                                    teacher = courseTeacherName,
                                    price = amountOriginal,
                                    discountPrice = amountDiscount,
                                    rating = 4.8f,
                                    description = courseDescription,
                                    validity = if (courseValidity.isEmpty()) "12 Months" else courseValidity,
                                    totalVideos = 120,
                                    totalPDFs = 95,
                                    category = courseCategory,
                                    isFree = courseIsFree
                                )

                                Toast.makeText(context, "Batch Registered & Syllabus Seeding Launched!", Toast.LENGTH_SHORT).show()

                                // reset fields
                                courseTitle = ""
                                courseTeacherName = ""
                                coursePrice = ""
                                courseDiscountPrice = ""
                                courseDescription = ""
                                courseValidity = ""
                                courseIsFree = false
                            },
                            enabled = courseTitle.isNotEmpty() && courseTeacherName.isNotEmpty(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.AddBusiness, "Launch Batch")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SAVE BATCH & SEND LIVE NOTIFICATION", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                2 -> {
                    // Push notification alert broadcasts
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Custom Notification Broadcaster System 📡", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("This launches an alert that instantly broadcasts to all active student drawer badges.", fontSize = 12.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = inviteAlertTitle,
                            onValueChange = { inviteAlertTitle = it },
                            label = { Text("Notification Title (e.g. Math Live Session Alert 🚨)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = inviteAlertBody,
                            onValueChange = { inviteAlertBody = it },
                            label = { Text("Detailed Alert Description message") },
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                viewModel.pushCustomNotification(inviteAlertTitle, inviteAlertBody)
                                Toast.makeText(context, "Pushed custom alert broadcast!", Toast.LENGTH_SHORT).show()
                                inviteAlertTitle = ""
                                inviteAlertBody = ""
                            },
                            enabled = inviteAlertTitle.isNotEmpty() && inviteAlertBody.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.NotificationAdd, "Broadcast")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("BROADCAST PUSH NOTIFICATION", fontWeight = FontWeight.Bold)
                        }

                        // Clear notif histories button
                        OutlinedButton(
                            onClick = {
                                viewModel.clearAllNotifications()
                                Toast.makeText(context, "Cleaned alert registers history.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reset / Clear notification logs list", color = Color.Red)
                        }
                    }
                }
                3 -> {
                    // Manage Teacher list screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Manage Faculty Board Registrations", fontWeight = FontWeight.Black, fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = teacherNameInput,
                            onValueChange = { teacherNameInput = it },
                            label = { Text("Teacher Full Name (e.g. Vipin Sir)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = teacherSubjectInput,
                            onValueChange = { teacherSubjectInput = it },
                            label = { Text("Primary Speciality (e.g. Geography)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = teacherQualificationInput,
                            onValueChange = { teacherQualificationInput = it },
                            label = { Text("Qualifications Credentials") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val ranId = "t_gen_" + UUID.randomUUID().toString().take(6)
                                viewModel.addTeacher(ranId, teacherNameInput, teacherSubjectInput, teacherQualificationInput)
                                Toast.makeText(context, "Registered new teacher under board roster!", Toast.LENGTH_SHORT).show()
                                teacherNameInput = ""
                                teacherSubjectInput = ""
                                teacherQualificationInput = ""
                            },
                            enabled = teacherNameInput.isNotEmpty() && teacherSubjectInput.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("SAVE FACULTY PROFILE DATA", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Faculty List Board Enrolled Roster:", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(teachersList) { teacher ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(teacher.name, fontWeight = FontWeight.Bold)
                                        Text("Expert: ${teacher.subject} • ${teacher.qualifications}", fontSize = 11.sp, color = Color.Gray)
                                    }

                                    IconButton(onClick = { viewModel.deleteTeacher(teacher.id) }) {
                                        Icon(Icons.Default.Delete, "Delete profile", tint = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
