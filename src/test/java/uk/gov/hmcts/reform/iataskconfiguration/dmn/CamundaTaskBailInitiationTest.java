package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import lombok.Builder;
import lombok.Value;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_IA_BAIL;

class CamundaTaskBailInitiationTest extends DmnDecisionTableBaseUnitTest {

    @Builder
    @Value
    static class Scenario {
        String description;
        String eventId;
        String postEventState;
        String applicationSubmittedBy;
        boolean hasActiveInterpreterFlag;
        String lastFileUploadedBy;
        String listingEvent;
        String recordDecisionType;
        List<Map<String, Object>> expectation;

        @Override
        public String toString() {
            return description;
        }
    }

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_IA_BAIL;
    }

    private static final String listingHearingDate = LocalDateTime.now().plusDays(5)
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    private static final String bailSummaryDueDate = LocalDateTime.now().plusDays(3)
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @ParameterizedTest(name = "Scenario: {0}")
    @MethodSource("scenarioProvider")
    void given_submit_application_should_evaluate_dmn(Scenario scenario) {
        Map<String, Object> data = new HashMap<>();
        data.put("applicationSubmittedBy", scenario.getApplicationSubmittedBy());
        data.put("hasActiveInterpreterFlag", scenario.isHasActiveInterpreterFlag());
        data.put("lastFileUploadedBy", scenario.getLastFileUploadedBy());
        data.put("listingEvent", scenario.getListingEvent());
        data.put("recordDecisionType", scenario.getRecordDecisionType());
        data.put("listingHearingDate", listingHearingDate);
        data.put("bailSummaryDueDate", bailSummaryDueDate);
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("Data", data);
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("additionalData", additionalData);

        inputVariables.putValue("eventId", scenario.getEventId());
        inputVariables.putValue("postEventState", scenario.getPostEventState());

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(scenario.getExpectation()));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(7));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(9));
    }

    private static Stream<Scenario> scenarioProvider() {
        return Stream.of(
            Scenario.builder()
                .description("Legal Rep submits with no other additional data")
                .eventId("submitApplication")
                .applicationSubmittedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "processBailApplication",
                    "name", "Process application",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Legal Rep submits with other additional data")
                .eventId("submitApplication")
                .applicationSubmittedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "processBailApplication",
                    "name", "Process application",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Non Legal Rep submits")
                .eventId("submitApplication")
                .applicationSubmittedBy("Non Legal Rep")
                .expectation(emptyList())
                .build(),
            Scenario.builder()
                .description("Legal Rep makes new application with no other additional data")
                .eventId("makeNewApplication")
                .applicationSubmittedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "processBailApplication",
                    "name", "Process application",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Legal Rep makes new application with other additional data")
                .eventId("makeNewApplication")
                .applicationSubmittedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "processBailApplication",
                    "name", "Process application",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Non Legal Rep makes new application")
                .eventId("makeNewApplication")
                .applicationSubmittedBy("Non Legal Rep")
                .expectation(emptyList())
                .build(),
            Scenario.builder()
                .description("Create flag with active interpreter")
                .eventId("updateInterpreterWaTask")
                .hasActiveInterpreterFlag(true)
                .expectation(List.of(Map.of(
                    "taskId", "reviewInterpreterFlag",
                    "name", "Review interpreter booking",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Create flag with no active interpreter")
                .eventId("updateInterpreterWaTask")
                .hasActiveInterpreterFlag(false)
                .expectation(emptyList())
                .build(),
            Scenario.builder()
                .description("LR removed from case Legal Rep submission")
                .eventId("removeBailLegalRepresentative")
                .applicationSubmittedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "noticeOfChange",
                    "name", "Notice of Change",
                    "processCategories", "followUpNoc"
                )))
                .build(),
            Scenario.builder()
                .description("LR removed from case Non Legal Rep submission")
                .eventId("removeBailLegalRepresentative")
                .applicationSubmittedBy("Non Legal Rep")
                .expectation(emptyList())
                .build(),
            Scenario.builder()
                .description("LR stops representing case Legal Rep submission")
                .eventId("stopLegalRepresenting")
                .applicationSubmittedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "noticeOfChange",
                    "name", "Notice of Change",
                    "processCategories", "followUpNoc"
                )))
                .build(),
            Scenario.builder()
                .description("LR stops representing case Non Legal Rep submission")
                .eventId("stopLegalRepresenting")
                .applicationSubmittedBy("Non Legal Rep")
                .expectation(emptyList())
                .build(),
            Scenario.builder()
                .description("Case is listed as initial listing")
                .eventId("caseListing")
                .listingEvent("initialListing")
                .expectation(List.of(
                    Map.of(
                        "taskId", "followUpBailSummary",
                        "name", "Follow up Home Office summary",
                        "delayUntil", Map.of(
                            "delayUntil", bailSummaryDueDate,
                            "delayUntilIntervalDays", "0"
                        ),
                        "processCategories", "followUpOverdue"
                    ),
                    Map.of(
                        "taskId", "postHearingRecord",
                        "name", "Post hearing – attendees, duration and recording",
                        "delayUntil", Map.of(
                            "delayUntil", listingHearingDate,
                            "delayUntilIntervalDays", "0"
                        ),
                        "processCategories", "followUpPostHearingTasks"
                    )
                ))
                .build(),
            Scenario.builder()
                .description("Case is listed as relisting")
                .eventId("caseListing")
                .listingEvent("relisting")
                .expectation(List.of(Map.of(
                    "taskId", "postHearingRecord",
                    "name", "Post hearing – attendees, duration and recording",
                    "delayUntil", Map.of(
                        "delayUntil", listingHearingDate,
                        "delayUntilIntervalDays", "0"
                    ),
                    "processCategories", "followUpPostHearingTasks"
                )))
                .build(),
            Scenario.builder()
                .description("Legal Rep uploads document")
                .eventId("uploadDocuments")
                .lastFileUploadedBy("Legal Representative")
                .expectation(List.of(Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Home Office uploads document")
                .eventId("uploadDocuments")
                .lastFileUploadedBy("Home Office")
                .expectation(List.of(Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )))
                .build(),
            Scenario.builder()
                .description("Admin Officer uploads document")
                .eventId("uploadDocuments")
                .lastFileUploadedBy("Admin Officer")
                .expectation(emptyList())
                .build(),
            Scenario.builder()
                .description("Decision is recorded as conditional")
                .eventId("recordTheDecision")
                .recordDecisionType("conditionalGrant")
                .expectation(List.of(
                    Map.of(
                        "taskId", "uploadSignedDecision",
                        "name", "Upload signed decision",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "listForFurtherReview",
                        "name", "List for further review",
                        "processCategories", "caseProgression"
                    )
                ))
                .build(),
            Scenario.builder()
                .description("Decision is recorded as anything other than conditional")
                .eventId("recordTheDecision")
                .recordDecisionType("approved")
                .expectation(List.of(
                    Map.of(
                        "taskId", "uploadSignedDecision",
                        "name", "Upload signed decision",
                        "processCategories", "caseProgression"
                    )
                ))
                .build(),
            Scenario.builder()
                .description("Change tribunal centre")
                .eventId("changeTribunalCentre")
                .expectation(List.of(Map.of(
                    "taskId", "processBailApplication",
                    "name", "Process application",
                    "processCategories", "caseProgression"
                )))
                .build()
        );
    }
}
