package io.mns.base.app.ui.tools

import org.junit.Assert.*
import org.junit.Test

class ToolsTest {

    @Test
    fun verifyAll100ToolsCountAndUniqueness() {
        val tools = ALL_100_TOOLS
        assertEquals("Total number of functional tools must be exactly 100", 100, tools.size)

        val ids = tools.map { it.id }
        val uniqueIds = ids.toSet()
        assertEquals("All 100 tool IDs must be strictly unique", 100, uniqueIds.size)
        assertEquals("Tool IDs must span from 1 to 100", (1..100).toList(), ids)

        // Verify all 5 categories have exactly 20 tools
        assertEquals(20, tools.count { it.category == ToolCategory.MATH })
        assertEquals(20, tools.count { it.category == ToolCategory.TEXT })
        assertEquals(20, tools.count { it.category == ToolCategory.TIME })
        assertEquals(20, tools.count { it.category == ToolCategory.HARDWARE })
        assertEquals(20, tools.count { it.category == ToolCategory.EVERYDAY })

        // Verify valid metadata on every tool
        tools.forEach { tool ->
            assertTrue("Tool #${tool.id} has empty name", tool.name.isNotBlank())
            assertTrue("Tool #${tool.id} has empty description", tool.description.isNotBlank())
            assertNotNull("Tool #${tool.id} has null icon", tool.icon)
            assertNotNull("Tool #${tool.id} has null content composable", tool.content)
        }
    }
}
