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
            Map.of("taskTypeId", "markAsPaid", "taskTypeName", "Mark as Paid"),
            Map.of("taskTypeId", "adaProcessApplicationToAdjourn",
                   "taskTypeName", "ADA-Process Application to Adjourn"),
            Map.of("taskTypeId", "adaProcessApplicationToExpedite",
                   "taskTypeName", "ADA-Process Application to Expedite"),
            Map.of("taskTypeId", "adaProcessApplicationForTimeExtension",
                   "taskTypeName", "ADA-Process Application for Time Extension"),
            Map.of("taskTypeId", "adaProcessApplicationToWithdraw",
                   "taskTypeName", "ADA-Process Application to Withdraw"),
            Map.of("taskTypeId", "adaProcessApplicationToReviewDecision",
                   "taskTypeName", "ADA-Process Application to Review Decision"),
            Map.of("taskTypeId", "adaProcessApplicationToUpdateHearingRequirements",
                   "taskTypeName", "ADA-Process Application to Update Hearing Requirements"),
            Map.of("taskTypeId", "adaProcessApplicationToUpdateAppealDetails",
                   "taskTypeName", "ADA-Process Application to Update Appeal Details"),
            Map.of("taskTypeId", "adaProcessApplicationToReinstateAnEndedAppeal",
                   "taskTypeName", "ADA-Process Application to Reinstate An Appeal"),
            Map.of("taskTypeId", "adaProcessApplicationToOther",
                   "taskTypeName", "ADA-Process Other Application"),
            Map.of("taskTypeId", "adaLinkUnlinkAppeals",
                   "taskTypeName", "ADA-Process Application to Link/Unlink Appeals"),
            Map.of("taskTypeId", "adaEditListing", "taskTypeName", "ADA-Edit Listing"),
            Map.of("taskTypeId", "adaReviewTheAppeal", "taskTypeName", "ADA-Review the appeal"),
            Map.of("taskTypeId", "adaReviewRespondentEvidence",
                   "taskTypeName", "ADA-Review Respondent Evidence"),
            Map.of("taskTypeId", "adaReviewAdditionalEvidence",
                   "taskTypeName", "ADA-Review additional evidence"),
            Map.of("taskTypeId", "adaReviewAdditionalHomeOfficeEvidence",
                   "taskTypeName", "ADA-Review additional Home Office evidence"),
            Map.of("taskTypeId", "adaReviewAppealSkeletonArgument",
                   "taskTypeName", "ADA-Review Appeal Skeleton Argument"),
            Map.of("taskTypeId", "adaCaseSummaryHearingBundleStartDecision",
                   "taskTypeName", "ADA-Create Hearing Bundle"),
            Map.of("taskTypeId", "adaFollowUpOverdueRespondentEvidence",
                   "taskTypeName", "ADA-Follow-up overdue respondent evidence"),
            Map.of("taskTypeId", "adaListCase", "taskTypeName", "ADA-List case"),
            Map.of("taskTypeId", "adaFollowUpExtendedDirection",
                   "taskTypeName", "ADA-Follow-up extended direction"),
            Map.of("taskTypeId", "adaFollowUpOverdueCaseBuilding",
                   "taskTypeName", "ADA-Follow-up overdue case building"),
            Map.of("taskTypeId", "adaFollowUpOverdueRespondentReview",
                   "taskTypeName", "ADA-Follow-up overdue respondent review"),
            Map.of("taskTypeId", "adaFollowUpNonStandardDirection",
                   "taskTypeName", "ADA-Follow-up non-standard direction"),
            Map.of("taskTypeId", "adaFollowUpNoticeOfChange",
                   "taskTypeName", "ADA-Follow-up Notice of Change"),
            Map.of("taskTypeId", "adaReviewAddendumEvidence",
                   "taskTypeName", "ADA-Review addendum evidence"),
            Map.of("taskTypeId", "adaReviewHearingBundle",
                   "taskTypeName", "ADA-Review Hearing bundle"),
            Map.of("taskTypeId", "adaSendDecisionsAndReasons",
                   "taskTypeName", "ADA-Send decisions and reasons"),
            Map.of("taskTypeId", "adaPrepareDecisionsAndReasons",
                   "taskTypeName", "ADA-Prepare decisions and reasons"),
            Map.of("taskTypeId", "adaUploadHearingRecording",
                   "taskTypeName", "ADA-Upload hearing recording"),
            Map.of("taskTypeId", "adaDecideAnFTPA", "taskTypeName", "ADA-Decide an FTPA"),
            Map.of("taskTypeId", "reviewCaseTransferredOutOfADA",
                   "taskTypeName", "Review Case Transferred Out Of ADA"),
            Map.of("taskTypeId", "reviewCaseMarkedUnsuitableForADA",
                   "taskTypeName", "Review Case Marked Unsuitable For ADA"),
            Map.of("taskTypeId", "reviewADASuitability", "taskTypeName", "Review ADA suitability"),
            Map.of("taskTypeId", "adaAllocateHearingJudge",
                   "taskTypeName", "ADA-Allocate Hearing Judge"),
            Map.of("taskTypeId", "adaAssignAFTPAJudge", "taskTypeName", "ADA-Assign a FTPA Judge"),
            Map.of("taskTypeId", "reviewTransferredS82AAppeal", "taskTypeName", "Review transferred S82A appeal")
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
        assertThat(logic.getRules().size(), is(70));

    }


}
