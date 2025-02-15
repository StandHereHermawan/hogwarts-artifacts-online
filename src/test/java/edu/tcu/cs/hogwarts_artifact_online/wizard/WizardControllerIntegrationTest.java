package edu.tcu.cs.hogwarts_artifact_online.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tcu.cs.hogwarts_artifact_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;

import org.json.JSONObject;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
/// Spring Security Is On
@DisplayName("Integration tests for Wizard API endpoints")
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

    @Value("/artifacts/")
    String artifactSpecificUrl;

    @Value("/artifacts")
    String artifactGeneralUrl;

    String jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest;

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

    @Value("Provided arguments are invalid, see data for details.")
    String invalidArgumentMessage;

    @Value("Artifact Assignment Success.")
    String successAssignArtifactToWizardMessage;

    @Value("Add Artifact Success")
    String successAddOneArtifactMessage;

    @Value("Delete Artifact Success")
    String successDeleteAnArtifactMessage;

    @Value("Find All Artifacts Success")
    String successFindAllArtifactMessage;

    String wizardEntityMessage;

    @BeforeEach
    void setUp() throws Exception {
        this.wizardEntityMessage = Wizard.class.getSimpleName().toLowerCase();

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
        this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @AfterEach
    void tearDown() {
        this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest = null;
    }

    /// Annotation "@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD" for reset the H2 database
    /// to default database content provided by database seeder before the method gets called.
    //@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findAllWizards (GET)")
    void testFindAllWizardsSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(4)))
        ;
    }

    @Test
    @DisplayName("Check findWizardById (GET)")
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
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
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
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(5)));

        this.mockMvc.perform(get(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneWizardMessage))
                .andExpect(jsonPath("$.data.id").value(wizardId))
                .andExpect(jsonPath("$.data.name").value(artifact.getName()));

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));

        }
    }

    @Test
    @DisplayName("Check findWizardById not found (GET)")
    void testFindWizardByIdNotFound() throws Exception {
        String wizardId = "123456789";

        this.mockMvc.perform(get(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value(new ObjectNotFoundException(this.wizardEntityMessage, wizardId)
                                .getMessage()));

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));

        }
    }

    @Test
    @DisplayName("Check addWizard with valid input (POST)")
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
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .content(jsonWizard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(wizard.getName()));

        String wizardId = new JSONObject(resultActions
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getString("id");

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(5)));

        }

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));

        }
    }

    @Test
    @DisplayName("Check addWizard invalid arguments (POST)")
    void testAddWizardInvalidArguments() throws Exception {
        Wizard wizard;
        {
            wizard = new Wizard();
            wizard.setName("");
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(wizard);
        }

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }

        this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .content(jsonWizard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value(this.invalidArgumentMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));

        }
    }

    @Test
    @DisplayName("Check updateWizard with valid input (PUT)")
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
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
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
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
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

        this.mockMvc.perform(put(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedWizardJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successUpdateAnWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(wizardId))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));

        }
    }

    @Test
    @DisplayName("Check updateWizard not found (PUT)")
    void testUpdateWizardNotFound() throws Exception {
        String wizardId = "123456789";

        Wizard updatedWizard;
        {
            updatedWizard = new Wizard();
            updatedWizard.setName("Adang");
        }

        String updatedWizardJson;
        {
            updatedWizardJson = this.objectMapper.writeValueAsString(updatedWizard);
        }

        this.mockMvc.perform(put(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedWizardJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value(new ObjectNotFoundException(this.wizardEntityMessage, wizardId)
                                .getMessage()));

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }

    @Test
    @DisplayName("Check updateWizard invalid argument (PUT)")
    void testUpdateWizardValidationError() throws Exception {
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
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
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
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(5)));

        Wizard updatedWizard;
        {
            updatedWizard = new Wizard();
            updatedWizard.setName("");
        }

        String updatedWizardJson;
        {
            updatedWizardJson = this.objectMapper.writeValueAsString(updatedWizard);
        }

        this.mockMvc.perform(put(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedWizardJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value(this.invalidArgumentMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));

        }
    }

    @Test
    @DisplayName("Check deleteWizard with valid input (DELETE)")
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
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
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
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(5)));
        }

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String wizardId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }

    @Test
    @DisplayName("Check deleteWizard not found (DELETE)")
    void testDeleteWizardNotFound() throws Exception {
        String wizardId = "123456789";

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value(new ObjectNotFoundException(this.wizardEntityMessage, wizardId)
                                .getMessage()));

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }

    @Test
    @DisplayName("Check assignArtifact success operation (PUT)")
    void testAssignArtifactToOneWizardSuccess() throws Exception {
        Artifact artifact2;
        {
            artifact2 = new Artifact();
            artifact2.setId("1250808601744904199");
            artifact2.setName("Mesin CNC");
            artifact2.setDescription("Paranti motongan beusi.");
            artifact2.setImageUrl("ImageUrl");
        }

        Wizard wizard2;
        {
            wizard2 = new Wizard();
            wizard2.setId(2);
            wizard2.setName("Heri Kolter");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact2);
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(wizard2);
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .content(jsonWizard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(wizard2.getName()));

        int wizardId = new JSONObject(resultActions
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getInt("id");

        ResultActions resultActionsArtifact = this.mockMvc.perform(
                        post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact2.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact2.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact2.getImageUrl()));

        String artifactId = new JSONObject(resultActionsArtifact
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(put(this.baseUrl +
                        this.wizardSpecificUrl + wizardId +
                        this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAssignArtifactToWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
        }

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }

    @Test
    @DisplayName("Check assignArtifact artifact not found (PUT)")
    void testAssignArtifactToOneWizardButArtifactNotFound() throws Exception {
        Wizard wizard2;
        {
            wizard2 = new Wizard();
            wizard2.setId(2);
            wizard2.setName("Heri Kolter");
        }

        String jsonWizard;
        {
            jsonWizard = this.objectMapper.writeValueAsString(wizard2);
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.wizardGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .content(jsonWizard)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneWizardMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(wizard2.getName()));

        int wizardId = new JSONObject(resultActions
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getInt("id");

        String artifactId = "123456";

        this.mockMvc.perform(put(this.baseUrl +
                        this.wizardSpecificUrl + wizardId +
                        this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(
                                Artifact.class.getSimpleName().toLowerCase(),
                                artifactId).getMessage()))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(delete(this.baseUrl + this.wizardSpecificUrl + wizardId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnWizardMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.wizardGeneralUrl)
                            .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllWizardMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }

    @Test
    @DisplayName("Check assignArtifact wizard not found (PUT)")
    void testAssignArtifactToOneWizardButWizardNotFound() throws Exception {
        Artifact artifact2;
        {
            artifact2 = new Artifact();
            artifact2.setId("1250808601744904199");
            artifact2.setName("Mesin CNC");
            artifact2.setDescription("Paranti motongan beusi.");
            artifact2.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact2);
        }

        int wizardId = 123456;

        ResultActions resultActionsArtifact = this.mockMvc.perform(
                        post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact2.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact2.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact2.getImageUrl()));

        String artifactId = new JSONObject(resultActionsArtifact
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(put(this.baseUrl +
                        this.wizardSpecificUrl + wizardId +
                        this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(
                                Wizard.class.getSimpleName().toLowerCase(),
                                wizardId).getMessage()))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilegeWizardControllerIntegrationTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        {
            this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
        }
    }
}
