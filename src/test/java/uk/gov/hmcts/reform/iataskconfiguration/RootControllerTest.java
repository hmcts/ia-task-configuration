package uk.gov.hmcts.reform.iataskconfiguration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.iataskconfiguration.controllers.RootController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RootController.class)
@SuppressWarnings("PMD.LawOfDemeter")
class RootControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void canRetrieveByIdWhenExists() throws Exception {
        MockHttpServletResponse response = mvc.perform(
            get("/")
                .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("Welcome to spring ia-task-configuration");
    }
}
