package gr.aueb.cf.finalprojectcf.validator;

import gr.aueb.cf.finalprojectcf.dto.UpdateUserDTO;
import gr.aueb.cf.finalprojectcf.dto.UserDTO;
import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.service.IUserService;
import gr.aueb.cf.finalprojectcf.service.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class UpdateUserValidator implements Validator {

    private final IUserService userService;

    @Autowired
    public UpdateUserValidator(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateUserDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdateUserDTO updateUserDTO = (UpdateUserDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "imgUrl", "empty");

        if (!Objects.equals(updateUserDTO.getPassword(), updateUserDTO.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "confirmPasswordError");
        }

//        if (userService.emailExists(updateUserDTO.getEmail())) {
//            errors.rejectValue("email", "emailTaken");
//        }
//
//        if (userService.usernameExists(updateUserDTO.getUsername()) && ) {
//            errors.rejectValue("username", "usernameTaken");
//        }

    }
}
