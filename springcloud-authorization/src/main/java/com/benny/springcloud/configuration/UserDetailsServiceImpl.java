package com.benny.springcloud.configuration;

import com.benny.springcloud.configuration.bean.UserDetailsBean;
import com.benny.springcloud.mapper.RoleMapper;
import com.benny.springcloud.mapper.UserMapper;
import com.benny.springcloud.mapper.UserRoleMapper;
import com.benny.springcloud.model.Role;
import com.benny.springcloud.model.RoleExample;
import com.benny.springcloud.model.User;
import com.benny.springcloud.model.UserExample;
import com.benny.springcloud.model.UserRole;
import com.benny.springcloud.model.UserRoleExample;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/5/8 0:44
 * @since 1.0
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        final List<User> users = userMapper.selectByExample(example);
        final User user = users.get(0);

        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(user.getId());
        final List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);

        final List<Long> roleIdList = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdIn(roleIdList);
        final List<Role> roles = roleMapper.selectByExample(roleExample);
        final List<String> roleNameList = roles.stream().map(Role::getName).collect(Collectors.toList());

        UserDetailsBean userDetailsBean = new UserDetailsBean();
        userDetailsBean.setId(user.getId());
        userDetailsBean.setPassword(user.getPassword());
        userDetailsBean.setUsername(user.getUsername());
        userDetailsBean.setRoleList(roleNameList);

        return userDetailsBean;
    }
}
