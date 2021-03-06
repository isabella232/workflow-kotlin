/*
 * Copyright 2019 Square Inc.
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
package com.squareup.workflow1.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

@OptIn(WorkflowUiExperimentalApi::class)
class ViewEnvironmentTest {
  private object StringHint : ViewEnvironmentKey<String>(String::class) {
    override val default = ""
  }

  private object OtherStringHint : ViewEnvironmentKey<String>(String::class) {
    override val default = ""
  }

  private data class DataHint(
    val int: Int = -1,
    val string: String = ""
  ) {
    companion object : ViewEnvironmentKey<DataHint>(DataHint::class) {
      override val default = DataHint()
    }
  }

  private val emptyEnv = ViewEnvironment(ViewRegistry())

  @Test fun defaults() {
    assertThat(emptyEnv[DataHint]).isEqualTo(DataHint())
  }

  @Test fun put() {
    val environment = emptyEnv +
        (StringHint to "fnord") +
        (DataHint to DataHint(42, "foo"))

    assertThat(environment[StringHint]).isEqualTo("fnord")
    assertThat(environment[DataHint]).isEqualTo(DataHint(42, "foo"))
  }

  @Test fun `map equality`() {
    val env1 = emptyEnv +
        (StringHint to "fnord") +
        (DataHint to DataHint(42, "foo"))

    val env2 = emptyEnv +
        (StringHint to "fnord") +
        (DataHint to DataHint(42, "foo"))

    assertThat(env1).isEqualTo(env2)
  }

  @Test fun `map inequality`() {
    val env1 = emptyEnv +
        (StringHint to "fnord") +
        (DataHint to DataHint(42, "foo"))

    val env2 = emptyEnv +
        (StringHint to "fnord") +
        (DataHint to DataHint(43, "foo"))

    assertThat(env1).isNotEqualTo(env2)
  }

  @Test fun `key equality`() {
    assertThat(StringHint).isEqualTo(StringHint)
  }

  @Test fun `key inequality`() {
    assertThat(StringHint).isNotEqualTo(OtherStringHint)
  }

  @Test fun override() {
    val environment = emptyEnv +
        (StringHint to "able") +
        (StringHint to "baker")

    assertThat(environment[StringHint]).isEqualTo("baker")
  }

  @Test fun `keys of the same type`() {
    val environment = emptyEnv +
        (StringHint to "able") +
        (OtherStringHint to "baker")

    assertThat(environment[StringHint]).isEqualTo("able")
    assertThat(environment[OtherStringHint]).isEqualTo("baker")
  }
}
