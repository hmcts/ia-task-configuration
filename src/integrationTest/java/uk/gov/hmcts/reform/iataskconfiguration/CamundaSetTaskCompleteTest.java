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

class CamundaSetTaskCompleteTest {

    private DmnEngine dmnEngine;

    private static Stream<Arguments> scenarioProvider() {

        return Stream.of(
            Arguments.of(
                "requestRespondentEvidence",
                singletonList(
                    Map.of(
                        "task_type", "reviewTheAppeal",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestCaseBuilding",
                singletonList(
                    Map.of(
                        "task_type", "reviewRespondentEvidence",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestReasonsForAppeal",
                singletonList(
                    Map.of(
                        "task_type", "reviewRespondentEvidence",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "sendDirection",
                singletonList(
                    Map.of(
                        "task_type", "reviewRespondentEvidence",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestResponseReview",
                asList(
                    Map.of(
                        "task_type", "reviewAppealSkeletonArgument",
                        "completion_mode", "Auto"
                    ),
                    Map.of(
                        "task_type", "reviewReasonsForAppeal",
                        "completion_mode", "Auto"
                    ),
                    Map.of(
                        "task_type", "reviewClarifyingQuestionsAnswers",
                        "completion_mode", "Auto"
                    ),
                    Map.of(
                        "task_type", "reviewRespondentResponse",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestCaseEdit",
                singletonList(
                    Map.of(
                        "task_type", "reviewAppealSkeletonArgument",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "sendDirectionWithQuestions",
                asList(
                    Map.of(
                        "task_type", "reviewReasonsForAppeal",
                        "completion_mode", "Auto"
                    ),
                    Map.of(
                        "task_type", "reviewClarifyingQuestionsAnswers",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestCmaRequirements",
                asList(
                    Map.of(
                        "task_type", "reviewReasonsForAppeal",
                        "completion_mode", "Auto"
                    ),
                    Map.of(
                        "task_type", "reviewClarifyingQuestionsAnswers",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "reviewCmaRequirements",
                singletonList(
                    Map.of(
                        "task_type", "reviewCmaRequirements",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "updateDetailsAfterCma",
                singletonList(
                    Map.of(
                        "task_type", "attendCma",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "requestResponseAmend",
                singletonList(
                    Map.of(
                        "task_type", "reviewRespondentResponse",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "reviewHearingRequirements",
                singletonList(
                    Map.of(
                        "task_type", "reviewHearingRequirements",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "createCaseSummary",
                singletonList(
                    Map.of(
                        "task_type", "createCaseSummary",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "generateHearingBundle",
                singletonList(
                    Map.of(
                        "task_type", "createHearingBundle",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "decisionAndReasonsStarted",
                singletonList(
                    Map.of(
                        "task_type", "startDecisionsAndReasonsDocument",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "recordAllocatedJudge",
                singletonList(
                    Map.of(
                        "task_type", "allocateFtpaToJudge",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "changeDirectionDueDate",
                singletonList(
                    Map.of(
                        "task_type", "decideOnTimeExtension",
                        "completion_mode", "Auto"
                    )
                )
            ),
            Arguments.of(
                "recordApplication",
                singletonList(
                    Map.of(
                        "task_type", "processAnApplication",
                        "completion_mode", "Auto"
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
    void given_multiple_event_ids_should_evaluate_and_return_response(String eventId, List<Map<String, String>> expectation) {

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmn(eventId);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    private DmnDecisionTableResult evaluateDmn(String eventId) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream("wa-task-completion-ia-asylum.dmn")) {
            DmnDecision decision = dmnEngine.parseDecision("wa-task-completion-ia-asylum", inputStream);

            VariableMap variables = new VariableMapImpl();
            variables.putValue("eventId", eventId);

            return dmnEngine.evaluateDecisionTable(decision, variables);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
