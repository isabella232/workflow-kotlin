@file:OptIn(WorkflowUiExperimentalApi::class)

package workflow.tutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowRunner.Config
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.setContentWorkflow

private val viewRegistry = ViewRegistry(WelcomeLayoutRunner)

class WelcomeActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentWorkflow(viewRegistry) {
      Config(WelcomeWorkflow, Unit)
    }
  }
}
