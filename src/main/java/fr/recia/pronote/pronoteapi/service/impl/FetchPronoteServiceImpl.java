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
package fr.recia.pronote.pronoteapi.service.impl;

import fr.recia.pronote.pronoteapi.exception.LostTicketException;
import fr.recia.pronote.pronoteapi.exception.PronoteXmlFetchException;
import fr.recia.pronote.pronoteapi.service.IFetchPronoteService;
import fr.recia.pronote.pronoteapi.service.IPronoteMappingService;
import fr.recia.pronote.pronoteapi.util.UserAttributesHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Profile("!mock-no-cas")
@RequiredArgsConstructor
public class FetchPronoteServiceImpl implements IFetchPronoteService {

    private static final String DONNEES_UTILISATEUR_PATH = "donneesUtilisateur";

    private final IPronoteMappingService pronoteMappingService;
    private final UserAttributesHandler userAttributesHandler;

    @Override
    public String getPronoteBaseUrl() {
        String uaiCourant = userAttributesHandler.getAttribute(UserAttributesHandler.UAI_CURRENT);
        return pronoteMappingService.getBaseUrl(uaiCourant);
    }

    @Override
    public String getPronoteXmlAsString() {
        CasAuthenticationToken token = (CasAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        String uaiCourant = userAttributesHandler.getAttribute(UserAttributesHandler.UAI_CURRENT);

        if (token == null) {
            throw new IllegalStateException("No CAS authentication found in security context");
        }
        String donneesUtilisateurUrl = pronoteMappingService.getBaseUrl(uaiCourant) + DONNEES_UTILISATEUR_PATH;
        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(donneesUtilisateurUrl);
        if (proxyTicket == null) {
            throw new LostTicketException(String.format(
                    "Proxy ticket introuvable pour uai %s et user id %s",
                    uaiCourant, userAttributesHandler.getAttribute(UserAttributesHandler.UID)
            ));
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String uri = donneesUtilisateurUrl + "?ticket=" + proxyTicket + "&methode=proxyValidate";
            log.trace("Fetching Pronote XML at uri {}", uri);
            ResponseEntity<String> response
                    = restTemplate.postForEntity(uri, String.class, String.class);

            String body = response.getBody();
            if (body == null) {
                throw new PronoteXmlFetchException("Pronote returned an empty response body");
            }
            return body;

        } catch (PronoteXmlFetchException e) {
            throw e;
        } catch (Exception e) {
            throw new PronoteXmlFetchException("Failed to fetch Pronote XML: " + e.getMessage(), e);
        }
    }
}
