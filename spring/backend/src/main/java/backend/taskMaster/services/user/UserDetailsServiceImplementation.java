package backend.taskMaster.services.user;

import backend.taskMaster.exceptions.user.SaveEditedUserException;
import backend.taskMaster.logging.BatchLogger;
import backend.taskMaster.logging.Loggable;
import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.models.user.UserEditRequest;
import backend.taskMaster.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    //@Loggable
    @BatchLogger
    @Override
    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        "User not found with username: "
                                + username)
        );
    }

    @Loggable
    public User editUserInformation(UserEditRequest userEditRequest, User user)
            throws SaveEditedUserException{
        try {
            user.setEmail(userEditRequest.getEmail());
            user.setSurname(userEditRequest.getSurname());
            user.setGivenName(userEditRequest.getGivenName());
            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new SaveEditedUserException("There was an error updating the user info");
        }
    }

    @Loggable
    public List<User> getAllStandardUsers() {
        return userRepository.findByRole(Role.USER);
    }
}
