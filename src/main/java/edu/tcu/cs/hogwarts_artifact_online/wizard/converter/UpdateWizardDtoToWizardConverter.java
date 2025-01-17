package edu.tcu.cs.hogwarts_artifact_online.wizard.converter;

import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.UpdateWizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UpdateWizardDtoToWizardConverter implements Converter<UpdateWizardDto, Wizard> {

    @Override
    public Wizard convert(UpdateWizardDto source) {
        Wizard wizard = new Wizard();
        wizard.setName(source.name());
        return wizard;
    }
}
