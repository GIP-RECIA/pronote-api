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

import fr.recia.pronote.pronoteapi.dto.ProfesseurDto;
import fr.recia.pronote.pronoteapi.dto.messagerie.MessagerieDto;
import fr.recia.pronote.pronoteapi.mapper.IEtablissementMapper;
import fr.recia.pronote.pronoteapi.model.Professeur;
import fr.recia.pronote.pronoteapi.service.IFetchAndParseProfesseurDataService;
import fr.recia.pronote.pronoteapi.service.IFetchPronoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.dataformat.xml.XmlMapper;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FetchAndParseProfesseurDataServiceImpl implements IFetchAndParseProfesseurDataService {

    private final IFetchPronoteService fetchPronoteService;
    private final IEtablissementMapper etablissementMapper;

    @Override
    @Cacheable(value = "professeurDtoCache", key = "#uid")
    public ProfesseurDto getDto(String uid) {
        String xml = fetchPronoteService.getPronoteXmlAsString();

        XmlMapper xmlMapper = new XmlMapper();
        Professeur professeur = xmlMapper.readValue(xml, Professeur.class);

        MessagerieDto messagerieDto = Objects.nonNull(professeur.getPageMessagerie())
                ? new MessagerieDto(professeur.getPageMessagerie()) : null;

        String etablissement = etablissementMapper.map(professeur.getPagePronoteList());

        ProfesseurDto professeurDto = ProfesseurDto.builder()
                .messagerieDto(messagerieDto)
                .etablissement(etablissement)
                .iCal(professeur.getICal())
                .build();

        log.trace("DTO for Professeur with uid {} is {}", uid, professeurDto);

        return professeurDto;
    }
}
