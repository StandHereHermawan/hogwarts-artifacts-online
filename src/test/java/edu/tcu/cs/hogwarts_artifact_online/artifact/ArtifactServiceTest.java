package edu.tcu.cs.hogwarts_artifact_online.artifact;

import edu.tcu.cs.hogwarts_artifact_online.artifact.util.IdWorker;
import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> listOfArtifacts;

    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
        a1.setImageUrl("ImageUrl");

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility Cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
        a2.setImageUrl("ImageUrl");

        this.listOfArtifacts = new ArrayList<>();
        this.listOfArtifacts.add(a1);
        this.listOfArtifacts.add(a2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testUsingMockingFindByIdSuccessBehavior() {
        ///
        /// Given. (Start of the "Given" step Section)
        ///
        /// Arrange Input and targets.
        /// Define the behavior of mock object artifactRepository.
        ///
        /// Code Below are Fake Data Generated by Mock.
        ///
        /// <pre>
        /// <blockquote>
        ///
        ///     "id": "1250808601744904192",
        ///     "name": "Invisibility Cloak",
        ///     "description": "An invisibility cloak is used to make the wearer invisible.",
        ///     "imageUrl": "ImageUrl",
        ///
        /// </blockquote>
        /// </pre>
        ///
        Wizard wizardMock1 = new Wizard();
        wizardMock1.setId(2);
        wizardMock1.setName("Terry Davis");
        ///
        Artifact artifactMock1 = new Artifact();
        artifactMock1.setId("1250808601744904192");
        artifactMock1.setName("Invisibility Cloak");
        artifactMock1.setDescription("An invisibility cloak is used to make the wearer invisible.");
        artifactMock1.setImageUrl("ImageUrl");
        artifactMock1.setOwner(wizardMock1);
        ///
        /// Defines the behavior of the mock object
        ///
        /// Using 'given' methods or function from 'org.mockito.BDDMockito.given' package.
        /// Mocking the success behavior of 'findById' method in artifactRepository object.
        given(artifactRepository
                .findById(artifactMock1.getId()))
                .willReturn(Optional.of(artifactMock1));
        /// (End of the "Given" step Section)
        ///


        ///
        /// When. (Start of the "When" step Section)
        /// Act on the target behavior.
        /// "When" steps should cover the method to be tested.
        ///
        /// Test the success behavior 'findById' method in artifactService object.
        Artifact returnedMockArtifactObject = artifactService.findById(artifactMock1.getId());
        /// (End of the "When" step Section)
        ///


        ///
        /// Then. (Start of the "When" step Section)
        /// Assert expected outcomes.
        ///
        /// Using 'assertThat' methods or function from 'org.assertj.core.api.Assertions.assertThat' package.
        assertThat(returnedMockArtifactObject.getId()).isEqualTo(artifactMock1.getId());
        assertThat(returnedMockArtifactObject.getName()).isEqualTo(artifactMock1.getName());
        assertThat(returnedMockArtifactObject.getDescription()).isEqualTo(artifactMock1.getDescription());
        assertThat(returnedMockArtifactObject.getImageUrl()).isEqualTo(artifactMock1.getImageUrl());
        ///
        /// Using 'verify' methods or function from 'org.mockito.Mockito.verify' package.
        verify(artifactRepository, times(1)).findById(artifactMock1.getId());
        ///
        /// Using 'assertEquals' methods or function from 'org.junit.jupiter.api.Assertions.assertEquals' package.
        assertEquals(returnedMockArtifactObject.getId(), artifactMock1.getId());
        assertEquals(returnedMockArtifactObject.getName(), artifactMock1.getName());
        assertEquals(returnedMockArtifactObject.getDescription(), artifactMock1.getDescription());
        assertEquals(returnedMockArtifactObject.getImageUrl(), artifactMock1.getImageUrl());
        /// (End of the "When" step Section)
        ///

    }

    @Test
    void testUsingMockingFindByIdNotFoundBehavior() {
        ///
        /// "Given" step.
        ///
        /// Mocking the fail behavior from findById method in artifactRepository object.
        given(artifactRepository
                .findById(Mockito.any(String.class)))
                .willReturn(Optional.empty());
        /// String contains artifactId that doesnt exist.
        String artifactId = "12345";
        /// End of "Given" step.
        ///

        ///
        /// "When" step.
        Throwable exceptionThrown = catchThrowable(() -> artifactService.findById(artifactId));
        /// End of "When" step.
        ///

        ///
        /// "Then" step.
        assertThat(exceptionThrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not found artifact with Id " + artifactId + " :(");
        verify(artifactRepository, times(1)).findById(artifactId);
        /// End of "Then" step.
        ///
    }

    @Test
    void testFindAllArtifactSuccessScenario() {
        /// Given Section.
        /// Define the behavior of the findAll() method in ArtifactRepository object.
        given(artifactRepository.findAll()).willReturn(this.listOfArtifacts);
        /// End of Given Section.

        /// When Section.
        List<Artifact> actualArtifact = artifactService.findAll();
        /// End of When Section.

        /// Then Section.
        assertThat(actualArtifact.size()).isEqualTo(this.listOfArtifacts.size());
        verify(artifactRepository, times(1)).findAll();
        /// End of Then Section.
    }

    @Test
    void testSaveAnArtifactsSuccessScenario() {
        /// Given Section.
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description...");
        newArtifact.setImageUrl("ImageUrl...");
        ///

        /// Defines the Mock Behavior of nextId Method in idWorker object.
        given(idWorker.nextId()).willReturn(123456L);
        /// Defines the Mock Behavior of save Method in artifactRepository object.
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);
        ///

        /// When Section.
        Artifact savedArtifact = artifactService.save(newArtifact);
        ///

        /// Then Section.
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        /// Verify that function being mocked get called
        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateAnArtifactSuccessScenario() {
        /// Given Section.
        ///
        /// Define the old artifact.
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        oldArtifact.setImageUrl("ImageUrl");
        ///
        /// Define the updated artifact.
        Artifact updatingArtifactData = new Artifact();
        updatingArtifactData.setName("Invisibility Cloak");
        updatingArtifactData.setDescription("New Description, An invisibility cloak is used to make the wearer invisible.");
        updatingArtifactData.setImageUrl("ImageUrl");
        ///
        /// Define the behavior of findById method repository object in this unit test method.
        given(artifactRepository.findById(oldArtifact.getId())).willReturn(Optional.of(oldArtifact));
        ///
        /// Define the behavior of save method repository object in this unit test method.
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);
        ///


        /// When Section.
        Artifact updatedArtifact = artifactService.update(oldArtifact.getId(), updatingArtifactData);
        ///


        /// Then Section.
        assertThat(updatedArtifact.getId()).isEqualTo(oldArtifact.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(updatingArtifactData.getDescription());
        ///
        /// Verify the save method and findById method get called 1 times.
        verify(artifactRepository, times(1)).findById(oldArtifact.getId());
        verify(artifactRepository, times(1)).save(oldArtifact);
        ///

    }

    @Test
    void testUpdateAnArtifactNotFoundScenario() {
        /// Given Section.
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility Cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
        artifact.setImageUrl("ImageUrl");
        ///
        /// Define findById method behavior in this unit test.
        given(artifactRepository.findById(artifact.getId())).willReturn(Optional.empty());
        ///


        /// When Section.
        assertThrows(ObjectNotFoundException.class, () -> artifactService.update(artifact.getId(), artifact));
        ///


        /// Then Section.
        verify(artifactRepository, times(1)).findById(artifact.getId());
        ///
    }

    @Test
    void testDeleteAnArtifactSuccess() {
        /// Given
        Artifact artifact;
        {
            artifact = new Artifact();
            artifact.setId("1250808601744904192");
            artifact.setName("Invisibility Cloak");
            artifact.setDescription("An invisibility cloak is used to make the wearer invisible.");
            artifact.setImageUrl("ImageUrl");
        }
        ///
        /// Define the behavior of findById and delete method from artifactRepository object in this unit test method
        given(artifactRepository.findById(artifact.getId())).willReturn(Optional.of(artifact));
        /// doNothing method for mocking the behavior of void method like deleteById method.
        doNothing().when(artifactRepository).deleteById(artifact.getId());
        ///


        /// When
        artifactService.delete(artifact.getId());
        ///


        /// Then
        verify(artifactRepository, times(1)).deleteById(artifact.getId());
        ///
    }

    @Test
    void testDeleteArtifactNotFound() {
        /// Given
        String artifactId;
        {
            artifactId = "1250808601744904192";
        }
        ///
        /// Define the behavior of findById method from artifactRepository object in this unit test method.
        given(artifactRepository.findById(artifactId)).willReturn(Optional.empty());
        ///


        /// When
        assertThrows(ObjectNotFoundException.class, () -> {
            artifactService.delete(artifactId);
        });
        ///


        /// Then
        verify(artifactRepository, times(1)).findById(artifactId);
        ///
    }
}