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
import org.junit.jupiter.params.provider.CsvSource;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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
        Map<String,Object> delayFor14Days = Map.of(
            "delayUntilIntervalDays", "14",
            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
            "delayUntilOrigin", LocalDate.now(),
            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
        );

        Map<String,Object> delayFor12Days = Map.of(
            "delayUntilIntervalDays", "12",
            "delayUntilOrigin", LocalDate.now()
        );
        Map<String,Object> delayFor5Days = Map.of(
            "delayUntilIntervalDays", "5",
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
                "applyForFTPAAppellant",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaDecideAnFTPA",
                        "name", "ADA-Decide an FTPA",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAssignAFTPAJudge",
                        "name", "ADA-Assign a FTPA Judge",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "applyForFTPARespondent",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaDecideAnFTPA",
                        "name", "ADA-Decide an FTPA",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAssignAFTPAJudge",
                        "name", "ADA-Assign a FTPA Judge",

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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaSendDecisionsAndReasons",
                        "name", "ADA-Send decisions and reasons",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                "decision",
                null,
                singletonList(
                    Map.of(
                        "taskId", "prepareDecisionsAndReasons",
                        "name", "Prepare decisions and reasons",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                "decision",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "prepareDecisionsAndReasons",
                        "name", "Prepare decisions and reasons",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                "decision",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaPrepareDecisionsAndReasons",
                        "name", "ADA-Prepare decisions and reasons",
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
                                      + "      \"appealType\":\"" + "revocationOfProtection" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
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
                "submitAppeal",
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
                "submitAppeal",
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
                "uploadHomeOfficeBundle",
                "awaitingRespondentEvidence",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRespondentEvidence",
                        "name", "Review Respondent Evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadHomeOfficeBundle",
                "awaitingRespondentEvidence",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewRespondentEvidence",
                        "name", "ADA-Review Respondent Evidence",
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAppealSkeletonArgument",
                        "name", "ADA-Review Appeal Skeleton Argument",
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAppealSkeletonArgument",
                        "name", "ADA-Review Appeal Skeleton Argument",
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
                        "taskId", "uploadHearingRecording",
                        "name", "Upload hearing recording",
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
                "awaitingRespondentEvidence",
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "        \"isAcceleratedDetainedAppeal\":\"" + true + "\",\n"
                                      + "          \"listCaseHearingDate\" : \"" + hearingDate + "\""
                                      + "        }\n"
                                      + "      }"),
                List.of(
                    Map.of(
                        "taskId", "adaUploadHearingRecording",
                        "name", "ADA-Upload hearing recording",
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
                        "delayUntil", delayFor14Days
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
                        "delayUntil", delayFor14Days
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
                "changeHearingCentre",
                "prepareForHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                "finalBundling",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                "preHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                "awaitingRespondentEvidence",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "caseBuilding",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "prepareForHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "finalBundling",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "preHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeHearingCentre",
                "decision",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaEditListing",
                        "name", "ADA-Edit Listing",
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
                        "taskId", "reviewHearingBundle",
                        "name", "Review Hearing bundle",

                        "processCategories", "caseProgression"
                    ),
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
                        "taskId", "reviewHearingBundle",
                        "name", "Review Hearing bundle",

                        "processCategories", "caseProgression"
                    ),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewHearingBundle",
                        "name", "Review Hearing bundle",
                        "processCategories", "caseProgression"
                    ),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewHearingBundle",
                        "name", "Review Hearing bundle",
                        "processCategories", "caseProgression"
                    ),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewHearingBundle",
                        "name", "ADA-Review Hearing bundle",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "customiseHearingBundle",
                "finalBundling",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewHearingBundle",
                        "name", "ADA-Review Hearing bundle",
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
                "adaSuitabilityReview",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"suitabilityReviewDecision\":\"" + "unsuitable" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewCaseMarkedUnsuitableForADA",
                        "name", "Review Case Marked Unsuitable For ADA",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "revocationOfProtection" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
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
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
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
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
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
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "deprivation" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
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
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "adaAllocateHearingJudge",
                        "name", "ADA-Allocate Hearing Judge",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewAdditionalEvidence",
                        "name", "Review additional evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewAdditionalEvidence",
                        "name", "ADA-Review additional evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaReviewAdditionalEvidence",
                        "name", "ADA-Review additional evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidence",
                "prepareForHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAdditionalEvidence",
                        "name", "ADA-Review additional evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                "prepareForHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAdditionalHomeOfficeEvidence",
                        "name", "ADA-Review additional Home Office evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAdditionalHomeOfficeEvidence",
                        "name", "ADA-Review additional Home Office evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAdditionalHomeOfficeEvidence",
                        "name", "ADA-Review additional Home Office evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadAdditionalEvidenceHomeOffice",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaReviewAdditionalHomeOfficeEvidence",
                        "name", "ADA-Review additional Home Office evidence",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "transferOutOfAda",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "reviewCaseTransferredOutOfADA",
                        "name", "Review Case Transferred Out Of ADA",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewADASuitability",
                        "name", "Review ADA suitability",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "adaSuitabilityReview",
                "prepareForHearing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"suitabilityReviewDecision\":\"" + "suitable" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "adaCaseSummaryHearingBundleStartDecision",
                        "name", "ADA-Create Hearing Bundle",
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
                null,
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
                null,
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
                                                      List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putAll(map);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "uploadAddendumEvidenceLegalRep, preHearing, false",
        "uploadAddendumEvidenceLegalRep, decision, false",
        "uploadAddendumEvidenceLegalRep, decided, false",
        "uploadAddendumEvidence, preHearing, false",
        "uploadAddendumEvidence, decision, false",
        "uploadAddendumEvidence, decided, false",
        "uploadAddendumEvidenceHomeOffice, preHearing, false",
        "uploadAddendumEvidenceHomeOffice, decision, false",
        "uploadAddendumEvidenceHomeOffice, decided, false",
        "uploadAddendumEvidenceAdminOfficer, preHearing, false",
        "uploadAddendumEvidenceAdminOfficer, decision, false",
        "uploadAddendumEvidenceAdminOfficer, decided, false"
    })
    void given_addendum_events_with_no_ada_should_evaluate_dmn(String eventId, String postEventState, String isAda) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(
            prepareAddendumWithAdaInputVariable(eventId, postEventState, isAda));

        assertThat(dmnDecisionTableResult.getResultList(), is(singletonList(Map.of(
            "name", "Review Addendum Evidence",
            "processCategories", "caseProgression",
            "taskId", "reviewAddendumEvidence"
        ))));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "uploadAddendumEvidenceLegalRep, preHearing, true",
        "uploadAddendumEvidenceLegalRep, decision, true",
        "uploadAddendumEvidenceLegalRep, decided, true",
        "uploadAddendumEvidence, preHearing, true",
        "uploadAddendumEvidence, decision, true",
        "uploadAddendumEvidence, decided, true",
        "uploadAddendumEvidenceHomeOffice, preHearing, true",
        "uploadAddendumEvidenceHomeOffice, decision, true",
        "uploadAddendumEvidenceHomeOffice, decided, true",
        "uploadAddendumEvidenceAdminOfficer, preHearing, true",
        "uploadAddendumEvidenceAdminOfficer, decision, true",
        "uploadAddendumEvidenceAdminOfficer, decided, true"
    })
    void given_addendum_events_with_yes_ada_should_evaluate_dmn(String eventId, String postEventState, String isAda) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(
            prepareAddendumWithAdaInputVariable(eventId, postEventState, isAda));

        assertThat(dmnDecisionTableResult.getResultList(), is(singletonList(Map.of(
            "name", "ADA-Review addendum evidence",
            "processCategories", "caseProgression",
            "taskId", "adaReviewAddendumEvidence"
        ))));
    }

    private VariableMap prepareAddendumWithAdaInputVariable(String eventId, String postEventState, String isAda) {
        String additionalData = "{\"Data\":{\"isAcceleratedDetainedAppeal\":\"" + isAda + "\"}}";

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putAll(mapAdditionalData(additionalData));
        return inputVariables;
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
                                      + "            \"decision\" : \"\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      }"),
                emptyList()
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Judge's review of Legal Officer decision\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "processApplicationToReviewDecision",
                        "name", "Process Application to Review Decision",
                        "processCategories", "application"
                    )
                )
            ),
            getArgumentOf("Adjourn"),
            getArgumentOf("Expedite"),
            getArgumentOf("Time extension"),
            getArgumentOf("Transfer"),
            getArgumentOf("Withdraw"),
            getArgumentOf("Update appeal details"),
            getArgumentOf("Reinstate an ended appeal"),
            getArgumentOf("Other"),
            getArgumentOf("Link/unlink appeals")
        );
    }

    private static Arguments getArgumentOf(String applicationType) {
        return Arguments.of(
            "makeAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                                  + "            \"decision\" : \"\"\n"
                                  + "          }\n"
                                  + "        }\n"
                                  + "      }"),
            singletonList(
                Map.of(
                    "taskId", "processApplication",
                    "name", "Process Application",
                    "processCategories", "application"
                )
            )
        );
    }

    public static Stream<Arguments> adaMakeAnApplicationScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                emptyList()
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Judge's review of Legal Officer decision\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToReviewDecision",
                        "name", "ADA-Process Application to Review Decision",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Adjourn\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToAdjourn",
                        "name", "ADA-Process Application to Adjourn",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Expedite\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToExpedite",
                        "name", "ADA-Process Application to Expedite",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Time extension\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationForTimeExtension",
                        "name", "ADA-Process Application for Time Extension",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Withdraw\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToWithdraw",
                        "name", "ADA-Process Application to Withdraw",
                        "processCategories", "application"
                    )
                )
            )
        );
    }

    public static Stream<Arguments> adaMakeAnApplicationLoScenarioProvider() {
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
                "makeAnApplication",
                null,
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                emptyList()
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Update hearing requirements\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToUpdateHearingRequirements",
                        "name", "ADA-Process Application to Update Hearing Requirements",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Update appeal details\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToUpdateAppealDetails",
                        "name", "ADA-Process Application to Update Appeal Details",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Reinstate an ended appeal\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToReinstateAnEndedAppeal",
                        "name", "ADA-Process Application to Reinstate An Appeal",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Other\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaProcessApplicationToOther",
                        "name", "ADA-Process Other Application",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "makeAnApplication",
                null,
                null,
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"lastModifiedApplication\" : {\n"
                                      + "            \"type\" : \"Link/unlink appeals\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          },\n"
                                      + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaLinkUnlinkAppeals",
                        "name", "ADA-Process Application to Link/Unlink Appeals",
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                asList(
                    Map.of(
                        "taskId", "adaFollowUpOverdueRespondentEvidence",
                        "name", "ADA-Follow-up overdue respondent evidence",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    ),
                    Map.of(
                        "taskId", "adaListCase",
                        "name", "ADA-List case",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                null,
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpExtendedDirection",
                        "name", "ADA-Follow-up extended direction",
                        "processCategories", "caseProgression",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpOverdueCaseBuilding",
                        "name", "ADA-Follow-up overdue case building",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                "respondentReview",
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpOverdueRespondentReview",
                        "name", "ADA-Follow-up overdue respondent review",
                        "processCategories", "followUpOverdue",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                null,
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpNonStandardDirection",
                        "name", "ADA-Follow-up non-standard direction",
                        "processCategories", "caseProgression",
                        "delayUntil", delayUntilDirectionDue
                    )
                )
            ),
            Arguments.of(
                "removeRepresentation",
                null,
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpNoticeOfChange",
                        "name", "Follow-up Notice of Change",
                        "processCategories", "followUpOverdue",
                        "delayUntil", Map.of(
                            "delayUntilIntervalDays", "14",
                            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
                            "delayUntilOrigin", LocalDate.now(),
                            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
                        )
                    )
                )
            ),
            Arguments.of(
                "removeRepresentation",
                null,
                variablesDirectionDueDate,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpNoticeOfChange",
                        "name", "ADA-Follow-up Notice of Change",
                        "processCategories", "followUpOverdue",
                        "delayUntil", Map.of(
                            "delayUntilIntervalDays", "14",
                            "delayUntilNonWorkingCalendar", "https://www.gov.uk/bank-holidays/england-and-wales.json",
                            "delayUntilOrigin", LocalDate.now(),
                            "delayUntilNonWorkingDaysOfWeek", "SATURDAY,SUNDAY"
                        )
                    )

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

    @ParameterizedTest
    @MethodSource("adaMakeAnApplicationScenarioProvider")
    void given_ada_makeAnApplication_should_evaluate_dmn(String eventId,
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

    @ParameterizedTest
    @MethodSource("adaMakeAnApplicationLoScenarioProvider")
    void given_ada_makeAnApplication_LO_should_evaluate_dmn(String eventId,
                                                         String postEventState,
                                                         Map<String, Object> directionDueDateMap,
                                                         Map<String, Object> additionalData,
                                                         List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putAll(directionDueDateMap);
        inputVariables.putAll(additionalData);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    public static Stream<Arguments> decideAnApplicationScenarioProvider() {
        return Stream.of(
            getDecideAnApplicationArgumentsOf("Adjourn", "editListing", "Edit Listing"),
            getDecideAnApplicationArgumentsOf("Expedite", "editListing", "Edit Listing"),
            getDecideAnApplicationArgumentsOf("Transfer", "editListing", "Edit Listing"),
            getAdaDecideAnApplicationArgumentsOf("Adjourn", "adaEditListing",
                                                 "ADA-Edit Listing"),
            getAdaDecideAnApplicationArgumentsOf("Expedite", "adaEditListing",
                                                 "ADA-Edit Listing")
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

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    private static Arguments getDecideAnApplicationArgumentsOf(String applicationType, String taskId, String name) {
        return Arguments.of(
            "decideAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                                  + "            \"decision\" : \"Granted\"\n"
                                  + "          }\n"
                                  + "        }\n"
                                  + "      }"),
            singletonList(
                Map.of(
                    "taskId", taskId,
                    "name", name,


                    "processCategories", "application"
                )
            )
        );
    }

    private static Arguments getAdaDecideAnApplicationArgumentsOf(String applicationType, String taskId, String name) {
        return Arguments.of(
            "decideAnApplication",
            null,
            mapAdditionalData(" {\n"
                                  + "        \"Data\" : {\n"
                                  + "          \"lastModifiedApplication\" : {\n"
                                  + "            \"type\" : \"" + applicationType + "\",\n"
                              + "                \"decision\":\"" + "Granted" + "\"\n"
                                  + "          },\n"
                                  + "           \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                  + "        }\n"
                                  + "      }"),
            singletonList(
                Map.of(
                    "taskId", taskId,
                    "name", name,
                    "processCategories", "application"
                )
            )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(12));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(80));
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

    @ParameterizedTest(name = "event id: {0} post event state: {1} additional data: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn_to_not_create_task_if_notification_turned_off(String eventId,
                                                      String postEventState,
                                                      Map<String, HashMap<String, HashMap<String, Object>>> map,
                                                      List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        addNotificationMapping(inputVariables, map, true);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(emptyList()));
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} additional data: {2}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn_to_create_task_if_notification_turned_on(String eventId,
                                                                         String postEventState,
                                                                         Map<String, HashMap<String, HashMap<String, Object>>> map,
                                                                         List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        addNotificationMapping(inputVariables, map, false);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    private static void addNotificationMapping(VariableMap inputVariables,
                                               Map<String, HashMap<String, HashMap<String, Object>>> map,
                                               boolean isNotificationTurnedOff) {
        if (map != null && map.containsKey("additionalData")) {
            HashMap<String, HashMap<String, Object>> additionalDataMap = map.get("additionalData");
            HashMap<String, Object> dataMap = additionalDataMap.get("Data");
            dataMap.put("isNotificationTurnedOff", isNotificationTurnedOff);
        } else {
            String additionalData = "{\"Data\":{\"isNotificationTurnedOff\":\"" + isNotificationTurnedOff + "\"}}";
            inputVariables.putAll(mapAdditionalData(additionalData));
        }
        inputVariables.putAll(map);
    }
}
