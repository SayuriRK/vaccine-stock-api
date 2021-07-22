package one.digitalinnovation.vaccinestockapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnologyType {

    modRNA ("modRNA"),
    Inactive ("Inactive"),
    Adenovirus("Non-Replicating Adenovirus");

    private final String description;
}
