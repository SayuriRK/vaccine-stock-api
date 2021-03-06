package one.digitalinnovation.vaccinestockapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import one.digitalinnovation.vaccinestockapi.enums.TechnologyType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineDTO {

    private Long id;

    @NotNull
    @Size (min = 1, max = 20)
    private String name;

    @NotNull
    @Size (min = 1, max = 20)
    private String company;

    @NotNull
    @Max(500)
    private Integer max;

    @NotNull
    @Max(500)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TechnologyType type;

}
