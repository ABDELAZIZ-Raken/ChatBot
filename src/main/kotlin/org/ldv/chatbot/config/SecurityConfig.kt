package org.ldv.chatbot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    // Hachage BCrypt des mots de passe
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .authorizeHttpRequests { auth ->
                auth
                    // Ressources statiques : accessibles à tous
                    .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/images/**",
                        "/logos/**",
                        "/vendor/**"
                    ).permitAll()

                    // Accueil : visible par tout le monde, connecté ou non
                    .requestMatchers("/", "/accueil").permitAll()

                    // Administration : réservée au rôle ROLE_ADMIN
                    .requestMatchers("/administration/**", "/plan-du-site").hasRole("ADMIN")

                    // Tout le reste nécessite d'être connecté (doit rester en dernier)
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/chat", true)
                    .failureUrl("/login?error")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            }

        return http.build()
    }
}