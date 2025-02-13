package edu.tcu.cs.hogwarts_artifact_online.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import org.hamcrest.Matchers;

import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
/// Spring Security Is On
@DisplayName("Integration tests for API endpoints")
@Tag("Integration")
public class WizardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Value("/wizards")
    String wizardGeneralUrl;

    @Value("/wizards/")
    String wizardSpecificUrl;

    String jsonWebToken;

    @Value("Find All Wizard Success.")
    String successFindAllWizardMessage;

    @Value("Find one wizard success.")
    String successFindOneWizardMessage;

    @Value("Add Wizard Success")
    String successAddOneWizardMessage;

    @Value("Update Wizard Success.")
    String successUpdateAnWizardMessage;

    @Value("Delete Wizard Success.")
    String successDeleteAnWizardMessage;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions;
        {
            resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login")
                    .with(httpBasic("Agus", "123456")));
        }
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();

        String responseContentAsString = mvcResult.getResponse().getContentAsString();
        /// JSON on 12 code line below are JSON response from AuthController.
        /// "flag": true,
        /// "code": 200,
        /// "message": "User Info and Json Web Token",
        /// "data": {
        ///     "userInfo": {
        ///         "id": 1,
        ///         "username": "Agus",
        ///         "enabled": true,
        ///         "roles": "admin user"
        ///     },
        ///     token: "exampleJsonWebToken"
        /// }
        ///
        JSONObject json = new JSONObject(responseContentAsString);
        this.jsonWebToken = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    /// Annotation "@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD" for reset the H2 database
    /// to default database content provided by database seeder before the method gets called.
    void testFindAllWizardsSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                        .header("Authorization", this.jsonWebToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(4)))
        ;
    }

    @Test
    @DisplayName("Check addWizard with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddWizardSuccess() throws Exception {
        Wizard wizard;
        {
            wizard = new Wizard();
            wizard.setName("Adang");
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(wizard);
        }

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header("Authorization", this.jsonWebToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }

        this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                        .header("Authorization", this.jsonWebToken)
                        .content(jsonWizard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(wizard.getName()));

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header("Authorization", this.jsonWebToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(5)));
        }
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindWizardByIdSuccess() throws Exception {
        Wizard artifact;
        {
            artifact = new Wizard();
            artifact.setName("Adang");
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                        .header("Authorization", this.jsonWebToken)
                        .content(jsonWizard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact.getName()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String wizardId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                        .header("Authorization", this.jsonWebToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(5)));

        this.mockMvc.perform(get(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header("Authorization", this.jsonWebToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneWizardMessage))
                .andExpect(jsonPath("$.data.id").value(wizardId))
                .andExpect(jsonPath("$.data.name").value(artifact.getName()));
    }

    @Test
    @DisplayName("Check updateWizard with valid input (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateWizardSuccess() throws Exception {
        Wizard artifact;
        {
            artifact = new Wizard();
            artifact.setName("Adang");
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                                .header("Authorization", this.jsonWebToken)
                                .content(jsonWizard)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                        .header("Authorization", this.jsonWebToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(5)));

        Wizard updatedWizard;
        {
            updatedWizard = new Wizard();
            updatedWizard.setName("Adang");
        }

        String updatedWizardJson;
        {
            updatedWizardJson = this.objectMapper.writeValueAsString(updatedWizard);
        }

        this.mockMvc.perform(put(this.baseUrl + this.wizardSpecificUrl + artifactId)
                        .header("Authorization", this.jsonWebToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedWizardJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successUpdateAnWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(artifactId))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
    }

    @Test
    @DisplayName("Check deleteWizard with valid input (DELETE)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteWizardSuccess() throws Exception {
        Wizard artifact;
        {
            artifact = new Wizard();
            artifact.setName("Adang");
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                                .header("Authorization", this.jsonWebToken)
                                .content(jsonWizard)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()));

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header("Authorization", this.jsonWebToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(5)));
        }

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + artifactId)
                        .header("Authorization", this.jsonWebToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header("Authorization", this.jsonWebToken)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }
}
