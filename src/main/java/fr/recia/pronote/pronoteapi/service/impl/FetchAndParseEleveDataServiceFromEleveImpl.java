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

import fr.recia.pronote.pronoteapi.dto.DevoirDto;
import fr.recia.pronote.pronoteapi.dto.ResumeCoursEtTravailAFaireAllDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.ResumeDeCoursDto;
import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.TravailAFaireDto;
import fr.recia.pronote.pronoteapi.dto.VieScolaireDto;
import fr.recia.pronote.pronoteapi.dto.factory.ResumeCoursEtTravailAFaireAllDtoFactory;
import fr.recia.pronote.pronoteapi.model.Eleve;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.CahierDeTextes;
import fr.recia.pronote.pronoteapi.service.IFetchAndParseEleveDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.dataformat.xml.XmlMapper;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FetchAndParseEleveDataServiceFromEleveImpl implements IFetchAndParseEleveDataService {


    @Autowired
    FetchPronoteServiceImpl fetchPronoteService;

    @Autowired
    ResumeCoursEtTravailAFaireAllDtoFactory resumeCoursEtTravailAFaireAllDtoFactory;

    @Override
    @Cacheable(value = "dtoListCache", key = "#uid")
    public List<EleveDto> getDto(String uid) {

        String xml = fetchPronoteService.getPronoteXmlAsString();

        XmlMapper xmlMapper = new XmlMapper();

        Eleve eleve = xmlMapper.readValue(xml, Eleve.class);


        VieScolaireDto vieScolaireDto = Objects.nonNull(eleve.getPageVieScolaire()) ? new VieScolaireDto(eleve.getPageVieScolaire()) : null;

        List<ResumeDeCoursDto> resumeDeCoursDtoList = null;
        List<TravailAFaireDto> travailAFaireDtoList = null;

        if(Objects.nonNull(eleve.getPageCahierDeTextes()) && Objects.nonNull(eleve.getPageCahierDeTextes().getCahierDeTextesList())){
            ResumeCoursEtTravailAFaireAllDto  resumeCoursEtTravailAFaireAllDto = resumeCoursEtTravailAFaireAllDtoFactory.create(eleve.getPageCahierDeTextes().getCahierDeTextesList());
            resumeDeCoursDtoList = resumeCoursEtTravailAFaireAllDto.getResumeCoursDtoList();
            travailAFaireDtoList = resumeCoursEtTravailAFaireAllDto.getTravailAFaireDtoList();
        }

        List<DevoirDto> devoirDtoList =
                Objects.nonNull(eleve.getPageReleveDeNotes()) && Objects.nonNull(eleve.getPageReleveDeNotes().getDevoirList()) ? eleve.getPageReleveDeNotes().getDevoirList().stream().map(DevoirDto::new).toList() : null;

        EleveDto eleveDto = new EleveDto(
                EleveDto.DEFAULT_PRENOM,
                resumeDeCoursDtoList,
                travailAFaireDtoList,
                vieScolaireDto,
                devoirDtoList);


        log.trace("DTO for Eleve with uid {} is {}", uid, eleveDto);

        return Collections.singletonList(eleveDto);
    }




    private List<CahierDeTextes> cahierDeTextesListFilterdForWidget(List<CahierDeTextes> listToConvert){
        List<CahierDeTextes> filtered = new ArrayList<>();
        Instant max = Instant.now().plus(3, ChronoUnit.DAYS);
        Instant min = Instant.now().minus(1, ChronoUnit.DAYS);

        for(CahierDeTextes cahierDeTextes : listToConvert){

            Instant toUse = max;
            int value = cahierDeTextes.getDate().toInstant().get(ChronoField.DAY_OF_WEEK);

            if(value == 0 || value == 6){
                toUse = toUse.plus(2, ChronoUnit.DAYS);
            }

            if(cahierDeTextes.getDate().toInstant().isBefore(toUse) && cahierDeTextes.getDate().toInstant().isAfter(min)){
                filtered.add(cahierDeTextes);
            }
        }
        return filtered;
    }

}
