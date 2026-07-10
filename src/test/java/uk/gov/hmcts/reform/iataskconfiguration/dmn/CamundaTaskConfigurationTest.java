package uk.gov.hmcts.reform.iataskconfiguration.dmn;

import lombok.Builder;
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
import org.junit.platform.commons.util.StringUtils;
import uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTableBaseUnitTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.iataskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_IA_ASYLUM;

class CamundaTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String DEFAULT_CALENDAR = "https://www.gov.uk/bank-holidays/england-and-wales.json";
    private static final String EXTRA_TEST_CALENDAR = "https://raw.githubusercontent.com/hmcts/"
        + "ia-task-configuration/master/src/test/resources/extra-non-working-day-calendar.json";
    private static final String CURRENT_DATE_TIME = LocalDateTime.now().toString();

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_IA_ASYLUM;
    }

    private static Stream<Scenario> nameAndValueScenarioProvider() {
        String dateOrigin = ZonedDateTime.now(ZoneId.of("UTC")).toString();
        Scenario givenCaseDataIsMissedThenDefaultToTaylorHouseScenario = Scenario.builder()
            .caseData(emptyMap())
            .expectedCaseNameValue(null)
            .expectedAppealTypeValue("")
            .expectedRegionValue("1")
            .expectedLocationValue("227101")
            .expectedLocationNameValue("Newport")
            .expectedCaseManagementCategoryValue("")
            .expectedDescriptionValue("")
            .expectedReconfigureValue("true")
            .expectedDueDateOrigin(dateOrigin)
            .build();

        String refusalOfEuLabel = "Refusal of a human rights claim";
        Scenario givenCaseDataIsPresentThenReturnNameAndValueScenario = Scenario.builder()
            .caseData(Map.of(
                "appealType", "refusalOfHumanRights",
                "appellantGivenNames", "some appellant given names",
                "appellantFamilyName", "some appellant family name",
                "caseManagementLocation", Map.of(
                    "region", "some other region",
                    "baseLocation", "some other location"
                ),
                "staffLocation", "some other location name",
                "caseManagementCategory", Map.of(
                    "value", Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"),
                    "list_items", List.of(Map.of("code", "refusalOfHumanRights", "label", refusalOfEuLabel))
                )
            ))
            .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
            .expectedCaseNameValue("some appellant given names some appellant family name")
            .expectedAppealTypeValue("Human rights")
            .expectedRegionValue("some other region")
            .expectedLocationValue("some other location")
            .expectedLocationNameValue("some other location name")
            .expectedCaseManagementCategoryValue("Human rights")
            .expectedWorkType("routine_work")
            .expectedReconfigureValue("true")
            .expectedRoleCategory("ADMIN")
            .expectedDescriptionValue(
                "[Mark the appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
            .expectedDueDateOrigin(dateOrigin)
            .build();

        Scenario givenSomeCaseDataAndArrangeOfflinePaymentTaskIdThenReturnExpectedNameAndValueScenario =
            Scenario.builder()
                .caseData(Map.of(
                    "appealType", "refusalOfHumanRights",
                    "appellantGivenNames", "some appellant given names",
                    "appellantFamilyName", "some appellant family name",
                    "caseManagementLocation", Map.of(
                        "region", "some other region",
                        "baseLocation", "some other location"
                    ),
                    "staffLocation", "some other location name",
                    "caseManagementCategory", Map.of(
                        "value", Map.of(
                            "code", "refusalOfHumanRights",
                            "label", "Refusal of a human rights claim"
                        ),
                        "list_items", List.of(Map.of(
                            "code", "refusalOfHumanRights",
                            "label", refusalOfEuLabel
                        ))
                    )
                ))
                .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
                .expectedCaseNameValue("some appellant given names some appellant family name")
                .expectedAppealTypeValue("Human rights")
                .expectedRegionValue("some other region")
                .expectedLocationValue("some other location")
                .expectedLocationNameValue("some other location name")
                .expectedCaseManagementCategoryValue("Human rights")
                .expectedReconfigureValue("true")
                .expectedWorkType("routine_work")
                .expectedRoleCategory("ADMIN")
                .expectedDescriptionValue("[Mark the appeal as "
                                              + "paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario givenSomeCaseDataAndTaskTypeIsEmptyThenExpectNoWorkTypeRuleScenario =
            Scenario.builder()
                .caseData(Map.of(
                    "appealType", "refusalOfHumanRights",
                    "appellantGivenNames", "some appellant given names",
                    "appellantFamilyName", "some appellant family name",
                    "caseManagementLocation", Map.of(
                        "region", "some other region",
                        "baseLocation", "some other location"
                    ),
                    "staffLocation", "some other location name",
                    "caseManagementCategory", Map.of(
                        "value", Map.of(
                            "code", "refusalOfHumanRights",
                            "label", "Refusal of a human rights claim"
                        ),
                        "list_items", List.of(Map.of(
                            "code", "refusalOfHumanRights",
                            "label", refusalOfEuLabel
                        ))
                    )
                ))
                .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
                .expectedCaseNameValue("some appellant given names some appellant family name")
                .expectedAppealTypeValue("Human rights")
                .expectedRegionValue("some other region")
                .expectedLocationValue("some other location")
                .expectedLocationNameValue("some other location name")
                .expectedCaseManagementCategoryValue("Human rights")
                .expectedWorkType("routine_work")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("ADMIN")
                .expectedDescriptionValue("[Mark the appeal as "
                                              + "paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario givenNoCaseDataAndSomeTaskTypeThenExpectOnlyTheWorkTypeRuleScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "markCaseAsPaid"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("routine_work")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("ADMIN")
                .expectedDescriptionValue("[Mark the appeal as "
                                              + "paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid)")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        Scenario processApplicationAdjournScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationAdjourn"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationAdjournScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationAdjourn"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedIsDetainedAppellant(true)
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationExpediteScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationExpedite"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationExpediteScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationExpedite"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationTimeExtensionScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationTimeExtension"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationTimeExtensionScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationTimeExtension"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedIsDetainedAppellant(true)
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationTransferScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationTransfer"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationTransferScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationTransfer"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationWithdrawScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationWithdraw"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationWithdrawScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationWithdraw"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationUpdateHearingRequirementsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationUpdateHearingRequirements"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationUpdateHearingRequirementsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationUpdateHearingRequirements"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationUpdateAppealDetailsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationUpdateAppealDetails"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationUpdateAppealDetailsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationUpdateAppealDetails"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationReinstateAnEndedAppealScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationReinstateAnEndedAppeal"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationReinstateAnEndedAppealScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationReinstateAnEndedAppeal"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationOtherScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationOther"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationOtherScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationOther"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario processApplicationLinkUnlinkAppealsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationLink/UnlinkAppeals"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationLinkUnlinkAppealsScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationLink/UnlinkAppeals"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedIsDetainedAppellant(true)
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();
        Scenario processApplicationChangeHearingTypeScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "processApplicationChangeHearingType"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        Scenario detainedProcessApplicationChangeHearingTypeScenario =
            Scenario.builder()
                .caseData(emptyMap())
                .taskAttributes(Map.of("taskType", "detainedProcessApplicationChangeHearingType"))
                .expectedCaseNameValue(null)
                .expectedAppealTypeValue("")
                .expectedRegionValue("1")
                .expectedLocationValue("227101")
                .expectedIsDetainedAppellant(true)
                .expectedLocationNameValue("Newport")
                .expectedCaseManagementCategoryValue("")
                .expectedWorkType("applications")
                .expectedReconfigureValue("true")
                .expectedRoleCategory("LEGAL_OPERATIONS")
                .expectedDescriptionValue("[Decide an application]"
                                              + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication)")
                .expectedDueDateOrigin(dateOrigin)
                .expectedDueDateIntervalDays("5")
                .build();

        return Stream.of(
            givenCaseDataIsMissedThenDefaultToTaylorHouseScenario,
            givenCaseDataIsPresentThenReturnNameAndValueScenario,
            givenSomeCaseDataAndArrangeOfflinePaymentTaskIdThenReturnExpectedNameAndValueScenario,
            givenSomeCaseDataAndTaskTypeIsEmptyThenExpectNoWorkTypeRuleScenario,
            givenNoCaseDataAndSomeTaskTypeThenExpectOnlyTheWorkTypeRuleScenario,
            processApplicationAdjournScenario,
            detainedProcessApplicationAdjournScenario,
            processApplicationExpediteScenario,
            processApplicationTimeExtensionScenario,
            detainedProcessApplicationTimeExtensionScenario,
            processApplicationTransferScenario,
            detainedProcessApplicationTransferScenario,
            processApplicationWithdrawScenario,
            detainedProcessApplicationWithdrawScenario,
            processApplicationUpdateHearingRequirementsScenario,
            detainedProcessApplicationUpdateHearingRequirementsScenario,
            processApplicationUpdateAppealDetailsScenario,
            detainedProcessApplicationUpdateAppealDetailsScenario,
            processApplicationReinstateAnEndedAppealScenario,
            detainedProcessApplicationReinstateAnEndedAppealScenario,
            processApplicationOtherScenario,
            detainedProcessApplicationOtherScenario,
            processApplicationLinkUnlinkAppealsScenario,
            detainedProcessApplicationLinkUnlinkAppealsScenario,
            processApplicationChangeHearingTypeScenario,
            detainedProcessApplicationChangeHearingTypeScenario
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertEquals(36, logic.getRules().size());
    }

    @SuppressWarnings("checkstyle:indentation")
    @ParameterizedTest
    @CsvSource(value = {
        "refusalOfHumanRights, Human rights",
        "refusalOfEu, EEA",
        "deprivation, DoC",
        "protection, Protection",
        "revocationOfProtection, Revocation",
        "NULL_VALUE, ''",
        "'', ''"
    }, nullValues = "NULL_VALUE")
    void when_caseData_then_return_expected_appealType(String appealType, String expectedAppealType) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>(); // allow null values
        caseData.put("appealType", appealType);
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "appealType",
            "value", expectedAppealType,
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "caseManagementCategory",
            "value", expectedAppealType,
            "canReconfigure", false
        )));
    }

    @Test
    void when_caseData_then_return_expected_case_management_category() {
        String refusalOfEuLabel = "Refusal of application under the EEA regulations";
        String revocationLabel = "Revocation of a protection status";
        List<Map<String, Object>> caseManagementCategories = List.of(
            Map.of(
                "value",
                Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"),
                "list_items",
                List.of(Map.of("code", "refusalOfHumanRights", "label", "Refusal of a human rights claim"))
            ),
            Map.of(
                "value", Map.of("code", "refusalOfEu", "label", "Refusal of application under the EEA regulations"),
                "list_items", List.of(Map.of("code", "refusalOfEu", "label", refusalOfEuLabel))
            ),
            Map.of(
                "value", Map.of("code", "deprivation", "label", "Deprivation of citizenship"),
                "list_items", List.of(Map.of("code", "deprivation", "label", "Deprivation of citizenship"))
            ),
            Map.of(
                "value", Map.of("code", "protection", "label", "Refusal of protection claim"),
                "list_items", List.of(Map.of("code", "protection", "label", "Refusal of protection claim"))
            ),
            Map.of(
                "value", Map.of("code", "revocationOfProtection", "label", "Revocation of a protection status"),
                "list_items", List.of(Map.of("code", "revocationOfProtection", "label", revocationLabel))
            )
        );

        List<String> expectedCaseManagementCategories = List.of(
            "Human rights",
            "EEA",
            "DoC",
            "Protection",
            "Revocation"
        );

        for (int i = 0; i < caseManagementCategories.size(); i++) {
            Map<String, Object> caseManagementCategory = caseManagementCategories.get(i);
            VariableMap inputVariables = new VariableMapImpl();
            Map<String, Object> caseData = new HashMap<>(); // allow null values
            caseData.put("caseManagementCategory", caseManagementCategory);
            inputVariables.putValue("caseData", caseData);

            DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

            assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
                "name", "caseManagementCategory",
                "value", expectedCaseManagementCategories.get(i),
                "canReconfigure", false
            )));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
        "false, false, false, false",
        "true, true, false, false",
        "true, true, true, true"
    })
    void nextHearingId_and_nextHearingDate_should_be_set_correctly(
        boolean caseDataSet, boolean nextHearingDetailsSet, boolean nextHearingIdAndDateSet, boolean expected) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = caseDataSet ? new HashMap<>() : null;
        Map<String, Object> nextHearingDetails = nextHearingDetailsSet ? new HashMap<>() : null;
        if (nextHearingIdAndDateSet && nextHearingDetailsSet) {
            nextHearingDetails.put("hearingID", "123Id");
            nextHearingDetails.put("hearingDateTime", CURRENT_DATE_TIME);
        }
        if (caseDataSet && nextHearingDetailsSet) {
            caseData.put("nextHearingDetails", nextHearingDetails);
        }
        if (caseDataSet) {
            inputVariables.putValue("caseData", caseData);
        }

        String nextHearingDate = expected ? CURRENT_DATE_TIME : "";
        String nextHearingId = expected ? "123Id" : "";

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingId",
            "value", nextHearingId,
            "canReconfigure", true
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "nextHearingDate",
            "value", nextHearingDate,
            "canReconfigure", true
        )));
    }

    private static VariableMap getInputVariables(String taskType, boolean is24w) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        Map<String, String> caseDataMap = new HashMap<>();
        if (is24w) {
            caseDataMap.put("stf24wCurrentStatusAutoGenerated", "Yes");
            caseDataMap.put("stf24wPreviousStatusWasYesAutoGenerated", "Yes");
        }
        if (taskType.equals("reviewMigratedCase") || taskType.equals("detainedReviewMigratedCase")) {
            caseDataMap.put("ariaMigrationTaskDueDays", "10");
        }
        inputVariables.putValue("caseData", caseDataMap);
        return inputVariables;
    }

    private static Arguments argumentsCreator(String taskType,
                                              List<Map<String, Object>> expectedResult,
                                              boolean is24w) {
        return Arguments.of(taskType, expectedResult, getInputVariables(taskType, is24w));
    }

    private static Stream<Arguments> workTypeScenarioProviderGetter(boolean is24w) {
        String prefix = is24w ? "stf_24w_" : "";
        List<Map<String, Object>> routineWork = List.of(Map.of(
            "name", "workType",
            "value", prefix + "routine_work",
            "canReconfigure", true
        ));
        List<Map<String, Object>> decisionMakingWork = List.of(Map.of(
            "name", "workType",
            "value", prefix + "decision_making_work",
            "canReconfigure", true
        ));
        List<Map<String, Object>> hearingWork = List.of(Map.of(
            "name", "workType",
            "value", prefix + "hearing_work",
            "canReconfigure", true
        ));
        List<Map<String, Object>> applications = List.of(Map.of(
            "name", "workType",
            "value", prefix + "applications",
            "canReconfigure", true
        ));
        List<Map<String, Object>> upperTribunal = List.of(Map.of(
            "name", "workType",
            "value", prefix + "upper_tribunal",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewCase = List.of(Map.of(
            "name", "workType",
            "value", prefix + "review_case",
            "canReconfigure", true
        ));

        return Stream.of(
            argumentsCreator("arrangeOfflinePayment", routineWork, is24w),
            argumentsCreator("markCaseAsPaid", routineWork, is24w),
            argumentsCreator("attendCma", routineWork, is24w),
            argumentsCreator("caseSummaryHearingBundleStartDecision", routineWork, is24w),
            argumentsCreator("detainedCaseSummaryHearingBundleStartDecision", routineWork, is24w),
            argumentsCreator("followUpExtendedDirection", routineWork, is24w),
            argumentsCreator("detainedFollowUpExtendedDirection", routineWork, is24w),
            argumentsCreator("followUpNonStandardDirection", routineWork, is24w),
            argumentsCreator("detainedFollowUpNonStandardDirection", routineWork, is24w),
            argumentsCreator("reviewClarifyingQuestionsAnswers", routineWork, is24w),
            argumentsCreator("reviewRemissionApplication", routineWork, is24w),
            argumentsCreator("reviewASRemission", routineWork, is24w),
            argumentsCreator("reviewLARemission", routineWork, is24w),
            argumentsCreator("reviewHOWaiverRemission", routineWork, is24w),
            argumentsCreator("reviewAuthorityRemission", routineWork, is24w),
            argumentsCreator("reviewHWFRemission", routineWork, is24w),
            argumentsCreator("reviewECRRemission", routineWork, is24w),
            argumentsCreator("detainedReviewASRemission", routineWork, is24w),
            argumentsCreator("detainedReviewLARemission", routineWork, is24w),
            argumentsCreator("detainedReviewHOWaiverRemission", routineWork, is24w),
            argumentsCreator("detainedReviewAuthorityRemission", routineWork, is24w),
            argumentsCreator("detainedReviewHWFRemission", routineWork, is24w),
            argumentsCreator("detainedReviewECRRemission", routineWork, is24w),
            argumentsCreator("assignAFTPAJudge", routineWork, is24w),
            argumentsCreator("detainedAssignAFTPAJudge", routineWork, is24w),
            argumentsCreator("reviewAppealSetAsideUnderRule35", routineWork, is24w),
            argumentsCreator("detainedReviewAppealSetAsideUnderRule35", routineWork, is24w),
            argumentsCreator("reviewAppealSetAsideUnderRule32", routineWork, is24w),
            argumentsCreator("detainedReviewAppealSetAsideUnderRule32", routineWork, is24w),
            argumentsCreator("sendPaymentRequest", routineWork, is24w),
            argumentsCreator("markAsPaid", routineWork, is24w),
            argumentsCreator("reviewRemittedAppeal", routineWork, is24w),
            argumentsCreator("detainedReviewRemittedAppeal", routineWork, is24w),
            argumentsCreator("reviewAriaRemissionApplication", routineWork, is24w),
            argumentsCreator("reviewDraftAppeal", routineWork, is24w),
            argumentsCreator("detainedReviewDraftAppeal", routineWork, is24w),
            argumentsCreator("printAndSendHoBundle", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendHoBundle", routineWork, is24w),
            argumentsCreator("printAndSendHoResponse", routineWork, is24w),
            argumentsCreator("printAndSendHearingRequirements", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendHearingRequirements", routineWork, is24w),
            argumentsCreator("printAndSendHearingBundle", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendHearingBundle", routineWork, is24w),
            argumentsCreator("printAndSendDecisionCorrectedRule31", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendDecisionCorrectedRule31", routineWork, is24w),
            argumentsCreator("printAndSendDecisionCorrectedRule32", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendDecisionCorrectedRule32", routineWork, is24w),
            argumentsCreator("printAndSendHoApplication", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendHoApplication", routineWork, is24w),
            argumentsCreator("printAndSendHoEvidence", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendHoEvidence", routineWork, is24w),
            argumentsCreator("printAndSendAppealDecision", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendAppealDecision", routineWork, is24w),
            argumentsCreator("printAndSendFTPADecision", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendFTPADecision", routineWork, is24w),
            argumentsCreator("printAndSendReheardHearingRequirements", routineWork, is24w),
            argumentsCreator("detainedPrintAndSendReheardHearingRequirements", routineWork, is24w),
            argumentsCreator("processFeeRefund", routineWork, is24w),
            argumentsCreator("detainedProcessFeeRefund", routineWork, is24w),
            argumentsCreator("reviewAdditionalEvidence", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewAdditionalEvidence", decisionMakingWork, is24w),
            argumentsCreator("reviewTheAppeal", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewTheAppeal", decisionMakingWork, is24w),
            argumentsCreator("followUpOverdueRespondentEvidence", decisionMakingWork, is24w),
            argumentsCreator("detainedFollowUpOverdueRespondentEvidence", decisionMakingWork, is24w),
            argumentsCreator("reviewRespondentEvidence", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewRespondentEvidence", decisionMakingWork, is24w),
            argumentsCreator("followUpOverdueCaseBuilding", decisionMakingWork, is24w),
            argumentsCreator("detainedFollowUpOverdueCaseBuilding", decisionMakingWork, is24w),
            argumentsCreator("reviewAppealSkeletonArgument", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewAppealSkeletonArgument", decisionMakingWork, is24w),
            argumentsCreator("followUpOverdueReasonsForAppeal", decisionMakingWork, is24w),
            argumentsCreator("reviewReasonsForAppeal", decisionMakingWork, is24w),
            argumentsCreator("followUpOverdueClarifyingAnswers", decisionMakingWork, is24w),
            argumentsCreator("reviewClarifyingAnswers", decisionMakingWork, is24w),
            argumentsCreator("followUpOverdueRespondentReview", decisionMakingWork, is24w),
            argumentsCreator("detainedFollowUpOverdueRespondentReview", decisionMakingWork, is24w),
            argumentsCreator("reviewRespondentResponse", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewRespondentResponse", decisionMakingWork, is24w),
            argumentsCreator("followUpOverdueCMARequirements", decisionMakingWork, is24w),
            argumentsCreator("reviewCmaRequirements", decisionMakingWork, is24w),
            argumentsCreator("reviewAdditionalHomeOfficeEvidence", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewAdditionalHomeOfficeEvidence", decisionMakingWork, is24w),
            argumentsCreator("reviewAddendumHomeOfficeEvidence", decisionMakingWork, is24w),
            argumentsCreator("reviewAddendumEvidence", decisionMakingWork, is24w),
            argumentsCreator("detainedReviewAddendumEvidence", decisionMakingWork, is24w),
            argumentsCreator("decideOnTimeExtension", decisionMakingWork, is24w),
            argumentsCreator("sendDecisionsAndReasons", decisionMakingWork, is24w),
            argumentsCreator("detainedSendDecisionsAndReasons", decisionMakingWork, is24w),
            argumentsCreator("generateDraftDecisionAndReasons", hearingWork, is24w),
            argumentsCreator("uploadDecision", hearingWork, is24w),
            argumentsCreator("uploadHearingRecording", hearingWork, is24w),
            argumentsCreator("postHearingAttendeesDurationAndRecording", hearingWork, is24w),
            argumentsCreator("detainedPostHearingAttendeesDurationAndRecording", hearingWork, is24w),
            argumentsCreator("editListing", hearingWork, is24w),
            argumentsCreator("detainedEditListing", hearingWork, is24w),
            argumentsCreator("followUpOverdueHearingRequirements", hearingWork, is24w),
            argumentsCreator("detainedFollowUpOverdueHearingRequirements", hearingWork, is24w),
            argumentsCreator("reviewHearingRequirements", hearingWork, is24w),
            argumentsCreator("detainedReviewHearingRequirements", hearingWork, is24w),
            argumentsCreator("allocateHearingJudge", hearingWork, is24w),
            argumentsCreator("detainedAllocateHearingJudge", hearingWork, is24w),
            argumentsCreator("prepareDecisionsAndReasons", hearingWork, is24w),
            argumentsCreator("startDecisionsAndReasonsDocument", hearingWork, is24w),
            argumentsCreator("createHearingBundle", hearingWork, is24w),
            argumentsCreator("createCaseSummary", hearingWork, is24w),
            argumentsCreator("listTheCase", hearingWork, is24w),
            argumentsCreator("detainedListTheCase", hearingWork, is24w),
            argumentsCreator("hearingException", hearingWork, is24w),
            argumentsCreator("detainedHearingException", hearingWork, is24w),
            argumentsCreator("cmrListed", hearingWork, is24w),
            argumentsCreator("detainedCmrListed", hearingWork, is24w),
            argumentsCreator("cmrUpdated", hearingWork, is24w),
            argumentsCreator("detainedCmrUpdated", hearingWork, is24w),
            argumentsCreator("relistCase", hearingWork, is24w),
            argumentsCreator("detainedRelistCase", hearingWork, is24w),
            argumentsCreator("reviewInterpreters", hearingWork, is24w),
            argumentsCreator("detainedReviewInterpreters", hearingWork, is24w),
            argumentsCreator("processApplicationAdjourn", applications, is24w),
            argumentsCreator("detainedProcessApplicationAdjourn", applications, is24w),
            argumentsCreator("processApplicationExpedite", applications, is24w),
            argumentsCreator("detainedProcessApplicationExpedite", applications, is24w),
            argumentsCreator("processApplicationTimeExtension", applications, is24w),
            argumentsCreator("detainedProcessApplicationTimeExtension", applications, is24w),
            argumentsCreator("processApplicationTransfer", applications, is24w),
            argumentsCreator("processApplicationWithdraw", applications, is24w),
            argumentsCreator("detainedProcessApplicationWithdraw", applications, is24w),
            argumentsCreator("processApplicationUpdateHearingRequirements", applications, is24w),
            argumentsCreator("detainedProcessApplicationUpdateHearingRequirements", applications, is24w),
            argumentsCreator("processApplicationUpdateAppealDetails", applications, is24w),
            argumentsCreator("detainedProcessApplicationUpdateAppealDetails", applications, is24w),
            argumentsCreator("processApplicationReinstateAnEndedAppeal", applications, is24w),
            argumentsCreator("detainedProcessApplicationReinstateAnEndedAppeal", applications, is24w),
            argumentsCreator("processApplicationOther", applications, is24w),
            argumentsCreator("detainedProcessApplicationOther", applications, is24w),
            argumentsCreator("processApplicationLink/UnlinkAppeals", applications, is24w),
            argumentsCreator("detainedProcessApplicationLink/UnlinkAppeals", applications, is24w),
            argumentsCreator("processHearingRequirementsApplication", applications, is24w),
            argumentsCreator("processHearingCentreApplication", applications, is24w),
            argumentsCreator("processApplicationExpedite", applications, is24w),
            argumentsCreator("detainedProcessApplicationExpedite", applications, is24w),
            argumentsCreator("processApplicationTransfer", applications, is24w),
            argumentsCreator("detainedProcessApplicationTransfer", applications, is24w),
            argumentsCreator("processApplicationForTimeExtension", applications, is24w),
            argumentsCreator("processAppealDetailsApplication", applications, is24w),
            argumentsCreator("processReinstatementApplication", applications, is24w),
            argumentsCreator("processApplicationToReviewDecision", applications, is24w),
            argumentsCreator("detainedProcessApplicationToReviewDecision", applications, is24w),
            argumentsCreator("reviewSetAsideDecisionApplication", applications, is24w),
            argumentsCreator("detainedReviewSetAsideDecisionApplication", applications, is24w),
            argumentsCreator("followUpSetAsideDecision", applications, is24w),
            argumentsCreator("detainedFollowUpSetAsideDecision", applications, is24w),
            argumentsCreator("decideAnFTPA", upperTribunal, is24w),
            argumentsCreator("processApplicationChangeHearingType", applications, is24w),
            argumentsCreator("detainedProcessApplicationChangeHearingType", applications, is24w),
            argumentsCreator("reviewMigratedCase", reviewCase, is24w),
            argumentsCreator("detainedListCmr", hearingWork, is24w)

        );
    }

    public static Stream<Arguments> workTypeScenarioProvider() {
        return workTypeScenarioProviderGetter(false);
    }

    public static Stream<Arguments> workType24wScenarioProvider() {
        return workTypeScenarioProviderGetter(true);
    }

    @ParameterizedTest
    @CsvSource({
        "followUpNoticeOfChange", "detainedFollowUpNoticeOfChange"
    })
    void when_taskId_then_return_Access_Requests(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("workType"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "workType",
                "value", "access_requests",
                "canReconfigure", true
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "reviewSpecificAccessRequestJudiciary", "reviewSpecificAccessRequestLegalOps",
        "reviewSpecificAccessRequestAdmin", "reviewSpecificAccessRequestCTSC"
    })
    void when_taskId_then_return_Access_requests(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();
        Map<String, String> taskAttributes = Map.of("taskType", taskType, "roleAssignmentId", roleAssignmentId);
        inputVariables.putValue("taskAttributes", taskAttributes);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("workType"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "workType",
                "value", "access_requests",
                "canReconfigure", true
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @MethodSource({"workTypeScenarioProvider", "workType24wScenarioProvider"})
    void when_taskId_then_return_workType(String taskType,
                                          List<Map<String, String>> expected,
                                          VariableMap inputVariables) {
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("workType"))
            .toList();

        assertEquals(expected, workTypeResultList);
    }

    @ParameterizedTest
    @CsvSource({
        "reviewSpecificAccessRequestJudiciary", "reviewSpecificAccessRequestLegalOps",
        "reviewSpecificAccessRequestAdmin", "reviewSpecificAccessRequestCTSC"
    })
    void should_return_request_value_when_role_assignment_id_exists_in_task_attributes(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();

        Map<String, String> taskAttributes = Map.of("taskType", taskType, "roleAssignmentId", roleAssignmentId);
        inputVariables.putValue("taskAttributes", taskAttributes);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dmnResults = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("additionalProperties_roleAssignmentId"))
            .toList();

        assertEquals(1, dmnResults.size());

        assertTrue(dmnResults.contains(Map.of(
            "name", "additionalProperties_roleAssignmentId",
            "value", roleAssignmentId,
            "canReconfigure", false
        )));

    }

    @ParameterizedTest
    @CsvSource({
        "generateDraftDecisionAndReasons", "uploadDecision", "reviewAddendumHomeOfficeEvidence",
        "reviewAddendumAppellantEvidence", "reviewSpecificAccessRequestJudiciary",
        "reviewSpecificAccessRequestLegalOps", "reviewSpecificAccessRequestAdmin", "reviewSpecificAccessRequestCTSC",
        "processApplicationToReviewDecision", "detainedProcessApplicationToReviewDecision",
        "sendDecisionsAndReasons", "detainedSendDecisionsAndReasons", "prepareDecisionsAndReasons", "decideAnFTPA",
        "reviewSetAsideDecisionApplication", "detainedReviewSetAsideDecisionApplication"
    })
    void when_taskId_then_return_judicial_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();
        inputVariables.putValue(
            "taskAttributes", Map.of(
                "taskType", taskType,
                "additionalProperties", Map.of("roleAssignmentId", roleAssignmentId)
            )
        );

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter(r -> "roleCategory".equals(r.get("name")))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "roleCategory",
                "value", "JUDICIAL",
                "canReconfigure", false
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "arrangeOfflinePayment", "markCaseAsPaid", "allocateHearingJudge", "detainedAllocateHearingJudge",
        "uploadHearingRecording", "postHearingAttendeesDurationAndRecording",
        "detainedPostHearingAttendeesDurationAndRecording",
        "editListing", "detainedEditListing", "followUpSetAsideDecision", "detainedFollowUpSetAsideDecision",
        "hearingException", "detainedHearingException",
        "cmrListed", "cmrUpdated", "detainedCmrListed", "detainedCmrUpdated",
        "relistCase", "detainedRelistCase",
        "reviewInterpreters", "detainedReviewInterpreters",
        "reviewMigratedCase", "detainedReviewMigratedCase",
        "reviewAriaRemissionApplication",
        "printAndSendHoBundle", "detainedPrintAndSendHoBundle",
        "printAndSendHoResponse",
        "printAndSendHearingRequirements", "detainedPrintAndSendHearingRequirements",
        "printAndSendHearingBundle", "detainedPrintAndSendHearingBundle",
        "printAndSendDecisionCorrectedRule31", "detainedPrintAndSendDecisionCorrectedRule31",
        "printAndSendDecisionCorrectedRule32", "detainedPrintAndSendDecisionCorrectedRule32",
        "printAndSendHoApplication", "detainedPrintAndSendHoApplication",
        "printAndSendHoEvidence", "detainedPrintAndSendHoEvidence",
        "printAndSendAppealDecision", "detainedPrintAndSendAppealDecision",
        "printAndSendFTPADecision", "detainedPrintAndSendFTPADecision",
        "printAndSendReheardHearingRequirements", "detainedPrintAndSendReheardHearingRequirements",
        "detainedListCmr"

    })
    void when_taskId_then_return_Admin_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        if (taskType.equals("reviewMigratedCase") || taskType.equals("detainedReviewMigratedCase")) {
            inputVariables.putValue("caseData", Map.of("ariaMigrationTaskDueDays", "10"));
        }

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "roleCategory",
                "value", "ADMIN",
                "canReconfigure", false
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "reviewRemissionApplication", "reviewASRemission",
        "reviewLARemission", "reviewHOWaiverRemission",
        "reviewAuthorityRemission", "reviewHWFRemission",
        "reviewECRRemission", "detainedReviewASRemission",
        "detainedReviewLARemission", "detainedReviewHOWaiverRemission",
        "detainedReviewAuthorityRemission", "detainedReviewHWFRemission",
        "detainedReviewECRRemission",
        "assignAFTPAJudge", "detainedAssignAFTPAJudge",
        "detainedListTheCase", "listTheCase",
        "sendPaymentRequest", "markAsPaid",
        "processFeeRefund", "detainedProcessFeeRefund",
        "reviewDraftAppeal", "detainedReviewDraftAppeal"
    })
    void when_taskId_then_return_Ctsc_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "roleCategory",
                "value", "CTSC",
                "canReconfigure", false
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "processApplicationAdjourn", "detainedProcessApplicationAdjourn", "processApplicationExpedite",
        "detainedProcessApplicationExpedite", "processApplicationTimeExtension",
        "detainedProcessApplicationTimeExtension",
        "processApplicationTransfer", "detainedProcessApplicationTransfer", "processApplicationWithdraw",
        "detainedProcessApplicationWithdraw", "processApplicationUpdateHearingRequirements",
        "detainedProcessApplicationUpdateHearingRequirements",
        "processApplicationUpdateAppealDetails", "detainedProcessApplicationUpdateAppealDetails",
        "processApplicationReinstateAnEndedAppeal", "detainedProcessApplicationReinstateAnEndedAppeal",
        "processApplicationOther",
        "detainedProcessApplicationOther",
        "processApplicationLink/UnlinkAppeals", "detainedProcessApplicationLink/UnlinkAppeals",
        "processApplicationChangeHearingType", "detainedProcessApplicationChangeHearingType", "reviewTheAppeal",
        "detainedReviewTheAppeal",
        "decideOnTimeExtension", "reviewRespondentEvidence",
        "detainedReviewRespondentEvidence,"
            + "[Request reasons for appeal](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestReasonsForAppeal)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),"
            + "aip,,",
        "reviewAppealSkeletonArgument", "detainedReviewAppealSkeletonArgument", "reviewReasonsForAppeal",
        "reviewClarifyingQuestionsAnswers", "reviewAdditionalHomeOfficeEvidence",
        "reviewCmaRequirements", "attendCma", "reviewRespondentResponse", "detainedReviewRespondentResponse",
        "caseSummaryHearingBundleStartDecision",
        "detainedCaseSummaryHearingBundleStartDecision",
        "reviewHearingRequirements", "detainedReviewHearingRequirements", "followUpOverdueRespondentEvidence",
        "detainedFollowUpOverdueRespondentEvidence",
        "followUpOverdueCaseBuilding", "detainedFollowUpOverdueCaseBuilding", "followUpOverdueReasonsForAppeal",
        "followUpOverdueClarifyingAnswers",
        "followUpOverdueCmaRequirements", "followUpOverdueRespondentReview", "detainedFollowUpOverdueRespondentReview",
        "followUpOverdueHearingRequirements", "detainedFollowUpOverdueHearingRequirements",
        "followUpNonStandardDirection", "detainedFollowUpNonStandardDirection", "followUpNoticeOfChange",
        "detainedFollowUpNoticeOfChange",
        "reviewAdditionalEvidence", "detainedReviewAdditionalEvidence", "reviewAdditionalHomeOfficeEvidence",
        "detainedReviewAdditionalHomeOfficeEvidence",
        "reviewRemittedAppeal", "detainedReviewRemittedAppeal",
        "reviewAppealSetAsideUnderRule35", "detainedReviewAppealSetAsideUnderRule35",
        "reviewAppealSetAsideUnderRule32", "detainedReviewAppealSetAsideUnderRule32"
    })
    void when_taskId_then_return_legal_operations_role_category(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "roleCategory",
                "value", "LEGAL_OPERATIONS",
                "canReconfigure", false
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "followUpExtendedDirection", "detainedFollowUpExtendedDirection",
        "createHearingBundle", "createCaseSummary", "reviewAddendumEvidence", "detainedReviewAddendumEvidence"
    })
    void when_taskId_then_return_legal_operations_role_category_can_reconfigure(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("roleCategory"))
            .toList();

        assertEquals(1, workTypeResultList.size());

        assertEquals(
            Map.of(
                "name", "roleCategory",
                "value", "LEGAL_OPERATIONS",
                "canReconfigure", true
            ), workTypeResultList.getFirst()
        );
    }

    @ParameterizedTest
    @MethodSource("nameAndValueScenarioProvider")
    void when_caseData_and_taskType_then_return_expected_name_and_value_rows(Scenario scenario) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", scenario.caseData);
        inputVariables.putValue("taskAttributes", scenario.taskAttributes());

        List<Map<String, Object>> expected = getExpectedValues(scenario);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertEquals(expected.size(), dmnDecisionTableResult.getResultList().size());
        for (int index = 0; index < expected.size(); index++) {
            if ("dueDateOrigin".equals(expected.get(index).get("name"))) {
                assertTrue(validNow(
                    ZonedDateTime.parse(expected.get(index).get("value").toString()),
                    parseCamundaTimestamp(dmnDecisionTableResult.getResultList().get(index).get("value").toString())
                ));
            } else {
                assertEquals(expected.get(index), dmnDecisionTableResult.getResultList().get(index));
            }
        }
    }

    private List<Map<String, Object>> getExpectedValues(Scenario scenario) {
        List<Map<String, Object>> rules = new ArrayList<>();

        getExpectedValueWithReconfigure(
            rules,
            "caseName",
            scenario.expectedCaseNameValue(),
            scenario.expectedReconfigureValue()
        );
        getExpectedValue(rules, "appealType", scenario.expectedAppealTypeValue());
        getExpectedValue(rules, "region", scenario.expectedRegionValue());
        getExpectedValueWithReconfigure(
            rules,
            "location",
            scenario.expectedLocationValue(),
            scenario.expectedReconfigureValue()
        );
        getExpectedValueWithReconfigure(
            rules,
            "locationName",
            scenario.expectedLocationNameValue(),
            scenario.expectedReconfigureValue()
        );
        getExpectedValue(rules, "caseManagementCategory", scenario.expectedCaseManagementCategoryValue());
        if (!Objects.isNull(scenario.taskAttributes())
            && StringUtils.isNotBlank(scenario.taskAttributes.get("taskType").toString())) {
            getExpectedValueWithReconfigure(rules, "workType", scenario.expectedWorkType(), "true");
            getExpectedValue(rules, "roleCategory", scenario.expectedRoleCategory());
        }

        getExpectedValue(rules, "description", scenario.expectedDescriptionValue());
        getExpectedValue(rules, "dueDateOrigin", scenario.expectedDueDateOrigin());
        getExpectedValue(rules, "dueDateNonWorkingCalendar", DEFAULT_CALENDAR + ", " + EXTRA_TEST_CALENDAR);
        if (!Objects.isNull(scenario.expectedDueDateIntervalDays())) {
            getExpectedValue(rules, "dueDateIntervalDays", scenario.expectedDueDateIntervalDays());
        }
        getExpectedValueWithReconfigure(rules, "majorPriority", String.valueOf(5000), "true");
        getExpectedValue(rules, "minorPriority", String.valueOf(500));
        getExpectedValue(rules, "priorityDateOriginRef", "dueDate");
        getExpectedValue(rules, "dueDateNonWorkingDaysOfWeek", "SATURDAY,SUNDAY");
        getExpectedValue(rules, "calculatedDates", "nextHearingDate,dueDate,priorityDate");
        getExpectedValueWithReconfigure(
            rules,
            "nextHearingId",
            "",
            "true"
        );
        getExpectedValueWithReconfigure(
            rules,
            "nextHearingDate",
            "",
            "true"
        );
        return rules;
    }

    @ParameterizedTest
    @CsvSource({
        "processApplicationAdjourn,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationAdjourn,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationExpedite,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationTimeExtension,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationTimeExtension,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationTransfer,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationTransfer,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationWithdraw,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationWithdraw,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationUpdateHearingRequirements,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationUpdateHearingRequirements,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationUpdateAppealDetails,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationUpdateAppealDetails,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationReinstateAnEndedAppeal,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationReinstateAnEndedAppeal,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationOther,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationOther,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationLink/UnlinkAppeals,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "detainedProcessApplicationLink/UnlinkAppeals,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "processApplicationChangeHearingType,"
            + "[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideAnApplication),,,",
        "reviewTheAppeal,[Request respondent evidence]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentEvidence),,,",
        "detainedReviewTheAppeal,[Request respondent evidence]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentEvidence),,,",
        "decideOnTimeExtension,"
            + "[Change the direction due date](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/changeDirectionDueDate),,,",
        "reviewRespondentEvidence,"
            + "[Request reasons for appeal](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestReasonsForAppeal)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),"
            + "aip,,",
        "detainedReviewRespondentEvidence,"
            + "[Request reasons for appeal](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestReasonsForAppeal)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),"
            + "aip,,",
        "reviewRespondentEvidence,"
            + "[Request case building](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCaseBuilding)<br />"
            + "[Send non-standard direction](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirection),,,",
        "reviewAppealSkeletonArgument,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Request case edit](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCaseEdit),,,",
        "detainedReviewAppealSkeletonArgument,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Request case edit](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCaseEdit),,,",
        "reviewReasonsForAppeal,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Send direction with questions]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/sendDirectionWithQuestions),"
            + "aip,,",
        "reviewReasonsForAppeal,"
            + "[Request respondent review](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestRespondentReview)<br />"
            + "[Request CMA requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/requestCmaRequirements),,,",
        "reviewCmaRequirements,"
            + "[Review CMA Requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/reviewCmaRequirements),,,",
        "attendCma,"
            + "[Update case details after CMA](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/updateDetailsAfterCma),,,",
        "reviewRespondentResponse,"
            + "[Review Home Office response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseReview)<br />[Amend appeal response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseAmend),,,",
        "detainedReviewRespondentResponse,"
            + "[Force case - hearing reqs](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/forceCaseToSubmitHearingRequirements)<br />"
            + "[Amend appeal response](/case/IA/Asylum/${[CASE_REFERENCE]}/"
            + "trigger/requestResponseAmend),,,",
        "createHearingBundle,"
            + "[Generate the hearing bundle](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger"
            + "/generateHearingBundle)<br />"
            + "[Customise the hearing bundle](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/customiseHearingBundle),,,",
        "startDecisionsAndReasonsDocument,"
            + "[Start decision and reasons document](/case/IA/Asylum/${[CASE_REFERENCE]}"
            + "/trigger/decisionAndReasonsStarted/decisionAndReasonsStartedcaseIntroduction),,,",
        "reviewHearingRequirements,"
            + "[Review hearing requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger"
            + "/reviewHearingRequirements),,,",
        "detainedReviewHearingRequirements,"
            + "[Review hearing requirements](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger"
            + "/reviewHearingRequirements),,,",
        "reviewAdditionalEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "detainedReviewAdditionalEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "reviewAdditionalHomeOfficeEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "detainedReviewAdditionalHomeOfficeEvidence,"
            + "[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markEvidence"
            + "AsReviewed),,,",
        "arrangeOfflinePayment,[Mark the appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markAppealPaid),,,",
        "markCaseAsPaid,[Mark the appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid),,,",
        "allocateHearingJudge," + "[Allocate Hearing Judge](/role-access/allocate-role/allocate?caseId="
            + "${[CASE_REFERENCE]}&roleCategory=JUDICIAL&jurisdiction=IA),,,",
        "detainedAllocateHearingJudge," + "[Allocate Hearing Judge](/role-access/allocate-role/allocate?caseId="
            + "${[CASE_REFERENCE]}&roleCategory=JUDICIAL&jurisdiction=IA),,,",
        "uploadHearingRecording,[Upload the hearing recording](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/upload"
            + "HearingRecording),,,",
        "generateDraftDecisionAndReasons,[Generate the draft decisions and reasons document](/case/IA/Asylum"
            + "/${[CASE_REFERENCE]}/trigger/generateDecisionAndReasons),,,",
        "reviewAddendumEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markAddendumEvidenceAsReviewed),,,",
        "detainedReviewAddendumEvidence,[Review evidence](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markAddendumEvidenceAsReviewed),,,",
        "editListing,[Edit case listing](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/editCaseListing),,,",
        "detainedEditListing,[Edit case listing](/case/IA/Asylum/${[CASE_REFERENCE]}"
            + "/trigger/editCaseListing),,,",
        "decideAnFTPA,[Decide FTPA application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/decideFtpaApplication),,,",
        "detainedDecideAnFTPA,[Decide FTPA application](/case/IA/Asylum/${[CASE_REFERENCE]}"
            + "/trigger/decideFtpaApplication),,,",
        "prepareDecisionsAndReasons,[Prepare decisions and reasons](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "generateDecisionAndReasons),,,",
        "sendDecisionsAndReasons,[Complete decision and reasons](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "sendDecisionAndReasons),,,",
        "detainedSendDecisionsAndReasons,[Complete decision and reasons](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "sendDecisionAndReasons),,,",
        "processApplicationToReviewDecision,[Decide an application](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "decideAnApplication),,,",
        "detainedProcessApplicationToReviewDecision,[Decide an application]"
            + "(/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "decideAnApplication),,,",
        "reviewRemissionApplication,[Record remission decision](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "reviewASRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "reviewLARemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "reviewHOWaiverRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "reviewAuthorityRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "reviewHWFRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "reviewECRRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewASRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewLARemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewHOWaiverRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewAuthorityRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewHWFRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "detainedReviewECRRemission,[Record remission decision]"
            + "(/cases/case-details/${[CASE_REFERENCE]}/trigger/"
            + "recordRemissionDecision/recordRemissionDecisionremissionDecision),,,",
        "assignAFTPAJudge,[Record allocated Judge](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "recordAllocatedJudge),,,",
        "detainedAssignAFTPAJudge,[Record allocated Judge](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "recordAllocatedJudge),,,",
        "listTheCase,[List the case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/listCase),,No,",
        "listTheCase,[List the case](cases/case-details/${[CASE_REFERENCE]}/hearings),,Yes,",
        "detainedListTheCase,[List the case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/listCase),,No,",
        "detainedListTheCase,[List the case](cases/case-details/${[CASE_REFERENCE]}/hearings),,Yes,",
        "sendPaymentRequest,[Mark payment request sent](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "markPaymentRequestSent),,,",
        "markAsPaid,[Mark appeal as paid](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/markAppealPaid),,,",
        "reviewMigratedCase,[Progress migrated case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "progressMigratedCase),,,10",
        "detainedReviewMigratedCase,[Progress migrated case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/"
            + "progressMigratedCase),,,10",
        "hearingException,[Go to case](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedHearingException,[Go to case](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "cmrListed,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedCmrListed,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "cmrUpdated,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedCmrUpdated,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "relistCase,[Relist the hearing](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedRelistCase,[Relist the hearing](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "reviewInterpreters,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedReviewInterpreters,[View the Hearings](cases/case-details/${[CASE_REFERENCE]}/hearings),,,",
        "detainedListCmr,[List CMR](cases/case-details/${[CASE_REFERENCE]}/hearings),,,"

    })
    void should_return_a_200_description_property(String taskType, String expectedDescription, String journeyType,
                                                  String isIntegrated, String ariaTaskDueDays) {
        VariableMap inputVariables = new VariableMapImpl();

        String roleAssignmentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        Map<String, String> taskAttributes = Map.of(
            "taskType", taskType,
            "roleAssignmentId", roleAssignmentId,
            "taskId", taskId
        );
        inputVariables.putValue("taskAttributes", taskAttributes);
        if (journeyType != null) {
            inputVariables.putValue("caseData", Map.of("journeyType", journeyType));
        }
        if (isIntegrated != null) {
            inputVariables.putValue("caseData", Map.of("isIntegrated", isIntegrated));
        }
        if (ariaTaskDueDays != null) {
            inputVariables.putValue("caseData", Map.of("ariaMigrationTaskDueDays", ariaTaskDueDays));
        }

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> descriptionList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("description"))
            .toList();

        assertEquals(1, descriptionList.size());

        assertEquals(
            Map.of(
                "name", "description",
                "value", expectedDescription
                    .replace("${[roleAssignmentId]}", roleAssignmentId).replace("${[taskId]}", taskId),
                "canReconfigure", true
            ), descriptionList.getFirst()
        );

    }

    private void getExpectedValue(List<Map<String, Object>> rules, String name, String value) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", false);
        rules.add(rule);
    }

    private void getExpectedValueWithReconfigure(List<Map<String, Object>> rules, String name, String value,
                                                 String reconfigure) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", Boolean.valueOf(reconfigure));
        rules.add(rule);
    }

    private ZonedDateTime parseCamundaTimestamp(String datetime) {
        String[] parts = datetime.split("[Z+]");
        String zone = datetime.substring(datetime.indexOf("[") + 1, datetime.lastIndexOf("]"));
        return ZonedDateTime.of(LocalDateTime.parse(parts[0]), ZoneId.of(zone));
    }

    private boolean validNow(ZonedDateTime expected, ZonedDateTime actual) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return actual != null
            && (expected.isEqual(actual) || expected.isBefore(actual))
            && (now.isEqual(actual) || now.isAfter(actual));
    }

    @Test
    void listTheCase_stf24w_overrides_isIntegrated_for_description_link() {
        String roleAssignmentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue(
            "taskAttributes", Map.of(
                "taskType", "listTheCase",
                "roleAssignmentId", roleAssignmentId,
                "taskId", taskId
            )
        );
        inputVariables.putValue(
            "caseData", Map.of(
                "stf24wCurrentStatusAutoGenerated", true,
                "stf24wPreviousStatusWasYesAutoGenerated", true,
                "isIntegrated", "Yes"
            )
        );

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> descriptionList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("description"))
            .toList();

        assertEquals(1, descriptionList.size());
        assertEquals(
            "[List the case](/case/IA/Asylum/${[CASE_REFERENCE]}/trigger/listCase)",
            descriptionList.getFirst().get("value")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "markAsPaid"
    })
    void when_taskId_then_return_due_date_skip_non_working_days_false(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dueDateSkipNonWorkingDaysResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("dueDateSkipNonWorkingDays"))
            .toList();

        assertEquals(1, dueDateSkipNonWorkingDaysResultList.size());

        assertEquals(
            Map.of(
                "name", "dueDateSkipNonWorkingDays",
                "value", "false",
                "canReconfigure", false
            ), dueDateSkipNonWorkingDaysResultList.getFirst()
        );
    }

    @ParameterizedTest
    @MethodSource("dueDateIntervalDaysScenarioProvider")
    void when_taskId_then_return_dueDateIntervalDays(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dueDateIntervalDaysResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("dueDateIntervalDays"))
            .toList();

        assertEquals(expected, dueDateIntervalDaysResultList);
    }

    public static Stream<Arguments> dueDateIntervalDaysScenarioProvider() {
        List<Map<String, Object>> fiveDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "5",
            "canReconfigure", false
        ));
        List<Map<String, Object>> threeDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "3",
            "canReconfigure", false
        ));
        List<Map<String, Object>> twoDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "2",
            "canReconfigure", false
        ));
        List<Map<String, Object>> zeroDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "0",
            "canReconfigure", false
        ));
        List<Map<String, Object>> fourteenDays = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "14",
            "canReconfigure", false
        ));

        return Stream.of(
            Arguments.of("processApplicationAdjourn", fiveDays),
            Arguments.of("detainedProcessApplicationAdjourn", fiveDays),
            Arguments.of("processApplicationExpedite", fiveDays),
            Arguments.of("detainedProcessApplicationExpedite", fiveDays),
            Arguments.of("processApplicationTimeExtension", fiveDays),
            Arguments.of("processApplicationTransfer", fiveDays),
            Arguments.of("processApplicationWithdraw", fiveDays),
            Arguments.of("detainedProcessApplicationWithdraw", fiveDays),
            Arguments.of("processApplicationUpdateHearingRequirements", fiveDays),
            Arguments.of("detainedProcessApplicationUpdateHearingRequirements", fiveDays),
            Arguments.of("processApplicationUpdateAppealDetails", fiveDays),
            Arguments.of("processApplicationReinstateAnEndedAppeal", fiveDays),
            Arguments.of("processApplicationOther", fiveDays),
            Arguments.of("processApplicationLink/UnlinkAppeals", fiveDays),
            Arguments.of("reviewRemittedAppeal", fiveDays),
            Arguments.of("detainedReviewRemittedAppeal", fiveDays),
            Arguments.of("reviewAppealSetAsideUnderRule35", fiveDays),
            Arguments.of("detainedReviewAppealSetAsideUnderRule35", fiveDays),
            Arguments.of("reviewAppealSetAsideUnderRule32", fiveDays),
            Arguments.of("detainedReviewAppealSetAsideUnderRule32", fiveDays),
            Arguments.of("reviewDraftAppeal", fiveDays),
            Arguments.of("detainedReviewDraftAppeal", fiveDays),
            Arguments.of("processFeeRefund", fiveDays),
            Arguments.of("detainedProcessFeeRefund", fiveDays),
            Arguments.of("processApplicationChangeHearingType", fiveDays),
            Arguments.of("detainedProcessApplicationChangeHearingType", fiveDays),
            Arguments.of("allocateHearingJudge", threeDays),
            Arguments.of("detainedAllocateHearingJudge", threeDays),
            Arguments.of("processApplicationToReviewDecision", twoDays),
            Arguments.of("detainedProcessApplicationToReviewDecision", twoDays),
            Arguments.of("editListing", twoDays),
            Arguments.of("reviewTheAppeal", twoDays),
            Arguments.of("detainedReviewTheAppeal", twoDays),
            Arguments.of("decideOnTimeExtension", twoDays),
            Arguments.of("reviewRespondentEvidence", twoDays),
            Arguments.of("detainedReviewRespondentEvidence", twoDays),
            Arguments.of("reviewAdditionalEvidence", twoDays),
            Arguments.of("detainedReviewAdditionalEvidence", twoDays),
            Arguments.of("reviewAdditionalHomeOfficeEvidence", twoDays),
            Arguments.of("detainedReviewAdditionalHomeOfficeEvidence", twoDays),
            Arguments.of("reviewAppealSkeletonArgument", twoDays),
            Arguments.of("detainedReviewAppealSkeletonArgument", twoDays),
            Arguments.of("reviewReasonsForAppeal", twoDays),
            Arguments.of("reviewClarifyingQuestionsAnswers", twoDays),
            Arguments.of("reviewCmaRequirements", twoDays),
            Arguments.of("attendCma", twoDays),
            Arguments.of("reviewRespondentResponse", twoDays),
            Arguments.of("detainedReviewRespondentResponse", twoDays),
            Arguments.of("caseSummaryHearingBundleStartDecision", twoDays),
            Arguments.of("detainedCaseSummaryHearingBundleStartDecision", twoDays),
            Arguments.of("reviewHearingRequirements", twoDays),
            Arguments.of("detainedReviewHearingRequirements", twoDays),
            Arguments.of("followUpOverdueRespondentEvidence", twoDays),
            Arguments.of("detainedFollowUpOverdueRespondentEvidence", twoDays),
            Arguments.of("followUpExtendedDirection", twoDays),
            Arguments.of("detainedFollowUpExtendedDirection", twoDays),
            Arguments.of("followUpOverdueCaseBuilding", twoDays),
            Arguments.of("detainedFollowUpOverdueCaseBuilding", twoDays),
            Arguments.of("followUpOverdueReasonsForAppeal", twoDays),
            Arguments.of("followUpOverdueClarifyingAnswers", twoDays),
            Arguments.of("followUpOverdueCmaRequirements", twoDays),
            Arguments.of("followUpOverdueRespondentReview", twoDays),
            Arguments.of("detainedFollowUpOverdueRespondentReview", twoDays),
            Arguments.of("followUpOverdueHearingRequirements", twoDays),
            Arguments.of("detainedFollowUpOverdueHearingRequirements", twoDays),
            Arguments.of("followUpNonStandardDirection", twoDays),
            Arguments.of("detainedFollowUpNonStandardDirection", twoDays),
            Arguments.of("followUpNoticeOfChange", twoDays),
            Arguments.of("detainedFollowUpNoticeOfChange", twoDays),
            Arguments.of("reviewAddendumEvidence", twoDays),
            Arguments.of("detainedReviewAddendumEvidence", twoDays),
            Arguments.of("reviewRemissionApplication", twoDays),
            Arguments.of("reviewASRemission", twoDays),
            Arguments.of("reviewLARemission", twoDays),
            Arguments.of("reviewHOWaiverRemission", twoDays),
            Arguments.of("reviewAuthorityRemission", twoDays),
            Arguments.of("reviewHWFRemission", twoDays),
            Arguments.of("reviewECRRemission", twoDays),
            Arguments.of("detainedReviewASRemission", twoDays),
            Arguments.of("detainedReviewLARemission", twoDays),
            Arguments.of("detainedReviewHOWaiverRemission", twoDays),
            Arguments.of("detainedReviewAuthorityRemission", twoDays),
            Arguments.of("detainedReviewHWFRemission", twoDays),
            Arguments.of("detainedReviewECRRemission", twoDays),
            Arguments.of("assignAFTPAJudge", twoDays),
            Arguments.of("detainedAssignAFTPAJudge", twoDays),
            Arguments.of("listTheCase", twoDays),
            Arguments.of("detainedListTheCase", twoDays),
            Arguments.of("reviewSetAsideDecisionApplication", twoDays),
            Arguments.of("detainedReviewSetAsideDecisionApplication", twoDays),
            Arguments.of("reviewAriaRemissionApplication", twoDays),
            Arguments.of("sendDecisionsAndReasons", zeroDays),
            Arguments.of("detainedSendDecisionsAndReasons", zeroDays),
            Arguments.of("prepareDecisionsAndReasons", zeroDays),
            Arguments.of("sendPaymentRequest", zeroDays),
            Arguments.of("uploadHearingRecording", zeroDays),
            Arguments.of("postHearingAttendeesDurationAndRecording", zeroDays),
            Arguments.of("detainedPostHearingAttendeesDurationAndRecording", zeroDays),
            Arguments.of("decideAnFTPA", zeroDays),
            Arguments.of("printAndSendHoBundle", zeroDays),
            Arguments.of("detainedPrintAndSendHoBundle", zeroDays),
            Arguments.of("printAndSendHoResponse", zeroDays),
            Arguments.of("printAndSendHearingRequirements", zeroDays),
            Arguments.of("detainedPrintAndSendHearingRequirements", zeroDays),
            Arguments.of("printAndSendHearingBundle", zeroDays),
            Arguments.of("detainedPrintAndSendHearingBundle", zeroDays),
            Arguments.of("printAndSendDecisionCorrectedRule31", zeroDays),
            Arguments.of("detainedPrintAndSendDecisionCorrectedRule31", zeroDays),
            Arguments.of("printAndSendDecisionCorrectedRule32", zeroDays),
            Arguments.of("detainedPrintAndSendDecisionCorrectedRule32", zeroDays),
            Arguments.of("printAndSendHoApplication", zeroDays),
            Arguments.of("detainedPrintAndSendHoApplication", zeroDays),
            Arguments.of("printAndSendHoEvidence", zeroDays),
            Arguments.of("detainedPrintAndSendHoEvidence", zeroDays),
            Arguments.of("printAndSendAppealDecision", zeroDays),
            Arguments.of("detainedPrintAndSendAppealDecision", zeroDays),
            Arguments.of("printAndSendFTPADecision", zeroDays),
            Arguments.of("detainedPrintAndSendFTPADecision", zeroDays),
            Arguments.of("printAndSendReheardHearingRequirements", zeroDays),
            Arguments.of("detainedPrintAndSendReheardHearingRequirements", zeroDays),
            Arguments.of("markAsPaid", fourteenDays),
            Arguments.of("cmrListed", twoDays),
            Arguments.of("detainedCmrUpdated", twoDays),
            Arguments.of("detainedListCmr", zeroDays)
        );
    }

    @Test
    void when_any_task_then_return_expected_priorities_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "processApplicationAdjourn"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "minorPriority",
            "value", "500",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "majorPriority",
            "value", "5000",
            "canReconfigure", true
        )));
    }

    @Test
    void detained_when_any_task_then_return_expected_priorities_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "detainedProcessApplicationAdjourn"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "minorPriority",
            "value", "500",
            "canReconfigure", false
        )));

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "majorPriority",
            "value", "5000",
            "canReconfigure", true
        )));
    }

    @Test
    void when_any_task_then_return_expected_non_working_days_of_week_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "processApplicationAdjourn"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "dueDateNonWorkingDaysOfWeek",
            "value", "SATURDAY,SUNDAY",
            "canReconfigure", false
        )));
    }

    @Builder
    private record Scenario(Map<String, Object> caseData, Map<String, Object> taskAttributes,
                            String expectedCaseNameValue, String expectedAppealTypeValue, String expectedRegionValue,
                            String expectedLocationValue, String expectedLocationNameValue,
                            String expectedCaseManagementCategoryValue, String expectedWorkType,
                            String expectedRoleCategory, String expectedDescriptionValue,
                            String expectedReconfigureValue, String expectedDueDateOrigin,
                            String expectedDueDateIntervalDays, String expectedHearingId, String expectedHearingDate,
                            Boolean expectedIsDetainedAppellant) {
    }

    @ParameterizedTest
    @MethodSource("customDueDateIntervalDaysScenarioProvider")
    void when_taskId_then_return_customDueDateIntervalDays(String taskType, String fieldName, String dueDays) {
        VariableMap inputVariables = new VariableMapImpl();
        Map<String, Object> caseData = new HashMap<>();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));
        caseData.put(fieldName, dueDays);
        inputVariables.putValue("caseData", caseData);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "dueDateIntervalDays",
            "value", dueDays,
            "canReconfigure", false
        )));
    }

    public static Stream<Arguments> customDueDateIntervalDaysScenarioProvider() {

        return Stream.of(
            Arguments.of("reviewMigratedCase", "ariaMigrationTaskDueDays", "10"),
            Arguments.of("detainedReviewMigratedCase", "ariaMigrationTaskDueDays", "10")

        );
    }
}

