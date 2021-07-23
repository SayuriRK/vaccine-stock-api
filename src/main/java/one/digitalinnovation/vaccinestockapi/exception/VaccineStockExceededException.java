package one.digitalinnovation.vaccinestockapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VaccineStockExceededException extends Exception {

    public VaccineStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Vaccine with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
