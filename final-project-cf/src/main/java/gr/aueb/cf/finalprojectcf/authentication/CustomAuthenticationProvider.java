package gr.aueb.cf.finalprojectcf.authentication;

import gr.aueb.cf.finalprojectcf.model.User;
import gr.aueb.cf.finalprojectcf.repository.UserRepository;
import gr.aueb.cf.finalprojectcf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    private final IUserService userService;

    private final MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @Autowired
    public CustomAuthenticationProvider(UserRepository userRepository, IUserService userService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();     // The username provided by the form.
        String password = authentication.getCredentials().toString();   // The password provided by the form.

        User user = userRepository.findByUsernameEquals(username);
        if (user == null) throw new BadCredentialsException(accessor.getMessage("badCredentials"));

        String hashedPassword = user.getPassword();
        boolean isPasswordCorrect = userService.isPasswordCorrect(password, hashedPassword);

        if (!userRepository.isUserValid(username, hashedPassword) || !isPasswordCorrect) {
            throw new BadCredentialsException(accessor.getMessage("badCredentials"));
        }

        String role = user.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // Prefix "ROLE_" is required by Spring Security
        System.out.println(authorities);

        return new UsernamePasswordAuthenticationToken(username, password, authorities);

//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        return new UsernamePasswordAuthenticationToken(username, password, authorities);

//        return new UsernamePasswordAuthenticationToken(username, password, Collections.<GrantedAuthority>emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
