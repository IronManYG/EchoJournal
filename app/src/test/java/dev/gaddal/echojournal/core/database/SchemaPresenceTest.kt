package dev.gaddal.echojournal.core.database

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SchemaPresenceTest {
    @Test
    fun roomSchemaDirectoryHasJson() {
        val dir = File("schemas")
        assertTrue(
            "[DEBUG_LOG] Expected Room schema directory to exist at ${dir.absolutePath}",
            dir.exists() && dir.isDirectory
        )
        val count =
            dir.walkTopDown().count { it.isFile && it.extension.equals("json", ignoreCase = true) }
        assertTrue(
            "[DEBUG_LOG] Expected at least 1 Room schema JSON under ${dir.absolutePath}",
            count > 0
        )
    }
}
