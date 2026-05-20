package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import lombok.Builder;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CANCELLATION_IA_BAIL;

class CamundaTaskBailCancellationTest extends DmnDecisionTableBaseUnitTest {

    @ParameterizedTest(name = "from state: {0}, event id: {1}, state: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(Scenario scenario) {
        String fromState = scenario.fromState();
        String eventId = scenario.eventId();
        String state = scenario.state();
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("fromState", fromState);
        inputVariables.putValue("event", eventId);
        inputVariables.putValue("state", state);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        List<Map<String, String>> expectation = scenario.expectation();
        assertEquals(expectation, dmnDecisionTableResult.getResultList());
    }

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CANCELLATION_IA_BAIL;
    }

    static Stream<Scenario> scenarioProvider() {
        return Stream.of(
            Scenario.builder()
                .eventId("endApplication")
                .expectation(singletonList(Map.of("action", "Cancel")))
                .build(),
            Scenario.builder()
                .eventId("moveApplicationToDecided")
                .expectation(singletonList(Map.of("action", "Cancel")))
                .build(),
            Scenario.builder()
                .fromState("applicationSubmitted")
                .eventId("editBailApplicationAfterSubmit")
                .expectation(singletonList(Map.of("action", "Reconfigure")))
                .build(),
            Scenario.builder()
                .eventId("applyNocDecision")
                .expectation(singletonList(Map.of("action", "Reconfigure")))
                .build(),
            Scenario.builder()
                .eventId("caseListing")
                .expectation(
                    List.of(
                        Map.of("action", "Reconfigure"),
                        Map.of(
                            "action", "Cancel",
                            "processCategories", "conditionalListTask"
                        )
                    ))
                .build(),
            Scenario.builder()
                .eventId("recordTheDecision")
                .expectation(singletonList(Map.of("action", "Reconfigure")))
                .build(),
            Scenario.builder()
                .eventId("uploadBailSummary")
                .expectation(
                    List.of(Map.of(
                        "action", "Cancel",
                        "processCategories", "followUpOverdue"
                    )))
                .build(),
            Scenario.builder()
                .eventId("updateBailLegalRepDetails")
                .expectation(
                    List.of(Map.of(
                                "action", "Cancel",
                                "processCategories", "followUpNoc"
                            )
                    ))
                .build(),
            Scenario.builder()
                .eventId("hearingCompletedOrCancelled")
                .expectation(
                    List.of(Map.of(
                                "action", "Cancel",
                                "processCategories", "followUpPostHearingTasks"
                            )
                    ))
                .build(),
            Scenario.builder()
                .eventId("uploadHearingRecording")
                .expectation(
                    List.of(Map.of(
                                "action", "Cancel",
                                "processCategories", "followUpPostHearingTasks"
                            )
                    ))
                .build(),
            Scenario.builder()
                .eventId("updateInterpreterWaTask")
                .expectation(
                    List.of(Map.of(
                                "action", "Cancel",
                                "processCategories", "flagReview"
                            )
                    ))
                .build(),
            Scenario.builder()
                .eventId("uploadSignedDecisionNotice")
                .expectation(
                    List.of(Map.of(
                                "action", "Cancel",
                                "processCategories", "uploadDecisionTask"
                            )
                    ))
                .build(),
            Scenario.builder()
                .eventId("uploadSignedDecisionNoticeConditionalGrant")
                .expectation(
                    List.of(Map.of(
                                "action", "Cancel",
                                "processCategories", "uploadDecisionTask"
                            )
                    ))
                .build()
        );
    }

    @Builder
    private record Scenario(String fromState, String eventId, String state, List<Map<String, String>> expectation) {
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertEquals(3, logic.getInputs().size());
        assertEquals(4, logic.getOutputs().size());
        assertEquals(11, logic.getRules().size());
    }
}
