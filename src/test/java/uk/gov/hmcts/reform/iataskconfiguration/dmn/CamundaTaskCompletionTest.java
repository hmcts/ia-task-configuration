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
                "residentJudgeFtpaDecision",
                asList(
                    Map.of(
                        "taskType", "decideAnFTPA",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaDecideAnFTPA",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "leadershipJudgeFtpaDecision",
                asList(
                    Map.of(
                        "taskType", "decideAnFTPA",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaDecideAnFTPA",
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
                        "taskType", "adaReviewTheAppeal",
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
                        "taskType", "adaReviewRespondentEvidence",
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
                        "taskType", "adaReviewRespondentEvidence",
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
                        "taskType", "adaReviewRespondentEvidence",
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
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaReviewAppealSkeletonArgument",
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
                        "taskType", "adaReviewAppealSkeletonArgument",
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
                        "taskType", "adaReviewAdditionalEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewAdditionalHomeOfficeEvidence",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaReviewAdditionalHomeOfficeEvidence",
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
                        "taskType", "adaReviewAddendumEvidence",
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
                        "taskType", "processApplication",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "processApplicationToReviewDecision",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToAdjourn",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToExpedite",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationForTimeExtension",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToWithdraw",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToReviewDecision",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToUpdateHearingRequirements",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToUpdateAppealDetails",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToReinstateAnEndedAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaProcessApplicationToOther",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "adaLinkUnlinkAppeals",
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
                        "taskType", "adaEditListing",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "listCase",
                List.of(
                    Map.of(
                        "taskType", "adaListCase",
                        "completionMode", "Auto"
                    )
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
                        "taskType", "adaSendDecisionsAndReasons",
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
                    Map.of(
                        "taskType", "adaPrepareDecisionsAndReasons",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "transferOutOfAda",
                asList(
                    Map.of(
                        "taskType", "reviewCaseMarkedUnsuitableForADA",
                        "completionMode", "Auto"
                    ),
                    Collections.emptyMap()
                )
            ),
            Arguments.of(
                "adaSuitabilityReview",
                asList(
                    Map.of(
                        "taskType", "reviewADASuitability",
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
        assertThat(logic.getRules().size(), is(45));


    }


}
