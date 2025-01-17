package edu.tcu.cs.hogwarts_artifact_online.wizard;

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
                .orElseThrow(() -> new WizardNotFoundException(wizardId));
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard) {
        newWizard.setId(Integer.valueOf(WizardIdentifier.getIdentifier()));
        WizardIdentifier.increment();
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(String wizardId, Wizard newerWizardData) {
        return this.wizardRepository.findById(Integer.valueOf(wizardId)).map(wizard -> {
            wizard.setName(newerWizardData.getName());
            wizard.setArtifacts(newerWizardData.getArtifacts());
            return this.wizardRepository.save(wizard);
        }).orElseThrow(() -> new WizardNotFoundException(wizardId));
    }

    public void delete(String wizardId) {
        this.wizardRepository.findById(Integer.valueOf(wizardId))
                .orElseThrow(() -> new WizardNotFoundException(wizardId));
        this.wizardRepository.deleteById(Integer.valueOf(wizardId));
    }
}
