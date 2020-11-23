package uk.gov.hmcts.reform.iataskconfiguration;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CamundaSetTaskCompleteTest {

    private DmnEngine dmnEngine;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @DisplayName("Set request respondent to complete")
    @Test
    void set_respondant_review_to_auto_complete() {
        VariableMap result = new VariableMapImpl();

        result.putValue("task_type","Review the appeal");
        result.putValue("completion_mode","Auto");

        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("requestRespondentEvidence");

        assertThat(dmnDecisionRuleResults.getFirstResult().getEntryMap(), is(result));
    }

    @DisplayName("Set request review to complete")
    @Test
    void set_request_review_to_auto_complete() {
        VariableMap result = new VariableMapImpl();

        result.putValue("task_type","Review respondent evidence");
        result.putValue("completion_mode","Auto");

        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("requestCaseBuilding");

        assertThat(dmnDecisionRuleResults.getFirstResult().getEntryMap(), is(result));
    }

    @DisplayName("Set request reason to complete")
    @Test
    void set_request_reasons_to_auto_complete() {
        VariableMap result = new VariableMapImpl();

        result.putValue("task_type","Review respondent evidence");
        result.putValue("completion_mode","Auto");

        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("requestReasonsForAppeal");

        assertThat(dmnDecisionRuleResults.getFirstResult().getEntryMap(), is(result));
    }

    @DisplayName("transition unmapped")
    @Test
    void transitionUnmapped() {
        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("null");

        System.out.println(dmnDecisionRuleResults.getResultList());
        assertThat(dmnDecisionRuleResults.isEmpty(), is(true));
    }

    private DmnDecisionTableResult evaluateDmn(String eventId) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("completeTask_IA_Asylum.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("completeTask_IA_Asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", eventId);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
