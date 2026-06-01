package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CourseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    chapterId: String,
    subjectId: String,
    viewModel: CourseViewModel,
    onBack: () -> Unit,
    onNavigateToPDF: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Load active chapters
    LaunchedEffect(chapterId) {
        viewModel.loadChapterDetails(chapterId)
    }

    val course by viewModel.selectedCourse.collectAsState()
    val subject by viewModel.selectedSubject.collectAsState()
    val chapter by viewModel.selectedChapter.collectAsState()
    val allChapters by viewModel.activeChapters.collectAsState()

    // Player state values
    var isPlaying by remember { mutableStateOf(true) }
    var mockProgress by remember { mutableFloatStateOf(0.24f) }
    var currentProgressSeconds by remember { mutableIntStateOf(520) } // 8 mins 40 seconds
    val maxProgressSeconds = 2700 // 45 minutes

    // Interactive progress updater loop
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            if (currentProgressSeconds < maxProgressSeconds) {
                currentProgressSeconds += 1
                mockProgress = currentProgressSeconds.toFloat() / maxProgressSeconds.toFloat()
            } else {
                isPlaying = false
            }
        }
    }

    // Video Resolution select
    var selectedResolution by remember { mutableStateOf("Auto (720px)") }
    var showResolutionSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chapter?.name?.take(20) ?: "Streaming Lecture") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Immersive Video Player Window box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Background video placeholder gradient art
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(Color(0xFF1F1F1F), Color(0xFF121212), Color(0xFF2E2E2E))
                            )
                        )
                )

                // Simulated dynamic video stats overlays
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top stats badge indicators (Resolution, Speed, etc)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.Red, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("LIVE BROADCAST • REPLAY", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Quality resolution pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.25f))
                                .clickable { showResolutionSheet = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, "Resolution Settings", tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedResolution, color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }

                    // Main Center Player Controls (Prev, Play/Pause, Next)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Lecture Button
                        IconButton(
                            onClick = {
                                val currentIndex = allChapters.indexOfFirst { it.id == chapter?.id }
                                if (currentIndex > 0) {
                                    val prevCh = allChapters[currentIndex - 1]
                                    viewModel.loadChapterDetails(prevCh.id)
                                    currentProgressSeconds = 0
                                    mockProgress = 0f
                                    isPlaying = true
                                    Toast.makeText(context, "Playing Lecture: ${prevCh.name}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "This is the first chapter lecture!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.Default.SkipPrevious, "Previous lecture video", tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(28.dp))

                        // Large Play / Pause selector
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play or Pause streaming video",
                                tint = Color(0xFF4A00E0),
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(28.dp))

                        // Next Lecture Button
                        IconButton(
                            onClick = {
                                val currentIndex = allChapters.indexOfFirst { it.id == chapter?.id }
                                if (currentIndex != -1 && currentIndex < allChapters.size - 1) {
                                    val nextCh = allChapters[currentIndex + 1]
                                    viewModel.loadChapterDetails(nextCh.id)
                                    currentProgressSeconds = 0
                                    mockProgress = 0f
                                    isPlaying = true
                                    Toast.makeText(context, "Playing Lecture: ${nextCh.name}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "This is the last chapter lecture available!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.Default.SkipNext, "Next lecture video", tint = Color.White)
                        }
                    }

                    // Bottom progress controller bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = mockProgress,
                            onValueChange = {
                                mockProgress = it
                                currentProgressSeconds = (it * maxProgressSeconds).toInt()
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Red,
                                activeTrackColor = Color.Red,
                                inactiveTrackColor = Color.White.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(currentProgressSeconds),
                                color = Color.White,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Total: " + (chapter?.videoDuration ?: "45:00"),
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Course progress tracker box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8EAF6))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Classroom Course Progress:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandPurpleDark)
                        Text("Completed: 24% (12/50 Videos)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.24f },
                        color = Color(0xFF4CAF50),
                        trackColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Text("Continue Watching: Click 'Next' to solve worksheets concurrently.", fontSize = 10.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // Lecture specific info card list
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = chapter?.name ?: "Topic Class Lecture",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                )
                Text(
                    text = "Instructor In-charge: ${course?.teacherName ?: "Rojgar Experts"} • ${subject?.name ?: "General Aptitude"}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Material study utilities list
                Text("Syllabus Notes & Resource Actions:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BrandPurpleDark)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudyUtilityButton(
                        modifier = Modifier.weight(1.0f),
                        icon = Icons.Default.PictureAsPdf,
                        color = Color(0xFFE57373),
                        title = "Download PDF Notes",
                        description = chapter?.pdfUrl1Name ?: "Notes.pdf"
                    ) {
                        if (chapter != null) {
                            onNavigateToPDF(chapter!!.id, "1")
                        }
                    }

                    StudyUtilityButton(
                        modifier = Modifier.weight(1.0f),
                        icon = Icons.Default.Assignment,
                        color = Color(0xFFFFB74D),
                        title = "Homework Practice Sheet",
                        description = chapter?.pdfUrl2Name ?: "Worksheet.pdf"
                    ) {
                        if (chapter != null) {
                            onNavigateToPDF(chapter!!.id, "2")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudyUtilityButton(
                        modifier = Modifier.weight(1.0f),
                        icon = Icons.Default.Send,
                        color = Color(0xFF4FC3F7),
                        title = "Join Teacher Telegram",
                        description = "Instant Doubt Solving Chat"
                    ) {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/rojgar_with_ankit"))
                        context.startActivity(i)
                    }

                    StudyUtilityButton(
                        modifier = Modifier.weight(1.0f),
                        icon = Icons.Default.SmartDisplay,
                        color = Color(0xFFFF8A65),
                        title = "Backup YouTube Stream",
                        description = "Watch backup mirrors"
                    ) {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(chapter?.videoUrl ?: "https://youtube.com"))
                        context.startActivity(i)
                    }
                }
            }
        }
    }

    // Resolution selection dialog popup sheet
    if (showResolutionSheet) {
        AlertDialog(
            onDismissRequest = { showResolutionSheet = false },
            title = { Text("Stream Video Quality") },
            text = {
                Column {
                    listOf("Auto (Adaptive Pixels)", "Premium HD (1080px)", "Medium Standard (720px)", "Data Saver Mode (360px)").forEach { tech ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedResolution = tech.substringBefore(" ")
                                    showResolutionSheet = false
                                    Toast
                                        .makeText(context, "Rescaling stream to $selectedResolution", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Hd, "Resolution Selection Icon", tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(tech, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showResolutionSheet = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudyUtilityButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
            Text(description, fontSize = 9.sp, color = Color.Gray, maxLines = 1)
        }
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d", m, s)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFViewerScreen(
    chapterId: String,
    pdfIndex: String,
    viewModel: CourseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val chapter by viewModel.selectedChapter.collectAsState()

    LaunchedEffect(chapterId) {
        viewModel.loadChapterDetails(chapterId)
    }

    val pdfName = remember(chapter, pdfIndex) {
        if (pdfIndex == "1") {
            chapter?.pdfUrl1Name ?: "Chapter_Lecture_Notes.pdf"
        } else {
            chapter?.pdfUrl2Name ?: "Practice_Homework_Worksheet.pdf"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pdfName.take(24)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Notes PDF Download Completed Successfully!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Download, "Download local notes", tint = Color.White)
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
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simulated high-quality interactive PDF Reading Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ROJGAR WITH ANKIT (RA) BATCH DESK",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Premium Live Class Handwritten Notes",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "TOPIC LECTURE ANALYSIS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF4A00E0)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CONCEPT 1: Fundamentals and Overview\n" +
                                "In this class, we analyzed the Core Curriculum Formulas. Ensure high revisions of specific topics, and match numerical questions. All PYQ from 2018 to 2024 are attached under section 2 study sheets.\n\n" +
                                "CONCEPT 2: Step-by-Step Problem Solving\n" +
                                "1. Break complex values into atomic ratios.\n" +
                                "2. Apply short-cut methods compiled by expert batch faculty.\n" +
                                "3. Cross check unit bounds: Resistance = Ohm (Ω), Conductance = Siemens.\n" +
                                "Refer to chapter video timestamps 12:40 and 28:15 for live derivations.\n\n" +
                                "CONCEPT 3: Practice Set homework worksheets\n" +
                                "Attempt practice worksheets prior to next live quiz sessions. Doubts solving chat answers are published inside official RA Telegram handles.\n\n" +
                                "--------------------------------------------------\n" +
                                "End of Classroom study guide handouts. Copy registered under Rojgar With Ankit student profiles.\n",
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress footer controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Page 1 of 1 (Handout complete)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Button(
                    onClick = {
                        Toast.makeText(context, "Material saved to local files!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.SaveAlt, "Download PDF")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Study File Offline", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
