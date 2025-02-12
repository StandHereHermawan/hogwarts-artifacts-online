package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

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
public class HogwartsUserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Value("/users")
    String userGeneralUrl;

    @Value("/users/")
    String userSpecificUrl;

    String jsonWebTokenAdminPrivilege;

    String jsonWebTokenNormalUserPrivilege;

    @Value("Find All Hogwarts User Success.")
    String successFindAllUserMessage;

    @Value("Find One Hogwarts User Success.")
    String successFindOneUserMessage;

    @Value("Add Hogwarts User Success.")
    String successAddOneUserMessage;

    @Value("Update Hogwarts User Success.")
    String successUpdateAnUserMessage;

    @Value("Delete Hogwarts User Success.")
    String successDeleteAnUserMessage;

    @Value("No permission.")
    String notAuthorizedMessage;

    @Value("Access Denied")
    String accessDeniedMessage;

    @BeforeEach
    void setUp() throws Exception {
        /// Privillege Admin Token
        {
            ResultActions resultActionsAdminPrivillege;
            {
                resultActionsAdminPrivillege = this.mockMvc
                        .perform(post(this.baseUrl + this.userSpecificUrl + "login")
                                .with(httpBasic("Agus", "123456")));
            }

            MvcResult mvcResult = resultActionsAdminPrivillege
                    .andDo(print())
                    .andReturn();

            String responseContentAsString = mvcResult
                    .getResponse()
                    .getContentAsString();
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
            this.jsonWebTokenAdminPrivilege = "Bearer " + json
                    .getJSONObject("data")
                    .getString("token");
        }
        /// Privillege Normal User Token
        {
            ResultActions resultActionsNormalUserPrivilege;
            {
                resultActionsNormalUserPrivilege = this.mockMvc
                        .perform(post(this.baseUrl + this.userSpecificUrl + "login")
                                .with(httpBasic("Adang", "654321")));
            }

            MvcResult mvcResultNormalUserPrivilege = resultActionsNormalUserPrivilege
                    .andDo(print())
                    .andReturn();

            String responseContentAsStringNormalUser = mvcResultNormalUserPrivilege
                    .getResponse()
                    .getContentAsString();

            JSONObject jsonNormalUserResponse = new JSONObject(responseContentAsStringNormalUser);
            this.jsonWebTokenNormalUserPrivilege = "Bearer " + jsonNormalUserResponse
                    .getJSONObject("data")
                    .getString("token");
        }
    }

    @AfterEach
    void tearDown() {
        this.jsonWebTokenAdminPrivilege = null;
        this.jsonWebTokenNormalUserPrivilege = null;
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    /// Annotation "@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD" for reset the H2 database
    /// to default database content provided by database seeder before the method gets called.
    void testFindAllHogwartsUsersUsingAdminPrivilegeSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)))
        ;
    }

    @Test
    @DisplayName("Check addHogwartsUser with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddNewHogwartsUserUsingAdminPrivilegeSuccess() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        /// Check hogwarts data in database before integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
        }

        this.mockMvc.perform(post(this.baseUrl + this.userGeneralUrl)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .content(jsonHogwartsUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        /// Check hogwarts data in database after integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindHogwartsUserByIdUsingAdminPrivilegeSuccess() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        /// Check seeder data does its already in database or not.
        ResultActions resultActions = this.mockMvc.perform(
                        post(this.baseUrl + this.userGeneralUrl)
                                .header("Authorization", this.jsonWebTokenAdminPrivilege)
                                .content(jsonHogwartsUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);
        String hogwartsUserId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        /// Check hogwarts data in database after database seeding.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }

        this.mockMvc.perform(get(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));
    }

    @Test
    @DisplayName("Check updateHogwartsUser with valid input (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateHogwartsUserUsingAdminPrivilegeSuccess() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        /// Check seeder data does its already in database or not.
        ResultActions resultActions = this.mockMvc.perform(
                        post(this.baseUrl + this.userGeneralUrl)
                                .header("Authorization", this.jsonWebTokenAdminPrivilege)
                                .content(jsonHogwartsUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String hogwartsUserId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        /// Check hogwarts data in database before integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }

        HogwartsUser updatedHogwartsUser;
        {
            updatedHogwartsUser = new HogwartsUser();
            updatedHogwartsUser.setUsername("Adang Update");
            updatedHogwartsUser.setRoles("user");
            updatedHogwartsUser.setEnabled(true);
        }

        String updatedWizardJson;
        {
            updatedWizardJson = this.objectMapper.writeValueAsString(updatedHogwartsUser);
        }

        this.mockMvc.perform(put(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedWizardJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successUpdateAnUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data.username").value(updatedHogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(updatedHogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(updatedHogwartsUser.isEnabled()));
    }

    @Test
    @DisplayName("Check deleteWizard with valid input (DELETE)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteHogwartsUserUsingAdminPrivilegeSuccess() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        /// Check seeder data does its already in database or not.
        ResultActions resultActions = this.mockMvc.perform(
                        post(this.baseUrl + this.userGeneralUrl)
                                .header("Authorization", this.jsonWebTokenAdminPrivilege)
                                .content(jsonHogwartsUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        /// Check hogwarts data in database before integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
        }

        String hogwartsUserId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwarts data in database after integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
        }
    }

    @Test
    @DisplayName("Check findAllHogwartsUsers with invalid token (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllHogwartsUsersUsingNormalUserPrivilege() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                        .header("Authorization", this.jsonWebTokenNormalUserPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(this.notAuthorizedMessage))
                .andExpect(jsonPath("$.data").value(this.accessDeniedMessage))
        ;
    }

    @Test
    @DisplayName("Check addHogwartsUser with invalid token (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddNewHogwartsUserUsingNormalUserPrivilege() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        this.mockMvc.perform(post(this.baseUrl + this.userGeneralUrl)
                        .content(jsonHogwartsUser)
                        .header("Authorization", this.jsonWebTokenNormalUserPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(this.notAuthorizedMessage))
                .andExpect(jsonPath("$.data").value(this.accessDeniedMessage));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindHogwartsUserByIdUsingNormalUserPrivilege() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        ResultActions resultActions = this.mockMvc.perform(
                        post(this.baseUrl + this.userGeneralUrl)
                                .header("Authorization", this.jsonWebTokenAdminPrivilege)
                                .content(jsonHogwartsUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);
        String hogwartsUserId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(get(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenNormalUserPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(this.notAuthorizedMessage))
                .andExpect(jsonPath("$.data").value(this.accessDeniedMessage));
    }

    @Test
    @DisplayName("Check updateHogwartsUser with invalid token (PUT)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testUpdateHogwartsUserUsingNormalUserPrivilege() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        ResultActions resultActions = this.mockMvc.perform(
                        post(this.baseUrl + this.userGeneralUrl)
                                .header("Authorization", this.jsonWebTokenAdminPrivilege)
                                .content(jsonHogwartsUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String hogwartsUserId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");


        HogwartsUser updatedHogwartsUser;
        {
            updatedHogwartsUser = new HogwartsUser();
            updatedHogwartsUser.setUsername("Adang Update");
            updatedHogwartsUser.setRoles("user");
            updatedHogwartsUser.setEnabled(true);
        }

        String updatedWizardJson;
        {
            updatedWizardJson = this.objectMapper.writeValueAsString(updatedHogwartsUser);
        }

        this.mockMvc.perform(put(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenNormalUserPrivilege)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedWizardJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(this.notAuthorizedMessage))
                .andExpect(jsonPath("$.data").value(this.accessDeniedMessage));
    }

    @Test
    @DisplayName("Check deleteWizard with invalid token (DELETE)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testDeleteHogwartsUserUsingNormalUserPrivilegeSuccess() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Adang");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("123456");
        }

        String jsonHogwartsUser;
        {
            jsonHogwartsUser = this.objectMapper.writeValueAsString(hogwartsUser);
        }

        ResultActions resultActions = this.mockMvc.perform(
                        post(this.baseUrl + this.userGeneralUrl)
                                .header("Authorization", this.jsonWebTokenAdminPrivilege)
                                .content(jsonHogwartsUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddOneUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject jsonResponseFromAddWizard = new JSONObject(contentAsString);

        String hogwartsUserId = jsonResponseFromAddWizard
                .getJSONObject("data")
                .getString("id");

        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenNormalUserPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(this.notAuthorizedMessage))
                .andExpect(jsonPath("$.data").value(this.accessDeniedMessage));
    }
}
