package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_TYPES_IA_ASYLUM;

class CamundaTaskTypesTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_TYPES_IA_ASYLUM;
    }

    static Stream<Arguments> scenarioProvider() {
        List<Map<String, String>> taskTypes = List.of(
            getMap(
                "processApplicationAdjourn",
                "Process Adjourn Application"
            ),
            getMap(
                "detainedProcessApplicationAdjourn",
                "Detained - Process Adjourn Application"
            ),
            getMap(
                "processApplicationExpedite",
                "Process Expedite Application"
            ),
            getMap(
                "detainedProcessApplicationExpedite",
                "Detained - Process Expedite Application"
            ),
            getMap(
                "processApplicationTimeExtension",
                "Process Time Extension Application"
            ),
            getMap(
                "detainedProcessApplicationTimeExtension",
                "Detained - Process Time Extension Application"
            ),
            getMap(
                "processApplicationTransfer",
                "Process Transfer Application"
            ),
            getMap(
                "detainedProcessApplicationTransfer",
                "Detained - Process Transfer Application"
            ),
            getMap(
                "processApplicationWithdraw",
                "Process Withdraw Application"
            ),
            getMap(
                "detainedProcessApplicationWithdraw",
                "Detained - Process Withdraw Application"
            ),
            getMap(
                "processApplicationUpdateHearingRequirements",
                "Process Update Hearing Requirements Application"
            ),
            getMap(
                "detainedProcessApplicationUpdateHearingRequirements",
                "Detained - Process Update Hearing Requirements Application"
            ),
            getMap(
                "processApplicationUpdateAppealDetails",
                "Process Update Appeal Details Application"
            ),
            getMap(
                "detainedProcessApplicationUpdateAppealDetails",
                "Detained - Process Update Appeal Details Application"
            ),
            getMap(
                "processApplicationReinstateAnEndedAppeal",
                "Process Reinstate An Ended Appeal Application"
            ),
            getMap(
                "detainedFollowUpNoticeOfChange",
                "Detained - Follow-up Notice of Change"
            ),
            getMap(
                "detainedFollowUpNonStandardDirection",
                "Detained - Follow-up non-standard direction"
            ),
            getMap(
                "detainedFollowUpOverdueRespondentReview",
                "Detained - Follow-up overdue respondent review"
            ),
            getMap(
                "detainedFollowUpOverdueCaseBuilding",
                "Detained - Follow-up overdue case building"
            ),
            getMap(
                "detainedFollowUpExtendedDirection",
                "Detained - Follow-up extended direction"
            ),
            getMap(
                "detainedFollowUpOverdueRespondentEvidence",
                "Detained - Follow-up overdue respondent evidence"
            ),

            getMap(
                "detainedProcessApplicationReinstateAnEndedAppeal",
                "Detained - Process Reinstate An Ended Appeal Application"

            ),
            getMap(
                "processApplicationOther",
                "Process Other Application"
            ),
            getMap(
                "detainedProcessApplicationOther",
                "Detained - Process Other Application"
            ),
            getMap(
                "processApplicationLink/UnlinkAppeals",
                "Process Link/Unlink Appeals Application"
            ),
            getMap(
                "detainedProcessApplicationLink/UnlinkAppeals",
                "Detained - Process Link/Unlink Appeals Application"
            ),
            getMap(
                "processApplicationToReviewDecision",
                "Process Application to Review Decision"
            ),
            getMap(
                "detainedProcessApplicationToReviewDecision",
                "Detained - Process Application to Review Decision"
            ),
            getMap(
                "editListing",
                "Edit Listing"
            ),
            getMap(
                "detainedEditListing",
                "Detained - Edit Listing"
            ),
            getMap(
                "reviewTheAppeal",
                "Review the appeal"
            ),
            getMap(
                "reviewRespondentEvidence",
                "Review Respondent Evidence"
            ),
            getMap(
                "reviewAdditionalEvidence",
                "Review additional evidence"
            ),
            getMap(
                "reviewAdditionalHomeOfficeEvidence",
                "Review additional Home Office evidence"
            ),
            getMap(
                "reviewAppealSkeletonArgument",
                "Review Appeal Skeleton Argument"
            ),
            getMap(
                "detainedReviewAppealSkeletonArgument",
                "Detained - Review Appeal Skeleton Argument"
            ),
            getMap(
                "reviewReasonsForAppeal",
                "Review Reasons For Appeal"
            ),
            getMap(
                "reviewClarifyingQuestionsAnswers",
                "Review Clarifying Questions Answers"
            ),
            getMap(
                "reviewRespondentResponse",
                "Review Respondent Response"
            ),
            getMap(
                "detainedReviewRespondentResponse",
                "Detained - Review Respondent Response"
            ),
            getMap(
                "caseSummaryHearingBundleStartDecision",
                "Create Hearing Bundle"
            ),
            getMap(
                "reviewHearingRequirements",
                "Review hearing requirements"
            ),
            getMap(
                "followUpOverdueRespondentEvidence",
                "Follow-up overdue respondent evidence"
            ),
            getMap(
                "followUpExtendedDirection",
                "Follow-up extended direction"
            ),
            getMap(
                "followUpOverdueCaseBuilding",
                "Follow-up overdue case building"
            ),
            getMap(
                "followUpOverdueReasonsForAppeal",
                "Follow-up overdue reasons for appeal"
            ),
            getMap(
                "followUpOverdueClarifyingAnswers",
                "Follow-up overdue clarifying answers"
            ),
            getMap(
                "followUpOverdueRespondentReview",
                "Follow-up overdue respondent review"
            ),
            getMap(
                "followUpOverdueHearingRequirements",
                "Follow-up overdue hearing requirements"
            ),
            getMap(
                "detainedFollowUpOverdueHearingRequirements",
                "Detained - Follow-up overdue hearing requirements"
            ),
            getMap(
                "followUpNonStandardDirection",
                "Follow-up non-standard direction"
            ),
            getMap(
                "followUpNoticeOfChange",
                "Follow-up Notice of Change"
            ),
            getMap(
                "reviewAddendumEvidence",
                "Review Addendum Evidence"
            ),
            getMap(
                "detainedReviewAddendumEvidence",
                "Detained - Review Addendum Evidence"
            ),
            getMap(
                "sendDecisionsAndReasons",
                "Send decisions and reasons"
            ),
            getMap(
                "detainedSendDecisionsAndReasons",
                "Detained - Send decisions and reasons"
            ),
            getMap(
                "prepareDecisionsAndReasons",
                "Prepare decisions and reasons"
            ),
            getMap(
                "uploadHearingRecording",
                "Upload hearing recording"
            ),
            getMap(
                "postHearingAttendeesDurationAndRecording",
                "Post hearing – attendees, duration and recording"
            ),
            getMap(
                "detainedPostHearingAttendeesDurationAndRecording",
                "Detained - Post hearing – attendees, duration and recording"
            ),
            getMap(
                "decideAnFTPA",
                "Decide an FTPA"
            ),
            getMap(
                "detainedDecideAnFTPA",
                "Detained - Decide an FTPA"
            ),
            getMap(
                "allocateHearingJudge",
                "Allocate Hearing Judge"
            ),
            getMap(
                "detainedAllocateHearingJudge",
                "Detained - Allocate Hearing Judge"
            ),
            getMap(
                "reviewRemissionApplication",
                "Review Remission Application"
            ),
            getMap(
                "detainedReviewRemissionApplication",
                "Detained - Review Remission Application"
            ),
            getMap(
                "assignAFTPAJudge",
                "Assign a FTPA Judge"
            ),
            getMap(
                "detainedAssignAFTPAJudge",
                "Detained - Assign a FTPA Judge"
            ),
            getMap(
                "listTheCase",
                "List the case"
            ),
            getMap(
                "detainedListTheCase",
                "Detained - List the case"
            ),
            getMap(
                "sendPaymentRequest",
                "Send Payment Request"
            ),
            getMap(
                "markAsPaid",
                "Mark as Paid"
            ),
            getMap(
                "reviewRemittedAppeal",
                "Review remitted appeal"
            ),
            getMap(
                "detainedReviewRemittedAppeal",
                "Detained - Review remitted appeal"
            ),
            getMap(
                "reviewSetAsideDecisionApplication",
                "Review set aside decision application"
            ),
            getMap(
                "followUpSetAsideDecision",
                "Follow up set aside decision"
            ),
            getMap(
                "detainedFollowUpSetAsideDecision",
                "Detained - Follow Up Set Aside Decision"
            ),
            getMap(
                "reviewAppealSetAsideUnderRule35",
                "Review appeal set aside under rule 35"
            ),
            getMap(
                "detainedReviewAppealSetAsideUnderRule35",
                "Detained - Review appeal set aside under rule 35"
            ),
            getMap(
                "reviewAppealSetAsideUnderRule32",
                "Review appeal set aside under rule 32"
            ), getMap(
                "detainedReviewAppealSetAsideUnderRule32",
                "Detained - Review appeal set aside under rule 32"
            ),
            getMap(
                "hearingException",
                "Hearing exception"
            ),
            getMap(
                "detainedHearingException",
                "Detained - Hearing exception"
            ),
            getMap(
                "cmrListed",
                "Send CMR notification"
            ),
            getMap(
                "detainedCmrListed",
                "Detained - Send CMR notification"
            ),
            getMap(
                "cmrUpdated",
                "Update CMR notification"
            ),
            getMap(
                "detainedCmrUpdated",
                "Detained - Update CMR notification"
            ),
            getMap(
                "reviewInterpreters",
                "Review interpreter booking"
            ),
            getMap(
                "detainedReviewInterpreters",
                "Detained - Review interpreter booking"
            ),
            getMap(
                "relistCase",
                "Relist The Case"
            ),
            getMap(
                "detainedRelistCase",
                "Detained - Relist The Case"
            ),
            getMap(
                "processApplicationChangeHearingType",
                "Process Change Hearing Type Application"
            ),
            getMap(
                "processFeeRefund",
                "Process fee refund"
            ),
            getMap(
                "detainedProcessFeeRefund",
                "Detained - Process Fee Refund"
            ),
            getMap(
                "reviewMigratedCase",
                "Review migrated case"
            ),
            getMap(
                "detainedReviewMigratedCase",
                "Detained - Review migrated case"
            ),
            getMap(
                "reviewAriaRemissionApplication",
                "Review Remission Application"
            ),
            getMap(
                "reviewDraftAppeal",
                "Review draft appeal"
            ),
            getMap(
                "DetainedReviewDraftAppeal",
                "Detained - Review Draft Appeal"
            ),
            getMap(
                "printAndSendHoBundle",
                "Print and send HO bundle and appeal reasons form"
            ),
            getMap(
                "detainedPrintAndSendHoBundle",
                "Detained - Print and send HO bundle and appeal reasons form"
            ),
            getMap(
                "printAndSendHoResponse",
                "Print and send HO response"
            ),
            getMap(
                "printAndSendHearingRequirements",
                "Print and send hearing requirements form"
            ),
            getMap(
                "detainedPrintAndSendHearingRequirements",
                "Detained - Print and send hearing requirements form"
            ),
            getMap(
                "printAndSendHearingBundle",
                "Print and send hearing bundle"
            ),
            getMap(
                "detainedPrintAndSendHearingBundle",
                "Detained - Print and send hearing bundle"
            ),
            getMap(
                "printAndSendDecisionCorrectedRule31",
                "Print and send decision corrected under rule 31"
            ),
            getMap(
                "detainedPrintAndSendDecisionCorrectedRule31",
                "Detained - Print and send decision corrected under rule 31"
            ),
            getMap(
                "printAndSendDecisionCorrectedRule32",
                "Print and send decision corrected under rule 32"
            ),
            getMap(
                "detainedPrintAndSendDecisionCorrectedRule32",
                "Detained - Print and send decision corrected under rule 32"
            ),
            getMap(
                "printAndSendHoApplication",
                "Print and send HO application"
            ),
            getMap(
                "detainedPrintAndSendHoApplication",
                "Detained - Print and send HO application"
            ),
            getMap(
                "printAndSendHoEvidence",
                "Print and send new HO evidence"
            ),
            getMap(
                "detainedPrintAndSendHoEvidence",
                "Detained - Print and send new HO evidence"
            ),
            getMap(
                "printAndSendAppealDecision",
                "Print and send appeal decision and FTPA form"
            ),
            getMap(
                "detainedPrintAndSendAppealDecision",
                "Detained - Print and send appeal decision and FTPA form"
            ),
            getMap(
                "printAndSendFTPADecision",
                "Print and send FTPA decision"
            ),
            getMap(
                "detainedPrintAndSendFTPADecision",
                "Detained - Print and send FTPA decision"
            ),
            getMap(
                "printAndSendReheardHearingRequirements",
                "Print and send reheard appeal hearing requirements form"
            ),
            getMap(
                "detainedPrintAndSendReheardHearingRequirements",
                "Detained - Print and send reheard appeal hearing requirements form"
            ),
            getMap(
                "detainedListCmr",
                "Detained - List CMR"
            ),
            getMap(
                "postHearingAttendeesDurationAndRecording",
                "Post hearing – attendees, duration and recording"
            ),
            getMap(
                "detainedPostHearingAttendeesDurationAndRecording",
                "Detained - Post hearing – attendees, duration and recording"
            ),
            getMap(
                "detainedReviewHearingRequirements",
                "Detained - Review hearing requirements"
            ),
            getMap(
                "detainedProcessApplicationChangeHearingType",
                "Detained - Process Change Hearing Type Application"
            ),
            getMap(
                "reviewASRemission",
                "Review AS remission"
            ),
            getMap(
                "reviewLARemission",
                "Review LA remission"
            ),
            getMap(
                "reviewHOWaiverRemission",
                "Review HO Waiver remission"
            ),
            getMap(
                "reviewAuthorityRemission",
                "Review Authority remission"
            ),
            getMap(
                "reviewHWFRemission",
                "Review HWF remission"
            ),
            getMap(
                "reviewECRRemission",
                "Review ECR remission"
            ),
            getMap(
                "detainedReviewASRemission",
                "Detained - Review AS remission"
            ),
            getMap(
                "detainedReviewLARemission",
                "Detained - Review LA remission"
            ),
            getMap(
                "detainedReviewHOWaiverRemission",
                "Detained - Review HO Waiver remission"
            ),
            getMap(
                "detainedReviewAuthorityRemission",
                "Detained - Review Authority remission"
            ),
            getMap(
                "detainedReviewHWFRemission",
                "Detained - Review HWF remission"
            ),
            getMap(
                "detainedReviewECRRemission",
                "Detained - Review ECR remission"
            ),
            getMap(
                "detainedReviewTheAppeal",
                "Detained - Review the appeal"
            ),
            getMap(
                "detainedReviewRespondentEvidence",
                "Detained - Review Respondent Evidence"
            ),
            getMap(
                "detainedReviewAdditionalEvidence",
                "Detained - Review additional evidence"
            ),
            getMap(
                "detainedReviewAdditionalHomeOfficeEvidence",
                "Detained - Review additional Home Office evidence"
            ),
            getMap(
                "detainedHearingException",
                "Detained - Hearing exception"
            )
        );
        return taskTypes.stream().map(taskType -> Arguments.of(Named.of(taskType.get("taskTypeId"), taskType)));
    }

    private static Map<String, String> getMap(String taskTypeId, String taskTypeName) {
        return Map.of(
            "taskTypeId", taskTypeId,
            "taskTypeName", taskTypeName
        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void should_evaluate_dmn_return_all_task_type_fields(Map<String, Object> expectedTaskType) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(Collections.emptyMap());
        List<Map<String, Object>> resultList = dmnDecisionTableResult.getResultList();
        assertTrue(resultList.contains(expectedTaskType));
    }

    @Test
    void check_dmn_changed() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertEquals(1, logic.getInputs().size());
        assertEquals(2, logic.getOutputs().size());
        assertEquals(142, logic.getRules().size());

    }
}
