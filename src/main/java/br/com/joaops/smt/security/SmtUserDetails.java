/*
 * Copyright (C) 2016 João
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.joaops.smt.security;

import br.com.joaops.smt.dto.SystemUserDto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author João
 */
public class SmtUserDetails implements UserDetails, CredentialsContainer, Serializable {
    
    private final SystemUserDto user;
    private final List<GrantedAuthority> authorities;
    
    public SmtUserDetails(SystemUserDto user) {
        this.user = user;
        this.authorities = new ArrayList<>();
        this.authorities.clear();
    }
    
    public SystemUserDto getUser() {
        return user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
    
    @Override
    public String getPassword() {
        return this.getUser().getPassword();
    }
    
    @Override
    public String getUsername() {
        return this.getUser().getEmail();
    }
    
    public String getDatabaseName() {
        return this.getUser().getSystemDatabase().getName(); //Pegar o nome do banco de dados
    }
    
    @Override
    public boolean isAccountNonExpired() {
        if (this.getUser().getAccountCanExpire()) {
            Calendar expirationDate = Calendar.getInstance();
            expirationDate.setTime(this.getUser().getAccountExpiration());
            if (Calendar.getInstance().after(expirationDate)) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            return Boolean.TRUE;
        }
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return !this.getUser().getLocked();
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        if (this.getUser().getCredentialCanExpire()) {
            Calendar expirationDate = Calendar.getInstance();
            expirationDate.setTime(this.getUser().getCredentialExpiration());
            if (Calendar.getInstance().after(expirationDate)) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        } else {
            return Boolean.TRUE;
        }
    }
    
    @Override
    public boolean isEnabled() {
        return this.getUser().getEnabled();
    }
    
    @Override
    public void eraseCredentials() {
        this.getUser().setPassword(null);
    }
    
}