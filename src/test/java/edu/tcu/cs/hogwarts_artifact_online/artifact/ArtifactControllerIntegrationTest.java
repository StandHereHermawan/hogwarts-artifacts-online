package edu.tcu.cs.hogwarts_artifact_online.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;

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

// import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc /// Spring Security Is On
@DisplayName("Integration tests for Artifact API endpoints")
@Tag("Integration")
public class ArtifactControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Value("/artifacts")
    String artifactGeneralUrl;

    @Value("/artifacts/")
    String artifactSpecificUrl;

    String jsonWebTokenAdminPrivilege;

    @Value("Find All Artifacts Success")
    String successFindAllArtifactMessage;

    @Value("Find One Success")
    String successFindOneArtifactMessage;

    @Value("Add Artifact Success")
    String successAddOneArtifactMessage;

    @Value("Update Artifact Success")
    String successUpdateAnArtifactMessage;

    @Value("Delete Artifact Success")
    String successDeleteAnArtifactMessage;

    @Value("The access token provided is expired, revoked, malformed or invalid for other reasons.")
    String notAuthenticatedMessage;

    @Value("Provided arguments are invalid, see data for details.")
    String invalidArgumentMessage;

    String artifactSimpleClassName;

    @BeforeEach
    void setUp() throws Exception {
        this.artifactSimpleClassName = Artifact.class.getSimpleName().toLowerCase();

        ResultActions resultActions;
        {
            resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login")
                    .with(httpBasic("Agus", "123456")));
        }

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String responseContentAsString = mvcResult.getResponse().getContentAsString();
        /// JSON on 14 code line below starts from this line are JSON response from AuthController.
        /// {
        ///     "flag": true,
        ///     "code": 200,
        ///     "message": "User Info and Json Web Token",
        ///     "data": {
        ///         "userInfo": {
        ///             "id": 1,
        ///             "username": "Agus",
        ///             "enabled": true,
        ///             "roles": "admin user"
        ///         },
        ///         token: "exampleJsonWebToken"
        ///     }
        /// }
        JSONObject json = new JSONObject(responseContentAsString);
        String tokenWithoutPrefix = json
                .getJSONObject("data")
                .getString("token");

        this.jsonWebTokenAdminPrivilege = "Bearer " + tokenWithoutPrefix;
    }

    @AfterEach
    void tearDown() {
        this.jsonWebTokenAdminPrivilege = null;
    }

    /// Annotation "@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD" for reset the H2 database
    /// to default database content provided by database seeder before the method gets called.
    /// But that annotation make Integration Test Slows.
    // @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findAllArtifacts no authentication (GET)")
    void testFindAllArtifactsNoAuthSuccessScenario() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)))
        ;
    }

    @Test
    @DisplayName("Check findAllArtifacts with admin privilege authentication (GET)")
    void testFindAllArtifactsWithAuthAdminPrivilegeSuccessScenario() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)))
        ;
    }

    @Test
    @DisplayName("Check findArtifactById with admin privilege authentication (GET)")
    void testFindArtifactByIdWithAuthAdminPrivilegeSuccessScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .content(jsonArtifact)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddArtifact = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddArtifact
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));

        this.mockMvc.perform(get(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").value(artifactId))
                .andExpect(jsonPath("$.data.name").value(artifact.getName()));

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check findArtifactById with admin privillege not found (GET)")
    void testFindArtifactByIdWithAuthAdminPrivilegeNotFoundScenario() throws Exception {
        String artifactId = "1250808601744904190";

        this.mockMvc.perform(get(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(this.artifactSimpleClassName, artifactId)
                                .getMessage()));
    }

    @Test
    @DisplayName("Check findArtifactById no authentication (GET)")
    void testFindArtifactByIdNoAuthSuccessScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .content(jsonArtifact)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddArtifact = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddArtifact
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));

        this.mockMvc.perform(get(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check findArtifactById no authentication not found (GET)")
    void testFindArtifactByIdNoAuthNotFoundScenario() throws Exception {
        String artifactId = "1250808601744904190";

        this.mockMvc.perform(get(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(this.artifactSimpleClassName, artifactId)
                                .getMessage()));
    }

    @Test
    @DisplayName("Check addArtifact with admin privilege and valid input (POST)")
    void testAddArtifactWithAuthAdminPrivilegeSuccessScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .content(jsonArtifact)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        String artifactId = new JSONObject(resultActions
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(artifactId))
                .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check addArtifact with admin privilege but invalid input (POST)")
    void testAddArtifactWithAuthAdminPrivilegeValidationErrorScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("");
            artifact.setDescription("");
            artifact.setImageUrl("");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .content(jsonArtifact)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value(this.invalidArgumentMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("Check addArtifact no authentication with valid input (POST)")
    void testAddArtifactNoAuthScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                        .content(jsonArtifact)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value(this.notAuthenticatedMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("Check updateArtifact with admin privilege and valid input (PUT)")
    void testUpdateArtifactWithAuthAdminPrivilegeSuccessScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                        .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                        .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddArtifact = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddArtifact
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));

        Artifact updatedArtifact;
        {
            updatedArtifact = new Artifact();
            updatedArtifact.setName("Kunci Torsi");
            updatedArtifact.setDescription("Update - Paranti mageuhkeun baut sesuai torsi kekencangan");
            updatedArtifact.setImageUrl("ImageUrl");
        }

        String updatedArtifactJson;
        {
            updatedArtifactJson = this.objectMapper.writeValueAsString(updatedArtifact);
        }

        this.mockMvc.perform(put(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedArtifactJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successUpdateAnArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(artifactId))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check updateArtifact with admin privilege but invalid input (PUT)")
    void testUpdateArtifactWithAuthAdminPrivilegeValidationErrorScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                        .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                        .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddArtifact = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddArtifact
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));

        Artifact updatedArtifact;
        {
            updatedArtifact = new Artifact();
            updatedArtifact.setName("");
            updatedArtifact.setDescription("");
            updatedArtifact.setImageUrl("");
        }

        String updatedArtifactJson;
        {
            updatedArtifactJson = this.objectMapper.writeValueAsString(updatedArtifact);
        }

        this.mockMvc.perform(put(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedArtifactJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value(this.invalidArgumentMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check updateArtifact with no authentication and valid input (PUT)")
    void testUpdateArtifactNoAuthScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                        .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                        .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));


        String artifactId = new JSONObject(resultActions
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getString("id");

        Artifact updatedArtifact;
        {
            updatedArtifact = new Artifact();
            updatedArtifact.setName("Kunci Torsi");
            updatedArtifact.setDescription("Update - Paranti mageuhkeun baut sesuai torsi kekencangan");
            updatedArtifact.setImageUrl("ImageUrl");
        }

        String updatedArtifactJson;
        {
            updatedArtifactJson = this.objectMapper.writeValueAsString(updatedArtifact);
        }

        this.mockMvc.perform(put(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedArtifactJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value(this.notAuthenticatedMessage))
                .andExpect(jsonPath("$.data").isNotEmpty())
        ;

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Check deleteArtifact with admin privilege with valid input (DELETE)")
    void testDeleteArtifactWithAuthAdminPrivilegeSuccessScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                        .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                        .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddArtifact = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddArtifact
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Check deleteArtifact with admin privilege but invalid input (DELETE)")
    void testDeleteArtifactWithAuthAdminPrivilegeNotFoundScenario() throws Exception {
        String artifactId = "1250808601744904190";

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(new ObjectNotFoundException(
                        this.artifactSimpleClassName, artifactId).getMessage()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check deleteArtifact with no authentication but valid input (DELETE)")
    void testDeleteArtifactNoAuthScenario() throws Exception {
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setName("Kunci Torsi");
            artifact.setDescription("Paranti mageuhkeun baut sesuai torsi kekencangan");
            artifact.setImageUrl("ImageUrl");
        }

        String jsonArtifact;
        {
            jsonArtifact = this.objectMapper.writeValueAsString(artifact);
        }

        ResultActions resultActions =
                this.mockMvc.perform(post(this.baseUrl + this.artifactGeneralUrl)
                                .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                                .content(jsonArtifact)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.flag").value(true))
                        .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                        .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                        .andExpect(jsonPath("$.data.id").isNotEmpty())
                        .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                        .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                        .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddArtifact = new JSONObject(contentAsString);

        String artifactId = jsonResponseFromAddArtifact
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value(this.notAuthenticatedMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());

        this.mockMvc.perform(delete(this.baseUrl + this.artifactSpecificUrl + artifactId)
                        .header(HttpHeaders.AUTHORIZATION, this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnArtifactMessage))
                .andExpect(jsonPath("$.data").isEmpty());

        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }
}
