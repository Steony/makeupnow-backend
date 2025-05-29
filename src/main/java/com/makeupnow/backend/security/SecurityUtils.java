package com.makeupnow.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities()
                          .stream()
                          .map(GrantedAuthority::getAuthority)
                          .findFirst()
                          .orElse(null);
    }

    public static boolean isCurrentUserAdmin() {
        return "ROLE_ADMIN".equals(getCurrentUserRole());
    }

    public static boolean isCurrentUserCustomer() {
        return "ROLE_CLIENT".equals(getCurrentUserRole());
    }

    public static boolean isCurrentUserProvider() {
        return "ROLE_PROVIDER".equals(getCurrentUserRole());
    }
}
