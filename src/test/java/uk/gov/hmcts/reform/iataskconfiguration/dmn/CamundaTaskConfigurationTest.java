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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_IA_ASYLUM;

class CamundaTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_IA_ASYLUM;
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(6));
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
            "value", expectedAppealType
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "caseManagementCategory",
            "value", expectedAppealType
        )));
    }

   @Test
    void when_caseData_then_return_expected_case_management_category() {
        String refusalOfEuLabel = "Refusal of application under the EEA regulations";
        String revocationLabel = "Revocation of a protection status";
        List<Map<String,Object>> caseManagementCategories = List.of(Map.of(
            "value", Map.of("code","refusalOfHumanRights","label","Refusal of a human rights claim"),
            "list_items", List.of(Map.of("code","refusalOfHumanRights","label","Refusal of a human rights claim"))
        ),
            Map.of(
                "value", Map.of("code","refusalOfEu","label","Refusal of application under the EEA regulations"),
                "list_items", List.of(Map.of("code","refusalOfEu","label",refusalOfEuLabel))),
            Map.of(
                "value", Map.of("code","deprivation","label","Deprivation of citizenship"),
                "list_items", List.of(Map.of("code","deprivation","label","Deprivation of citizenship"))),
            Map.of(
                "value", Map.of("code","protection","label","Refusal of protection claim"),
                "list_items", List.of(Map.of("code","protection","label","Refusal of protection claim"))),
            Map.of(
                "value", Map.of("code","revocationOfProtection","label","Revocation of a protection status"),
                "list_items", List.of(Map.of("code","revocationOfProtection","label",revocationLabel)))
        );

        List<String> expectedCaseManagementCategories = List.of("Human rights","EEA","DoC","Protection","Revocation");

        for (int i = 0; i < caseManagementCategories.size(); i++) {
            Map<String,Object> caseManagementCategory = caseManagementCategories.get(i);
            VariableMap inputVariables = new VariableMapImpl();
            Map<String, Object> caseData = new HashMap<>(); // allow null values
            caseData.put("caseManagementCategory", caseManagementCategory);
            inputVariables.putValue("caseData", caseData);

            DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

            assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
                "name", "caseManagementCategory",
                "value", expectedCaseManagementCategories.get(i)
            )));
        }
    }

    @ParameterizedTest
    @MethodSource("nameAndValueScenarioProvider")
    void when_caseData_then_return_expected_name_and_value_rows(Scenario scenario) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", scenario.caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(getExpectedValues(scenario)));
    }

    private static Stream<Scenario> nameAndValueScenarioProvider() {
        Scenario givenCaseDataIsMissedThenDefaultToTaylorHouseScenario = Scenario.builder()
            .caseData(emptyMap())
            .expectedCaseNameValue(null)
            .expectedAppealTypeValue("")
            .expectedRegionValue("1")
            .expectedLocationValue("765324")
            .expectedLocationNameValue("Taylor House")
            .expectedCaseManagementCategoryValue("")
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
                "caseManagementCategory",  Map.of(
                    "value", Map.of("code","refusalOfHumanRights","label","Refusal of a human rights claim"),
                    "list_items", List.of(Map.of("code","refusalOfHumanRights","label",refusalOfEuLabel))
                )
            ))
            .expectedCaseNameValue("some appellant given names some appellant family name")
            .expectedAppealTypeValue("Human rights")
            .expectedRegionValue("some other region")
            .expectedLocationValue("some other location")
            .expectedLocationNameValue("some other location name")
            .expectedCaseManagementCategoryValue("Human rights")
            .build();

        return Stream.of(
            givenCaseDataIsMissedThenDefaultToTaylorHouseScenario,
            givenCaseDataIsPresentThenReturnNameAndValueScenario
        );
    }

    @Value
    @Builder
    private static class Scenario {
        Map<String, Object> caseData;

        String expectedCaseNameValue;
        String expectedAppealTypeValue;
        String expectedRegionValue;
        String expectedLocationValue;
        String expectedLocationNameValue;
        String expectedCaseManagementCategoryValue;
    }

    private List<Map<String, Object>> getExpectedValues(Scenario scenario) {
        Map<String, Object> caseNameRule = new HashMap<>(); // allow null values
        caseNameRule.put("name", "caseName");
        caseNameRule.put("value", scenario.getExpectedCaseNameValue());

        Map<String, Object> appealTypeRule = Map.of(
            "name", "appealType",
            "value", scenario.getExpectedAppealTypeValue()
        );
        Map<String, Object> regionRule = Map.of(
            "name", "region",
            "value", scenario.getExpectedRegionValue()
        );
        Map<String, Object> locationRule = Map.of(
            "name", "location",
            "value", scenario.getExpectedLocationValue()
        );
        Map<String, Object> locationNameRule = Map.of(
            "name", "locationName",
            "value", scenario.getExpectedLocationNameValue()
        );
        Map<String, Object> caseManagementCategoryRule = Map.of(
            "name", "caseManagementCategory",
            "value", scenario.getExpectedCaseManagementCategoryValue()
        );
        return List.of(
            caseNameRule, appealTypeRule, regionRule, locationRule, locationNameRule,caseManagementCategoryRule
        );
    }

}

