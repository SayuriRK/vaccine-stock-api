package one.digitalinnovation.vaccinestockapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VaccineNotFoundException extends Exception {

    public VaccineNotFoundException(String name) {
        super(String.format("Vaccine with name %s was not found",name));
    }

    public VaccineNotFoundException (Long id) {
        super(String.format("Vaccine with ID %s was not found", id));}
}
