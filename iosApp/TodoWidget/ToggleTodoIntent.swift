import AppIntents
import WidgetKit

@available(iOS 17.0, *)
struct ToggleTodoIntent: AppIntent {
    static var title: LocalizedStringResource = "Toggle Todo"
    static var description = IntentDescription("Toggles completion status of a task directly from the widget.")

    @Parameter(title: "Todo ID")
    var todoId: String

    init() {}

    init(todoId: String) {
        self.todoId = todoId
    }

    func perform() async throws -> some IntentResult {
        WidgetDataStore.shared.toggle(todoId: todoId)
        WidgetCenter.shared.reloadAllTimelines()
        return .result()
    }
}
