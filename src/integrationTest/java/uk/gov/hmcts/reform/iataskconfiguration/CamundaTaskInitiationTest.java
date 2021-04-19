package uk.gov.hmcts.reform.iataskconfiguration;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.FeelException;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

    private static Stream<Scenario> scenarioProvider() {
        // requestCaseBuilding scenario
        List<Map<String, Object>> expectedResultsRequestCaseBuilding = getExpectedResults(
            "Follow-up overdue case building",
            "followUpOverdueCaseBuilding",
            2
        );

        // requestReasonsForAppeal scenario
        List<Map<String, Object>> expectedResultsRequestReasonsForAppeal = getExpectedResults(
            "Follow-up overdue reasons for appeal",
            "followUpOverdueReasonsForAppeal",
            2
        );

        // sendDirectionWithQuestions scenario
        List<Map<String, Object>> expectedResultsSendDirectionWithQuestions = getExpectedResults(
            "Follow-up overdue clarifying answers",
            "followUpOverdueClarifyingAnswers",
            2
        );

        // requestCmaRequirements scenario
        List<Map<String, Object>> expectedResultsRequestCmaRequirements = getExpectedResults(
            "Follow-up overdue CMA requirements",
            "followUpOverdueCmaRequirements",
            2
        );

        // requestRespondentReview scenario
        List<Map<String, Object>> expectedResultsRequestRespondentReview = getExpectedResults(
            "Follow-up overdue respondent review",
            "followUpOverdueRespondentReview",
            2
        );

        // requestHearingRequirements scenario
        List<Map<String, Object>> expectedResultsRequestHearingRequirements = getExpectedResults(
            "Follow-up overdue hearing requirements",
            "followUpOverdueHearingRequirements",
            2
        );

        // followUpNonStandardDirection scenario
        List<Map<String, Object>> followUpNonStandardDirection = getExpectedResults(
            "Follow-up non-standard direction",
            "followUpNonStandardDirection",
            2
        );

        // applyForFTPAAppellant scenario
        Map<String, Object> ruleFTPAAppellant1 = Map.of(
            "name", "Allocate FTPA to Judge",
            "workingDaysAllowed", 5,
            "taskId", "allocateFtpaToJudge",
            "group", "TCW",
            "taskCategory", "Case progression"
        );
        List<Map<String, Object>> expectedResultsApplyForFTPAAppellant = List.of(
            ruleFTPAAppellant1
        );

        // applyForFTPARespondent scenario
        Map<String, Object> ruleFTPARespondent1 = Map.of(
            "name", "Allocate FTPA to Judge",
            "workingDaysAllowed", 5,
            "taskId", "allocateFtpaToJudge",
            "group", "TCW",
            "taskCategory", "Case progression"
        );
        List<Map<String, Object>> expectedResultsApplyForFTPARespondent = List.of(
            ruleFTPARespondent1
        );

        Map<String, Object> ruleMakeAnApplication = Map.of(
            "name", "Process Application",
            "workingDaysAllowed", 2,
            "taskId", "processApplication",
            "group", "TCW"
        );

        List<Map<String, Object>> expectedResultsApplyForMakeAnApplication = List.of(
            ruleMakeAnApplication
        );

        Map<String, Object> ruleSubmitAppeal = Map.of(
            "name", "Review the appeal",
            "workingDaysAllowed", 2,
            "taskId", "reviewTheAppeal",
            "group", "TCW",
            "taskCategory", "Case progression"
        );

        List<Map<String, Object>> expectedResultsApplyForSubmitAppeal = List.of(
            ruleSubmitAppeal
        );

        return Stream.of(
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
                "sendDirection",
                "",
                followUpNonStandardDirection
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
            ),
            new Scenario(
                "submitAppeal",
                "appealSubmitted",
                expectedResultsApplyForSubmitAppeal
            ),
            new Scenario(
                "makeAnApplication",
                "anything",
                expectedResultsApplyForMakeAnApplication
            )
        );
    }

    private static List<Map<String, Object>> getExpectedResults(String name1,
                                                                String taskId1,
                                                                int delayDuration) {
        Map<String, Object> rule2 = Map.of(
            "name", name1,
            "workingDaysAllowed", 2,
            "delayDuration", delayDuration,
            "taskId", taskId1,
            "group", "TCW",
            "taskCategory", "Followup overdue"
        );

        return List.of(rule2);
    }

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @DisplayName("Get task id")
    @ParameterizedTest(name = "\"{0}\" \"{1}\" should go to \"{2}\"")
    @CsvSource({
        "submitTimeExtension, anything, decideOnTimeExtension, TCW, 2, true, Time extension, false, ",
        "uploadHomeOfficeBundle, awaitingRespondentEvidence, reviewRespondentEvidence, TCW, 2, true, Case progression, false, ",
        "submitCase, caseUnderReview, reviewAppealSkeletonArgument, TCW, 2, true, Case progression, false, ",
        "submitReasonsForAppeal, reasonsForAppealSubmitted, reviewReasonsForAppeal, TCW, 2, true, Case progression, false, ",
        "submitClarifyingQuestionAnswers, clarifyingQuestionsAnswersSubmitted, reviewClarifyingQuestionsAnswers, TCW, 2, true, Case progression, false, ",
        "submitCmaRequirements, cmaRequirementsSubmitted, reviewCmaRequirements, TCW, 2, true, Case progression, false, ",
        "listCma, cmaListed, attendCma, TCW, 2, true, Case progression, false, ",
        "uploadHomeOfficeAppealResponse, respondentReview, reviewRespondentResponse, TCW, 2, true, Case progression, false, ",
        "anything, prepareForHearing, createCaseSummary, TCW, 2, true, Case progression, false, ",
        "anything, finalBundling, createHearingBundle, TCW, 2, true, Case progression, false, ",
        "anything, preHearing, startDecisionsAndReasonsDocument, TCW, 2, true, Case progression, false, ",
        "sendDirection, anything, followUpNonStandardDirection, TCW, 2, true, Followup overdue, true, 2",
        "removeRepresentation, anything, followUpNoticeOfChange, TCW, 2, true, Followup overdue, true, 14",
        "removeLegalRepresentative, anything, followUpNoticeOfChange, TCW, 2, true, Followup overdue, true, 14",
        "requestRespondentEvidence, awaitingRespondentEvidence, followUpOverdueRespondentEvidence, TCW, 2, true, Followup overdue, true, 2",
        "draftHearingRequirements, listing, reviewHearingRequirements, TCW, 2, true, Case progression, false, ",
        "requestCaseBuilding, caseBuilding, followUpOverdueCaseBuilding, TCW, 2, true, Followup overdue, true, 2",
        "requestReasonsForAppeal, awaitingReasonsForAppeal, followUpOverdueReasonsForAppeal, TCW, 2, true, Followup overdue, true, 2",
        "sendDirectionWithQuestions, awaitingClarifyingQuestionsAnswers, followUpOverdueClarifyingAnswers, TCW, 2, true, Followup overdue, true, 2",
        "requestCmaRequirements, awaitingCmaRequirements, followUpOverdueCmaRequirements, TCW, 2, true, Followup overdue, true, 2",
        "requestRespondentReview, respondentReview, followUpOverdueRespondentReview, TCW, 2, true, Followup overdue, true, 2",
        "requestHearingRequirements, submitHearingRequirements, followUpOverdueHearingRequirements, TCW, 2, true, Followup overdue, true, 2"
    })
    void given_single_rule_match_should_evaluate(String eventId,
                                                 String postState,
                                                 String taskId,
                                                 String group,
                                                 Integer workingDaysAllowed,
                                                 boolean expectedTaskCategoryPresent,
                                                 String taskCategory,
                                                 boolean expectedDelayDuration,
                                                 Integer delayDuration) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(eventId, postState, "2021-04-08");

        DmnDecisionRuleResult singleResult = dmnDecisionTableResult.getSingleResult();

        assertThat(singleResult.getEntry("taskId"), is(taskId));
        assertThat(singleResult.getEntry("group"), is(group));
        assertThat(singleResult.containsKey("taskCategory"), is(expectedTaskCategoryPresent));
        if (expectedTaskCategoryPresent) {
            assertThat(singleResult.getEntry("taskCategory"), is(taskCategory));
        }
        if (expectedDelayDuration) {
            assertThat(singleResult.getEntry("delayDuration"), is(delayDuration));
        } else {
            assertThat(singleResult.containsKey("delayDuration"), is(false));
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

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(scenario.eventId, scenario.postState, "2021-04-08");

        assertThat(dmnDecisionTableResult.getResultList(), is(scenario.expectedResultList));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void given_without_direction_due_date_should_evaluate_dmn(String directionDueDate) {

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn("requestHearingRequirements",
                                                                    "submitHearingRequirements", directionDueDate);

        assertThat(dmnDecisionTableResult.getResultList(),
                   is(getExpectedResults("Follow-up overdue hearing requirements",
                                         "followUpOverdueHearingRequirements", 0)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2021-04-0612:00:00", "test"})
    void given_invalid_direction_due_date_should_throw_expectation(String directionDueDate) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("wa-task-initiation-ia-asylum.dmn")) {
            final DmnDecision decision = dmnEngine.parseDecision("wa-task-initiation-ia-asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", "requestHearingRequirements");
            variables.putValue("postEventState", "submitHearingRequirements");
            variables.putValue("directionDueDate", directionDueDate);
            variables.putValue("now", "2021-04-06");

            Assertions.assertThrows(FeelException.class, () -> dmnEngine.evaluateDecisionTable(decision, variables));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @DisplayName("transition unmapped")
    @Test
    void transitionUnmapped() {
        DmnDecisionTableResult dmnDecisionRuleResults = evaluateDmn("anything", "anything", "2021-04-08");

        assertThat(dmnDecisionRuleResults.isEmpty(), is(true));
    }

    private DmnDecisionTableResult evaluateDmn(String eventId, String postState, String directionDueDate) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("wa-task-initiation-ia-asylum.dmn")) {
            final DmnDecision decision = dmnEngine.parseDecision("wa-task-initiation-ia-asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", eventId);
            variables.putValue("postEventState", postState);
            variables.putValue("directionDueDate", directionDueDate);
            variables.putValue("now", "2021-04-06");

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
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

}
