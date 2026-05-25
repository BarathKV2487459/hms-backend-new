package com.symmetricsquad.hms_backend.config;

import com.symmetricsquad.hms_backend.security.JwtAuthenticationEntryPoint;
import com.symmetricsquad.hms_backend.security.JwtAuthenticationFilter;
import com.symmetricsquad.hms_backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // ── Fix 1: PasswordEncoder bean — was missing entirely ───────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ── Fix 2: Filter as a bean so Spring manages its lifecycle ──────────────
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .authorizeHttpRequests(auth -> auth

                        // ── Public ────────────────────────────────────────────────────
//                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/contact-queries").permitAll()
//                        .requestMatchers(HttpMethod.GET,  "/api/doctors/**").permitAll()
//                        .requestMatchers(HttpMethod.GET,  "/api/doctor-specializations").permitAll()
//                        .requestMatchers(HttpMethod.GET,  "/api/doctor-specializations/**").permitAll()
//
//                        // ── Specializations (admin only) ──────────────────────────────
//                        .requestMatchers(HttpMethod.POST,   "/api/doctor-specializations").hasAuthority("ADMIN")
//                        .requestMatchers(HttpMethod.PATCH,  "/api/doctor-specializations/**").hasAuthority("ADMIN") // Fix 3: was PUT
//                        .requestMatchers(HttpMethod.DELETE, "/api/doctor-specializations/**").hasAuthority("ADMIN")
//
//                        // ── Users ─────────────────────────────────────────────────────
//                        .requestMatchers(HttpMethod.GET,    "/api/users/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.PUT,    "/api/users/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyAuthority("DOCTOR", "ADMIN")
//
//                        // ── Doctors ───────────────────────────────────────────────────
//                        .requestMatchers(HttpMethod.POST,   "/api/doctors").hasAuthority("ADMIN")
//                        .requestMatchers(HttpMethod.PUT,    "/api/doctors/**").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/doctors/**").hasAuthority("ADMIN")
//
//                        // ── Patients ──────────────────────────────────────────────────
//                        .requestMatchers(HttpMethod.POST, "/api/patients").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,  "/api/patients/doctor/**").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,  "/api/patients/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.PUT,  "/api/patients/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/patients/**").hasAnyAuthority("DOCTOR", "ADMIN")
//
//                        // ── Medical history ───────────────────────────────────────────
//                        .requestMatchers(HttpMethod.POST, "/api/patients/*/medical-history").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,  "/api/patients/*/medical-history").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.PUT,  "/api/patients/medical-history/**").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/patients/medical-history/**").hasAuthority("ADMIN")
//
//                        // ── Appointments ──────────────────────────────────────────────
//                        .requestMatchers(HttpMethod.POST,  "/api/appointments").hasAnyAuthority("PATIENT", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,   "/api/appointments/slot-available").hasAnyAuthority("PATIENT", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,   "/api/appointments/user/**").hasAnyAuthority("PATIENT", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,   "/api/appointments/doctor/**").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.GET,   "/api/appointments/**").hasAnyAuthority("PATIENT", "DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel-by-user").hasAnyAuthority("PATIENT", "ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel-by-doctor").hasAnyAuthority("DOCTOR", "ADMIN")
//                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/complete").hasAnyAuthority("DOCTOR", "ADMIN")
//
//                        // ── Admin ─────────────────────────────────────────────────────
//                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
//
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        // Fix 2: use the bean, not new JwtAuthenticationFilter(...)
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}