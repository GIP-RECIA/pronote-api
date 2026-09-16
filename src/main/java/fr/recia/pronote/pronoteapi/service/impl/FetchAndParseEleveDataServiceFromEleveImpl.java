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

import fr.recia.pronote.pronoteapi.dto.DevoirDto;
import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.ResumeCoursEtTravailAFaireAllDto;
import fr.recia.pronote.pronoteapi.dto.VieScolaireDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.ResumeDeCoursDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.TravailAFaireDto;
import fr.recia.pronote.pronoteapi.dto.factory.ResumeCoursEtTravailAFaireAllDtoFactory;
import fr.recia.pronote.pronoteapi.dto.messagerie.MessagerieDto;
import fr.recia.pronote.pronoteapi.model.Eleve;
import fr.recia.pronote.pronoteapi.service.IFetchAndParseEleveDataService;
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

    private final FetchPronoteServiceImpl fetchPronoteService;
    private final ResumeCoursEtTravailAFaireAllDtoFactory resumeCoursEtTravailAFaireAllDtoFactory;

    @Override
    @Cacheable(value = "dtoListCache", key = "#uid")
    public List<EleveDto> getDto(String uid) {

        String xml = fetchPronoteService.getPronoteXmlAsString();

        XmlMapper xmlMapper = new XmlMapper();

        Eleve eleve = xmlMapper.readValue(xml, Eleve.class);

        VieScolaireDto vieScolaireDto = Objects.nonNull(eleve.getPageVieScolaire()) ? new VieScolaireDto(eleve.getPageVieScolaire()) : null;

        List<ResumeDeCoursDto> resumeDeCoursDtoList = null;
        List<TravailAFaireDto> travailAFaireDtoList = null;

        if (Objects.nonNull(eleve.getPageCahierDeTextes()) && Objects.nonNull(eleve.getPageCahierDeTextes().getCahierDeTextesList())) {
            ResumeCoursEtTravailAFaireAllDto resumeCoursEtTravailAFaireAllDto = resumeCoursEtTravailAFaireAllDtoFactory.create(eleve.getPageCahierDeTextes().getCahierDeTextesList());
            resumeDeCoursDtoList = resumeCoursEtTravailAFaireAllDto.getResumeCoursDtoList();
            travailAFaireDtoList = resumeCoursEtTravailAFaireAllDto.getTravailAFaireDtoList();
        }

        List<DevoirDto> devoirDtoList =
                Objects.nonNull(eleve.getPageReleveDeNotes()) && Objects.nonNull(eleve.getPageReleveDeNotes().getDevoirList()) ? eleve.getPageReleveDeNotes().getDevoirList().stream().map(DevoirDto::new).toList() : null;

        MessagerieDto messagerieDto = Objects.nonNull(eleve.getPageMessagerie()) ? new MessagerieDto(eleve.getPageMessagerie()) : null;

        EleveDto eleveDto = EleveDto.builder()
                .resumeDeCoursDtoList(resumeDeCoursDtoList)
                .travailAFaireDtoList(travailAFaireDtoList)
                .vieScolaireDto(vieScolaireDto)
                .devoirDtoList(devoirDtoList)
                .messagerieDto(messagerieDto)
                .build();


        log.trace("DTO for Eleve with uid {} is {}", uid, eleveDto);

        return Collections.singletonList(eleveDto);
    }
}
