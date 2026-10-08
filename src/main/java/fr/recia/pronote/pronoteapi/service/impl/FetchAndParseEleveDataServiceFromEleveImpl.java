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

import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.messagerie.MessagerieDto;
import fr.recia.pronote.pronoteapi.mapper.IEleveDtoMapper;
import fr.recia.pronote.pronoteapi.model.Eleve;
import fr.recia.pronote.pronoteapi.service.IAgendaService;
import fr.recia.pronote.pronoteapi.service.IFetchAndParseEleveDataService;
import fr.recia.pronote.pronoteapi.service.IFetchPronoteService;
import fr.recia.pronote.pronoteapi.util.LogMasking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.dataformat.xml.XmlMapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FetchAndParseEleveDataServiceFromEleveImpl implements IFetchAndParseEleveDataService {

    private final IFetchPronoteService fetchPronoteService;
    private final IEleveDtoMapper eleveDtoMapper;
    private final IAgendaService agendaService;

    @Override
    @Cacheable(value = "dtoListCache", key = "#uid")
    public List<EleveDto> getDto(String uid) {

        String xml = fetchPronoteService.getPronoteXmlAsString();
        String pronoteBaseUrl = fetchPronoteService.getPronoteBaseUrl();

        XmlMapper xmlMapper = new XmlMapper();

        Eleve eleve = xmlMapper.readValue(xml, Eleve.class);

        MessagerieDto messagerieDto = Objects.nonNull(eleve.getPageMessagerie()) ? new MessagerieDto(eleve.getPageMessagerie()) : null;

        EleveDto eleveDto = eleveDtoMapper.map(eleve, pronoteBaseUrl)
                .messagerieDto(messagerieDto)
                .build();
        eleveDto.setEvenementsAgenda(agendaService.getEvenementsFromUrl(eleveDto.getICal()));
        log.trace("DTO for Eleve with uid {} is {}", LogMasking.mask(uid), eleveDto);

        return Collections.singletonList(eleveDto);
    }
}

