package one.digitalinnovation.vaccinestockapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnologyType {

    RNA ("RNA"),
    INACTIVE ("INACTIVE"),
    ADENOVIRUS ("ADENOVIRUS");

    private final String description;
}
