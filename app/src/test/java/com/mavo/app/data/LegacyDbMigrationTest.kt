package com.mavo.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class LegacyDbMigrationTest {
    private fun tempDir(): File = Files.createTempDirectory("mavo-mig").toFile()

    @Test
    fun migratesLegacyDbAndRetiresOldFiles() {
        val dir = tempDir()
        File(dir, "ZeroBook.db").writeText("data")
        File(dir, "ZeroBook.db-wal").writeText("wal")

        migrateLegacyDbFiles(dir)

        assertEquals("data", File(dir, "Mavo.db").readText())
        assertEquals("wal", File(dir, "Mavo.db-wal").readText())
        assertFalse(File(dir, "ZeroBook.db").exists())
        assertFalse(File(dir, "ZeroBook.db-wal").exists())
    }

    @Test
    fun neverOverwritesAnExistingDb() {
        val dir = tempDir()
        File(dir, "Mavo.db").writeText("current")
        File(dir, "ZeroBook.db").writeText("old")

        migrateLegacyDbFiles(dir)

        assertEquals("current", File(dir, "Mavo.db").readText())
        assertTrue(File(dir, "ZeroBook.db").exists())
    }

    @Test
    fun failedCopyCleansPartialTargetAndKeepsLegacyForRetry() {
        val dir = tempDir()
        File(dir, "ZeroBook.db").writeText("data")
        File(dir, "ZeroBook.db-shm").writeText("shm")
        File(dir, "Mavo.db-shm").mkdirs() // blocks the sidecar copy mid-migration

        migrateLegacyDbFiles(dir)

        assertFalse(File(dir, "Mavo.db").exists())
        assertEquals("data", File(dir, "ZeroBook.db").readText())
    }
}
