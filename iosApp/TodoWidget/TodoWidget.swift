import WidgetKit
import SwiftUI
import AppIntents

struct TodoEntry: TimelineEntry {
    let date: Date
    let todos: [WidgetTodoItem]
}

struct TodoWidgetProvider: TimelineProvider {
    func placeholder(in context: Context) -> TodoEntry {
        TodoEntry(date: Date(), todos: WidgetDataStore.shared.getTodos())
    }

    func getSnapshot(in context: Context, completion: @escaping (TodoEntry) -> Void) {
        let entry = TodoEntry(date: Date(), todos: WidgetDataStore.shared.getTodos())
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<TodoEntry>) -> Void) {
        let todos = WidgetDataStore.shared.getTodos()
        let entry = TodoEntry(date: Date(), todos: todos)
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 15, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
}

struct TodoWidgetEntryView: View {
    var entry: TodoEntry
    @Environment(\.widgetFamily) var family

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text("mTodo")
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundStyle(Color(red: 0.39, green: 0.40, blue: 0.95))
                Spacer()
                let remaining = entry.todos.filter { !$0.isDone }.count
                Text("\(remaining) active")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }

            Divider()

            if entry.todos.isEmpty {
                Spacer()
                Text("All tasks completed! 🎉")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                Spacer()
            } else {
                ForEach(entry.todos.prefix(family == .systemSmall ? 2 : 4)) { item in
                    HStack(spacing: 8) {
                        if #available(iOS 17.0, *) {
                            Button(intent: ToggleTodoIntent(todoId: item.id)) {
                                Image(systemName: item.isDone ? "checkmark.circle.fill" : "circle")
                                    .foregroundColor(item.isDone ? .green : .gray)
                                    .font(.system(size: 14))
                            }
                            .buttonStyle(.plain)
                        } else {
                            Image(systemName: item.isDone ? "checkmark.circle.fill" : "circle")
                                .foregroundColor(item.isDone ? .green : .gray)
                                .font(.system(size: 14))
                        }

                        Text(item.title)
                            .font(.system(size: 12, weight: .medium))
                            .strikethrough(item.isDone)
                            .foregroundColor(item.isDone ? .secondary : .primary)
                            .lineLimit(1)

                        Spacer()

                        if item.priority == 3 {
                            Circle()
                                .fill(Color.red)
                                .frame(width: 6, height: 6)
                        } else if item.priority == 2 {
                            Circle()
                                .fill(Color.orange)
                                .frame(width: 6, height: 6)
                        }
                    }
                }
                Spacer(minLength: 0)
            }
        }
        .containerBackground(for: .widget) {
            Color(UIColor.secondarySystemBackground)
        }
    }
}

struct TodoWidget: Widget {
    let kind: String = "TodoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: TodoWidgetProvider()) { entry in
            TodoWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("mTodo")
        .description("Quickly view and complete your daily tasks.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}
