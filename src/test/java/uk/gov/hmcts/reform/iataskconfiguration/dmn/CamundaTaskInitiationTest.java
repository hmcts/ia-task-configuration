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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff": false
                                         }
                                      }\
                                      """),
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
                        mapAdditionalData("""
                                              {
                                                 "Data":{
                                                    "appellantInDetention":"\
                                              true\
                                              ",
                                                    "isNotificationTurnedOff": false
                                                 }
                                              }\
                                              """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff": false
                                         }
                                      }\
                                      """),
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
                        mapAdditionalData("""
                                              {
                                                 "Data":{
                                                    "appellantInDetention":"\
                                              true\
                                              ",
                                                    "isNotificationTurnedOff": false
                                                 }
                                              }\
                                              """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      revocationOfProtection\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      deprivation\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            euSettlementScheme\
                            ",
                                  "remissionClaim":"\
                            asylumSupport\
                            ",
                                  "appellantInDetention":"\
                            false\
                            ",
                                  "isNotificationTurnedOff": false
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "appealType":"\
                                      euSettlementScheme\
                                      ",
                                            "remissionOption":"\
                                      asylumSupportFromHo\
                                      ",
                                            "appellantInDetention":"\
                                      false\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }\
                                      }\
                                      """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "appealType":"\
                                euSettlementScheme\
                                ",
                                      "remissionClaim":"\
                                asylumSupport\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff": false
                                   }\
                                }\
                                """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "journeyType":"\
                                aip\
                                ",
                                      "appealType":"\
                                euSettlementScheme\
                                ",
                                      "remissionOption":"\
                                asylumSupportFromHo\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff": false
                                   }\
                                }\
                                """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            euSettlementScheme\
                            ",
                                  "remissionClaim":"\
                            homeOfficeWaiver\
                            ",
                                  "appellantInDetention":"\
                            false\
                            ",
                                  "isNotificationTurnedOff": false
                               }\
                            }\
                            """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "appealType":"\
                                euSettlementScheme\
                                ",
                                      "remissionClaim":"\
                                homeOfficeWaiver\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff": false
                                   }\
                                }\
                                """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            euSettlementScheme\
                            ",
                                  "remissionType":"\
                            exceptionalCircumstancesRemission\
                            ",
                                  "appellantInDetention":"\
                            false\
                            ",
                                  "isNotificationTurnedOff": false
                               }\
                            }\
                            """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "appealType":"\
                                euSettlementScheme\
                                ",
                                      "remissionType":"\
                                exceptionalCircumstancesRemission\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff":"\
                                false\
                                "
                                   }\
                                }\
                                """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            euSettlementScheme\
                            ",
                                  "remissionType":"\
                            helpWithFees\
                            ",
                                  "appellantInDetention":"\
                            false\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "appealType":"\
                                      protection\
                                      ",
                                            "remissionOption":"\
                                      feeWaiverFromHo\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }\
                                      }\
                                      """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "journeyType":"\
                                aip\
                                ",
                                      "appealType":"\
                                protection\
                                ",
                                      "remissionOption":"\
                                feeWaiverFromHo\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff": false
                                   }\
                                }\
                                """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "remissionOption":"\
                                      under18GetSupportFromLocalAuthority\
                                      ",
                                            "isNotificationTurnedOff": false
                                         }\
                                      }\
                                      """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "journeyType":"\
                                aip\
                                ",
                                      "appealType":"\
                                refusalOfHumanRights\
                                ",
                                      "remissionOption":"\
                                under18GetSupportFromLocalAuthority\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff": false
                                   }\
                                }\
                                """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "journeyType":"\
                            aip\
                            ",
                                  "appealType":"\
                            refusalOfHumanRights\
                            ",
                                  "remissionOption":"\
                            iWantToGetHelpWithFees\
                            ",
                                  "isNotificationTurnedOff": false
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "remissionOption":"\
                                      noneOfTheseStatements\
                                      ",
                                            "helpWithFeesOption":"\
                                      alreadyApplied\
                                      ",
                                            "appellantInDetention":"\
                                      false\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "remissionOption":"\
                                      noneOfTheseStatements\
                                      ",
                                            "helpWithFeesOption":"\
                                      wantToApply\
                                      ",
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "appealType":"\
                                refusalOfHumanRights\
                                ",
                                      "remissionClaim":"\
                                legalAid\
                                ",
                                      "isNotificationTurnedOff":"\
                                false\
                                "
                                   }\
                                }\
                                """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "appealType":"\
                                refusalOfHumanRights\
                                ",
                                      "remissionClaim":"\
                                legalAid\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff":"\
                                false\
                                "
                                   }\
                                }\
                                """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            refusalOfHumanRights\
                            ",
                                  "remissionClaim":"\
                            section17\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                        mapAdditionalData("""
                                {
                                   "Data":{
                                      "appealType":"\
                                refusalOfHumanRights\
                                ",
                                      "remissionClaim":"\
                                section17\
                                ",
                                      "appellantInDetention":"\
                                true\
                                ",
                                      "isNotificationTurnedOff":"\
                                false\
                                "
                                   }\
                                }\
                                """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "paymentStatus":"\
                                      Paid\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "paymentStatus":"\
                                      Paid\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      euSettlementScheme\
                                      ",
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "paymentStatus":"\
                                      Paid\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      protection\
                                      ",
                                            "journeyType":"\
                                      aip\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      protection\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfEu\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      refusalOfHumanRights\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "appellantInDetention":"\
                                          true\
                                          ",
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """),
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "appellantInDetention":"\
                                          true\
                                          ",
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "appellantInDetention":"\
                                          true\
                                          ",
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "appellantInDetention":"\
                                          true\
                                          ",
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "appellantInDetention":"\
                                          true\
                                          ",
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                    mapAdditionalData("""
                                           {
                                                  "Data" : {
                                                "appellantInDetention":"\
                                          true\
                                          ",
                                                "isNotificationTurnedOff":"\
                                          false\
                                          "
                                                  }
                                                }\
                                          """)
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                       {
                                              "Data" : {
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                              }
                                            }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      protection\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                           {
                              "Data":{
                                 "appealType":"\
                           protection\
                           ",
                                 "appellantInDetention":"\
                           true\
                           ",
                                 "isNotificationTurnedOff":"\
                           false\
                           "
                              }\
                           }\
                           """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      protection\
                                      ",
                                            "remissionType":"\
                                      exceptionalCircumstancesRemission\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            protection\
                            ",
                                  "remissionType":"\
                            exceptionalCircumstancesRemission\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      protection\
                                      ",
                                            "remissionType":"\
                                      helpWithFees\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            protection\
                            ",
                                  "remissionType":"\
                            helpWithFees\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      euSettlementScheme\
                                      ",
                                            "remissionType":"\
                                      exceptionalCircumstancesRemission\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            euSettlementScheme\
                            ",
                                  "remissionType":"\
                            exceptionalCircumstancesRemission\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"\
                                      euSettlementScheme\
                                      ",
                                            "remissionType":"\
                                      helpWithFees\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appealType":"\
                            euSettlementScheme\
                            ",
                                  "remissionType":"\
                            helpWithFees\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                        {
                           "Data":{
                              "lateRemissionType":"\
                        helpWithFees\
                        ",
                              "isNotificationTurnedOff":"\
                        false\
                        "
                           }\
                        }\
                        """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "lateRemissionType":"\
                            helpWithFees\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                        {
                           "Data":{
                           "journeyType":"\
                        aip\
                        ",
                              "remissionOption":"\
                        asylumSupportFromHo\
                        ",
                              "isNotificationTurnedOff":"\
                        false\
                        "
                           }\
                        }\
                        """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            asylumSupportFromHo\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            asylumSupport\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            asylumSupport\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            homeOfficeWaiver\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            homeOfficeWaiver\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "lateRemissionType":"\
                            exceptionalCircumstancesRemission\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "lateRemissionType":"\
                            exceptionalCircumstancesRemission\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            feeWaiverFromHo\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            feeWaiverFromHo\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            parentGetSupportFromLocalAuthority\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            parentGetSupportFromLocalAuthority\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            iWantToGetHelpWithFees\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                         "journeyType":"\
                                      aip\
                                      ",
                                            "remissionOption":"\
                                      noneOfTheseStatements\
                                      ",
                                            "helpWithFeesOption":"\
                                      wantToApply\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                         "journeyType":"\
                                      aip\
                                      ",
                                            "remissionOption":"\
                                      noneOfTheseStatements\
                                      ",
                                            "helpWithFeesOption":"\
                                      alreadyApplied\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                               "journeyType":"\
                            aip\
                            ",
                                  "remissionOption":"\
                            iWantToGetHelpWithFees\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            legalAid\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            legalAid\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            section20\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "remissionClaim":"\
                            section20\
                            ",
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      false\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appellantInDetention":"\
                            false\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                    mapAdditionalData("""
                            {
                               "Data":{
                                  "appellantInDetention":"\
                            true\
                            ",
                                  "isNotificationTurnedOff":"\
                            false\
                            "
                               }\
                            }\
                            """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "remissionDecision":"\
                                      partiallyApproved\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "remissionDecision":"\
                                      partiallyApproved\
                                      ",
                                            "paymentStatus":"\
                                      Paid\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
                emptyList()
            ),
            Arguments.of(
                "markAppealAsRemitted",
                "remitted",
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "ftpaAppellantRjDecisionOutcomeType":"\
                                      reheardRule35\
                                      ",
                                            "ftpaApplicantType":"\
                                      appellant\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "ftpaAppellantRjDecisionOutcomeType":"\
                                      reheardRule35\
                                      ",
                                            "ftpaApplicantType":"\
                                      appellant\
                                      ",
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "ftpaRespondentRjDecisionOutcomeType":"\
                                      reheardRule35\
                                      ",
                                            "ftpaApplicantType":"\
                                      respondent\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "ftpaRespondentRjDecisionOutcomeType":"\
                                      reheardRule35\
                                      ",
                                            "ftpaApplicantType":"\
                                      respondent\
                                      ",
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "updateTribunalDecisionList":"\
                                      underRule32\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "updateTribunalDecisionList":"\
                                      underRule32\
                                      ",
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isIntegrated":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                                "isIntegrated" : true,
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "hearingAdjournmentWhen":"onHearingDate",
                                            "relistCaseImmediately":true,
                                            "autoHearingRequestEnabled" : false,
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "hearingAdjournmentWhen":"onHearingDate",
                                            "relistCaseImmediately":true,
                                            "autoHearingRequestEnabled" : false,
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                                "isIntegrated" : true,
                                            "isDecisionWithoutHearing" : true,
                                            "autoHearingRequestEnabled" : false,
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                                "isIntegrated" : true,
                                            "autoHearingRequestEnabled" : false,
                                            "isDecisionWithoutHearing" : true,
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "shouldTriggerReviewInterpreterTask" : "true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "shouldTriggerReviewInterpreterTask" : "true",
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "feeUpdateTribunalAction":"\
                                      refund\
                                      ",
                                            "appellantInDetention":"\
                                      false\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "feeUpdateTribunalAction":"\
                                      refund\
                                      ",
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "feeUpdateTribunalAction":"\
                                      refund\
                                      ",
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"\
                                      true\
                                      ",
                                            "isNotificationTurnedOff":"\
                                      false\
                                      "
                                         }\
                                      }\
                                      """),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isDecisionRule31Changed":"true",
                                            "updateTribunalDecisionList":"underRule31",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isDecisionRule31Changed":"true",
                                            "updateTribunalDecisionList":"underRule31",
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "updateTribunalDecisionList":"underRule32",
                                            "appellantInDetention":"false",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "updateTribunalDecisionList":"underRule32",
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "appellantInDetention":"false",
                                            "isNotificationTurnedOff":"false",
                                            "lastModifiedApplication" : {
                                                  "type" : "Judge's review of application decision",
                                                  "decision" : "",
                                                  "applicant":"Respondent"
                                                }
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false",
                                            "lastModifiedApplication" : {
                                                  "type" : "Judge's review of application decision",
                                                  "decision" : "",
                                                  "applicant":"Respondent"
                                                }
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "isAdmin":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "isAdmin":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "isAdmin":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isAdmin":"true",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appealType":"protection"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appealType":"revocationOfProtection"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appealType":"deprivation"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appealType":"refusalOfHumanRights"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appealType":"refusalOfEu"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appealType":"euSettlementScheme"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"euSettlementScheme",
                                            "isNotificationTurnedOff":"false",
                                            "remissionType":"helpWithFees"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"refusalOfHumanRights",
                                            "isNotificationTurnedOff":"false",
                                            "remissionType":"exceptionalCircumstancesRemission"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"refusalOfEu",
                                            "isNotificationTurnedOff":"false",
                                            "remissionType":"helpWithFees"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"protection",
                                            "isNotificationTurnedOff":"false",
                                            "remissionClaim":"homeOfficeWaiver"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appealType":"euSettlementScheme",
                                            "isNotificationTurnedOff":"false",
                                            "remissionType":"exceptionalCircumstancesRemission"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "appellantInDetention":"true",
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "reviewedHearingRequirements":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "reviewedHearingRequirements":"false",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "reviewedHearingRequirements":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "reviewedHearingRequirements":"true",
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "appellantInDetention":"true"
                                         }\
                                      }"""),
                List.of(
                    Map.of(
                        "taskId", "detainedListCmr",
                        "name", "Detained - List CMR",
                        "processCategories", "caseProgression"
                    )
                )
            ),
            Arguments.of(
                "completeCaseReview",
                "appealSubmitted",
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "stf24wCurrentStatusAutoGenerated":true
                                         }\
                                      }\
                                      """),
                List.of(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",
                        "processCategories", "listTheCaseTask"
                    )
                )
            ),
            Arguments.of(
                "completeCaseReview",
                "listing",
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "stf24wCurrentStatusAutoGenerated":true
                                         }\
                                      }\
                                      """),
                List.of(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",
                        "processCategories", "listTheCaseTask"
                    )
                )
            ),
            Arguments.of(
                "sendToAdminForListing",
                "prepareForHearing",
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "stf24wCurrentStatusAutoGenerated":true
                                         }\
                                      }\
                                      """),
                List.of(
                    Map.of(
                        "taskId", "listTheCase",
                        "name", "List the case",
                        "processCategories", "listTheCaseTask"
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "isAdmin":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "isAdmin":"true"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "uploadHomeOfficeBundleAvailable":"false"
                                         }\
                                      }"""),
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
                mapAdditionalData("""
                                      {
                                         "Data":{
                                            "isNotificationTurnedOff":"false",
                                            "uploadHomeOfficeBundleAvailable":"true"
                                         }\
                                      }"""),
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

        assertEquals(dmnDecisionTableResult.getResultList(), expectation);
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
                                                               Map<String,Object> delayUntil,
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
        assertEquals(204, logic.getRules().size());
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

}
