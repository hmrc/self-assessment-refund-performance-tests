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

  setup("start-successful-refund-journey", "Start Refund Journey")
    .withRequests(getAuthLogin, postAuthLoginRefundSuccess, getStartPage, postStartPageRefund)

  setup("start-unsuccessful-refund-journey", "Start Refund Journey")
    .withRequests(getAuthLogin, postAuthLoginRefundUnsuccessful, getStartPage, postStartPageRefund, getRefundAmount)

  setup("start-history-journey", "Start History Journey")
    .withRequests(getAuthLogin, postAuthLoginHistory, getStartPage, postStartPageHistory, getRefundHistory)

  setup("successful-refund-journey", "Successful Refund Journey")
    .withRequests(
      getRefundAmountPage,
      postRefundAmountPage,
      getHowYouWillGetRefundPage,
      postHowYouWillGetRefundPage,
      getAccountTypePage,
      postAccountTypePage,
      getBankDetailsPage,
      postBankDetailsPage,
      getCheckDetailsPage,
      postCheckDetailsConfirmPage,
      getReauthentication,
      getReauthenticationPage,
      getSubmit,
      getConfirmationPage
    )

  setup("unsuccessful-refund-journey", "Unsuccessful Refund Journey")
    .withRequests(getIvStubRefund, postIvStubFailed, getCannotConfirmIdentityPage)

  setup("history-journey", "History Journey")
    .withRequests(
      getHistoryPage,
      getRefundProcessingPage,
      getHistoryPage,
      getRefundPaidPage,
      getHistoryPage,
      getRefundApprovedPage,
      getHistoryPage,
      getRefundRejectedPage
    )

  runSimulation()
}
