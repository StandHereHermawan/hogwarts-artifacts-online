package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

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
@AutoConfigureMockMvc /// Spring Security Is On
@DisplayName("Integration tests for HogwartsUser API endpoints")
@Tag("Integration")
public class HogwartsUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    @Value("/users")
    private String userGeneralUrl;

    @Value("/users/")
    private String userSpecificUrl;

    private String jsonWebTokenAdminPrivilege;

    private String jsonWebTokenNormalUserPrivilege;

    @Value("Find All Hogwarts User Success.")
    private String successFindAllUserMessage;

    @Value("Find One Hogwarts User Success.")
    private String successFindOneUserMessage;

    @Value("Add Hogwarts User Success.")
    private String successAddOneUserMessage;

    @Value("Update Hogwarts User Success.")
    private String successUpdateAnUserMessage;

    @Value("Delete Hogwarts User Success.")
    private String successDeleteAnUserMessage;

    @Value("No permission.")
    private String notAuthorizedMessage;

    @Value("Access Denied")
    private String accessDeniedMessage;

    private String hogwartsUserNotFoundMessage;

    @Value("Provided arguments are invalid, see data for details.")
    private String invalidArgumentsMessage;

    private int numbersDataAsInDBInitializarer = 3;

    private int numbersDataAsPlusOne = 4;

    @BeforeEach
    void setUp() throws Exception {
        hogwartsUserNotFoundMessage = HogwartsUser.class.getSimpleName().toLowerCase();

        /// Privillege Normal User Token
        ResultActions resultActionsNormalUserPrivilege;
        resultActionsNormalUserPrivilege = this.mockMvc
                .perform(post(this.baseUrl + "/users/login")
                        .with(httpBasic("Adang", "654321")));

        MvcResult mvcResultNormalUserPrivilege = resultActionsNormalUserPrivilege
                .andDo(print())
                .andReturn();

        String responseContentAsStringNormalUser = mvcResultNormalUserPrivilege
                .getResponse()
                .getContentAsString();

        System.err.println("Response Content As String User: " + responseContentAsStringNormalUser);
        JSONObject jsonNormalUserResponse = new JSONObject(responseContentAsStringNormalUser);
        System.err.println("Normal User privilege: "+jsonNormalUserResponse);
        String tokenNormalUserWithoutPrefix = jsonNormalUserResponse
                .getJSONObject("data")
                .getString("token");

        this.jsonWebTokenNormalUserPrivilege = "Bearer " + tokenNormalUserWithoutPrefix;

        /// Privillege Admin Token
        ResultActions resultActionsAdminPrivilege;
        resultActionsAdminPrivilege = this.mockMvc.perform(post(this.baseUrl + "/users/login")
                .with(httpBasic("Agus", "123456")));

        MvcResult mvcResultAdminPrivilege = resultActionsAdminPrivilege.andDo(print()).andReturn();
        String responseContentAsString = mvcResultAdminPrivilege.getResponse().getContentAsString();
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
        System.err.println("Response Content As String Admin: " + responseContentAsString);
        JSONObject jsonAdminPrivilege = new JSONObject(responseContentAsString);
        System.err.println("Admin privilege: "+jsonAdminPrivilege);
        String tokenWithoutPrefix = jsonAdminPrivilege
                .getJSONObject("data")
                .getString("token");

        this.jsonWebTokenAdminPrivilege = "Bearer " + tokenWithoutPrefix; // add "Bearer " prefix.
    }

    @AfterEach
    void tearDown() {
        this.jsonWebTokenAdminPrivilege = null;
        this.jsonWebTokenNormalUserPrivilege = null;
    }

    /// Annotation "@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD" for reset the H2 database
    /// to default database content provided by database seeder before the method gets called.
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findAllHogwartsUser with admin privilege (GET)")
    void testFindAllHogwartsUsersUsingAdminPrivilegeSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findHogwartsUserById using admin privilege with valid input (GET)")
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsPlusOne)));
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

        /// Delete operation.
        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findHogwartsUserById using admin privilege with invalid input (GET)")
    void testFindHogwartsUserByIdUsingAdminPrivilegeNotFound() throws Exception {
        String hogwartsUserId = "123456789";

        this.mockMvc.perform(get(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(this.hogwartsUserNotFoundMessage, hogwartsUserId).getMessage()
                ));
        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check addHogwartsUser with admin privilege and valid input (POST)")
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }

        ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + this.userGeneralUrl)
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

        int hogwartsUserId = new JSONObject(resultActions
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString())
                .getJSONObject("data")
                .getInt("id");

        /// Check hogwarts data in database after integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsPlusOne)));
        }

        /// Delete operation.
        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check addHogwartsUser with admin privilege and invalid input (POST)")
    void testAddNewHogwartsUserUsingAdminPrivilegeValidationError() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("");
            hogwartsUser.setRoles("");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("");
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }

        this.mockMvc.perform(post(this.baseUrl + this.userGeneralUrl)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .content(jsonHogwartsUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value(this.invalidArgumentsMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwarts data in database after this integration test method operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check updateHogwartsUser with admin privilege and valid input (PUT)")
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsPlusOne)));
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

        /// Delete operation.
        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check updateHogwartsUser with admin privilege and invalid input (PUT)")
    void testUpdateHogwartsUserUsingAdminPrivilegeButHogwartsUserNotFound() throws Exception {
        String hogwartsUserId = "123456789";

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
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(this.hogwartsUserNotFoundMessage, hogwartsUserId)
                                .getMessage()));

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check updateHogwartsUser with admin privilege and invalid input (PUT)")
    void testUpdateHogwartsUserUsingAdminPrivilegeInvalidArguments() throws Exception {
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsPlusOne)));
        }

        HogwartsUser updatedHogwartsUser;
        {
            updatedHogwartsUser = new HogwartsUser();
            updatedHogwartsUser.setUsername("");
            updatedHogwartsUser.setRoles("");
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
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value(this.invalidArgumentsMessage))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Delete operation.
        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check deleteHogwartsUser with admin privilege and valid input (DELETE)")
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsPlusOne)));
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check deleteHogwartsUser with admin privilege but invalid input (DELETE)")
    void testDeleteHogwartsUserUsingAdminPrivilegeNotFound() throws Exception {
        String hogwartsUserId = "123456789";

        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(
                        new ObjectNotFoundException(this.hogwartsUserNotFoundMessage, hogwartsUserId)
                                .getMessage()));

        /// Check hogwarts data in database after integration test operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findAllHogwartsUsers with normal user privilege (GET)")
    void testFindAllHogwartsUsersUsingNormalUserPrivilege() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                        .header("Authorization", this.jsonWebTokenNormalUserPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value(this.notAuthorizedMessage))
                .andExpect(jsonPath("$.data").value(this.accessDeniedMessage));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check findHogwartsUserById normal user privilege but valid input (GET)")
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

        /// Delete operation.
        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check addHogwartsUser with normal user privilege (POST)")
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

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check updateHogwartsUser with normal user privilege (PUT)")
    void testUpdateHogwartsUserUsingNormalUserPrivilege() throws Exception {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername("Bedo");
            hogwartsUser.setRoles("user");
            hogwartsUser.setEnabled(true);
            hogwartsUser.setPassword("12345678");
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
            updatedHogwartsUser.setUsername("Bedo Update");
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

        /// Delete operation.
        this.mockMvc.perform(delete(this.baseUrl + this.userSpecificUrl + hogwartsUserId)
                        .header("Authorization", this.jsonWebTokenAdminPrivilege)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteAnUserMessage))
                .andExpect(jsonPath("$.data.id").value(hogwartsUserId))
                .andExpect(jsonPath("$.data").isNotEmpty());

        /// Check hogwartsUser data in database after delete operation.
        {
            this.mockMvc.perform(get(this.baseUrl + this.userGeneralUrl)
                            .header("Authorization", this.jsonWebTokenAdminPrivilege)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.flag").value(true))
                    .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                    .andExpect(jsonPath("$.message").value(this.successFindAllUserMessage))
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @DisplayName("Check deleteWizard with normal user privilege and valid input (DELETE)")
    void testDeleteHogwartsUserUsingNormalUserPrivilege() throws Exception {
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
                    .andExpect(jsonPath("$.data", Matchers.hasSize(this.numbersDataAsInDBInitializarer)));
        }
    }
}
