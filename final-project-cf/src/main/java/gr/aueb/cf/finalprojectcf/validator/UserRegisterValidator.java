package gr.aueb.cf.finalprojectcf.validator;

import gr.aueb.cf.finalprojectcf.dto.RegisterUserDTO;
import gr.aueb.cf.finalprojectcf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class UserRegisterValidator implements Validator {
    private final IUserService userService;

    @Autowired
    public UserRegisterValidator(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterUserDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterUserDTO registerUserDTO = (RegisterUserDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "imgUrl", "empty");

        if (!Objects.equals(registerUserDTO.getPassword(), registerUserDTO.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "confirmPasswordError");
        }

        if (userService.emailExists(registerUserDTO.getEmail())) {
            errors.rejectValue("email", "emailTaken");
        }

        if (userService.usernameExists(registerUserDTO.getUsername())) {
            errors.rejectValue("username", "usernameTaken");
        }
    }
}
