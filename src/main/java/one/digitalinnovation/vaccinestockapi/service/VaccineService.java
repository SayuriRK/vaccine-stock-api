package one.digitalinnovation.vaccinestockapi.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.entity.Vaccine;
import one.digitalinnovation.vaccinestockapi.exception.VaccineAlreadyRegisteredException;
import one.digitalinnovation.vaccinestockapi.exception.VaccineNotFoundException;
import one.digitalinnovation.vaccinestockapi.mapper.VaccineMapper;
import one.digitalinnovation.vaccinestockapi.repository.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__ (@Autowired))
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final VaccineMapper vaccineMapper = VaccineMapper.INSTANCE;

    public VaccineDTO createVaccine(VaccineDTO vaccineDTO) throws VaccineAlreadyRegisteredException {
        Vaccine vaccine = vaccineMapper.toModel(vaccineDTO);
        Vaccine savedVaccine = vaccineRepository.save(vaccine);
        return vaccineMapper.toDTO(savedVaccine);
    }
    public List<VaccineDTO> listAll() {
        List<Vaccine> vaccine = vaccineRepository.findAll();
        return vaccine.stream()
        .map(vaccineMapper::toDTO)
        .collect(Collectors.toList());
    }

    public VaccineDTO findByName(String name) throws VaccineNotFoundException{
    Vaccine foundVaccine = vaccineRepository.findByName(name)
            .orElseThrow(()-> new VaccineNotFoundException(name));
    return vaccineMapper.toDTO(foundVaccine);
    }

    public  void deleteById (Long id) throws VaccineNotFoundException {
        verifyIfExists(id);
        vaccineRepository.deleteById(id);}

    /*public void updateById (Long id, VaccineDTO vaccineDTO) throws VaccineNotFoundException {
        verifyIfExists(id);
        Vaccine vaccineToUpdate = vaccineMapper.toModel(vaccineDTO);
        Vaccine updatedVaccine = vaccineRepository.save(vaccineToUpdate);
    }
*/
        private void verifyIfAlreadyRegistered (String name) throws VaccineAlreadyRegisteredException {
            Optional<Vaccine> optSavedVaccine = vaccineRepository.findByName(name);
            if (optSavedVaccine.isPresent()) {
                throw new VaccineAlreadyRegisteredException(name);
            }
        }

        private Vaccine verifyIfExists (Long id) throws VaccineNotFoundException {
            return vaccineRepository.findById(id)
                    .orElseThrow(() -> new VaccineNotFoundException(id));
        }

    }