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

@SuppressWarnings("checkstyle:LineLength")
class CamundaGetTaskTest {

    private DmnEngine dmnEngine;

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @DisplayName("Get task id")
    @ParameterizedTest(name = "\"{0}\" \"{1}\" should go to \"{2}\"")
    @CsvSource({
        "submitAppeal, anything, processApplication, TCW, 2, false, ",
        "submitTimeExtension, anything, decideOnTimeExtension, TCW, 2, true, Time extension",
        "uploadHomeOfficeBundle, awaitingRespondentEvidence, reviewRespondentEvidence, TCW, 2, true, Case progression",
        "submitCase, caseUnderReview, reviewAppealSkeletonArgument, TCW, 2, true, Case progression",
        "submitReasonsForAppeal, reasonsForAppealSubmitted, reviewReasonsForAppeal, TCW, 2, true, Case progression",
        "submitClarifyingQuestionAnswers, clarifyingQuestionsAnswersSubmitted, reviewClarifyingQuestionsAnswers, TCW, 2, true, Case progression",
        "submitCmaRequirements, cmaRequirementsSubmitted, reviewCmaRequirements, TCW, 2, true, Case progression",
        "listCma, cmaListed, attendCma, TCW, 2, true, Case progression",
        "uploadHomeOfficeAppealResponse, respondentReview, reviewRespondentResponse, TCW, 2, true, Case progression",
        "anything, prepareForHearing, createCaseSummary, TCW, 2, true, Case progression",
        "anything, finalBundling, createHearingBundle, TCW, 2, true, Case progression",
        "anything, preHearing, startDecisionsAndReasonsDocument, TCW, 2, true, Case progression",
        "requestRespondentEvidence, awaitingRespondentEvidence, provideRespondentEvidence, external, -1, false, ",
        "requestCaseBuilding, caseBuilding, provideCaseBuilding, external, -1, false, ",
        "requestReasonsForAppeal, awaitingReasonsForAppeal, provideReasonsForAppeal, external, -1, false, ",
        "sendDirectionWithQuestions, awaitingClarifyingQuestionsAnswers, provideClarifyingAnswers, external, -1, false, ",
        "requestCmaRequirements, awaitingCmaRequirements, provideCmaRequirements, external, -1, false, ",
        "requestRespondentReview, respondentReview, provideRespondentReview, external, -1, false, ",
        "requestHearingRequirements, submitHearingRequirements, provideHearingRequirements, external, -1, false, "
    })
    void shouldGetTaskIdTest(String eventId,
                             String postState,
                             String taskId,
                             String group,
                             Integer workingDaysAllowed,
                             boolean expectedTaskCategoryPresent,
                             String taskCategory) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(eventId, postState);

        DmnDecisionRuleResult singleResult = dmnDecisionTableResult.getSingleResult();

        assertThat(singleResult.getEntry("taskId"), is(taskId));
        assertThat(singleResult.getEntry("group"), is(group));
        assertThat(singleResult.containsKey("taskCategory"), is(expectedTaskCategoryPresent));
        if (expectedTaskCategoryPresent) {
            assertThat(singleResult.getEntry("taskCategory"), is(taskCategory));
        }
        if (workingDaysAllowed > 0) {
            assertThat(singleResult.getEntry("workingDaysAllowed"), is(workingDaysAllowed));
        } else {
            assertThat(singleResult.containsKey("workingDaysAllowed"), is(false));
        }
    }

    @DisplayName("transition unmapped")
    @Test
    void transitionUnmapped() {
        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("anything", "anything");

        assertThat(dmnDecisionRuleResults.isEmpty(), is(true));
    }

    private DmnDecisionTableResult evaluateDmn(String eventId, String postState) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("wa-task-initiation-ia-asylum.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("getTask_IA_Asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", eventId);
            variables.putValue("postEventState", postState);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
