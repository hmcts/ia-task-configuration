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
            Map.of("taskTypeId",
                   "processApplicationAdjourn",
                   "taskTypeName",
                   "Process Adjourn Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationExpedite",
                   "taskTypeName",
                   "Process Expedite Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationTimeExtension",
                   "taskTypeName",
                   "Process Time Extension Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationTransfer",
                   "taskTypeName",
                   "Process Transfer Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationWithdraw",
                   "taskTypeName",
                   "Process Withdraw Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationUpdateHearingRequirements",
                   "taskTypeName",
                   "Process Update Hearing Requirements Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationUpdateAppealDetails",
                   "taskTypeName",
                   "Process Update Appeal Details Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationReinstateAnEndedAppeal",
                   "taskTypeName",
                   "Process Reinstate An Ended Appeal Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationOther",
                   "taskTypeName",
                   "Process Other Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationLink/UnlinkAppeals",
                   "taskTypeName",
                   "Process Link/Unlink Appeals Application"
            ),
            Map.of("taskTypeId",
                   "processApplicationToReviewDecision",
                   "taskTypeName",
                   "Process Application to Review Decision"
            ),
            Map.of("taskTypeId",
                   "editListing",
                   "taskTypeName",
                   "Edit Listing"
            ),
            Map.of("taskTypeId",
                   "reviewTheAppeal",
                   "taskTypeName",
                   "Review the appeal"
            ),
            Map.of("taskTypeId",
                   "reviewRespondentEvidence",
                   "taskTypeName",
                   "Review Respondent Evidence"
            ),
            Map.of("taskTypeId",
                   "reviewAdditionalEvidence",
                   "taskTypeName",
                   "Review additional evidence"
            ),
            Map.of("taskTypeId",
                   "reviewAdditionalHomeOfficeEvidence",
                   "taskTypeName",
                   "Review additional Home Office evidence"
            ),
            Map.of("taskTypeId",
                   "reviewAppealSkeletonArgument",
                   "taskTypeName",
                   "Review Appeal Skeleton Argument"
            ),
            Map.of("taskTypeId",
                   "reviewReasonsForAppeal",
                   "taskTypeName",
                   "Review Reasons For Appeal"
            ),
            Map.of("taskTypeId",
                   "reviewClarifyingQuestionsAnswers",
                   "taskTypeName",
                   "Review Clarifying Questions Answers"
            ),
            Map.of("taskTypeId",
                   "reviewRespondentResponse",
                   "taskTypeName",
                   "Review Respondent Response"
            ),
            Map.of("taskTypeId",
                   "caseSummaryHearingBundleStartDecision",
                   "taskTypeName",
                   "Create Hearing Bundle"
            ),
            Map.of("taskTypeId",
                   "reviewHearingRequirements",
                   "taskTypeName",
                   "Review hearing requirements"
            ),
            Map.of("taskTypeId",
                   "followUpOverdueRespondentEvidence",
                   "taskTypeName",
                   "Follow-up overdue respondent evidence"
            ),
            Map.of("taskTypeId",
                   "followUpExtendedDirection",
                   "taskTypeName",
                   "Follow-up extended direction"
            ),
            Map.of("taskTypeId",
                   "followUpOverdueCaseBuilding",
                   "taskTypeName",
                   "Follow-up overdue case building"
            ),
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
            Map.of("taskTypeId",
                   "followUpNonStandardDirection",
                   "taskTypeName",
                   "Follow-up non-standard direction"
            ),
            Map.of("taskTypeId",
                   "followUpNoticeOfChange",
                   "taskTypeName",
                   "Follow-up Notice of Change"
            ),
            Map.of("taskTypeId",
                   "reviewAddendumEvidence",
                   "taskTypeName",
                   "Review Addendum Evidence"
            ),
            Map.of("taskTypeId",
                   "sendDecisionsAndReasons",
                   "taskTypeName",
                   "Send decisions and reasons"
            ),
            Map.of("taskTypeId",
                   "prepareDecisionsAndReasons",
                   "taskTypeName",
                   "Prepare decisions and reasons"
            ),
            Map.of("taskTypeId",
                   "uploadHearingRecording",
                   "taskTypeName",
                   "Upload hearing recording"
            ),
            Map.of("taskTypeId",
                   "postHearingAttendeesDurationAndRecording",
                   "taskTypeName",
                   "Post hearing – attendees, duration and recording"
            ),
            Map.of("taskTypeId",
                   "decideAnFTPA",
                   "taskTypeName",
                   "Decide an FTPA"
            ),
            Map.of("taskTypeId",
                   "allocateHearingJudge",
                   "taskTypeName",
                   "Allocate Hearing Judge"
            ),
            Map.of("taskTypeId",
                   "reviewRemissionApplication",
                   "taskTypeName",
                   "Review Remission Application"
            ),
            Map.of("taskTypeId",
                   "assignAFTPAJudge",
                   "taskTypeName",
                   "Assign a FTPA Judge"
            ),
            Map.of("taskTypeId",
                   "listTheCase",
                   "taskTypeName",
                   "List the case"
            ),
            Map.of("taskTypeId",
                   "sendPaymentRequest",
                   "taskTypeName",
                   "Send Payment Request"
            ),
            Map.of("taskTypeId",
                   "markAsPaid",
                   "taskTypeName",
                   "Mark as Paid"
            ),
            Map.of("taskTypeId",
                   "reviewRemittedAppeal",
                   "taskTypeName",
                   "Review remitted appeal"
            ),
            Map.of("taskTypeId",
                   "reviewSetAsideDecisionApplication",
                   "taskTypeName",
                   "Review set aside decision application"
            ),
            Map.of("taskTypeId",
                   "followUpSetAsideDecision",
                   "taskTypeName",
                   "Follow up set aside decision"
            ),
            Map.of("taskTypeId",
                   "reviewAppealSetAsideUnderRule35",
                   "taskTypeName",
                   "Review appeal set aside under rule 35"
            ),
            Map.of("taskTypeId",
                   "reviewAppealSetAsideUnderRule32",
                   "taskTypeName",
                   "Review appeal set aside under rule 32"
            ),
            Map.of("taskTypeId",
                "hearingException",
                "taskTypeName",
                "Hearing exception"
            ),
            Map.of("taskTypeId",
                "cmrListed",
                "taskTypeName",
                "Send CMR notification"
            ),
            Map.of("taskTypeId",
                "cmrUpdated",
                "taskTypeName",
                "Update CMR notification"
            ),
            Map.of("taskTypeId",
                "reviewInterpreters",
                "taskTypeName",
                "Review interpreter booking"
            ),
            Map.of("taskTypeId",
                "relistCase",
                "taskTypeName",
                "Relist The Case"
            ),
            Map.of("taskTypeId",
                "processApplicationChangeHearingType",
                "taskTypeName",
                "Process Change Hearing Type Application"
            ),
            Map.of("taskTypeId",
                   "processFeeRefund",
                   "taskTypeName",
                   "Process fee refund"
            ),
            Map.of("taskTypeId",
                   "reviewMigratedCase",
                   "taskTypeName",
                   "Review migrated case"
            ),
            Map.of("taskTypeId",
                   "reviewAriaRemissionApplication",
                   "taskTypeName",
                   "Review Remission Application"
            ),
            Map.of("taskTypeId",
                   "reviewDraftAppeal",
                   "taskTypeName",
                   "Review draft appeal"
            ),
            Map.of("taskTypeId",
                   "printAndSendHoBundle",
                   "taskTypeName",
                   "Print and send HO bundle and appeal reasons form"
            ),
            Map.of("taskTypeId",
                   "printAndSendHoResponse",
                   "taskTypeName",
                   "Print and send HO response"
            ),
            Map.of("taskTypeId",
                   "printAndSendHearingRequirements",
                   "taskTypeName",
                   "Print and send hearing requirements form"
            ),
            Map.of("taskTypeId",
                   "printAndSendHearingBundle",
                   "taskTypeName",
                   "Print and send hearing bundle"
            ),
            Map.of("taskTypeId",
                   "printAndSendDecisionCorrectedRule31",
                   "taskTypeName",
                   "Print and send decision corrected under rule 31"
            ),
            Map.of("taskTypeId",
                   "printAndSendDecisionCorrectedRule32",
                   "taskTypeName",
                   "Print and send decision corrected under rule 32"
            ),
            Map.of("taskTypeId",
                   "printAndSendHoApplication",
                   "taskTypeName",
                   "Print and send HO application"
            ),
            Map.of("taskTypeId",
                   "printAndSendHoEvidence",
                   "taskTypeName",
                   "Print and send new HO evidence"
            ),
            Map.of("taskTypeId",
                   "printAndSendAppealDecision",
                   "taskTypeName",
                   "Print and send appeal decision and FTPA form"
            ),
            Map.of("taskTypeId",
                   "printAndSendFTPADecision",
                   "taskTypeName",
                   "Print and send FTPA decision"
            ),
            Map.of("taskTypeId",
                   "printAndSendReheardHearingRequirements",
                   "taskTypeName",
                   "Print and send reheard appeal hearing requirements form"
            )
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
        assertThat(logic.getRules().size(), is(69));
    }
}
