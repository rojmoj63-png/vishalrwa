package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CourseRepository(application)

    // ======= AUTH STATES =======
    private val _isLoggedIn = MutableStateFlow(repository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userId = MutableStateFlow(repository.getUserId())
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _userName = MutableStateFlow(repository.getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(repository.getUserEmail())
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userMobile = MutableStateFlow(repository.getUserMobile())
    val userMobile: StateFlow<String> = _userMobile.asStateFlow()

    private val _isAdmin = MutableStateFlow(repository.isAdminLoggedIn())
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // ======= FIRESTORE ADMIN DATA FLOWS =======
    private val firestoreService = FirestoreService(application)

    private val _firestoreUsers = MutableStateFlow<List<FirestoreUser>>(emptyList())
    val firestoreUsers: StateFlow<List<FirestoreUser>> = _firestoreUsers.asStateFlow()

    private val _firestoreSelectedUserFiles = MutableStateFlow<List<FirestoreFile>>(emptyList())
    val firestoreSelectedUserFiles: StateFlow<List<FirestoreFile>> = _firestoreSelectedUserFiles.asStateFlow()

    private val _isFetchingFirestoreUsers = MutableStateFlow(false)
    val isFetchingFirestoreUsers: StateFlow<Boolean> = _isFetchingFirestoreUsers.asStateFlow()

    fun loadFirestoreUsers() {
        viewModelScope.launch {
            _isFetchingFirestoreUsers.value = true
            try {
                _firestoreUsers.value = firestoreService.getAllUsers()
            } catch (e: Exception) {
                Log.e("CourseViewModel", "Error loading users: ${e.message}")
            } finally {
                _isFetchingFirestoreUsers.value = false
            }
        }
    }

    fun loadFirestoreUserFiles(userId: String) {
        viewModelScope.launch {
            try {
                _firestoreSelectedUserFiles.value = firestoreService.getUserFiles(userId)
            } catch (e: Exception) {
                Log.e("CourseViewModel", "Error loading user files: ${e.message}")
            }
        }
    }

    // ======= COURES & DATA FLOWS =======
    val allCourses = repository.allCoursesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allPurchases = repository.allPurchasesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allBanners = repository.allBannersFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allFavorites = repository.allFavoritesFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allNotifications = repository.allNotificationsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // ======= INTERACTIVE SEARCH & CATEGORIES =======
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All") // "All", "Featured", "Popular", "Free", "New Batches"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Combined filtered courses list
    val filteredCourses = combine(allCourses, _searchQuery, _selectedCategory) { list, query, cat ->
        var res = list
        if (query.isNotEmpty()) {
            res = res.filter { it.title.contains(query, ignoreCase = true) || it.teacherName.contains(query, ignoreCase = true) }
        }
        res = when (cat) {
            "Featured" -> res.filter { it.category == "featured" }
            "Popular" -> res.filter { it.category == "popular" }
            "Free" -> res.filter { it.isFree || it.category == "free" }
            "New Batches" -> res.filter { it.category == "new_batch" }
            "My Favorites" -> {
                // filter by user favorites list
                val favIds = allFavorites.value.map { it.courseId }
                res.filter { favIds.contains(it.id) }
            }
            else -> res
        }
        res
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cache of active subjects, chapters, teachers loaded on demand
    private val _teachersList = MutableStateFlow<List<TeacherEntity>>(emptyList())
    val teachersList = _teachersList.asStateFlow()

    private val _activeSubjects = MutableStateFlow<List<SubjectEntity>>(emptyList())
    val activeSubjects = _activeSubjects.asStateFlow()

    private val _activeChapters = MutableStateFlow<List<ChapterEntity>>(emptyList())
    val activeChapters = _activeChapters.asStateFlow()

    private val _selectedCourse = MutableStateFlow<CourseEntity?>(null)
    val selectedCourse = _selectedCourse.asStateFlow()

    private val _selectedSubject = MutableStateFlow<SubjectEntity?>(null)
    val selectedSubject = _selectedSubject.asStateFlow()

    private val _selectedChapter = MutableStateFlow<ChapterEntity?>(null)
    val selectedChapter = _selectedChapter.asStateFlow()

    private val _isCurrentCoursePurchased = MutableStateFlow(false)
    val isCurrentCoursePurchased = _isCurrentCoursePurchased.asStateFlow()

    init {
        viewModelScope.launch {
            // Seeding checks
            repository.checkAndSeedDatabase()
            // Pull teachers list
            _teachersList.value = repository.getAllTeachers()
        }
    }

    // ======= SEARCH CONTROLS =======
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // ======= AUTH CONTROLS =======
    fun loginWithMobile(mobile: String, name: String, email: String) {
        repository.loginWithMobile(mobile, name, email)
        syncSessionStates()
    }

    fun loginWithGoogle(email: String, name: String) {
        repository.loginWithGoogle(email, name)
        syncSessionStates()
    }

    fun loginAsAdmin() {
        repository.loginAsAdmin()
        syncSessionStates()
    }

    fun registerWithEmailAndPassword(
        email: String, 
        password: String, 
        name: String, 
        mobile: String, 
        onSuccess: () -> Unit, 
        onError: (String) -> Unit
    ) {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail.isEmpty() || password.isEmpty() || name.isEmpty() || mobile.isEmpty()) {
            onError("All fields are required!")
            return
        }
        if (password.length < 6) {
            onError("Password must be at least 6 characters!")
            return
        }
        val registered = repository.registerWithEmailAndPassword(cleanEmail, password, name, mobile)
        if (registered) {
            val loggedIn = repository.loginWithEmailAndPassword(cleanEmail, password)
            if (loggedIn) {
                syncSessionStates()
                onSuccess()
            } else {
                onError("Error logging in after registration!")
            }
        } else {
            onError("This email address is already registered!")
        }
    }

    fun loginWithEmailAndPassword(
        email: String, 
        password: String, 
        onSuccess: () -> Unit, 
        onError: (String) -> Unit
    ) {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            onError("Email and Password are required!")
            return
        }
        val success = repository.loginWithEmailAndPassword(cleanEmail, password)
        if (success) {
            syncSessionStates()
            onSuccess()
        } else {
            onError("Invalid Email or Password!")
        }
    }

    fun updateProfile(name: String, email: String, mobile: String) {
        repository.updateProfile(name, email, mobile)
        syncSessionStates()
    }

    fun logout() {
        repository.logout()
        syncSessionStates()
    }

    private fun syncSessionStates() {
        _isLoggedIn.value = repository.isLoggedIn()
        _userName.value = repository.getUserName()
        _userEmail.value = repository.getUserEmail()
        _userMobile.value = repository.getUserMobile()
        _isAdmin.value = repository.isAdminLoggedIn()
        _userId.value = repository.getUserId()
    }

    // ======= DETAIL VIEW LOADERS =======
    fun loadCourseDetails(courseId: String) {
        viewModelScope.launch {
            val course = repository.getCourseById(courseId)
            _selectedCourse.value = course
            _isCurrentCoursePurchased.value = repository.isCoursePurchased(courseId)
            if (course != null) {
                val subs = repository.getSubjectsForCourse(courseId)
                _activeSubjects.value = subs
            }
        }
    }

    fun loadSubjectDetails(subjectId: String) {
        viewModelScope.launch {
            val sub = _activeSubjects.value.find { it.id == subjectId }
            _selectedSubject.value = sub
            val chaps = repository.getChaptersForSubject(subjectId)
            _activeChapters.value = chaps
        }
    }

    fun loadChapterDetails(chapterId: String) {
        _selectedChapter.value = _activeChapters.value.find { it.id == chapterId }
    }

    // ======= FAVORITES =======
    fun isFavoriteCourse(courseId: String): Boolean {
        return allFavorites.value.any { it.courseId == courseId }
    }

    fun toggleFavorite(courseId: String) {
        viewModelScope.launch {
            if (isFavoriteCourse(courseId)) {
                repository.removeFavorite(courseId)
            } else {
                repository.addFavorite(courseId)
            }
        }
    }

    // ======= PURCHASE LOGIC =======
    fun processPurchase(courseId: String, amountPaid: Double, paymentMode: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.purchaseCourse(courseId, amountPaid, paymentMode)
            _isCurrentCoursePurchased.value = true
            onSuccess()
        }
    }

    // ======= ADMIN CONTROLS =======
    fun addCourse(
        id: String,
        title: String,
        teacher: String,
        price: Double,
        discountPrice: Double,
        rating: Float,
        description: String,
        validity: String,
        totalVideos: Int,
        totalPDFs: Int,
        category: String,
        isFree: Boolean
    ) {
        viewModelScope.launch {
            val newCourse = CourseEntity(
                id = id,
                title = title,
                teacherName = teacher,
                price = price,
                discountPrice = if (isFree) 0.0 else discountPrice,
                rating = rating,
                description = description,
                validity = validity,
                totalVideos = totalVideos,
                totalPDFs = totalPDFs,
                category = category,
                thumbnailRes = when {
                    isFree -> "gk"
                    title.contains("Maths", true) -> "maths"
                    title.contains("Police", true) -> "vardi"
                    else -> "fauji"
                },
                isFree = isFree
            )
            repository.insertCourse(newCourse)

            // Auto seed 2 subjects for new course
            repository.insertSubject(SubjectEntity(id = "sub_${id}_g", courseId = id, name = "General Science & Maths", iconName = "maths"))
            repository.insertSubject(SubjectEntity(id = "sub_${id}_gk", courseId = id, name = "GK & Static Capsule", iconName = "gk"))

            // Seed 1 active sample chapter for each subject
            repository.insertChapter(
                ChapterEntity(
                    id = "chap_${id}_1",
                    subjectId = "sub_${id}_g",
                    name = "Lec 01: Course Orientation & Basics",
                    videoUrl = "https://www.youtube.com/watch?v=F_S4S4h7-r8",
                    pdfUrl1Name = "Orientation Study Planner.pdf",
                    pdfUrl1Url = "https://rojgarwithankit.com/notes_sys/planner.pdf",
                    pdfUrl2Name = "Free Syllabus Blueprint.pdf",
                    pdfUrl2Url = "https://rojgarwithankit.com/notes_sys/blueprint.pdf",
                    dateTimeString = "Just Now",
                    videoDuration = "40:00"
                )
            )

            repository.sendNotification("New Course Alert! 🎓", "Fresh Batch \"$title\" has been launched by $teacher. Check it out now!")
        }
    }

    fun modifyCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.updateCourse(course)
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
        }
    }

    fun pushCustomNotification(title: String, body: String) {
        viewModelScope.launch {
            repository.sendNotification(title, body)
        }
    }

    fun addBanner(id: String, title: String, bannerUrl: String, courseId: String?) {
        viewModelScope.launch {
            repository.insertBanner(BannerEntity(id, bannerUrl, title, courseId))
        }
    }

    fun deleteBanner(id: String) {
        viewModelScope.launch {
            repository.deleteBanner(id)
        }
    }

    fun addTeacher(id: String, name: String, subject: String, qualifications: String) {
        viewModelScope.launch {
            repository.insertTeacher(TeacherEntity(id, name, subject, qualifications))
            // refresh
            _teachersList.value = repository.getAllTeachers()
        }
    }

    fun deleteTeacher(id: String) {
        viewModelScope.launch {
            repository.deleteTeacher(id)
            // refresh
            _teachersList.value = repository.getAllTeachers()
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearNotifications()
        }
    }
}
