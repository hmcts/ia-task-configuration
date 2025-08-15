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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_INITIATION_IA_ASYLUM;

class CamundaTaskInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_INITIATION_IA_ASYLUM;
    }

    private static String hearingDate = LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    static Stream<Arguments> scenarioProvider() {
        LocalDateTime directionDueDate = LocalDateTime.now().plusDays(5);
        Map<String, LocalDateTime> variablesDirectionDueDate = Map.of(
            "directionDueDate", directionDueDate
        );
        Map<String, Object> delayUntilDirectionDue = Map.of(
            "delayUntilIntervalDays", "0",
            "delayUntil", directionDueDate
        );
        Map<String,Object> delayForDaysExcludingBankHolidays = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
            "delayUntilOrigin", LocalDate.now(),
            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
        );

        Map<String,Object> delayFor12Days = Map.of(
            "delayUntilIntervalDays", "12",
            "delayUntilOrigin", LocalDate.now()
        );

        Map<String,Object> delayForDays = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilOrigin", LocalDate.now()
        );
        Map<String, Object> appellantInDetention = mapAdditionalData("{\n"
                                                                 + "   \"Data\":{\n"
                                                                 + "      \"appellantInDetention\": true\n"
                                                                 + "   }\n"
                                                                 + "}");

        return Stream.of(
            Arguments.of(
                "applyForFTPAAppellant",
                null,
                null,
                List.of(
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
                )
            ),
                Arguments.of(
                        "applyForFTPAAppellant",
                        null,
                        appellantInDetention,
                        List.of(
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
                        )
                ),
            Arguments.of(
                "applyForFTPAAppellant",
                null,
                appellantInDetention,
                List.of(
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
                )
            ),
            Arguments.of(
                "applyForFTPARespondent",
                null,
                null,
                List.of(
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
                )
            ),
                Arguments.of(
                        "applyForFTPARespondent",
                        null,
                        appellantInDetention,
                        List.of(
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
                        )
                ),
            Arguments.of(
                "applyForFTPARespondent",
                null,
                appellantInDetention,
                List.of(
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
                )
            ),
            Arguments.of(
                "generateDecisionAndReasons",
                "decision",
                null,
                singletonList(
                    Map.of(
                        "taskId", "sendDecisionsAndReasons",
                        "name", "Send decisions and reasons",


                        "processCategories", "caseProgression"
                    )
                )
            ),

            Arguments.of(
                "generateDecisionAndReasons",
                "decision",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedSendDecisionsAndReasons",
                        "name", "Detained - Send decisions and reasons",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "revocationOfProtection" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",



                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "deprivation" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionOption\":\"" + "asylumSupportFromHo" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionOption\":\"" + "feeWaiverFromHo" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionOption\":\"" + "under18GetSupportFromLocalAuthority" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionOption\":\"" + "iWantToGetHelpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionOption\":\"" + "parentGetSupportFromLocalAuthority" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "paymentAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "paymentAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "paymentAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "payAndSubmitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "payAndSubmitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "payAndSubmitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "payAndSubmitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "markAppealPaid",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "markAppealPaid",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "moveToSubmitted",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "moveToSubmitted",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "payForAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "payForAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "recordRemissionDecision",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "recordRemissionDecision",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "updatePaymentStatus",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "updatePaymentStatus",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitTimeExtension",
                "null",
                null,
                singletonList(
                    Map.of(
                        "taskId", "decideOnTimeExtension",
                        "name", "Decide On Time Extension",


                        "processCategories", "timeExtension"
                    )
                )
            ),
            Arguments.of(
                "uploadHomeOfficeBundle",
                "awaitingRespondentEvidence",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewRespondentEvidence",
                        "name", "Review Respondent Evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "caseUnderReview",
                null,
                List.of(
                    Map.of(
                        "taskId", "reviewAdditionalEvidence",
                        "name", "Review additional evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "respondentReview",
                null,
                List.of(
                    Map.of(
                        "taskId", "reviewAdditionalEvidence",
                        "name", "Review additional evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "submitHearingRequirements",
                null,
                List.of(
                    Map.of(
                        "taskId", "reviewAdditionalEvidence",
                        "name", "Review additional evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "listing",
                null,
                List.of(
                    Map.of(
                        "taskId", "reviewAdditionalEvidence",
                        "name", "Review additional evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "prepareForHearing",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalEvidence",
                        "name", "Review additional evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalHomeOfficeEvidence",
                        "name", "Review additional Home Office evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalHomeOfficeEvidence",
                        "name", "Review additional Home Office evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "caseUnderReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalHomeOfficeEvidence",
                        "name", "Review additional Home Office evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "respondentReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalHomeOfficeEvidence",
                        "name", "Review additional Home Office evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "submitHearingRequirements",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalHomeOfficeEvidence",
                        "name", "Review additional Home Office evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "listing",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalHomeOfficeEvidence",
                        "name", "Review additional Home Office evidence",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitCase",
                "caseUnderReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAppealSkeletonArgument",
                        "name", "Review Appeal Skeleton Argument",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitCase",
                "caseUnderReview",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSkeletonArgument",
                        "name", "Detained - Review Appeal Skeleton Argument",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "buildCase",
                "caseUnderReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAppealSkeletonArgument",
                        "name", "Review Appeal Skeleton Argument",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "buildCase",
                "caseUnderReview",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSkeletonArgument",
                        "name", "Detained - Review Appeal Skeleton Argument",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitReasonsForAppeal",
                "reasonsForAppealSubmitted",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewReasonsForAppeal",
                        "name", "Review Reasons For Appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitClarifyingQuestionAnswers",
                "clarifyingQuestionsAnswersSubmitted",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewClarifyingQuestionsAnswers",
                        "name", "Review Clarifying Questions Answers",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitCmaRequirements",
                "cmaRequirementsSubmitted",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewCmaRequirements",
                        "name", "Review Cma Requirements",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCma",
                "cmaListed",
                null,
                singletonList(
                    Map.of(
                        "taskId", "attendCma",
                        "name", "Attend Cma",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewRespondentResponse",
                        "name", "Review Respondent Response",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCase",
                "prepareForHearing",
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "         \"listCaseHearingDate\" : \"" + hearingDate + "\","
                                      + "         \"appellantInDetention\": false\n"
                                      + "        }\n"
                                      + "      }"),
                List.of(
                    Map.of(
                        "taskId", "caseSummaryHearingBundleStartDecision",
                        "name", "Create Hearing Bundle",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "postHearingAttendeesDurationAndRecording",
                        "name", "Post hearing – attendees, duration and recording",
                        "processCategories", "caseProgression",
                        "delayUntil", Map.of(
                            "delayUntil", hearingDate,
                            "delayUntilIntervalDays","0"
                        )
                    )
                )
            ),
            Arguments.of(
                "listCase",
                "prepareForHearing",
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "         \"listCaseHearingDate\" : \"" + hearingDate + "\","
                                      + "         \"appellantInDetention\": true\n"
                                      + "        }\n"
                                      + "      }"),
                List.of(
                    Map.of(
                        "taskId", "detainedCaseSummaryHearingBundleStartDecision",
                        "name", "Detained - Create Hearing Bundle",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "detainedPostHearingAttendeesDurationAndRecording",
                        "name", "Detained - Post hearing – attendees, duration and recording",
                        "processCategories", "caseProgression",
                        "delayUntil", Map.of(
                            "delayUntil", hearingDate,
                            "delayUntilIntervalDays","0"
                        )
                    )
                )
            ),
            Arguments.of(
                "listCase",
                "prepareForHearing",
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "         \"listCaseHearingDate\" : \"" + hearingDate + "\","
                                      + "         \"appellantInDetention\": true\n"
                                      + "        }\n"
                                      + "      }"),
                List.of(
                    Map.of(
                        "taskId", "detainedCaseSummaryHearingBundleStartDecision",
                        "name", "Detained - Create Hearing Bundle",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "detainedPostHearingAttendeesDurationAndRecording",
                        "name", "Detained - Post hearing – attendees, duration and recording",
                        "processCategories", "caseProgression",
                        "delayUntil", Map.of(
                            "delayUntil", hearingDate,
                            "delayUntilIntervalDays","0"
                        )
                    )
                )
            ),
            Arguments.of(
                "draftHearingRequirements",
                "listing",
                null,
                List.of(
                    Map.of(
                        "taskId", "reviewHearingRequirements",
                        "name", "Review hearing requirements",


                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "draftHearingRequirements",
                        "listing",
                        appellantInDetention,
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewHearingRequirements",
                                        "name", "Detained - Review hearing requirements",


                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                merge(variablesDirectionDueDate, appellantInDetention),
                singletonList(
                    Map.of(
                        "taskId", "detainedFollowUpOverdueRespondentEvidence",
                        "name", "Detained - Follow-up overdue respondent evidence",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueRespondentEvidence",
                        "name", "Follow-up overdue respondent evidence",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                null,
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpExtendedDirection",
                        "name", "Follow-up extended direction",
                        "processCategories", "caseProgression",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                null,
                merge(variablesDirectionDueDate,appellantInDetention),
                singletonList(
                    Map.of(
                        "taskId", "detainedFollowUpExtendedDirection",
                        "name", "Detained - Follow-up extended direction",
                        "processCategories", "caseProgression",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueCaseBuilding",
                        "name", "Follow-up overdue case building",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                merge(variablesDirectionDueDate, appellantInDetention),
                singletonList(
                    Map.of(
                        "taskId", "detainedFollowUpOverdueCaseBuilding",
                        "name", "Detained - Follow-up overdue case building",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestReasonsForAppeal",
                "awaitingReasonsForAppeal",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueReasonsForAppeal",
                        "name", "Follow-up overdue reasons for appeal",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "sendDirectionWithQuestions",
                "awaitingClarifyingQuestionsAnswers",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueClarifyingAnswers",
                        "name", "Follow-up overdue clarifying answers",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestCmaRequirements",
                "awaitingCmaRequirements",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueCmaRequirements",
                        "name", "Follow-up overdue CMA requirements",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                "respondentReview",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueRespondentReview",
                        "name", "Follow-up overdue respondent review",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                "respondentReview",
                merge(variablesDirectionDueDate, appellantInDetention),
                singletonList(
                    Map.of(
                        "taskId", "detainedFollowUpOverdueRespondentReview",
                        "name", "Detained - Follow-up overdue respondent review",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestHearingRequirementsFeature",
                "submitHearingRequirements",
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueHearingRequirements",
                        "name", "Follow-up overdue hearing requirements",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                null,
                variablesDirectionDueDate,
                singletonList(
                    Map.of(
                        "taskId", "followUpNonStandardDirection",
                        "name", "Follow-up non-standard direction",
                        "processCategories", "caseProgression",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                null,
                merge(variablesDirectionDueDate, appellantInDetention),
                singletonList(
                    Map.of(
                        "taskId", "detainedFollowUpNonStandardDirection",
                        "name", "Detained - Follow-up non-standard direction",
                        "processCategories", "caseProgression",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "removeRepresentation",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpNoticeOfChange",
                        "name", "Follow-up Notice of Change",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayForDaysExcludingBankHolidays
                    )
                )
            ),
            Arguments.of(
                "removeRepresentation",
                null,
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedFollowUpNoticeOfChange",
                        "name", "Detained - Follow-up Notice of Change",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayForDaysExcludingBankHolidays
                    )
                )
            ),
            Arguments.of(
                "removeLegalRepresentative",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpNoticeOfChange",
                        "name", "Follow-up Notice of Change",


                        "processCategories", "followUpOverdue",
                        "delayUntil", delayForDaysExcludingBankHolidays
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "prepareForHearing",
                null,
                singletonList(
                    Map.of(
                        "taskId", "editListing",
                        "name", "Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "prepareForHearing",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedEditListing",
                        "name", "Detained - Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "finalBundling",
                null,
                singletonList(
                    Map.of(
                        "taskId", "editListing",
                        "name", "Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "prepareForHearing",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedEditListing",
                        "name", "Detained - Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "preHearing",
                null,
                singletonList(
                    Map.of(
                        "taskId", "editListing",
                        "name", "Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "preHearing",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedEditListing",
                        "name", "Detained - Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "decision",
                null,
                List.of(
                    Map.of(
                        "taskId", "editListing",
                        "name", "Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "decision",
                appellantInDetention,
                List.of(
                    Map.of(
                        "taskId", "detainedEditListing",
                        "name", "Detained - Edit listing",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "generateHearingBundle",
                "finalBundling",
                null,
                List.of(
                    Map.of(
                        "taskId", "allocateHearingJudge",
                        "name", "Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "generateHearingBundle",
                "finalBundling",
                appellantInDetention,
                List.of(
                    Map.of(
                        "taskId", "detainedAllocateHearingJudge",
                        "name", "Detained - Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "customiseHearingBundle",
                "finalBundling",
                null,
                List.of(
                    Map.of(
                        "taskId", "allocateHearingJudge",
                        "name", "Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "customiseHearingBundle",
                "finalBundling",
                appellantInDetention,
                List.of(
                    Map.of(
                        "taskId", "detainedAllocateHearingJudge",
                        "name", "Detained - Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "sendToPreHearing",
                "preHearing",
                null,
                List.of(
                    Map.of(
                        "taskId", "allocateHearingJudge",
                        "name", "Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "sendToPreHearing",
                "preHearing",
                appellantInDetention,
                List.of(
                    Map.of(
                        "taskId", "detainedAllocateHearingJudge",
                        "name", "Detained - Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                    "submitAppeal",
                    "pendingPayment",
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                            + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                            + "      \"appellantInDetention\":\"" + true + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewRemissionApplication",
                                    "name", "Detained - Review Remission Application",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                    "submitAppeal",
                    "pendingPayment",
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                            + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                            + "      \"appellantInDetention\":\"" + true + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewRemissionApplication",
                                    "name", "Detained - Review Remission Application",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                    "submitAppeal",
                    "pendingPayment",
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                            + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                            + "      \"appellantInDetention\":\"" + true + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewRemissionApplication",
                                    "name", "Detained - Review Remission Application",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "protection" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "protection" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "protection" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",


                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",


                                        "processCategories", "caseProgression"
                                )
                        )
                ),
                Arguments.of(
                        "submitAppeal",
                        "pendingPayment",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "reviewRemissionApplication",
                                        "name", "Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"

                        + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewRemissionApplication",
                        "name", "Detained - Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "protection" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewTheAppeal",
                                        "name", "Detained - Review the appeal",

                                        "processCategories", "caseProgression"
                                ),
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "protection" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewTheAppeal",
                                        "name", "Detained - Review the appeal",

                                        "processCategories", "caseProgression"
                                ),
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "protection" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewTheAppeal",
                                        "name", "Detained - Review the appeal",

                                        "processCategories", "caseProgression"
                                ),
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"remissionType\":\"" + "hoWaiverRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"

                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "submitAppeal",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                + "      \"appellantInDetention\":\"" + true + "\"\n"
                                + "   }"
                                + "}"),
                        List.of(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "requestFeeRemission",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
                Arguments.of(
                        "requestFeeRemission",
                        null,
                        appellantInDetention,
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewRemissionApplication",
                                        "name", "Detained - Review Remission Application",

                                        "processCategories", "caseProgression"
                                )
                        )
                ),
            Arguments.of(
                "requestFeeRemission",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"autoHearingRequestEnabled\" : " + false + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + false + "\n"
                                      + "          \"autoHearingRequestEnabled\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + true + "\n"
                                      + "          \"isPanelRequired\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + true + "\n"
                                      + "          \"isPanelRequired\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCaseWithoutHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + true + "\n"
                                      + "          \"isPanelRequired\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCaseWithoutHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"autoHearingRequestEnabled\" : " + false + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCaseWithoutHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + false + "\n"
                                      + "          \"autoHearingRequestEnabled\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "recordRemissionDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"remissionDecision\":\"" + "partiallyApproved" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "sendPaymentRequest",
                        "name", "Send Payment Request",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "markAsPaid",
                        "name", "Mark as Paid",
                        "delayUntil", delayFor12Days,

                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "recordRemissionDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"remissionDecision\":\"" + "partiallyApproved" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\"\n"
                                      + "   }"
                                      + "}"),
                emptyList()
            ),
            Arguments.of(
                "markAppealAsRemitted",
                "remitted",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewRemittedAppeal",
                        "name", "Review remitted appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"ftpaAppellantRjDecisionOutcomeType\":\"" + "reheardRule35" + "\",\n"
                                      + "      \"ftpaApplicantType\":\"" + "appellant" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewAppealSetAsideUnderRule35",
                        "name", "Review appeal set aside under rule 35",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"ftpaAppellantRjDecisionOutcomeType\":\"" + "reheardRule35" + "\",\n"
                                      + "      \"ftpaApplicantType\":\"" + "appellant" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"

                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSetAsideUnderRule35",
                        "name", "Detained - Review appeal set aside under rule 35",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"ftpaRespondentRjDecisionOutcomeType\":\"" + "reheardRule35" + "\",\n"
                                      + "      \"ftpaApplicantType\":\"" + "respondent" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewAppealSetAsideUnderRule35",
                        "name", "Review appeal set aside under rule 35",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"ftpaRespondentRjDecisionOutcomeType\":\"" + "reheardRule35" + "\",\n"
                                      + "      \"ftpaApplicantType\":\"" + "respondent" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSetAsideUnderRule35",
                        "name", "Detained - Review appeal set aside under rule 35",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "updateTribunalDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule32" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewAppealSetAsideUnderRule32",
                        "name", "Review appeal set aside under rule 32",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "handleHearingException",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "hearingException",
                        "name", "Hearing Exception",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "handleHearingException",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\" : \"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedHearingException",
                        "name", "Detained - Hearing Exception",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "cmrReListing",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "cmrUpdated",
                        "name", "Update CMR notification",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "cmrReListing",
                null,
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedCmrUpdated",
                        "name", "Detained - Update CMR notification",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "cmrListing",

                null,
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedCmrListed",
                        "name", "Detained - Send CMR notification",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "restoreStateFromAdjourn",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "relistCase",
                        "name", "Relist The Case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "recordAdjournmentDetails",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"hearingAdjournmentWhen\":\"" + "onHearingDate" + "\",\n"
                                      + "      \"relistCaseImmediately\":" + "\"Yes\"" + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",



                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "recordAdjournmentDetails",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"hearingAdjournmentWhen\":\"" + "onHearingDate" + "\",\n"
                                      + "      \"relistCaseImmediately\":" + "\"Yes\"" + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + ",\n"
                                      + "      \"appellantInDetention\":" + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedListTheCase",
                        "name", "Detained - List the case",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "cmrListing",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "cmrListed",
                        "name", "Send CMR notification",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "cmrListing",
                null,
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedCmrListed",
                        "name", "Detained - Send CMR notification",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "cmrListing",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "cmrListed",
                        "name", "Send CMR notification",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                "decision",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"listCaseHearingCentre\":\"" + "decisionWithoutHearing" + "\",\n"
                                      + "          \"isIntegrated\" : " + true + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",



                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                "decision",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"listCaseHearingCentre\":\"" + "decisionWithoutHearing" + "\",\n"
                                      + "          \"isIntegrated\" : " + true + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + ",\n"
                                      + "      \"appellantInDetention\" : " + true + "\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedListTheCase",
                        "name", "Detained - List the case",



                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "triggerReviewInterpreterBookingTask",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewInterpreters",
                        "name", "Review interpreter booking",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "triggerReviewInterpreterBookingTask",
                null,
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewInterpreters",
                        "name", "Detained - Review interpreter booking",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "hearingCancelled",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewInterpreters",
                        "name", "Review interpreter booking",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "hearingCancelled",
                null,
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewInterpreters",
                        "name", "Detained - Review interpreter booking",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "editCaseListing",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"shouldTriggerReviewInterpreterTask\" : \"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewInterpreters",
                        "name", "Review interpreter booking",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "editCaseListing",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"shouldTriggerReviewInterpreterTask\" : \"" + true + "\",\n"
                                      + "      \"appellantInDetention\" : \"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewInterpreters",
                        "name", "Detained - Review interpreter booking",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "manageFeeUpdate",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"feeUpdateTribunalAction\":\"" + "refund" + "\",\n"
                                      + "      \"appellantInDetention\" : \"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "processFeeRefund",
                        "name", "Process fee refund",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "manageFeeUpdate",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"feeUpdateTribunalAction\":\"" + "refund" + "\",\n"
                                      + "      \"appellantInDetention\" : \"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedProcessFeeRefund",
                        "name", "Detained - Process Fee Refund",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "manageFeeUpdate",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"feeUpdateTribunalAction\":\"" + "refund" + "\",\n"
                                      + "      \"appellantInDetention\" : \"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedProcessFeeRefund",
                        "name", "Detained - Process Fee Refund",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "ariaCreateCase",
                "migrated",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewMigratedCase",
                        "name", "Review migrated case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "ariaCreateCase",
                "migrated",
                null,
                List.of(
                    Map.of(
                        "taskId", "reviewMigratedCase",
                        "name", "Review migrated case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "startAppeal",
                "appealStartedByAdmin",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewDraftAppeal",
                        "name", "Review draft appeal",
                        "delayUntil", delayForDays,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "startAppeal",
                "appealStartedByAdmin",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "DetainedReviewDraftAppeal",
                        "name", "Detained - Review Draft Appeal",
                        "delayUntil", delayForDays,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestResponseReview",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendHoResponse",
                        "name", "Print and send HO response",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "asyncStitchingComplete",
                "preHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendHearingBundle",
                        "name", "Print and send hearing bundle",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "asyncStitchingComplete",
                "preHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendHearingBundle",
                        "name", "Detained - Print and send hearing bundle",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "updateTribunalDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"isDecisionRule31Changed\":\"" + true + "\",\n"
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule31" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendDecisionCorrectedRule31",
                        "name", "Print and send decision corrected under rule 31",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "updateTribunalDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"isDecisionRule31Changed\":\"" + true + "\",\n"
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule31" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendDecisionCorrectedRule31",
                        "name", "Detained - Print and send decision corrected under rule 31",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "updateTribunalDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule32" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                "updateTribunalDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule32" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + false + "\",\n"
                                      + "      \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Judge's review of application decision\",\n"
                                      + "            \"decision\" : \"\",\n"
                                      + "            \"applicant\":\"" + "Respondent" + "\"\n"
                                      + "          }\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\",\n"
                                      + "      \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Judge's review of application decision\",\n"
                                      + "            \"decision\" : \"\",\n"
                                      + "            \"applicant\":\"" + "Respondent" + "\"\n"
                                      + "          }\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "detainedProcessApplicationToReviewDecision",
                        "name", "Detained Process Application to Review Decision",

                        "processCategories", "application"
                    ),
                    Map.of(
                        "taskId", "detainedPrintAndSendHoApplication",
                        "name", "Detained - Print and send HO application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendHoEvidence",
                        "name", "Print and send new HO evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendHoEvidence",
                        "name", "Detained - Print and send new HO evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceHomeOffice",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendHoEvidence",
                        "name", "Print and send new HO evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAddendumEvidenceHomeOffice",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendHoEvidence",
                        "name", "Detained - Print and send new HO evidence",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "sendDecisionAndReasons",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendAppealDecision",
                        "name", "Print and send appeal decision and FTPA form",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "sendDecisionAndReasons",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendAppealDecision",
                        "name", "Detained - Print and send appeal decision and FTPA form",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendFTPADecision",
                        "name", "Print and send FTPA decision",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendFTPADecision",
                        "name", "Detained - Print and send FTPA decision",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "requestNewHearingRequirements",
                "submitHearingRequirements",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "printAndSendReheardHearingRequirements",
                        "name", "Print and send reheard appeal hearing requirements form",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "requestNewHearingRequirements",
                "submitHearingRequirements",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedPrintAndSendReheardHearingRequirements",
                        "name", "Detained - Print and send reheard appeal hearing requirements form",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "revocationOfProtection" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",



                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "deprivation" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "pendingPayment",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewTheAppeal",
                        "name", "Review the appeal",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewRemissionApplication",
                        "name", "Review Remission Application",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "caseUnderReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewAppealSkeletonArgument",
                        "name", "Review Appeal Skeleton Argument",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "caseUnderReview",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSkeletonArgument",
                        "name", "Detained - Review Appeal Skeleton Argument",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "caseUnderReview",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSkeletonArgument",
                        "name", "Detained - Review Appeal Skeleton Argument",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "caseUnderReview",
                appellantInDetention,
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSkeletonArgument",
                        "name", "Detained - Review Appeal Skeleton Argument",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "reasonsForAppealSubmitted",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewReasonsForAppeal",
                        "name", "Review Reasons For Appeal",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "prepareForHearing",
                null,
                List.of(
                    Map.of(
                        "taskId", "caseSummaryHearingBundleStartDecision",
                        "name", "Create Hearing Bundle",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "remitted",
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewRemittedAppeal",
                        "name", "Review remitted appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "ftpaSubmitted",
                null,
                List.of(
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
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "ftpaSubmitted",
                appellantInDetention,
                List.of(
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
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"reviewedHearingRequirements\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewHearingRequirements",
                        "name", "Review hearing requirements",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"reviewedHearingRequirements\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\": true\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "detainedReviewHearingRequirements",
                        "name", "Detained - Review hearing requirements",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"reviewedHearingRequirements\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"reviewedHearingRequirements\":\"" + true + "\",\n"
                                      + "      \"appellantInDetention\": true\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "detainedListTheCase",
                        "name", "Detained - List the case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "generateListCmrTask",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "detainedListCmr",
                        "name", "Detained - List CMR",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "unknownEvent",
                null,
                null,
                emptyList()
            )
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
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putAll(map);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
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
        Map<String, LocalDateTime> variablesDirectionDueDate = Map.of(
            "directionDueDate", directionDueDate
        );
        Map<String, Object> delayUntilDirectionDue = Map.of(
            "delayUntilIntervalDays", "0",
            "delayUntil", directionDueDate
        );

        return Stream.of(
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                "requestHearingRequirementsFeature",
                "submitHearingRequirements",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"uploadHomeOfficeBundleAvailable\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "followUpOverdueRespondentEvidence",
                        "name", "Follow-up overdue respondent evidence",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"uploadHomeOfficeBundleAvailable\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewRespondentEvidence",
                        "name", "Review Respondent Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            )
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

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }


    public static Stream<Arguments> makeAnApplicationScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"\",\n"
                                      + "            \"decision\" : \"\",\n"
                                      + "            \"applicant\" : \"\"\n"
                                      + "            \"appellantInDetention\" : \"\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      }"),
                emptyList()
            ),
            getArgumentOf("Adjourn",
                          "processApplicationAdjourn",
                          "Process Adjourn Application",
                          false),

            getArgumentOf("Adjourn",
                          "detainedProcessApplicationAdjourn",
                          "Detained Process Adjourn Application",
                          true),
            getArgumentOf("Expedite",
                          "processApplicationExpedite",
                          "Process Expedite Application",
                          false),
            getArgumentOf("Expedite",
                          "detainedProcessApplicationExpedite",
                          "Detained Process Expedite Application",
                          true),
            getArgumentOf("Time extension",
                          "processApplicationTimeExtension",
                          "Process Time Extension Application",
                          false),
            getArgumentOf("Time extension",
                          "detainedProcessApplicationTimeExtension",
                          "Detained Process Time Extension Application",
                          true),
            getArgumentOf("Transfer",
                          "processApplicationTransfer",
                          "Process Transfer Application",
                          false),
            getArgumentOf("Transfer",
                          "detainedProcessApplicationTransfer",
                          "Detained Process Transfer Application",
                          true),
            getArgumentOf("Withdraw",
                          "processApplicationWithdraw",
                          "Process Withdraw Application",
                          false),
            getArgumentOf("Withdraw",
                          "detainedProcessApplicationWithdraw",
                          "Detained Process Withdraw Application",
                          true),
            getArgumentOf("Update hearing requirements",
                          "processApplicationUpdateHearingRequirements",
                          "Process Update Hearing Requirements Application",
                          false),
            getArgumentOf("Update hearing requirements",
                          "detainedProcessApplicationUpdateHearingRequirements",
                          "Detained Process Update Hearing Requirements Application",
                          true),
            getArgumentOf("Update appeal details",
                          "processApplicationUpdateAppealDetails",
                          "Process Update Appeal Details Application",
                          false),
            getArgumentOf("Update appeal details",
                          "detainedProcessApplicationUpdateAppealDetails",
                          "Detained Process Update Appeal Details Application",
                          true),
            getArgumentOf("Reinstate an ended appeal",
                          "processApplicationReinstateAnEndedAppeal",
                          "Process Reinstate An Ended Appeal Application",
                          false),
            getArgumentOf("Reinstate an ended appeal",
                          "detainedProcessApplicationReinstateAnEndedAppeal",
                          "Detained Process Reinstate An Ended Appeal Application",
                          true),
            getArgumentOf("Other",
                          "processApplicationOther",
                          "Process Other Application",
                          false),
            getArgumentOf("Other",
                          "detainedProcessApplicationOther",
                          "Detained Process Other Application",
                          true),
            getArgumentOf("Link/unlink appeals",
                          "processApplicationLink/UnlinkAppeals",
                          "Process Link/Unlink Appeals Application",
                          false),
            getArgumentOf("Link/unlink appeals",
                          "detainedProcessApplicationLink/UnlinkAppeals",
                          "Detained Process Link/Unlink Appeals Application",
                          true),
            getArgumentOf("Set aside a decision",
                          "reviewSetAsideDecisionApplication",
                          "Review set aside decision application",
                          false),
            getArgumentOf("Set aside a decision",
                          "detainedReviewSetAsideDecisionApplication",
                          "Detained Review set aside decision application",
                          true),
            getArgumentOf("Judge's review of application decision",
                          "processApplicationToReviewDecision",
                          "Process Application to Review Decision",
                          false),
            getArgumentOf("Judge's review of application decision",
                          "detainedProcessApplicationToReviewDecision",
                          "Detained Process Application to Review Decision",
                          true),
            getArgumentOf("Change hearing type",
                          "processApplicationChangeHearingType",
                          "Process Change Hearing Type Application",
                          false),
            getArgumentOf("Change hearing type",
                          "detainedProcessApplicationChangeHearingType",
                          "Detained Process Change Hearing Type Application",
                          true)
        );
    }

    private static Arguments getArgumentOf(String applicationType,
                                           String taskId,
                                           String taskName,
                                           boolean appellantInDetention) {
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
                                  + "          \"appellantInDetention\" : \"" + appellantInDetention + "\"\n"
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

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    public static Stream<Arguments> decideAnApplicationScenarioProvider() {
        Map<String,Object> delayFor5Days = Map.of(
            "delayUntilIntervalDays", "5",
            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
            "delayUntilOrigin", LocalDate.now(),
            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
        );

        return Stream.of(
            getDecideAnApplicationArgumentsOf("Adjourn", "editListing",
                                              "Edit Listing", "application", null, false),
            getDecideAnApplicationArgumentsOf("Expedite", "editListing",
                                              "Edit Listing", "application", null, false),
            getDecideAnApplicationArgumentsOf("Transfer", "editListing",
                                              "Edit Listing", "application", null, false),
            getDecideAnApplicationArgumentsOf("Adjourn", "editListing",
                                              "Edit Listing", "application", null, true),
            getDecideAnApplicationArgumentsOf("Expedite", "editListing",
                                              "Edit Listing", "application", null, true),
            getDecideAnApplicationArgumentsOf("Transfer", "editListing",
                                              "Edit Listing", "application", null, true),
            getDecideAnApplicationArgumentsOf("Set aside a decision",
                                              "followUpSetAsideDecision",
                                              "Follow up set aside decision",
                                              "followUpOverdue",
                                              delayFor5Days, false)
        );
    }

    private static Arguments getDecideAnApplicationArgumentsOf(String applicationType,
                                                               String taskId,
                                                               String name,
                                                               String processCategories,
                                                               Map<String,Object> delayUntil,
                                                               boolean isIntegrated) {
        Map<String, Object> map = isIntegrated
            ? emptyMap()
            : new HashMap<String, Object>() {
                {
                    put("taskId", taskId);
                    put("name", name);
                    put("processCategories", processCategories);
                    if (delayUntil != null) {
                        put("delayUntil", delayUntil);
                    }
                }
            };

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

        assertThat(dmnDecisionTableResult.getResultList().size() == 0
                       ? singletonList(emptyMap()) : dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(28));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(166));

    }

    public static Stream<Arguments> addendumScenarioProvider() {
        Map<String, Object> appellantInDetention = mapAdditionalData("{\n"
                                                                 + "   \"Data\":{\n"
                                                                 + "      \"appellantInDetention\": true\n"
                                                                 + "   }\n"
                                                                 + "}");

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

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
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

}
