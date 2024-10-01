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

  val baseUrl: String                   = baseUrlFor("self-assessment-refund-frontend")
  val baseUrlAuth: String               = baseUrlFor("auth-login-stub")
  val baseUrlIV: String                 = baseUrlFor("iv-stub")
  val route: String                     = "/self-assessment-refund"
  val routeRefundRequestJourney: String = "/request-a-self-assessment-refund"
  val routeAuth: String                 = "/auth-login-stub"
  val routeIV: String                   = "/iv-stub"

  val getAuthLogin: HttpRequestBuilder =
    http("Get Auth Login")
      .get(s"$baseUrlAuth$routeAuth/gg-sign-in": String)
      .check(status.is(200))

  def postAuthLoginRefund(userType: String): HttpRequestBuilder =
    http("Post Auth Login - Refund Journey Successful")
      .post(s"$baseUrlAuth$routeAuth/gg-sign-in": String)
      .formParam("authorityId", "")
      .formParam(
        "redirectionUrl",
        s"$baseUrl$route/test-only/start-journey?type=StartRefund&nino=AB200111C&fullAmount=987.65&lastPaymentMethod=CARD&primeStubs=IfNotExists"
      )
      .formParam(
        "nino",
        if (userType == "Individual") { "AB200111C" }
        else { "" }
      )
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "250")
      .formParam("affinityGroup", userType)
      .formParam(
        "enrolment[0].name",
        if (userType == "Agent") { "HMRC-MTD-IT" }
        else { "" }
      )
      .formParam(
        "enrolment[0].taxIdentifier[0].name",
        if (userType == "Agent") { "MTDITID" }
        else { "" }
      )
      .formParam(
        "enrolment[0].taxIdentifier[0].value",
        if (userType == "Agent") { "123" }
        else { "" }
      )
      .formParam(
        "enrolment[0].state",
        if (userType == "Agent") { "Activated" }
        else { "" }
      )
      .formParam(
        "delegatedEnrolment[0].key",
        if (userType == "Agent") { "HMRC-MTD-IT" }
        else { "" }
      )
      .formParam(
        "delegatedEnrolment[0].taxIdentifier[0].name",
        if (userType == "Agent") { "MTDITID" }
        else { "" }
      )
      .formParam(
        "delegatedEnrolment[0].taxIdentifier[0].value",
        if (userType == "Agent") { "123" }
        else { "" }
      )
      .formParam(
        "delegatedEnrolment[0].delegatedAuthRule",
        if (userType == "Agent") { "mtd-it-auth" }
        else { "" }
      )
      .check(status.is(303))
      .check(
        header("Location")
          .is(
            s"$baseUrl$route/test-only/start-journey?type=StartRefund&nino=AB200111C&fullAmount=987.65&lastPaymentMethod=CARD&primeStubs=IfNotExists"
          )
          .saveAs("StartJourneyPage")
      )

  val postAuthLoginHistory: HttpRequestBuilder =
    http("Post Auth Login - History")
      .post(s"$baseUrlAuth$routeAuth/gg-sign-in": String)
      .formParam("authorityId", "")
      .formParam(
        "redirectionUrl",
        s"$baseUrl$route/test-only/start-journey?type=ViewHistory&nino=AB111111C&fullAmount=&lastPaymentMethod=BACS&primeStubs=IfNotExists"
      )
      .formParam("nino", "AB111111C")
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "250")
      .formParam("affinityGroup", "Individual")
      .check(status.is(303))
      .check(
        header("Location")
          .is(
            s"$baseUrl$route/test-only/start-journey?type=ViewHistory&nino=AB111111C&fullAmount=&lastPaymentMethod=BACS&primeStubs=IfNotExists"
          )
          .saveAs("StartJourneyPage")
      )

  val getStartPage: HttpRequestBuilder =
    http("Get Start Page")
      .get("${StartJourneyPage}": String)
      .check(status.is(200))

  val postStartPageRefund: HttpRequestBuilder =
    http("Post Start Page - Refund")
      .post("${StartJourneyPage}": String)
      .formParam("type", "StartRefund")
      .formParam("nino", "AB200111C")
      .formParam("fullAmount", "987.65")
      .formParam("lastPaymentMethod", "CARD")
      .formParam("primeStubs", "IfNotExists")
      .check(status.is(303))

  val postStartPageHistory: HttpRequestBuilder =
    http("Post Start Page - History")
      .post("${StartJourneyPage}": String)
      .formParam("type", "ViewHistory")
      .formParam("nino", "AB111111C")
      .formParam("fullAmount", "")
      .formParam("lastPaymentMethod", "BACS")
      .formParam("primeStubs", "IfNotExists")
      .check(status.is(303))

  val getRefundAmount: HttpRequestBuilder =
    http("Get Refund Amount")
      .get(s"$baseUrl$routeRefundRequestJourney/refund-amount": String)
      .check(status.is(303))

  val getRefundHistory: HttpRequestBuilder =
    http("Get Refund History")
      .get(s"$baseUrl$route/refund-history/start": String)
      .check(status.is(303))
      .check(header("Location").is(s"$route/refund-history") saveAs "HistoryPage")

  val getRefundAmountPage: HttpRequestBuilder =
    http("Get Refund Amount Page")
      .get(s"$baseUrl$routeRefundRequestJourney/refund-amount": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  def postRefundAmountPage(userType: String): HttpRequestBuilder =
    http("Post Refund Amount Page")
      .post(s"$baseUrl$routeRefundRequestJourney/refund-amount": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("choice", "partial")
      .formParam("amount", "500")
      .check(status.is(303))
      .check(
        if (userType == "Individual") {
          header("Location")
            .is(routeRefundRequestJourney + "/how-you-will-get-the-refund")
            .saveAs("HowYouWillGetRefund")
        } else {
          header("Location")
            .is(routeRefundRequestJourney + "/how-your-client-will-get-the-refund")
            .saveAs("HowYouWillGetRefund")
        }
      )

  val getHowYouWillGetRefundPage: HttpRequestBuilder =
    http("Get How You Will Get Refund Page")
      .get(s"$baseUrl$${HowYouWillGetRefund}": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postHowYouWillGetRefundPage: HttpRequestBuilder =
    http("Post How You Will Get Refund Page")
      .post(s"$baseUrl$routeRefundRequestJourney/how-you-will-get-the-refund": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(routeRefundRequestJourney + "/account-details").saveAs("HowYouWillGetRefund"))

  val getAccountTypePage: HttpRequestBuilder =
    http("Get Account Type Page")
      .get(s"$baseUrl$routeRefundRequestJourney/account-details": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postAccountTypePage: HttpRequestBuilder =
    http("Post Account Type Page")
      .post(s"$baseUrl$routeRefundRequestJourney/account-details": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("accountType", "business")
      .formParam("continue", "")
      .check(status.is(303))
      .check(header("Location").is(routeRefundRequestJourney + "/bank-building-society-details").saveAs("BankDetailsPage"))

  val getBankDetailsPage: HttpRequestBuilder =
    http("Get Bank Details Page")
      .get(s"$baseUrl$${BankDetailsPage}": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postBankDetailsPage: HttpRequestBuilder =
    http("Post Bank Details Page")
      .post(s"$baseUrl$${BankDetailsPage}": String)
      .formParam("csrfToken", "${csrfToken}")
      .formParam("accountName", "Security Engima")
      .formParam("sortCode", "20 71 06")
      .formParam("accountNumber", "86473611")
      .formParam("rollNumber", "0")
      .formParam("continue", "")
      .check(status.is(303))
      .check(header("Location").is(routeRefundRequestJourney + "/check-your-answers").saveAs("CheckAnswersPage"))

  val getCheckAnswersPage: HttpRequestBuilder =
    http("Get Check Answers Page")
      .get(s"$baseUrl$${CheckAnswersPage}": String)
      .check(status.is(200))
      .check(css("input[name=csrfToken]", "value").saveAs("csrfToken"))

  val postCheckAnswersConfirmPage: HttpRequestBuilder =
    http("Post Check Answers Confirm Page")
      .post(s"$baseUrl$routeRefundRequestJourney/check-your-answers-confirm": String)
      .formParam("csrfToken", "${csrfToken}")
      .check(status.is(303))
      .check(header("Location").is(route + "/reauthentication").saveAs("Reauthentication"))

  val getReauthentication: HttpRequestBuilder =
    http("Get Reauthentication")
      .get(s"$baseUrl$${Reauthentication}": String)
      .check(status.is(303))
      .check(
        header("Location")
          .is(route + "/test-only/reauthentication?continue=/request-a-self-assessment-refund/check-your-answers-submit")
          .saveAs("ReauthenticationPage")
      )

  val getReauthenticationPage: HttpRequestBuilder =
    http("Get Reauthentication Page")
      .get(s"$baseUrl$${ReauthenticationPage}": String)
      .check(status.is(200))

  val getSubmit: HttpRequestBuilder =
    http("Get Submit")
      .get(s"$baseUrl$routeRefundRequestJourney/check-your-answers-submit": String)
      .check(status.is(303))
      .check(header("Location").saveAs("ConfirmationPage"))

  val getConfirmationPage: HttpRequestBuilder =
    http("Get Confirmation Page")
      .get(s"$baseUrl$${ConfirmationPage}": String)
      .check(status.is(200))

  val getHistoryPage: HttpRequestBuilder =
    http("Get History Page")
      .get(s"$baseUrl$${HistoryPage}": String)
      .check(status.is(200))

  val getRefundProcessingPage: HttpRequestBuilder =
    http("Get Refund Processing Page")
      .get(s"$baseUrl$route/refund-status/003": String)
      .check(status.is(200))

  val getRefundApprovedPage: HttpRequestBuilder =
    http("Get Refund Approved Page")
      .get(s"$baseUrl$route/refund-status/001": String)
      .check(status.is(200))

  val getRefundPaidPage: HttpRequestBuilder =
    http("Get Refund Paid Page")
      .get(s"$baseUrl$route/refund-status/004": String)
      .check(status.is(200))

  val getRefundRejectedPage: HttpRequestBuilder =
    http("Get Refund Rejected Page")
      .get(s"$baseUrl$route/refund-status/002": String)
      .check(status.is(200))

}
