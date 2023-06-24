package gr.aueb.cf.finalprojectcf.validator;

import gr.aueb.cf.finalprojectcf.dto.UserDTO;
import gr.aueb.cf.finalprojectcf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final IUserService userService;

    @Autowired
    public UserValidator(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role", "empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "imgUrl", "empty");

        if (userService.emailExists(userDTO.getEmail())) {
            errors.rejectValue("email", "emailTaken");
        }

        if (userService.usernameExists(userDTO.getUsername())) {
            errors.rejectValue("username", "usernameTaken");
        }
    }
}
