package com.example.demo.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.model.User;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Devuelve una lista vacía ya que no hay roles asociados
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Asumiendo que la contraseña ya está codificada
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Reemplazar con lógica para determinar si el usuario está habilitado
        // Por ahora, simplemente retornamos true como un marcador de posición
        return true;
    }

    public String getNombre() {
        return user.getUsername();
    }
}
