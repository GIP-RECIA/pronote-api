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
package fr.recia.pronote.pronoteapi.web.rest;

import fr.recia.pronote.pronoteapi.config.bean.ProfilsProperties;
import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.PageResponseDto;
import fr.recia.pronote.pronoteapi.dto.SummaryResponseDto;
import fr.recia.pronote.pronoteapi.enums.UserProfile;
import fr.recia.pronote.pronoteapi.exception.UnexpectedProfilException;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromEleveImpl;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromParentImpl;
import fr.recia.pronote.pronoteapi.util.UserAttributesHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PronoteController {


    private final UserAttributesHandler userAttributesHandler;

    private final FetchAndParseEleveDataServiceFromEleveImpl eleveService;

    private final FetchAndParseEleveDataServiceFromParentImpl parentService;

    private final ProfilsProperties profilsProperties;

    private record ResolvedProfile(UserProfile userProfile, List<EleveDto> eleveDtoList) {}

    private ResolvedProfile resolveProfile() {
        String profil = userAttributesHandler.getAttribute(UserAttributesHandler.ENT_PERSON_PROFILS);
        String uid = userAttributesHandler.getAttribute(UserAttributesHandler.UID);

        if (profilsProperties.getEleveProfilName().equals(profil)) {
            return new ResolvedProfile(UserProfile.eleve, eleveService.getDto(uid));
        } else if (profilsProperties.getParentProfilName().equals(profil)) {
            return new ResolvedProfile(UserProfile.parent, parentService.getDto(uid));
        } else {
            throw new UnexpectedProfilException(profil);
        }
    }

    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageResponseDto> getPronotePage(){
        ResolvedProfile resolved = resolveProfile();
        return ResponseEntity.ok(new PageResponseDto(resolved.userProfile(), resolved.eleveDtoList()));
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SummaryResponseDto> getPronoteWidgetSummary(){
        ResolvedProfile resolved = resolveProfile();
        return ResponseEntity.ok(new SummaryResponseDto(resolved.eleveDtoList()));
    }
}
