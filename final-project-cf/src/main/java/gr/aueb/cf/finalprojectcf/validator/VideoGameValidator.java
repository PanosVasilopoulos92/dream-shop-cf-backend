package gr.aueb.cf.finalprojectcf.validator;

import gr.aueb.cf.finalprojectcf.dto.VideoGameDTO;
import gr.aueb.cf.finalprojectcf.service.IVideoGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class VideoGameValidator implements Validator {

    private final IVideoGameService videoGameService;

    @Autowired
    public VideoGameValidator(IVideoGameService videoGameService) {
        this.videoGameService = videoGameService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return VideoGameDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        VideoGameDTO videoGameDTO = (VideoGameDTO) target;

        ValidationUtils.rejectIfEmpty(errors, "title", "empty.title");
        ValidationUtils.rejectIfEmpty(errors, "description", "empty.description");
        ValidationUtils.rejectIfEmpty(errors, "manufacturer", "empty.manufacturer");
        ValidationUtils.rejectIfEmpty(errors, "genre", "empty.genre");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "empty.price");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publishedYear", "empty.publishedYear");

        if (videoGameDTO.getTitle().length() < 4 || videoGameDTO.getTitle().length() > 80) {
            errors.rejectValue("title", "size");
        }
        if (videoGameDTO.getDescription().length() > 240) {
            errors.rejectValue("description", "size.description");
        }
        if (videoGameDTO.getPrice() < 1 || videoGameDTO.getPrice() > 160) {
            errors.rejectValue("price", "value");
        }
        if (videoGameDTO.getPublishedYear() < 1960 ) {
            errors.rejectValue("publishedYear", "date");
        }
    }
}
