package gr.aueb.cf.finalprojectcf.validator;

import gr.aueb.cf.finalprojectcf.dto.BookDTO;
import gr.aueb.cf.finalprojectcf.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class BookValidator implements Validator {

    private final IBookService bookService;

    @Autowired
    public BookValidator(IBookService bookService) {
        this.bookService = bookService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return BookDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        BookDTO bookDTO = (BookDTO) target;

        ValidationUtils.rejectIfEmpty(errors, "title", "empty");
        ValidationUtils.rejectIfEmpty(errors, "author", "empty");
        ValidationUtils.rejectIfEmpty(errors, "description", "empty");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publishedYear", "empty");

        if (bookDTO.getTitle().length() < 5 || bookDTO.getTitle().length() > 80) {
            errors.rejectValue("title", "size");
        }
        if (bookDTO.getDescription().length() > 240) {
            errors.rejectValue("description", "size.description");
        }
        if (bookDTO.getPrice() < 1 || bookDTO.getPrice() > 210) {
            errors.rejectValue("price", "value");
        }

        if (bookDTO.getPublishedYear() < 1 ) {
            errors.rejectValue("publishedYear", "date");
        }
    }

}
