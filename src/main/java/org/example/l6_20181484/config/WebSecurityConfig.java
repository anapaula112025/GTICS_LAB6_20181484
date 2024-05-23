package org.example.l6_20181484.config;

import jakarta.servlet.http.HttpSession;
import org.example.l6_20181484.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {
    final UsuarioRepository usuarioRepository;
    final DataSource dataSource;//1er paso: instancia de la conexion a DB

    public WebSecurityConfig(DataSource dataSource, UsuarioRepository usuarioRepository) {
        this.dataSource = dataSource;//1er paso: instancia de la conexion a DB
        this.usuarioRepository = usuarioRepository;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //Para comparar el password el cual esta hasheado
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/openLoginWindow")//ruta donde estará la ventana de login
                .loginProcessingUrl("/submitLoginForm")//ruta donde se envía el formulario de login
                .successHandler((request, response, authentication) -> {//configurar para elegir a donde debe ir el usuario, en este caso, en función de su rol

                    DefaultSavedRequest defaultSavedRequest =
                            (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                    HttpSession session = request.getSession();//Se guarda en sesión la información del usuario
                    session.setAttribute("usuario", usuarioRepository.findByEmail(authentication.getName()));
                    //Devuelve el username del usuario autenticado

                    //si vengo por url -> defaultSR existe
                    if (defaultSavedRequest != null) {
                        String targetURl = defaultSavedRequest.getRequestURL();
                        new DefaultRedirectStrategy().sendRedirect(request, response, targetURl);
                    } else { //estoy viniendo del botón de login
                        String rol = "";
                        for (GrantedAuthority role : authentication.getAuthorities()) {//roles del usuario
                            rol = role.getAuthority();
                            break;
                        }

                        if (rol.equals("admin")) {
                            response.sendRedirect("/dispositivos");
                        } else {
                            response.sendRedirect("/dispositivos");
                        }
                    }
                });

        http.authorizeHttpRequests()//Protege las rutas y las subrutas
                .requestMatchers("/dispositivos", "/dispositivos/**").hasAnyAuthority("admin")//solo podra acceder el admin y logistica
                .anyRequest().permitAll();

        http.logout()//Para cerrar sesión
                .logoutSuccessUrl("/dispositivos")
                .deleteCookies("JSESSIONID")//configurar que Spring Security invalide la sesión gestionada por Spring Session.
                .invalidateHttpSession(true);//borre todos los objetos vinculados con la sesión

        return http.build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        //para loguearse sqlAuth -> username | password | enable
        String sqlAuth = "SELECT email,pwd,activo FROM usuario where email = ?";

        //para autenticación -> username, nombre del rol
        String sqlAuto = "SELECT u.email, r.nombre FROM usuario u " +
                "inner join rol r on u.idrol = r.idrol " +
                "where u.email = ?";

        users.setUsersByUsernameQuery(sqlAuth); //para obtener el email, password y estado
        users.setAuthoritiesByUsernameQuery(sqlAuto); //para obtener los roles de los usuarios en base al email

        return users;
    }


}
