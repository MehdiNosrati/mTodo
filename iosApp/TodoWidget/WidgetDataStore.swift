import Foundation

struct WidgetTodoItem: Identifiable, Codable {
    let id: String
    let title: String
    let isDone: Bool
    let priority: Int // 0: None, 1: Low, 2: Medium, 3: High
    let category: String
}

class WidgetDataStore {
    static let shared = WidgetDataStore()
    private let suiteName = "group.dev.mahdins.mtodo"
    private let key = "widget_todos"

    private var defaults: UserDefaults {
        UserDefaults(suiteName: suiteName) ?? UserDefaults.standard
    }

    private init() {}

    func getTodos() -> [WidgetTodoItem] {
        if let data = defaults.data(forKey: key),
           let items = try? JSONDecoder().decode([WidgetTodoItem].self, from: data) {
            return items
        }
        // Sample default items for initial preview
        return [
            WidgetTodoItem(id: "1", title: "Review product roadmap", isDone: false, priority: 3, category: "Work"),
            WidgetTodoItem(id: "2", title: "Ship mTodo 2.5.0 KMP update", isDone: true, priority: 3, category: "Work"),
            WidgetTodoItem(id: "3", title: "Workout & stretch", isDone: false, priority: 1, category: "Personal")
        ]
    }

    func saveTodos(_ todos: [WidgetTodoItem]) {
        if let data = try? JSONEncoder().encode(todos) {
            defaults.set(data, forKey: key)
        }
    }

    func toggle(todoId: String) {
        var todos = getTodos()
        if let index = todos.firstIndex(where: { $0.id == todoId }) {
            let item = todos[index]
            todos[index] = WidgetTodoItem(
                id: item.id,
                title: item.title,
                isDone: !item.isDone,
                priority: item.priority,
                category: item.category
            )
            saveTodos(todos)
        }
    }
}
