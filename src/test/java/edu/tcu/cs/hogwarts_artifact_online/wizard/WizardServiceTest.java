package edu.tcu.cs.hogwarts_artifact_online.wizard;

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
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> listOfWizard;

    @BeforeEach
    void setUp() {
        this.listOfWizard = new ArrayList<>();

        Wizard w1;
        {
            w1 = new Wizard();
            w1.setId(1);
            w1.setName("Agus Dumbledore");
        }
        this.listOfWizard.add(w1);

        Wizard w2;
        {
            w2 = new Wizard();
            w2.setId(2);
            w2.setName("Heri Potter");
        }
        this.listOfWizard.add(w2);

        Wizard w3;
        {
            w3 = new Wizard();
            w3.setId(3);
            w3.setName("Ujang Bedil Longbottom");
        }
        this.listOfWizard.add(w3);

        assertNotNull(listOfWizard);
    }

    @AfterEach
    void tearDown() {
        this.listOfWizard.forEach((wizard) -> {

        });
        this.listOfWizard = null;
    }

    @Test
    void testFindByIdSuccessScenario() {
        /// Given Section
        Wizard wizardMock1 = new Wizard();
        wizardMock1.setId(1);
        wizardMock1.setName("Agus Dumbledore");
        wizardMock1.setArtifacts(null);
        ///
        /// Define findById method behavior from wizardRepository Object.
        given(this.wizardRepository
                .findById(wizardMock1.getId()))
                .willReturn(Optional.of(wizardMock1));
        ///
        /// End of Given Section.

        /// When Section.
        Wizard wizardFromServiceLayer = this.wizardService.findById(wizardMock1.getId().toString());
        /// End of When Section.

        /// Then Section.
        ///
        /// checking using org.junit.jupiter.api.Assertions.assertEquals() method.
        assertEquals(wizardFromServiceLayer.getId(), wizardMock1.getId());
        assertEquals(wizardFromServiceLayer.getName(), wizardMock1.getName());
        assertEquals(wizardFromServiceLayer.getArtifacts(), wizardMock1.getArtifacts());
        ///
        /// checking using org.assertj.core.api.Assertions.assertThat() method.
        assertThat(wizardFromServiceLayer.getId())
                .isEqualTo(wizardMock1.getId());
        assertThat(wizardFromServiceLayer.getName())
                .isEqualTo(wizardMock1.getName());
        assertThat(wizardFromServiceLayer.getArtifacts())
                .isEqualTo(wizardMock1.getArtifacts());
        ///
        /// Verify
        verify(this.wizardRepository, times(1))
                .findById(wizardMock1.getId());
        /// End of Then Section
    }

    @Test
    void testFindByIdNotFoundScenario() {
        String wizardId;
        ///
        /// Given Section.
        given(this.wizardRepository
                .findById(Mockito.any(Integer.class)))
                .willReturn(Optional.empty());
        {
            wizardId = "1";
        }
        /// End of Given Section.

        /// When Section.
        Throwable throwableThrown = catchThrowable(() -> this.wizardService.findById(wizardId));
        /// End of When Section.

        /// Then Section.
        assertThat(throwableThrown)
                .isInstanceOf(WizardNotFoundException.class)
                .hasMessage(new WizardNotFoundException(wizardId).getMessage());
        /// Checking the findById method from wizardService and wizardRepository is gettin called.
        verify(this.wizardRepository, times(1)).findById(Integer.valueOf(wizardId));
        /// End of Then Section.
    }

    @Test
    void testFindAllWizardSuccessScenario() {
        /// Given Section.
        given(this.wizardRepository.findAll()).willReturn(this.listOfWizard);
        /// End of Given Section.

        /// When Section.
        List<Wizard> allWizardInDatabase = this.wizardService.findAll();
        /// End of When Section.

        /// Then Section.
        assertThat(allWizardInDatabase.size()).isEqualTo(this.listOfWizard.size());
        /// Verify pemanggilan
        verify(this.wizardRepository, times(1)).findAll();
        /// End of Then Section.
    }

    @Test
    void testSaveAnWizardSuccessScenario() {
        /// Given Section.
        Wizard wizardMock = new Wizard();
        wizardMock.setId(1);
        wizardMock.setName("Agus Dumbledore");
        wizardMock.setArtifacts(null);
        ///
        /// Define Method Behavior..
        given(this.wizardRepository.save(wizardMock)).willReturn(wizardMock);
        /// End of Given Section.

        /// When Section.
        Wizard savedWizard = this.wizardService.save(wizardMock);
        /// End of When Section.

        /// Then Section
        assertThat(savedWizard.getId()).isEqualTo(wizardMock.getId());
        assertThat(savedWizard.getName()).isEqualTo(wizardMock.getName());
        assertThat(savedWizard.getArtifacts()).isEqualTo(wizardMock.getArtifacts());
        ///
        /// Verify Mocking Method getting called
        verify(this.wizardRepository, times(1)).save(wizardMock);
        /// End of Then Section
    }

    @Test
    void testUpdateAnWizardSuccessScenario() {
        /// Given Section.
        Wizard wizardDataReturnedFromMock;
        {
            wizardDataReturnedFromMock = new Wizard();
            wizardDataReturnedFromMock.setId(4);
            wizardDataReturnedFromMock.setName("Albus Dumbledore");
            wizardDataReturnedFromMock.setArtifacts(null);
        }
        ///
        Wizard olderVersionWizard;
        {
            olderVersionWizard = new Wizard();
            olderVersionWizard.setId(4);
            olderVersionWizard.setName("Albus Dumbledore");
            olderVersionWizard.setArtifacts(null);
        }
        ///
        Wizard newerVersionWizard;
        {
            newerVersionWizard = new Wizard();
            newerVersionWizard.setId(4);
            newerVersionWizard.setName("Agus Dumbledore");
            newerVersionWizard.setArtifacts(null);
        }
        ///
        /// Define findById Method Behavior.
        given(this.wizardRepository
                .findById(wizardDataReturnedFromMock.getId()))
                .willReturn(Optional.of(wizardDataReturnedFromMock));
        /// Define save Method Behavior.
        given(this.wizardRepository
                .save(wizardDataReturnedFromMock))
                .willReturn(wizardDataReturnedFromMock);
        /// End of Given Section.

        /// When Section.
        Wizard updatedWizard = this.wizardService
                .update(String.valueOf(wizardDataReturnedFromMock.getId()), newerVersionWizard);
        /// End of When Section.

        /// Then Section.
        assertThat(updatedWizard.getId())
                .isEqualTo(wizardDataReturnedFromMock.getId());
        assertThat(olderVersionWizard.getName())
                .isNotEqualTo(wizardDataReturnedFromMock.getName());
        assertThat(newerVersionWizard.getName())
                .isEqualTo(wizardDataReturnedFromMock.getName());
        /// Verify Method gets called.
        verify(this.wizardRepository, times(1)).findById(wizardDataReturnedFromMock.getId());
        verify(this.wizardRepository, times(1)).save(wizardDataReturnedFromMock);
        /// End of Then Section.
    }

    @Test
    void testDeleteAnWizardSuccessScenario() {
        /// Given Section.
        Wizard wizard;
        {
            wizard = new Wizard();
            wizard.setId(4);
            wizard.setName("Albus Dumbledore");
            wizard.setArtifacts(new ArrayList<>());
        }
        ///
        /// Define behavior of findById method in wizardRepository
        given(this.wizardRepository.findById(wizard.getId()))
                .willReturn(Optional.of(wizard));
        /// Define behavior of deleteById method in wizardRepository
        doNothing().when(this.wizardRepository)
                .deleteById(wizard.getId());
        /// End of Given Section.

        /// When Section.
        wizardService.delete(String.valueOf(wizard.getId()));
        /// End of When Section.

        /// Then Section.
        verify(this.wizardRepository, times(1))
                .findById(wizard.getId());
        verify(this.wizardRepository, times(1))
                .deleteById(wizard.getId());
        /// End of then Section.
    }

    @Test
    void testDeleteAnWizardNotFoundScenatio() {
        /// Given Section.
        String wizardId;
        {
            wizardId = "5";
        }
        ///
        /// Define behavior of findById method from wizardRepository.
        given(this.wizardRepository.findById(Integer.valueOf(wizardId)))
                .willReturn(Optional.empty());
        /// End of Given Section.

        /// When Section.
        assertThrows(WizardNotFoundException.class, () -> {
            this.wizardService.delete(wizardId);
        });
        /// End of When Section.

        /// Then Section.
        verify(this.wizardRepository, times(1))
                .findById(Integer.valueOf(wizardId));
        /// End of Then Section.
    }
}