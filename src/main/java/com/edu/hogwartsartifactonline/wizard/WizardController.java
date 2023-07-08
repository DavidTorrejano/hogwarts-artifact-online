package com.edu.hogwartsartifactonline.wizard;

import com.edu.hogwartsartifactonline.system.Result;
import com.edu.hogwartsartifactonline.system.StatusCode;
import com.edu.hogwartsartifactonline.wizard.converter.WizardDTOToWizardConverter;
import com.edu.hogwartsartifactonline.wizard.converter.WizardToWizardDTOConverter;
import com.edu.hogwartsartifactonline.wizard.dto.WizardDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

    private final WizardService wizardService;
    private final WizardToWizardDTOConverter wizardToWizardDTOConverter;
    private final WizardDTOToWizardConverter wizardDTOToWizardConverter;

    public WizardController(WizardService wizardService,
                            WizardToWizardDTOConverter wizardToWizardDTOConverter,
                            WizardDTOToWizardConverter wizardDTOToWizardConverter) {
        this.wizardService = wizardService;
        this.wizardToWizardDTOConverter = wizardToWizardDTOConverter;
        this.wizardDTOToWizardConverter = wizardDTOToWizardConverter;
    }

    @GetMapping
    public Result findAllWizards(){
        List<Wizard> foundWizards = wizardService.findAll();
        List<WizardDTO> foundWizardsDTO = foundWizards.stream()
                .map(wizardToWizardDTOConverter::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Find All Success", foundWizardsDTO);
    }

    @PostMapping
    public Result saveNewWizard(@Valid @RequestBody WizardDTO wizardDTO){
        // First need to convert DTO to a normal wizard
        Wizard wizardToBeSaved = wizardDTOToWizardConverter.convert(wizardDTO);
        Wizard savedWizard = wizardService.save(wizardToBeSaved);
        WizardDTO wizardDTOToBeReturned = wizardToWizardDTOConverter.convert(savedWizard);

        return new Result(true, StatusCode.SUCCESS, "Save Success", wizardDTOToBeReturned);
    }

    @GetMapping("/{wizardId}")
    public Result findWizardById(@PathVariable int wizardId){
        Wizard foundWizard = wizardService.findById(wizardId);
        WizardDTO wizardDTO = wizardToWizardDTOConverter.convert(foundWizard);

        return new Result(true, StatusCode.SUCCESS, "Find One Success", wizardDTO);
    }

    @PutMapping("/{wizardId}")
    public Result updateWizard(@Valid @PathVariable int wizardId, @RequestBody WizardDTO wizardDTO){
        Wizard update = wizardDTOToWizardConverter.convert(wizardDTO);
        Wizard updatedWizard = wizardService.update(wizardId, update);
        WizardDTO wizardDTOToBeReturned = wizardToWizardDTOConverter.convert(updatedWizard);
        return new Result(true, StatusCode.SUCCESS, "Update Success", wizardDTOToBeReturned);
    }

    @DeleteMapping("/{wizardId}")
    public Result deleteArtifactById(@PathVariable int wizardId){
        wizardService.delete(wizardId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }
}
