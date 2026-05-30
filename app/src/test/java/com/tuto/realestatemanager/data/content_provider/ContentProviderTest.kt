package com.tuto.realestatemanager.data.content_provider

import android.database.Cursor
import android.net.Uri
import com.tuto.realestatemanager.data.database.PropertyDao
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ContentProviderTest {

    private lateinit var contentProvider: ContentProvider
    lateinit var propertyDao: PropertyDao

    @Before
    fun setUp() {
        propertyDao = mockk()

        contentProvider = ContentProvider().apply {
            this.propertyDao = this@ContentProviderTest.propertyDao
        }
    }

    @Test
    fun `query with properties uri should return all properties cursor`() {
        val fakeCursor: Cursor = mockk()

        every { propertyDao.getAllPropertiesWithCursor() } returns fakeCursor
        every { fakeCursor.setNotificationUri(any(), any()) } just Runs

        val uri = Uri.parse("content://com.tuto.realestatemanager.provider/properties")

        val result = contentProvider.query(uri, null, null, null, null)

        verify { propertyDao.getAllPropertiesWithCursor() }
        verify { fakeCursor.setNotificationUri(any(), uri) }

        assertEquals(fakeCursor, result)
    }

    @Test
    fun `query with property id uri should return property cursor`() {
        val fakeCursor: Cursor = mockk()

        every { propertyDao.getPropertyByIdWithCursor(1L) } returns fakeCursor
        every { fakeCursor.setNotificationUri(any(), any()) } just Runs

        val uri = Uri.parse("content://com.tuto.realestatemanager.provider/properties/1")

        val result = contentProvider.query(uri, null, null, null, null)

        verify { propertyDao.getPropertyByIdWithCursor(1L) }
        verify { fakeCursor.setNotificationUri(any(), uri) }

        assertEquals(fakeCursor, result)
    }

    @Test
    fun `query with photos uri should return all photos cursor`() {
        val fakeCursor: Cursor = mockk()

        every { propertyDao.getAllPhotosWithCursor() } returns fakeCursor
        every { fakeCursor.setNotificationUri(any(), any()) } just Runs

        val uri = Uri.parse("content://com.tuto.realestatemanager.provider/photos")

        val result = contentProvider.query(uri, null, null, null, null)

        verify { propertyDao.getAllPhotosWithCursor() }
        verify { fakeCursor.setNotificationUri(any(), uri) }

        assertEquals(fakeCursor, result)
    }

}