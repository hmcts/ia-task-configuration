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

    static Stream<Arguments> scenarioProvider() {

        return Stream.of(
            Arguments.of(
                "applyForFTPAAppellant",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "decideAnFTPA",
                        "name", "Decide an FTPA",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "applyForFTPARespondent",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "decideAnFTPA",
                        "name", "Decide an FTPA",

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
                singletonList(
                    Map.of(
                        "taskId", "decideAnFTPA",
                        "name", "Decide an FTPA",

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
                singletonList(
                    Map.of(
                        "taskId", "decideAnFTPA",
                        "name", "Decide an FTPA",

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
                singletonList(
                    Map.of(
                        "taskId", "adaDecideAnFTPA",
                        "name", "ADA-Decide an FTPA",

                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaDecideAnFTPA",
                        "name", "ADA-Decide an FTPA",

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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


                        "workingDaysAllowed", 2,
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


                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCase",
                "prepareForHearing",
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "          \"listCaseHearingDate\" : \""
                                      + LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                      + "\""
                                      + "        }\n"
                                      + "      }"),
                List.of(
                    Map.of(
                        "taskId", "caseSummaryHearingBundleStartDecision",
                        "name", "Create Hearing Bundle",
                        "workingDaysAllowed", 2,
                        "processCategories", "caseProgression"
                    ),
                    Map.of(
                        "taskId", "uploadHearingRecording",
                        "name", "Upload hearing recording",
                        "delayDuration", 5,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "listCase",
                "awaitingRespondentEvidence",
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "        \"isAcceleratedDetainedAppeal\":\"" + true + "\",\n"
                                      + "          \"listCaseHearingDate\" : \""
                                      + LocalDateTime.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                      + "\""
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "adaUploadHearingRecording",
                        "name", "ADA-Upload hearing recording",
                        "delayDuration", 5,
                        "processCategories", "caseProgression"
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

                        "workingDaysAllowed", 2,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueRespondentEvidence",
                        "name", "Follow-up overdue respondent evidence",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpExtendedDirection",
                        "name", "Follow-up extended direction",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueCaseBuilding",
                        "name", "Follow-up overdue case building",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestReasonsForAppeal",
                "awaitingReasonsForAppeal",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueReasonsForAppeal",
                        "name", "Follow-up overdue reasons for appeal",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "sendDirectionWithQuestions",
                "awaitingClarifyingQuestionsAnswers",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueClarifyingAnswers",
                        "name", "Follow-up overdue clarifying answers",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestCmaRequirements",
                "awaitingCmaRequirements",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueCmaRequirements",
                        "name", "Follow-up overdue CMA requirements",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                "respondentReview",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueRespondentReview",
                        "name", "Follow-up overdue respondent review",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestHearingRequirementsFeature",
                "submitHearingRequirements",
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueHearingRequirements",
                        "name", "Follow-up overdue hearing requirements",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                null,
                null,
                singletonList(
                    Map.of(
                        "taskId", "followUpNonStandardDirection",
                        "name", "Follow-up non-standard direction",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "caseProgression"
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

                        "workingDaysAllowed", 2,
                        "processCategories", "followUpOverdue",
                        "delayDuration", 14
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

                        "workingDaysAllowed", 2,
                        "processCategories", "followUpOverdue",
                        "delayDuration", 14
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
                        "taskId", "reviewHearingBundle",
                        "name", "Review Hearing bundle",
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",


                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",


                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",


                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",


                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",


                        "workingDaysAllowed", 0,
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
                singletonList(
                    Map.of(
                        "taskId", "adaReviewTheAppeal",
                        "name", "ADA-Review the appeal",


                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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

                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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

                        "workingDaysAllowed", 0,
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
                                                      List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putValue("additionalData", map);

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
            "workingDaysAllowed", 2,
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
            "workingDaysAllowed", 0,
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
        inputVariables.putValue("additionalData", mapAdditionalData(additionalData));
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
                                      + "            \"type\" : \"Judge's review of application decision\",\n"
                                      + "            \"decision\" : \"\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      }"),
                singletonList(
                    Map.of(
                        "taskId", "processApplicationToReviewDecision",
                        "name", "Process Application to Review Decision",
                        "workingDaysAllowed", 2,
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
                    "workingDaysAllowed", 5,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
                        "processCategories", "application"
                    )
                )
            )
        );
    }

    public static Stream<Arguments> adaMakeAnApplicationLoScenarioProvider() {
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
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
                        "workingDaysAllowed", 0,
                        "processCategories", "application"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueRespondentEvidence",
                        "name", "Follow-up overdue respondent evidence",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentEvidence",
                "awaitingRespondentEvidence",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                asList(
                    Map.of(
                        "taskId", "adaFollowUpOverdueRespondentEvidence",
                        "name", "ADA-Follow-up overdue respondent evidence",

                        "workingDaysAllowed", 0,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    ),
                    Map.of(
                        "taskId", "adaListCase",
                        "name", "ADA-List case",

                        "workingDaysAllowed", 0,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpExtendedDirection",
                        "name", "Follow-up extended direction",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpExtendedDirection",
                        "name", "ADA-Follow-up extended direction",

                        "workingDaysAllowed", 0,
                        "delayDuration", 0,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueCaseBuilding",
                        "name", "Follow-up overdue case building",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                "caseBuilding",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpOverdueCaseBuilding",
                        "name", "ADA-Follow-up overdue case building",

                        "workingDaysAllowed", 0,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpOverdueRespondentReview",
                        "name", "Follow-up overdue respondent review",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "requestRespondentReview",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpOverdueRespondentReview",
                        "name", "ADA-Follow-up overdue respondent review",

                        "workingDaysAllowed", 0,
                        "delayDuration", 0,
                        "processCategories", "followUpOverdue"
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpNonStandardDirection",
                        "name", "Follow-up non-standard direction",

                        "workingDaysAllowed", 2,
                        "delayDuration", 0,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpNonStandardDirection",
                        "name", "ADA-Follow-up non-standard direction",

                        "workingDaysAllowed", 0,
                        "delayDuration", 0,
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "removeRepresentation",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "followUpNoticeOfChange",
                        "name", "Follow-up Notice of Change",

                        "workingDaysAllowed", 2,
                        "processCategories", "followUpOverdue",
                        "delayDuration", 14
                    )
                )
            ),
            Arguments.of(
                "removeRepresentation",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isAcceleratedDetainedAppeal\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "adaFollowUpNoticeOfChange",
                        "name", "ADA-Follow-up Notice of Change",

                        "workingDaysAllowed", 0,
                        "processCategories", "followUpOverdue",
                        "delayDuration", 14
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
        inputVariables.putValue("additionalData", additionalData);

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
        inputVariables.putValue("additionalData", additionalData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @ParameterizedTest
    @MethodSource("adaMakeAnApplicationLoScenarioProvider")
    void given_ada_makeAnApplication_LO_should_evaluate_dmn(String eventId,
                                                         String postEventState,
                                                         Map<String, Object> additionalData,
                                                         List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("additionalData", additionalData);

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
        inputVariables.putValue("additionalData", additionalData);

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

                    "workingDaysAllowed", 2,
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

                    "workingDaysAllowed", 0,
                    "processCategories", "application"
                )
            )
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(8));
        assertThat(logic.getOutputs().size(), is(5));
        assertThat(logic.getRules().size(), is(69));
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
                        "workingDaysAllowed", 2,
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
            return mapper.readValue(additionalData, typeRef);
        } catch (IOException exp) {
            return null;
        }
    }

}
