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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_IA_ASYLUM;

class CamundaTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {
    public static final String WA_TASK_CONFIGURATION_DMN_NAME = "wa-task-configuration";
    public static final String JURISDICTION = "ia";
    public static final String CASE_TYPE = "asylum";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_IA_ASYLUM;
    }

    private static Stream<Scenario> scenarioProvider() {
        Scenario givenCaseDataIsMissedThenDefaultToTaylorHouseScenario = Scenario.builder()
            .caseData(emptyMap())
            .caseNameValue(null)
            .appealTypeValue("")
            .regionValue("1")
            .locationValue("765324")
            .locationNameValue("Taylor House")
            .build();

        Scenario givenCaseDataIsPresentThenReturnNameAndValueScenario = Scenario.builder()
            .caseData(Map.of(
                "appealType", "asylum",
                "appellantGivenNames", "some appellant given names",
                "appellantFamilyName", "some appellant family name",
                "caseManagementLocation", Map.of(
                    "region", "some other region",
                    "baseLocation", "some other location"
                ),
                "staffLocation", "some other location name"
            ))
            .caseNameValue("some appellant given names some appellant family name")
            .appealTypeValue("asylum")
            .regionValue("some other region")
            .locationValue("some other location")
            .locationNameValue("some other location name")
            .build();

        return Stream.of(
            givenCaseDataIsMissedThenDefaultToTaylorHouseScenario,
            givenCaseDataIsPresentThenReturnNameAndValueScenario
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(5));

    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void when_case_then_return_name_and_value_rows(Scenario scenario) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", scenario.caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(getExpectedResults(scenario)));

    }

    private List<Map<String, Object>> getExpectedResults(Scenario scenario) {
        Map<String, Object> caseNameRule = new HashMap<>(); // allow null values
        caseNameRule.put("name", "caseName");
        caseNameRule.put("value", scenario.getCaseNameValue());

        Map<String, Object> appealTypeRule = Map.of(
            "name", "appealType",
            "value", scenario.getAppealTypeValue()
        );
        Map<String, Object> regionRule = Map.of(
            "name", "region",
            "value", scenario.getRegionValue()
        );
        Map<String, Object> locationRule = Map.of(
            "name", "location",
            "value", scenario.getLocationValue()
        );
        Map<String, Object> locationNameRule = Map.of(
            "name", "locationName",
            "value", scenario.getLocationNameValue()
        );
        return List.of(
            caseNameRule, appealTypeRule, regionRule, locationRule, locationNameRule
        );
    }

    @Value
    @Builder
    private static class Scenario {
        Map<String, Object> caseData;

        String caseNameValue;
        String appealTypeValue;
        String regionValue;
        String locationValue;
        String locationNameValue;
    }

}

