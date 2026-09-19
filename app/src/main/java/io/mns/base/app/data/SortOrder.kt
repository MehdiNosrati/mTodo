package io.mns.base.app.data

enum class SortOrder(val label: String) {
    CREATION_DATE_DESC("Date Created"),
    PRIORITY_DESC("Priority (High to Low)"),
    DUE_DATE_ASC("Due Date (Soonest First)"),
    TITLE_ASC("Alphabetical (A - Z)")
}
