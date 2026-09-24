/*
 * Copyright © 2026 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.recia.pronote.pronoteapi.config.custom.impl;

import fr.recia.pronote.pronoteapi.config.bean.MockProperties;
import fr.recia.pronote.pronoteapi.config.bean.ProfilsProperties;
import fr.recia.pronote.pronoteapi.util.UserAttributesHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MockAuthenticationFilter extends OncePerRequestFilter {

    private final MockProperties mockProperties;
    private final ProfilsProperties profilsProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String profil = switch (mockProperties.getScenario()) {
            case eleve -> profilsProperties.getEleveProfilName();
            case parentUnEnfant, parentDeuxEnfants -> profilsProperties.getParentProfilName();
        };

        Map<String, Object> attributes = Map.of(
                UserAttributesHandler.ENT_PERSON_PROFILS, profil,
                UserAttributesHandler.UID, "mock-uid",
                UserAttributesHandler.UAI_CURRENT, "0000000X"
        );

        UserCustomImplementation principal = new UserCustomImplementation(
                "mock-user", "", List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        filterChain.doFilter(request, response);
    }
}

