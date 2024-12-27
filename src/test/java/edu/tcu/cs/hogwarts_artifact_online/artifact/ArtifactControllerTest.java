package edu.tcu.cs.hogwarts_artifact_online.artifact;

import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;
import org.hamcrest.Matchers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
/// import org.springframework.boot.test.mock.mockito.MockBean; /// Deprecated and Subject for removal. in this case replaced by "@MockitoBean" annotation.

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
/// import static org.springframework.test.web.servlet.MockMvcBuilder.*;
/// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/// import static org.assertj.core.configuration.Services.get; /// Salah Impor, Harusnya method get dari package 'org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get'
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArtifactControllerTest {

    /// annotation "@MockBean" /// Deprecated in the future, but used in Bingyang's Spring Boot 3 Course.
    /// there's alternative use "@MockitoBean" used for replace "@MockBean" annotation. it works on this case.
    @MockitoBean
    ArtifactService artifactService;

    @Autowired
    MockMvc mockMvc;

    List<Artifact> listOfArtifact;

    @BeforeEach
    void setUp() {
        this.listOfArtifact = new ArrayList<>();

        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");
        this.listOfArtifact.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");
        this.listOfArtifact.add(a2);

        Artifact a3 = new Artifact();
        a3.setId("1250808601744904193");
        a3.setName("Elder Wand");
        a3.setDescription("The Elder Wand, known throughout history as the Deathstick or the Wand of Destiny, is an extremely powerful wand made of elder wood with a core of Thestral tail hair.");
        a3.setImageUrl("ImageUrl");
        this.listOfArtifact.add(a3);

        Artifact a4 = new Artifact();
        a4.setId("1250808601744904194");
        a4.setName("The Marauder's Map");
        a4.setDescription("A magical map of Hogwarts created by Remus Lupin, Peter Pettigrew, Sirius Black, and James Potter while they were students at Hogwarts.");
        a4.setImageUrl("ImageUrl");
        this.listOfArtifact.add(a4);

        Artifact a5 = new Artifact();
        a5.setId("1250808601744904195");
        a5.setName("The Sword Of Gryffindor");
        a5.setDescription("A goblin-made sword adorned with large rubies on the pommel. It was once owned by Godric Gryffindor, one of the medieval founders of Hogwarts.");
        a5.setImageUrl("ImageUrl");
        this.listOfArtifact.add(a5);

        Artifact a6 = new Artifact();
        a6.setId("1250808601744904196");
        a6.setName("Resurrection Stone");
        a6.setDescription("The Resurrection Stone allows the holder to bring back deceased loved ones, in a semi-physical form, and communicate with them.");
        a6.setImageUrl("ImageUrl");
        this.listOfArtifact.add(a6);

        for (int index = 0; index < 6; index++) {
            int id = index + 1;
            Artifact artifacts = new Artifact();
            artifacts.setId(String.valueOf(id + Long.parseLong("1250808601744904196")));
            artifacts.setName("Artifact " + id);
            artifacts.setDescription("Artifact " + id + " with description");
            artifacts.setImageUrl("ImageUrl");
            /// Store Artifact to list collection.
            this.listOfArtifact.add(artifacts);

            /// Checking the list collection.
            assertNotNull(this.listOfArtifact.get(index + 6));
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactByIdSuccessScenario() throws Exception {
        /// Given step
        given(this.artifactService.findById("1"))
                .willReturn(this.listOfArtifact.getFirst());
        /// End of Given step

        /// When and Then step
        this.mockMvc.perform(get("/api/v1/artifacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator")
                );
        /// End of When and Then step
    }

    @Test
    void testFindArtifactByIdNotFoundScenario() throws Exception {
        /// Given step
        given(this.artifactService.findById("1"))
                .willThrow(new ArtifactNotFoundException("1"));
        /// End of Given step

        /// When and Then step
        this.mockMvc.perform(get("/api/v1/artifacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not found artifact with Id 1 :("))
                .andExpect(jsonPath("$.data").isEmpty()
                );
        /// End of When and Then step
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        /// Given Section.
        ///
        given(this.artifactService.findAll()).willReturn(this.listOfArtifact);
        /// End of Given Section.


        /// When and Then Section.
        ///
        this.mockMvc.perform(get("/api/v1/artifacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Artifacts Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(this.listOfArtifact.size())))
                .andExpect(jsonPath("$.data[0].id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data[0].name").value("Deluminator"))
                .andExpect(jsonPath("$.data[1].id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data[1].name").value("Invisibility Cloak"))
                .andExpect(jsonPath("$.data[6].id").value("1250808601744904197"))
                .andExpect(jsonPath("$.data[6].name").value("Artifact 1")
                );
        /// End of When and Then Section.
    }
}
