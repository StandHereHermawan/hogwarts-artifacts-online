package edu.tcu.cs.hogwarts_artifact_online.wizard.converter;

import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.CreateWizardDto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateWizardDtoToWizardConverter implements Converter<CreateWizardDto, Wizard> {

    @Override
    public Wizard convert(CreateWizardDto source) {
        Wizard wizard = new Wizard();
        wizard.setName(source.name());
        return wizard;
    }
}
