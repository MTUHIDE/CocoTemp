package space.hideaway.controllers.login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import space.hideaway.model.User;
import space.hideaway.model.security.PasswordResetToken;
import space.hideaway.repositories.PasswordTokenRepository;
import space.hideaway.repositories.RoleRepository;
import space.hideaway.repositories.UserRepository;
import space.hideaway.services.PasswordResetService;
import space.hideaway.services.security.SecurityService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import space.hideaway.services.user.UserService;
import space.hideaway.services.user.UserToolsService;

import javax.management.relation.Role;
import java.util.Calendar;


@Controller
@RequestMapping("/resetPassword")
@SessionAttributes("user")
public class ResetPasswordController {

    private final UserService userService;
    private final SecurityService securityService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordTokenRepository passwordTokenRepository;

    @Autowired
    public ResetPasswordController(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            SecurityService securityService,
            UserService userService)
    {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.securityService = securityService;
        this.userService = userService;
    }

    @RequestMapping
    public String checkToken(WebRequest request, final Model modelMap, final @RequestParam("token") String token){
        if(token ==  null){
            modelMap.addAttribute("Error","Request Parameter token is null");
            return "error";
        }
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        if(passwordResetToken == null){
            modelMap.addAttribute("error","Error 404: Token not found or Invalid");
            return "error";
        }
        Calendar cal = Calendar.getInstance();
        if(passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0){
            modelMap.addAttribute("error","Error 500: Link Expired");
            return "error";
        }
        modelMap.addAttribute("user", new User());
        return "password/resetPassword";
        }



    @RequestMapping(method = RequestMethod.POST)
    public String CheckUser(
            @RequestParam("token") String token,
            final @ModelAttribute("password") String password,
            final @ModelAttribute("confirmationPassword") String conPass) {


        User user = passwordTokenRepository.findByToken(token).getUser();
            user.setPassword(bCryptPasswordEncoder.encode(password));
            userService.update(user);
            securityService.autoLogin(user.getUsername(), user.getPassword());
            passwordTokenRepository.delete(passwordTokenRepository.findByToken(token));
            return "redirect:/home";
    }
}
