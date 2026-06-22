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
package fr.recia.pronote.pronoteapi.web.rest;

import fr.recia.pronote.pronoteapi.config.bean.AppConfProperties;
import fr.recia.pronote.pronoteapi.util.UserAttributesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/widgets")
public class PronoteWidgetsController {


    @Autowired
    AppConfProperties appConfProperties;

    @Autowired
    UserAttributesHandler userAttributesHandler;

    // todo : dev only
    @GetMapping(value = "/authenticated", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> getWidgets(){
        Map<String, Object> returnMap = new HashMap<>();

        CasAuthenticationToken token = (CasAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication();
        assert token != null;
        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(appConfProperties.getCasProxyTicketFor());

        returnMap.put("token", token);

        returnMap.put("proxyTicket", proxyTicket);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String uaiCourant =   userAttributesHandler.getAttribute(UserAttributesHandler.UAI_CURRENT);
            String uri = String.format(appConfProperties.getCasProxyTicketFor(), uaiCourant);
            ResponseEntity<String> response
                    = restTemplate.postForEntity(uri + "?ticket=" + proxyTicket + "&methode=proxyValidate", String.class, String.class);
            returnMap.put("response", response.getBody());

        }catch (Exception e){
            returnMap.put("exception", e.getMessage());
            return ResponseEntity.internalServerError().body(returnMap);

        }


        return ResponseEntity.ok(returnMap);
    }
}
