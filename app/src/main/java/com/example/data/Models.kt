package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val teacherName: String,
    val price: Double,
    val discountPrice: Double,
    val rating: Float,
    val description: String,
    val validity: String,
    val totalVideos: Int,
    val totalPDFs: Int,
    val category: String, // "featured", "popular", "free", "new_batch"
    val thumbnailRes: String, // String representation of dynamic or placeholder icons/tint
    val isFree: Boolean = false
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val name: String,
    val iconName: String // "hindi", "maths", "english", etc.
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val name: String,
    val videoUrl: String,
    val pdfUrl1Name: String,
    val pdfUrl1Url: String,
    val pdfUrl2Name: String,
    val pdfUrl2Url: String,
    val dateTimeString: String,
    val videoDuration: String = "45:00"
)

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey val transactionId: String,
    val courseId: String,
    val purchaseDate: Long,
    val pricePaid: Double,
    val paymentMode: String, // "UPI", "Card", "NetBanking"
    val orderId: String,
    val invoiceNo: String
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val courseId: String,
    val userId: String = "default_user_123"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val dateString: String,
    val isRead: Boolean = false
)

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: String,
    val bannerUrl: String,
    val title: String,
    val actionCourseId: String? = null
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val name: String,
    val subject: String,
    val qualifications: String,
    val ratingString: String = "4.9 ★",
    val experience: String = "8+ Years"
)

data class CourseProgress(
    val courseId: String,
    val completedVideosCount: Int,
    val totalVideosCount: Int,
    val percentage: Float
)
