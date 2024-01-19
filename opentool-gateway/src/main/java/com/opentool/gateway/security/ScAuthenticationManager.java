package com.opentool.gateway.security;

import com.opentool.gateway.holder.ReactiveRemoteRoleService;
import com.opentool.gateway.holder.ReactiveRemoteUserService;
import com.opentool.gateway.security.domain.SecurityUserDetails;
import com.opentool.system.api.domain.SysRole;
import com.opentool.system.api.domain.SysUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 登录认证管理
 * 2. 从AuthenticationToken读取Token并做用户数据解析
 *
 * / @Author: ZenSheep
 * / @Date: 2023/12/28 15:19
 */
@Slf4j
@Component
public class ScAuthenticationManager implements ReactiveAuthenticationManager {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private ReactiveRemoteUserService reactiveRemoteUserService;

    @Autowired
    private ReactiveRemoteRoleService reactiveRemoteRoleService;

    /**
     * 认证
     * @param authentication
     * @return
     */
    @SneakyThrows
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        // 校验token
        log.info("ScAuthenticationManager.authenticate()");
        String username = authentication.getName(); // 获取用户提供的用户名（账号）
        log.info("username:{}", username);
        String password = authentication.getCredentials().toString(); //获取用户提供的密码
        log.info("password:{}", password);

        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("用户名为空");
        }

        // 调用数据库根据用户名获取用户(需要保证用户名唯一)
        SysUser user = reactiveRemoteUserService.findUserByUserName(username).get();

        if (user == null) {
            // 用户名错误或不存在
            throw new UsernameNotFoundException("用户不存在");
        }

        if (!password.equals(user.getPassword())) {
            throw new UsernameNotFoundException("用户密码不正确");
        }

        // 登录成功，进行授权
        SysRole sysRole = reactiveRemoteRoleService.getSysRoleById(user.getRoleId()).get();
        String role = sysRole.getRoleKey();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        // 返回securityUserDetails对象后会自定校验用户密码是否正确
        SecurityUserDetails securityUserDetails = new SecurityUserDetails(username, "{bcrypt}" + passwordEncoder.encode(user.getPassword()), authorities, user.getUserId());

        return Mono.just(new UsernamePasswordAuthenticationToken(securityUserDetails, securityUserDetails.getPassword(), securityUserDetails.getAuthorities()));
    }

    /**
     * 校验token
     * @param token
     * @return 校验成功返回用户信息，失败则返回null
     */
    private Map<String, Object> parseToken(String token) {
        // 读取token
        String jwtToken = getJwtToken(token);
        log.info("ScAuthenticationManager jwtToken = {}", jwtToken);

        // 模拟认证成功: 这里以后要替换成更加安全的jwt认证方法
//        if (jwtToken != null) {
//            // 解码token信息
//            Map<String, Object> userMap = JWTUtils.getTokenInfo(jwtToken);
//            // 通过用户名从redis缓存获取对应token
//            String redisToken = (String) redisTemplate.opsForValue().get(userMap.get("username"));
//            if (redisToken == null || !redisToken.equals(jwtToken)) {
//                // token过期或token不正确
//                return null;
//            }
//
//            // 返回用户信息
//            return userMap;
//        }

        return null;
    }

    /**
     * 读取Jwt Token
     * @param token
     * @return
     */
    private String getJwtToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        // 校验token开头(有需要再修改)
//        boolean valid = token.startsWith("Bearer ");
//        if (!valid) {
//            return null;
//        }
//        token = token.replace("Bearer ", "");

        return token;
    }
}