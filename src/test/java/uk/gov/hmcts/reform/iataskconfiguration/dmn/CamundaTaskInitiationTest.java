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

        return Stream.of(
            Arguments.of(
                "applyForFTPAAppellant",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }\n"
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
                                              + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                              + "      \"isNotificationTurnedOff\": false\n"
                                              + "   }\n"
                                              + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }\n"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }\n"
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
                                              + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                              + "      \"isNotificationTurnedOff\": false\n"
                                              + "   }\n"
                                              + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }\n"
                                      + "}"),
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
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "revocationOfProtection" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
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
                                      + "      \"appealType\":\"" + "deprivation" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
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
                            + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                            + "      \"remissionClaim\":\"" + "asylumSupport" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                            + "      \"isNotificationTurnedOff\": false\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewASRemission",
                                    "name", "Review AS remission",

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
                                      + "      \"remissionOption\":\"" + "asylumSupportFromHo" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewASRemission",
                        "name", "Review AS remission",

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
                                + "      \"remissionClaim\":\"" + "asylumSupport" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\": false\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewASRemission",
                                        "name", "Detained - Review AS remission",

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
                                + "      \"remissionOption\":\"" + "asylumSupportFromHo" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\": false\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewASRemission",
                                        "name", "Detained - Review AS remission",

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
                            + "      \"remissionClaim\":\"" + "homeOfficeWaiver" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                            + "      \"isNotificationTurnedOff\": false\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewHOWaiverRemission",
                                    "name", "Review HO Waiver remission",

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
                                + "      \"remissionClaim\":\"" + "homeOfficeWaiver" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\": false\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewHOWaiverRemission",
                                        "name", "Detained - Review HO Waiver remission",

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
                            + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                            + "      \"isNotificationTurnedOff\": false\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewECRRemission",
                                    "name", "Review ECR remission",

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
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewECRRemission",
                                        "name", "Detained - Review ECR remission",

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
                            + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewHWFRemission",
                                    "name", "Review HWF remission",

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
                                      + "      \"remissionOption\":\"" + "feeWaiverFromHo" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewHOWaiverRemission",
                        "name", "Review HO Waiver remission",

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
                                + "      \"remissionOption\":\"" + "feeWaiverFromHo" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\": false\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewHOWaiverRemission",
                                        "name", "Detained - Review HO Waiver remission",

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
                                      + "      \"remissionOption\":\"" + "under18GetSupportFromLocalAuthority" + "\",\n"
                                      + "      \"isNotificationTurnedOff\": false\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewAuthorityRemission",
                        "name", "Review Authority remission",

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
                                + "      \"remissionOption\":\"" + "under18GetSupportFromLocalAuthority" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\": false\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewAuthorityRemission",
                                        "name", "Detained - Review Authority remission",

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
                            + "      \"remissionOption\":\"" + "iWantToGetHelpWithFees" + "\",\n"
                            + "      \"isNotificationTurnedOff\": false\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewHWFRemission",
                                    "name", "Review HWF remission",

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
                                      + "      \"remissionOption\":\"" + "noneOfTheseStatements" + "\",\n"
                                      + "      \"helpWithFeesOption\":\"" + "alreadyApplied" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

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
                                      + "      \"remissionOption\":\"" + "noneOfTheseStatements" + "\",\n"
                                      + "      \"helpWithFeesOption\":\"" + "wantToApply" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewHWFRemission",
                        "name", "Detained - Review HWF remission",

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
                                + "      \"remissionClaim\":\"" + "legalAid" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "reviewLARemission",
                                        "name", "Review LA remission",

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
                                + "      \"remissionClaim\":\"" + "legalAid" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewLARemission",
                                        "name", "Detained - Review LA remission",

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
                            + "      \"remissionClaim\":\"" + "section17" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewAuthorityRemission",
                                    "name", "Review Authority remission",

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
                                + "      \"remissionClaim\":\"" + "section17" + "\",\n"
                                + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                + "   }"
                                + "}"),
                        singletonList(
                                Map.of(
                                        "taskId", "detainedReviewAuthorityRemission",
                                        "name", "Detained - Review Authority remission",

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
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "migrateCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "migrateCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "migrateCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                        "migrateCase",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                + "      \"journeyType\":\"" + "aip" + "\",\n"
                                + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                        "migrateCase",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                + "      \"journeyType\":\"" + "aip" + "\",\n"
                                + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                        "migrateCase",
                        "appealSubmitted",
                        mapAdditionalData("{\n"
                                + "   \"Data\":{\n"
                                + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                + "      \"journeyType\":\"" + "aip" + "\",\n"
                                + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfEu" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appealType\":\"" + "refusalOfHumanRights" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "uploadAdditionalEvidence",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "uploadAdditionalEvidenceHomeOffice",
                "caseBuilding",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "submitCase",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRespondentResponse",
                        "name", "Review Respondent Response",


                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "uploadHomeOfficeAppealResponse",
                "respondentReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewRespondentResponse",
                        "name", "Detained - Review Respondent Response",


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
                                      + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }"),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                merge(
                    variablesDirectionDueDate,
                    mapAdditionalData(" {\n"
                                          + "        \"Data\" : {\n"
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                          + "        }\n"
                                          + "      }")
                ),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                mapAdditionalData(" {\n"
                                      + "        \"Data\" : {\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "        }\n"
                                      + "      }"),
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
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                           + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                           + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    List.of(
                            Map.of(
                                    "taskId", "detainedReviewTheAppeal",
                                    "name", "Detained - Review the appeal",

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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                    "submitAppeal",
                    "appealSubmitted",
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"appealType\":\"" + "protection" + "\",\n"
                            + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    List.of(
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
                    )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "protection" + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                    "submitAppeal",
                    "appealSubmitted",
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"appealType\":\"" + "protection" + "\",\n"
                            + "      \"remissionType\":\"" + "helpWithFees" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    List.of(
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
                    )
            ),
            Arguments.of(
                "submitAppeal",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"remissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewECRRemission",
                        "name", "Review ECR remission",

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
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    List.of(
                            Map.of(
                                    "taskId", "detainedReviewECRRemission",
                                    "name", "Detained - Review ECR remission",

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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

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
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    List.of(
                            Map.of(
                                    "taskId", "detainedReviewHWFRemission",
                                    "name", "Detained - Review HWF remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "requestFeeRemission",
                null,
                mapAdditionalData("{\n"
                        + "   \"Data\":{\n"
                        + "      \"lateRemissionType\":\"" + "helpWithFees" + "\",\n"
                        + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                        + "   }"
                        + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"lateRemissionType\":\"" + "helpWithFees" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewHWFRemission",
                                    "name", "Detained - Review HWF remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "requestFeeRemission",
                null,
                mapAdditionalData("{\n"
                        + "   \"Data\":{\n"
                        + "   \"journeyType\":\"" + "aip" + "\",\n"
                        + "      \"remissionOption\":\"" + "asylumSupportFromHo" + "\",\n"
                        + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                        + "   }"
                        + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewASRemission",
                        "name", "Review AS remission",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "asylumSupportFromHo" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewASRemission",
                                    "name", "Detained - Review AS remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "asylumSupport" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewASRemission",
                                    "name", "Review AS remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "asylumSupport" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewASRemission",
                                    "name", "Detained - Review AS remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "homeOfficeWaiver" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewHOWaiverRemission",
                                    "name", "Review HO Waiver remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "homeOfficeWaiver" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewHOWaiverRemission",
                                    "name", "Detained - Review HO Waiver remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"lateRemissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewECRRemission",
                                    "name", "Review ECR remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"lateRemissionType\":\"" + "exceptionalCircumstancesRemission" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewECRRemission",
                                    "name", "Detained - Review ECR remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "feeWaiverFromHo" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewHOWaiverRemission",
                                    "name", "Review HO Waiver remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "feeWaiverFromHo" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewHOWaiverRemission",
                                    "name", "Detained - Review HO Waiver remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "parentGetSupportFromLocalAuthority" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewAuthorityRemission",
                                    "name", "Review Authority remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "parentGetSupportFromLocalAuthority" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewAuthorityRemission",
                                    "name", "Detained - Review Authority remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "iWantToGetHelpWithFees" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewHWFRemission",
                                    "name", "Review HWF remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "requestFeeRemission",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"remissionOption\":\"" + "noneOfTheseStatements" + "\",\n"
                                      + "      \"helpWithFeesOption\":\"" + "wantToApply" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "requestFeeRemission",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"journeyType\":\"" + "aip" + "\",\n"
                                      + "      \"remissionOption\":\"" + "noneOfTheseStatements" + "\",\n"
                                      + "      \"helpWithFeesOption\":\"" + "alreadyApplied" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "   \"journeyType\":\"" + "aip" + "\",\n"
                            + "      \"remissionOption\":\"" + "iWantToGetHelpWithFees" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewHWFRemission",
                                    "name", "Detained - Review HWF remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "legalAid" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewLARemission",
                                    "name", "Review LA remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "legalAid" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewLARemission",
                                    "name", "Detained - Review LA remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "section20" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "reviewAuthorityRemission",
                                    "name", "Review Authority remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                    "requestFeeRemission",
                    null,
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                            + "      \"remissionClaim\":\"" + "section20" + "\",\n"
                            + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                            + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                            + "   }"
                            + "}"),
                    singletonList(
                            Map.of(
                                    "taskId", "detainedReviewAuthorityRemission",
                                    "name", "Detained - Review Authority remission",

                                    "processCategories", "caseProgression"
                            )
                    )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                "listing",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                    "listCaseWithoutHearingRequirements",
                    "listing",
                    mapAdditionalData("{\n"
                            + "   \"Data\":{\n"
                                          + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                          + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                          + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "recordRemissionDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"remissionDecision\":\"" + "partiallyApproved" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"paymentStatus\":\"" + "Paid" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                emptyList()
            ),
            Arguments.of(
                "markAppealAsRemitted",
                "remitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "reviewRemittedAppeal",
                        "name", "Review remitted appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "markAppealAsRemitted",
                "remitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewRemittedAppeal",
                        "name", "Detained - Review remitted appeal",

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
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"ftpaApplicantType\":\"" + "respondent" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule32" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "updateTribunalDecision",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule32" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewAppealSetAsideUnderRule32",
                        "name", "Detained - Review appeal set aside under rule 32",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "handleHearingException",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                                      + "      \"isIntegrated\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "restoreStateFromAdjourn",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "          \"isIntegrated\" : " + true + ",\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedRelistCase",
                        "name", "Detained - Relist The Case",

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
                                      + "      \"relistCaseImmediately\":" + true + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + ",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"relistCaseImmediately\":" + true + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + ",\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                                      + "          \"isIntegrated\" : " + true + ",\n"
                                      + "      \"isDecisionWithoutHearing\" : " + true + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + ",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "          \"isIntegrated\" : " + true + ",\n"
                                      + "      \"autoHearingRequestEnabled\" : " + false + ",\n"
                                      + "      \"isDecisionWithoutHearing\" : " + true + ",\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "triggerReviewInterpreterBookingTask",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "hearingCancelled",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "hearingCancelled",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                "editCaseListing",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"shouldTriggerReviewInterpreterTask\" : \"" + true + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewMigratedCase",
                        "name", "Detained - Review migrated case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "ariaCreateCase",
                "migrated",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "detainedReviewMigratedCase",
                        "name", "Detained - Review migrated case",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "startAppeal",
                "appealStartedByAdmin",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
                                      + "   }"
                                      + "}"),
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
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isAdmin\":\"" + true + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule31" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"updateTribunalDecisionList\":\"" + "underRule32" + "\",\n"
                                      + "      \"appellantInDetention\":\"" + "false" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"appellantInDetention\":\"" + "true" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + "false" + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                        "name", "Detained - Process Application to Review Decision",

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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                "decideFtpaApplication",
                null,
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"remissionType\":\"" + "helpWithFees" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                        "taskId", "reviewECRRemission",
                        "name", "Review ECR remission",

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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                        "taskId", "reviewHWFRemission",
                        "name", "Review HWF remission",

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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"remissionClaim\":\"" + "homeOfficeWaiver" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
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
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "appealSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appealType\":\"" + "euSettlementScheme" + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                        "taskId", "reviewECRRemission",
                        "name", "Review ECR remission",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\"\n"
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
                "progressMigratedCase",
                "caseUnderReview",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                "remitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"appellantInDetention\":\"" + true + "\",\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
                singletonList(
                    Map.of(
                        "taskId", "detainedReviewRemittedAppeal",
                        "name", "Detained - Review remitted appeal",

                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "progressMigratedCase",
                "ftpaSubmitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\"\n"
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
                                      + "   }"
                                      + "}"),
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
                                      + "      \"appellantInDetention\":\"" + true + "\"\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "      \"isNotificationTurnedOff\":\"" + false + "\",\n"
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
                                      + "            \"applicant\" : \"\",\n"
                                      + "            \"appellantInDetention\" : \"\"\n"
                                      + "          }\n"
                                      + "        }\n"
                                      + "      }"),
                emptyList()
            ),
            getArgumentOf("Adjourn",
                          "processApplicationAdjourn",
                          "Process Adjourn Application",
                          false,
                                            false),

            getArgumentOf("Adjourn",
                          "detainedProcessApplicationAdjourn",
                          "Detained - Process Adjourn Application",
                          true,
                          false),
            getArgumentOf("Expedite",
                          "processApplicationExpedite",
                          "Process Expedite Application",
                          false,
                          false),
            getArgumentOf("Expedite",
                          "detainedProcessApplicationExpedite",
                          "Detained - Process Expedite Application",
                          true,
                          false),
            getArgumentOf("Time extension",
                          "processApplicationTimeExtension",
                          "Process Time Extension Application",
                          false,
                          false),
            getArgumentOf("Time extension",
                          "detainedProcessApplicationTimeExtension",
                          "Detained - Process Time Extension Application",
                          true,
                          false),
            getArgumentOf("Transfer",
                          "processApplicationTransfer",
                          "Process Transfer Application",
                          false,
                          false),
            getArgumentOf("Transfer",
                          "detainedProcessApplicationTransfer",
                          "Detained - Process Transfer Application",
                          true,
                          false),
            getArgumentOf("Withdraw",
                          "processApplicationWithdraw",
                          "Process Withdraw Application",
                          false,
                          false),
            getArgumentOf("Withdraw",
                          "detainedProcessApplicationWithdraw",
                          "Detained - Process Withdraw Application",
                          true,
                          false),
            getArgumentOf("Update hearing requirements",
                          "processApplicationUpdateHearingRequirements",
                          "Process Update Hearing Requirements Application",
                          false,
                          false),
            getArgumentOf("Update hearing requirements",
                          "detainedProcessApplicationUpdateHearingRequirements",
                          "Detained - Process Update Hearing Requirements Application",
                          true,
                          false),
            getArgumentOf("Update appeal details",
                          "processApplicationUpdateAppealDetails",
                          "Process Update Appeal Details Application",
                          false,
                          false),
            getArgumentOf("Update appeal details",
                          "detainedProcessApplicationUpdateAppealDetails",
                          "Detained - Process Update Appeal Details Application",
                          true,
                          false),
            getArgumentOf("Reinstate an ended appeal",
                          "processApplicationReinstateAnEndedAppeal",
                          "Process Reinstate An Ended Appeal Application",
                          false,
                          false),
            getArgumentOf("Reinstate an ended appeal",
                          "detainedProcessApplicationReinstateAnEndedAppeal",
                          "Detained - Process Reinstate An Ended Appeal Application",
                          true,
                          false),
            getArgumentOf("Other",
                          "processApplicationOther",
                          "Process Other Application",
                          false,
                          false),
            getArgumentOf("Other",
                          "detainedProcessApplicationOther",
                          "Detained - Process Other Application",
                          true,
                          false),
            getArgumentOf("Link/unlink appeals",
                          "processApplicationLink/UnlinkAppeals",
                          "Process Link/Unlink Appeals Application",
                          false,
                          false),
            getArgumentOf("Link/unlink appeals",
                          "detainedProcessApplicationLink/UnlinkAppeals",
                          "Detained - Process Link/Unlink Appeals Application",
                          true,
                          false),
            getArgumentOf("Set aside a decision",
                          "reviewSetAsideDecisionApplication",
                          "Review set aside decision application",
                          false,
                          false),
            getArgumentOf("Set aside a decision",
                          "detainedReviewSetAsideDecisionApplication",
                          "Detained - Review set aside decision application",
                          true,
                          false),
            getArgumentOf("Judge's review of application decision",
                          "processApplicationToReviewDecision",
                          "Process Application to Review Decision",
                          false,
                          false),
            getArgumentOf("Judge's review of application decision",
                          "detainedProcessApplicationToReviewDecision",
                          "Detained - Process Application to Review Decision",
                          true,
                          false),
            getArgumentOf("Change hearing type",
                          "processApplicationChangeHearingType",
                          "Process Change Hearing Type Application",
                          false,
                          false),
            getArgumentOf("Change hearing type",
                          "detainedProcessApplicationChangeHearingType",
                          "Detained - Process Change Hearing Type Application",
                          true,
                          false)
        );
    }

    private static Arguments getArgumentOf(String applicationType,
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
                                              delayFor5Days, false, false, false),
            getDecideAnApplicationArgumentsOf("Set aside a decision",
                                              "detainedFollowUpSetAsideDecision",
                                              "Detained - Follow Up Set Aside Decision",
                                              "followUpOverdue",
                                              delayFor5Days, false, true, false)
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

    private static Arguments getDecideAnApplicationArgumentsOf(String applicationType,
                                                               String taskId,
                                                               String name,
                                                               String processCategories,
                                                               Map<String,Object> delayUntil,
                                                               boolean isIntegrated,
                                                               boolean appellantInDetention,
                                                               boolean isNotificationTurnedOff) {
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

        assertThat(dmnDecisionTableResult.getResultList().size() == 0
                       ? singletonList(emptyMap()) : dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(31));
        assertThat(logic.getOutputs().size(), is(4));
        assertThat(logic.getRules().size(), is(195));
    }

    public static Stream<Arguments> addendumScenarioProvider() {
        Map<String, Object> appellantInDetention = mapAdditionalData("{\n"
                                                                 + "   \"Data\":{\n"
                                                                 + "          \"appellantInDetention\" : \"" + true + "\",\n"
                                                                 + "          \"isNotificationTurnedOff\" : \"" + false + "\"\n"
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
