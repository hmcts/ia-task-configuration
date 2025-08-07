package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_COMPLETION_IA_ASYLUM;

class CamundaTaskCompletionTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_COMPLETION_IA_ASYLUM;
    }

    static Stream<Arguments> scenarioProvider() {

        return Stream.of(
            Arguments.of(
                "decideFtpaApplication",
                asList(
                    Map.of(
                        "taskType", "decideAnFTPA",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedDecideAnFTPA",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestRespondentEvidence",
                asList(
                    Map.of(
                        "taskType", "reviewTheAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewTheAppeal",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "uploadHearingRecording",
                asList(
                    Map.of(
                        "taskType", "uploadHearingRecording",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedPostHearingAttendeesDurationAndRecording",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                asList(
                    Map.of(
                        "taskType", "reviewRespondentEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewRespondentEvidence",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestReasonsForAppeal",
                asList(
                    Map.of(
                        "taskType", "reviewRespondentEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewRespondentEvidence",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "sendDirection",
                asList(
                    Map.of(
                        "taskType", "reviewRespondentEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewRespondentEvidence",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestResponseReview",
                asList(
                    Map.of(
                        "taskType", "reviewRespondentResponse",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                asList(
                    Map.of(
                        "taskType", "reviewAppealSkeletonArgument",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewAppealSkeletonArgument",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestCaseEdit",
                asList(
                    Map.of(
                        "taskType", "reviewAppealSkeletonArgument",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewAppealSkeletonArgument",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "sendDirectionWithQuestions",
                asList(
                    Map.of(
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestCmaRequirements",
                asList(
                    Map.of(
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "markEvidenceAsReviewed",
                asList(
                    Map.of(
                        "taskType", "reviewAdditionalEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewAdditionalEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewAdditionalHomeOfficeEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewAdditionalHomeOfficeEvidence",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "markAddendumEvidenceAsReviewed",
                asList(
                    Map.of(
                        "taskType", "reviewAddendumEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewAddendumEvidence",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "reviewCmaRequirements",
                asList(
                    Map.of(
                        "taskType", "reviewCmaRequirements",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "updateDetailsAfterCma",
                asList(
                    Map.of(
                        "taskType", "attendCma",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestResponseAmend",
                asList(
                    Map.of(
                        "taskType", "reviewRespondentResponse",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                asList(
                    Map.of(
                        "taskType", "reviewHearingRequirements",
                        "completionMode", "Auto"
                    ),Map.of(
                        "taskType", "detainedReviewHearingRequirements",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                asList(
                    Map.of(
                        "taskType", "caseSummaryHearingBundleStartDecision",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedCaseSummaryHearingBundleStartDecision",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                asList(
                    Map.of(
                        "taskType", "decideOnTimeExtension",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "decideAnApplication",
                asList(
                    Map.of(
                        "taskType", "processApplicationAdjourn",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationAdjourn",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationExpedite",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationExpedite",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationTimeExtension",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationTimeExtension",
                        "completionMode", "Auto"
                    ),

                    Map.of(
                        "taskType", "processApplicationTransfer",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationTransfer",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationWithdraw",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationWithdraw",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationUpdateHearingRequirements",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationUpdateHearingRequirements",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationUpdateAppealDetails",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationUpdateAppealDetails",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationReinstateAnEndedAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationReinstateAnEndedAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationOther",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationOther",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationLink/UnlinkAppeals",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationLink/UnlinkAppeals",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewSetAsideDecisionApplication",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewSetAsideDecisionApplication",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationChangeHearingType",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationChangeHearingType",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationToReviewDecision",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessApplicationToReviewDecision",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "editCaseListing",
                List.of(
                    Map.of(
                        "taskType", "editListing",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedEditListing",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "sendDecisionAndReasons",
                asList(
                    Map.of(
                        "taskType", "sendDecisionsAndReasons",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedSendDecisionsAndReasons",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "generateDecisionAndReasons",
                asList(
                    Map.of(
                        "taskType", "prepareDecisionsAndReasons",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "recordRemissionDecision",
                asList(
                    Map.of(
                        "taskType", "reviewRemissionApplication",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedReviewRemissionApplication",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "reviewAriaRemissionApplication"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "recordAllocatedJudge",
                asList(
                    Map.of(
                        "taskType", "assignAFTPAJudge",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedAssignAFTPAJudge",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "listCase",
                asList(
                    Map.of(
                        "taskType", "listTheCase",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedListTheCase",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "markAppealPaid",
                asList(
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "markAsPaid"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "markPaymentRequestSent",
                asList(
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "sendPaymentRequest"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "requestNewHearingRequirements",
                asList(
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "reviewRemittedAppeal"
                    ),
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "reviewAppealSetAsideUnderRule35"
                    ),
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "reviewAppealSetAsideUnderRule32"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                asList(
                    Map.of(
                        "completionMode", "Auto",
                        "taskType", "reviewMigratedCase"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "submitAppeal",
                asList(
                    Map.of(
                        "taskType", "reviewDraftAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "DetainedReviewDraftAppeal",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "endAppeal",
                asList(
                    Map.of(
                        "taskType", "reviewDraftAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "DetainedReviewDraftAppeal",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "refundConfirmation",
                asList(
                    Map.of(
                        "taskType", "processFeeRefund",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "detainedProcessFeeRefund",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "unknownEvent",
                emptyList()
            )
        );
    }

    @ParameterizedTest(name = "event id: {0}")
    @MethodSource("scenarioProvider")
    void given_event_ids_should_evaluate_dmn(String eventId, List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(76));
    }


}
