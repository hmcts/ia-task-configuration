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

        return Stream.of(
            getArgumentOf(
                "applyForFTPAAppellant",
                null,
                Map.of(
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "decideAnFTPA",
                    "name", "Decide an FTPA",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "assignAFTPAJudge",
                    "name", "Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "applyForFTPAAppellant",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "detainedDecideAnFTPA",
                    "name", "Detained - Decide an FTPA",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedAssignAFTPAJudge",
                    "name", "Detained - Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "applyForFTPAAppellant",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "detainedDecideAnFTPA",
                    "name", "Detained - Decide an FTPA",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedAssignAFTPAJudge",
                    "name", "Detained - Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "applyForFTPARespondent",
                null,
                Map.of(
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "decideAnFTPA",
                    "name", "Decide an FTPA",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "assignAFTPAJudge",
                    "name", "Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "applyForFTPARespondent",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "detainedDecideAnFTPA",
                    "name", "Detained - Decide an FTPA",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedAssignAFTPAJudge",
                    "name", "Detained - Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "applyForFTPARespondent",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "detainedDecideAnFTPA",
                    "name", "Detained - Decide an FTPA",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedAssignAFTPAJudge",
                    "name", "Detained - Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "revocationOfProtection",
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "deprivation",
                    "isNotificationTurnedOff", false
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewASRemission",
                    "name", "Review AS remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewASRemission",
                    "name", "Review AS remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewASRemission",
                    "name", "Detained - Review AS remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewASRemission",
                    "name", "Detained - Review AS remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewHOWaiverRemission",
                    "name", "Review HO Waiver remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewHOWaiverRemission",
                    "name", "Detained - Review HO Waiver remission",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewECRRemission",
                    "name", "Review ECR remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewECRRemission",
                    "name", "Detained - Review ECR remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewHOWaiverRemission",
                    "name", "Review HO Waiver remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewHOWaiverRemission",
                    "name", "Detained - Review HO Waiver remission",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewAuthorityRemission",
                    "name", "Review Authority remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewAuthorityRemission",
                    "name", "Detained - Review Authority remission",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewHWFRemission",
                    "name", "Detained - Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "remissionClaim", "legalAid",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewLARemission",
                    "name", "Review LA remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewLARemission",
                    "name", "Detained - Review LA remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "pendingPayment",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "remissionClaim", "section17",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAuthorityRemission",
                    "name", "Review Authority remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewAuthorityRemission",
                    "name", "Detained - Review Authority remission",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "journeyType", "aip",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "payAndSubmitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "markAppealPaid",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "markAppealPaid",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "moveToSubmitted",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "moveToSubmitted",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "payForAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "payForAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "recordRemissionDecision",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "recordRemissionDecision",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "updatePaymentStatus",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "updatePaymentStatus",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitTimeExtension",
                "null",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "decideOnTimeExtension",
                    "name", "Decide On Time Extension",
                    "processCategories", "timeExtension"
                )
            ),
            getArgumentOf(
                "uploadHomeOfficeBundle",
                "awaitingRespondentEvidence",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewRespondentEvidence",
                    "name", "Review Respondent Evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "respondentReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "submitHearingRequirements",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidence",
                "prepareForHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalEvidence",
                    "name", "Review additional evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalHomeOfficeEvidence",
                    "name", "Review additional Home Office evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalHomeOfficeEvidence",
                    "name", "Review additional Home Office evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalHomeOfficeEvidence",
                    "name", "Review additional Home Office evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "respondentReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalHomeOfficeEvidence",
                    "name", "Review additional Home Office evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "submitHearingRequirements",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalHomeOfficeEvidence",
                    "name", "Review additional Home Office evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAdditionalHomeOfficeEvidence",
                    "name", "Review additional Home Office evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAppealSkeletonArgument",
                    "name", "Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitCase",
                "caseUnderReview",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewAppealSkeletonArgument",
                    "name", "Detained - Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "buildCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAppealSkeletonArgument",
                    "name", "Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "buildCase",
                "caseUnderReview",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewAppealSkeletonArgument",
                    "name", "Detained - Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitReasonsForAppeal",
                "reasonsForAppealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewReasonsForAppeal",
                    "name", "Review Reasons For Appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitClarifyingQuestionAnswers",
                "clarifyingQuestionsAnswersSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewClarifyingQuestionsAnswers",
                    "name", "Review Clarifying Questions Answers",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitCmaRequirements",
                "cmaRequirementsSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewCmaRequirements",
                    "name", "Review Cma Requirements",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "listCma",
                "cmaListed",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "attendCma",
                    "name", "Attend Cma",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewRespondentResponse",
                    "name", "Review Respondent Response",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewRespondentResponse",
                    "name", "Detained - Review Respondent Response",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "caseSummaryHearingBundleStartDecision",
                    "name", "Create Hearing Bundle",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "postHearingAttendeesDurationAndRecording",
                    "name", "Post hearing – attendees, duration and recording",
                    "processCategories", "caseProgression",
                    "delayUntil",
                    Map.of(
                        "delayUntil", hearingDate, "delayUntilIntervalDays", "0"
                    )
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
                Map.of(
                    "taskId", "detainedCaseSummaryHearingBundleStartDecision",
                    "name", "Detained - Create Hearing Bundle",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedPostHearingAttendeesDurationAndRecording",
                    "name", "Detained - Post hearing – attendees, duration and recording",
                    "processCategories", "caseProgression",
                    "delayUntil",
                    Map.of(
                        "delayUntil", hearingDate, "delayUntilIntervalDays", "0"
                    )
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
                Map.of(
                    "taskId", "detainedCaseSummaryHearingBundleStartDecision",
                    "name", "Detained - Create Hearing Bundle",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedPostHearingAttendeesDurationAndRecording",
                    "name", "Detained - Post hearing – attendees, duration and recording",
                    "processCategories", "caseProgression",
                    "delayUntil",
                    Map.of(
                        "delayUntil", hearingDate, "delayUntilIntervalDays", "0"
                    )
                )
            ),
            getArgumentOf(
                "draftHearingRequirements",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewHearingRequirements",
                    "name", "Review hearing requirements",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "draftHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewHearingRequirements",
                    "name", "Detained - Review hearing requirements",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedFollowUpOverdueRespondentEvidence",
                    "name", "Detained - Follow-up overdue respondent evidence",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueRespondentEvidence",
                    "name", "Follow-up overdue respondent evidence",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpExtendedDirection",
                    "name", "Follow-up extended direction",
                    "processCategories", "caseProgression",
                    "delayUntil",
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
                Map.of(
                    "taskId", "detainedFollowUpExtendedDirection",
                    "name", "Detained - Follow-up extended direction",
                    "processCategories", "caseProgression",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueCaseBuilding",
                    "name", "Follow-up overdue case building",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "detainedFollowUpOverdueCaseBuilding",
                    "name", "Detained - Follow-up overdue case building",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueReasonsForAppeal",
                    "name", "Follow-up overdue reasons for appeal",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueClarifyingAnswers",
                    "name", "Follow-up overdue clarifying answers",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueCmaRequirements",
                    "name", "Follow-up overdue CMA requirements",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueRespondentReview",
                    "name", "Follow-up overdue respondent review",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "detainedFollowUpOverdueRespondentReview",
                    "name", "Detained - Follow-up overdue respondent review",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpOverdueHearingRequirements",
                    "name", "Follow-up overdue hearing requirements",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "followUpNonStandardDirection",
                    "name", "Follow-up non-standard direction",
                    "processCategories", "caseProgression",
                    "delayUntil",
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
                Map.of(
                    "taskId", "detainedFollowUpNonStandardDirection",
                    "name", "Detained - Follow-up non-standard direction",
                    "processCategories", "caseProgression",
                    "delayUntil",
                    delayUntilDirectionDue
                )
            ),
            getArgumentOf(
                "removeRepresentation",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "followUpNoticeOfChange",
                    "name", "Follow-up Notice of Change",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
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
                Map.of(
                    "taskId", "detainedFollowUpNoticeOfChange",
                    "name", "Detained - Follow-up Notice of Change",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
                    delayForDaysExcludingBankHolidays
                )
            ),
            getArgumentOf(
                "removeLegalRepresentative",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "followUpNoticeOfChange",
                    "name", "Follow-up Notice of Change",
                    "processCategories", "followUpOverdue",
                    "delayUntil",
                    delayForDaysExcludingBankHolidays
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "prepareForHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "editListing",
                    "name", "Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "prepareForHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedEditListing",
                    "name", "Detained - Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "finalBundling",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "editListing",
                    "name", "Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "prepareForHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedEditListing",
                    "name", "Detained - Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "preHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "editListing",
                    "name", "Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "preHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedEditListing",
                    "name", "Detained - Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "decision",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "editListing",
                    "name", "Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "changeHearingCentre",
                "decision",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedEditListing",
                    "name", "Detained - Edit listing",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "generateHearingBundle",
                "finalBundling",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "allocateHearingJudge",
                    "name", "Allocate Hearing Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "generateHearingBundle",
                "finalBundling",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedAllocateHearingJudge",
                    "name", "Detained - Allocate Hearing Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "customiseHearingBundle",
                "finalBundling",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "allocateHearingJudge",
                    "name", "Allocate Hearing Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "customiseHearingBundle",
                "finalBundling",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedAllocateHearingJudge",
                    "name", "Detained - Allocate Hearing Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "sendToPreHearing",
                "preHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "allocateHearingJudge",
                    "name", "Allocate Hearing Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "sendToPreHearing",
                "preHearing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedAllocateHearingJudge",
                    "name", "Detained - Allocate Hearing Judge",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewTheAppeal",
                    "name", "Detained - Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "reviewECRRemission",
                    "name", "Review ECR remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewTheAppeal",
                    "name", "Detained - Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedReviewECRRemission",
                    "name", "Detained - Review ECR remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "remissionType", "helpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewTheAppeal",
                    "name", "Detained - Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedReviewHWFRemission",
                    "name", "Detained - Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "exceptionalCircumstancesRemission",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewECRRemission",
                    "name", "Review ECR remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewECRRemission",
                    "name", "Detained - Review ECR remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "submitAppeal",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "remissionType", "helpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewHWFRemission",
                    "name", "Detained - Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "helpWithFees",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "helpWithFees",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewHWFRemission",
                    "name", "Detained - Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "asylumSupportFromHo",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewASRemission",
                    "name", "Review AS remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewASRemission",
                    "name", "Detained - Review AS remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "asylumSupport",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewASRemission",
                    "name", "Review AS remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "asylumSupport",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewASRemission",
                    "name", "Detained - Review AS remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "homeOfficeWaiver",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewHOWaiverRemission",
                    "name", "Review HO Waiver remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "homeOfficeWaiver",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewHOWaiverRemission",
                    "name", "Detained - Review HO Waiver remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "exceptionalCircumstancesRemission",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewECRRemission",
                    "name", "Review ECR remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "lateRemissionType", "exceptionalCircumstancesRemission",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewECRRemission",
                    "name", "Detained - Review ECR remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "journeyType", "aip",
                    "remissionOption", "feeWaiverFromHo",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewHOWaiverRemission",
                    "name", "Review HO Waiver remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewHOWaiverRemission",
                    "name", "Detained - Review HO Waiver remission",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewAuthorityRemission",
                    "name", "Review Authority remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewAuthorityRemission",
                    "name", "Detained - Review Authority remission",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewHWFRemission",
                    "name", "Detained - Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "legalAid",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewLARemission",
                    "name", "Review LA remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "legalAid",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewLARemission",
                    "name", "Detained - Review LA remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "section20",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAuthorityRemission",
                    "name", "Review Authority remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestFeeRemission",
                null,
                Map.of(
                    "remissionClaim", "section20",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewAuthorityRemission",
                    "name", "Detained - Review Authority remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "reviewHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "reviewHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedListTheCase",
                    "name", "Detained - List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "listCaseWithoutHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "false",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "listCaseWithoutHearingRequirements",
                "listing",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedListTheCase",
                    "name", "Detained - List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "recordRemissionDecision",
                null,
                Map.of(
                    "remissionDecision", "partiallyApproved",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "sendPaymentRequest",
                    "name", "Send Payment Request",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "markAsPaid",
                    "name", "Mark as Paid",
                    "delayUntil",
                    delayFor12Days,
                    "processCategories", "followUpOverdue"
                )
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
                Map.of(
                    "taskId", "reviewRemittedAppeal",
                    "name", "Review remitted appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "markAppealAsRemitted",
                "remitted",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewRemittedAppeal",
                    "name", "Detained - Review remitted appeal",
                    "processCategories", "caseProgression"
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
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAppealSetAsideUnderRule35",
                    "name", "Review appeal set aside under rule 35",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedReviewAppealSetAsideUnderRule35",
                    "name", "Detained - Review appeal set aside under rule 35",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewAppealSetAsideUnderRule35",
                    "name", "Review appeal set aside under rule 35",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedReviewAppealSetAsideUnderRule35",
                    "name", "Detained - Review appeal set aside under rule 35",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "updateTribunalDecision",
                null,
                Map.of(
                    "updateTribunalDecisionList", "underRule32",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAppealSetAsideUnderRule32",
                    "name", "Review appeal set aside under rule 32",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedReviewAppealSetAsideUnderRule32",
                    "name", "Detained - Review appeal set aside under rule 32",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "handleHearingException",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "hearingException",
                    "name", "Hearing Exception",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "handleHearingException",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedHearingException",
                    "name", "Detained - Hearing Exception",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "cmrReListing",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "cmrUpdated",
                    "name", "Update CMR notification",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "cmrReListing",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedCmrUpdated",
                    "name", "Detained - Update CMR notification",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedCmrListed",
                    "name", "Detained - Send CMR notification",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "restoreStateFromAdjourn",
                null,
                Map.of(
                    "isIntegrated", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "relistCase",
                    "name", "Relist The Case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "restoreStateFromAdjourn",
                null,
                Map.of(
                    "isIntegrated", true, "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedRelistCase",
                    "name", "Detained - Relist The Case",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedListTheCase",
                    "name", "Detained - List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "cmrListed",
                    "name", "Send CMR notification",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedCmrListed",
                    "name", "Detained - Send CMR notification",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "cmrListing",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "cmrListed",
                    "name", "Send CMR notification",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedListTheCase",
                    "name", "Detained - List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "triggerReviewInterpreterBookingTask",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewInterpreters",
                    "name", "Review interpreter booking",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "triggerReviewInterpreterBookingTask",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewInterpreters",
                    "name", "Detained - Review interpreter booking",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "hearingCancelled",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewInterpreters",
                    "name", "Review interpreter booking",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "hearingCancelled",
                null,
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewInterpreters",
                    "name", "Detained - Review interpreter booking",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "editCaseListing",
                null,
                Map.of(
                    "shouldTriggerReviewInterpreterTask", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewInterpreters",
                    "name", "Review interpreter booking",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedReviewInterpreters",
                    "name", "Detained - Review interpreter booking",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "processFeeRefund",
                    "name", "Process fee refund",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedProcessFeeRefund",
                    "name", "Detained - Process Fee Refund",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedProcessFeeRefund",
                    "name", "Detained - Process Fee Refund",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewMigratedCase",
                    "name", "Review migrated case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewMigratedCase",
                    "name", "Detained - Review migrated case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewMigratedCase",
                    "name", "Review migrated case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "ariaCreateCase",
                "migrated",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewMigratedCase",
                    "name", "Detained - Review migrated case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "startAppeal",
                "appealStartedByAdmin",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewDraftAppeal",
                    "name", "Review draft appeal",
                    "delayUntil",
                    delayForDays,
                    "processCategories", "followUpOverdue"
                )
            ),
            getArgumentOf(
                "startAppeal",
                "appealStartedByAdmin",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "DetainedReviewDraftAppeal",
                    "name", "Detained - Review Draft Appeal",
                    "delayUntil",
                    delayForDays,
                    "processCategories", "followUpOverdue"
                )
            ),
            getArgumentOf(
                "requestResponseReview",
                "respondentReview",
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "printAndSendHoResponse",
                    "name", "Print and send HO response",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "asyncStitchingComplete",
                "preHearing",
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "printAndSendHearingBundle",
                    "name", "Print and send hearing bundle",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "asyncStitchingComplete",
                "preHearing",
                Map.of(
                    "isAdmin", "true",
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedPrintAndSendHearingBundle",
                    "name", "Detained - Print and send hearing bundle",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "printAndSendDecisionCorrectedRule31",
                    "name", "Print and send decision corrected under rule 31",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedPrintAndSendDecisionCorrectedRule31",
                    "name", "Detained - Print and send decision corrected under rule 31",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "reviewAppealSetAsideUnderRule32",
                    "name", "Review appeal set aside under rule 32",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "printAndSendDecisionCorrectedRule32",
                    "name", "Print and send decision corrected under rule 32",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedReviewAppealSetAsideUnderRule32",
                    "name", "Detained - Review appeal set aside under rule 32",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedPrintAndSendDecisionCorrectedRule32",
                    "name", "Detained - Print and send decision corrected under rule 32",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "processApplicationToReviewDecision",
                    "name", "Process Application to Review Decision",
                    "processCategories", "application"
                ),
                Map.of(
                    "taskId", "printAndSendHoApplication",
                    "name", "Print and send HO application",
                    "processCategories", "caseProgression"
                )
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
                Map.of(
                    "taskId", "detainedProcessApplicationToReviewDecision",
                    "name", "Detained - Process Application to Review Decision",
                    "processCategories", "application"
                ),
                Map.of(
                    "taskId", "detainedPrintAndSendHoApplication",
                    "name", "Detained - Print and send HO application",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                Map.of(
                    "taskId", "printAndSendHoEvidence",
                    "name", "Print and send new HO evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAdditionalEvidenceHomeOffice",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedPrintAndSendHoEvidence",
                    "name", "Detained - Print and send new HO evidence",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "printAndSendHoEvidence",
                    "name", "Print and send new HO evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "uploadAddendumEvidenceHomeOffice",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedPrintAndSendHoEvidence",
                    "name", "Detained - Print and send new HO evidence",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                Map.of(
                    "taskId", "printAndSendFTPADecision",
                    "name", "Print and send FTPA decision",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "decideFtpaApplication",
                null,
                Map.of(
                    "isAdmin", "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedPrintAndSendFTPADecision",
                    "name", "Detained - Print and send FTPA decision",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "requestNewHearingRequirements",
                "submitHearingRequirements",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "isAdmin", "true"
                ),
                Map.of(
                    "taskId", "printAndSendReheardHearingRequirements",
                    "name", "Print and send reheard appeal hearing requirements form",
                    "processCategories", "caseProgression"
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
                Map.of(
                    "taskId", "detainedPrintAndSendReheardHearingRequirements",
                    "name", "Detained - Print and send reheard appeal hearing requirements form",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "protection"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "revocationOfProtection"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "deprivation"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "refusalOfHumanRights"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "refusalOfEu"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appealType", "euSettlementScheme"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "pendingPayment",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "helpWithFees"
                ),
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfHumanRights",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "exceptionalCircumstancesRemission"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "reviewECRRemission",
                    "name", "Review ECR remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "refusalOfEu",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "helpWithFees"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "reviewHWFRemission",
                    "name", "Review HWF remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "protection",
                    "isNotificationTurnedOff", "false",
                    "remissionClaim", "homeOfficeWaiver"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "reviewHOWaiverRemission",
                    "name", "Review HO Waiver remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "appealSubmitted",
                Map.of(
                    "appealType", "euSettlementScheme",
                    "isNotificationTurnedOff", "false",
                    "remissionType", "exceptionalCircumstancesRemission"
                ),
                Map.of(
                    "taskId", "reviewTheAppeal",
                    "name", "Review the appeal",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "reviewECRRemission",
                    "name", "Review ECR remission",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewAppealSkeletonArgument",
                    "name", "Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedReviewAppealSkeletonArgument",
                    "name", "Detained - Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedReviewAppealSkeletonArgument",
                    "name", "Detained - Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "caseUnderReview",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedReviewAppealSkeletonArgument",
                    "name", "Detained - Review Appeal Skeleton Argument",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "reasonsForAppealSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewReasonsForAppeal",
                    "name", "Review Reasons For Appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "prepareForHearing",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "caseSummaryHearingBundleStartDecision",
                    "name", "Create Hearing Bundle",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "remitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "reviewRemittedAppeal",
                    "name", "Review remitted appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "remitted",
                Map.of(
                    "appellantInDetention", "true",
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "detainedReviewRemittedAppeal",
                    "name", "Detained - Review remitted appeal",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "ftpaSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false"
                ),
                Map.of(
                    "taskId", "assignAFTPAJudge",
                    "name", "Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "decideAnFTPA",
                    "name", "Decide an FTPA",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "ftpaSubmitted",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedAssignAFTPAJudge",
                    "name", "Detained - Assign a FTPA Judge",
                    "processCategories", "caseProgression"
                ),
                Map.of(
                    "taskId", "detainedDecideAnFTPA",
                    "name", "Detained - Decide an FTPA",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "reviewedHearingRequirements", "false"
                ),
                Map.of(
                    "taskId", "reviewHearingRequirements",
                    "name", "Review hearing requirements",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "reviewedHearingRequirements",
                    "false",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"

                ),
                Map.of(
                    "taskId", "detainedReviewHearingRequirements",
                    "name", "Detained - Review hearing requirements",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "reviewedHearingRequirements", "true"
                ),
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "progressMigratedCase",
                "listing",
                Map.of(
                    "reviewedHearingRequirements",
                    "true",
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"

                ),
                Map.of(
                    "taskId", "detainedListTheCase",
                    "name", "Detained - List the case",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "generateListCmrTask",
                null,
                Map.of(
                    "isNotificationTurnedOff", "false",
                    "appellantInDetention", "true"
                ),
                Map.of(
                    "taskId", "detainedListCmr",
                    "name", "Detained - List CMR",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "completeCaseReview",
                "appealSubmitted",
                Map.of(
                    "stf24wCurrentStatusAutoGenerated", true
                ),
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "listTheCaseTask"
                )
            ),
            getArgumentOf(
                "completeCaseReview",
                "listing",
                Map.of(
                    "stf24wCurrentStatusAutoGenerated", true
                ),
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "listTheCaseTask"
                )
            ),
            getArgumentOf(
                "sendToAdminForListing",
                "prepareForHearing",
                Map.of(
                    "stf24wCurrentStatusAutoGenerated", true
                ),
                Map.of(
                    "taskId", "listTheCase",
                    "name", "List the case",
                    "processCategories", "listTheCaseTask"
                )
            ),
            getArgumentOf(
                "addStatutoryTimeframe24Weeks",
                null,
                null,
                Map.of(
                    "taskId", "reviewAddStf24wFlag",
                    "name", "Review appeal - 24 week STF added",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf(
                "removeStatutoryTimeframe24Weeks",
                null,
                null,
                Map.of(
                    "taskId", "reviewRemoveStf24wFlag",
                    "name", "Review appeal - 24 week STF removed",
                    "processCategories", "caseProgression"
                )
            ),
            getArgumentOf("unknownEvent", null, null, null)
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
        assertEquals(205, logic.getRules().size());
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
