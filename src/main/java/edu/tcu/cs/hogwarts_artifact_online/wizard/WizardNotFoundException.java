package edu.tcu.cs.hogwarts_artifact_online.wizard;

import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;

public class WizardNotFoundException extends ObjectNotFoundException {

    public WizardNotFoundException(String wizardId) {
        super(Wizard.class.getSimpleName().toLowerCase(), wizardId);
    }
}
