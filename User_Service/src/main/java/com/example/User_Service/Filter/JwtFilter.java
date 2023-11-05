package com.example.User_Service.Filter;



import com.example.User_Service.Service.MyUserDetailsService;
import com.example.User_Service.Service.TokenBlacklistService;
import com.example.User_Service.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
                        System.out.println("inside catch line 48");
                        response.setStatus(409);
                        //response.getWriter().write("\"Access denied: You don't have permission to access this resource\"");
                       // super.doFilter(request,response,filterChain);
                        throw new RuntimeException("Issue with extracting username from JWT.");

//                        filterChain.doFilter(request,response);
                    }

        }
        if(userName !=null && SecurityContextHolder.getContext().getAuthentication()==null && !tokenBlacklistService.isTokenBlacklisted(jwt)){
           UserDetails userDetails=this.myUserDetailsService.loadUserByUsername(userName);
            if(jwtUtil.validateToken(jwt,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                System.out.println("Current User: " + authentication.getName());
//                System.out.println("Authorities: " + authentication.getAuthorities());
            }
        }

        filterChain.doFilter(request,response);
    }
}

