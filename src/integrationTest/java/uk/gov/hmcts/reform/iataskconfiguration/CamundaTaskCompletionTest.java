package uk.gov.hmcts.reform.iataskconfiguration;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CamundaTaskCompletionTest {

    private DmnEngine dmnEngine;

    private static Stream<Arguments> scenarioProvider() {

        return Stream.of(
            Arguments.of(
                "requestRespondentEvidence",
                singletonList(
                    Map.of(
                        "taskType", "reviewTheAppeal",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                singletonList(
                    Map.of(
                        "taskType", "reviewRespondentEvidence",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestReasonsForAppeal",
                singletonList(
                    Map.of(
                        "taskType", "reviewRespondentEvidence",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                singletonList(
                    Map.of(
                        "taskType", "reviewRespondentEvidence",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestResponseReview",
                asList(
                    Map.of(
                        "taskType", "reviewAppealSkeletonArgument",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewRespondentResponse",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestCaseEdit",
                singletonList(
                    Map.of(
                        "taskType", "reviewAppealSkeletonArgument",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "sendDirectionWithQuestions",
                asList(
                    Map.of(
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestCmaRequirements",
                asList(
                    Map.of(
                        "taskType", "reviewReasonsForAppeal",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "reviewClarifyingQuestionsAnswers",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "reviewCmaRequirements",
                singletonList(
                    Map.of(
                        "taskType", "reviewCmaRequirements",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "updateDetailsAfterCma",
                singletonList(
                    Map.of(
                        "taskType", "attendCma",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestResponseAmend",
                singletonList(
                    Map.of(
                        "taskType", "reviewRespondentResponse",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                singletonList(
                    Map.of(
                        "taskType", "reviewHearingRequirements",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "createCaseSummary",
                singletonList(
                    Map.of(
                        "taskType", "createCaseSummary",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "generateHearingBundle",
                singletonList(
                    Map.of(
                        "taskType", "createHearingBundle",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                singletonList(
                    Map.of(
                        "taskType", "startDecisionsAndReasonsDocument",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "recordAllocatedJudge",
                singletonList(
                    Map.of(
                        "taskType", "allocateFtpaToJudge",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                singletonList(
                    Map.of(
                        "taskType", "decideOnTimeExtension",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "recordApplication",
                singletonList(
                    Map.of(
                        "taskType", "processAnApplication",
                        "completionMode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "unknownEvent",
                emptyList()
            )
        );
    }

    @BeforeEach
    void setUp() {
        dmnEngine = DmnEngineConfiguration
            .createDefaultDmnEngineConfiguration()
            .buildEngine();
    }

    @ParameterizedTest(name = "Scenario for event id: {0}")
    @MethodSource("scenarioProvider")
    void given_multiple_event_ids_should_evaluate_dmn(String eventId, List<Map<String, String>> expectation) {

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(eventId);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    private DmnDecisionTableResult evaluateDmn(String eventId) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream =
                 contextClassLoader.getResourceAsStream("wa-task-completion-ia-asylum.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("wa-task-completion-ia-asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", eventId);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
