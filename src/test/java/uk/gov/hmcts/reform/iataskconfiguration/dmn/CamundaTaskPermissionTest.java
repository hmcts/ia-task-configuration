package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableInputImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableOutputImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_IA_ASYLUM;

class CamundaTaskPermissionTest extends DmnDecisionTableBaseUnitTest {

    private static final Map<String, Serializable> nationalBusinessCentre = Map.of(
        "name", "national-business-centre",
        "value", "Read,Refer,Own",
        "roleCategory", "ADMINISTRATOR",
        "assignmentPriority", 1,
        "autoAssignable", false
    );

    private static final Map<String, Serializable> taskSupervisor = Map.of(
        "autoAssignable", false,
        "name", "task-supervisor",
        "value", "Read,Refer,Manage,Cancel"
    );
    private static final Map<String, Serializable> caseManager = Map.of(
        "autoAssignable", true,
        "name", "case-manager",
        "roleCategory", "LEGAL_OPERATIONS",
        "value", "Read,Refer,Own"
    );
    private static final Map<String, Serializable> tribunalCaseWorkerPriorityOne = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 1,
        "name", "tribunal-caseworker",
        "roleCategory", "LEGAL_OPERATIONS",
        "value", "Read,Refer,Own"
    );
    private static final Map<String, Serializable> tribunalCaseWorkerPriorityTwo = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 2,
        "name", "tribunal-caseworker",
        "roleCategory", "LEGAL_OPERATIONS",
        "value", "Read,Refer,Execute"
    );
    private static final Map<String, Serializable> seniorCaseWorkerPriorityOne = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 1,
        "name", "senior-tribunal-caseworker",
        "roleCategory", "LEGAL_OPERATIONS",
        "value", "Read,Refer,Own"
    );
    private static final Map<String, Serializable> seniorCaseWorkerPriorityTwo = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 2,
        "name", "senior-tribunal-caseworker",
        "roleCategory", "LEGAL_OPERATIONS",
        "value", "Read,Refer,Execute"
    );
    private static final Map<String, Serializable> judgePriorityTwo = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 2,
        "authorisations", "373",
        "name", "judge",
        "roleCategory", "JUDICIAL",
        "value", "Read,Refer,Execute"
    );
    private static final Map<String, Serializable> hearingJudgePriorityOne = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 1,
        "authorisations", "373",
        "name", "hearing-judge",
        "roleCategory", "JUDICIAL",
        "value", "Read,Refer,Own"
    );
    private static final Map<String, Serializable> hearingJudgePriorityTwo = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 2,
        "authorisations", "373",
        "name", "hearing-judge",
        "roleCategory", "JUDICIAL",
        "value", "Read,Refer,Execute"
    );
    private static final Map<String, Serializable> judgePriorityOne = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 1,
        "authorisations", "373",
        "name", "judge",
        "roleCategory", "JUDICIAL",
        "value", "Read,Refer,Own"
    );

    private static final Map<String, Serializable> hearingCentreAdminPriorityOne = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 1,
        "name", "hearing-centre-admin",
        "value", "Read,Refer,Own",
        "roleCategory", "ADMINISTRATOR"
    );
    private static final Map<String, Serializable> ftpaJudgePriorityOne = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 1,
        "authorisations", "373",
        "name", "FTPA-judge",
        "roleCategory", "JUDICIAL",
        "value", "Read,Refer,Own"
    );

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_IA_ASYLUM;
    }

    public static Stream<Arguments> genericScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "decideAnFTPA",
                List.of(
                    taskSupervisor,
                    judgePriorityTwo,
                    hearingJudgePriorityOne,
                    judgePriorityOne,
                    ftpaJudgePriorityOne
                )
            ),
            Arguments.of(
                "uploadHearingRecording",
                List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "sendDecisionsAndReasons",
                List.of(
                    taskSupervisor,
                    hearingJudgePriorityOne,
                    judgePriorityOne
                )
            ),
            Arguments.of(
                "updateHearingRequirements",
                List.of(
                    taskSupervisor,
                    tribunalCaseWorkerPriorityOne,
                    seniorCaseWorkerPriorityOne,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "reviewHearingBundle",
                List.of(
                    taskSupervisor,
                    tribunalCaseWorkerPriorityTwo,
                    seniorCaseWorkerPriorityTwo,
                    hearingJudgePriorityOne,
                    judgePriorityOne
                )
            ),
            Arguments.of(
                "editListing",
                List.of(
                    taskSupervisor,
                    judgePriorityTwo,
                    hearingJudgePriorityTwo,
                    hearingCentreAdminPriorityOne,
                    tribunalCaseWorkerPriorityTwo,
                    seniorCaseWorkerPriorityTwo
                )
            ),
            Arguments.of(
                "addListingDate",
                List.of(
                    taskSupervisor,
                    judgePriorityTwo,
                    hearingJudgePriorityTwo,
                    nationalBusinessCentre,
                    tribunalCaseWorkerPriorityTwo,
                    seniorCaseWorkerPriorityTwo
                )
            ),
            Arguments.of(
                "processApplicationToReviewDecision",
                List.of(
                    taskSupervisor,
                    hearingJudgePriorityOne,
                    judgePriorityOne
                )
            ),
            Arguments.of(
                "prepareDecisionsAndReasons",
                List.of(
                    taskSupervisor,
                    hearingJudgePriorityOne,
                    judgePriorityOne
                )
            )
        );
    }

    /*
        todo: Refactor all other tests into this one for the sake of simplicity
        important: permissions rules in the DMN are in order, in case you can't find why your test fails.
     */
    @ParameterizedTest
    @MethodSource("genericScenarioProvider")
    void given_taskType_and_CaseData_when_evaluate_then_returns_expected_rules(
        String taskType,
        List<Map<String, Serializable>> expectedRules) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        Assertions.assertEquals(expectedRules, dmnDecisionTableResult.getResultList());
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                "someTaskType",
                "someCaseData",
                List.of(
                    Map.of(
                        "name", "task-supervisor",
                        "value", "Read,Refer,Manage,Cancel",
                        "autoAssignable", false
                    )
                )
            ),
            Arguments.of(
                "null",
                "someCaseData",
                List.of(
                    Map.of(
                        "name", "task-supervisor",
                        "value", "Read,Refer,Manage,Cancel",
                        "autoAssignable", false
                    )
                )
            ),
            Arguments.of(
                "someTaskType",
                "null",
                List.of(
                    Map.of(
                        "name", "task-supervisor",
                        "value", "Read,Refer,Manage,Cancel",
                        "autoAssignable", false
                    )
                )
            ),
            Arguments.of(
                "someTaskType",
                "{}",
                List.of(
                    Map.of(
                        "name", "task-supervisor",
                        "value", "Read,Refer,Manage,Cancel",
                        "autoAssignable", false
                    )
                )
            )
        );
    }

    @ParameterizedTest(name = "task type: {0} case data: {1}")
    @MethodSource("scenarioProvider")
    void given_null_or_empty_inputs_when_evaluate_dmn_it_returns_expected_rules(String taskType,
                                                                                String caseData,
                                                                                List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        inputVariables.putValue("case", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @SuppressWarnings("checkstyle:indentation")
    @ParameterizedTest
    @CsvSource(value = {
        "reviewRespondentEvidence", "followUpOverdueRespondentEvidence",
        "followUpOverdueCaseBuilding", "followUpOverdueReasonsForAppeal", "reviewTheAppeal",
        "reviewClarifyingQuestionsAnswers", "followUpOverdueClarifyingAnswers", "reviewRespondentResponse",
        "followUpOverdueRespondentReview", "reviewHearingRequirements", "followUpOverdueHearingRequirements",
        "reviewCmaRequirements",
        "attendCma",
        "followUpNoticeOfChange", "followUpOverdueCmaRequirements", "followUpNonStandardDirection",
         "attendCma", "followUpNoticeOfChange", "followUpOverdueCmaRequirements",
        "followUpNonStandardDirection",
         "reviewAdditionalEvidence"
    })
    void given_taskType_when_evaluate_dmn_then_it_returns_first_second_and_third_rules(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(List.of(
            Map.of(
                "name", "task-supervisor",
                "value", "Read,Refer,Manage,Cancel",
                "autoAssignable", false
            ), Map.of(
                "name", "case-manager",
                "value", "Read,Refer,Own",
                "roleCategory", "LEGAL_OPERATIONS",
                "autoAssignable", true
            ),
            Map.of(
                "name", "tribunal-caseworker",
                "value", "Read,Refer,Own",
                "roleCategory", "LEGAL_OPERATIONS",
                "assignmentPriority", 1,
                "autoAssignable", false
            ),
            Map.of(
                "name", "senior-tribunal-caseworker",
                "value", "Read,Refer,Own",
                "roleCategory", "LEGAL_OPERATIONS",
                "assignmentPriority", 1,
                "autoAssignable", false
            )
        )));
    }

    @SuppressWarnings("checkstyle:indentation")
    @ParameterizedTest
    @CsvSource(value = {
        "caseSummaryHearingBundleStartDecision"
    })
    void given_taskType_when_evaluate_dmn_then_it_returns_1st_2nd_3rd_4th_5th_rules(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(List.of(
            Map.of(
                "name", "task-supervisor",
                "value", "Read,Refer,Manage,Cancel",
                "autoAssignable", false
            ), Map.of(
                "name", "case-manager",
                "value", "Read,Refer,Own",
                "roleCategory", "LEGAL_OPERATIONS",
                "autoAssignable", true
            ),
            Map.of(
                "name", "tribunal-caseworker",
                "value", "Read,Refer,Own",
                "roleCategory", "LEGAL_OPERATIONS",
                "assignmentPriority", 1,
                "autoAssignable", false
            ),
            Map.of(
                "name", "senior-tribunal-caseworker",
                "value", "Read,Refer,Own",
                "roleCategory", "LEGAL_OPERATIONS",
                "assignmentPriority", 1,
                "autoAssignable", false
            ),
            Map.of(
                "name", "judge",
                "value", "Read,Refer,Execute",
                "roleCategory", "JUDICIAL",
                "authorisations", "373",
                "assignmentPriority", 2,
                "autoAssignable", false
            ),Map.of(
                "name", "hearing-judge",
                "value", "Read,Refer,Execute",
                "roleCategory", "JUDICIAL",
                "authorisations", "373",
                "assignmentPriority",2,
                "autoAssignable", false
            )
        )));
    }

    @SuppressWarnings("checkstyle:indentation")
    @ParameterizedTest
    @CsvSource(value = {
        "editListing"
    })
    void given_taskType_when_evaluate_dmn_then_it_returns_expected(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(List.of(
            Map.of(
                "name", "task-supervisor",
                "value", "Read,Refer,Manage,Cancel",
                "autoAssignable", false
            ),
            Map.of(
                "name", "judge",
                "value", "Read,Refer,Execute",
                "roleCategory", "JUDICIAL",
                "authorisations", "373",
                "assignmentPriority",2,
                "autoAssignable", false
            ),
            Map.of(
                "name", "hearing-judge",
                "value", "Read,Refer,Execute",
                "roleCategory", "JUDICIAL",
                "authorisations", "373",
                "assignmentPriority",2,
                "autoAssignable", false
            ),
            Map.of(
                "name", "hearing-centre-admin",
                "value", "Read,Refer,Own",
                "roleCategory", "ADMINISTRATOR",
                "assignmentPriority", 1,
                "autoAssignable", false
            ),
            Map.of(
                "name", "tribunal-caseworker",
                "value", "Read,Refer,Execute",
                "roleCategory", "LEGAL_OPERATIONS",
                "assignmentPriority", 2,
                "autoAssignable", false
            ),
            Map.of(
                "name", "senior-tribunal-caseworker",
                "value", "Read,Refer,Execute",
                "roleCategory", "LEGAL_OPERATIONS",
                "assignmentPriority", 2,
                "autoAssignable", false
            )
        )));
    }

    @SuppressWarnings("checkstyle:indentation")
    @ParameterizedTest
    @CsvSource(value = {
        "processApplicationToReviewDecision",
    })
    void given_taskType_5_when_evaluate_dmn_then_it_returns_expected(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(List.of(
            Map.of(
                "name", "task-supervisor",
                "value", "Read,Refer,Manage,Cancel",
                "autoAssignable", false
            ),
            Map.of(
                "autoAssignable", true,
                "assignmentPriority", 1,
                "authorisations", "373",
                "name", "hearing-judge",
                "roleCategory", "JUDICIAL",
                "value", "Read,Refer,Own"
            ),
            Map.of(
                "autoAssignable", false,
                "assignmentPriority", 1,
                "authorisations", "373",
                "name", "judge",
                "roleCategory", "JUDICIAL",
                "value", "Read,Refer,Own"
            )
        )));
    }

    @SuppressWarnings("checkstyle:indentation")
    @Test
    void given_blank_taskType_when_evaluate_dmn_then_it_returns_release1_rule() {
        VariableMap inputVariables = new VariableMapImpl();

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(List.of(
            Map.of(
                "name", "task-supervisor",
                "value", "Read,Refer,Manage,Cancel",
                "autoAssignable", false
            ),
            Map.of(
                "name", "tribunal-caseworker",
                "value", "Read,Refer,Own,Manage,Cancel",
                "roleCategory", "LEGAL_OPERATIONS",
                "autoAssignable", false
            ),
            Map.of(
                "name", "senior-tribunal-caseworker",
                "value", "Read,Refer,Own,Manage,Cancel",
                "roleCategory", "LEGAL_OPERATIONS",
                "autoAssignable", false
            )
        )));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();

        List<String> inputColumnIds = asList("taskType", "case");
        //Inputs
        assertThat(logic.getInputs().size(), is(2));
        assertThatInputContainInOrder(inputColumnIds, logic.getInputs());
        //Outputs
        List<String> outputColumnIds = asList(
            "caseAccessCategory",
            "name",
            "value",
            "roleCategory",
            "authorisations",
            "assignmentPriority",
            "autoAssignable"
        );
        assertThat(logic.getOutputs().size(), is(7));
        assertThatOutputContainInOrder(outputColumnIds, logic.getOutputs());
        //Rules
        assertThat(logic.getRules().size(), is(15));
    }

    private void assertThatInputContainInOrder(List<String> inputColumnIds, List<DmnDecisionTableInputImpl> inputs) {
        IntStream.range(0, inputs.size())
            .forEach(i -> assertThat(inputs.get(i).getInputVariable(), is(inputColumnIds.get(i))));
    }

    private void assertThatOutputContainInOrder(List<String> outputColumnIds, List<DmnDecisionTableOutputImpl> output) {
        IntStream.range(0, output.size())
            .forEach(i -> assertThat(output.get(i).getOutputName(), is(outputColumnIds.get(i))));
    }
}
