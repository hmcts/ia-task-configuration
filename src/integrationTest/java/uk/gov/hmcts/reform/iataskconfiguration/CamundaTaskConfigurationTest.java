package uk.gov.hmcts.reform.iataskconfiguration;

import lombok.Builder;
import lombok.Value;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CamundaTaskConfigurationTest {

    public static final String WA_TASK_CONFIGURATION_DMN_NAME = "wa-task-configuration";
    public static final String JURISDICTION = "ia";
    public static final String CASE_TYPE = "asylum";
    private DmnEngine dmnEngine;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void when_case_then_return_name_and_value_rows(Scenario scenario) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(scenario.getCaseData());

        assertThat(dmnDecisionTableResult.getResultList(), is(getExpectedResults(scenario)));
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

    private DmnDecisionTableResult evaluateDmn(Map<String, Object> caseData) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream =
                 contextClassLoader.getResourceAsStream(
                     WA_TASK_CONFIGURATION_DMN_NAME + "-" + JURISDICTION + "-" + CASE_TYPE + ".dmn")) {

            DmnDecision decision = dmnEngine.parseDecision(
                WA_TASK_CONFIGURATION_DMN_NAME + "-" + JURISDICTION + "-" + CASE_TYPE,
                inputStream
            );

            return dmnEngine.evaluateDecisionTable(
                decision,
                Variables.createVariables().putValue("caseData", caseData)
            );
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
