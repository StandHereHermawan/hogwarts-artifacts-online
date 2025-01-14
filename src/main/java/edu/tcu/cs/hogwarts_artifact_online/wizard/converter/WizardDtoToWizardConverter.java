package edu.tcu.cs.hogwarts_artifact_online.wizard.converter;

import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDTO, Wizard> {

    @Override
    public Wizard convert(WizardDTO source) {
        Wizard wizard = new Wizard();
        wizard.setId(source.id());
        wizard.setName(source.name());
        return wizard;
    }
}
