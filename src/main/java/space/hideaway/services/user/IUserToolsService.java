
package space.hideaway.services.user;

import space.hideaway.model.User;
import space.hideaway.model.security.VerificationToken;

public interface IUserToolsService {

    User findUserByEmail(String email);

    public void createVerificationTokenForUser(User user,String token);

    public VerificationToken getVerificationToken(String VerificationToken);

}