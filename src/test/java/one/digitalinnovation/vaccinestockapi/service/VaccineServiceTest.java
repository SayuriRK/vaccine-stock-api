package one.digitalinnovation.vaccinestockapi.service;

import one.digitalinnovation.vaccinestockapi.builder.VaccineDTOBuilder;
import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.entity.Vaccine;
import one.digitalinnovation.vaccinestockapi.exception.VaccineAlreadyRegisteredException;
import one.digitalinnovation.vaccinestockapi.exception.VaccineNotFoundException;
import one.digitalinnovation.vaccinestockapi.exception.VaccineStockExceededException;
import one.digitalinnovation.vaccinestockapi.mapper.VaccineMapper;
import one.digitalinnovation.vaccinestockapi.repository.VaccineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//meaning: to run this unit test I want to use the mockito extension
public class VaccineServiceTest {

    @Mock //make a clone of the vaccine repository
    private VaccineRepository vaccineRepository;
    //mapper is automatically inserted as a constant, not need to mock it
    private VaccineMapper vaccineMapper = VaccineMapper.INSTANCE;

    @InjectMocks
    private VaccineService vaccineService;

    @Test//test to verify if vaccine was created
    void whenVaccineInformedThenItShouldBeCreated() throws VaccineAlreadyRegisteredException {
        // given
       VaccineDTO expectedVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
       Vaccine expectedSavedVaccine = vaccineMapper.toModel(expectedVaccineDTO);

       // when
        when(vaccineRepository.findByName(expectedVaccineDTO.getName())).thenReturn(Optional.empty());
        when(vaccineRepository.save(expectedSavedVaccine)).thenReturn(expectedSavedVaccine);

        //then
        VaccineDTO createdVaccineDTO = vaccineService.createVaccine(expectedVaccineDTO);

        assertThat(createdVaccineDTO.getId(), is(equalTo(expectedVaccineDTO.getId())));
        assertThat(createdVaccineDTO.getName(), is(equalTo(expectedVaccineDTO.getName())));
        assertThat(createdVaccineDTO.getQuantity(), is(equalTo(expectedVaccineDTO.getQuantity())));
        //using hamcrest test became more fluid
        assertThat(createdVaccineDTO.getQuantity(), is(greaterThan(2)));
    }
    @Test
    void whenAlreadyRegisteredVaccineInformedThenAnExceptionShouldBeThrow() {
        // given
        VaccineDTO expectedVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine duplicatedVaccine = vaccineMapper.toModel(expectedVaccineDTO);

        // when
        when(vaccineRepository.findByName(expectedVaccineDTO.getName())).thenReturn(Optional.of(duplicatedVaccine));

        //then   assert that exception was thrown
        assertThrows(VaccineAlreadyRegisteredException.class, () -> vaccineService.createVaccine(expectedVaccineDTO)) ;
    }

    @Test
    void whenValidVaccineNameIsGivenThenReturnVaccine() throws VaccineNotFoundException {

        // given
        VaccineDTO expectedFoundVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine expectedFoundVaccine = vaccineMapper.toModel(expectedFoundVaccineDTO);

        //when
        when(vaccineRepository.findByName(expectedFoundVaccine.getName())).thenReturn(Optional.of(expectedFoundVaccine));

        //then
        VaccineDTO foundVaccineDTO = vaccineService.findByName(expectedFoundVaccineDTO.getName());

        assertThat(foundVaccineDTO, is(equalTo(expectedFoundVaccineDTO)));
    }
    @Test
    void whenNoRegisteredVaccineNameIsGivenThenThrowException() {

        // given
        VaccineDTO expectedFoundVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        when(vaccineRepository.findByName(expectedFoundVaccineDTO.getName())).thenReturn(Optional.empty());

        //then
        assertThrows(VaccineNotFoundException.class, () -> vaccineService.findByName(expectedFoundVaccineDTO.getName()));

    }

    @Test
    void whenListIsCalledThenReturnListOfVaccines() {

        // given
        VaccineDTO expectedFoundVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine expectedFoundVaccine = vaccineMapper.toModel(expectedFoundVaccineDTO);

        //when
        when(vaccineRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundVaccine));

        //then
        List<VaccineDTO> foundListVaccinesDTO = vaccineService.listAll();
        assertThat(foundListVaccinesDTO, is(not(empty())));
        assertThat(foundListVaccinesDTO.get(0),is(equalTo(expectedFoundVaccineDTO)));
    }
    @Test
    void whenListIsCalledThenReturnEmptyListOfVaccines() {

        //when
        when(vaccineRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<VaccineDTO> foundListVaccinesDTO = vaccineService.listAll();
        assertThat(foundListVaccinesDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenVaccineShouldBeDeleted() throws VaccineNotFoundException {
        // given
        VaccineDTO expectedDeletedVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine expectedDeletedVaccine = vaccineMapper.toModel(expectedDeletedVaccineDTO);

        //when
        when(vaccineRepository.findById(expectedDeletedVaccineDTO.getId())).thenReturn(Optional.of(expectedDeletedVaccine));
        doNothing().when(vaccineRepository).deleteById(expectedDeletedVaccineDTO.getId());

        //then
        vaccineService.deleteById(expectedDeletedVaccineDTO.getId());

        verify(vaccineRepository, times(1)).findById(expectedDeletedVaccineDTO.getId());
        verify(vaccineRepository, times(1)).deleteById(expectedDeletedVaccineDTO.getId());
    }

    @Test
    void whenIncrementedIsCalledThenIncrementedVaccineStock() throws VaccineNotFoundException, VaccineStockExceededException {
        // given
        VaccineDTO expectedVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine expectedVaccine = vaccineMapper.toModel(expectedVaccineDTO);

        //when
        when(vaccineRepository.findById(expectedVaccineDTO.getId())).thenReturn(Optional.of(expectedVaccine));
        when(vaccineRepository.save(expectedVaccine)).thenReturn(expectedVaccine);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedVaccineDTO.getQuantity() + quantityToIncrement;

        //then
        VaccineDTO incrementVaccineDTO = vaccineService.increment(expectedVaccineDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementVaccineDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedVaccineDTO.getMax()));
    }
    @Test
    void whenIncrementedIsGreaterThanMaxThrowException(){
        // given
        VaccineDTO expectedVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine expectedVaccine = vaccineMapper.toModel(expectedVaccineDTO);

        //when
        when(vaccineRepository.findById(expectedVaccineDTO.getId())).thenReturn(Optional.of(expectedVaccine));

        int quantityToIncrement = 500;
        assertThrows(VaccineStockExceededException.class, () -> vaccineService.increment(expectedVaccineDTO.getId(), quantityToIncrement));
    }
    @Test
    void whenIncrementedAfterSumIsGreaterThanMaxThrowException(){
        // given
        VaccineDTO expectedVaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        Vaccine expectedVaccine = vaccineMapper.toModel(expectedVaccineDTO);

        //when
        when(vaccineRepository.findById(expectedVaccineDTO.getId())).thenReturn(Optional.of(expectedVaccine));

        int quantityToIncrement = 491;
        assertThrows(VaccineStockExceededException.class, () -> vaccineService.increment(expectedVaccineDTO.getId(), quantityToIncrement));
    }

}
