package uk.gov.hmcts.reform.iataskconfiguration.dmn;

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
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_IA_BAIL;

class CamundaTaskBailConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String DEFAULT_CALENDAR = "https://www.gov.uk/bank-holidays/england-and-wales.json";
    private static final String EXTRA_TEST_CALENDAR = "https://raw.githubusercontent.com/hmcts/"
        + "ia-task-configuration/master/src/test/resources/extra-non-working-day-calendar.json";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_IA_BAIL;
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(17));
    }

    @Test
    void when_case_data_name_then_return_case_name() {
        String randomString = UUID.randomUUID().toString();
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        caseData.put("caseNameHmctsInternal", randomString);
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "caseName",
            "value", randomString,
            "canReconfigure", true
        )));
    }

    @Test
    void when_case_data_name_null_then_return_empty() {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "caseName",
            "value", "",
            "canReconfigure", true
        )));
    }

    @Test
    void when_case_data_next_hearing_details_then_return_Id_and_date() {
        String randomString = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> nextHearingDetails = new HashMap<>();
        nextHearingDetails.put("hearingID", randomString);
        nextHearingDetails.put("hearingDateTime", now);
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        caseData.put("nextHearingDetails", nextHearingDetails);
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingId",
            "value", randomString,
            "canReconfigure", true
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingDate",
            "value", now.toString(),
            "canReconfigure", true
        )));
    }

    @Test
    void when_case_data_next_hearing_details_null_fields_then_return_empty() {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        caseData.put("nextHearingDetails", new HashMap<>());
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingId",
            "value", "",
            "canReconfigure", true
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingDate",
            "value", "",
            "canReconfigure", true
        )));
    }

    @Test
    void when_case_data_next_hearing_details_null_value_then_return_empty() {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingId",
            "value", "",
            "canReconfigure", true
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingDate",
            "value", "",
            "canReconfigure", true
        )));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "someRegion, someEpimmsId, someLocationName",
        "null, null, null"
    }, nullValues = "null")
    void when_case_data_caseManagementLocation_then_return_fields(String region,
                                                                  String baseLocationCode,
                                                                  String baseLocationLabel) {
        Map<String, Object> caseManagementLocation = new HashMap<>(); // allow null values
        caseManagementLocation.put("region", region);
        caseManagementLocation.put("baseLocationCode", baseLocationCode);
        caseManagementLocation.put("baseLocationLabel", baseLocationLabel);
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        caseData.put("caseManagementLocation", caseManagementLocation);
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        String expectedRegion = region != null ? region : "1";
        String expectedBaseLocationCode = baseLocationCode != null ? baseLocationCode : "227101";
        String expectedBaseLocationLabel = baseLocationLabel != null ? baseLocationLabel : "Newport";
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "region",
            "value", expectedRegion,
            "canReconfigure", false
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "location",
            "value", expectedBaseLocationCode,
            "canReconfigure", true
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "locationName",
            "value", expectedBaseLocationLabel,
            "canReconfigure", true
        )));
    }

    @Test
    void when_case_data_caseManagementLocation_null_then_return_fields() {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", new HashMap<>());

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "region",
            "value", "1",
            "canReconfigure", false
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "location",
            "value", "227101",
            "canReconfigure", true
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "locationName",
            "value", "Newport",
            "canReconfigure", true
        )));
    }

    @Test
    void when_task_type_then_sets_work_type_and_role_category() {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskType", "processBailApplication");

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "workType",
            "value", "bail_work",
            "canReconfigure", false
        )));
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "roleCategory",
            "value", "ADMIN",
            "canReconfigure", true
        )));
    }

    @Test
    void when_unknown_task_type_then_does_not_set_work_type_and_role_category() {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskType", "unknownTask");

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertFalse(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "workType",
            "value", "bail_work",
            "canReconfigure", false
        )));
        assertFalse(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "roleCategory",
            "value", "ADMIN",
            "canReconfigure", false
        )));
        assertFalse(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "roleCategory",
            "value", "JUDICIAL",
            "canReconfigure", false
        )));
    }

    @ParameterizedTest
    @MethodSource("workTypeScenarioProvider")
    void when_task_type_then_description(String taskType, String value) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskType", taskType);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "description",
            "value", value,
            "canReconfigure", false
        )));
    }

    @ParameterizedTest
    @MethodSource("workTypeScenarioProvider")
    void when_task_attributes_task_type_then_description_and_due_date(String taskType, String value) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> taskAttributes = new HashMap<>(); // allow null values
        taskAttributes.put("taskType", taskType);
        inputVariables.putValue("taskAttributes", taskAttributes);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "description",
            "value", value,
            "canReconfigure", false
        )));
    }

    @Test
    void when_any_then_sets_fields() {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(new VariableMapImpl());
        List<Map<String, Object>> dmnDecisionTableResultList = dmnDecisionTableResult.getResultList();
        assertTrue(dmnDecisionTableResultList.stream()
                       .anyMatch(m -> Objects.equals(m.get("name"), "dueDateOrigin")));
        assertTrue(dmnDecisionTableResultList
                       .contains(nameValueMap(
                           "dueDateNonWorkingCalendar", DEFAULT_CALENDAR + ", "
                               + EXTRA_TEST_CALENDAR, false
                       )));
        assertTrue(dmnDecisionTableResultList
                       .contains(nameValueMap("majorPriority", "5000", false)));
        assertTrue(dmnDecisionTableResultList
                       .contains(nameValueMap("minorPriority", "500", false)));
        assertTrue(dmnDecisionTableResultList
                       .contains(nameValueMap("priorityDateOriginRef", "dueDate", false)));
        assertTrue(dmnDecisionTableResultList
                       .contains(nameValueMap("dueDateNonWorkingDaysOfWeek", "SATURDAY,SUNDAY", false)));
        assertTrue(dmnDecisionTableResultList
                       .contains(nameValueMap("calculatedDates", "nextHearingDate,dueDate,priorityDate", false)));
        assertTrue(dmnDecisionTableResult.getResultList()
                       .contains(Map.of("name", "caseManagementCategory", "value", "Bail", "canReconfigure", false
        )));

    }

    public Map<String, Object> nameValueMap(String name, Object value, boolean canReconfigure) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        map.put("canReconfigure", canReconfigure);
        return map;
    }

    public static Stream<Arguments> workTypeScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "processBailApplication",
                "[Confirm location](/case/IA/Bail/${[CASE_REFERENCE]}/trigger/confirmDetentionLocation)"
            ),
            Arguments.of(
                "reviewInterpreterFlag",
                "[Update interpreter status](/case/IA/Bail/${[CASE_REFERENCE]}/trigger/updateInterpreterBookingStatus)"
            ),
            Arguments.of(
                "noticeOfChange",
                "Follow up with applicant"
            ),
            Arguments.of(
                "followUpBailSummary",
                "[Send direction](/case/IA/Bail/${[CASE_REFERENCE]}/trigger/sendBailDirection) if required"
            ),
            Arguments.of(
                "reviewAdditionalEvidence",
                "Review additional evidence"
            ),
            Arguments.of(
                "uploadSignedDecision",
                "[Upload Signed Decision](/case/IA/Bail/${[CASE_REFERENCE]}/trigger/uploadSignedDecisionNotice)"
            ),
            Arguments.of(
                "uploadSignedDecisionConditionalGrant",
                "[Upload Signed Decision](/case/IA/Bail/${[CASE_REFERENCE]}/trigger/uploadSignedDecisionNoticeConditionalGrant)"
            ),
            Arguments.of(
                "listForFurtherReview",
                "[Relist the case](/cases/case-details/IA/Bail/${[CASE_REFERENCE]}/hearings)"
            ),
            Arguments.of(
                "postHearingRecord",
                "[Add hearing details](/cases/case-details/IA/Bail/${[CASE_REFERENCE]}/hearings)"
            ),
            Arguments.of(
                "unknownTask",
                ""
            )
        );
    }
}

