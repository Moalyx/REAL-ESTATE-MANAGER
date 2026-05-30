package com.tuto.realestatemanager.data.content_provider


import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import com.tuto.realestatemanager.data.database.PropertyDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class ContentProvider : ContentProvider() {

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(AUTHORITY, "properties", PROPERTIES)
        uriMatcher.addURI(AUTHORITY, "photos", PHOTOS)
        uriMatcher.addURI(AUTHORITY, "properties/#", PROPERTY_BY_ID)
    }

    companion object {
        private const val AUTHORITY = "com.tuto.realestatemanager.provider"

        private const val TABLE_NAME = "PropertyDatabase"
        private const val MIME_TYPE_PREFIX = "vnd.android.cursor.dir/vnd."

        private const val PROPERTIES = 1
        private const val PHOTOS = 2
        private const val PROPERTY_BY_ID = 3
    }

    private lateinit var propertyDao: PropertyDao

    override fun onCreate(): Boolean {

        val appContext = context?.applicationContext
            ?: throw IllegalStateException("Application context is null")

        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            ContentProviderEntryPoint::class.java
        )

        propertyDao = hiltEntryPoint.getPropertyDao()

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {

        val match = uriMatcher.match(uri)

        return try {

            when (match) {

                PROPERTIES ->
                    propertyDao.getAllPropertiesWithCursor()

                PHOTOS ->
                    propertyDao.getAllPhotosWithCursor()

                PROPERTY_BY_ID ->
                    propertyDao.getPropertyByIdWithCursor(
                        uri.lastPathSegment!!.toLong()
                    )

                else ->
                    throw IllegalArgumentException("Unknown URI: $uri")
            }.apply {
                setNotificationUri(context?.contentResolver, uri)
            }

        } catch (e: SQLiteException) {

            Log.e(
                "ContentProvider",
                "Error querying URI: $uri",
                e
            )

            MatrixCursor(arrayOf("error_message")).apply {
                addRow(
                    arrayOf(
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    override fun getType(uri: Uri): String {

        return when (uriMatcher.match(uri)) {

            PROPERTIES ->
                "$MIME_TYPE_PREFIX$AUTHORITY.$TABLE_NAME"

            PHOTOS ->
                "$MIME_TYPE_PREFIX$AUTHORITY.$TABLE_NAME"

            PROPERTY_BY_ID ->
                "$MIME_TYPE_PREFIX$AUTHORITY.$TABLE_NAME"

            else ->
                throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? = null

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ContentProviderEntryPoint {

        fun getPropertyDao(): PropertyDao
    }
}