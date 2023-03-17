package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_TYPES_IA_ASYLUM;

class CamundaTaskTypesTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_TYPES_IA_ASYLUM;
    }

    static Stream<Arguments> scenarioProvider() {
        List<Map<String, String>> taskTypes = List.of(
            Map.of("taskTypeId", "processApplication", "taskTypeName", "Process Application"),
            Map.of("taskTypeId", "processApplicationToReviewDecision",
                   "taskTypeName", "Process Application to Review Decision"
            ),
            Map.of("taskTypeId", "editListing", "taskTypeName", "Edit Listing"),
            Map.of("taskTypeId", "reviewTheAppeal", "taskTypeName", "Review the appeal"),
            Map.of("taskTypeId", "decideOnTimeExtension", "taskTypeName", "Decide On Time Extension"),
            Map.of("taskTypeId", "reviewRespondentEvidence", "taskTypeName", "Review Respondent Evidence"),
            Map.of("taskTypeId", "reviewAdditionalEvidence", "taskTypeName", "Review additional evidence"),
            Map.of("taskTypeId", "reviewAdditionalHomeOfficeEvidence",
                   "taskTypeName",
                   "Review additional Home Office evidence"
            ),
            Map.of("taskTypeId", "reviewAppealSkeletonArgument", "taskTypeName", "Review Appeal Skeleton Argument"),
            Map.of("taskTypeId", "reviewReasonsForAppeal", "taskTypeName", "Review Reasons For Appeal"),
            Map.of("taskTypeId",
                   "reviewClarifyingQuestionsAnswers",
                   "taskTypeName",
                   "Review Clarifying Questions Answers"
            ),
            Map.of("taskTypeId", "reviewCmaRequirements", "taskTypeName", "Review Cma Requirements"),
            Map.of("taskTypeId", "attendCma", "taskTypeName", "Attend Cma"),
            Map.of("taskTypeId", "reviewRespondentResponse", "taskTypeName", "Review Respondent Response"),
            Map.of("taskTypeId", "caseSummaryHearingBundleStartDecision", "taskTypeName", "Create Hearing Bundle"),
            Map.of("taskTypeId", "reviewHearingRequirements", "taskTypeName", "Review hearing requirements"),
            Map.of("taskTypeId",
                   "followUpOverdueRespondentEvidence",
                   "taskTypeName",
                   "Follow-up overdue respondent evidence"
            ),
            Map.of("taskTypeId", "followUpExtendedDirection", "taskTypeName", "Follow-up extended direction"),
            Map.of("taskTypeId", "followUpOverdueCaseBuilding", "taskTypeName", "Follow-up overdue case building"),
            Map.of("taskTypeId",
                   "followUpOverdueReasonsForAppeal",
                   "taskTypeName",
                   "Follow-up overdue reasons for appeal"
            ),
            Map.of("taskTypeId",
                   "followUpOverdueClarifyingAnswers",
                   "taskTypeName",
                   "Follow-up overdue clarifying answers"
            ),
            Map.of("taskTypeId",
                   "followUpOverdueCmaRequirements",
                   "taskTypeName",
                   "Follow-up overdue CMA requirements"
            ),
            Map.of("taskTypeId",
                   "followUpOverdueRespondentReview",
                   "taskTypeName",
                   "Follow-up overdue respondent review"
            ),
            Map.of("taskTypeId",
                   "followUpOverdueHearingRequirements",
                   "taskTypeName",
                   "Follow-up overdue hearing requirements"
            ),
            Map.of("taskTypeId", "followUpNonStandardDirection", "taskTypeName", "Follow-up non-standard direction"),
            Map.of("taskTypeId", "followUpNoticeOfChange", "taskTypeName", "Follow-up Notice of Change"),
            Map.of("taskTypeId", "reviewAddendumEvidence", "taskTypeName", "Review Addendum Evidence"),
            Map.of("taskTypeId", "reviewHearingBundle", "taskTypeName", "Review Hearing bundle"),
            Map.of("taskTypeId", "sendDecisionsAndReasons", "taskTypeName", "Send decisions and reasons"),
            Map.of("taskTypeId", "prepareDecisionsAndReasons", "taskTypeName", "Prepare decisions and reasons"),
            Map.of("taskTypeId", "uploadHearingRecording", "taskTypeName", "Upload hearing recording"),
            Map.of("taskTypeId", "decideAnFTPA", "taskTypeName", "Decide an FTPA"),
            Map.of("taskTypeId", "allocateHearingJudge", "taskTypeName", "Allocate Hearing Judge"),
            Map.of("taskTypeId", "reviewRemissionApplication", "taskTypeName", "Review Remission Application"),
            Map.of("taskTypeId", "assignAFTPAJudge", "taskTypeName", "Assign a FTPA Judge"),
            Map.of("taskTypeId", "listTheCase", "taskTypeName", "List the case"),
            Map.of("taskTypeId", "sendPaymentRequest", "taskTypeName", "Send Payment Request"),
            Map.of("taskTypeId", "markAsPaid", "taskTypeName", "Mark as Paid")
        );
        return Stream.of(
            Arguments.of(
                taskTypes
            )
        );
    }

    @ParameterizedTest(name = "retrieve all task type data")
    @MethodSource("scenarioProvider")
    void should_evaluate_dmn_return_all_task_type_fields(List<Map<String, Object>> expectedTaskTypes) {

        VariableMap inputVariables = new VariableMapImpl();
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        MatcherAssert.assertThat(dmnDecisionTableResult.getResultList(), is(expectedTaskTypes));

    }

    @Test
    void check_dmn_changed() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(1));
        assertThat(logic.getOutputs().size(), is(2));
        assertThat(logic.getRules().size(), is(38));

    }


}
