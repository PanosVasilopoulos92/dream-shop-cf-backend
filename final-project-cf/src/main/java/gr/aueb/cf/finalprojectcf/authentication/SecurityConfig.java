package gr.aueb.cf.finalprojectcf.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(CustomAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    // Registers our custom authenticationProvider to the AuthenticationManager.
    @Autowired
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    // Declare the users rights for every endpoint we want.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/login").permitAll()
                .antMatchers(HttpMethod.POST,"/api/login").permitAll()
                .antMatchers(HttpMethod.POST,"/api/users/register").permitAll()
//                .antMatchers(HttpMethod.POST,"/users/findOne/{username}").permitAll()
                .antMatchers(HttpMethod.GET, "/books/findOne/{bookId}").permitAll()
                .antMatchers(HttpMethod.GET, "/books/findAll-pages").permitAll()
                .antMatchers(HttpMethod.GET, "/books/findAll").permitAll()
                .antMatchers(HttpMethod.GET, "/books/find/**").permitAll()
                .antMatchers(HttpMethod.GET, "/boardGames/findOne/{boardGameId}").permitAll()
                .antMatchers(HttpMethod.GET, "/boardGames/find/**").permitAll()
                .antMatchers(HttpMethod.GET, "/videoGames/findOne/{videoGameId}").permitAll()
                .antMatchers(HttpMethod.GET, "/videoGames/find/**").permitAll()
//                .antMatchers(HttpMethod.PUT,"/api/users/{userId}/update-role").hasRole("ADMIN")
//                .antMatchers(HttpMethod.GET,"api/users/findOne/{username}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.GET,"api/users/find-users").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE,"api/users/delete/{username}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST,"/api/books/create").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT,"api/books/update/{bookId}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE,"api/books/delete/{bookId}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST,"api/boardGames/create").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT,"api/boardGames/update/{boardGameId}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE,"api/boardGames/delete/{boardGameId}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST,"api/videoGames/create").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT,"api/videoGames/update/{videoGameId}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE,"api/videoGames/delete/{videoGameId}").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        return http.build();
    }

//     We declare every file that user has access without authentication.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/styles/**", "/img/**", "/js/**");
    }


    // We override the default AuthenticationManager.
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
