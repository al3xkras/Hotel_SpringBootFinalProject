package ua.alexkras.hotel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ua.alexkras.hotel.controller.CustomLogoutSuccessHandler;
import ua.alexkras.hotel.model.HotelUserDetailsService;
import ua.alexkras.hotel.model.NoPasswordEncoder;
import ua.alexkras.hotel.model.UserType;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    public SecurityConfig(CustomLogoutSuccessHandler logoutSuccessHandler){
        this.logoutSuccessHandler=logoutSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/registration").anonymous()
                .antMatchers("/user/**").hasAuthority(UserType.USER.name())
                .antMatchers("/admin/**").hasAnyAuthority(UserType.ADMIN.name())
                .antMatchers("/add_apartment").hasAnyAuthority(UserType.ADMIN.name())
                .antMatchers("/error").permitAll()
                .antMatchers("/apartment").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/auth/login").permitAll()
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout","POST"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(logoutSuccessHandler())
                .logoutSuccessUrl("/");
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return logoutSuccessHandler;
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new HotelUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new NoPasswordEncoder();
    }

}
