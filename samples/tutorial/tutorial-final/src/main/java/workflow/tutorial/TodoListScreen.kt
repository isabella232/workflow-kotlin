package workflow.tutorial

/**
 *
 * This should contain all data to display in the UI
 *
 * It should also contain callbacks for any UI events, for example:
 * var onButtonTapped: () -> Void
 *
 * It should also return viewControllerDescription property that
 * describes the UIViewController that will be used for rendering
 * the screen.
 */
data class TodoListScreen(
  val name: String,
  val todoTitles: List<String>,
  val onTodoSelected: (Int) -> Unit,
  val onBack: () -> Unit
)
