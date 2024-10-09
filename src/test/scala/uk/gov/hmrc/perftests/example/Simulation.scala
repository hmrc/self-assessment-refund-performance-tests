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

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.example.Requests._

class Simulation extends PerformanceTestRunner {

  setup("individual-refund-start", "Refund Journey - Individual")
    .withRequests(
      getAuthLogin,
      postAuthLoginRefund("Individual"),
      getStartPage,
      postStartPageRefund,
      getRefundAmountPage,
      postRefundAmountPage("Individual")
    )

  setup("agent-refund-start", "Refund Journey - Agent")
    .withRequests(
      getAuthLogin,
      postAuthLoginRefund("Agent"),
      getStartPage,
      postStartPageRefund,
      getRefundAmountPage,
      postRefundAmountPage("Agent")
    )

  setup("refund-journey", "Refund Journey")
    .withRequests(
      getHowYouWillGetRefundPage,
      postHowYouWillGetRefundPage,
      getAccountTypePage,
      postAccountTypePage,
      getBankDetailsPage,
      postBankDetailsPage,
      getCheckAnswersPage,
      postCheckAnswersConfirmPage,
      getSignInAgainPage,
      postSignInAgainPage,
      getReauthentication,
      getReauthenticationPage,
      getSubmit,
      getConfirmationPage
    )

  setup("tracker-journey", "Tracker Journey")
    .withRequests(
      getAuthLogin,
      postAuthLoginTrack,
      getStartPage,
      postStartPageTrack,
      getRefundTracker,
      getTrackerPage,
      getRefundProcessingRiskingPage,
      getTrackerPage,
      getRefundApprovedPage,
      getTrackerPage,
      getRefundRejectedPage,
      getTrackerPage,
      getRefundProcessingPage
    )

  runSimulation()
}
