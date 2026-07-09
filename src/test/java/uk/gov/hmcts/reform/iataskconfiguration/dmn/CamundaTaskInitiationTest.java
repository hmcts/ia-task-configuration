package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_IA_ASYLUM;

class CamundaTaskInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_IA_ASYLUM;
    }

    private static final String hearingDate = LocalDateTime.now().plusDays(5)
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    static Stream<Arguments> scenarioProvider() {
        LocalDateTime directionDueDate = LocalDateTime.now().plusDays(5);
        Map<String, LocalDateTime> variablesDirectionDueDate = Map.of(
            "directionDueDate", directionDueDate
        );
        Map<String, Object> delayUntilDirectionDue = Map.of(
            "delayUntilIntervalDays", "0",
            "delayUntil", directionDueDate
        );
        Map<String, Object> delayForDaysExcludingBankHolidays = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
            "delayUntilOrigin", LocalDate.now(),
            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
        );

        Map<String, Object> delayFor12Days = Map.of(
            "delayUntilIntervalDays", "12",
            "delayUntilOrigin", LocalDate.now()
        );

        Map<String, Object> delayForDays = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilOrigin", LocalDate.now()
        );

        Map<String, Object> delayUntilHearing = Map.of(
            "delayUntil", hearingDate,
            "delayUntilIntervalDays", "0"
        );

        return Stream.of(
            getArgumentOf(
                "applyForFTPAAppellant",
                null,
                Map.of(
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("decideAnFTPA", "Decide an FTPA", "caseProgression"),
                getTaskMap("assignAFTPAJudge", "Assign a FTPA Judge", "caseProgression")
            ),
            getArgumentOf(
                "applyForFTPAAppellant",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("detainedDecideAnFTPA", "Detained - Decide an FTPA", "caseProgression"),
                getTaskMap("detainedAssignAFTPAJudge", "Detained - Assign a FTPA Judge", "caseProgression")
            ),
            getArgumentOf(
                "applyForFTPAAppellant",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("detainedDecideAnFTPA", "Detained - Decide an FTPA", "caseProgression"),
                getTaskMap("detainedAssignAFTPAJudge", "Detained - Assign a FTPA Judge", "caseProgression")
            ),
            getArgumentOf(
                "applyForFTPARespondent",
                null,
                Map.of(
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("decideAnFTPA", "Decide an FTPA", "caseProgression"),
                getTaskMap("assignAFTPAJudge", "Assign a FTPA Judge", "caseProgression")
            ),
            getArgumentOf(
                "applyForFTPARespondent",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("detainedDecideAnFTPA", "Detained - Decide an FTPA", "caseProgression"),
                getTaskMap("detainedAssignAFTPAJudge", "Detained - Assign a FTPA Judge", "caseProgression")
            ),
            getArgumentOf(
                "applyForFTPARespondent",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("detainedDecideAnFTPA", "Detained - Decide an FTPA", "caseProgression"),
                getTaskMap("detainedAssignAFTPAJudge", "Detained - Assign a FTPA Judge", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "revocationOfProtection",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "deprivation",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionClaim", "asylumSupport",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewASRemission", "Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "euSettlementScheme",
                    "remissionOption", "asylumSupportFromHo",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewASRemission", "Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionClaim", "asylumSupport",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("detainedReviewASRemission", "Detained - Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "euSettlementScheme",
                    "remissionOption", "asylumSupportFromHo",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("detainedReviewASRemission", "Detained - Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionClaim", "homeOfficeWaiver",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewHOWaiverRemission", "Review HO Waiver remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionClaim", "homeOfficeWaiver",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap(
                    "detainedReviewHOWaiverRemission",
                    "Detained - Review HO Waiver remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewECRRemission", "Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewECRRemission", "Detained - Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "helpWithFees",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "protection",
                    "remissionOption", "feeWaiverFromHo",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewHOWaiverRemission", "Review HO Waiver remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "protection",
                    "remissionOption", "feeWaiverFromHo",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap(
                    "detainedReviewHOWaiverRemission",
                    "Detained - Review HO Waiver remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "refusalOfHumanRights",
                    "remissionOption", "under18GetSupportFromLocalAuthority",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewAuthorityRemission", "Review Authority remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "refusalOfHumanRights",
                    "remissionOption", "under18GetSupportFromLocalAuthority",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap(
                    "detainedReviewAuthorityRemission",
                    "Detained - Review Authority remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "refusalOfHumanRights",
                    "remissionOption", "iWantToGetHelpWithFees",
                    "isNotificationTurnedOff", false
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "refusalOfHumanRights",
                    "remissionOption", "noneOfTheseStatements",
                    "helpWithFeesOption",
                    "alreadyApplied",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "journeyType", "aip",
                    "appealType", "refusalOfHumanRights",
                    "remissionOption", "noneOfTheseStatements",
                    "helpWithFeesOption",
                    "wantToApply",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewHWFRemission", "Detained - Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "remissionClaim", "legalAid",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewLARemission", "Review LA remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "remissionClaim", "legalAid",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewLARemission", "Detained - Review LA remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "remissionClaim", "section17",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAuthorityRemission", "Review Authority remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "remissionClaim", "section17",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAuthorityRemission",
                    "Detained - Review Authority remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "paymentAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "journeyType", "aip",
                    "paymentStatus",
                    "Paid",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "paymentAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "journeyType", "aip",
                    "paymentStatus",
                    "Paid",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "paymentAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "journeyType", "aip",
                    "paymentStatus",
                    "Paid",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "journeyType", "aip",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "markAppealPaid",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "markAppealPaid",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "moveToSubmitted",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "moveToSubmitted",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "payForAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "payForAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "recordRemissionDecision",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "recordRemissionDecision",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "updatePaymentStatus",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "updatePaymentStatus",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "submitTimeExtension",
                "null",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("decideOnTimeExtension", "Decide On Time Extension", "timeExtension")
            ),
            getArgumentOf(
                "uploadHomeOfficeBundle",
                "awaitingRespondentEvidence",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewRespondentEvidence", "Review Respondent Evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAdditionalEvidence", "Review additional evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "respondentReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAdditionalEvidence", "Review additional evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "submitHearingRequirements",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAdditionalEvidence", "Review additional evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAdditionalEvidence", "Review additional evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "prepareForHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAdditionalEvidence", "Review additional evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAdditionalHomeOfficeEvidence",
                    "Review additional Home Office evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAdditionalHomeOfficeEvidence",
                    "Review additional Home Office evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAdditionalHomeOfficeEvidence",
                    "Review additional Home Office evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "respondentReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAdditionalHomeOfficeEvidence",
                    "Review additional Home Office evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "submitHearingRequirements",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAdditionalHomeOfficeEvidence",
                    "Review additional Home Office evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAdditionalHomeOfficeEvidence",
                    "Review additional Home Office evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "submitCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAppealSkeletonArgument", "Review Appeal Skeleton Argument", "caseProgression")
            ),
            getArgumentOf(
                "submitCase",
                "caseUnderReview",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAppealSkeletonArgument",
                    "Detained - Review Appeal Skeleton Argument",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "buildCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAppealSkeletonArgument", "Review Appeal Skeleton Argument", "caseProgression")
            ),
            getArgumentOf(
                "buildCase",
                "caseUnderReview",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAppealSkeletonArgument",
                    "Detained - Review Appeal Skeleton Argument",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "submitReasonsForAppeal",
                "reasonsForAppealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewReasonsForAppeal", "Review Reasons For Appeal", "caseProgression")
            ),
            getArgumentOf(
                "submitClarifyingQuestionAnswers",
                "clarifyingQuestionsAnswersSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewClarifyingQuestionsAnswers", "Review Clarifying Questions Answers", "caseProgression")
            ),
            getArgumentOf(
                "submitCmaRequirements",
                "cmaRequirementsSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewCmaRequirements", "Review Cma Requirements", "caseProgression")
            ),
            getArgumentOf(
                "listCma",
                "cmaListed",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("attendCma", "Attend Cma", "caseProgression")
            ),
            getArgumentOf(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewRespondentResponse", "Review Respondent Response", "caseProgression")
            ),
            getArgumentOf(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewRespondentResponse",
                    "Detained - Review Respondent Response",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "listCase",
                "prepareForHearing",
                Map.of(
                    "listCaseHearingDate",
                    hearingDate,
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("caseSummaryHearingBundleStartDecision", "Create Hearing Bundle", "caseProgression"),
                getTaskMap(
                    "postHearingAttendeesDurationAndRecording",
                    "Post hearing – attendees, duration and recording",
                    "caseProgression",
                    delayUntilHearing
                )
            ),
            getArgumentOf(
                "listCase",
                "prepareForHearing",
                Map.of(
                    "listCaseHearingDate",
                    hearingDate,
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedCaseSummaryHearingBundleStartDecision",
                    "Detained - Create Hearing Bundle",
                    "caseProgression"
                ),
                getTaskMap(
                    "detainedPostHearingAttendeesDurationAndRecording",
                    "Detained - Post hearing – attendees, duration and recording",
                    "caseProgression",
                    delayUntilHearing
                )
            ),
            getArgumentOf(
                "listCase",
                "prepareForHearing",
                Map.of(
                    "listCaseHearingDate",
                    hearingDate,
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedCaseSummaryHearingBundleStartDecision",
                    "Detained - Create Hearing Bundle",
                    "caseProgression"
                ),
                getTaskMap(
                    "detainedPostHearingAttendeesDurationAndRecording",
                    "Detained - Post hearing – attendees, duration and recording",
                    "caseProgression",
                    delayUntilHearing
                )
            ),
            getArgumentOf(
                "draftHearingRequirements",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHearingRequirements", "Review hearing requirements", "caseProgression")
            ),
            getArgumentOf(
                "draftHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewHearingRequirements",
                    "Detained - Review hearing requirements",
                    "caseProgression"
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedFollowUpOverdueRespondentEvidence",
                    "Detained - Follow-up overdue respondent evidence",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueRespondentEvidence",
                    "Follow-up overdue respondent evidence",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "changeDirectionDueDate",
                null,
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpExtendedDirection",
                    "Follow-up extended direction",
                    "caseProgression",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "changeDirectionDueDate",
                null,
                variablesDirectionDueDate,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedFollowUpExtendedDirection",
                    "Detained - Follow-up extended direction",
                    "caseProgression",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestCaseBuilding",
                "caseBuilding",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueCaseBuilding",
                    "Follow-up overdue case building",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestCaseBuilding",
                "caseBuilding",
                variablesDirectionDueDate,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedFollowUpOverdueCaseBuilding",
                    "Detained - Follow-up overdue case building",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestReasonsForAppeal",
                "awaitingReasonsForAppeal",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueReasonsForAppeal",
                    "Follow-up overdue reasons for appeal",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "sendDirectionWithQuestions",
                "awaitingClarifyingQuestionsAnswers",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueClarifyingAnswers",
                    "Follow-up overdue clarifying answers",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestCmaRequirements",
                "awaitingCmaRequirements",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueCmaRequirements",
                    "Follow-up overdue CMA requirements",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestRespondentReview",
                "respondentReview",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueRespondentReview",
                    "Follow-up overdue respondent review",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestRespondentReview",
                "respondentReview",
                variablesDirectionDueDate,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedFollowUpOverdueRespondentReview",
                    "Detained - Follow-up overdue respondent review",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "requestHearingRequirementsFeature",
                "submitHearingRequirements",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpOverdueHearingRequirements",
                    "Follow-up overdue hearing requirements",
                    "followUpOverdue",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "sendDirection",
                null,
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpNonStandardDirection",
                    "Follow-up non-standard direction",
                    "caseProgression",
                    delayUntilDirectionDue
                )
            ),
            getWithLocalDateTimeMapArgumentOf(
                "sendDirection",
                null,
                variablesDirectionDueDate,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedFollowUpNonStandardDirection",
                    "Detained - Follow-up non-standard direction",
                    "caseProgression",
                    delayUntilDirectionDue
                )
            ),
            getArgumentOf(
                "removeRepresentation",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpNoticeOfChange",
                    "Follow-up Notice of Change",
                    "followUpOverdue",
                    delayForDaysExcludingBankHolidays
                )
            ),
            getArgumentOf(
                "removeRepresentation",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedFollowUpNoticeOfChange",
                    "Detained - Follow-up Notice of Change",
                    "followUpOverdue",
                    delayForDaysExcludingBankHolidays
                )
            ),
            getArgumentOf(
                "removeLegalRepresentative",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "followUpNoticeOfChange",
                    "Follow-up Notice of Change",
                    "followUpOverdue",
                    delayForDaysExcludingBankHolidays
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "prepareForHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("editListing", "Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "prepareForHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedEditListing", "Detained - Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "finalBundling",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("editListing", "Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "prepareForHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedEditListing", "Detained - Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "preHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("editListing", "Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "preHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedEditListing", "Detained - Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "decision",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("editListing", "Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "changeHearingCentre",
                "decision",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedEditListing", "Detained - Edit listing", "caseProgression")
            ),
            getArgumentOf(
                "generateHearingBundle",
                "finalBundling",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("allocateHearingJudge", "Allocate Hearing Judge", "caseProgression")
            ),
            getArgumentOf(
                "generateHearingBundle",
                "finalBundling",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedAllocateHearingJudge", "Detained - Allocate Hearing Judge", "caseProgression")
            ),
            getArgumentOf(
                "customiseHearingBundle",
                "finalBundling",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("allocateHearingJudge", "Allocate Hearing Judge", "caseProgression")
            ),
            getArgumentOf(
                "customiseHearingBundle",
                "finalBundling",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedAllocateHearingJudge", "Detained - Allocate Hearing Judge", "caseProgression")
            ),
            getArgumentOf(
                "sendToPreHearing",
                "preHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("allocateHearingJudge", "Allocate Hearing Judge", "caseProgression")
            ),
            getArgumentOf(
                "sendToPreHearing",
                "preHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedAllocateHearingJudge", "Detained - Allocate Hearing Judge", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewTheAppeal", "Detained - Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression"),
                getTaskMap("reviewECRRemission", "Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewTheAppeal", "Detained - Review the appeal", "caseProgression"),
                getTaskMap("detainedReviewECRRemission", "Detained - Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "remissionType", "helpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression"),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "remissionType", "helpWithFees",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewTheAppeal", "Detained - Review the appeal", "caseProgression"),
                getTaskMap("detainedReviewHWFRemission", "Detained - Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewECRRemission", "Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewECRRemission", "Detained - Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "helpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "helpWithFees",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewHWFRemission", "Detained - Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "helpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "helpWithFees",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewHWFRemission", "Detained - Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "asylumSupportFromHo",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewASRemission", "Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "asylumSupportFromHo",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewASRemission", "Detained - Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "asylumSupport",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewASRemission", "Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "asylumSupport",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewASRemission", "Detained - Review AS remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "homeOfficeWaiver",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHOWaiverRemission", "Review HO Waiver remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "homeOfficeWaiver",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewHOWaiverRemission",
                    "Detained - Review HO Waiver remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "exceptionalCircumstancesRemission",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewECRRemission", "Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "exceptionalCircumstancesRemission",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewECRRemission", "Detained - Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "feeWaiverFromHo",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHOWaiverRemission", "Review HO Waiver remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "feeWaiverFromHo",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewHOWaiverRemission",
                    "Detained - Review HO Waiver remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "parentGetSupportFromLocalAuthority",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAuthorityRemission", "Review Authority remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "parentGetSupportFromLocalAuthority",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAuthorityRemission",
                    "Detained - Review Authority remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "iWantToGetHelpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "noneOfTheseStatements",
                    "helpWithFeesOption",
                    "wantToApply",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "noneOfTheseStatements",
                    "helpWithFeesOption",
                    "alreadyApplied",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "iWantToGetHelpWithFees",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewHWFRemission", "Detained - Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "legalAid",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewLARemission", "Review LA remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "legalAid",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewLARemission", "Detained - Review LA remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "section20",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAuthorityRemission", "Review Authority remission", "caseProgression")
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "section20",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAuthorityRemission",
                    "Detained - Review Authority remission",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "reviewHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("listTheCase", "List the case", "caseProgression")
            ),
            getArgumentOf(
                "reviewHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedListTheCase", "Detained - List the case", "caseProgression")
            ),
            getArgumentOf(
                "listCaseWithoutHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("listTheCase", "List the case", "caseProgression")
            ),
            getArgumentOf(
                "listCaseWithoutHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedListTheCase", "Detained - List the case", "caseProgression")
            ),
            getArgumentOf(
                "recordRemissionDecision",
                null,
                Map.of(
                    "remissionDecision", "partiallyApproved",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("sendPaymentRequest", "Send Payment Request", "caseProgression"),
                getTaskMap("markAsPaid", "Mark as Paid", "followUpOverdue", delayFor12Days)
            ),
            getArgumentOf(
                "recordRemissionDecision",
                null,
                Map.of(
                    "remissionDecision",
                    "partiallyApproved",
                    "paymentStatus",
                    "Paid",
                    "isNotificationTurnedOff", "false"
                ),
                null
            ),
            getArgumentOf(
                "markAppealAsRemitted",
                "remitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewRemittedAppeal", "Review remitted appeal", "caseProgression")
            ),
            getArgumentOf(
                "markAppealAsRemitted",
                "remitted",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewRemittedAppeal", "Detained - Review remitted appeal", "caseProgression")
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "ftpaAppellantRjDecisionOutcomeType",
                    "reheardRule35",
                    "ftpaApplicantType",
                    "appellant",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAppealSetAsideUnderRule35",
                    "Review appeal set aside under rule 35",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "ftpaAppellantRjDecisionOutcomeType",
                    "reheardRule35",
                    "ftpaApplicantType",
                    "appellant",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAppealSetAsideUnderRule35",
                    "Detained - Review appeal set aside under rule 35",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "ftpaRespondentRjDecisionOutcomeType",
                    "reheardRule35",
                    "ftpaApplicantType",
                    "respondent",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAppealSetAsideUnderRule35",
                    "Review appeal set aside under rule 35",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "ftpaRespondentRjDecisionOutcomeType",
                    "reheardRule35",
                    "ftpaApplicantType",
                    "respondent",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAppealSetAsideUnderRule35",
                    "Detained - Review appeal set aside under rule 35",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "updateTribunalDecisionList", "underRule32",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAppealSetAsideUnderRule32",
                    "Review appeal set aside under rule 32",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "updateTribunalDecisionList",
                    "underRule32",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAppealSetAsideUnderRule32",
                    "Detained - Review appeal set aside under rule 32",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "handleHearingException",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("hearingException", "Hearing Exception", "caseProgression")
            ),
            getArgumentOf(
                "handleHearingException",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedHearingException", "Detained - Hearing Exception", "caseProgression")
            ),
            getArgumentOf(
                "cmrReListing",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("cmrUpdated", "Update CMR notification", "caseProgression")
            ),
            getArgumentOf(
                "cmrReListing",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedCmrUpdated", "Detained - Update CMR notification", "caseProgression")
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedCmrListed", "Detained - Send CMR notification", "caseProgression")
            ),
            getArgumentOf(
                "restoreStateFromAdjourn",
                null,
                Map.of(
                    "isIntegrated", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("relistCase", "Relist The Case", "caseProgression")
            ),
            getArgumentOf(
                "restoreStateFromAdjourn",
                null,
                Map.of(
                    "isIntegrated", true, "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedRelistCase", "Detained - Relist The Case", "caseProgression")
            ),
            getArgumentOf(
                "recordAdjournmentDetails",
                null,
                Map.of(
                    "hearingAdjournmentWhen",
                    "onHearingDate",
                    "relistCaseImmediately",
                    true,
                    "autoHearingRequestEnabled",
                    false,
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("listTheCase", "List the case", "caseProgression")
            ),
            getArgumentOf(
                "recordAdjournmentDetails",
                null,
                Map.of(
                    "hearingAdjournmentWhen",
                    "onHearingDate",
                    "relistCaseImmediately",
                    true,
                    "autoHearingRequestEnabled",
                    false,
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedListTheCase", "Detained - List the case", "caseProgression")
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("cmrListed", "Send CMR notification", "caseProgression")
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedCmrListed", "Detained - Send CMR notification", "caseProgression")
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("cmrListed", "Send CMR notification", "caseProgression")
            ),
            getArgumentOf(
                "decisionAndReasonsStarted",
                "decision",
                Map.of(
                    "isIntegrated",
                    true,
                    "isDecisionWithoutHearing",
                    true,
                    "autoHearingRequestEnabled",
                    false,
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("listTheCase", "List the case", "caseProgression")
            ),
            getArgumentOf(
                "decisionAndReasonsStarted",
                "decision",
                Map.of(
                    "isIntegrated",
                    true,
                    "autoHearingRequestEnabled",
                    false,
                    "isDecisionWithoutHearing",
                    true,
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedListTheCase", "Detained - List the case", "caseProgression")
            ),
            getArgumentOf(
                "triggerReviewInterpreterBookingTask",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewInterpreters", "Review interpreter booking", "caseProgression")
            ),
            getArgumentOf(
                "triggerReviewInterpreterBookingTask",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewInterpreters", "Detained - Review interpreter booking", "caseProgression")
            ),
            getArgumentOf(
                "hearingCancelled",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewInterpreters", "Review interpreter booking", "caseProgression")
            ),
            getArgumentOf(
                "hearingCancelled",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewInterpreters", "Detained - Review interpreter booking", "caseProgression")
            ),
            getArgumentOf(
                "editCaseListing",
                null,
                Map.of(
                    "shouldTriggerReviewInterpreterTask", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewInterpreters", "Review interpreter booking", "caseProgression")
            ),
            getArgumentOf(
                "editCaseListing",
                null,
                Map.of(
                    "shouldTriggerReviewInterpreterTask",
                    "true",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewInterpreters", "Detained - Review interpreter booking", "caseProgression")
            ),
            getArgumentOf(
                "manageFeeUpdate",
                null,
                Map.of(
                    "feeUpdateTribunalAction",
                    "refund",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("processFeeRefund", "Process fee refund", "caseProgression")
            ),
            getArgumentOf(
                "manageFeeUpdate",
                null,
                Map.of(
                    "feeUpdateTribunalAction",
                    "refund",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedProcessFeeRefund", "Detained - Process Fee Refund", "caseProgression")
            ),
            getArgumentOf(
                "manageFeeUpdate",
                null,
                Map.of(
                    "feeUpdateTribunalAction",
                    "refund",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedProcessFeeRefund", "Detained - Process Fee Refund", "caseProgression")
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewMigratedCase", "Review migrated case", "caseProgression")
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewMigratedCase", "Detained - Review migrated case", "caseProgression")
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewMigratedCase", "Review migrated case", "caseProgression")
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewMigratedCase", "Detained - Review migrated case", "caseProgression")
            ),
            getArgumentOf(
                "startAppeal",
                "appealStartedByAdmin",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewDraftAppeal", "Review draft appeal", "followUpOverdue", delayForDays)
            ),
            getArgumentOf(
                "startAppeal",
                "appealStartedByAdmin",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "DetainedReviewDraftAppeal",
                    "Detained - Review Draft Appeal",
                    "followUpOverdue",
                    delayForDays
                )
            ),
            getArgumentOf(
                "requestResponseReview",
                "respondentReview",
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("printAndSendHoResponse", "Print and send HO response", "caseProgression")
            ),
            getArgumentOf(
                "asyncStitchingComplete",
                "preHearing",
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("printAndSendHearingBundle", "Print and send hearing bundle", "caseProgression")
            ),
            getArgumentOf(
                "asyncStitchingComplete",
                "preHearing",
                Map.of(
                    "isAdmin", "true",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedPrintAndSendHearingBundle",
                    "Detained - Print and send hearing bundle",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "isAdmin",
                    "true",
                    "isDecisionRule31Changed",
                    "true",
                    "updateTribunalDecisionList",
                    "underRule31",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "printAndSendDecisionCorrectedRule31",
                    "Print and send decision corrected under rule 31",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "isAdmin",
                    "true",
                    "isDecisionRule31Changed",
                    "true",
                    "updateTribunalDecisionList",
                    "underRule31",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedPrintAndSendDecisionCorrectedRule31",
                    "Detained - Print and send decision corrected under rule 31",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "isAdmin",
                    "true",
                    "updateTribunalDecisionList",
                    "underRule32",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "reviewAppealSetAsideUnderRule32",
                    "Review appeal set aside under rule 32",
                    "caseProgression"
                ),
                getTaskMap(
                    "printAndSendDecisionCorrectedRule32",
                    "Print and send decision corrected under rule 32",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "isAdmin",
                    "true",
                    "updateTribunalDecisionList",
                    "underRule32",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap(
                    "detainedReviewAppealSetAsideUnderRule32",
                    "Detained - Review appeal set aside under rule 32",
                    "caseProgression"
                ),
                getTaskMap(
                    "detainedPrintAndSendDecisionCorrectedRule32",
                    "Detained - Print and send decision corrected under rule 32",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "makeAnApplication",
                null,
                Map.of(
                    "isAdmin",
                    "true",
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false",
                    "lastModifiedApplication",
                    Map.of(
                        "type",
                        "Judge's review of application decision",
                        "decision",
                        "",
                        "applicant",
                        "Respondent"
                    )
                ),
                getTaskMap(
                    "processApplicationToReviewDecision",
                    "Process Application to Review Decision",
                    "application"
                ),
                getTaskMap("printAndSendHoApplication", "Print and send HO application", "caseProgression")
            ),
            getArgumentOf(
                "makeAnApplication",
                null,
                Map.of(
                    "isAdmin",
                    "true",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false",
                    "lastModifiedApplication",
                    Map.of(
                        "type",
                        "Judge's review of application decision",
                        "decision",
                        "",
                        "applicant",
                        "Respondent"
                    )
                ),
                getTaskMap(
                    "detainedProcessApplicationToReviewDecision",
                    "Detained - Process Application to Review Decision",
                    "application"
                ),
                getTaskMap(
                    "detainedPrintAndSendHoApplication",
                    "Detained - Print and send HO application",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                getTaskMap("printAndSendHoEvidence", "Print and send new HO evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedPrintAndSendHoEvidence",
                    "Detained - Print and send new HO evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAddendumEvidenceHomeOffice",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "false"
                ),
                getTaskMap("printAndSendHoEvidence", "Print and send new HO evidence", "caseProgression")
            ),
            getArgumentOf(
                "uploadAddendumEvidenceHomeOffice",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedPrintAndSendHoEvidence",
                    "Detained - Print and send new HO evidence",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                getTaskMap("printAndSendFTPADecision", "Print and send FTPA decision", "caseProgression")
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedPrintAndSendFTPADecision",
                    "Detained - Print and send FTPA decision",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "requestNewHearingRequirements",
                "submitHearingRequirements",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                getTaskMap(
                    "printAndSendReheardHearingRequirements",
                    "Print and send reheard appeal hearing requirements form",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "requestNewHearingRequirements",
                "submitHearingRequirements",
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedPrintAndSendReheardHearingRequirements",
                    "Detained - Print and send reheard appeal hearing requirements form",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "protection"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "revocationOfProtection"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "deprivation"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "refusalOfHumanRights"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "refusalOfEu"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "euSettlementScheme"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "helpWithFees"
                ),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "exceptionalCircumstancesRemission"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression"),
                getTaskMap("reviewECRRemission", "Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "helpWithFees"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression"),
                getTaskMap("reviewHWFRemission", "Review HWF remission", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "isNotificationTurnedOff", "false",
                    "remissionClaim", "homeOfficeWaiver"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression"),
                getTaskMap("reviewHOWaiverRemission", "Review HO Waiver remission", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "exceptionalCircumstancesRemission"
                ),
                getTaskMap("reviewTheAppeal", "Review the appeal", "caseProgression"),
                getTaskMap("reviewECRRemission", "Review ECR remission", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewAppealSkeletonArgument", "Review Appeal Skeleton Argument", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedReviewAppealSkeletonArgument",
                    "Detained - Review Appeal Skeleton Argument",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedReviewAppealSkeletonArgument",
                    "Detained - Review Appeal Skeleton Argument",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap(
                    "detainedReviewAppealSkeletonArgument",
                    "Detained - Review Appeal Skeleton Argument",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "reasonsForAppealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewReasonsForAppeal", "Review Reasons For Appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "prepareForHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("caseSummaryHearingBundleStartDecision", "Create Hearing Bundle", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "remitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("reviewRemittedAppeal", "Review remitted appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "remitted",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("detainedReviewRemittedAppeal", "Detained - Review remitted appeal", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "ftpaSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                getTaskMap("assignAFTPAJudge", "Assign a FTPA Judge", "caseProgression"),
                getTaskMap("decideAnFTPA", "Decide an FTPA", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "ftpaSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap("detainedAssignAFTPAJudge", "Detained - Assign a FTPA Judge", "caseProgression"),
                getTaskMap("detainedDecideAnFTPA", "Detained - Decide an FTPA", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "reviewedHearingRequirements", "false"
                ),
                getTaskMap("reviewHearingRequirements", "Review hearing requirements", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "reviewedHearingRequirements", "false",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"

                ),
                getTaskMap(
                    "detainedReviewHearingRequirements",
                    "Detained - Review hearing requirements",
                    "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "reviewedHearingRequirements", "true"
                ),
                getTaskMap("listTheCase", "List the case", "caseProgression")
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "reviewedHearingRequirements", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"

                ),
                getTaskMap("detainedListTheCase", "Detained - List the case", "caseProgression")
            ),
            getArgumentOf(
                "generateListCmrTask",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                getTaskMap("detainedListCmr", "Detained - List CMR", "caseProgression")
            ),
            getArgumentOf(
                "completeCaseReview",
                "appealSubmitted",
                Map.of(
                    "stf24wCurrentStatusAutoGenerated", true
                ),
                getTaskMap("listTheCase", "List the case", "listTheCaseTask")
            ),
            getArgumentOf(
                "completeCaseReview",
                "listing",
                Map.of(
                    "stf24wCurrentStatusAutoGenerated", true
                ),
                getTaskMap("listTheCase", "List the case", "listTheCaseTask")
            ),
            getArgumentOf(
                "sendToAdminForListing",
                "prepareForHearing",
                Map.of(
                    "stf24wCurrentStatusAutoGenerated", true
                ),
                getTaskMap("listTheCase", "List the case", "listTheCaseTask")
            ),
            getArgumentOf("unknownEvent", null, null, null)
        );

    }

    private static Map<String, Object> getTaskMap(
        String taskId,
        String name,
        String processCategories
    ) {
        return Map.of(
            "taskId", taskId,
            "name", name,
            "processCategories", processCategories
        );
    }

    private static Map<String, Object> getTaskMap(
        String taskId,
        String name,
        String processCategories,
        Map<String, Object> delayUntil
    ) {
        return Map.of(
            "taskId", taskId,
            "name", name,
            "processCategories", processCategories,
            "delayUntil", delayUntil
        );
    }

    private static Arguments getArgumentOf(String eventId,
                                           String postEventState,
                                           Map<String, Object> additionalData,
                                           Map<String, Object> expectation) {
        return Arguments.of(
            eventId,
            postEventState,
            additionalData != null ? mapAdditionalData(additionalData) : null,
            expectation != null ? List.of(expectation) : emptyList()
        );
    }

    private static Arguments getArgumentOf(String eventId,
                                           String postEventState,
                                           Map<String, Object> additionalData,
                                           Map<String, Object> expectationOne,
                                           Map<String, Object> expectationTwo) {
        return Arguments.of(
            eventId,
            postEventState,
            additionalData != null ? mapAdditionalData(additionalData) : null,
            List.of(expectationOne, expectationTwo)
        );
    }

    private static Arguments getWithLocalDateTimeMapArgumentOf(String eventId,
                                                               String postEventState,
                                                               Map<String, LocalDateTime> localDateTimeMap,
                                                               Map<String, Object> additionalData,
                                                               Map<String, Object> expectation) {
        return Arguments.of(
            eventId,
            postEventState,
            additionalData != null ? merge(localDateTimeMap, mapAdditionalData(additionalData)) : localDateTimeMap,
            expectation != null ? List.of(expectation) : emptyList()
        );
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} additional data: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> map,
                                                      List<Map<String, Object>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue(
            "now", LocalDateTime.now().minusMinutes(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        inputVariables.putAll(map);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertEquals(expectation, dmnDecisionTableResult.getResultList());
    }

    @SafeVarargs
    private static Map<String, Object> merge(Map<String, ?>... maps) {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, ?> map : maps) {
            if (map != null) {
                result.putAll(map);
            }
        }
        return result;
    }

    public static Stream<Arguments> multipleMapScenarioProvider() {
        LocalDateTime directionDueDate = LocalDateTime.now().plusDays(5);
        Map<String, Object> variablesDirectionDueDate = Map.of(
            "directionDueDate", directionDueDate
        );
        Map<String, Object> delayUntilDirectionDue = Map.of(
            "delayUntilIntervalDays", "0",
            "delayUntil", directionDueDate
        );

        return Stream.of(
            getMultipleMapArgumentOf(
                "requestCaseBuilding",
                "caseBuilding",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                Map.of(
                    "taskId", "followUpOverdueCaseBuilding",
                    "name", "Follow-up overdue case building",
                    "processCategories", "followUpOverdue",
                    "delayUntil", delayUntilDirectionDue
                ),
                Map.of(
                    "taskId", "printAndSendHoBundle",
                    "name", "Print and send HO bundle and appeal reasons form",
                    "processCategories", "caseProgression"
                )

            ),
            getMultipleMapArgumentOf(
                "requestHearingRequirementsFeature",
                "submitHearingRequirements",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                Map.of(
                    "taskId", "followUpOverdueHearingRequirements",
                    "name", "Follow-up overdue hearing requirements",
                    "processCategories", "followUpOverdue",
                    "delayUntil", delayUntilDirectionDue
                ),
                Map.of(
                    "taskId", "printAndSendHearingRequirements",
                    "name", "Print and send hearing requirements form",
                    "processCategories", "caseProgression"
                )

            ),
            getMultipleMapArgumentOf(
                "progressMigratedCase",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "uploadHomeOfficeBundleAvailable", "false"
                ),
                Map.of(
                    "taskId", "followUpOverdueRespondentEvidence",
                    "name", "Follow-up overdue respondent evidence",
                    "processCategories", "followUpOverdue",
                    "delayUntil", delayUntilDirectionDue
                ),
                null
            ),
            getMultipleMapArgumentOf(
                "progressMigratedCase",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "uploadHomeOfficeBundleAvailable", "true"
                ),
                Map.of(
                    "taskId", "reviewRespondentEvidence",
                    "name", "Review Respondent Evidence",
                    "processCategories", "caseProgression"
                ),
                null
            )
        );
    }


    private static Arguments getMultipleMapArgumentOf(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> additionalMap,
                                                      Map<String, Object> additionalData,
                                                      Map<String, Object> expectationOne,
                                                      Map<String, Object> expectationTwo) {
        return Arguments.of(
            eventId,
            postEventState,
            additionalMap,
            mapAdditionalData(additionalData),
            expectationTwo == null ? List.of(expectationOne) : List.of(expectationOne, expectationTwo)
        );
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} "
        + "additional map: {2} additional data: {3} expectation data: {4}")
    @MethodSource("multipleMapScenarioProvider")
    void given_multiple_data_map_should_evaluate_dmn(String eventId,
                                                     String postEventState,
                                                     Map<String, Object> additionalMap,
                                                     Map<String, Object> additionalData,
                                                     List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putAll(additionalMap);
        inputVariables.putAll(additionalData);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertEquals(expectation, dmnDecisionTableResult.getResultList());
    }


    public static Stream<Arguments> makeAnApplicationScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                                "lastModifiedApplication" : {
                                                  "type" : "",
                                                  "decision" : "",
                                                  "applicant" : "",
                                                  "appellantInDetention" : ""
                                                }
                                              }
                                            }\
                                      """),
                emptyList()
            ),
            getMakeApplicationArgumentOf(
                "Adjourn",
                "processApplicationAdjourn",
                "Process Adjourn Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Adjourn",
                "detainedProcessApplicationAdjourn",
                "Detained - Process Adjourn Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Expedite",
                "processApplicationExpedite",
                "Process Expedite Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Expedite",
                "detainedProcessApplicationExpedite",
                "Detained - Process Expedite Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Time extension",
                "processApplicationTimeExtension",
                "Process Time Extension Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Time extension",
                "detainedProcessApplicationTimeExtension",
                "Detained - Process Time Extension Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Transfer",
                "processApplicationTransfer",
                "Process Transfer Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Transfer",
                "detainedProcessApplicationTransfer",
                "Detained - Process Transfer Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Withdraw",
                "processApplicationWithdraw",
                "Process Withdraw Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Withdraw",
                "detainedProcessApplicationWithdraw",
                "Detained - Process Withdraw Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Update hearing requirements",
                "processApplicationUpdateHearingRequirements",
                "Process Update Hearing Requirements Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Update hearing requirements",
                "detainedProcessApplicationUpdateHearingRequirements",
                "Detained - Process Update Hearing Requirements Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Update appeal details",
                "processApplicationUpdateAppealDetails",
                "Process Update Appeal Details Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Update appeal details",
                "detainedProcessApplicationUpdateAppealDetails",
                "Detained - Process Update Appeal Details Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Reinstate an ended appeal",
                "processApplicationReinstateAnEndedAppeal",
                "Process Reinstate An Ended Appeal Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Reinstate an ended appeal",
                "detainedProcessApplicationReinstateAnEndedAppeal",
                "Detained - Process Reinstate An Ended Appeal Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Other",
                "processApplicationOther",
                "Process Other Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Other",
                "detainedProcessApplicationOther",
                "Detained - Process Other Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Link/unlink appeals",
                "processApplicationLink/UnlinkAppeals",
                "Process Link/Unlink Appeals Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Link/unlink appeals",
                "detainedProcessApplicationLink/UnlinkAppeals",
                "Detained - Process Link/Unlink Appeals Application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Set aside a decision",
                "reviewSetAsideDecisionApplication",
                "Review set aside decision application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Set aside a decision",
                "detainedReviewSetAsideDecisionApplication",
                "Detained - Review set aside decision application",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Judge's review of application decision",
                "processApplicationToReviewDecision",
                "Process Application to Review Decision",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Judge's review of application decision",
                "detainedProcessApplicationToReviewDecision",
                "Detained - Process Application to Review Decision",
                true,
                false
            ),
            getMakeApplicationArgumentOf(
                "Change hearing type",
                "processApplicationChangeHearingType",
                "Process Change Hearing Type Application",
                false,
                false
            ),
            getMakeApplicationArgumentOf(
                "Change hearing type",
                "detainedProcessApplicationChangeHearingType",
                "Detained - Process Change Hearing Type Application",
                true,
                false
            )
        );
    }

    private static Arguments getMakeApplicationArgumentOf(String applicationType,
                                                          String taskId,
                                                          String taskName,
                                                          boolean appellantInDetention,
                                                          boolean isNotificationTurnedOff) {
        return Arguments.of(
            "makeAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                                  + "            \"decision\" : \"\",\n"
                                  + "            \"applicant\" : \"\"\n"
                                  + "          },\n"
                                  + "          \"appellantInDetention\" : \"" + appellantInDetention + "\",\n"
                                  + "          \"isNotificationTurnedOff\" : \"" + isNotificationTurnedOff + "\"\n"
                                  + "        }\n"
                                  + "      }"),
            singletonList(
                Map.of(
                    "taskId", taskId,
                    "name", taskName,

                    "processCategories", "application"
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("makeAnApplicationScenarioProvider")
    void given_makeAnApplication_should_evaluate_dmn(String eventId,
                                                     String postEventState,
                                                     Map<String, Object> additionalData,
                                                     List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putAll(additionalData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertEquals(dmnDecisionTableResult.getResultList(), expectation);
    }

    public static Stream<Arguments> decideAnApplicationScenarioProvider() {
        Map<String, Object> delayFor5Days = Map.of(
            "delayUntilIntervalDays", "5",
            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
            "delayUntilOrigin", LocalDate.now(),
            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
        );

        return Stream.of(
            getDecideAnApplicationArgumentsOf(
                "Adjourn", "editListing",
                "Edit Listing", "application", null, false
            ),
            getDecideAnApplicationArgumentsOf(
                "Expedite", "editListing",
                "Edit Listing", "application", null, false
            ),
            getDecideAnApplicationArgumentsOf(
                "Transfer", "editListing",
                "Edit Listing", "application", null, false
            ),
            getDecideAnApplicationArgumentsOf(
                "Adjourn", "editListing",
                "Edit Listing", "application", null, true
            ),
            getDecideAnApplicationArgumentsOf(
                "Expedite", "editListing",
                "Edit Listing", "application", null, true
            ),
            getDecideAnApplicationArgumentsOf(
                "Transfer", "editListing",
                "Edit Listing", "application", null, true
            ),
            getDecideAnApplicationArgumentsOf(
                "Set aside a decision",
                "followUpSetAsideDecision",
                "Follow up set aside decision",
                "followUpOverdue",
                delayFor5Days, false, false, false
            ),
            getDecideAnApplicationArgumentsOf(
                "Set aside a decision",
                "detainedFollowUpSetAsideDecision",
                "Detained - Follow Up Set Aside Decision",
                "followUpOverdue",
                delayFor5Days, false, true, false
            )
        );
    }

    private static Arguments getDecideAnApplicationArgumentsOf(String applicationType,
                                                               String taskId,
                                                               String name,
                                                               String processCategories,
                                                               Map<String, Object> delayUntil,
                                                               boolean isIntegrated) {
        Map<String, Object> map = new HashMap<>();
        if (!isIntegrated) {
            map.put("taskId", taskId);
            map.put("name", name);
            map.put("processCategories", processCategories);
            if (delayUntil != null) {
                map.put("delayUntil", delayUntil);
            }
        }

        return Arguments.of(
            "decideAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                                  + "            \"decision\" : \"Granted\",\n"
                                  + "            \"applicant\" : \"\"\n"
                                  + "          },\n"
                                  + "          \"isIntegrated\" : \"" + isIntegrated + "\"\n"
                                  + "        }\n"
                                  + "      }"),
            singletonList(map)
        );
    }

    private static Arguments getDecideAnApplicationArgumentsOf(String applicationType,
                                                               String taskId,
                                                               String name,
                                                               String processCategories,
                                                               Map<String, Object> delayUntil,
                                                               boolean isIntegrated,
                                                               boolean appellantInDetention,
                                                               boolean isNotificationTurnedOff) {
        Map<String, Object> map = new HashMap<>();
        if (!isIntegrated) {
            map.put("taskId", taskId);
            map.put("name", name);
            map.put("processCategories", processCategories);
            if (delayUntil != null) {
                map.put("delayUntil", delayUntil);
            }
        }
        return Arguments.of(
            "decideAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                                  + "            \"decision\" : \"Granted\",\n"
                                  + "            \"applicant\" : \"\"\n"
                                  + "          },\n"
                                  + "          \"isIntegrated\" : \"" + isIntegrated + "\",\n"
                                  + "          \"appellantInDetention\" : \"" + appellantInDetention + "\",\n"
                                  + "          \"isNotificationTurnedOff\" : \"" + isNotificationTurnedOff + "\"\n"
                                  + "        }\n"
                                  + "      }"),
            singletonList(map)
        );
    }


    @ParameterizedTest
    @MethodSource("decideAnApplicationScenarioProvider")
    void given_decideAnApplication_should_evaluate_dmn(String eventId,
                                                       String postEventState,
                                                       Map<String, Object> additionalData,
                                                       List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putAll(additionalData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        if (dmnDecisionTableResult.getResultList().isEmpty()) {
            assertEquals(singletonList(emptyMap()), expectation);
        } else {
            assertEquals(expectation.size(), dmnDecisionTableResult.getResultList().size());
            assertEquals(expectation, dmnDecisionTableResult.getResultList());
        }
    }

    public static Stream<Arguments> addendumScenarioProvider() {
        Map<String, Object> appellantInDetention = mapAdditionalData("""
                                                                         {
                                                                           "Data":{
                                                                           "appellantInDetention" : "true",
                                                                           "isNotificationTurnedOff" : "false"
                                                                           }
                                                                         }""");

        return Stream.of(
            Arguments.of(
                "uploadAddendumEvidenceLegalRep",
                "preHearing",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAddendumEvidence",
                        "name", "Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceLegalRep",
                "decision",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAddendumEvidence",
                        "name", "Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceLegalRep",
                "decided",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAddendumEvidence",
                        "name", "Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceLegalRep",
                "preHearing",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAddendumEvidence",
                        "name", "Detained - Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidence",
                "decision",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAddendumEvidence",
                        "name", "Detained - Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceHomeOffice",
                "decided",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAddendumEvidence",
                        "name", "Detained - Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceAdminOfficer",
                "preHearing",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAddendumEvidence",
                        "name", "Detained - Review Addendum Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertEquals(33, logic.getInputs().size());
        assertEquals(4, logic.getOutputs().size());
        assertEquals(203, logic.getRules().size());
    }

    @ParameterizedTest
    @MethodSource("addendumScenarioProvider")
    void given_addendum_events_when_evaluate_initiate_dmn_then_expect_reviewAddendumEvidence_task(
        String eventId,
        String postEventState,
        Map<String, Object> additionalData,
        List<Map<String, String>> expectation
    ) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        if (additionalData != null) {
            inputVariables.putAll(additionalData);
        }

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertEquals(dmnDecisionTableResult.getResultList(), expectation);
    }


    private static Map<String, Object> mapAdditionalData(String additionalData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
            };
            return Map.of("additionalData", mapper.readValue(additionalData, typeRef));
        } catch (IOException exp) {
            return null;
        }
    }


    private static Map<String, Object> mapAdditionalData(Map<String, Object> additionalData) {
        return Map.of("additionalData", Map.of("Data", additionalData));
    }

}
