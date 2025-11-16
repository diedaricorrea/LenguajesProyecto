package com.example.Ejemplo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    // Recursos públicos
                    .requestMatchers("/register", "/register/save", "/login", "/css/**", "/js/**", "/imagenes/**", "/data/**").permitAll()
                    
                    // Catálogo - Requiere permiso PRODUCTOS_VER
                    .requestMatchers("/catalogo", "/catalogo/**").hasAnyAuthority("PRODUCTOS_VER", "ROLE_ADMINISTRADOR", "ROLE_USUARIO", "ROLE_TRABAJADOR")
                    
                    // Carrito - Requiere permiso CARRITO_GESTIONAR
                    .requestMatchers("/carrito/**").hasAnyAuthority("CARRITO_GESTIONAR", "ROLE_ADMINISTRADOR", "ROLE_USUARIO", "ROLE_TRABAJADOR")
                    
                    // Productos Admin - Requiere permisos específicos
                    .requestMatchers("/productos/crear", "/productos/nuevo").hasAnyAuthority("PRODUCTOS_CREAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    .requestMatchers("/productos/editar/**", "/productos/actualizar/**").hasAnyAuthority("PRODUCTOS_EDITAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    .requestMatchers("/productos/eliminar/**").hasAnyAuthority("PRODUCTOS_ELIMINAR", "ROLE_ADMINISTRADOR")
                    .requestMatchers("/productos", "/productos/**").hasAnyAuthority("PRODUCTOS_GESTIONAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    
                    // Categorías - Requiere permisos específicos
                    .requestMatchers("/categorias/crear", "/categorias/nuevo").hasAnyAuthority("CATEGORIAS_CREAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    .requestMatchers("/categorias/editar/**", "/categorias/actualizar/**").hasAnyAuthority("CATEGORIAS_EDITAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    .requestMatchers("/categorias/eliminar/**").hasAnyAuthority("CATEGORIAS_ELIMINAR", "ROLE_ADMINISTRADOR")
                    .requestMatchers("/categorias", "/categorias/**").hasAnyAuthority("CATEGORIAS_VER", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    
                    // Pedidos - Usuario puede crear, Admin/Trabajador pueden gestionar
                    .requestMatchers("/admin/pedidos", "/admin/pedidos/**").hasAnyAuthority("PEDIDOS_GESTIONAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    .requestMatchers("/pedidos/pedir").hasAnyAuthority("PEDIDOS_CREAR", "ROLE_ADMINISTRADOR", "ROLE_USUARIO", "ROLE_TRABAJADOR")
                    .requestMatchers("/pedidos/**").hasAnyAuthority("PEDIDOS_VER", "PEDIDOS_GESTIONAR", "ROLE_ADMINISTRADOR", "ROLE_USUARIO", "ROLE_TRABAJADOR")
                    .requestMatchers("/pedidos").hasAnyAuthority("PEDIDOS_VER", "ROLE_ADMINISTRADOR", "ROLE_USUARIO", "ROLE_TRABAJADOR")
                    
                    // Usuarios - Solo admin
                    .requestMatchers("/usuarios/**").hasAnyAuthority("USUARIOS_VER", "ROLE_ADMINISTRADOR")
                    
                    // Ventas - Admin y Trabajador
                    .requestMatchers("/ventas", "/ventas/**").hasAnyAuthority("VENTAS_VER", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    
                    // Estadísticas - Admin y Trabajador
                    .requestMatchers("/api/estadisticas/**").hasAnyAuthority("ESTADISTICAS_VER", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    
                    // Menú del día - Admin y Trabajador
                    .requestMatchers("/menuDia/**").hasAnyAuthority("MENU_DIA_GESTIONAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    
                    // Notificaciones
                    .requestMatchers("/notificacion/notificar").hasAnyAuthority("NOTIFICACIONES_CREAR", "ROLE_ADMINISTRADOR", "ROLE_TRABAJADOR")
                    .requestMatchers("/notificacion/**").hasAnyAuthority("NOTIFICACIONES_VER", "ROLE_ADMINISTRADOR", "ROLE_USUARIO", "ROLE_TRABAJADOR")
                    
                    // Login redirect
                    .requestMatchers("/login2").authenticated()
                    
                    // Cualquier otra petición requiere autenticación
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/login2", true)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            )
            .exceptionHandling(exception -> exception
                    .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
