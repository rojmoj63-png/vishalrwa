package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import coil.compose.AsyncImage
import com.example.data.CourseEntity
import com.example.data.FirestoreUser
import com.example.data.FirestoreFile
import com.example.ui.CourseViewModel
import com.example.ui.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CourseViewModel,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToStudy: (String) -> Unit,
    onNavigateToPayment: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrderHistory: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Collect variables from state flows
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val currentUserId by viewModel.userId.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    val coursesList by viewModel.allCourses.collectAsState()
    val favoritesList by viewModel.allFavorites.collectAsState()
    val purchasesList by viewModel.allPurchases.collectAsState()
    val notificationsList by viewModel.allNotifications.collectAsState()
    val banners by viewModel.allBanners.collectAsState()

    // Map helpers
    val wishlistedIds = remember(favoritesList) { favoritesList.map { it.courseId } }
    val purchasedIds = remember(purchasesList) { purchasesList.map { it.courseId } }
    val unreadCount = remember(notificationsList) { notificationsList.filter { !it.isRead }.size }

    // Form search states
    var homeSearchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") } // all, featured, popular, free, new_batch

    // Banner indices slider state
    var activeBannerIndex by remember { mutableStateOf(0) }

    // Dialog state controllers
    var showAppUpdateDialog by remember { mutableStateOf(false) }
    var showShareAppDialog by remember { mutableStateOf(false) }

    // Navigation drawer wrapped with Scaffold Material 3 layout
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                // Header of Drawer with Student Details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleGradient)
                        .padding(24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A00E0)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (userName.isEmpty()) "RA Student" else userName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = if (userEmail.isEmpty()) "student@rojgar.com" else userEmail,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Drawer Links list
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.School, "My Purchases") },
                    label = { Text("My Study batches / Course Desk") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Filter user purchased courses and jump to first, or show warning toast
                        val firstPurchased = purchasedIds.firstOrNull()
                        if (firstPurchased != null) {
                            onNavigateToStudy(firstPurchased)
                        } else {
                            Toast.makeText(context, "Purchase a classroom course batch to enter!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ReceiptLong, "Order Invoices") },
                    label = { Text("My Bills & Receipts invoices") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToOrderHistory()
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ManageAccounts, "Edit Profile") },
                    label = { Text("Personal details profile settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                )

                if (isAdmin) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AdminPanelSettings, "Admin settings panel Icon") },
                        label = { Text("Admin Faculty portal (Create Batches)", fontWeight = FontWeight.ExtraBold) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToAdmin()
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color(0xFFFFF3CD))
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Send, "Telegram Doubt group") },
                    label = { Text("Join Official Doubts Telegram") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val telegramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/rojgar_with_ankit"))
                        context.startActivity(telegramIntent)
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.SmartDisplay, "YouTube mirror channels") },
                    label = { Text("Free Youtube Prep stream lectures") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@RojgarwithAnkit"))
                        context.startActivity(youtubeIntent)
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Share, "Share app link") },
                    label = { Text("Share Rojgar with Ankit App") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showShareAppDialog = true
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.SystemUpdate, "Sync upgrades") },
                    label = { Text("Check Online Updates") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showAppUpdateDialog = true
                    }
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Logout, "Power logout session") },
                    label = { Text("Log out Session") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.logout()
                        Toast.makeText(context, "Logged Out! Please login again.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Open Menu Drawers dashboard link", tint = Color.White)
                        }
                    },
                    title = {
                        Column {
                            Text("Rojgar With Ankit", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                "Hey, ${if (userName.isEmpty()) "RA Student" else userName} • Exam target prep",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.82f)
                            )
                        }
                    },
                    actions = {
                        // Admin quick entry badge
                        if (isAdmin) {
                            IconButton(onClick = onNavigateToAdmin) {
                                Icon(Icons.Default.AdminPanelSettings, "Admin console icon", tint = Color.Yellow)
                            }
                        }

                        // Notification alert Bell vector
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge { Text(unreadCount.toString()) }
                                }
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            IconButton(onClick = {
                                onNavigateToNotifications()
                            }) {
                                Icon(Icons.Default.Notifications, "Notification feed link", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color(0xFF4A00E0),
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, "Explore courses page") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            val firstCh = purchasedIds.firstOrNull()
                            if (firstCh != null) {
                                onNavigateToStudy(firstCh)
                            } else {
                                Toast.makeText(context, "Purchase any Course Classroom Batch to unlock direct study desk!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        icon = { Icon(Icons.Default.Tv, "Study dynamic stream catalog") },
                        label = { Text("My Library") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onNavigateToProfile,
                        icon = { Icon(Icons.Default.Person, "My details page") },
                        label = { Text("Profile") }
                    )
                }
            }
        ) { innerPadding ->
            if (currentUserId == "rVjVo93UBFPv6zysToWIfi4htG23") {
                AdminPortalView(viewModel = viewModel, innerPadding = innerPadding)
            } else {
                // Filter list of courses by Category tag and search queries
                val filteredCoursesList = coursesList.filter { item ->
                    val matchQuery = item.title.contains(homeSearchQuery, true) ||
                            item.teacherName.contains(homeSearchQuery, true)
                    val matchTab = if (selectedCategory == "all") {
                        true
                    } else if (selectedCategory == "free") {
                        item.isFree
                    } else {
                        item.category == selectedCategory
                    }
                    matchQuery && matchTab
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Color(0xFFF4F6F9))
                ) {
                    // Search Input Field Box
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF4A00E0)) // Sits right below appbar
                                .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
                        ) {
                            OutlinedTextField(
                                value = homeSearchQuery,
                                onValueChange = { homeSearchQuery = it },
                                placeholder = { Text("Search Courses, Exams, Teachers...", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Search, "Search icon pointer", tint = Color.Gray) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color.White,
                                    unfocusedBorderColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Interactive Promotions Banner Carousel Slider
                    if (banners.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .padding(horizontal = 16.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PurpleGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Showing current slider page info
                                    val activeB = banners[activeBannerIndex]
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                if (activeB.actionCourseId != null) {
                                                    onNavigateToCourse(activeB.actionCourseId)
                                                } else {
                                                    Toast.makeText(context, "Welcome under Promotional Prep Classes!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Yellow, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("UPCOMING SPECIAL PREP LECTURE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(activeB.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                                        Text("Free Worksheets PDF included • Tap to checkout", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                                    }
                                }

                                // Horizontal Dot Paging indicator controls
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    banners.forEachIndexed { num, _ ->
                                        Box(
                                            modifier = Modifier
                                                .size(if (num == activeBannerIndex) 8.dp else 5.dp)
                                                .clip(CircleShape)
                                                .background(if (num == activeBannerIndex) Color(0xFF4A00E0) else Color.LightGray)
                                                .clickable { activeBannerIndex = num }
                                                .padding(horizontal = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Horizontal Category Exam preparation chips filter
                    item {
                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                "Syllabus Targets / Category:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val tabs = listOf(
                                    "all" to "Showing All",
                                    "free" to "Free Classes",
                                    "featured" to "Super Batches",
                                    "popular" to "Live Batches",
                                    "new_batch" to "Target Exams"
                                )

                                items(tabs) { tab ->
                                    val active = selectedCategory == tab.first
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (active) Color(0xFF4A00E0) else Color.White)
                                            .clickable { selectedCategory = tab.first }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = tab.second,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (active) Color.White else Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Course card listings
                    if (filteredCoursesList.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Inbox, "Empty records icon", tint = Color.LightGray, modifier = Modifier.size(54.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No preparation batches fit current queries!", fontWeight = FontWeight.SemiBold, color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(filteredCoursesList) { item ->
                            CourseCardItem(
                                course = item,
                                purchased = purchasedIds.contains(item.id),
                                wishlisted = wishlistedIds.contains(item.id),
                                onCardClick = { onNavigateToCourse(item.id) },
                                onWishlistToggle = { viewModel.toggleFavorite(item.id) },
                                onStudyClick = { onNavigateToStudy(item.id) },
                                onBuyNow = { onNavigateToPayment(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Checking Online app Update details Dialog Box
    if (showAppUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showAppUpdateDialog = false },
            title = { Text("App Upgrades Checked") },
            text = {
                Text("Your Rojgar With Ankit App is currently up-to-date with the highest performance specs (Stable v1.0.4). No new catalog updates required.")
            },
            confirmButton = {
                TextButton(onClick = { showAppUpdateDialog = false }) {
                    Text("Superb!")
                }
            }
        )
    }

    // Share App trigger dialog
    if (showShareAppDialog) {
        AlertDialog(
            onDismissRequest = { showShareAppDialog = false },
            title = { Text("Share Prepared batches links") },
            text = {
                Column {
                    Text("Copy Rojgar App link to send to fellow students:")
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Text("https://rojgarwithankit.com/download?ref=ra_stud", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showShareAppDialog = false
                    Toast.makeText(context, "Link Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy Link")
                }
            }
        )
    }
}

@Composable
fun AdminPortalView(
    viewModel: CourseViewModel,
    innerPadding: PaddingValues
) {
    val users by viewModel.firestoreUsers.collectAsState()
    val selectedUserFiles by viewModel.firestoreSelectedUserFiles.collectAsState()
    val isFetching by viewModel.isFetchingFirestoreUsers.collectAsState()
    val context = LocalContext.current

    var selectedUser by remember { mutableStateOf<FirestoreUser?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Trigger load of users when this screen has composed
    LaunchedEffect(Unit) {
        viewModel.loadFirestoreUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(0xFFF1F5F9))
            .padding(16.dp)
    ) {
        // 1. CLEAR ADMIN INDICATOR BADGE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFC107)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD32F2F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Indicator",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ADMIN PORTAL ACTIVE",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRIVILEGED VIEW",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Text(
                        text = "Viewing Firestore cloud database as Admin UID: rVjVo93UBFPv...",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // 2. SEARCH & REFRESH FIELD
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search users by email/name", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, "Search users", modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            IconButton(
                onClick = {
                    viewModel.loadFirestoreUsers()
                    Toast.makeText(context, "Refreshing Firestore Database...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.background(Color(0xFF4A00E0), RoundedCornerShape(8.dp))
            ) {
                if (isFetching) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, "Refresh database users", tint = Color.White)
                }
            }
        }

        // 3. TITLE & DESCRIPTION
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registered Firestore Users",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = "${users.size} record(s)",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 4. USERS LIST VIEW
        val filteredUsers = remember(users, searchQuery) {
            users.filter {
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }

        if (filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Empty",
                        tint = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isFetching) "Loading users..." else "No user records match search queries.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    UserListItem(
                        user = user,
                        onClick = {
                            selectedUser = user
                            viewModel.loadFirestoreUserFiles(user.id)
                        }
                    )
                }
            }
        }
    }

    // 5. USER FILE VIEWER BOTTOM SHEET / DIALOG
    selectedUser?.let { user ->
        AlertDialog(
            onDismissRequest = { selectedUser = null },
            title = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Uploaded Files",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { selectedUser = null }) {
                            Icon(Icons.Default.Close, "Close details")
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4A00E0)
                    )
                    Text(
                        text = user.email,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                    
                    if (selectedUserFiles.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.FilePresent,
                                    contentDescription = "No Files",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("This user has not uploaded any files yet.", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(selectedUserFiles) { file ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PictureAsPdf,
                                                contentDescription = "PDF document",
                                                tint = Color(0xFFD32F2F),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = file.fileName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${file.fileSize} • Uploaded: ${file.uploadedAt}",
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                        
                                        IconButton(
                                            onClick = {
                                                Toast.makeText(context, "Opening ${file.fileName}...", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FileDownload,
                                                contentDescription = "Download File",
                                                tint = Color(0xFF4A00E0),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedUser = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0))
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun UserListItem(
    user: FirestoreUser,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A00E0),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = user.email,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Joined: ${user.createdAt}",
                        fontSize = 9.sp,
                        color = Color.LightGray
                    )
                }
            }

            // Number of uploaded files badge
            Box(
                modifier = Modifier
                    .background(Color(0xFFE0F2FE), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Uploads",
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user.fileCount} files",
                        color = Color(0xFF0369A1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CourseCardItem(
    course: CourseEntity,
    purchased: Boolean,
    wishlisted: Boolean,
    onCardClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onStudyClick: () -> Unit,
    onBuyNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual decorative prep banner box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = if (course.isFree) listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)) else listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))
                        )
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (course.isFree) Color(0xFF2E7D32) else Color(0xFF673AB7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, "Syllabus details icon", tint = Color.White, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (course.isFree) Color(0xFF2E7D32) else Color(0xFF673AB7), RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (course.isFree) "FREE DEMO CLASS" else "PREMIUM SUPER BATCH",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(course.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1)
                        Text("Instructor: ${course.teacherName}", fontSize = 11.sp, color = Color.Gray)
                    }

                    // Wishlist Star Icon
                    IconButton(onClick = onWishlistToggle) {
                        Icon(
                            imageVector = if (wishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            tint = if (wishlisted) Color.Red else Color.Gray,
                            contentDescription = "Save Course"
                        )
                    }
                }
            }

            // Bottom particulars values detail
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ratings summary
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, "Stars ratings badge representation", tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(course.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("•  Validity: ${course.validity}", fontSize = 11.sp, color = Color.Gray)
                    }

                    // Pricing values description
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (course.isFree) {
                            Text("FREE STUDY", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF2E7D32))
                        } else {
                            Text(
                                "₹${course.price.toInt()}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                "₹${course.discountPrice.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color(0xFFE53935)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action study triggers buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (purchased || course.isFree) {
                        // Enter classroom button
                        Button(
                            onClick = onStudyClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PlayCircleOutline, "Launch streaming lecture classes")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Let's Study (Classroom)", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Quick Purchase action
                        Button(
                            onClick = onCardClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A00E0)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Syllabus Classes Details", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBuyNow,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Icon(Icons.Default.ShoppingCart, "Quick Buy lock purchase indicator")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buy Batch", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
