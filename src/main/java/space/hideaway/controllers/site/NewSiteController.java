package space.hideaway.controllers.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import space.hideaway.model.globe.Globe;
import space.hideaway.model.site.Site;
import space.hideaway.model.site.SiteMetadata;
import space.hideaway.repositories.GlobeRepository;
import space.hideaway.services.site.SiteService;
import space.hideaway.validation.SiteValidator;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Edited by Justin Havely
 * 8/18/17
 *
 * The controller for the multi-page new site registration. Contains a session attribute "site"
 * to persist a template site across the multiple pages of the new site registration route.
 *
 * Route: new-site-form -> site-questionnaire -> globe-questionnaire -> dashboard (aka "My Sites")
 */
@Controller
@RequestMapping("/settings/new")
@SessionAttributes("site")
public class NewSiteController
{

    private final SiteValidator siteValidator;
    private final GlobeRepository globeRepository;
    private final SiteService siteService;

    @Autowired
    public NewSiteController(
            SiteValidator siteValidator,
            SiteService siteService,
            GlobeRepository globeRepository)
    {
        this.globeRepository = globeRepository;
        this.siteValidator = siteValidator;
        this.siteService = siteService;
    }

    /**
     * Load the initial page (new-site-form) of the new site registration route. Injects a blank
     * site into the model for the user to manipulate over the next few pages.
     * The session begins with this page. This page contains the Site name, Latitude,
     * and Longitude fields.
     *
     * @param modelMap The model maintained by Spring for the new site route.
     * @return The path to the new-site-form template.
     */
    @RequestMapping
    public String initialPage(final ModelMap modelMap)
    {
        // Add a blank site to the session's model.
        modelMap.addAttribute("site", new Site());
        return "new-site/new-site-form";
    }

    /**
     * The second page (site-questionnaire) of the new site registration route.
     * This page contains the site's description field.
     *
     * @param site The site that exists in the model with fields populated by what the user
     *             entered into the form on the initial page.
     * @param bindingResult The error module for this page, allows bindings between the validation module
     *                      and the actual page.
     * @return The path to the site-questionnaire template, or the path to the new-site-form
     * if validation has failed on fields from the first page.
     */
    @RequestMapping(params = "_questions", method = RequestMethod.POST)
    public String questionPage(
            final @ModelAttribute("site") Site site,
            final BindingResult bindingResult)
    {
        // Perform validation on the Site name, Latitude, and Longitude fields.
        siteValidator.validate(site, bindingResult);
        // Redirect to the previous page if errors are present.
        if (bindingResult.hasErrors())
        {
            return "new-site/new-site-form";
        }

        return "new-site/site-questionnaire";
    }

    /**
     * The third page (globe-questionnaire) of the new site registration route.
     * This page contains the optional GLOBE survey.
     *
     * @param site The site that exists in the model with fields populated by what the user
     *             entered into the form on the initial page.
     * @param bindingResult The error module for this page, allows bindings between the validation module
     *                      and the actual page.
     * @return The path to the globe-questionnaire template, or the path to the site-questionnaire
     * if validation has failed on fields from the site-questionnaire page.
     */
    @RequestMapping(params = "_globe", method = RequestMethod.POST)
    public String globePage(
            final @ModelAttribute("site") Site site,
            final BindingResult bindingResult,
            Model model)
    {
        // Perform validation on the site's description field.
        siteValidator.validateDescription(site, bindingResult);
        // Redirect to the previous page if errors are present.
        if (bindingResult.hasErrors())
        {
            return "new-site/site-questionnaire";
        }

        model.addAttribute("metadata", new SiteMetadata());

        ArrayList<String> environments = new ArrayList<String>();
        environments.add("Natural");
        environments.add("Urban");
        model.addAttribute("environments", environments);

        ArrayList<String> purposes = new ArrayList<String>();
        purposes.add("Commercial Offices");
        purposes.add("Retail");
        purposes.add("Restaurant");
        model.addAttribute("purposes", purposes);

        ArrayList<String> times = new ArrayList<String>();
        for(int i = 0; i < 24; i++) {
            times.add(i+":00");
        }
        model.addAttribute("times", times);

        ArrayList<String> canopyTypes = new ArrayList<String>();
        canopyTypes.add("No Canopy");
        canopyTypes.add("Tree/Vegetation");
        canopyTypes.add("Shade Sail");
        canopyTypes.add("Pergola/Ramada");
        canopyTypes.add("Other Solid Roof");
        model.addAttribute("canopyTypes", canopyTypes);

        ArrayList<String> nearestWaterTypes = new ArrayList<String>();
        nearestWaterTypes.add("Swimming Pool");
        nearestWaterTypes.add("Large river");
        nearestWaterTypes.add("Small stream");
        nearestWaterTypes.add("Lake/Pond");
        nearestWaterTypes.add("Other (describe)");
        model.addAttribute("nearestWaterTypes", nearestWaterTypes);

        return "new-site/globe-questionnaire";
    }

    /**
     * The final step (dashboard) of the new site registration route, if the GLOBE survey was skipped.
     *
     * @param site The site that exists in the model with fields populated by what the user
     *             entered into the form on the second page.
     * @param sessionStatus The session module for the new site registration route.
     * @return A redirect command to the dashboard.
     */
    @RequestMapping(params = "_finish", method = RequestMethod.POST)
    public String createSite(
            @ModelAttribute("site") Site site,
            SessionStatus sessionStatus)
    {
        // Persist the site.
        siteService.save(site);
        // Set the session complete, as the site has been safely persisted.
        sessionStatus.setComplete();
        // Redirect to the dashboard.
        return "redirect:/dashboard";
    }

    /**
     * The final step (dashboard) of the new site registration route, if the GLOBE survey was not skipped.
     *
     * @param site The site that exists in the model with fields populated by what the user
     *             entered into the form on the second page.
     * @param sessionStatus The session module for the new site registration route.
     * @return A redirect command to the dashboard.
     */
    @RequestMapping(params = "_finish_globe", method = RequestMethod.POST)
    public String createGlobeSite(
            @ModelAttribute("site") Site site,
            @ModelAttribute("metadata") SiteMetadata metadata,
            final BindingResult bindingResult,
            SessionStatus sessionStatus)
    {


        // Set the session complete, as the site has been safely persisted.
        sessionStatus.setComplete();

        // Redirect to the dashboard.
        return "redirect:/dashboard";
    }
}
