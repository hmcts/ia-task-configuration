package uk.gov.hmcts.reform.iataskconfiguration;

import lombok.Builder;
import lombok.Value;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    @Builder
    @Value
    private static class Scenario {
        String caseData;
        String caseNameValue;
    }

    private static Stream<Scenario> scenarioProvider() {
        return Stream.of(
            Scenario.builder()
                .caseData("{}")
                .caseNameValue(null)
                .build()
        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void when_case_then_return_name_and_value_rows(Scenario scenario) {
        Map<String, Object> caseNameRule = new HashMap<>(); // allow null values
        caseNameRule.put("name", "caseName");
        caseNameRule.put("value", scenario.getCaseNameValue());

        Map<String, Object> appealTypeRule = Map.of(
            "name", "appealType",
            "value", ""
        );
        Map<String, Object> regionRule = Map.of(
            "name", "region",
            "value", "1"
        );
        Map<String, Object> locationRule = Map.of(
            "name", "location",
            "value", "765324"
        );
        Map<String, Object> locationNameRule = Map.of(
            "name", "locationName",
            "value", "Taylor House"
        );
        List<Map<String, Object>> expectedResults = List.of(
            caseNameRule, appealTypeRule, regionRule, locationRule, locationNameRule
        );

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(scenario.getCaseData());
        System.out.println(dmnDecisionTableResult.getResultList());
        assertThat(dmnDecisionTableResult.getResultList(), is(expectedResults));
    }


    private DmnDecisionTableResult evaluateDmn(String caseData) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream =
                 contextClassLoader.getResourceAsStream(
                     WA_TASK_CONFIGURATION_DMN_NAME + "-" + JURISDICTION + "-" + CASE_TYPE + ".dmn")) {

            VariableMap variables = new VariableMapImpl();
            variables.putValue("case", caseData);

            DmnDecision decision = dmnEngine.parseDecision(
                WA_TASK_CONFIGURATION_DMN_NAME + "-" + JURISDICTION + "-" + CASE_TYPE,
                inputStream
            );
            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

    }

}
