package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
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
import com.example.data.PurchaseEntity
import com.example.ui.CourseViewModel
import com.example.ui.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CourseViewModel,
    onBack: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    val context = LocalContext.current

    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val mobile by viewModel.userMobile.collectAsState()

    var editedName by remember { mutableStateOf(name) }
    var editedEmail by remember { mutableStateOf(email) }
    var editedMobile by remember { mutableStateOf(mobile) }
    var isEditing by remember { mutableStateOf(false) }

    // Toggle Dark Mode
    var isDarkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Management") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile avatar display with Purple background gradient
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(PurpleGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (name.isEmpty()) "R" else name.take(1).uppercase(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(if (name.isEmpty()) "RA Student" else name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
                        Text(if (email.isEmpty()) "student@rojgar.com" else email, color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }

            // Student Information Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Personal Details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandPurpleDark)
                            TextButton(onClick = {
                                if (isEditing) {
                                    viewModel.updateProfile(editedName, editedEmail, editedMobile)
                                    Toast.makeText(context, "Profile details updated successfully!", Toast.LENGTH_SHORT).show()
                                }
                                isEditing = !isEditing
                            }) {
                                Text(if (isEditing) "Save Data" else "Edit")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isEditing) {
                            OutlinedTextField(
                                value = editedName,
                                onValueChange = { editedName = it },
                                label = { Text("Display Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = editedEmail,
                                onValueChange = { editedEmail = it },
                                label = { Text("Email Address") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = editedMobile,
                                onValueChange = { editedMobile = it },
                                label = { Text("Registered Telephone") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                        } else {
                            ProfileInfoRow(icon = Icons.Default.Person, value = if (name.isEmpty()) "RA Student" else name, label = "Full Name")
                            ProfileInfoRow(icon = Icons.Default.Email, value = if (email.isEmpty()) "student@rojgar.com" else email, label = "Email Address")
                            ProfileInfoRow(icon = Icons.Default.Phone, value = if (mobile.isEmpty()) "+91 9888776655" else "+91 $mobile", label = "Mobile Number")
                        }
                    }
                }
            }

            // Extra Settings switches
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("App Customizations", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandPurpleDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Dark Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, "Dark Mode", tint = Color.DarkGray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Display Mode Choice", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Switch dark theme mode layouts", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Switch(
                                checked = isDarkModeEnabled,
                                onCheckedChange = {
                                    isDarkModeEnabled = it
                                    Toast.makeText(context, "Theme toggled. Mode shifts instantly.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Clear Cache
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Toast.makeText(context, "Offline material cache cleared successfully!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CleaningServices, "Clear Cache", tint = Color.DarkGray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Clear Lesson Cache Storage", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Free up space used by recorded PDFs notes", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, "Action")
                        }
                    }
                }
            }

            // Interactive Support / Help contacts
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Connect Help Desk", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandPurpleDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.SupportAgent, "Support Phone", tint = BrandPurpleLight, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Toll Free: 1800-202-ANKIT (09:00 AM - 06:00 PM)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.Email, "Support Email", tint = BrandPurpleLight, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("E-support: help@rojgarwithankit.com", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Session Logout anchor
            item {
                Button(
                    onClick = {
                        viewModel.logout()
                        onNavigateLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4C4D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Logout Current Session", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RazorpayPaymentScreen(
    courseId: String,
    viewModel: CourseViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(courseId) {
        viewModel.loadCourseDetails(courseId)
    }

    val course by viewModel.selectedCourse.collectAsState()

    // Interactive Transaction screen stages
    var paymentStage by remember { mutableStateOf(0) } // 0 = checkout details review , 1 = simulator processing delay , 2 = animation success confirmation

    var billingUpiId by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("UPI Instant Pay") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (paymentStage == 2) "Order Confirmed!" else "Razorpay Secure Pay") },
                navigationIcon = {
                    if (paymentStage < 1) {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0xFF1E2835), // Dark sleek signature Razorpay interface color
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF1F5F9))
        ) {
            when (paymentStage) {
                0 -> {
                    // Billing Statement details review with razorpay secure badges
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("RAZORPAY ORDER DETAILS", fontSize = 11.sp, color = Color(0xFF0D6EFD), fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(course?.title ?: "Classroom Batch Access", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("₹${course?.discountPrice?.toInt() ?: 499}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Syllabus Duration: ${course?.validity ?: "12 Months"}", fontSize = 12.sp, color = Color.Gray)

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                // Payment breakdown summary
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Subtotal:", fontSize = 13.sp, color = Color.Gray)
                                    Text("₹${course?.price?.toInt() ?: 999}", fontSize = 13.sp, color = Color.Gray)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Rojgar Code Discount:", fontSize = 13.sp, color = Color.Gray)
                                    Text("-₹${((course?.price ?: 999.0) - (course?.discountPrice ?: 499.0)).toInt()}", fontSize = 13.sp, color = Color.Red)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Interactive GST (18%):", fontSize = 13.sp, color = Color.Gray)
                                    Text("Included", fontSize = 13.sp, color = Color.Gray)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Payable:", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                    Text("₹${course?.discountPrice?.toInt() ?: 499}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF2E7D32))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Payment selector methods
                        Text("Select Secure Payment Mode:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        PaymentMethodSelector(title = "UPI Instant Payments (GPay/PhonePe)", active = selectedMethod == "UPI Instant Pay") { selectedMethod = "UPI Instant Pay" }
                        PaymentMethodSelector(title = "Credit/Debit Card (Visa, RuPay, MasterCard)", active = selectedMethod == "Custom Card") { selectedMethod = "Custom Card" }
                        PaymentMethodSelector(title = "NetBanking / Wallet Integrations", active = selectedMethod == "NetBanking") { selectedMethod = "NetBanking" }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedMethod == "UPI Instant Pay") {
                            OutlinedTextField(
                                value = billingUpiId,
                                onValueChange = { billingUpiId = it },
                                label = { Text("Enter Your UPI ID (e.g. mobile@ybl)") },
                                placeholder = { Text("9876543210@ybl") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Pay Securely Button
                        Button(
                            onClick = {
                                paymentStage = 1
                                scope.launch {
                                    delay(2000) // simulated Razorpay processor sequence
                                    viewModel.processPurchase(
                                        courseId = courseId,
                                        amountPaid = course?.discountPrice ?: 499.0,
                                        paymentMode = selectedMethod,
                                        onSuccess = {
                                            paymentStage = 2
                                        }
                                    )
                                }
                            },
                            enabled = selectedMethod != "UPI Instant Pay" || billingUpiId.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Icon(Icons.Default.Security, "Secure lock")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PAY SECURELY WITH RAZORPAY", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                1 -> {
                    // Processing animation screen
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0D6EFD), strokeWidth = 4.dp, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Authorizing Razorpay Payment...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Please do not press back button or minimize the app.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
                2 -> {
                    // Purchase successful invoice confirmation
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Course Unlocked Successfully! 🎉", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF2E7D32))
                        Text(
                            "Thank you! Your payment of ₹${course?.discountPrice?.toInt() ?: 499} is confirmed by Razorpay.",
                            color = Color.DarkGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                onSuccess()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Let's Meet In Classroom!", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: CourseViewModel,
    onBack: () -> Unit
) {
    val purchases by viewModel.allPurchases.collectAsState()
    val courses by viewModel.allCourses.collectAsState()

    var activeInvoiceDetails by remember { mutableStateOf<PurchaseEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Purchase Receipts") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9F9FA))
        ) {
            if (purchases.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Receipt, "No orders", tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No purchased batches found!", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Unlock premium target exams batches to view order histories.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(purchases) { item ->
                        val matchingCourse = courses.find { it.id == item.courseId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(matchingCourse?.title ?: "Exam Target Batch", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("Invoice ID: ${item.invoiceNo}", fontSize = 11.sp, color = Color.Gray)
                                        Text("Purchased On: ${formatTimestamp(item.purchaseDate)}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Text("₹${item.pricePaid.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF2E7D32))
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { activeInvoiceDetails = item },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F0FE), contentColor = Color(0xFF1E88E5)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, "Invoice PDF logo", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate PDF Invoice Receipt", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Generated invoice detail modal
            val detail = activeInvoiceDetails
            if (detail != null) {
                val courseObj = courses.find { it.id == detail.courseId }
                val userNameVal by viewModel.userName.collectAsState()
                val userMobileVal by viewModel.userMobile.collectAsState()
                val userEmailVal by viewModel.userEmail.collectAsState()

                AlertDialog(
                    onDismissRequest = { activeInvoiceDetails = null },
                    title = {
                        Text(
                            text = "GST TAX INVOICE RECEIPT",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4A00E0),
                            fontSize = 15.sp
                        )
                    },
                    text = {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF1F5F9))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("ROJGAR WITH ANKIT (RA)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Bhati online education private limited, Delhi NCR", fontSize = 10.sp, color = Color.Gray)
                                    Text("GSTIN No: 09AAPCB2026R1ZX", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("BILL TO STUDENT:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandPurpleDark)
                            Text("Name: $userNameVal", fontSize = 12.sp)
                            Text("Mobile: +91 $userMobileVal", fontSize = 11.sp)
                            Text("Email: $userEmailVal", fontSize = 11.sp)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Text("COURSE ORDER SPECIFICS:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandPurpleDark)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(courseObj?.title ?: "Batch Exam", fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                                Text("1 Qty x ₹${detail.pricePaid.toInt()}", fontSize = 11.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal (9% CGST + 9% SGST INC):", fontSize = 11.sp)
                                Text("₹${detail.pricePaid.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Order Reference Transaction ID:", fontSize = 11.sp, color = Color.Gray)
                                Text(detail.transactionId.take(16), fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { activeInvoiceDetails = null }) {
                            Text("Dismiss Receipt")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = BrandPurpleDark, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Composable
fun PaymentMethodSelector(
    title: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) Color(0xFFE8F0FE) else Color.White)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = active, onClick = onClick)
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (active) Color(0xFF0D6EFD) else Color.DarkGray)
    }
}

fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return formatter.format(Date(millis))
}
