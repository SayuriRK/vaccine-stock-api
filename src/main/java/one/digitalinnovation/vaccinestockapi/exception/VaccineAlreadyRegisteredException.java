package one.digitalinnovation.vaccinestockapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VaccineAlreadyRegisteredException extends Exception{

    public VaccineAlreadyRegisteredException (String vaccinename) {
        super(String.format("Vaccine with name %s already registered.", vaccinename));
    }
}
