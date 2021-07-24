package one.digitalinnovation.vaccinestockapi.controller;

import one.digitalinnovation.vaccinestockapi.builder.VaccineDTOBuilder;
import one.digitalinnovation.vaccinestockapi.dto.QuantityDTO;
import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.exception.VaccineNotFoundException;
import one.digitalinnovation.vaccinestockapi.service.VaccineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static one.digitalinnovation.vaccinestockapi.utils.JsonConversionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VaccineControllerTest {

    private static final String VACCINE_API_URL_PATH = "/api/v1/vaccine";
    private static final long VALID_VACCINE_ID = 1L;
    private static final long INVALID_VACCINE_ID = 2l   ;
    private static final String VACCINE_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String VACCINE_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private VaccineService vaccineService;

    @InjectMocks
    private VaccineController vaccineController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vaccineController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenVaccineIsCreated() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        when(vaccineService.createVaccine(vaccineDTO)).thenReturn(vaccineDTO);

        //then
        mockMvc.perform(post(VACCINE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(vaccineDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(vaccineDTO.getName())))
                .andExpect(jsonPath("$.company", is(vaccineDTO.getCompany())))
                .andExpect(jsonPath("$.type", is(vaccineDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenErrorIsReturned() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        vaccineDTO.setCompany(null);

        //then
        mockMvc.perform(post(VACCINE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(vaccineDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        when(vaccineService.findByName(vaccineDTO.getName())).thenReturn(vaccineDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(VACCINE_API_URL_PATH + "/" + vaccineDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(vaccineDTO.getName())))
                .andExpect(jsonPath("$.company", is(vaccineDTO.getCompany())))
                .andExpect(jsonPath("$.type", is(vaccineDTO.getType().toString())));
    }
    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusReturned() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        when(vaccineService.findByName(vaccineDTO.getName())).thenThrow(VaccineNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(VACCINE_API_URL_PATH + "/" + vaccineDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void whenGETListWithVaccineIsCalledThenOkStatusIsReturned() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        when(vaccineService.listAll()).thenReturn(Collections.singletonList(vaccineDTO));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(VACCINE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(vaccineDTO.getName())))
                .andExpect(jsonPath("$[0].company", is(vaccineDTO.getCompany())))
                .andExpect(jsonPath("$[0].type", is(vaccineDTO.getType().toString())));
    }
    @Test
    void whenGETListWithoutVaccineIsCalledThenOkStatusIsReturned() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        when(vaccineService.listAll()).thenReturn(Collections.singletonList(vaccineDTO));

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(VACCINE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void whenDELETEIsCalledWithValidNameThenNoContentStatusReturned() throws Exception {
        //given
        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();

        //when
        doNothing().when(vaccineService).deleteById(vaccineDTO.getId());

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(VACCINE_API_URL_PATH + "/" + vaccineDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    @Test
    void whenDELETEIsCalledWithInvalidNameThenNotFoundStatusReturned() throws Exception {

        //when
        doThrow(VaccineNotFoundException.class).when(vaccineService).deleteById(INVALID_VACCINE_ID);

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(VACCINE_API_URL_PATH + "/" + INVALID_VACCINE_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        VaccineDTO vaccineDTO = VaccineDTOBuilder.builder().build().toVaccineDTO();
        vaccineDTO.setQuantity(vaccineDTO.getQuantity() + quantityDTO.getQuantity());

        when(vaccineService.increment(VALID_VACCINE_ID, quantityDTO.getQuantity())).thenReturn(vaccineDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(VACCINE_API_URL_PATH + "/" + VALID_VACCINE_ID + VACCINE_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(vaccineDTO.getName())))
                .andExpect(jsonPath("$.company", is(vaccineDTO.getCompany())))
                .andExpect(jsonPath("$.type", is(vaccineDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(vaccineDTO.getQuantity())));
    }
}

