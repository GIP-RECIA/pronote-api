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
package fr.recia.pronote.pronoteapi.mapper.impl;

import fr.recia.pronote.pronoteapi.dto.DevoirDto;
import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.ResumeCoursEtTravailAFaireAllDto;
import fr.recia.pronote.pronoteapi.dto.VieScolaireDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.ResumeDeCoursDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.TravailAFaireDto;
import fr.recia.pronote.pronoteapi.dto.competences.CompetencesDto;
import fr.recia.pronote.pronoteapi.dto.factory.ResumeCoursEtTravailAFaireAllDtoFactory;
import fr.recia.pronote.pronoteapi.mapper.IEleveDtoMapper;
import fr.recia.pronote.pronoteapi.mapper.IEtablissementMapper;
import fr.recia.pronote.pronoteapi.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class EleveDtoMapperImpl implements IEleveDtoMapper {

    private final ResumeCoursEtTravailAFaireAllDtoFactory resumeCoursEtTravailAFaireAllDtoFactory;
    private final IEtablissementMapper etablissementMapper;

    @Override
    public EleveDto.EleveDtoBuilder map(Eleve eleve, String pronoteBaseUrl) {
        return map(eleve.getPageVieScolaire(), eleve.getPageCahierDeTextes(), eleve.getPageReleveDeNotes(),
                eleve.getPageCompetences(), eleve.getPagePronoteList(), eleve.getICal(), pronoteBaseUrl);
    }

    @Override
    public EleveDto.EleveDtoBuilder map(EleveFromParent eleve, String pronoteBaseUrl) {
        return map(eleve.getPageVieScolaire(), eleve.getPageCahierDeTextes(), eleve.getPageReleveDeNotes(),
                eleve.getPageCompetences(), eleve.getPagePronoteList(), eleve.getICal(), pronoteBaseUrl);
    }

    private EleveDto.EleveDtoBuilder map(
            PageVieScolaire pageVieScolaire,
            PageCahierDeTextes pageCahierDeTextes,
            PageReleveDeNotes pageReleveDeNotes,
            PageCompetences pageCompetences,
            List<PagePronote> pagePronoteList,
            String iCal,
            String pronoteBaseUrl
    ) {
        VieScolaireDto vieScolaireDto = Objects.nonNull(pageVieScolaire) ? new VieScolaireDto(pageVieScolaire) : null;

        List<ResumeDeCoursDto> resumeDeCoursDtoList = null;
        List<TravailAFaireDto> travailAFaireDtoList = null;

        if (Objects.nonNull(pageCahierDeTextes) && Objects.nonNull(pageCahierDeTextes.getCahierDeTextesList())) {
            ResumeCoursEtTravailAFaireAllDto resumeCoursEtTravailAFaireAllDto =
                    resumeCoursEtTravailAFaireAllDtoFactory.create(pageCahierDeTextes.getCahierDeTextesList(), pronoteBaseUrl);
            resumeDeCoursDtoList = resumeCoursEtTravailAFaireAllDto.getResumeCoursDtoList();
            travailAFaireDtoList = resumeCoursEtTravailAFaireAllDto.getTravailAFaireDtoList();
        }

        List<DevoirDto> devoirDtoList = Objects.nonNull(pageReleveDeNotes) && Objects.nonNull(pageReleveDeNotes.getDevoirList())
                ? pageReleveDeNotes.getDevoirList().stream().map(DevoirDto::new).toList()
                : null;

        CompetencesDto competencesDto = Objects.nonNull(pageCompetences) ? new CompetencesDto(pageCompetences) : null;

        return EleveDto.builder()
                .vieScolaireDto(vieScolaireDto)
                .resumeDeCoursDtoList(resumeDeCoursDtoList)
                .travailAFaireDtoList(travailAFaireDtoList)
                .devoirDtoList(devoirDtoList)
                .competencesDto(competencesDto)
                .etablissement(etablissementMapper.map(pagePronoteList))
                .iCal(iCal);
    }
}
