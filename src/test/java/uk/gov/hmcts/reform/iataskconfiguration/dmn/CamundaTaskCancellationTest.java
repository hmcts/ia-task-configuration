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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CANCELLATION_IA_ASYLUM;

class CamundaTaskCancellationTest extends DmnDecisionTableBaseUnitTest {
    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CANCELLATION_IA_ASYLUM;
    }

    static Stream<Arguments> scenarioProvider() {

        return Stream.of(
            Arguments.of(
                null,
                "endAppeal",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel"
                    )
                )
            ),
            Arguments.of(
                null,
                "removeAppealFromOnline",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel"
                    )
                )
            ),
            Arguments.of(
                "awaitingReasonsForAppeal",
                "submitReasonsForAppeal",
                null,
                asList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Time extension"
                    )
                )
            ),
            Arguments.of(
                "awaitingClarifyingQuestionsAnswers",
                "submitClarifyingQuestionAnswers",
                null,
                asList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Time extension"
                    )
                )
            ),
            Arguments.of(
                "awaitingCmaRequirements",
                "submitCmaRequirements",
                null,
                asList(

                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Time extension"
                    )
                )
            ),
            Arguments.of(
                "awaitingRespondentEvidence",
                "uploadHomeOfficeBundle",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    )
                )
            ),
            Arguments.of(
                "caseBuilding",
                "submitCase",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    )
                )
            ),
            Arguments.of(
                "respondentReview",
                "uploadHomeOfficeAppealResponse",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    )
                )
            ),
            Arguments.of(
                "submitHearingRequirements",
                "draftHearingRequirements",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    )
                )
            ),
            Arguments.of(
                null,
                "makeAnApplication",
                null,
                singletonList(
                    Map.of(
                        "action", "Warn"
                    )
                )
            ),
            Arguments.of(
                null,
                "markAppealPaid",
                null,
                singletonList(
                    Map.of(
                        "action", "Cancel",
                        "TaskCategories", "Followup overdue"
                    )
                )
            ),
            Arguments.of(
                "unknownState",
                null,
                null,
                emptyList()
            )
        );
    }

    @ParameterizedTest(name = "from state: {0}, event id: {1}, state: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String fromState,
                                                      String eventId,
                                                      String state,
                                                      List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("fromState", fromState);
        inputVariables.putValue("event", eventId);
        inputVariables.putValue("state", state);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(13));

    }
}
