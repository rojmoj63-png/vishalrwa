package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChapterEntity
import com.example.data.SubjectEntity
import com.example.ui.CourseViewModel
import com.example.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsScreen(
    courseId: String,
    viewModel: CourseViewModel,
    onBack: () -> Unit,
    onNavigateToSubjectStudy: (String) -> Unit,
    onNavigateToPayment: (String) -> Unit
) {
    // Force loading course details from state
    LaunchedEffect(courseId) {
        viewModel.loadCourseDetails(courseId)
    }

    val course by viewModel.selectedCourse.collectAsState()
    val isPurchased by viewModel.isCurrentCoursePurchased.collectAsState()
    val activeSubjects by viewModel.activeSubjects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course?.title ?: "Batch Specification") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF4A00E0),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Floating Sticky action bar matching current purchase state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp)
            ) {
                if (isPurchased || course?.isFree == true) {
                    Button(
                        onClick = { onNavigateToSubjectStudy(courseId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.School, "Access Classroom")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enter Classroom (Let's Study)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { onNavigateToPayment(courseId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.LockOpen, "Unlock Course")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buy Now to Unlock (₹${course?.discountPrice?.toInt() ?: 499})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (course == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandPurpleDark)
            }
        } else {
            val validCourse = course!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF9F9FA))
            ) {
                // Banner area with gradient highlights
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF4A00E0), Color(0xFFA259FF))
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "TARGET BATCH • " + if (validCourse.isFree) "FREE" else "Premium Exam Prep",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                validCourse.title,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp
                            )
                            Text(
                                "Enrolled Teacher: ${validCourse.teacherName}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Description card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("About This Course Batch", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandPurpleDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                validCourse.description,
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                lineHeight = 20.sp
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Features list: duration info, certificate, quizzes
                            Text("Product Features Included:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            BulletFeatureItem("Interactive Live Daily video streams")
                            BulletFeatureItem("Recorded lectures access 24x7 with lifetime revision")
                            BulletFeatureItem("Dedicated PDF Class hand-written notes by expert teachers")
                            BulletFeatureItem("Daily homework worksheets + PYQ Practice pdf sheets")
                            BulletFeatureItem("Complete Full length mock test evaluation")
                        }
                    }
                }

                // Batch overview counters card
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatsSmallCard(modifier = Modifier.weight(1f), icon = Icons.Default.Videocam, value = "${validCourse.totalVideos}+ Lectures", label = "High Quality")
                        StatsSmallCard(modifier = Modifier.weight(1f), icon = Icons.Default.PictureAsPdf, value = "${validCourse.totalPDFs}+ Notes", label = "Downloadable")
                        StatsSmallCard(modifier = Modifier.weight(1f), icon = Icons.Default.DateRange, value = validCourse.validity, label = "Course Validity")
                    }
                }

                // Subject curriculum lists index header
                item {
                    Text(
                        "Subjects Covered in Batch (${activeSubjects.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // List preview of subjects in this course
                if (activeSubjects.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                "Subject Syllabus index is being prepared by the expert team.",
                                color = Color.Gray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(activeSubjects) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF0E6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.iconName) {
                                        "hindi" -> Icons.Default.Description
                                        "maths" -> Icons.Default.Calculate
                                        "science" -> Icons.Default.Science
                                        "gk" -> Icons.Default.Public
                                        else -> Icons.Default.School
                                    },
                                    contentDescription = "Subject icon",
                                    tint = BrandPurpleDark
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                item.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            if (!isPurchased && validCourse.isFree.not()) {
                                Icon(Icons.Default.Lock, "Locked syllabus category", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, "Active Subject lessons", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Interactive simulated Telegram join banner
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Send, "Telegram channel icon", tint = Color(0xFF229ED9), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Join Our Doubt Solving Telegram Channel", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1565C0))
                                Text("Connect with 500k+ students and teachers", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsSmallCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = "Stat highlight icon", tint = BrandPurpleDark, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 1)
            Text(label, fontSize = 9.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun BulletFeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", fontWeight = FontWeight.Bold, color = BrandPurpleDark)
        Text(text = text, fontSize = 12.sp, color = Color.DarkGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    courseId: String,
    viewModel: CourseViewModel,
    onBack: () -> Unit,
    onNavigateToChapters: (String) -> Unit
) {
    LaunchedEffect(courseId) {
        viewModel.loadCourseDetails(courseId)
    }

    val course by viewModel.selectedCourse.collectAsState()
    val activeSubjects by viewModel.activeSubjects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course?.title?.take(22) ?: "Curriculum Classrooms") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF4A00E0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9FA))
        ) {
            // Classroom Announcement Board Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8EAF6))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VolumeUp, "Announcement Speaker", tint = Color(0xFF3F51B5))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Click any Subject class below to access daily recorded lectures, upcoming live videos and notes PDFs.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )
                }
            }

            if (activeSubjects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading interactive classrooms...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeSubjects) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToChapters(item.id) },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(PurpleGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item.iconName) {
                                            "hindi" -> Icons.Default.HistoryEdu
                                            "maths" -> Icons.Default.Calculate
                                            "science" -> Icons.Default.Science
                                            "gk" -> Icons.Default.Public
                                            else -> Icons.Default.School
                                        },
                                        contentDescription = "Subject category",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(
                                        item.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        "Click to access chapters, homework & lectures",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(Icons.Default.ArrowForwardIos, "Enter", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterScreen(
    subjectId: String,
    courseId: String,
    viewModel: CourseViewModel,
    onBack: () -> Unit,
    onNavigateToVideo: (String) -> Unit,
    onNavigateToPDF: (String, String) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(subjectId) {
        viewModel.loadSubjectDetails(subjectId)
    }

    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val activeChapters by viewModel.activeChapters.collectAsState()

    var showQuizDialog by remember { mutableStateOf(false) }

    // Toggle Tab choice: Recorded Lectures , Live & Upcoming, Test & Quiz
    var activeChapterTab by remember { mutableIntStateOf(0) } // 0 = Lectures & Notes, 1 = Live & Upcoming, 2 = Mock Tests & Quizzes

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedSubject?.name ?: "Curriculum Classrooms") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Go back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF4A00E0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F5F7))
        ) {
            // Live / Recorded tabs
            TabRow(selectedTabIndex = activeChapterTab) {
                Tab(selected = activeChapterTab == 0, onClick = { activeChapterTab = 0 }) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text("Recorded & Notes", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Tab(selected = activeChapterTab == 1, onClick = { activeChapterTab = 1 }) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Live Schedule", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                Tab(selected = activeChapterTab == 2, onClick = { activeChapterTab = 2 }) {
                    Box(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text("Mock Tests", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            when (activeChapterTab) {
                0 -> {
                    // Recorded Chapters List
                    if (activeChapters.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No chapters uploaded in this segment yet.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(activeChapters) { chapter ->
                                ChapterCardItem(
                                    chapter = chapter,
                                    onWatchClick = { onNavigateToVideo(chapter.id) },
                                    onPdf1Click = { onNavigateToPDF(chapter.id, "1") },
                                    onPdf2Click = { onNavigateToPDF(chapter.id, "2") }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Live schedule screen placeholder
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.LiveTv, "Live Streaming Video Screen Placeholder", tint = Color.Red, modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No Live Stream Session Is Broadcasted Currently 📡", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text(
                            "All scheduled live alerts will appear here. Next Live classes scheduled at 08:30 AM Tomorrow. Stay Tuned!",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                2 -> {
                    // Tests and quiz panel
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Feed, "Mock tests", tint = BrandPurpleDark, modifier = Modifier.size(72.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Daily Live Practice Tests 🏆", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Solve authentic quizzes with exact real-time exam timers to secure maximum ranks on our platform.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))

                        Button(
                            onClick = { showQuizDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Start Practice MCQ Mock Test #01", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showQuizDialog) {
        var selectedProgressQuiz by remember { mutableStateOf(-1) }
        var isSubmittedQuiz by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showQuizDialog = false
                isSubmittedQuiz = false
                selectedProgressQuiz = -1
            },
            title = { Text("Topic-wise Practice MCQ Test") },
            text = {
                Column {
                    Text("Q1. What is the Unit of Resistance in general physics concepts?", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.clickable { selectedProgressQuiz = 0 }) {
                        RadioButton(selected = selectedProgressQuiz == 0, onClick = { selectedProgressQuiz = 0 })
                        Text("A. Watt", modifier = Modifier.padding(top = 4.dp))
                    }
                    Row(modifier = Modifier.clickable { selectedProgressQuiz = 1 }) {
                        RadioButton(selected = selectedProgressQuiz == 1, onClick = { selectedProgressQuiz = 1 })
                        Text("B. Ampere", modifier = Modifier.padding(top = 4.dp))
                    }
                    Row(modifier = Modifier.clickable { selectedProgressQuiz = 2 }) {
                        RadioButton(selected = selectedProgressQuiz == 2, onClick = { selectedProgressQuiz = 2 })
                        Text("C. Ohm", modifier = Modifier.padding(top = 4.dp))
                    }
                    Row(modifier = Modifier.clickable { selectedProgressQuiz = 3 }) {
                        RadioButton(selected = selectedProgressQuiz == 3, onClick = { selectedProgressQuiz = 3 })
                        Text("D. Volt", modifier = Modifier.padding(top = 4.dp))
                    }

                    if (isSubmittedQuiz) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (selectedProgressQuiz == 2) "🎉 Correct Answer! Ohm (Ω) is the correct standard SI unit of electric resistance." else "❌ Incorrect. The correct answer was C. Ohm.",
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isSubmittedQuiz) {
                            showQuizDialog = false
                            isSubmittedQuiz = false
                            selectedProgressQuiz = -1
                        } else {
                            isSubmittedQuiz = true
                        }
                    },
                    enabled = selectedProgressQuiz != -1
                ) {
                    Text(if (isSubmittedQuiz) "Dismiss" else "Submit Answer")
                }
            }
        )
    }
}

@Composable
fun ChapterCardItem(
    chapter: ChapterEntity,
    onWatchClick: () -> Unit,
    onPdf1Click: () -> Unit,
    onPdf2Click: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title chapter
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayCircle, "Video lecture item", tint = BrandPurpleDark, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(chapter.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Broadcasted On: ${chapter.dateTimeString} • Duration: ${chapter.videoDuration}", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PDFs checklist system (Notes, worksheet homework)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PDF 1 Note
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Initiating Notes download: ${chapter.pdfUrl1Name}", Toast.LENGTH_SHORT).show()
                        onPdf1Click()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PictureAsPdf, "Notes", tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(chapter.pdfUrl1Name, fontSize = 10.sp, maxLines = 1)
                }

                // PDF 2 Sheet homework
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Initiating sheet homework download: ${chapter.pdfUrl2Name}", Toast.LENGTH_SHORT).show()
                        onPdf2Click()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Grading, "Practice Sheet", tint = BrandPurpleLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(chapter.pdfUrl2Name, fontSize = 10.sp, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Play lecture button
            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(Icons.Default.Tv, "Watch lecture stream", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Watch Now (HD Streaming)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
