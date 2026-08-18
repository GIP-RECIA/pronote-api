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

import fr.recia.pronote.pronoteapi.config.bean.CasProperties;
import fr.recia.pronote.pronoteapi.config.bean.ProfilsProperties;
import fr.recia.pronote.pronoteapi.dto.PronotePageResponseDto;
import fr.recia.pronote.pronoteapi.dto.PronoteWidgetSummaryResponseDto;
import fr.recia.pronote.pronoteapi.enums.UserProfile;
import fr.recia.pronote.pronoteapi.exception.UnexpectedProfilException;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromEleveImpl;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromParentImpl;
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
public class PronoteController {

    @Autowired
    UserAttributesHandler userAttributesHandler;

    @Autowired
    FetchAndParseEleveDataServiceFromEleveImpl eleveService;

    @Autowired
    FetchAndParseEleveDataServiceFromParentImpl parentService;

    @Autowired
    ProfilsProperties profilsProperties;

    @GetMapping(value = "/pronotePage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PronotePageResponseDto> getPronotePage(){
        String profil = userAttributesHandler.getAttribute(UserAttributesHandler.ENT_PERSON_PROFILS);
         if(profilsProperties.getEleveProfilName().equals(profil)){
             return  ResponseEntity.ok(new PronotePageResponseDto(UserProfile.eleve, eleveService.getDto(userAttributesHandler.getAttribute(UserAttributesHandler.UID))));
         }else if(profilsProperties.getParentProfilName().equals(profil)){
             return  ResponseEntity.ok(new PronotePageResponseDto(UserProfile.parent, parentService.getDto(userAttributesHandler.getAttribute(UserAttributesHandler.UID))));
         }else{
            throw new UnexpectedProfilException(profil);
        }
    }

    @GetMapping(value = "/pronoteWidgetSummary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PronoteWidgetSummaryResponseDto> getPronoteWidgetSummary(){
        String profil = userAttributesHandler.getAttribute(UserAttributesHandler.ENT_PERSON_PROFILS);
        if(profilsProperties.getEleveProfilName().equals(profil)){
            return  ResponseEntity.ok(new PronoteWidgetSummaryResponseDto(UserProfile.eleve, eleveService.getDto(userAttributesHandler.getAttribute(UserAttributesHandler.UID))));
        }else if(profilsProperties.getParentProfilName().equals(profil)){
            return  ResponseEntity.ok(new PronoteWidgetSummaryResponseDto(UserProfile.parent, parentService.getDto(userAttributesHandler.getAttribute(UserAttributesHandler.UID))));
        }else{
            throw new UnexpectedProfilException(profil);
        }
    }
}
