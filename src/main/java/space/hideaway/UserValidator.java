package space.hideaway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import space.hideaway.model.User;
import space.hideaway.services.UserService;

/**
 * HIDE CoCoTemp 2016
 * Class responsible for validating user properties and passing possible errors to a binding result.
 */
@Component
public class UserValidator implements Validator {

    /**
     * The service responsible for CRUD operations on users.
     */
    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    /**
     * Validate a user.
     *
     * @param o      The user object to be validated.
     * @param errors A BindingResult object for errors to be passed to.
     */
    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        try {
            User byUsername = userService.findByUsername(user.getUsername());
            errors.rejectValue("username", "Duplicate.userForm.username");
        } catch (UserNotFoundException ignored) {
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }

        if (!user.getConfirmationPassword().equals(user.getPassword())) {
            errors.rejectValue("confirmationPassword", "Diff.userForm.passwordConfirm");
        }
    }
}
