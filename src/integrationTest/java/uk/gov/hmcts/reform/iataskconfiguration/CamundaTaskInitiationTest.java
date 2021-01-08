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
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:AbbreviationAsWordInName"})
class CamundaTaskInitiationTest {

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
        "draftHearingRequirements, listing, reviewHearingRequirements, TCW, 2, true, Case progression, "
    })
    void given_single_rule_match_should_evaluate(String eventId,
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

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void given_multiple_rules_matches_should_evaluate(Scenario scenario) {

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(scenario.eventId, scenario.postState);

        assertThat(dmnDecisionTableResult.getResultList(), is(scenario.expectedResultList));
    }

    private static Stream<Scenario> scenarioProvider() {
        // requestRespondentEvidence scenario
        List<Map<String, Object>> expectedResultsRequestRespondentEvidence = getExpectedResults(
            "Provide Respondent Evidence",
            "provideRespondentEvidence",
            "Follow-up overdue respondent evidence",
            "followUpOverdueRespondentEvidence"
        );

        // requestCaseBuilding scenario
        List<Map<String, Object>> expectedResultsRequestCaseBuilding = getExpectedResults(
            "Provide Case Building",
            "provideCaseBuilding",
            "Follow-up overdue case building",
            "followUpOverdueCaseBuilding"
        );

        // requestReasonsForAppeal scenario
        List<Map<String, Object>> expectedResultsRequestReasonsForAppeal = getExpectedResults(
            "Provide Reasons For Appeal",
            "provideReasonsForAppeal",
            "Follow-up overdue reasons for appeal",
            "followUpOverdueReasonsForAppeal"
        );

        // sendDirectionWithQuestions scenario
        List<Map<String, Object>> expectedResultsSendDirectionWithQuestions = getExpectedResults(
            "Provide Clarifying Answers",
            "provideClarifyingAnswers",
            "Follow-up overdue clarifying answers",
            "followUpOverdueClarifyingAnswers"
        );

        // requestCmaRequirements scenario
        List<Map<String, Object>> expectedResultsRequestCmaRequirements = getExpectedResults(
            "Provide Cma Requirements",
            "provideCmaRequirements",
            "Follow-up overdue CMA requirements",
            "followUpOverdueCmaRequirements"
        );

        // requestRespondentReview scenario
        List<Map<String, Object>> expectedResultsRequestRespondentReview = getExpectedResults(
            "Provide Respondent Review",
            "provideRespondentReview",
            "Follow-up overdue respondent review",
            "followUpOverdueRespondentReview"
        );

        // requestHearingRequirements scenario
        List<Map<String, Object>> expectedResultsRequestHearingRequirements = getExpectedResults(
            "Provide Hearing Requirements",
            "provideHearingRequirements",
            "Follow-up overdue hearing requirements",
            "followUpOverdueHearingRequirements"
        );

        // applyForFTPAAppellant scenario
        Map<String, Object> ruleFTPAAppellant1 = Map.of(
            "name", "Record allocated Judge",
            "workingDaysAllowed", 5,
            "taskId", "allocateFTPAToJudge",
            "group", "external",
            "taskCategory", "Case progression"
        );
        Map<String, Object> ruleFTPAAppellant2 = Map.of(
            "name", "Allocate FTPA to Judge",
            "workingDaysAllowed", 5,
            "taskId", "allocateFtpaToJudge",
            "group", "TCW",
            "taskCategory", "Case progression"
        );
        List<Map<String, Object>> expectedResultsApplyForFTPAAppellant = List.of(
            ruleFTPAAppellant1,
            ruleFTPAAppellant2
        );

        // applyForFTPARespondent scenario
        Map<String, Object> ruleFTPARespondent1 = Map.of(
            "name", "Record allocated Judge",
            "workingDaysAllowed", 5,
            "taskId", "allocateFTPAToJudge",
            "group", "external",
            "taskCategory", "Case progression"
        );
        Map<String, Object> ruleFTPARespondent2 = Map.of(
            "name", "Allocate FTPA to Judge",
            "workingDaysAllowed", 5,
            "taskId", "allocateFtpaToJudge",
            "group", "TCW",
            "taskCategory", "Case progression"
        );
        List<Map<String, Object>> expectedResultsApplyForFTPARespondent = List.of(
            ruleFTPARespondent1,
            ruleFTPARespondent2
        );


        return Stream.of(
            new Scenario(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                expectedResultsRequestRespondentEvidence
            ),
            new Scenario(
                "requestCaseBuilding",
                "caseBuilding",
                expectedResultsRequestCaseBuilding
            ),
            new Scenario(
                "requestReasonsForAppeal",
                "awaitingReasonsForAppeal",
                expectedResultsRequestReasonsForAppeal
            ),
            new Scenario(
                "sendDirectionWithQuestions",
                "awaitingClarifyingQuestionsAnswers",
                expectedResultsSendDirectionWithQuestions
            ),
            new Scenario(
                "requestCmaRequirements",
                "awaitingCmaRequirements",
                expectedResultsRequestCmaRequirements
            ),
            new Scenario(
                "requestRespondentReview",
                "respondentReview",
                expectedResultsRequestRespondentReview
            ),
            new Scenario(
                "requestHearingRequirements",
                "submitHearingRequirements",
                expectedResultsRequestHearingRequirements
            ),
            new Scenario(
                "applyForFTPAAppellant",
                "ftpaSubmitted",
                expectedResultsApplyForFTPAAppellant
            ),
            new Scenario(
                "applyForFTPARespondent",
                "ftpaSubmitted",
                expectedResultsApplyForFTPARespondent
            )
        );
    }

    private static List<Map<String, Object>> getExpectedResults(String name1,
                                                                String taskId1,
                                                                String name2,
                                                                String taskId2) {
        Map<String, Object> rule1 = Map.of(
            "name", name1,
            "taskId", taskId1,
            "group", "external"
        );
        Map<String, Object> rule2 = Map.of(
            "name", name2,
            "workingDaysAllowed", 2,
            "taskId", taskId2,
            "group", "TCW",
            "taskCategory", "Followup overdue"
        );

        return List.of(rule1, rule2);
    }

    private static class Scenario {
        String eventId;
        String postState;
        List<Map<String, Object>> expectedResultList;

        public Scenario(String eventId,
                        String postState,
                        List<Map<String, Object>> expectedResultList
        ) {
            this.eventId = eventId;
            this.postState = postState;
            this.expectedResultList = expectedResultList;
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
            DmnDecision decision = dmnEngine.parseDecision("wa-task-initiation-ia-asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", eventId);
            variables.putValue("postEventState", postState);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
