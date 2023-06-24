package gr.aueb.cf.finalprojectcf.validator;

import gr.aueb.cf.finalprojectcf.dto.BoardGameDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class BoardGameValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return BoardGameDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        BoardGameDTO boardGameDTO = (BoardGameDTO) target;

        ValidationUtils.rejectIfEmpty(errors, "title", "empty.title");
        ValidationUtils.rejectIfEmpty(errors, "description", "empty.description");
        ValidationUtils.rejectIfEmpty(errors, "manufacturer", "empty.manufacturer");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "empty.price");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publishedYear", "empty.publishedYear");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "numberOfPlayers", "empty.numberOfPlayers");

        if (boardGameDTO.getTitle().length() < 4 || boardGameDTO.getTitle().length() > 80) {
            errors.rejectValue("title", "size.title");
        }
        if (boardGameDTO.getDescription().length() > 240) {
            errors.rejectValue("description", "size.description");
        }
        if (boardGameDTO.getPrice() < 1 || boardGameDTO.getPrice() > 850) {
            errors.rejectValue("price", "value.price");
        }

        if (boardGameDTO.getPublishedYear() < 1 ) {
            errors.rejectValue("publishedYear", "date.publishedYear");
        }
    }
}
