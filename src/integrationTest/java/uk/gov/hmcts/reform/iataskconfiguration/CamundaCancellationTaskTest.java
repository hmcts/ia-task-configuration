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

class CamundaCancellationTaskTest {

    private DmnEngine dmnEngine;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }


    @DisplayName("Set request reason to complete")
    @Test
    void set_request_reasons_to_auto_complete() {
        VariableMap result = new VariableMapImpl();

        result.putValue("action","Cancel");

        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("","EndTheAppeal","");

        assertThat(dmnDecisionRuleResults.getFirstResult().getEntryMap(), is(result));
    }

    @DisplayName("transition unmapped")
    @Test
    void transitionUnmapped() {
        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn(null,null,null);

        assertThat(dmnDecisionRuleResults.isEmpty(), is(true));
    }

    private DmnDecisionTableResult evaluateDmn(String fromState, String event, String state) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("cancellationTasks_IA_Asylum.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("CancellationTask_IA_Asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("fromState", fromState);
            variables.putValue("event", event);
            variables.putValue("state", state);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
