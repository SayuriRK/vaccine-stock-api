package one.digitalinnovation.vaccinestockapi.controller;

import lombok.AllArgsConstructor;

import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.exception.VaccineAlreadyRegisteredException;
import one.digitalinnovation.vaccinestockapi.exception.VaccineNotFoundException;
import one.digitalinnovation.vaccinestockapi.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vaccine")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class VaccineController {

    private final VaccineService vaccineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VaccineDTO createVaccine (@Valid @RequestBody VaccineDTO vaccineDTO) throws VaccineAlreadyRegisteredException {
    return  vaccineService.createVaccine(vaccineDTO);
    }

    @GetMapping
    public List<VaccineDTO> listAll(){
        return vaccineService.listAll();
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public VaccineDTO findByName (@PathVariable String name) throws VaccineNotFoundException{
    return vaccineService.findByName (name);
    }

   /* @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VaccineDTO updateById (@PathVariable Long id, @Valid @RequestBody VaccineDTO vaccineDTO) throws VaccineNotFoundException{
        return vaccineService.updateById(id, vaccineDTO);
    }*/

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id)throws VaccineNotFoundException{
    vaccineService.deleteById(id);
    }

}
