package one.digitalinnovation.vaccinestockapi.controller;

import one.digitalinnovation.vaccinestockapi.builder.VaccineDTOBuilder;
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

import static one.digitalinnovation.vaccinestockapi.utils.JsonConversionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class VaccineControllerTest {

    private static final String VACCINE_API_URL_PATH = "/api/v1/vaccine";
    private static final long VALID_VACCINE_ID = 1L;
    private static final long INVALID_VACCINE_ID = 2l;
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
}

