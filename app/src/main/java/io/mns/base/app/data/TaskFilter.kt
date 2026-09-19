package io.mns.base.app.data

sealed interface TaskFilter {
    val id: String
    val label: String

    object All : TaskFilter {
        override val id: String = "all"
        override val label: String = "All"
    }

    data class ByPriority(val priority: Priority) : TaskFilter {
        override val id: String = "priority_${priority.name}"
        override val label: String = "${priority.label} Priority"
    }

    data class ByTag(val tag: String) : TaskFilter {
        override val id: String = "tag_$tag"
        override val label: String = "#$tag"
    }

    object Overdue : TaskFilter {
        override val id: String = "overdue"
        override val label: String = "Overdue"
    }
}
