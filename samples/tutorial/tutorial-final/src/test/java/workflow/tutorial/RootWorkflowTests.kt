package workflow.tutorial

import com.squareup.workflow1.WorkflowOutput
import com.squareup.workflow1.testing.expectWorkflow
import com.squareup.workflow1.testing.launchForTestingFromStartWith
import com.squareup.workflow1.testing.testRender
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.backstack.BackStackScreen
import workflow.tutorial.RootWorkflow.State.Todo
import workflow.tutorial.RootWorkflow.State.Welcome
import workflow.tutorial.WelcomeWorkflow.LoggedIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(WorkflowUiExperimentalApi::class)
class RootWorkflowTests {

  // region Render

  @Test fun `welcome rendering`() {
    RootWorkflow
        // Start in the Welcome state
        .testRender(initialState = Welcome, props = Unit)
        // The `WelcomeWorkflow` is expected to be started in this render.
        .expectWorkflow(
            workflowType = WelcomeWorkflow::class,
            rendering = WelcomeScreen(
                name = "Ada",
                onNameChanged = {},
                onLoginTapped = {}
            )
        )
        // Now, validate that there is a single item in the BackStackScreen, which is our welcome
        // screen.
        .render { rendering ->
          val backstack = (rendering as BackStackScreen<*>).frames
          assertEquals(1, backstack.size)

          val welcomeScreen = backstack[0] as WelcomeScreen
          assertEquals("Ada", welcomeScreen.name)
        }
        // Assert that no action was produced during this render, meaning our state remains unchanged
        .verifyActionResult { _, output ->
          assertNull(output)
        }
  }

  @Test fun `login event`() {
    RootWorkflow
        // Start in the Welcome state
        .testRender(initialState = Welcome, props = Unit)
        // The WelcomeWorkflow is expected to be started in this render.
        .expectWorkflow(
            workflowType = WelcomeWorkflow::class,
            rendering = WelcomeScreen(
                name = "Ada",
                onNameChanged = {},
                onLoginTapped = {}
            ),
            // Simulate the WelcomeWorkflow sending an output of LoggedIn as if the "log in" button
            // was tapped.
            output = WorkflowOutput(LoggedIn(name = "Ada"))
        )
        // Now, validate that there is a single item in the BackStackScreen, which is our welcome
        // screen (prior to the output).
        .render { rendering ->
          val backstack = rendering.frames
          assertEquals(1, backstack.size)

          val welcomeScreen = backstack[0] as WelcomeScreen
          assertEquals("Ada", welcomeScreen.name)
        }
        // Assert that the state transitioned to Todo.
        .verifyActionResult { newState, _ ->
          assertEquals(Todo(name = "Ada"), newState)
        }
  }

  // endregion

  // region Integration

  @Test fun `app flow`() {
    RootWorkflow.launchForTestingFromStartWith {
      // First rendering is just the welcome screen. Update the name.
      awaitNextRendering().let { rendering ->
        assertEquals(1, rendering.frames.size)
        val welcomeScreen = rendering.frames[0] as WelcomeScreen

        // Enter a name.
        welcomeScreen.onNameChanged("Ada")
      }

      // Log in and go to the todo list.
      awaitNextRendering().let { rendering ->
        assertEquals(1, rendering.frames.size)
        val welcomeScreen = rendering.frames[0] as WelcomeScreen

        welcomeScreen.onLoginTapped()
      }

      // Expect the todo list to be rendered. Edit the first todo.
      awaitNextRendering().let { rendering ->
        assertEquals(2, rendering.frames.size)
        assertTrue(rendering.frames[0] is WelcomeScreen)
        val todoScreen = rendering.frames[1] as TodoListScreen
        assertEquals(1, todoScreen.todoTitles.size)

        // Select the first todo.
        todoScreen.onTodoSelected(0)
      }

      // Selected a todo to edit. Expect the todo edit screen.
      awaitNextRendering().let { rendering ->
        assertEquals(3, rendering.frames.size)
        assertTrue(rendering.frames[0] is WelcomeScreen)
        assertTrue(rendering.frames[1] is TodoListScreen)
        val editScreen = rendering.frames[2] as TodoEditScreen

        // Update the title.
        editScreen.onTitleChanged("New Title")
      }

      // Save the selected todo.
      awaitNextRendering().let { rendering ->
        assertEquals(3, rendering.frames.size)
        assertTrue(rendering.frames[0] is WelcomeScreen)
        assertTrue(rendering.frames[1] is TodoListScreen)
        val editScreen = rendering.frames[2] as TodoEditScreen

        // Save the changes by tapping the save button.
        editScreen.saveChanges()
      }

      // Expect the todo list. Validate the title was updated.
      awaitNextRendering().let { rendering ->
        assertEquals(2, rendering.frames.size)
        assertTrue(rendering.frames[0] is WelcomeScreen)
        val todoScreen = rendering.frames[1] as TodoListScreen

        assertEquals(1, todoScreen.todoTitles.size)
        assertEquals("New Title", todoScreen.todoTitles[0])
      }
    }
  }

  // endregion
}
