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
import fr.recia.pronote.pronoteapi.config.custom.impl.UserCustomImplementation;
import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromEleveImpl;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromParentImpl;
import fr.recia.pronote.pronoteapi.util.UserAttributesHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PronoteController.class)
@Import(UserAttributesHandler.class)
class PronoteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    FetchAndParseEleveDataServiceFromEleveImpl eleveService;

    @MockitoBean
    FetchAndParseEleveDataServiceFromParentImpl parentService;

    @MockitoBean
    ProfilsProperties profilsProperties;

    private void authenticateAs(String profil, String uid) {
        Map<String, Object> casAttributes = Map.of(
                UserAttributesHandler.ENT_PERSON_PROFILS, profil,
                UserAttributesHandler.UID, uid
        );
        UserCustomImplementation principal = new UserCustomImplementation(
                uid, "", List.of(new SimpleGrantedAuthority("ROLE_USER")), casAttributes);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getPronotePage_forEleveProfil_dispatchesToEleveServiceAndReturnsJson() throws Exception {
        authenticateAs("National_ELV", "jdupont");
        when(profilsProperties.getEleveProfilName()).thenReturn("National_ELV");
        when(profilsProperties.getParentProfilName()).thenReturn("National_TUT");
        when(eleveService.getDto("jdupont")).thenReturn(
                List.of(EleveDto.builder().build()));

        mockMvc.perform(get("/api/widgets/pronotePage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profil").value("Eleve"))
                .andExpect(jsonPath("$.eleveDtoList", hasSize(1)));

        verifyNoInteractions(parentService);
    }

    @Test
    void getPronotePage_forParentProfil_dispatchesToParentServiceAndReturnsJson() throws Exception {
        authenticateAs("National_TUT", "pmartin");
        when(profilsProperties.getEleveProfilName()).thenReturn("National_ELV");
        when(profilsProperties.getParentProfilName()).thenReturn("National_TUT");
        when(parentService.getDto("pmartin")).thenReturn(List.of(
                EleveDto.builder().prenom("Alice$abc").build(),
                EleveDto.builder().prenom("Bob$def").build()
        ));

        mockMvc.perform(get("/api/widgets/pronotePage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profil").value("Parent"))
                .andExpect(jsonPath("$.eleveDtoList", hasSize(2)))
                .andExpect(jsonPath("$.eleveDtoList[0].prenom").value("Alice$abc"))
                .andExpect(jsonPath("$.eleveDtoList[1].prenom").value("Bob$def"));

        verifyNoInteractions(eleveService);
    }


    @Test
    void getPronotePage_forUnexpectedProfil_returns403WithMessage() throws Exception {
        authenticateAs("National_INCONNU", "xuser");
        when(profilsProperties.getEleveProfilName()).thenReturn("National_ELV");
        when(profilsProperties.getParentProfilName()).thenReturn("National_TUT");

        mockMvc.perform(get("/api/widgets/pronotePage"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("National_INCONNU"));

        verifyNoInteractions(eleveService);
        verifyNoInteractions(parentService);
    }

}