/*
 * Copyright 2020 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.workflow1

import com.squareup.workflow1.WorkflowInterceptor.WorkflowSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * A [WorkflowInterceptor] that just prints all method calls using [log].
 */
@OptIn(ExperimentalWorkflowApi::class)
open class SimpleLoggingWorkflowInterceptor : WorkflowInterceptor {
  override fun onSessionStarted(
    workflowScope: CoroutineScope,
    session: WorkflowSession
  ) {
    invokeSafely("logBeforeMethod") { logBeforeMethod("onInstanceStarted", session) }
    workflowScope.coroutineContext[Job]!!.invokeOnCompletion {
      invokeSafely("logAfterMethod") { logAfterMethod("onInstanceStarted", session) }
    }
  }

  override fun <P, S> onInitialState(
    props: P,
    snapshot: Snapshot?,
    proceed: (P, Snapshot?) -> S,
    session: WorkflowSession
  ): S = logMethod("onInitialState", session) {
    proceed(props, snapshot)
  }

  override fun <P, S> onPropsChanged(
    old: P,
    new: P,
    state: S,
    proceed: (P, P, S) -> S,
    session: WorkflowSession
  ): S = logMethod("onPropsChanged", session) {
    proceed(old, new, state)
  }

  override fun <P, S, O, R> onRender(
    props: P,
    state: S,
    context: BaseRenderContext<P, S, O>,
    proceed: (P, S, BaseRenderContext<P, S, O>) -> R,
    session: WorkflowSession
  ): R = logMethod("onRender", session) {
    proceed(props, state, context)
  }

  override fun <S> onSnapshotState(
    state: S,
    proceed: (S) -> Snapshot?,
    session: WorkflowSession
  ): Snapshot? = logMethod("onSnapshotState", session) {
    proceed(state)
  }

  private inline fun <T> logMethod(
    name: String,
    session: WorkflowSession,
    block: () -> T
  ): T {
    invokeSafely("logBeforeMethod") { logBeforeMethod(name, session) }
    return block().also {
      invokeSafely("logAfterMethod") { logAfterMethod(name, session) }
    }
  }

  /**
   * Executes [function], catching and printing any exceptions it throws without rethrowing them.
   */
  private inline fun invokeSafely(
    name: String,
    function: () -> Unit
  ) {
    try {
      function()
    } catch (e: Throwable) {
      val className =
        this::class.simpleName ?: "anonymous " + SimpleLoggingWorkflowInterceptor::class.simpleName
      logError("$className.$name threw exception:\n$e")
    }
  }

  /**
   * Called with descriptions of every event. Default implementation just calls [log].
   */
  protected open fun logBeforeMethod(
    name: String,
    session: WorkflowSession
  ) {
    log("START| $name($session)")
  }

  /**
   * Called with descriptions of every event. Default implementation just calls [log].
   */
  protected open fun logAfterMethod(
    name: String,
    session: WorkflowSession
  ) {
    log("  END| $name($session)")
  }

  /**
   * Called by [logBeforeMethod] and [logAfterMethod] to display a log message.
   */
  protected open fun log(text: String) {
    println(text)
  }

  protected open fun logError(text: String) = System.err.println(text)
}
