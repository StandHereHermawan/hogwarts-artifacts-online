package edu.tcu.cs.hogwarts_artifact_online.wizard;

import edu.tcu.cs.hogwarts_artifact_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifact_online.artifact.ArtifactRepository;
import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwarts_artifact_online.wizard.util.identifier.WizardIdentifier;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(String wizardId) {
        return this.wizardRepository.findById(Integer.valueOf(wizardId))
                .orElseThrow(() -> new ObjectNotFoundException(
                                Wizard.class.getSimpleName().toLowerCase(),
                                Integer.valueOf(wizardId)
                        )
                );
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard) {
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(String wizardId, Wizard newerWizardData) {
        return this.wizardRepository.findById(Integer.valueOf(wizardId)).map(wizard -> {
            wizard.setName(newerWizardData.getName());
            wizard.setArtifacts(newerWizardData.getArtifacts());
            return this.wizardRepository.save(wizard);
        }).orElseThrow(() -> new ObjectNotFoundException(
                        Wizard.class.getSimpleName().toLowerCase(),
                        Integer.valueOf(wizardId)
                )
        );
    }

    public void delete(String wizardId) {
        Wizard wizardToBeDeleted = this.wizardRepository.findById(Integer.valueOf(wizardId))
                .orElseThrow(() -> new ObjectNotFoundException(
                                Wizard.class.getSimpleName().toLowerCase(),
                                Integer.valueOf(wizardId)
                        )
                );

        wizardToBeDeleted.removeAllArtifacts();
        this.wizardRepository.deleteById(Integer.valueOf(wizardId));
    }

    public void assignArtifact(Integer wizardId, String artifactId) {
        /// Find the desired artifact by artifactId that already given.
        Artifact artifactToBeAssigned;
        {
            artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                    .orElseThrow(() -> new ObjectNotFoundException(
                            Artifact.class.getSimpleName().toLowerCase(),
                            artifactId));
        }
        /// Find the desired wizard by wizardId that already given.
        Wizard newArtifactOwner;
        {
            newArtifactOwner = this.wizardRepository.findById(wizardId)
                    .orElseThrow(() -> new ObjectNotFoundException(
                            Wizard.class.getSimpleName().toLowerCase(),
                            wizardId));
        }

        /// Artifact assignment.
        /// We need to check if artifact already signed to a wizard or not.
        if (artifactToBeAssigned.getOwner() != null) {
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }
        newArtifactOwner.addArtifact(artifactToBeAssigned);
    }
}
