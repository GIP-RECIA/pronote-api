/*
 * Copyright © ${project.inceptionYear} GIP-RECIA (https://www.recia.fr/)
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

import fr.recia.pronote.pronoteapi.config.bean.AppConfProperties;
import fr.recia.pronote.pronoteapi.config.bean.CasProperties;
import fr.recia.pronote.pronoteapi.exception.PronoteXmlFetchException;
import fr.recia.pronote.pronoteapi.service.IFetchPronoteService;
import fr.recia.pronote.pronoteapi.util.UserAttributesHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FetchPronoteServiceImpl implements IFetchPronoteService {


    @Autowired
    AppConfProperties appConfProperties;

    @Autowired
    CasProperties casProperties;

    @Autowired
    UserAttributesHandler userAttributesHandler;


    // l'établissement fictif n'as pas l'uai dans son nom de sous domaine, mais un nom, d'où ce filtre
    // configurable pour d'autres potentiels cas similaires
    // la map de remplacement diffère entre la requete et la demande de ticket de service car une erreur de
    // configuration fait que le nom attendu pour le proxy ticket n'est pas le meme que l'adresse à laquelle
    // le pronote est exposé

    public String transformedUaiForRequest(String uai){
        if(appConfProperties.getUaiReplacementMapRequest().containsKey(uai)){
            return appConfProperties.getUaiReplacementMapRequest().get(uai);
        }
        return uai;
    }

    public String transformedUaiForProxyTicketFor(String uai){
        if(appConfProperties.getUaiReplacementMapProxyTicketFor().containsKey(uai)){
            return appConfProperties.getUaiReplacementMapProxyTicketFor().get(uai);
        }
        return uai;
    }

    @Override
    public String getPronoteXmlAsString() {
        CasAuthenticationToken token = (CasAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        String uaiCourant =   userAttributesHandler.getAttribute(UserAttributesHandler.UAI_CURRENT);

        assert token != null;
        String uaiCourantTransformedForProxyTicketFor = transformedUaiForProxyTicketFor(uaiCourant);
        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(String.format( casProperties.getCasProxyTicketFor(), uaiCourantTransformedForProxyTicketFor));
        try {
            String uaiCourantTransformedForRequest = transformedUaiForRequest(uaiCourant);
            RestTemplate restTemplate = new RestTemplate();
            String uri = String.format(casProperties.getCasProxyTicketFor(), uaiCourantTransformedForRequest) + "?ticket=" + proxyTicket + "&methode=proxyValidate" ;
            log.info("using uri {}", uri);
            log.info("proxy ticket for {}", casProperties.getCasProxyTicketFor());
            ResponseEntity<String> response
                    = restTemplate.postForEntity(uri, String.class, String.class);
            assert response.getBody() != null;
            return response.getBody();

        }catch (Exception e){
            throw new PronoteXmlFetchException(e.getMessage());
        }
    }
}
