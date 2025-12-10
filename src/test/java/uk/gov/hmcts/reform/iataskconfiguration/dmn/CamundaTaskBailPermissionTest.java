package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_IA_BAIL;

class CamundaTaskBailPermissionTest extends DmnDecisionTableBaseUnitTest {

    private static final Map<String, Serializable> taskSupervisor = Map.of(
        "autoAssignable", false,
        "name", "task-supervisor",
        "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel"
    );

    private static final Map<String, Serializable> hearingCentreAdminPriorityOne = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 1,
        "name", "hearing-centre-admin",
        "value", "Read,Own,Claim,Manage,Unassign,Assign,Complete,Cancel",
        "roleCategory", "ADMIN"
    );

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_IA_BAIL;
    }

    public static Stream<Arguments> genericScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "processBailApplication", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "reviewInterpreterFlag", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "noticeOfChange", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "followUpBailSummary", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "reviewAdditionalEvidence", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "uploadSignedDecision", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "uploadSignedDecisionConditionalGrant", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
                )
            ),
            Arguments.of(
                "postHearingRecord", List.of(
                    taskSupervisor,
                    hearingCentreAdminPriorityOne
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
                        "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel",
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
                        "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel",
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
                        "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel",
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
                        "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel",
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

    @ParameterizedTest
    @ValueSource(strings = {"processBailApplication", "reviewInterpreterFlag", "noticeOfChange", "followUpBailSummary",
        "reviewAdditionalEvidence", "uploadSignedDecision", "uploadSignedDecisionConditionalGrant", "postHearingRecord"})
    void given_taskType_when_evaluate_dmn_then_it_returns_expected_rules(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(
            dmnDecisionTableResult.getResultList(), is(List.of(
                Map.of(
                    "name", "task-supervisor",
                    "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel",
                    "autoAssignable", false
                ),
                Map.of(
                    "name", "hearing-centre-admin",
                    "value", "Read,Own,Claim,Manage,Unassign,Assign,Complete,Cancel",
                    "roleCategory", "ADMIN",
                    "assignmentPriority", 1,
                    "autoAssignable", false
                )
            ))
        );
    }

    @Test
    void given_any_taskType_when_evaluate_dmn_then_it_contains_supervisor_rule() {
        String randomString = UUID.randomUUID().toString();
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", randomString));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        MatcherAssert.assertThat(
            dmnDecisionTableResult.getResultList(), is(List.of(
                Map.of(
                    "name", "task-supervisor",
                    "value", "Read,Execute,Claim,Manage,Unassign,Assign,Complete,Cancel",
                    "autoAssignable", false
                )
            ))
        );
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
        assertThat(logic.getRules().size(), is(2));
    }
}
