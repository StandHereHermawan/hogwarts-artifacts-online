package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.data_transfer_object.HogwartsUserDTO;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;
import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    List<HogwartsUser> hogwartsUserList;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Value("/users")
    String usersPathInfoGeneral;

    @Value("/users/")
    String usersPathInfoSpecific;

    @Value("Find All Hogwarts User Success.")
    String successFindAllMessage;

    @Value("Find One Hogwarts User Success.")
    String successFindUserByIdMessage;

    @Value("Add Hogwarts User Success.")
    String successAddUserMessage;

    @Value("Update Hogwarts User Success.")
    String successUpdateUserMessage;

    @Value("Delete Hogwarts User Success.")
    String successDeleteUserMessage;

    @BeforeEach
    void setUp() {

        int index;
        {
            index = 0;
        }

        HogwartsUser hogwartsUser1 = new HogwartsUser();
        {
            index++;
            hogwartsUser1.setId(index);
            hogwartsUser1.setUsername("Agus");
            hogwartsUser1.setPassword("123456");
            hogwartsUser1.setEnabled(true);
            hogwartsUser1.setRoles("admin user");
        }

        HogwartsUser hogwartsUser2 = new HogwartsUser();
        {
            index++;
            hogwartsUser2.setId(index);
            hogwartsUser2.setUsername("Agus");
            hogwartsUser2.setPassword("654321");
            hogwartsUser2.setEnabled(true);
            hogwartsUser2.setRoles("user");
        }

        HogwartsUser hogwartsUser3 = new HogwartsUser();
        {
            index++;
            hogwartsUser3.setId(index);
            hogwartsUser3.setUsername("Tono");
            hogwartsUser3.setPassword("qwerty");
            hogwartsUser3.setEnabled(false);
            hogwartsUser3.setRoles("user");
        }

        this.hogwartsUserList = new ArrayList<>();
        {
            this.hogwartsUserList.add(hogwartsUser1);
            this.hogwartsUserList.add(hogwartsUser2);
            this.hogwartsUserList.add(hogwartsUser3);
        }
    }

    @Test
    void testFindAllUsersSuccessScenario() throws Exception {
        /// Given Section.
        given(this.userService.findAllUsers())
                .willReturn(this.hogwartsUserList);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(get(this.baseUrl + this.usersPathInfoGeneral)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindAllMessage))
                .andExpect(jsonPath("$.data[0].id").value(this.hogwartsUserList
                        .get(0).getId()))
                .andExpect(jsonPath("$.data[0].username").value(this.hogwartsUserList
                        .get(0).getUsername()))
                .andExpect(jsonPath("$.data[1].id").value(this.hogwartsUserList
                        .get(1).getId()))
                .andExpect(jsonPath("$.data[1].username").value(this.hogwartsUserList
                        .get(1).getUsername()))
        ;
        /// When and Then Section.
    }

    @Test
    void testFindUserByIdSuccessScenario() throws Exception {
        /// Given Section.
        int userId, indexInList;
        {
            userId = 2;
            indexInList = userId - 1;
        }
        ///
        given(this.userService.findHogwartsUserById(userId))
                .willReturn(this.hogwartsUserList.get(indexInList));
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(get(this.baseUrl + this.usersPathInfoSpecific + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successFindUserByIdMessage))
                .andExpect(jsonPath("$.data.id").value(this.hogwartsUserList
                        .get(indexInList).getId()))
                .andExpect(jsonPath("$.data.username").value(this.hogwartsUserList
                        .get(indexInList).getUsername()))
        ;
        /// End of When and Then Section.
    }

    @Test
    void testFindUserByIdNotFoundScenario() throws Exception {
        /// Given Section.
        int userId;
        {
            userId = 2;
        }
        ///
        given(this.userService.findHogwartsUserById(userId))
                .willThrow(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(),
                        userId
                ));
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(get(this.baseUrl + this.usersPathInfoSpecific + userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(new ObjectNotFoundException(
                                HogwartsUser.class.getSimpleName().toLowerCase(),
                                userId).getMessage()
                        )
                )
                .andExpect(jsonPath("$.data").isEmpty())
        ;
        /// End of When and Then Section.
    }

    @Test
    void testAddUserSuccessScenario() throws Exception {
        /// Given Section.
        HogwartsUser newhogwartsUser;
        {
            newhogwartsUser = new HogwartsUser();
            newhogwartsUser.setId(4);
            newhogwartsUser.setUsername("Yanto Kates");
            newhogwartsUser.setPassword("123456");
            newhogwartsUser.setEnabled(true);
            newhogwartsUser.setRoles("admin user");
        }
        int indexInList;
        {
            indexInList = newhogwartsUser.getId() - 1;
        }
        String json = this.objectMapper.writeValueAsString(newhogwartsUser);
        ///
        given(this.userService.save(Mockito.any(HogwartsUser.class)))
                .willReturn(newhogwartsUser);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(post(this.baseUrl + this.usersPathInfoGeneral)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successAddUserMessage))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(newhogwartsUser.getId()))
                .andExpect(jsonPath("$.data.username").value(newhogwartsUser.getUsername()))
        ;
        /// When and Then Section.
    }

    @Test
    void testUpdateUserSuccessScenario() throws Exception {
        /// Given Section.
        HogwartsUserDTO hogwartsUserDTO;
        {
            hogwartsUserDTO = new HogwartsUserDTO(3, "tom123", false, "user");
        }
        HogwartsUser updatedHogwartsUser;
        {
            updatedHogwartsUser = new HogwartsUser();
            updatedHogwartsUser.setId(3);
            updatedHogwartsUser.setUsername("tom123");
            updatedHogwartsUser.setEnabled(false);
            updatedHogwartsUser.setRoles("user");
        }
        ///
        String json = this.objectMapper.writeValueAsString(hogwartsUserDTO);
        ///
        given(this.userService.update(eq(updatedHogwartsUser.getId()), Mockito.any(HogwartsUser.class)))
                .willReturn(updatedHogwartsUser);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(put(this.baseUrl + this.usersPathInfoSpecific + updatedHogwartsUser.getId())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successUpdateUserMessage))
                .andExpect(jsonPath("$.data.id").value(updatedHogwartsUser.getId()))
                .andExpect(jsonPath("$.data.username").value(updatedHogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(updatedHogwartsUser.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(updatedHogwartsUser.getRoles()))
        ;
        /// End of When and Then Section.
    }

    @Test
    void testUpdateUserNonExistentScenario() throws Exception {
        /// Given Section.
        HogwartsUserDTO hogwartsUserDTO;
        {
            hogwartsUserDTO = new HogwartsUserDTO(3, "tom123", false, "user");
        }
        HogwartsUser updatedHogwartsUser;
        {
            updatedHogwartsUser = new HogwartsUser();
            updatedHogwartsUser.setId(3);
            updatedHogwartsUser.setUsername("tom123");
            updatedHogwartsUser.setEnabled(false);
            updatedHogwartsUser.setRoles("user");
        }
        ///
        String json = this.objectMapper.writeValueAsString(hogwartsUserDTO);
        ///
        given(this.userService.update(eq(updatedHogwartsUser.getId()), Mockito.any(HogwartsUser.class)))
                .willThrow(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(),
                        updatedHogwartsUser.getId())
                );
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(put(this.baseUrl + this.usersPathInfoSpecific + updatedHogwartsUser.getId())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(new ObjectNotFoundException(
                                HogwartsUser.class.getSimpleName().toLowerCase(),
                                updatedHogwartsUser.getId()).getMessage()
                        )
                )
                .andExpect(jsonPath("$.data").isEmpty())
        ;
        /// End of When and Then Section.
    }

    @Test
    void testDeleteUserSuccessScenario() throws Exception {
        /// Given Section.
        HogwartsUser deletedUser;
        {
            deletedUser = new HogwartsUser();
            deletedUser.setId(4);
            deletedUser.setUsername("Yanto Kates");
            deletedUser.setPassword("123456");
            deletedUser.setEnabled(true);
            deletedUser.setRoles("admin user");
        }
        ///
        given(this.userService.delete(deletedUser.getId()))
                .willReturn(deletedUser);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(delete(this.baseUrl + this.usersPathInfoSpecific + deletedUser.getId()))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value(this.successDeleteUserMessage))
        ;
        /// End of When and Then Section.
    }

    @Test
    void testDeleteUserErrorWithNonExistentId() throws Exception {
        /// Given Section.
        HogwartsUser deletedUser;
        {
            deletedUser = new HogwartsUser();
            deletedUser.setId(4);
            deletedUser.setUsername("Yanto Kates");
            deletedUser.setPassword("123456");
            deletedUser.setEnabled(true);
            deletedUser.setRoles("admin user");
        }
        ///
        given(this.userService.delete(eq(deletedUser.getId())))
                .willThrow(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(), deletedUser.getId()
                ));
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(delete(this.baseUrl + this.usersPathInfoSpecific + deletedUser.getId()))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(), deletedUser.getId())
                        .getMessage()
                ))
        ;
        /// End of When and Then Section.
    }
}