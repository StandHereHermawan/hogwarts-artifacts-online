package edu.tcu.cs.hogwarts_artifact_online.wizard;

import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.CreateWizardDto;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.UpdateWizardDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class WizardControllerTest {

    @MockitoBean
    WizardService wizardService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    List<Wizard> listOfWizard;

    @BeforeEach
    void setUp() {
        this.listOfWizard = new ArrayList<>();

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Agus Dumbledore");
        this.listOfWizard.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Heri Potter");
        this.listOfWizard.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Ujang Bedil Longbottom");
        this.listOfWizard.add(w3);

        assertNotNull(objectMapper);
    }

    @AfterEach
    void tearDown() {
        listOfWizard.forEach((wizard) -> {
        });
        this.listOfWizard = null;
    }

    @Test
    void testFindByIdSuccessScenario() throws Exception {
        String wizardId;
        {
            wizardId = String.valueOf(1);
        }
        Wizard expectedWizardData;
        {
            expectedWizardData = this.listOfWizard.getFirst();
        }
        /// Given Section.
        given(this.wizardService
                .findById(wizardId))
                .willReturn(this.listOfWizard.getFirst());
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(get("/api/v1/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find one wizard success."))
                .andExpect(jsonPath("$.data.id").value(expectedWizardData.getId()))
                .andExpect(jsonPath("$.data.name").value(expectedWizardData.getName()));
        /// End of When and Then Section.
    }

    @Test
    void testFindByIdNotFoundScenario() throws Exception {
        String wizardId;
        {
            wizardId = String.valueOf(1);
        }
        /// Given Section.
        given(this.wizardService
                .findById(wizardId))
                .willThrow(new WizardNotFoundException(wizardId));
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(get("/api/v1/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not found wizard with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty());
        /// End of When and Then Section.
    }

    @Test
    void testFindAllSuccessScenario() throws Exception {
        Wizard firstWizard;
        Wizard secondWizard;
        {
            firstWizard = this.listOfWizard.getFirst();
            secondWizard = this.listOfWizard.get(1); /// get(1) to get the second data in listOfWizard array.
        }
        /// Given Section.
        given(this.wizardService.findAll())
                .willReturn(this.listOfWizard);
        /// End of Given Section.

        /// When and Then Section
        this.mockMvc.perform(get("/api/v1/wizards")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Wizard Success."))
                .andExpect(jsonPath("$.data[0].id").value(firstWizard.getId()))
                .andExpect(jsonPath("$.data[0].name").value(firstWizard.getName()))
                .andExpect(jsonPath("$.data[1].id").value(secondWizard.getId()))
                .andExpect(jsonPath("$.data[1].name").value(secondWizard.getName()));
        /// End of When and Then Section
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        /// Given Section.
        CreateWizardDto wizardDTO = new CreateWizardDto("Agus Dumbledore");
        ///
        String wizardDtoToJson = this.objectMapper.writeValueAsString(wizardDTO);
        ///
        Wizard wizardForCompare = new Wizard();
        wizardForCompare.setId(4);
        wizardForCompare.setName("Agus Dumbledore");
        wizardForCompare.setArtifacts(List.of());
        ///
        /// Define wizardService save function behavior.
        given(this.wizardService.save(Mockito.any(Wizard.class)))
                .willReturn(wizardForCompare);
        /// End of Given Section.

        /// When and Then Section
        this.mockMvc.perform(post("/api/v1/wizards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wizardDtoToJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Wizard Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(wizardForCompare.getName())
                );
        /// End of When and Then Section
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        /// Given Section.
        UpdateWizardDto updateWizardDto;
        {
            updateWizardDto = new UpdateWizardDto(
                    "Agus Dumbledore"
            );
        }
        ///
        String jsonFromUpdatedArtifactDto;
        {
            jsonFromUpdatedArtifactDto = this.objectMapper.writeValueAsString(updateWizardDto);
        }
        ///
        Wizard returnedUpdatedWizard;
        {
            returnedUpdatedWizard = new Wizard();
            returnedUpdatedWizard.setId(1);
            returnedUpdatedWizard.setName("Agus Dumbledore");
        }
        ///
        given(this.wizardService
                .update(eq(String.valueOf(returnedUpdatedWizard.getId())), Mockito.any(Wizard.class)))
                .willReturn(returnedUpdatedWizard);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(put("/api/v1/wizards/" + returnedUpdatedWizard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFromUpdatedArtifactDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Wizard Success."))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(returnedUpdatedWizard.getId()))
                .andExpect(jsonPath("$.data.name").value(returnedUpdatedWizard.getName())
                );
        /// End of When and Then Section.
    }

    @Test
    void testUpdateWizardNotExistentId() throws Exception {
        /// Given Section.
        UpdateWizardDto updateWizardDto;
        {
            updateWizardDto = new UpdateWizardDto("Agus Dumbledore");
        }
        ///
        String jsonFromUpdatedArtifactDto;
        {
            jsonFromUpdatedArtifactDto = this.objectMapper.writeValueAsString(updateWizardDto);
        }
        ///
        String wizardId;
        {
            wizardId = "1";
        }
        ///
        given(this.wizardService
                .update(eq(wizardId), Mockito.any(Wizard.class)))
                .willThrow(new WizardNotFoundException(wizardId));
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(put("/api/v1/wizards/" + wizardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonFromUpdatedArtifactDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value(new WizardNotFoundException(wizardId).getMessage()))
                .andExpect(jsonPath("$.data").isEmpty()
                );
        /// End of When and Then Section.
    }

    @Test
    void testDeleteWizardSuccessScenario() throws Exception {
        /// Given Section.
        String wizardId;
        {
            wizardId = "1";
        }
        ///
        /// Defines delete method behavior in artifactService object in this unit test.
        doNothing()
                .when(this.wizardService)
                .delete(wizardId);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(delete("/api/v1/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Wizard Success."))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
        /// End of When and Then Section.
    }

    @Test
    void testDeleteWizardNotFoundScnario() throws Exception {
        /// Given Section.
        String wizardId;
        {
            wizardId = "1";
        }
        ///
        /// Defines delete method behavior in artifactService object in this unit test.
        doThrow(new WizardNotFoundException(wizardId))
                .when(this.wizardService)
                .delete(wizardId);
        /// End of Given Section.

        /// When and Then Section.
        this.mockMvc.perform(delete("/api/v1/wizards/" + wizardId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not found wizard with Id " + wizardId + " :("))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
        /// End of When and Then Section.
    }
}