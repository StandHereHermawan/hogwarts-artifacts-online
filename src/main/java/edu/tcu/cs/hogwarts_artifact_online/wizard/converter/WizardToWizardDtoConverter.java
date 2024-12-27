package edu.tcu.cs.hogwarts_artifact_online.wizard.converter;

import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDTO> {

    @Override
    public WizardDTO convert(Wizard source) {
        return new WizardDTO(
                source.getId(),
                source.getName(),
                source.getNumberOfArtifacts()
        );
    }
}
