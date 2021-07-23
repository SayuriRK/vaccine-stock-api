package one.digitalinnovation.vaccinestockapi.builder;

import lombok.Builder;
import one.digitalinnovation.vaccinestockapi.dto.VaccineDTO;
import one.digitalinnovation.vaccinestockapi.enums.TechnologyType;

@Builder//allows to create a test in a fluid manner and return object with all the values filled
public class VaccineDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "mRNA-1273";

    @Builder.Default
    private String company = "Moderna Therapeutics";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private TechnologyType type = TechnologyType.RNA;

    public VaccineDTO toVaccineDTO() {
        return new VaccineDTO(id,
                name,
                company,
                max,
                quantity,
                type);
    }

}
