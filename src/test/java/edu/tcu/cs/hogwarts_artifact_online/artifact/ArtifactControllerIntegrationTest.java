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

    String jsonWebToken;

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
    void testFindAllArtifactsSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)))
        ;
    }

    @Test
    @DisplayName("Check addArtifact with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddArtifactSuccess() throws Exception {
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
                        .header("Authorization", this.jsonWebToken)
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
        this.mockMvc.perform(get(this.baseUrl + this.artifactGeneralUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllArtifactMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindArtifactByIdSuccess() throws Exception {
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
                        .header("Authorization", this.jsonWebToken)
                        .content(jsonArtifact)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(artifact.getName()))
                .andExpect(jsonPath("$.data.description").value(artifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(artifact.getImageUrl())
                );

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
                        .header("Authorization", this.jsonWebToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneArtifactMessage))
                .andExpect(jsonPath("$.data.id").value(artifactId))
                .andExpect(jsonPath("$.data.name").value(artifact.getName()));
    }

    @Test
    @DisplayName("Check updateArtifact with valid input (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateArtifactSuccess() throws Exception {
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
                                .header("Authorization", this.jsonWebToken)
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
                        .header("Authorization", this.jsonWebToken)
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
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl())
                );
    }

    @Test
    @DisplayName("Check deleteArtifact with valid input (DELETE)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteArtifactSuccess() throws Exception {
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
                                .header("Authorization", this.jsonWebToken)
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
                        .header("Authorization", this.jsonWebToken)
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
