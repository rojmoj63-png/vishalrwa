package com.example.ui

object Screen {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val CourseDetails = "course_details/{courseId}"
    const val Subject = "subject/{courseId}"
    const val Chapter = "chapter/{subjectId}/{courseId}"
    const val Video = "video/{chapterId}/{subjectId}"
    const val PDF = "pdf/{chapterId}/{pdfIndex}" // pdfIndex: '1' or '2'
    const val Profile = "profile"
    const val Payment = "payment/{courseId}"
    const val OrderHistory = "order_history"
    const val AdminDashboard = "admin_dashboard"

    fun courseDetailsRoute(courseId: String) = "course_details/$courseId"
    fun subjectRoute(courseId: String) = "subject/$courseId"
    fun chapterRoute(subjectId: String, courseId: String) = "chapter/$subjectId/$courseId"
    fun videoRoute(chapterId: String, subjectId: String) = "video/$chapterId/$subjectId"
    fun pdfRoute(chapterId: String, pdfIndex: String) = "pdf/$chapterId/$pdfIndex"
    fun paymentRoute(courseId: String) = "payment/$courseId"
}
