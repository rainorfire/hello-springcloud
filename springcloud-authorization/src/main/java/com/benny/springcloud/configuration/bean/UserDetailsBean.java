package com.benny.springcloud.configuration.bean;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 0:47
 * @since 1.0
 */
@Data
public class UserDetailsBean implements UserDetails {

    private Long id;

    private String password;

    private String username;

    private List<String> roleList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(CollectionUtils.isEmpty(roleList)) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> result = new ArrayList<>();
        for(String role : roleList) {
            result.add((GrantedAuthority)() -> role);
        }
        return result;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }
}
