package com.example.booking.handleLogin;

import com.example.booking.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class LoginSuccess extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    public LoginSuccess(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("USER"));

            if (isUser) {
                userService.incrementLoginCount(userDetails.getUsername());
            }
        }
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        boolean isAdmin = false;
        boolean isEmployee = false;
        boolean isUser = false;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ADMIN")) {
                isAdmin = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("EMPLOYEE")) {
                isEmployee = true;
            } else if (grantedAuthority.getAuthority().equals("USER")) {
                isUser = true;
            }
        }

        if (isAdmin || isEmployee) {
            return "/hotels/homeAdmin";
        } else if (isUser) {
            return "/homeUser";
        } else {
            throw new IllegalStateException("Unexpected role found");
        }
    }
}

