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

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import fr.recia.pronote.pronoteapi.exception.LostTicketException;
import fr.recia.pronote.pronoteapi.util.LogMasking;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler simplifié pour le succès d'authentification CAS.
 * Répond en JSON pour les appels API, sinon redirige l'utilisateur.
 */
@Slf4j
@Component
public class CasSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CustomSessionMappingStorage redisService;

    @Autowired
    private ServletContext servletContext;

    @PostConstruct
    void init(){
        this.setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull Authentication authentication) throws IOException, ServletException {
        // URI et type de requête
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");

        log.debug("URI de la requête : {}", uri);
        log.debug("Header Accept : {}", accept != null ? accept : "null");

        // Authentification et session
            log.debug("Utilisateur authentifié : {}", authentication.getName());
        String credentials = (String) authentication.getCredentials();
        log.debug("Credentials (Session Ticket) : {}", LogMasking.mask(credentials));
        String sessionId = request.getSession(false).getId();
        log.debug("Session ID : {}", LogMasking.mask(sessionId));

        if (credentials == null) {
            throw new LostTicketException("Ticket perdu pour la session: " + LogMasking.mask(sessionId));
        }
        log.debug("Création du mappage entre le ticket [{}] et l'ID de session [{}] dans le cache Redis", LogMasking.mask(credentials), LogMasking.mask(sessionId));
        redisService.setSessionTicketSessionIdPair(credentials, sessionId);
        super.onAuthenticationSuccess(request, response, authentication);
    }



}
