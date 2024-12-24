package edu.tcu.cs.hogwarts_artifact_online.artifact;

import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
/// import org.springframework.boot.test.mock.mockito.MockBean; /// Deprecated and Subject for removal. in this case replaced by "@MockitoBean" annotation.
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
/// import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
/// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/// import static org.assertj.core.configuration.Services.get; /// Salah Impor, Harusnya method get dari package 'org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get'
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArtifactControllerTest {

    /// annotation "@MockBean" /// Deprecated in future, but used in Bingyang's Spring Boot 3 Course.
    /// there's alternative use "@MockitoBean" used for replace "@MockBean" annotation and it works on this case.
    @MockitoBean
    ArtifactService artifactService;

    @Autowired
    MockMvc mockMvc;

    List<Artifact> listOfArtifact;

    @BeforeEach
    void setUp() {
        this.listOfArtifact = new ArrayList<>();

        for (int index = 0; index < 6; index++) {
            int id = index + 1;
            Artifact artifact1 = new Artifact();
            artifact1.setId(String.valueOf(id));
            artifact1.setName("Artifact " + id);
            artifact1.setDescription("Artifact " + id + " with description");
            artifact1.setImageUrl("ImageUrl");
            /// Store Artifact to list collection.
            this.listOfArtifact.add(artifact1);

            /// Checking the list collection
            assertNotNull(this.listOfArtifact.get(index));
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
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("Artifact 1")
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
}