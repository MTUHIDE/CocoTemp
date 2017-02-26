package space.hideaway.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import space.hideaway.model.User;
import space.hideaway.services.UserManagementImpl;


/**
 * The controller responsible for displaying the dashboard page.
 */
@Controller
public class DashboardController
{

    /**
     * The service responsible for user CRUD operations.
     */
    private final UserManagementImpl userManagementImpl;


    /**
     * Obtain the default temperature unit from the application.properties file and inject
     * it into a variable for later use.
     */
    @Value("${cocotemp.temperature.unit}")
    String temperatureUnit;

    @Autowired
    public DashboardController(
            UserManagementImpl userManagementImpl)
    {
        this.userManagementImpl = userManagementImpl;
    }

    /**
     * The endpoint for the dashboard view.
     * Secured: Yes
     * Method: GET
     *
     * Sample URL: /dashboard
     *
     * @param model The model maintained by Spring for the dashboard page.
     * @return The path to the dashboard template.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model)
    {
        //Obtain the logged in user.
        User user = userManagementImpl.getCurrentLoggedInUser();
        model.addAttribute("greeting", "Hello, " + user.getFirstName());

        //Refers to dashboard.html.
        return "dashboard";
    }
}
