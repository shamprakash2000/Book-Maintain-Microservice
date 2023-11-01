//package com.example.User_Service.Util;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//        import org.springframework.security.access.AccessDeniedException;
//        import org.springframework.security.core.AuthenticationException;
//        import org.springframework.security.web.access.AccessDeniedHandler;
//        import org.springframework.stereotype.Component;
//        import javax.servlet.ServletException;
//        import javax.servlet.http.HttpServletRequest;
//        import javax.servlet.http.HttpServletResponse;
//        import java.io.IOException;
//
//@Component
//public class CustomAccessDeniedHandler implements AccessDeniedHandler {
//
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
//            throws IOException, ServletException {
//        // Customize the response here, e.g., set a custom error message
//       // ErrorResponse errorResponse = new ErrorResponse("Access denied: You don't have permission to access this resource");
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // HTTP 403
//
//        // Serialize the error response as JSON and write it to the response
//        response.setContentType("application/json");
//        response.getWriter().write("Access denied: You don't have permission to access this resource");
//    }
//}
