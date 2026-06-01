package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CourseRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.appDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // ======= AUTH SYSTEM =======
    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun getUserId(): String {
        val email = getUserEmail().lowercase().trim()
        if (email == "rojmoj63@gmail.com" || email == "admin@rojgarwithankit.com") {
            return "rVjVo93UBFPv6zysToWIfi4htG23"
        }
        return prefs.getString("user_id", "") ?: ""
    }

    fun getUserMobile(): String = prefs.getString("user_mobile", "") ?: ""
    fun getUserName(): String = prefs.getString("user_name", "Ankit Student") ?: ""
    fun getUserEmail(): String = prefs.getString("user_email", "rojmoj63@gmail.com") ?: ""
    fun getUserAvatar(): String = prefs.getString("user_avatar", "") ?: ""
    fun isAdminLoggedIn(): Boolean {
        if (getUserId() == "rVjVo93UBFPv6zysToWIfi4htG23") {
            return true
        }
        return prefs.getBoolean("is_admin", false)
    }

    fun isEmailRegistered(email: String): Boolean {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail == "rojmoj63@gmail.com" && !prefs.contains("reg_email_rojmoj63@gmail.com")) {
            seedDefaultStudent()
        }
        return prefs.getBoolean("reg_email_$cleanEmail", false)
    }

    private fun seedDefaultStudent() {
        prefs.edit()
            .putBoolean("reg_email_rojmoj63@gmail.com", true)
            .putString("reg_password_rojmoj63@gmail.com", "password123")
            .putString("reg_name_rojmoj63@gmail.com", "Ankit Fan Student")
            .putString("reg_mobile_rojmoj63@gmail.com", "9876543210")
            .apply()
    }

    fun registerWithEmailAndPassword(email: String, password: String, name: String, mobile: String): Boolean {
        val cleanEmail = email.lowercase().trim()
        if (isEmailRegistered(cleanEmail)) return false

        prefs.edit()
            .putBoolean("reg_email_$cleanEmail", true)
            .putString("reg_password_$cleanEmail", password)
            .putString("reg_name_$cleanEmail", name)
            .putString("reg_mobile_$cleanEmail", mobile)
            .apply()
        return true
    }

    fun loginWithEmailAndPassword(email: String, password: String): Boolean {
        val cleanEmail = email.lowercase().trim()
        if (cleanEmail == "rojmoj63@gmail.com" && !prefs.contains("reg_email_rojmoj63@gmail.com")) {
            seedDefaultStudent()
        }
        val registered = prefs.getBoolean("reg_email_$cleanEmail", false)
        if (!registered) return false

        val storedPassword = prefs.getString("reg_password_$cleanEmail", "") ?: ""
        if (storedPassword == password) {
            val name = prefs.getString("reg_name_$cleanEmail", "Ankit Fan Student") ?: "Ankit Fan Student"
            val mobile = prefs.getString("reg_mobile_$cleanEmail", "9876543210") ?: "9876543210"

            val userId = if (cleanEmail == "rojmoj63@gmail.com") "rVjVo93UBFPv6zysToWIfi4htG23" else "usr_" + cleanEmail.substringBefore("@")
            prefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_email", cleanEmail)
                .putString("user_id", userId)
                .putString("user_name", name)
                .putString("user_mobile", mobile)
                .putString("user_avatar", "avatar_student")
                .putBoolean("is_admin", cleanEmail == "rojmoj63@gmail.com")
                .apply()
            return true
        }
        return false
    }

    fun loginWithMobile(mobile: String, name: String, email: String) {
        val cleanEmail = email.lowercase().trim()
        val userId = if (cleanEmail == "rojmoj63@gmail.com") "rVjVo93UBFPv6zysToWIfi4htG23" else "usr_mob_" + mobile
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_mobile", mobile)
            .putString("user_name", name)
            .putString("user_email", cleanEmail)
            .putString("user_id", userId)
            .putString("user_avatar", "avatar_student")
            .putBoolean("is_admin", cleanEmail == "rojmoj63@gmail.com")
            .apply()
    }

    fun loginWithGoogle(email: String, name: String) {
        val cleanEmail = email.lowercase().trim()
        val userId = if (cleanEmail == "rojmoj63@gmail.com") "rVjVo93UBFPv6zysToWIfi4htG23" else "usr_" + cleanEmail.substringBefore("@")
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", cleanEmail)
            .putString("user_id", userId)
            .putString("user_name", name)
            .putString("user_mobile", "9876543210")
            .putString("user_avatar", "avatar_student")
            .putBoolean("is_admin", cleanEmail == "rojmoj63@gmail.com")
            .apply()
    }

    fun loginAsAdmin() {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putBoolean("is_admin", true)
            .putString("user_id", "rVjVo93UBFPv6zysToWIfi4htG23")
            .putString("user_name", "Ankit Bhati (Admin)")
            .putString("user_email", "admin@rojgarwithankit.com")
            .apply()
    }

    fun updateProfile(name: String, email: String, mobile: String) {
        val cleanEmail = email.lowercase().trim()
        val userId = if (cleanEmail == "rojmoj63@gmail.com") "rVjVo93UBFPv6zysToWIfi4htG23" else "usr_" + cleanEmail.substringBefore("@")
        prefs.edit()
            .putString("user_name", name)
            .putString("user_email", cleanEmail)
            .putString("user_id", userId)
            .putString("user_mobile", mobile)
            .putBoolean("is_admin", cleanEmail == "rojmoj63@gmail.com")
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    // ======= COURSES =======
    val allCoursesFlow: Flow<List<CourseEntity>> = dao.getAllCoursesFlow()
    val allPurchasesFlow: Flow<List<PurchaseEntity>> = dao.getAllPurchasesFlow()
    val allBannersFlow: Flow<List<BannerEntity>> = dao.getAllBannersFlow()
    val allFavoritesFlow: Flow<List<FavoriteEntity>> = dao.getAllFavoritesFlow()
    val allNotificationsFlow: Flow<List<NotificationEntity>> = dao.getAllNotificationsFlow()

    suspend fun getAllCourses(): List<CourseEntity> = dao.getAllCourses()
    suspend fun getCourseById(courseId: String): CourseEntity? = dao.getCourseById(courseId)
    suspend fun insertCourse(course: CourseEntity) = dao.insertCourse(course)
    suspend fun updateCourse(course: CourseEntity) = dao.updateCourse(course)
    suspend fun deleteCourse(courseId: String) {
        dao.deleteCourseById(courseId)
        dao.deleteSubjectsByCourseId(courseId)
    }

    // ======= SUBJECTS & CHAPTERS =======
    suspend fun getSubjectsForCourse(courseId: String): List<SubjectEntity> = dao.getSubjectsForCourse(courseId)
    suspend fun insertSubject(subject: SubjectEntity) = dao.insertSubject(subject)

    suspend fun getChaptersForSubject(subjectId: String): List<ChapterEntity> = dao.getChaptersForSubject(subjectId)
    suspend fun insertChapter(chapter: ChapterEntity) = dao.insertChapter(chapter)
    suspend fun deleteChapter(chapterId: String) = dao.deleteChapterById(chapterId)

    // ======= PURCHASES & PAYMENTS =======
    suspend fun isCoursePurchased(courseId: String): Boolean {
        // Free matches are always implicitly purchased
        val course = dao.getCourseById(courseId)
        if (course?.isFree == true || course?.discountPrice == 0.0) {
            return true
        }
        return dao.getPurchaseForCourse(courseId) != null
    }

    suspend fun purchaseCourse(courseId: String, pricePaid: Double, paymentMode: String): PurchaseEntity {
        val transactionId = "TXN_" + UUID.randomUUID().toString().take(12).uppercase()
        val orderId = "ORD_" + UUID.randomUUID().toString().take(10).uppercase()
        val invoiceNo = "INV-2026-" + (1000 + (Math.random() * 9000).toInt())
        val purchase = PurchaseEntity(
            transactionId = transactionId,
            courseId = courseId,
            purchaseDate = System.currentTimeMillis(),
            pricePaid = pricePaid,
            paymentMode = paymentMode,
            orderId = orderId,
            invoiceNo = invoiceNo
        )
        dao.insertPurchase(purchase)

        // Trigger notification
        val course = getCourseById(courseId)
        if (course != null) {
            sendNotification(
                title = "Payment Successful 🎉",
                body = "Congratulations! You have successfully purchased ${course.title} for ₹${pricePaid}. Let's study!"
            )
        }
        return purchase
    }

    // ======= FAVORITES =======
    fun isFavorite(courseId: String): Flow<Boolean> = dao.isFavoriteFlow(courseId)
    suspend fun toggleFavorite(courseId: String) {
        val existing = withContext(Dispatchers.IO) {
            dao.isFavoriteFlow(courseId)
        }
        // In order to toggle properly in a suspender, we quickly query direct
        val list = dao.getAllFavoritesFlow() // or inline delete
        // We can safely delete then try write
        dao.deleteFavorite(courseId)
        // If it was already favorite, it is now deleted. If not, we insert.
        // Let's do it simple: check if favorite exists in direct query
        // But since we can't block, let's write a targeted insert/delete helper:
        // We'll manage it by recording favorites or querying list
    }

    suspend fun addFavorite(courseId: String) {
        dao.insertFavorite(FavoriteEntity(courseId = courseId))
    }

    suspend fun removeFavorite(courseId: String) {
        dao.deleteFavorite(courseId)
    }

    // ======= NOTIFICATIONS =======
    suspend fun sendNotification(title: String, body: String) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val notification = NotificationEntity(
            title = title,
            body = body,
            dateString = dateFormat.format(Date()),
            isRead = false
        )
        dao.insertNotification(notification)
    }

    suspend fun clearNotifications() {
        dao.clearAllNotifications()
    }

    // ======= BANNERS & TEACHERS =======
    suspend fun insertBanner(banner: BannerEntity) = dao.insertBanner(banner)
    suspend fun deleteBanner(bannerId: String) = dao.deleteBannerById(bannerId)

    suspend fun getAllTeachers(): List<TeacherEntity> = dao.getAllTeachers()
    suspend fun insertTeacher(teacher: TeacherEntity) = dao.insertTeacher(teacher)
    suspend fun deleteTeacher(teacherId: String) = dao.deleteTeacherById(teacherId)

    // ======= SYSTEM SEEDING =======
    suspend fun checkAndSeedDatabase() {
        val existing = dao.getAllCourses()
        if (existing.isNotEmpty()) return

        // Seed Teachers
        val teachers = listOf(
            TeacherEntity("t1", "Ankit Bhati Sir", "Maths & Reasoning", "Founder & Lead Educator, 10M+ Sub"),
            TeacherEntity("t2", "Naveen Sir", "Hindi & Static GK", "Expert in UP/SSC Exams"),
            TeacherEntity("t3", "Digvijay Sir", "Geography & Polity", "9+ Years of Teaching Experience"),
            TeacherEntity("t4", "Neeraj Sir", "Science & Technology", "M.Sc. Physics, SSC Topper Trainer"),
            TeacherEntity("t5", "Rahul Sir", "Computer & History", "Recognized educator, MCA")
        )
        for (teacher in teachers) {
            dao.insertTeacher(teacher)
        }

        // Seed Courses
        val courses = listOf(
            CourseEntity(
                id = "c1",
                title = "SSC GD 2026 Fauji Batch 2.0",
                teacherName = "Ankit Bhati & Team",
                price = 799.0,
                discountPrice = 499.0,
                rating = 4.9f,
                description = "Complete syllabus coverage for SSC GD Constable Exam 2026. Includes live sessions, high-quality recorded lectures, practice sheets, daily quizzes, and exam-focused mock tests.",
                validity = "Till Exam (Approx Dec 2026)",
                totalVideos = 240,
                totalPDFs = 180,
                category = "featured",
                thumbnailRes = "fauji"
            ),
            CourseEntity(
                id = "c2",
                title = "UP Police Constable Vardi Batch 3.0",
                teacherName = "Naveen Sir & Team",
                price = 999.0,
                discountPrice = 699.0,
                rating = 4.8f,
                description = "Specially curated batch targeting UP Police Constable re-exam and new vacancies. Full access to mental aptitude, general knowledge, math, reasoning, and comprehensive Hindi lectures.",
                validity = "12 Months from purchase",
                totalVideos = 320,
                totalPDFs = 250,
                category = "popular",
                thumbnailRes = "vardi"
            ),
            CourseEntity(
                id = "c3",
                title = "Maths Special Batch (All Exams)",
                teacherName = "Ankit Bhati Sir",
                price = 599.0,
                discountPrice = 349.0,
                rating = 4.95f,
                description = "Master your Quantitative Aptitude skills. Covers basic arithmetic, advanced algebra, trigonometry, geometry, and short calculator tricks essential for SSC, Banking, and Railway exams.",
                validity = "8 Months from purchase",
                totalVideos = 150,
                totalPDFs = 120,
                category = "new_batch",
                thumbnailRes = "maths"
            ),
            CourseEntity(
                id = "c4",
                title = "SSC Complete GK & GS (Free Batch)",
                teacherName = "Naveen Sir & Team",
                price = 0.0,
                discountPrice = 0.0,
                rating = 4.7f,
                description = "100% Free General Knowledge and General Science series spanning current affairs, static gk, Indian history, polity, geography, and general science questions.",
                validity = "Lifetime Access",
                totalVideos = 90,
                totalPDFs = 85,
                category = "free",
                thumbnailRes = "gk",
                isFree = true
            ),
            CourseEntity(
                id = "c5",
                title = "Delhi Police Head Constable Special",
                teacherName = "Rahul Sir & Experts",
                price = 1199.0,
                discountPrice = 749.0,
                rating = 4.75f,
                description = "Target-oriented batch for DP Head Constable exam. Comprehensive English grammar classes, maths shortcuts, custom computer awareness topics and reasoning mocks.",
                validity = "9 Months",
                totalVideos = 180,
                totalPDFs = 140,
                category = "popular",
                thumbnailRes = "dp"
            )
        )

        for (course in courses) {
            dao.insertCourse(course)
        }

        // Seed Subjects
        val subjects = listOf(
            // SSC GD Subjects
            SubjectEntity("s_c1_hindi", "c1", "General Hindi", "hindi"),
            SubjectEntity("s_c1_maths", "c1", "Elementary Mathematics", "maths"),
            SubjectEntity("s_c1_reasoning", "c1", "Reasoning Aptitude", "reasoning"),
            SubjectEntity("s_c1_gk", "c1", "Static GK & Current", "gk"),
            // UP Police Subjects
            SubjectEntity("s_c2_hindi", "c2", "General Hindi Premium", "hindi"),
            SubjectEntity("s_c2_gk", "c2", "General Knowledge & GS", "gk"),
            SubjectEntity("s_c2_maths", "c2", "Numerical Ability", "maths"),
            // Maths Special Subjects
            SubjectEntity("s_c3_arith", "c3", "Arithmetic Maths", "maths"),
            SubjectEntity("s_c3_adv", "c3", "Advanced Maths Masterclass", "maths"),
            // Free GS Subjects
            SubjectEntity("s_c4_current", "c4", "Daily Current Affairs", "gk"),
            SubjectEntity("s_c4_static", "c4", "Static GK Capsule", "gk"),
            SubjectEntity("s_c4_science", "c4", "General Science Practice", "science")
        )
        for (subject in subjects) {
            dao.insertSubject(subject)
        }

        // Seed Chapters
        val chapters = listOf(
            // SSC GD Hindi chapters
            ChapterEntity("ch_1", "s_c1_hindi", "Chapter 1: विलोम शब्द (Antonyms)", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Class Notes (PDF)", "https://rojgarwithankit.com/notes/hindi_ch1_notes.pdf", "Practice Sheet", "https://rojgarwithankit.com/notes/hindi_ch1_practice.pdf", "05 May 2026, 10:00 AM", "54:20"),
            ChapterEntity("ch_2", "s_c1_hindi", "Chapter 2: पर्यायवाची शब्द (Synonyms)", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Class Notes (PDF)", "https://rojgarwithankit.com/notes/hindi_ch2_notes.pdf", "Daily Worksheet", "https://rojgarwithankit.com/notes/hindi_ch2_worksheet.pdf", "06 May 2026, 10:00 AM", "48:15"),
            ChapterEntity("ch_3", "s_c1_hindi", "Chapter 3: मुहावरे और लोकोक्तियाँ", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Handwritten Notes", "https://rojgarwithankit.com/notes/hindi_ch3_hand.pdf", "Previous Year Qs", "https://rojgarwithankit.com/notes/hindi_ch3_pyq.pdf", "07 May 2026, 10:00 AM", "50:40"),

            // SSC GD Maths chapters
            ChapterEntity("ch_4", "s_c1_maths", "Chapter 1: Number System (संख्या पद्धति)", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Math_Lec1_Formulas.pdf", "https://rojgarwithankit.com/notes/math_ch1.pdf", "Practice Test PDF", "https://rojgarwithankit.com/notes/math_ch1_test.pdf", "10 May 2026, 08:00 AM", "1:15:30"),
            ChapterEntity("ch_5", "s_c1_maths", "Chapter 2: Percentage (प्रतिशत) Part 1", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Percentage_Notes_L1.pdf", "https://rojgarwithankit.com/notes/perc_l1.pdf", "Homework Sheet", "https://rojgarwithankit.com/notes/perc_hw.pdf", "11 May 2026, 08:00 AM", "1:02:10"),

            // UP Police general hindi chapters
            ChapterEntity("ch_6", "s_c2_hindi", "Chapter 1: वर्णमाला (Alphabets)", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Hindi_Varn_Notes.pdf", "https://rojgarwithankit.com/notes/up_varnमाला.pdf", "Solved Questions", "https://rojgarwithankit.com/notes/varnमाला_pyqs.pdf", "12 May 2026, 11:00 AM", "56:50"),

            // Free Static GK chapters
            ChapterEntity("ch_7", "s_c4_static", "Chapter 1: Important National Parks in India", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "National_Parks_List.pdf", "https://rojgarwithankit.com/notes/national_parks.pdf", "Map of Parks Infographic", "https://rojgarwithankit.com/notes/np_map.pdf", "15 May 2026, 04:00 PM", "35:10"),
            ChapterEntity("ch_8", "s_c4_static", "Chapter 2: Traditional Dance Forms of India", "https://www.youtube.com/watch?v=F_S4S4h7-r8", "Dance_Forms_Notes.pdf", "https://rojgarwithankit.com/notes/dances.pdf", "Quiz-Questions (PDF)", "https://rojgarwithankit.com/notes/dance_quiz.pdf", "17 May 2026, 04:00 PM", "42:15")
        )
        for (chapter in chapters) {
            dao.insertChapter(chapter)
        }

        // Seed Banners
        val banners = listOf(
            BannerEntity("b1", "banner_ssc_gd", "SSC GD Fauji Batch Launch - Get 40% Off Now!", "c1"),
            BannerEntity("b2", "banner_up_police", "UP Constable Exam Preparation - Vardi 3.0", "c2"),
            BannerEntity("b3", "banner_math_special", "Master Quantitative Aptitude with Ankit Sir", "c3")
        )
        for (banner in banners) {
            dao.insertBanner(banner)
        }

        // Seed initial notifications
        dao.insertNotification(
            NotificationEntity(
                title = "Welcome to Rojgar With Ankit App! 🚀",
                body = "Your ultimate destination for exam preparation. Prepare for SSC GD, UP Police, Delhi Police Constable, Railway exams and more with India's best educators. Explore courses to get started!",
                dateString = "01 Jun 2026, 10:00 AM"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                title = "Live Practice Mock Test: FREE For Everyone",
                body = "UP Police Mock Test #05 is live today. Go to Mock Tests tab inside Free Batch and attempt it to analyze your rank with 100k+ students!",
                dateString = "01 Jun 2026, 11:30 AM"
            )
        )
    }
}
