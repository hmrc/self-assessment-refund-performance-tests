/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.perftests.example

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object Requests extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("self-assessment-refund-frontend")
  val baseUrlAuth: String = baseUrlFor("auth-login-stub")
  val route: String   = "/self-assessment-refund"
  val routeAuth: String = "/auth-login-stub"

  val navigateToStartPage: HttpRequestBuilder =
    http("Navigate to Start Page")
      .get(s"$baseUrl$route/test-only/start-journey")
      .check(status.is(200))
    //  .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postRefundPreset: HttpRequestBuilder =
    http("Post Refund Preset")
      .post(s"$baseUrl$route/test-only/start-journey/select-preset": String)
      .formParam("index", "0")
      .check(status.is(303))
      .check(header("Location").is(s"$route/test-only/start-journey?type=StartRefund&nino=AB111111C&fullAmount=987.65&lastPaymentMethod=CARD&primeStubs=IfNotExists").saveAs("startRefundPresetPage"))

  val getStartPageRefundPreset: HttpRequestBuilder =
    http("Get Start Page with Refund Preset")
      .get(s"$baseUrl$${startRefundPresetPage}": String)
      .check(status.is(200))

  val postStartJourneyRefundPreset: HttpRequestBuilder =
    http("Post Start Page with Refund Preset")
      .post(s"$baseUrl$route/test-only/start-journey": String)
      .formParam("type", "StartRefund")
      .formParam("nino", "AB111111C")
      .formParam("fullAmount", "987.65")
      .formParam("lastPaymentMethod", "CARD")
      .formParam("primeStubs", "IfNotExists")
      .check(status.is(303))
      .check(header("Location").is(s"$baseUrlAuth$routeAuth/gg-sign-in?continue=https%3A%2F%2Fwww.staging.tax.service.gov.uk%2Fself-assessment-refund%2Ftest-only%2Fstart-journey%3Ftype%3DStartRefund%26nino%3DAB111111C%26fullAmount%3D987.65%26lastPaymentMethod%3DCARD%26primeStubs%3DIfNotExists").saveAs("authLoginPage"))

  val getAuthLogin: HttpRequestBuilder =
    http("Get Auth Login")
      .get(s"$${authLoginPage}": String)
      .check(status.is(200))

}
