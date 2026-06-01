package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.CourseViewModel
import com.example.ui.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CourseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash
                ) {
                    // 1. Splash Screen
                    composable(Screen.Splash) {
                        SplashScreen(
                            viewModel = viewModel,
                            onNavigateNext = { nextRoute ->
                                navController.navigate(nextRoute) {
                                    popUpTo(Screen.Splash) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Login Screen
                    composable(Screen.Login) {
                        LoginScreen(
                            viewModel = viewModel,
                            onNavigateRegister = {
                                navController.navigate(Screen.Register)
                            },
                            onNavigateHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 3. Register Screen
                    composable(Screen.Register) {
                        RegisterScreen(
                            viewModel = viewModel,
                            onNavigateLogin = {
                                navController.navigate(Screen.Login)
                            },
                            onNavigateHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Register) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 4. Home Screen
                    composable(Screen.Home) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToCourse = { courseId ->
                                navController.navigate(Screen.courseDetailsRoute(courseId))
                            },
                            onNavigateToStudy = { courseId ->
                                navController.navigate(Screen.subjectRoute(courseId))
                            },
                            onNavigateToPayment = { courseId ->
                                navController.navigate(Screen.paymentRoute(courseId))
                            },
                            onNavigateToProfile = {
                                navController.navigate(Screen.Profile)
                            },
                            onNavigateToOrderHistory = {
                                navController.navigate(Screen.OrderHistory)
                            },
                            onNavigateToAdmin = {
                                navController.navigate(Screen.AdminDashboard)
                            },
                            onNavigateToNotifications = {
                                navController.navigate("notifications_history")
                            }
                        )
                    }

                    // 5. Course Details Screen
                    composable(
                        route = Screen.CourseDetails,
                        arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        CourseDetailsScreen(
                            courseId = courseId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToSubjectStudy = { id ->
                                navController.navigate(Screen.subjectRoute(id))
                            },
                            onNavigateToPayment = { id ->
                                navController.navigate(Screen.paymentRoute(id))
                            }
                        )
                    }

                    // 6. My Batch & Subject Screen (Accessing direct course syllabus)
                    composable(
                        route = Screen.Subject,
                        arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        SubjectScreen(
                            courseId = courseId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToChapters = { subjectId ->
                                navController.navigate(Screen.chapterRoute(subjectId, courseId))
                            }
                        )
                    }

                    // 7. Chapter Curriculum Screen
                    composable(
                        route = Screen.Chapter,
                        arguments = listOf(
                            navArgument("subjectId") { type = NavType.StringType },
                            navArgument("courseId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        ChapterScreen(
                            subjectId = subjectId,
                            courseId = courseId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToVideo = { chapterId ->
                                navController.navigate(Screen.videoRoute(chapterId, subjectId))
                            },
                            onNavigateToPDF = { chapterId, pdfIndex ->
                                navController.navigate(Screen.pdfRoute(chapterId, pdfIndex))
                            }
                        )
                    }

                    // 8. Custom Immersive Video Player Screen
                    composable(
                        route = Screen.Video,
                        arguments = listOf(
                            navArgument("chapterId") { type = NavType.StringType },
                            navArgument("subjectId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                        val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                        VideoPlayerScreen(
                            chapterId = chapterId,
                            subjectId = subjectId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToPDF = { chapId, index ->
                                navController.navigate(Screen.pdfRoute(chapId, index))
                            }
                        )
                    }

                    // 9. PDF Lesson Reader Screen
                    composable(
                        route = Screen.PDF,
                        arguments = listOf(
                            navArgument("chapterId") { type = NavType.StringType },
                            navArgument("pdfIndex") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                        val pdfIndex = backStackEntry.arguments?.getString("pdfIndex") ?: "1"
                        PDFViewerScreen(
                            chapterId = chapterId,
                            pdfIndex = pdfIndex,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 10. Student Profile Screen
                    composable(Screen.Profile) {
                        ProfileScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateLogin = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 11. Razorpay Secure Payment Checkout Screen
                    composable(
                        route = Screen.Payment,
                        arguments = listOf(navArgument("courseId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
                        RazorpayPaymentScreen(
                            courseId = courseId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onSuccess = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 12. Receipts & Order Histories Screen
                    composable(Screen.OrderHistory) {
                        OrderHistoryScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 13. Admin Dashboard Screen Configuration
                    composable(Screen.AdminDashboard) {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 14. Notifications Broadcaster Feed Screen
                    composable("notifications_history") {
                        NotificationScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
