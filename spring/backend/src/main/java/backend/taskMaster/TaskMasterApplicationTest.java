package backend.taskMaster;

import backend.taskMaster.models.user.Role;
import backend.taskMaster.models.user.User;
import backend.taskMaster.repositories.tasks.task.SingleTaskRepo;
import backend.taskMaster.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@Profile("test")
public class TaskMasterApplicationTest implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SingleTaskRepo singleTaskRepo;

    //@Autowired
    //private UserDetailsServiceImplementation userService;

    public static void main(String[] args) {
        SpringApplication
                .run(TaskMasterApplicationTest.class, args);
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("----------------Preparing Database for Tests---------------");
        // note: these mock users differ from the ones below (spaces in username)
        // The mock users deleted here are created in the UserRegistrationControllerEndpointTest class
        userRepository.deleteByUsername("Test User");
        userRepository.deleteByUsername("Test Admin");
        singleTaskRepo.deleteAll();
        if (userRepository.findAll().isEmpty()) {
            System.out.println("The user repo is empty. Creating mock users");
            User testUser = User.builder()
                    .givenName("Test")
                    .surname("User")
                    .username("TestUser")
                    .email("test@gmx.com")
                    .password(passwordEncoder.encode("testpassword"))
                    .role(Role.USER)
                    .build();
            userRepository.save(testUser);
            User testUser2 = User.builder()
                    .givenName("Test")
                    .surname("User2")
                    .username("TestUser2")
                    .email("test@gmx.com")
                    .password("testpassword")
                    .role(Role.USER)
                    .build();
            userRepository.save(testUser2);
            User testAdmin = User.builder()
                    .givenName("Test")
                    .surname("Admin")
                    .username("TestAdmin")
                    .email("test@gmx.com")
                    .password(passwordEncoder.encode("testpassword"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(testAdmin);
        }
        System.out.println("*****************Running Tests**********************");
    }
}
