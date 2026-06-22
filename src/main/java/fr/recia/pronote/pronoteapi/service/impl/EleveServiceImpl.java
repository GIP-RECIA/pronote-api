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

import fr.recia.pronote.pronoteapi.dto.CahierDeTextesDto;
import fr.recia.pronote.pronoteapi.dto.ResponseEleveDto;
import fr.recia.pronote.pronoteapi.model.Eleve;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.CahierDeTextes;
import fr.recia.pronote.pronoteapi.service.IEleveService;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EleveServiceImpl implements IEleveService {
    @Override
    public ResponseEleveDto getDto(boolean isForWidget) {

        ResponseEleveDto responseEleveDto = new ResponseEleveDto();


        Eleve eleve = new Eleve(); // todo  fetch eleve

        responseEleveDto.setCahierDeTextesDtoList(getCahierDeTextesDtoList(eleve, isForWidget));

        return null;
    }


    private List<CahierDeTextesDto> getCahierDeTextesDtoList(Eleve eleve, boolean isForWidget){

        List<CahierDeTextesDto> cahierDeTextesDtoList = new ArrayList<>();

        if(Objects.isNull( eleve.getPageCahierDeTextes())){
            return new ArrayList<>();
        }

        List<CahierDeTextes> listToConvert = eleve.getPageCahierDeTextes().getCahierDeTextesList();

        if(isForWidget){
            listToConvert = cahierDeTextesListFilterdForWidget(listToConvert);
        }

        //todo transformer listToConvert


        return cahierDeTextesDtoList;


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
