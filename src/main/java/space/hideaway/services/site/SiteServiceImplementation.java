package space.hideaway.services.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import space.hideaway.model.site.Site;
import space.hideaway.model.User;
import space.hideaway.repositories.UserRepository;
import space.hideaway.repositories.site.SiteRepository;
import space.hideaway.services.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class SiteServiceImplementation implements SiteService
{

    private final UserService userService;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;


    @Autowired
    private Environment env;

    @Autowired
    public SiteServiceImplementation(
            UserService userService,
            SiteRepository siteRepository,
            UserRepository userRepository)
    {
        this.userService = userService;
        this.siteRepository = siteRepository;
        this.userRepository=userRepository;
    }

    /**
     * Save a new newSite into the database. Automatically sets the user who created the device.
     *
     * @param newSite The new newSite to be inserted.
     */
    @Override
    public Site save(Site newSite)
    {
        Long id = userService.getCurrentLoggedInUser().getId();
        newSite.setUserID(id);
        siteRepository.save(newSite);
        return newSite;
    }

    /**
     * Find a site by site ID.
     *
     * @param siteID The ID of the site to obtain.
     * @return The obtained site matching the given siteID.
     */
    @Override
    public Site findByKey(String siteID)
    {

        Optional<Site> site = siteRepository.findById(UUID.fromString(siteID));
        if(site.isPresent())
        {
            Site realSite= site.get();
            return realSite;
        }
        return null;
    }

    /**
     * Obtain a list of all sites.
     *
     * @return A list of all sites.
     */
    @Override
    public List<Site> getAllSites()
    {
        return siteRepository.findAll();
    }

    /**
     * Compare a siteID and a user, and determine whether the relationship between
     * them is valid.
     *
     * @param user     The user to compare to the site.
     * @param siteID The ID of the site to compare to the user.
     * @return True if the user owns the site, false otherwise.
     */
    @Override
    public boolean isCorrectUser(User user, String siteID)
    {
        if (user == null) return false;

        boolean found = false;
        Set<Site> siteSet = user.getSiteSet();

        for (Site site : siteSet)
        {
            if (site.getId().toString().equals(siteID)) found = true;
        }
        return found;
    }

    /**
     * Compare a siteID and the currently logged in user, and determine whether the relationship between
     * them is valid.
     *
     * @return True if the user owns the site, false otherwise.
     */
    public boolean isCorrectUser(String siteKey)
    {
        return isCorrectUser(userService.getCurrentLoggedInUser(), siteKey);
    }

    /**
     * Gets the count of sites created by an user.
     *
     * @param currentLoggedInUser The user id of the current login user
     * @return The number of sites.
     */
    @Override
    public Long countByUserID(User currentLoggedInUser)
    {
        return siteRepository.countByUserID(currentLoggedInUser.getId());
    }

    public void deleteSite(Site site){
        site.setUserID((long)Integer.parseInt(env.getProperty("spring.admin.id")));
        Optional<User> user = userRepository.findById((long)Integer.parseInt(env.getProperty("spring.admin.id")));
        User userNormal =null;
        if(user.isPresent()){
            userNormal = user.get();
        }
        site.setUser(userNormal);
        siteRepository.save(site);
    }

}
