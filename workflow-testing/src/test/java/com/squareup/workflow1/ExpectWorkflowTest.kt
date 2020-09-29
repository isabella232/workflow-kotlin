package com.squareup.workflow1

import com.squareup.workflow1.testing.expectWorkflow
import com.squareup.workflow1.testing.testRender
import org.junit.Test

class ExpectWorkflowTest {

  @Test fun stuff() {
    val workflow = Workflow.rendering(Unit)
    workflow.testRender(Unit)
        .expectWorkflow(TestWorkflow::class, )
  }

  private interface  TestWorkflow:Workflow<Unit,Nothing,Unit>
}
