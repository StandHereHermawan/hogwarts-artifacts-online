package edu.tcu.cs.hogwarts_artifact_online.wizard;

import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;
import edu.tcu.cs.hogwarts_artifact_online.wizard.converter.CreateWizardDtoToWizardConverter;
import edu.tcu.cs.hogwarts_artifact_online.wizard.converter.UpdateWizardDtoToWizardConverter;
import edu.tcu.cs.hogwarts_artifact_online.wizard.converter.WizardDtoToWizardConverter;
import edu.tcu.cs.hogwarts_artifact_online.wizard.converter.WizardToWizardDtoConverter;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.CreateWizardDto;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.UpdateWizardDto;
import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.WizardDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

    private final WizardService wizardService;

    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    private final WizardDtoToWizardConverter wizardDtoToWizardConverter;

    private final CreateWizardDtoToWizardConverter createWizardDtoToWizardConverter;

    private final UpdateWizardDtoToWizardConverter updateWizardDtoToWizardConverter;

    public WizardController(WizardService wizardService,
                            WizardToWizardDtoConverter wizardToWizardDtoConverter,
                            WizardDtoToWizardConverter wizardDtoToWizardConverter,
                            CreateWizardDtoToWizardConverter createWizardDtoToWizardConverter,
                            UpdateWizardDtoToWizardConverter updateWizardDtoToWizardConverter) {
        this.wizardService = wizardService;
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
        this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
        this.createWizardDtoToWizardConverter = createWizardDtoToWizardConverter;
        this.updateWizardDtoToWizardConverter = updateWizardDtoToWizardConverter;
    }

    @GetMapping("/{wizardId}")
    public Result findArtifactById(@PathVariable String wizardId) {
        Wizard wizardFromServiceLayer = this.wizardService.findById(wizardId);
        WizardDTO serializableWizardObject = this.wizardToWizardDtoConverter.convert(wizardFromServiceLayer);
        return new Result(true, StatusCode.SUCCESS, "Find one wizard success.", serializableWizardObject);
    }

    @GetMapping
    public Result findAllWizards() {
        List<Wizard> listOfWizardFromServiceLayer = this.wizardService.findAll();
        List<WizardDTO> listOfWizardDTO = listOfWizardFromServiceLayer
                .stream()
                .map(oneWizardFromServiceLayer -> this.wizardToWizardDtoConverter
                        .convert(oneWizardFromServiceLayer))
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Wizard Success.", listOfWizardDTO);
    }

    @PostMapping
    public Result addWizard(@RequestBody
                            @Valid
                            CreateWizardDto createWizardDTO) {
        Wizard notYetSavedWizard = this.createWizardDtoToWizardConverter.convert(createWizardDTO);
        Wizard savedWizard = this.wizardService.save(notYetSavedWizard);
        WizardDTO alreadySavedWizardDto = this.wizardToWizardDtoConverter.convert(savedWizard);
        return new Result(true, StatusCode.SUCCESS, "Add Wizard Success", alreadySavedWizardDto);
    }

    @PutMapping("/{wizardId}")
    public Result updateWizard(@PathVariable
                               String wizardId,
                               @Valid
                               @RequestBody
                               UpdateWizardDto updateWizardDto) {
        Wizard newWizardRecord = this.updateWizardDtoToWizardConverter.convert(updateWizardDto);
        Wizard updatedWizardObject = this.wizardService.update(wizardId, newWizardRecord);
        WizardDTO updatedWizardObjectConvertedToDto = this.wizardToWizardDtoConverter.convert(updatedWizardObject);
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Update Wizard Success.",
                updatedWizardObjectConvertedToDto
        );
    }

    @DeleteMapping("/{wizardId}")
    public Result deleteWizard(@PathVariable
                               String wizardId) {
        this.wizardService.delete(wizardId);
        return new Result(true, StatusCode.SUCCESS, "Delete Wizard Success.", null);
    }
}
