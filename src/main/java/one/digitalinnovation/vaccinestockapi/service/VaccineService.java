package one.digitalinnovation.vaccinestockapi.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.entity.Vaccine;
import one.digitalinnovation.vaccinestockapi.exception.VaccineAlreadyRegisteredException;
import one.digitalinnovation.vaccinestockapi.exception.VaccineNotFoundException;
import one.digitalinnovation.vaccinestockapi.exception.VaccineStockExceededException;
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
        verifyIfAlreadyRegistered(vaccineDTO.getName());
        Vaccine vaccine = vaccineMapper.toModel(vaccineDTO);
        Vaccine savedVaccine = vaccineRepository.save(vaccine);
        return vaccineMapper.toDTO(savedVaccine);
    }
    public List<VaccineDTO> listAll() {
        return vaccineRepository.findAll()
                .stream()
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
        vaccineRepository.deleteById(id);
    }

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

    public VaccineDTO increment(Long id, int quantityToIncrement) throws VaccineNotFoundException, VaccineStockExceededException {
        Vaccine vaccineToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = vaccineToIncrementStock.getQuantity() + quantityToIncrement; //sum of what we had + the increment
        if (quantityAfterIncrement <= vaccineToIncrementStock.getMax()) { //if the sum is less or equal than the max quantity
            vaccineToIncrementStock.setQuantity(vaccineToIncrementStock.getQuantity() + quantityToIncrement);//the quantity is the new sum
            Vaccine incrementedVaccineStock = vaccineRepository.save(vaccineToIncrementStock);//then you save the new quantity
            return vaccineMapper.toDTO(incrementedVaccineStock);//return the new quantity
        }
        throw new VaccineStockExceededException(id, quantityToIncrement);
        }

    public VaccineDTO decrement(Long id, int quantityToDecrement) throws VaccineNotFoundException, VaccineStockExceededException {
        Vaccine vaccineToDecrementStock = verifyIfExists(id);
        int quantityAfterDecrement = vaccineToDecrementStock.getQuantity() - quantityToDecrement;//subtraction of what we had -the decrement
        if (quantityAfterDecrement >= 0) {   //if subtraction is greater than or equal zero
            vaccineToDecrementStock.setQuantity(quantityAfterDecrement);// the quantity is the new subtraction
            Vaccine decrementedVaccineStock = vaccineRepository.save(vaccineToDecrementStock);//save new quantity
            return vaccineMapper.toDTO(decrementedVaccineStock); //return it
        }
        throw new VaccineStockExceededException(id, quantityToDecrement);  //else throw the exception
    }
    }