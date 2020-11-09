package uk.gov.hmcts.reform.iataskconfiguration;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CamundaGetOverdueTaskTest {

    private DmnEngine dmnEngine;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @DisplayName("Get overdue task")
    @ParameterizedTest(name = "\"{0}\" should go to \"{1}\"")
    @CsvSource({
        "provideRespondentEvidence, followUpOverdueRespondentEvidence, TCW, 2",
        "provideCaseBuilding, followUpOverdueCaseBuilding, TCW, 2",
        "provideReasonsForAppeal, followUpOverdueReasonsForAppeal, TCW, 2",
        "provideClarifyingAnswers, followUpOverdueClarifyingAnswers, TCW, 2",
        "provideCmaRequirements, followUpOverdueCmaRequirements, TCW, 2",
        "provideRespondentReview, followUpOverdueRespondentReview, TCW, 2",
        "provideHearingRequirements, followUpOverdueHearingRequirements, TCW, 2"
    })
    void shouldGetOverdueTaskIdTest(String taskId, String overdueTaskId, String group, Integer workingDaysAllowed) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(taskId);

        DmnDecisionRuleResult singleResult = dmnDecisionTableResult.getSingleResult();

        assertThat(singleResult.getEntry("taskId"), is(overdueTaskId));
        assertThat(singleResult.getEntry("group"), is(group));
        assertThat(singleResult.getEntry("workingDaysAllowed"), is(workingDaysAllowed));
    }

    @DisplayName("transition unmapped")
    @Test
    void transitionUnmapped() {
        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("anything");

        assertThat(dmnDecisionRuleResults.isEmpty(), is(true));
    }

    private DmnDecisionTableResult evaluateDmn(String taskId) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("getOverdueTask_IA_Asylum.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("getOverdueTask_IA_Asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("taskId", taskId);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
