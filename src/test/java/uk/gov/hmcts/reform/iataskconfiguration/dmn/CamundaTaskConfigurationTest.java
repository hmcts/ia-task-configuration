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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_IA_ASYLUM;

class CamundaTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String DEFAULT_CALENDAR = "https://www.gov.uk/bank-holidays/england-and-wales.json";
    private static final String EXTRA_TEST_CALENDAR = "https://raw.githubusercontent.com/hmcts/"
        + "ia-task-configuration/master/src/test/resources/extra-non-working-day-calendar.json";
    private static final String CURRENT_DATE_TIME = LocalDateTime.now().toString();

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_IA_ASYLUM;
    }

    @ParameterizedTest
    @CsvSource({
        "followUpNoticeOfChange","detainedFollowUpNoticeOfChange"
    })
    void when_taskId_then_return_Access_Requests(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("workType"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "workType",
            "value", "access_requests",
            "canReconfigure", false
        ), workTypeResultList.get(0));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(36));
    }

    @SuppressWarnings("checkstyle:indentation")
    @ParameterizedTest
    @CsvSource(value = {
        "refusalOfHumanRights, Human rights",
        "refusalOfEu, EEA",
        "deprivation, DoC",
        "protection, Protection",
        "revocationOfProtection, Revocation",
        "NULL_VALUE, ''",
        "'', ''"
    }, nullValues = "NULL_VALUE")
    void when_caseData_then_return_expected_appealType(String appealType, String expectedAppealType) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        caseData.put("appealType", appealType);
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "appealType",
            "value", expectedAppealType,
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "caseManagementCategory",
            "value", expectedAppealType,
            "canReconfigure", false
        )));
    }

    @Test
    void when_caseData_then_return_expected_case_management_category() {
        String refusalOfEuLabel = "Refusal of application under the EEA regulations";
        String revocationLabel = "Revocation of a protection status";
        List<Map<String, Object>> caseManagementCategories = List.of(
            Map.of(
                "value",
                Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"),
                "list_items",
                List.of(Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"))
            ),
            Map.of(
                "value", Map.of("code", "refusalOfEu", "label", "Refusal of application under the EEA regulations"),
                "list_items", List.of(Map.of("code", "refusalOfEu", "label", refusalOfEuLabel))
            ),
            Map.of(
                "value", Map.of("code", "deprivation", "label", "Deprivation of citizenship"),
                "list_items", List.of(Map.of("code", "deprivation", "label", "Deprivation of citizenship"))
            ),
            Map.of(
                "value", Map.of("code", "protection", "label", "Refusal of protection claim"),
                "list_items", List.of(Map.of("code", "protection", "label", "Refusal of protection claim"))
            ),
            Map.of(
                "value", Map.of("code", "revocationOfProtection", "label", "Revocation of a protection status"),
                "list_items", List.of(Map.of("code", "revocationOfProtection", "label", revocationLabel))
            )
        );

        List<String> expectedCaseManagementCategories = List.of(
            "Human rights",
            "EEA",
            "DoC",
            "Protection",
            "Revocation"
        );

        for (int i = 0; i < caseManagementCategories.size(); i++) {
            Map<String, Object> caseManagementCategory = caseManagementCategories.get(i);
            VariableMap inputVariables = new VariableMapImpl();
            Map<String, Object> caseData = new HashMap<>(); // allow null values
            caseData.put("caseManagementCategory", caseManagementCategory);
            inputVariables.putValue("caseData", caseData);

            DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

            assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
                "name", "caseManagementCategory",
                "value", expectedCaseManagementCategories.get(i),
                "canReconfigure", false
            )));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
        "false, false, false, false",
        "true, true, false, false",
        "true, true, true, true"
    })
    void nextHearingId_and_nextHearingDate_should_be_set_correctly(
        boolean caseDataSet, boolean nextHearingDetailsSet, boolean nextHearingIdAndDateSet, boolean expected) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = caseDataSet ? new HashMap<>() : null;
        Map<String, Object> nextHearingDetails = nextHearingDetailsSet ? new HashMap<>() : null;
        if (nextHearingIdAndDateSet && nextHearingDetailsSet) {
            nextHearingDetails.put("hearingID", "123Id");
            nextHearingDetails.put("hearingDateTime", CURRENT_DATE_TIME);
        }
        if (caseDataSet && nextHearingDetailsSet) {
            caseData.put("nextHearingDetails", nextHearingDetails);
        }
        if (caseDataSet) {
            inputVariables.putValue("caseData", caseData);
        }

        String nextHearingDate = expected ? CURRENT_DATE_TIME : "";
        String nextHearingId = expected ? "123Id" : "";

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingId",
            "value", nextHearingId,
            "canReconfigure", true
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingDate",
            "value", nextHearingDate,
            "canReconfigure", true
        )));
    }

    public static Stream<Arguments> workTypeScenarioProvider() {
        List<Map<String, Object>> routineWork = List.of(Map.of(
            "name", "workType",
            "value", "routine_work",
            "canReconfigure", false
        ));
        List<Map<String, Object>> decisionMakingWork = List.of(Map.of(
            "name", "workType",
            "value", "decision_making_work",
            "canReconfigure", false
        ));
        List<Map<String, Object>> hearingWork = List.of(Map.of(
            "name", "workType",
            "value", "hearing_work",
            "canReconfigure", false
        ));
        List<Map<String, Object>> applications = List.of(Map.of(
            "name", "workType",
            "value", "applications",
            "canReconfigure", false
        ));
        List<Map<String, Object>> upperTribunal = List.of(Map.of(
            "name", "workType",
            "value", "upper_tribunal",
            "canReconfigure", false
        ));
        List<Map<String, Object>> reviewCase = List.of(Map.of(
            "name", "workType",
            "value", "review_case",
            "canReconfigure", false
        ));

        return Stream.of(
            Arguments.of("arrangeOfflinePayment", routineWork),
            Arguments.of("markCaseAsPaid", routineWork),
            Arguments.of("attendCma", routineWork),
            Arguments.of("caseSummaryHearingBundleStartDecision", routineWork),
            Arguments.of("detainedCaseSummaryHearingBundleStartDecision", routineWork),
            Arguments.of("followUpExtendedDirection", routineWork),
            Arguments.of("detainedFollowUpExtendedDirection", routineWork),
            Arguments.of("followUpNonStandardDirection", routineWork),
            Arguments.of("detainedFollowUpNonStandardDirection", routineWork),
            Arguments.of("reviewClarifyingQuestionsAnswers", routineWork),
            Arguments.of("reviewRemissionApplication", routineWork),
            Arguments.of("detainedReviewRemissionApplication", routineWork),
            Arguments.of("assignAFTPAJudge", routineWork),
            Arguments.of("detainedAssignAFTPAJudge", routineWork),
            Arguments.of("reviewAppealSetAsideUnderRule35", routineWork),
            Arguments.of("reviewAppealSetAsideUnderRule32", routineWork),
            Arguments.of("detainedReviewAppealSetAsideUnderRule32", routineWork),
            Arguments.of("sendPaymentRequest", routineWork),
            Arguments.of("markAsPaid", routineWork),
            Arguments.of("reviewRemittedAppeal", routineWork),
            Arguments.of("detainedReviewRemittedAppeal", routineWork),
            Arguments.of("reviewAriaRemissionApplication", routineWork),
            Arguments.of("reviewDraftAppeal", routineWork),
            Arguments.of("printAndSendHoBundle", routineWork),
            Arguments.of("detainedPrintAndSendHoBundle", routineWork),
            Arguments.of("printAndSendHoResponse", routineWork),
            Arguments.of("printAndSendHearingRequirements", routineWork),
            Arguments.of("detainedPrintAndSendHearingRequirements", routineWork),
            Arguments.of("printAndSendHearingBundle", routineWork),
            Arguments.of("detainedPrintAndSendHearingBundle", routineWork),
            Arguments.of("printAndSendDecisionCorrectedRule31", routineWork),
            Arguments.of("detainedPrintAndSendDecisionCorrectedRule31", routineWork),
            Arguments.of("printAndSendDecisionCorrectedRule32", routineWork),
            Arguments.of("detainedPrintAndSendDecisionCorrectedRule32", routineWork),
            Arguments.of("printAndSendHoApplication", routineWork),
            Arguments.of("detainedPrintAndSendHoApplication", routineWork),
            Arguments.of("printAndSendHoEvidence", routineWork),
            Arguments.of("detainedPrintAndSendHoEvidence", routineWork),
            Arguments.of("printAndSendAppealDecision", routineWork),
            Arguments.of("detainedPrintAndSendAppealDecision", routineWork),
            Arguments.of("printAndSendFTPADecision", routineWork),
            Arguments.of("detainedPrintAndSendFTPADecision", routineWork),
            Arguments.of("printAndSendReheardHearingRequirements", routineWork),
            Arguments.of("detainedPrintAndSendReheardHearingRequirements", routineWork),
            Arguments.of("processFeeRefund", routineWork),
            Arguments.of("reviewAdditionalEvidence", decisionMakingWork),
            Arguments.of("detainedReviewAdditionalEvidence", decisionMakingWork),
            Arguments.of("reviewTheAppeal", decisionMakingWork),
            Arguments.of("detainedReviewTheAppeal", decisionMakingWork),
            Arguments.of("followUpOverdueRespondentEvidence", decisionMakingWork),
            Arguments.of("detainedFollowUpOverdueRespondentEvidence", decisionMakingWork),
            Arguments.of("reviewRespondentEvidence", decisionMakingWork),
            Arguments.of("detainedReviewRespondentEvidence", decisionMakingWork),
            Arguments.of("followUpOverdueCaseBuilding", decisionMakingWork),
            Arguments.of("detainedFollowUpOverdueCaseBuilding", decisionMakingWork),
            Arguments.of("reviewAppealSkeletonArgument", decisionMakingWork),
            Arguments.of("detainedReviewAppealSkeletonArgument", decisionMakingWork),
            Arguments.of("followUpOverdueReasonsForAppeal", decisionMakingWork),
            Arguments.of("reviewReasonsForAppeal", decisionMakingWork),
            Arguments.of("followUpOverdueClarifyingAnswers", decisionMakingWork),
            Arguments.of("reviewClarifyingAnswers", decisionMakingWork),
            Arguments.of("followUpOverdueRespondentReview", decisionMakingWork),
            Arguments.of("detainedFollowUpOverdueRespondentReview", decisionMakingWork),
            Arguments.of("reviewRespondentResponse", decisionMakingWork),
            Arguments.of("detainedReviewRespondentResponse", decisionMakingWork),
            Arguments.of("followUpOverdueCMARequirements", decisionMakingWork),
            Arguments.of("reviewCmaRequirements", decisionMakingWork),
            Arguments.of("reviewAdditionalHomeOfficeEvidence", decisionMakingWork),
            Arguments.of("detainedReviewAdditionalHomeOfficeEvidence", decisionMakingWork),
            Arguments.of("reviewAddendumHomeOfficeEvidence", decisionMakingWork),
            Arguments.of("reviewAddendumEvidence", decisionMakingWork),
            Arguments.of("detainedReviewAddendumEvidence", decisionMakingWork),
            Arguments.of("decideOnTimeExtension", decisionMakingWork),
            Arguments.of("sendDecisionsAndReasons", decisionMakingWork),
            Arguments.of("detainedSendDecisionsAndReasons", decisionMakingWork),
            Arguments.of("generateDraftDecisionAndReasons", hearingWork),
            Arguments.of("uploadDecision", hearingWork),
            Arguments.of("uploadHearingRecording", hearingWork),
            Arguments.of("postHearingAttendeesDurationAndRecording", hearingWork),
            Arguments.of("detainedPostHearingAttendeesDurationAndRecording", hearingWork),
            Arguments.of("editListing", hearingWork),
            Arguments.of("detainedEditListing", hearingWork),
            Arguments.of("followUpOverdueHearingRequirements", hearingWork),
            Arguments.of("detainedFollowUpOverdueHearingRequirements", hearingWork),
            Arguments.of("reviewHearingRequirements", hearingWork),
            Arguments.of("detainedReviewHearingRequirements", hearingWork),
            Arguments.of("allocateHearingJudge", hearingWork),
            Arguments.of("detainedAllocateHearingJudge", hearingWork),
            Arguments.of("prepareDecisionsAndReasons", hearingWork),
            Arguments.of("startDecisionsAndReasonsDocument", hearingWork),
            Arguments.of("createHearingBundle", hearingWork),
            Arguments.of("createCaseSummary", hearingWork),
            Arguments.of("listTheCase", hearingWork),
            Arguments.of("detainedListTheCase", hearingWork),
            Arguments.of("hearingException", hearingWork),
            Arguments.of("cmrListed", hearingWork),
            Arguments.of("detainedCmrListed", hearingWork),
            Arguments.of("cmrUpdated", hearingWork),
            Arguments.of("detainedCmrUpdated", hearingWork),
            Arguments.of("relistCase", hearingWork),
            Arguments.of("reviewInterpreters", hearingWork),
            Arguments.of("detainedReviewInterpreters", hearingWork),
            Arguments.of("processApplicationAdjourn", applications),
            Arguments.of("detainedProcessApplicationAdjourn", applications),
            Arguments.of("processApplicationExpedite", applications),
            Arguments.of("detainedProcessApplicationExpedite", applications),
            Arguments.of("processApplicationTimeExtension", applications),
            Arguments.of("detainedProcessApplicationTimeExtension", applications),
            Arguments.of("processApplicationTransfer", applications),
            Arguments.of("processApplicationWithdraw", applications),
            Arguments.of("detainedProcessApplicationWithdraw", applications),
            Arguments.of("processApplicationUpdateHearingRequirements", applications),
            Arguments.of("detainedProcessApplicationUpdateHearingRequirements", applications),
            Arguments.of("processApplicationUpdateAppealDetails", applications),
            Arguments.of("detainedProcessApplicationUpdateAppealDetails", applications),
            Arguments.of("processApplicationReinstateAnEndedAppeal", applications),
            Arguments.of("detainedProcessApplicationReinstateAnEndedAppeal", applications),
            Arguments.of("processApplicationOther", applications),
            Arguments.of("detainedProcessApplicationOther", applications),
            Arguments.of("processApplicationLink/UnlinkAppeals", applications),
            Arguments.of("detainedProcessApplicationLink/UnlinkAppeals", applications),
            Arguments.of("processHearingRequirementsApplication", applications),
            Arguments.of("processHearingCentreApplication", applications),
            Arguments.of("processApplicationExpedite", applications),
            Arguments.of("detainedProcessApplicationExpedite", applications),
            Arguments.of("processApplicationTransfer", applications),
            Arguments.of("detainedProcessApplicationTransfer", applications),
            Arguments.of("processApplicationForTimeExtension", applications),
            Arguments.of("processAppealDetailsApplication", applications),
            Arguments.of("processReinstatementApplication", applications),
            Arguments.of("processApplicationToReviewDecision", applications),
            Arguments.of("detainedProcessApplicationToReviewDecision", applications),
            Arguments.of("reviewSetAsideDecisionApplication", applications),
            Arguments.of("detainedReviewSetAsideDecisionApplication", applications),
            Arguments.of("followUpSetAsideDecision", applications),
            Arguments.of("decideAnFTPA", upperTribunal),
            Arguments.of("processApplicationChangeHearingType", applications),
            Arguments.of("detainedProcessApplicationChangeHearingType", applications),
            Arguments.of("reviewMigratedCase", reviewCase),
            Arguments.of("reviewMigratedCase", reviewCase)

        );
    }

    @ParameterizedTest
    @CsvSource({
        "reviewSpecificAccessRequestJudiciary", "reviewSpecificAccessRequestLegalOps",
        "reviewSpecificAccessRequestAdmin","reviewSpecificAccessRequestCTSC"
    })
    void when_taskId_then_return_Access_requests(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();
        Map<String, String> taskAttributes = Map.of("taskType", taskType, "roleAssignmentId", roleAssignmentId);
        inputVariables.putValue("taskAttributes", taskAttributes);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("workType"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "workType",
            "value", "access_requests",
            "canReconfigure", false
        ), workTypeResultList.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        "reviewSpecificAccessRequestJudiciary", "reviewSpecificAccessRequestLegalOps",
        "reviewSpecificAccessRequestAdmin","reviewSpecificAccessRequestCTSC"
    })
    void should_return_request_value_when_role_assignment_id_exists_in_task_attributes(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();

        Map<String, String> taskAttributes = Map.of("taskType", taskType, "roleAssignmentId", roleAssignmentId);
        inputVariables.putValue("taskAttributes", taskAttributes);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dmnResults = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("additionalProperties_roleAssignmentId"))
            .toList();

        assertThat(dmnResults.size(), is(1));

        assertTrue(dmnResults.contains(Map.of(
            "name", "additionalProperties_roleAssignmentId",
            "value", roleAssignmentId,
            "canReconfigure", false
        )));

    }

    @ParameterizedTest
    @MethodSource("workTypeScenarioProvider")
    void when_taskId_then_return_workType(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        if (taskType.equals("reviewMigratedCase") || taskType.equals("detainedReviewMigratedCase")) {
            inputVariables.putValue("caseData", Map.of("ariaMigrationTaskDueDays", "10"));
        }

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("workType"))
            .toList();

        assertEquals(expected, workTypeResultList);
    }

    @ParameterizedTest
    @CsvSource({
        "generateDraftDecisionAndReasons", "uploadDecision", "reviewAddendumHomeOfficeEvidence",
        "reviewAddendumAppellantEvidence", "reviewSpecificAccessRequestJudiciary",
        "reviewSpecificAccessRequestLegalOps", "reviewSpecificAccessRequestAdmin","reviewSpecificAccessRequestCTSC",
        "processApplicationToReviewDecision", "detainedProcessApplicationToReviewDecision",
        "sendDecisionsAndReasons","detainedSendDecisionsAndReasons", "prepareDecisionsAndReasons", "decideAnFTPA",
        "reviewSetAsideDecisionApplication", "detainedReviewSetAsideDecisionApplication"
    })
    void when_taskId_then_return_judicial_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();
        inputVariables.putValue("taskAttributes", Map.of(
            "taskType", taskType,
            "additionalProperties", Map.of("roleAssignmentId", roleAssignmentId)
        ));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter(r -> "roleCategory".equals(r.get("name")))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "roleCategory",
            "value", "JUDICIAL",
            "canReconfigure", false
        ), workTypeResultList.get(0));
    }

    @ParameterizedTest
    @CsvSource({

        "arrangeOfflinePayment", "markCaseAsPaid", "allocateHearingJudge", "detainedAllocateHearingJudge",
        "uploadHearingRecording", "postHearingAttendeesDurationAndRecording",
        "detainedPostHearingAttendeesDurationAndRecording",
        "editListing", "detainedEditListing", "followUpSetAsideDecision",
        "hearingException", "cmrListed", "cmrUpdated", "detainedCmrListed", "detainedCmrUpdated","relistCase",
        "reviewInterpreters","reviewMigratedCase","detainedReviewMigratedCase","reviewAriaRemissionApplication",
        "printAndSendHoBundle","detainedPrintAndSendHoBundle","printAndSendHoResponse",
        "printAndSendHearingRequirements","detainedPrintAndSendHearingRequirements",
        "printAndSendHearingBundle","detainedPrintAndSendHearingBundle",
        "printAndSendDecisionCorrectedRule31","detainedPrintAndSendDecisionCorrectedRule31",
        "printAndSendDecisionCorrectedRule32","detainedPrintAndSendDecisionCorrectedRule32",
        "printAndSendHoApplication","detainedPrintAndSendHoApplication",
        "printAndSendHoEvidence","detainedPrintAndSendHoEvidence",
        "printAndSendAppealDecision","printAndSendFTPADecision","detainedPrintAndSendAppealDecision",
        "detainedPrintAndSendFTPADecision",
        "printAndSendReheardHearingRequirements","detainedPrintAndSendReheardHearingRequirements",
        "detainedListCmr","detainedReviewInterpreters"
    })
    void when_taskId_then_return_Admin_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        if (taskType.equals("reviewMigratedCase") || taskType.equals("detainedReviewMigratedCase")) {
            inputVariables.putValue("caseData", Map.of("ariaMigrationTaskDueDays", "10"));
        }

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "roleCategory",
            "value", "ADMIN",
            "canReconfigure", false
        ), workTypeResultList.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        "reviewRemissionApplication","assignAFTPAJudge","detainedAssignAFTPAJudge","listTheCase",
        "sendPaymentRequest","markAsPaid","detainedListTheCase",
        "processFeeRefund", "reviewDraftAppeal","detainedReviewRemissionApplication"
    })
    void when_taskId_then_return_Ctsc_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "roleCategory",
            "value", "CTSC",
            "canReconfigure", false
        ), workTypeResultList.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        "processApplicationAdjourn", "detainedProcessApplicationAdjourn", "processApplicationExpedite",
        "detainedProcessApplicationExpedite","processApplicationTimeExtension",
        "detainedProcessApplicationTimeExtension",
        "processApplicationTransfer", "detainedProcessApplicationTransfer", "processApplicationWithdraw",
        "detainedProcessApplicationWithdraw", "processApplicationUpdateHearingRequirements",
        "detainedProcessApplicationUpdateHearingRequirements",
        "processApplicationUpdateAppealDetails", "detainedProcessApplicationUpdateAppealDetails",
        "processApplicationReinstateAnEndedAppeal", "detainedProcessApplicationReinstateAnEndedAppeal",
        "processApplicationOther",
        "detainedProcessApplicationOther",
        "processApplicationLink/UnlinkAppeals", "detainedProcessApplicationLink/UnlinkAppeals",
        "processApplicationChangeHearingType", "detainedProcessApplicationChangeHearingType", "reviewTheAppeal",
        "detainedReviewTheAppeal",
        "decideOnTimeExtension", "reviewRespondentEvidence",
        "detainedReviewRespondentEvidence,"
            + "[Request reasons for appeal](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestReasonsForAppeal)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),"
            + "aip,,",
        "reviewAppealSkeletonArgument","detainedReviewAppealSkeletonArgument", "reviewReasonsForAppeal",
        "reviewClarifyingQuestionsAnswers", "reviewAdditionalHomeOfficeEvidence",
        "reviewCmaRequirements", "attendCma", "reviewRespondentResponse","detainedReviewRespondentResponse",
        "caseSummaryHearingBundleStartDecision",
        "detainedCaseSummaryHearingBundleStartDecision",
        "reviewHearingRequirements","detainedReviewHearingRequirements", "followUpOverdueRespondentEvidence",
        "detainedFollowUpOverdueRespondentEvidence",
        "followUpOverdueCaseBuilding","detainedFollowUpOverdueCaseBuilding", "followUpOverdueReasonsForAppeal",
        "followUpOverdueClarifyingAnswers",
        "followUpOverdueCmaRequirements", "followUpOverdueRespondentReview","detainedFollowUpOverdueRespondentReview",
        "followUpOverdueHearingRequirements","detainedFollowUpOverdueHearingRequirements",
        "followUpNonStandardDirection","detainedFollowUpNonStandardDirection", "followUpNoticeOfChange",
        "detainedFollowUpNoticeOfChange",
        "reviewAdditionalEvidence", "detainedReviewAdditionalEvidence","reviewAdditionalHomeOfficeEvidence",
        "detainedReviewAdditionalHomeOfficeEvidence", "reviewRemittedAppeal","detainedReviewRemittedAppeal", "reviewAppealSetAsideUnderRule35",
        "reviewAppealSetAsideUnderRule32","detainedReviewAppealSetAsideUnderRule32"
    })
    void when_taskId_then_return_legal_operations_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "roleCategory",
            "value", "LEGAL_OPERATIONS",
            "canReconfigure", false
        ), workTypeResultList.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        "followUpExtendedDirection","detainedFollowUpExtendedDirection",
        "createHearingBundle", "createCaseSummary", "reviewAddendumEvidence", "detainedReviewAddendumEvidence"
    })
    void when_taskId_then_return_legal_operations_role_category_can_reconfigure(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(Map.of(
            "name", "roleCategory",
            "value", "LEGAL_OPERATIONS",
            "canReconfigure", true
        ), workTypeResultList.get(0));
    }

    @ParameterizedTest
    @MethodSource("nameAndValueScenarioProvider")
    void when_caseData_and_taskType_then_return_expected_name_and_value_rows(Scenario scenario) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", scenario.caseData);
        inputVariables.putValue("taskAttributes", scenario.getTaskAttributes());

        List<Map<String, Object>> expected = getExpectedValues(scenario);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList().size(), is(expected.size()));
        for (int index = 0; index < expected.size(); index++) {
            if ("dueDateOrigin".equals(expected.get(index).get("name"))) {
                assertTrue(validNow(
                    ZonedDateTime.parse(expected.get(index).get("value").toString()),
                    parseCamundaTimestamp(dmnDecisionTableResult.getResultList().get(index).get("value").toString())
                ));
            } else {
                assertThat(dmnDecisionTableResult.getResultList().get(index), is(expected.get(index)));
            }
        }
    }

    private static Stream<Scenario> nameAndValueScenarioProvider() {
        String dateOrigin = ZonedDateTime.now(ZoneId.of("UTC")).toString();
        Scenario givenCaseDataIsMissedThenDefaultToTaylorHouseScenario = Scenario.builder()
            .caseData(emptyMap())
            .expectedCaseNameValue(null)
            .expectedAppealTypeValue("")
            .expectedRegionValue("1")
            .expectedLocationValue("227101")
            .expectedLocationNameValue("Newport")
            .expectedCaseManagementCategoryValue("")
            .expectedDescriptionValue("")
            .expectedReconfigureValue("true")
            .expectedDueDateOrigin(dateOrigin)
            .build();

        String refusalOfEuLabel = "Refusal of a human rights claim";
        Scenario givenCaseDataIsPresentThenReturnNameAndValueScenario = Scenario.builder()
            .caseData(Map.of(
                "appealType", "refusalOfHumanRights",
                "appellantGivenNames", "some appellant given names",
                "appellantFamilyName", "some appellant family name",
                "caseManagementLocation", Map.of(
                    "region", "some other region",
                    "baseLocation", "some other location"
                ),
                "staffLocation", "some other location name",
                "caseManagementCategory", Map.of(
                    "value", Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"),
                    "list_items", List.of(Map.of("code", "refusalOfHumanRights", "label", refusalOfEuLabel))
                )
            ))
            .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
            .expectedCaseNameValue("some appellant given names some appellant family name")
            .expectedAppealTypeValue("Human rights")
            .expectedRegionValue("some other region")
            .expectedLocationValue("some other location")
            .expectedLocationNameValue("some other location name")
            .expectedCaseManagementCategoryValue("Human rights")
            .expectedWorkType("routine_work")
            .expectedReconfigureValue("true")
            .expectedRoleCategory("ADMIN")
            .expectedDescriptionValue(
                "[Mark the appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
            .expectedDueDateOrigin(dateOrigin)
            .build();

        Scenario givenSomeCaseDataAndArrangeOfflinePaymentTaskIdThenReturnExpectedNameAndValueScenario =
            Scenario.builder()
                .caseData(Map.of(
                    "appealType", "refusalOfHumanRights",
                    "appellantGivenNames", "some appellant given names",
                    "appellantFamilyName", "some appellant family name",
                    "caseManagementLocation", Map.of(
                        "region", "some other region",
                        "baseLocation", "some other location"
                    ),
                    "staffLocation", "some other location name",
                    "caseManagementCategory", Map.of(
                        "value", Map.of(
                            "code", "refusalOfHumanRights",
                            "label", "Refusal of a human rights claim"
                        ),
                        "list_items", List.of(Map.of(
                            "code", "refusalOfHumanRights",
                            "label", refusalOfEuLabel
                        ))
                    )
                ))
                .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
                .expectedCaseNameValue("some appellant given names some appellant family name")
                .expectedAppealTypeValue("Human rights")
                .expectedRegionValue("some other region")
                .expectedLocationValue("some other location")
                .expectedLocationNameValue("some other location name")
                .expectedCaseManagementCategoryValue("Human rights")
                .expectedReconfigureValue("true")
                .expectedWorkType("routine_work")
                .expectedRoleCategory("ADMIN")
                .expectedDescriptionValue("[Mark the appeal as "
                                              + "paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario givenSomeCaseDataAndTaskTypeIsEmptyThenExpectNoWorkTypeRuleScenario =
            Scenario.builder()
                .caseData(Map.of(
                    "appealType", "refusalOfHumanRights",
                    "appellantGivenNames", "some appellant given names",
                    "appellantFamilyName", "some appellant family name",
                    "caseManagementLocation", Map.of(
                        "region", "some other region",
                        "baseLocation", "some other location"
                    ),
                    "staffLocation", "some other location name",
                    "caseManagementCategory", Map.of(
                        "value", Map.of(
                            "code", "refusalOfHumanRights",
                            "label", "Refusal of a human rights claim"
                        ),
                        "list_items", List.of(Map.of(
                            "code", "refusalOfHumanRights",
                            "label", refusalOfEuLabel
                        ))
                    )
                ))
                .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
                .expectedCaseNameValue("some appellant given names some appellant family name")
                .expectedAppealTypeValue("Human rights")
                .expectedRegionValue("some other region")
                .expectedLocationValue("some other location")
                .expectedLocationNameValue("some other location name")
                .expectedCaseManagementCategoryValue("Human rights")
                .expectedWorkType("routine_work")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("ADMIN")
                .expectedDescriptionValue("[Mark the appeal as "
                                              + "paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario givenNoCaseDataAndSomeTaskTypeThenExpectOnlyTheWorkTypeRuleScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("routine_work")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("ADMIN")
                .expectedDescriptionValue("[Mark the appeal as "
                                              + "paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario processApplicationAdjournScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationAdjourn"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationAdjournScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationAdjourn"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedIsDetainedAppellant(true)
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationExpediteScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationExpedite"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationExpediteScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationExpedite"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationTimeExtensionScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationTimeExtension"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationTimeExtensionScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationTimeExtension"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedIsDetainedAppellant(true)
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationTransferScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationTransfer"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationTransferScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationTransfer"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationWithdrawScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationWithdraw"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationWithdrawScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationWithdraw"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationUpdateHearingRequirementsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationUpdateHearingRequirements"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationUpdateHearingRequirementsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationUpdateHearingRequirements"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationUpdateAppealDetailsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationUpdateAppealDetails"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationUpdateAppealDetailsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationUpdateAppealDetails"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationReinstateAnEndedAppealScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationReinstateAnEndedAppeal"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationReinstateAnEndedAppealScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationReinstateAnEndedAppeal"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationOtherScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationOther"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationOtherScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationOtherScenario"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationLinkUnlinkAppealsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationLink/UnlinkAppeals"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationLinkUnlinkAppealsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationLink/UnlinkAppeals"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();
        Scenario processApplicationChangeHearingTypeScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationChangeHearingType"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                          + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario detainedProcessApplicationChangeHearingTypeScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationChangeHearingTypeScenario"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        return Stream.of(
            givenCaseDataIsMissedThenDefaultToTaylorHouseScenario,
            givenCaseDataIsPresentThenReturnNameAndValueScenario,
            givenSomeCaseDataAndArrangeOfflinePaymentTaskIdThenReturnExpectedNameAndValueScenario,
            givenSomeCaseDataAndTaskTypeIsEmptyThenExpectNoWorkTypeRuleScenario,
            givenNoCaseDataAndSomeTaskTypeThenExpectOnlyTheWorkTypeRuleScenario,
            processApplicationAdjournScenario,
            detainedProcessApplicationAdjournScenario,
            processApplicationExpediteScenario,
            processApplicationTimeExtensionScenario,
            //detainedProcessApplicationTimeExtensionScenario,
            processApplicationTransferScenario,
            detainedProcessApplicationTransferScenario,
            processApplicationWithdrawScenario,
            detainedProcessApplicationWithdrawScenario,
            processApplicationUpdateHearingRequirementsScenario,
            detainedProcessApplicationUpdateHearingRequirementsScenario,
            processApplicationUpdateAppealDetailsScenario,
            detainedProcessApplicationUpdateAppealDetailsScenario,
            processApplicationReinstateAnEndedAppealScenario,
            detainedProcessApplicationReinstateAnEndedAppealScenario,
            processApplicationOtherScenario,
            //detainedProcessApplicationOtherScenario,
            processApplicationLinkUnlinkAppealsScenario,
            processApplicationChangeHearingTypeScenario
            //detainedProcessApplicationChangeHearingTypeScenario
        );
    }

    @Value
    @Builder
    private static class Scenario {
        Map<String, Object> caseData;
        Map<String, Object> taskAttributes;
        String expectedCaseNameValue;
        String expectedAppealTypeValue;
        String expectedRegionValue;
        String expectedLocationValue;
        String expectedLocationNameValue;
        String expectedCaseManagementCategoryValue;
        String expectedWorkType;
        String expectedRoleCategory;
        String expectedDescriptionValue;
        String expectedReconfigureValue;
        String expectedDueDateOrigin;
        String expectedDueDateIntervalDays;
        String expectedHearingId;
        String expectedHearingDate;
        Boolean expectedIsDetainedAppellant;
    }

    private List<Map<String, Object>> getExpectedValues(Scenario scenario) {
        List<Map<String, Object>> rules = new ArrayList<>();

        getExpectedValueWithReconfigure(
            rules,
            "caseName",
            scenario.getExpectedCaseNameValue(),
            scenario.getExpectedReconfigureValue()
        );
        getExpectedValue(rules, "appealType", scenario.getExpectedAppealTypeValue());
        getExpectedValue(rules, "region", scenario.getExpectedRegionValue());
        getExpectedValueWithReconfigure(
            rules,
            "location",
            scenario.getExpectedLocationValue(),
            scenario.getExpectedReconfigureValue()
        );
        getExpectedValueWithReconfigure(
            rules,
            "locationName",
            scenario.getExpectedLocationNameValue(),
            scenario.getExpectedReconfigureValue()
        );
        getExpectedValue(rules, "caseManagementCategory", scenario.getExpectedCaseManagementCategoryValue());
        if (!Objects.isNull(scenario.getTaskAttributes())
            && StringUtils.isNotBlank(scenario.taskAttributes.get("taskType").toString())) {
            getExpectedValue(rules, "workType", scenario.getExpectedWorkType());
            getExpectedValue(rules, "roleCategory", scenario.getExpectedRoleCategory());
        }

        getExpectedValue(rules, "description", scenario.getExpectedDescriptionValue());
        getExpectedValue(rules, "dueDateOrigin", scenario.getExpectedDueDateOrigin());
        getExpectedValue(rules, "dueDateNonWorkingCalendar", DEFAULT_CALENDAR + ", " + EXTRA_TEST_CALENDAR);
        if (!Objects.isNull(scenario.getExpectedDueDateIntervalDays())) {
            getExpectedValue(rules, "dueDateIntervalDays", scenario.getExpectedDueDateIntervalDays());
        }
        getExpectedValue(rules, "majorPriority", String.valueOf(5000));
        getExpectedValue(rules, "minorPriority", String.valueOf(500));
        getExpectedValue(rules, "priorityDateOriginRef", "dueDate");
        getExpectedValue(rules, "dueDateNonWorkingDaysOfWeek", "SATURDAY,SUNDAY");
        getExpectedValue(rules, "calculatedDates", "nextHearingDate,dueDate,priorityDate");
        getExpectedValueWithReconfigure(
            rules,
            "nextHearingId",
            "",
            "true"
        );
        getExpectedValueWithReconfigure(
            rules,
            "nextHearingDate",
            "",
            "true"
        );
        return rules;
    }

    private void getExpectedValue(List<Map<String, Object>> rules, String name, String value) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", false);
        rules.add(rule);
    }

    private void getExpectedValueWithReconfigure(List<Map<String, Object>> rules, String name, String value,
                                                 String reconfigure) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", Boolean.valueOf(reconfigure));
        rules.add(rule);
    }

    private ZonedDateTime parseCamundaTimestamp(String datetime) {
        String[] parts = datetime.split("[Z+]");
        String zone = datetime.substring(datetime.indexOf("[") + 1, datetime.lastIndexOf("]"));
        return ZonedDateTime.of(LocalDateTime.parse(parts[0]), ZoneId.of(zone));
    }

    private boolean validNow(ZonedDateTime expected, ZonedDateTime actual) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return actual != null
            && (expected.isEqual(actual) || expected.isBefore(actual))
            && (now.isEqual(actual) || now.isAfter(actual));
    }

    @ParameterizedTest
    @CsvSource({
        "processApplicationAdjourn,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationAdjourn,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationExpedite,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationTimeExtension,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationTimeExtension,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationTransfer,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationTransfer,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationWithdraw,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationWithdraw,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationUpdateHearingRequirements,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationUpdateHearingRequirements,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationUpdateAppealDetails,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationUpdateAppealDetails,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationReinstateAnEndedAppeal,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationReinstateAnEndedAppeal,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationOther,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationOther,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationLink/UnlinkAppeals,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationLink/UnlinkAppeals,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationChangeHearingType,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "reviewTheAppeal,[Request respondent evidence]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentEvidence),,,",
        "detainedReviewTheAppeal,[Request respondent evidence]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentEvidence),,,",
        "decideOnTimeExtension,"
            + "[Change the direction due date](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/changeDirectionDueDate),,,",
        "reviewRespondentEvidence,"
            + "[Request reasons for appeal](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestReasonsForAppeal)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),"
            + "aip,,",
        "detainedReviewRespondentEvidence,"
            + "[Request reasons for appeal](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestReasonsForAppeal)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),"
            + "aip,,",
        "reviewRespondentEvidence,"
            + "[Request case building](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCaseBuilding)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),,,",
        "reviewAppealSkeletonArgument,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Request case edit](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCaseEdit),,,",
        "detainedReviewAppealSkeletonArgument,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Request case edit](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCaseEdit),,,",
        "reviewReasonsForAppeal,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Send direction with questions]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirectionWithQuestions),"
            + "aip,,",
        "reviewReasonsForAppeal,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Request CMA requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCmaRequirements),,,",
        "reviewCmaRequirements,"
            + "[Review CMA Requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/reviewCmaRequirements),,,",
        "attendCma,"
            + "[Update case details after CMA](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/updateDetailsAfterCma),,,",
        "reviewRespondentResponse,"
            + "[Review Home Office response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseReview)<br />[Amend appeal response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseAmend),,,",
        "detainedReviewRespondentResponse,"
            + "[Review Home Office response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseReview)<br />[Amend appeal response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseAmend),,,",
        "createHearingBundle,"
            + "[Generate the hearing bundle](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger"
            + "/generateHearingBundle)<br />"
            + "[Customise the hearing bundle](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/customiseHearingBundle),,,",
        "startDecisionsAndReasonsDocument,"
            + "[Start decision and reasons document](/case/IA/Asylum/${[CASE_REFERENCE]}"
            + "/trigger/decisionAndReasonsStarted/decisionAndReasonsStartedcaseIntroduction),,,",
        "reviewHearingRequirements,"
            + "[Review hearing requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger"
            + "/reviewHearingRequirements),,,",
        "detainedReviewHearingRequirements,"
            + "[Review hearing requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger"
            + "/reviewHearingRequirements),,,",
        "reviewAdditionalEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "detainedReviewAdditionalEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "reviewAdditionalHomeOfficeEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "detainedReviewAdditionalHomeOfficeEvidence,"
            + "[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "arrangeOfflinePayment,[Mark the appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markAppealPaid),,,",
        "markCaseAsPaid,[Mark the appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid),,,",
        "allocateHearingJudge," + "[Allocate Hearing Judge](/role-access/allocate-role/allocate?caseId="
            + "${[CASE_REFERENCE]}&roleCategory=JUDICIAL&jurisdiction=IA),,,",
        "detainedAllocateHearingJudge," + "[Allocate Hearing Judge](/role-access/allocate-role/allocate?caseId="
            + "${[CASE_REFERENCE]}&roleCategory=JUDICIAL&jurisdiction=IA),,,",
        "uploadHearingRecording,[Upload the hearing recording](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/upload"
            + "HearingRecording),,,",
        "generateDraftDecisionAndReasons,[Generate the draft decisions and reasons document](/case/IA/Asylum"
            + "/${[CASE_REFERENCE]}/trigger/generateDecisionAndReasons),,,",
        "reviewAddendumEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markAddendumEvidenceAsReviewed),,,",
        "detainedReviewAddendumEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markAddendumEvidenceAsReviewed),,,",
        "editListing,[Edit case listing](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/editCaseListing),,,",
        "detainedEditListing,[Edit case listing](/case/IA/Asylum/${[CASE_REFERENCE]}"
            + "/trigger/editCaseListing),,,",
        "decideAnFTPA,[Decide FTPA application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideFtpaApplication),,,",
        "detainedDecideAnFTPA,[Decide FTPA application](/case/IA/Asylum/${[CASE_REFERENCE]}"
            + "/trigger/decideFtpaApplication),,,",
        "prepareDecisionsAndReasons,[Prepare decisions and reasons](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "generateDecisionAndReasons),,,",
        "sendDecisionsAndReasons,[Complete decision and reasons](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "sendDecisionAndReasons),,,",
        "detainedSendDecisionsAndReasons,[Complete decision and reasons](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "sendDecisionAndReasons),,,",
        "processApplicationToReviewDecision,[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "decideAnApplication),,,",
        "detainedProcessApplicationToReviewDecision,[Decide an application]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "decideAnApplication),,,",
        "reviewRemissionApplication,[Record remission decision](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewRemissionApplication,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "assignAFTPAJudge,[Record allocated Judge](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "recordAllocatedJudge),,,",
        "detainedAssignAFTPAJudge,[Record allocated Judge](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "recordAllocatedJudge),,,",
        "listTheCase,[List the case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/listCase),,No,",
        "listTheCase,[List the case](cases/case-details/${[CASE_REFERENCE]}/hearings),,Yes,",
        "detainedListTheCase,[List the case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/listCase),,No,",
        "detainedListTheCase,[List the case](cases/case-details/${[CASE_REFERENCE]}/hearings),,Yes,",
        "sendPaymentRequest,[Mark payment request sent](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markPaymentRequestSent),,,",
        "markAsPaid,[Mark appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid),,,",
        "reviewMigratedCase,[Progress migrated case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "progressMigratedCase),,,10",
        "detainedReviewMigratedCase,[Progress migrated case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "progressMigratedCase),,,10",
        "hearingException,[Go to case](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "cmrListed,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedCmrListed,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "cmrUpdated,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedCmrUpdated,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "relistCase,[Relist the hearing](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "reviewInterpreters,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedReviewInterpreters,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,"

    })
    void should_return_a_200_description_property(String taskType, String expectedDescription, String journeyType,
                                                  String isIntegrated, String ariaTaskDueDays) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        Map<String, String> taskAttributes = Map.of("taskType", taskType,
                                                    "roleAssignmentId", roleAssignmentId,
                                                    "taskId", taskId);
        inputVariables.putValue("taskAttributes", taskAttributes);
        if (journeyType != null) {
            inputVariables.putValue("caseData", Map.of("journeyType", journeyType));
        }
        if (isIntegrated != null) {
            inputVariables.putValue("caseData", Map.of("isIntegrated", isIntegrated));
        }
        if (ariaTaskDueDays != null) {
            inputVariables.putValue("caseData", Map.of("ariaMigrationTaskDueDays", ariaTaskDueDays));
        }

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> descriptionList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("description"))
            .toList();

        assertEquals(1, descriptionList.size());

        assertEquals(Map.of(
            "name", "description",
            "value", expectedDescription
                .replace("${[roleAssignmentId]}", roleAssignmentId).replace("${[taskId]}", taskId),
            "canReconfigure", false
        ), descriptionList.get(0));

    }

    @ParameterizedTest
    @MethodSource("dueDateIntervalDaysScenarioProvider")
    void when_taskId_then_return_dueDateIntervalDays(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dueDateIntervalDaysResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("dueDateIntervalDays"))
            .toList();

        assertEquals(expected, dueDateIntervalDaysResultList);
    }

    public static Stream<Arguments> dueDateIntervalDaysScenarioProvider() {
        List<Map<String, Object>> fiveDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "5",
            "canReconfigure", false
        ));
        List<Map<String, Object>> threeDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "3",
            "canReconfigure", false
        ));
        List<Map<String, Object>> twoDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "2",
            "canReconfigure", false
        ));
        List<Map<String, Object>> zeroDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "0",
            "canReconfigure", false
        ));
        List<Map<String, Object>> fourteenDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "14",
            "canReconfigure", false
        ));

        return Stream.of(
            Arguments.of("processApplicationAdjourn", fiveDays),
            Arguments.of("detainedProcessApplicationAdjourn", fiveDays),
            Arguments.of("processApplicationExpedite", fiveDays),
            Arguments.of("detainedProcessApplicationExpedite", fiveDays),
            Arguments.of("processApplicationTimeExtension", fiveDays),
            Arguments.of("processApplicationTransfer", fiveDays),
            Arguments.of("processApplicationWithdraw", fiveDays),
            Arguments.of("detainedProcessApplicationWithdraw", fiveDays),
            Arguments.of("processApplicationUpdateHearingRequirements", fiveDays),
            Arguments.of("detainedProcessApplicationUpdateHearingRequirements", fiveDays),
            Arguments.of("processApplicationUpdateAppealDetails", fiveDays),
            Arguments.of("processApplicationReinstateAnEndedAppeal", fiveDays),
            Arguments.of("processApplicationOther", fiveDays),
            Arguments.of("processApplicationLink/UnlinkAppeals", fiveDays),
            Arguments.of("reviewRemittedAppeal", fiveDays),
            Arguments.of("detainedReviewRemittedAppeal", fiveDays),
            Arguments.of("reviewAppealSetAsideUnderRule35", fiveDays),
            Arguments.of("reviewAppealSetAsideUnderRule32", fiveDays),
            Arguments.of("detainedReviewAppealSetAsideUnderRule32", fiveDays),
            Arguments.of("reviewDraftAppeal", fiveDays),
            Arguments.of("processFeeRefund", fiveDays),
            Arguments.of("allocateHearingJudge", threeDays),
            Arguments.of("detainedAllocateHearingJudge", threeDays),
            Arguments.of("processApplicationToReviewDecision", twoDays),
            Arguments.of("detainedProcessApplicationToReviewDecision", twoDays),
            Arguments.of("editListing", twoDays),
            Arguments.of("reviewTheAppeal", twoDays),
            Arguments.of("detainedReviewTheAppeal", twoDays),
            Arguments.of("decideOnTimeExtension", twoDays),
            Arguments.of("reviewRespondentEvidence", twoDays),
            Arguments.of("detainedReviewRespondentEvidence", twoDays),
            Arguments.of("reviewAdditionalEvidence", twoDays),
            Arguments.of("detainedReviewAdditionalEvidence", twoDays),
            Arguments.of("reviewAdditionalHomeOfficeEvidence", twoDays),
            Arguments.of("detainedReviewAdditionalHomeOfficeEvidence", twoDays),
            Arguments.of("reviewAppealSkeletonArgument", twoDays),
            Arguments.of("detainedReviewAppealSkeletonArgument", twoDays),
            Arguments.of("reviewReasonsForAppeal", twoDays),
            Arguments.of("reviewClarifyingQuestionsAnswers", twoDays),
            Arguments.of("reviewCmaRequirements", twoDays),
            Arguments.of("attendCma", twoDays),
            Arguments.of("reviewRespondentResponse", twoDays),
            Arguments.of("detainedReviewRespondentResponse", twoDays),
            Arguments.of("caseSummaryHearingBundleStartDecision", twoDays),
            Arguments.of("detainedCaseSummaryHearingBundleStartDecision", twoDays),
            Arguments.of("reviewHearingRequirements", twoDays),
            Arguments.of("detainedReviewHearingRequirements", twoDays),
            Arguments.of("followUpOverdueRespondentEvidence", twoDays),
            Arguments.of("detainedFollowUpOverdueRespondentEvidence", twoDays),
            Arguments.of("followUpExtendedDirection", twoDays),
            Arguments.of("detainedFollowUpExtendedDirection", twoDays),
            Arguments.of("followUpOverdueCaseBuilding", twoDays),
            Arguments.of("detainedFollowUpOverdueCaseBuilding", twoDays),
            Arguments.of("followUpOverdueReasonsForAppeal", twoDays),
            Arguments.of("followUpOverdueClarifyingAnswers", twoDays),
            Arguments.of("followUpOverdueCmaRequirements", twoDays),
            Arguments.of("followUpOverdueRespondentReview", twoDays),
            Arguments.of("detainedFollowUpOverdueRespondentReview", twoDays),
            Arguments.of("followUpOverdueHearingRequirements", twoDays),
            Arguments.of("detainedFollowUpOverdueHearingRequirements", twoDays),
            Arguments.of("followUpNonStandardDirection", twoDays),
            Arguments.of("detainedFollowUpNonStandardDirection", twoDays),
            Arguments.of("followUpNoticeOfChange", twoDays),
            Arguments.of("detainedFollowUpNoticeOfChange", twoDays),
            Arguments.of("reviewAddendumEvidence", twoDays),
            Arguments.of("detainedReviewAddendumEvidence", twoDays),
            Arguments.of("reviewRemissionApplication", twoDays),
            Arguments.of("detainedReviewRemissionApplication", twoDays),
            Arguments.of("assignAFTPAJudge", twoDays),
            Arguments.of("detainedAssignAFTPAJudge", twoDays),
            Arguments.of("listTheCase", twoDays),
            Arguments.of("detainedListTheCase", twoDays),
            Arguments.of("reviewSetAsideDecisionApplication", twoDays),
            Arguments.of("detainedReviewSetAsideDecisionApplication", twoDays),
            Arguments.of("reviewAriaRemissionApplication", twoDays),
            Arguments.of("sendDecisionsAndReasons", zeroDays),
            Arguments.of("detainedSendDecisionsAndReasons", zeroDays),
            Arguments.of("prepareDecisionsAndReasons", zeroDays),
            Arguments.of("sendPaymentRequest", zeroDays),
            Arguments.of("uploadHearingRecording", zeroDays),
            Arguments.of("postHearingAttendeesDurationAndRecording", zeroDays),
            Arguments.of("detainedPostHearingAttendeesDurationAndRecording", zeroDays),
            Arguments.of("decideAnFTPA", zeroDays),
            Arguments.of("printAndSendHoBundle", zeroDays),
            Arguments.of("detainedPrintAndSendHoBundle", zeroDays),
            Arguments.of("printAndSendHoResponse", zeroDays),
            Arguments.of("printAndSendHearingRequirements", zeroDays),
            Arguments.of("detainedPrintAndSendHearingRequirements", zeroDays),
            Arguments.of("printAndSendHearingBundle", zeroDays),
            Arguments.of("detainedPrintAndSendHearingBundle", zeroDays),
            Arguments.of("printAndSendDecisionCorrectedRule31", zeroDays),
            Arguments.of("detainedPrintAndSendDecisionCorrectedRule31", zeroDays),
            Arguments.of("printAndSendDecisionCorrectedRule32", zeroDays),
            Arguments.of("detainedPrintAndSendDecisionCorrectedRule32", zeroDays),
            Arguments.of("printAndSendHoApplication", zeroDays),
            Arguments.of("detainedPrintAndSendHoApplication", zeroDays),
            Arguments.of("printAndSendHoEvidence", zeroDays),
            Arguments.of("detainedPrintAndSendHoEvidence", zeroDays),
            Arguments.of("printAndSendAppealDecision", zeroDays),
            Arguments.of("detainedPrintAndSendAppealDecision", zeroDays),
            Arguments.of("printAndSendFTPADecision", zeroDays),
            Arguments.of("detainedPrintAndSendFTPADecision", zeroDays),
            Arguments.of("printAndSendReheardHearingRequirements", zeroDays),
            Arguments.of("detainedPrintAndSendReheardHearingRequirements", zeroDays),
            Arguments.of("markAsPaid", fourteenDays),
            Arguments.of("cmrListed", twoDays),
            Arguments.of("detainedCmrUpdated", twoDays)
        );
    }

    @Test
    void when_any_task_then_return_expected_priorities_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "processApplicationAdjourn"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "minorPriority",
            "value", "500",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "majorPriority",
            "value", "5000",
            "canReconfigure", false
        )));
    }

    @Test
    void detained_when_any_task_then_return_expected_priorities_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "detainedProcessApplicationAdjourn"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "minorPriority",
            "value", "500",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "majorPriority",
            "value", "5000",
            "canReconfigure", false
        )));
    }

    @Test
    void when_any_task_then_return_expected_non_working_days_of_week_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "processApplicationAdjourn"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "dueDateNonWorkingDaysOfWeek",
            "value", "SATURDAY,SUNDAY",
            "canReconfigure", false
        )));
    }

    @ParameterizedTest
    @CsvSource({
        "markAsPaid"
    })
    void when_taskId_then_return_due_date_skip_non_working_days_false(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dueDateSkipNonWorkingDaysResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("dueDateSkipNonWorkingDays"))
            .toList();

        assertEquals(1, dueDateSkipNonWorkingDaysResultList.size());

        assertEquals(Map.of(
            "name", "dueDateSkipNonWorkingDays",
            "value", "false",
            "canReconfigure", false
        ), dueDateSkipNonWorkingDaysResultList.get(0));
    }

    @ParameterizedTest
    @MethodSource("customDueDateIntervalDaysScenarioProvider")
    void when_taskId_then_return_customDueDateIntervalDays(String taskType, String fieldName, String dueDays) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        caseData.put(fieldName, dueDays);
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "dueDateIntervalDays",
            "value", dueDays,
            "canReconfigure", false
        )));
    }

    public static Stream<Arguments> customDueDateIntervalDaysScenarioProvider() {

        return Stream.of(
            Arguments.of("reviewMigratedCase", "ariaMigrationTaskDueDays", "10"),
            Arguments.of("detainedReviewMigratedCase", "ariaMigrationTaskDueDays", "10")

        );
    }
}

