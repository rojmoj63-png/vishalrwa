package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MailOutline
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CourseViewModel
import com.example.ui.Screen
import kotlinx.coroutines.delay

// Purple brand color accents
val BrandPurpleLight = Color(0xFFA259FF)
val BrandPurpleDark = Color(0xFF6200EE)
val PurpleGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
)

@Composable
fun SplashScreen(
    viewModel: CourseViewModel,
    onNavigateNext: (String) -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var isLogoVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLogoVisible = true
        delay(2200) // Beautiful splash showing our brand
        if (isLoggedIn) {
            onNavigateNext(Screen.Home)
        } else {
            onNavigateNext(Screen.Login)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Animated visual asset representing the brand logo
            AnimatedVisibility(
                visible = isLogoVisible,
                enter = fadeIn(animationSpec = tween(1000)) + scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Symbol of Education: Graduation Book in Golden Purple
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Rojgar With Ankit Logo",
                        tint = Color(0xFF4A00E0),
                        modifier = Modifier.size(72.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ROJGAR WITH ANKIT",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "सफलता का दूसरा नाम",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
        }

        // Footer version info
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Version 1.0.4 Premium • Powered by Jetpack Compose",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: CourseViewModel,
    onNavigateRegister: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current
    var loginMethod by remember { mutableStateOf("email") } // Default is Email login now since they enabled Email & password auth

    var mobileNo by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var userNameInput by remember { mutableStateOf("") }
    var userEmailInput by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }

    // Email Credentials Login States
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FA))
    ) {
        // App top decorative banner in Purple
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(PurpleGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome Student",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sign in to practice tests & access live classroom batches",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Form container sheet
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .padding(top = 110.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Beautiful Segmented tabs for selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (loginMethod == "email") Color.White else Color.Transparent)
                            .clickable { loginMethod = "email" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Email Login",
                            fontWeight = FontWeight.Bold,
                            color = if (loginMethod == "email") Color(0xFF4A00E0) else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (loginMethod == "mobile") Color.White else Color.Transparent)
                            .clickable { loginMethod = "mobile" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mobile OTP",
                            fontWeight = FontWeight.Bold,
                            color = if (loginMethod == "mobile") Color(0xFF4A00E0) else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }

                // Render based on Tab choice
                if (loginMethod == "mobile") {
                    Text(
                        text = if (isOtpSent) "Verify OTP Code" else "Mobile Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (!isOtpSent) {
                        // Mobile OTP trigger mode
                        OutlinedTextField(
                            value = mobileNo,
                            onValueChange = { if (it.length <= 10) mobileNo = it },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("10 digit mobile number") },
                            leadingIcon = { Icon(Icons.Default.Phone, "Mobile Phone") },
                            prefix = { Text("+91 ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPurpleDark,
                                focusedLabelColor = BrandPurpleDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (mobileNo.length == 10) {
                                    isOtpSent = true
                                }
                            },
                            enabled = mobileNo.length == 10,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Send, "Send")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send OTP Code", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Simulated quick login option
                        TextButton(
                            onClick = {
                                viewModel.loginWithMobile("9888776655", "Premium Student", "student@rojgarwithankit.com")
                                onNavigateHome()
                            }
                        ) {
                            Text("Quick Skip / Demo Student Login", color = Color(0xFF8E2DE2), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // OTP Verification Mode
                        Text(
                            text = "We sent an OTP code via SMS to +91 $mobileNo. Enter '1234' to verify instantly.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Profile placeholders for registration-on-the-fly
                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { userNameInput = it },
                            label = { Text("Full Name (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Person, "Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = userEmailInput,
                            onValueChange = { userEmailInput = it },
                            label = { Text("Email Address (Optional)") },
                            leadingIcon = { Icon(Icons.Outlined.MailOutline, "Email") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 4) otpCode = it },
                            label = { Text("4-Digit OTP") },
                            placeholder = { Text("Enter 1234") },
                            leadingIcon = { Icon(Icons.Default.Lock, "OTP Lock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isVerifying) {
                            CircularProgressIndicator(color = BrandPurpleDark)
                        } else {
                            Button(
                                onClick = {
                                    isVerifying = true
                                    val finalName = if (userNameInput.isEmpty()) "RA Student" else userNameInput
                                    val finalEmail = if (userEmailInput.isEmpty()) "student_${mobileNo}@rojgar.com" else userEmailInput
                                    viewModel.loginWithMobile(mobileNo, finalName, finalEmail)
                                    isVerifying = false
                                    onNavigateHome()
                                },
                                enabled = otpCode.length >= 4,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text("Verify & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = { isOtpSent = false }) {
                                Text("Edit Mobile Number", color = Color.Gray)
                            }
                        }
                    }
                } else {
                    // EMAIL & PASSWORD LOGIN METHOD Mode
                    Text(
                        text = "Sign In with Credentials",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    if (emailErrorMessage.isNotEmpty()) {
                        Text(
                            text = emailErrorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { 
                            emailInput = it
                            emailErrorMessage = ""
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("student@gmail.com") },
                        leadingIcon = { Icon(Icons.Outlined.MailOutline, "Email field signin") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPurpleDark,
                            focusedLabelColor = BrandPurpleDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { 
                            passwordInput = it
                            emailErrorMessage = ""
                        },
                        label = { Text("Account Password") },
                        placeholder = { Text("Min 6 characters") },
                        leadingIcon = { Icon(Icons.Default.Lock, "Pass lock") },
                        trailingIcon = {
                            val visibilityIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (passwordVisible) "Hide Password text" else "Show Password text"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = visibilityIcon, contentDescription = description)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPurpleDark,
                            focusedLabelColor = BrandPurpleDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.loginWithEmailAndPassword(
                                email = emailInput,
                                password = passwordInput,
                                onSuccess = {
                                    Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                                    onNavigateHome()
                                },
                                onError = { feedback ->
                                    emailErrorMessage = feedback
                                }
                            )
                        },
                        enabled = emailInput.trim().isNotEmpty() && passwordInput.length >= 6,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Login, "Enter credential dashboard")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log In Securely", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated default testing account presentation (Seeded user account)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8F0FE))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Registered Student Account Enabled:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E88E5),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Email: rojmoj63@gmail.com • Pass: password123",
                                fontSize = 10.sp,
                                color = Color(0xFF0D47A1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Bottom login option: Admin login or register route
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", color = Color.Gray)
                TextButton(onClick = { onNavigateRegister() }) {
                    Text("Register Now", color = Color(0xFF8E2DE2), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Google Sign In & Admin trigger buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Interactive Simulated Google Signin
                OutlinedButton(
                    onClick = {
                        viewModel.loginWithGoogle("rojmoj63@gmail.com", "Ankit Fan Student")
                        Toast.makeText(context, "Logged in via Google secure portal!", Toast.LENGTH_SHORT).show()
                        onNavigateHome()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = "Google Icon integration",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Google", fontSize = 12.sp)
                }

                // Admin Login Option
                Button(
                    onClick = {
                        viewModel.loginAsAdmin()
                        Toast.makeText(context, "Logged in as Lead Faculty Admin!", Toast.LENGTH_SHORT).show()
                        onNavigateHome()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Faculty Portal link",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Admin Login", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: CourseViewModel,
    onNavigateLogin: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(PurpleGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Join India's most trusted online exam prep platform",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .padding(top = 90.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Student Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                // Full Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        errorMessage = ""
                    },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, "Full student name entry") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email Address
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        errorMessage = ""
                    },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Outlined.MailOutline, "Student account email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile Number
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { 
                        if (it.length <= 10) {
                            mobile = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("10-Digit Mobile") },
                    leadingIcon = { Icon(Icons.Default.Phone, "Mobile Contact Details") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password Input Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("Create Account Password") },
                    placeholder = { Text("Minimum 6 characters") },
                    leadingIcon = { Icon(Icons.Default.Lock, "Interactive Password Key") },
                    trailingIcon = {
                        val visibilityIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val description = if (passwordVisible) "Hide visual pass text" else "Show visual pass text"
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPurpleDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = BrandPurpleLight)
                    )
                    Text(
                        text = "I agree to RA Terms and Privacy Policy.",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
                            errorMessage = "Please complete all registration fields"
                        } else if (password.length < 6) {
                            errorMessage = "Password must be at least 6 characters!"
                        } else if (mobile.length != 10) {
                            errorMessage = "Please enter a valid 10-digit mobile number!"
                        } else {
                            viewModel.registerWithEmailAndPassword(
                                email = email,
                                password = password,
                                name = name,
                                mobile = mobile,
                                onSuccess = {
                                    Toast.makeText(context, "Registration Complete! Welcome to RA.", Toast.LENGTH_SHORT).show()
                                    onNavigateHome()
                                },
                                onError = { reason ->
                                    errorMessage = reason
                                }
                            )
                        }
                    },
                    enabled = agreeToTerms && name.isNotEmpty() && email.isNotEmpty() && mobile.isNotEmpty() && password.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Register & Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already registered?", color = Color.Gray)
                TextButton(onClick = { onNavigateLogin() }) {
                    Text("Login Now", color = Color(0xFF8E2DE2), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
