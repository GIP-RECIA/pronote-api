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
package fr.recia.pronote.pronoteapi.dto.factory;

import fr.recia.pronote.pronoteapi.dto.cahierdetextes.ResumeDeCoursDto;
import fr.recia.pronote.pronoteapi.dto.ResumeCoursEtTravailAFaireAllDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.TravailAFaireDto;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.CahierDeTextes;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.TravailAFaire;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ResumeCoursEtTravailAFaireAllDtoFactory {

    public ResumeCoursEtTravailAFaireAllDto create(List<CahierDeTextes> cahierDeTextesList){
        List<ResumeDeCoursDto> resumeCoursDtoList = new ArrayList<>();

        List<TravailAFaireDto> travailAFaireDtoList = new ArrayList<>();

        for(CahierDeTextes cahierDeTextes: cahierDeTextesList){

            ResumeDeCoursDto resumeDeCoursDto = new ResumeDeCoursDto(cahierDeTextes);
            resumeCoursDtoList.add(resumeDeCoursDto);
            String id = resumeDeCoursDto.getId();
            if (Objects.nonNull(cahierDeTextes.getTravailAFaireList())){
                for(TravailAFaire travailAFaire: cahierDeTextes.getTravailAFaireList()){
                    travailAFaireDtoList.add(new TravailAFaireDto(travailAFaire, id, cahierDeTextes.getMatiere()));
                }
            }
        }

        return new ResumeCoursEtTravailAFaireAllDto(resumeCoursDtoList, travailAFaireDtoList);

    }

}
