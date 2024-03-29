package com.example.User_Service.Filter;



import com.example.User_Service.Service.MyUserDetailsService;
import com.example.User_Service.Service.TokenBlacklistService;
import com.example.User_Service.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader=request.getHeader("Authorization");
        String userName=null;
        String jwt=null;

        if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            jwt=authorizationHeader.substring(7);
                    try{
                        userName = jwtUtil.extractUsername(jwt);
                    }
                    catch (Exception e){
                        response.setStatus(409);
                        throw new RuntimeException("Issue with extracting username from JWT.");
                    }

        }
        if(userName !=null && SecurityContextHolder.getContext().getAuthentication()==null && !tokenBlacklistService.isTokenBlacklisted(jwt)){
           UserDetails userDetails=this.myUserDetailsService.loadUserByUsername(userName);
            if(jwtUtil.validateToken(jwt,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}

