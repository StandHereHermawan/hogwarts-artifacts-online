package edu.tcu.cs.hogwarts_artifact_online.wizard;

import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwarts_artifact_online.wizard.util.identifier.WizardIdentifier;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
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
}
