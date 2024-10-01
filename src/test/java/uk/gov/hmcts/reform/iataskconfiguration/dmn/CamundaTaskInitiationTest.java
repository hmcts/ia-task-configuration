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
        Map<String,Object> delayFor14DaysExcludingBankHolidays = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
            "delayUntilOrigin", LocalDate.now(),
            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
        );

        Map<String,Object> delayFor12Days = Map.of(
            "delayUntilIntervalDays", "12",
            "delayUntilOrigin", LocalDate.now()
        );

        Map<String,Object> delayFor14Days = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilOrigin", LocalDate.now()
        );

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
                "paymentAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                  + "   \"Data\":{\n"
                                  + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
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
                "paymentAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                  + "   \"Data\":{\n"
                                  + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
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
                "paymentAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
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
                                      + "          \"listCaseHearingDate\" : \"" + hearingDate + "\""
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
                "removeRepresentation",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpNoticeOfChange",
                        "name", "Follow-up Notice of Change",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayFor14DaysExcludingBankHolidays
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
                        "delayUntil", delayFor14DaysExcludingBankHolidays
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
                null,
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
                "triggerCmrUpdated",
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
                "triggerCmrListed",
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"remissionType\":\"" + "hoWaiverRemission" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewMigratedCase",
                        "name", "Review migrated case",

                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "reviewAriaRemissionApplication",
                        "name", "Review Remission Application",

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
                        "delayUntil", delayFor14Days,
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
                "makeAnApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAdmin\":\"" + true + "\",\n"
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
                "uploadAddendumEvidenceHomeOffice",
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
                                      + "          }\n"
                                      + "        }\n"
                                      + "      }"),
                emptyList()
            ),
            getArgumentOf("Adjourn",
                          "processApplicationAdjourn",
                          "Process Adjourn Application"),
            getArgumentOf("Expedite",
                          "processApplicationExpedite",
                          "Process Expedite Application"),
            getArgumentOf("Time extension",
                          "processApplicationTimeExtension",
                          "Process Time Extension Application"),
            getArgumentOf("Transfer",
                          "processApplicationTransfer",
                          "Process Transfer Application"),
            getArgumentOf("Withdraw",
                          "processApplicationWithdraw",
                          "Process Withdraw Application"),
            getArgumentOf("Update hearing requirements",
                          "processApplicationUpdateHearingRequirements",
                          "Process Update Hearing Requirements Application"),
            getArgumentOf("Update appeal details",
                          "processApplicationUpdateAppealDetails",
                          "Process Update Appeal Details Application"),
            getArgumentOf("Reinstate an ended appeal",
                          "processApplicationReinstateAnEndedAppeal",
                          "Process Reinstate An Ended Appeal Application"),
            getArgumentOf("Other",
                          "processApplicationOther",
                          "Process Other Application"),
            getArgumentOf("Link/unlink appeals",
                          "processApplicationLink/UnlinkAppeals",
                          "Process Link/Unlink Appeals Application"),
            getArgumentOf("Set aside a decision",
                          "reviewSetAsideDecisionApplication",
                          "Review set aside decision application"),
            getArgumentOf("Judge's review of application decision",
                          "processApplicationToReviewDecision",
                          "Process Application to Review Decision"),
            getArgumentOf("Change hearing type",
                          "processApplicationChangeHearingType",
                          "Process Change Hearing Type Application")
        );
    }

    private static Arguments getArgumentOf(String applicationType, String taskId, String taskName) {
        return Arguments.of(
            "makeAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                                  + "            \"decision\" : \"\",\n"
                                  + "            \"applicant\" : \"\"\n"
                                  + "          }\n"
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
        assertThat(logic.getInputs().size(), is(23));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(82));
    }

    public static Stream<Arguments> addendumScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "uploadAddendumEvidenceLegalRep",
                "preHearing",
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
                List.of(
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
                List.of(
                    Map.of(
                        "taskId", "reviewAddendumEvidence",
                        "name", "Review Addendum Evidence",

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
        List<Map<String, String>> expectation
    ) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);

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
