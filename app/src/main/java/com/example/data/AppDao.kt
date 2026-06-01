package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // ======= COURSES =======
    @Query("SELECT * FROM courses")
    fun getAllCoursesFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: String)

    // ======= SUBJECTS =======
    @Query("SELECT * FROM subjects WHERE courseId = :courseId")
    suspend fun getSubjectsForCourse(courseId: String): List<SubjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE courseId = :courseId")
    suspend fun deleteSubjectsByCourseId(courseId: String)

    // ======= CHAPTERS =======
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId")
    suspend fun getChaptersForSubject(subjectId: String): List<ChapterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteChapterById(chapterId: String)

    // ======= PURCHASES =======
    @Query("SELECT * FROM purchases")
    fun getAllPurchasesFlow(): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE courseId = :courseId")
    suspend fun getPurchaseForCourse(courseId: String): PurchaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: PurchaseEntity)

    // ======= FAVORITES =======
    @Query("SELECT * FROM favorites")
    fun getAllFavoritesFlow(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE courseId = :courseId)")
    fun isFavoriteFlow(courseId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE courseId = :courseId")
    suspend fun deleteFavorite(courseId: String)

    // ======= NOTIFICATIONS =======
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    // ======= BANNERS =======
    @Query("SELECT * FROM banners")
    fun getAllBannersFlow(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity)

    @Query("DELETE FROM banners WHERE id = :bannerId")
    suspend fun deleteBannerById(bannerId: String)

    // ======= TEACHERS =======
    @Query("SELECT * FROM teachers")
    suspend fun getAllTeachers(): List<TeacherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity)

    @Query("DELETE FROM teachers WHERE id = :teacherId")
    suspend fun deleteTeacherById(teacherId: String)
}
