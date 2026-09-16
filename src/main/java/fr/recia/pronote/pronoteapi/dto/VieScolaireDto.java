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
package fr.recia.pronote.pronoteapi.dto;

import fr.recia.pronote.pronoteapi.dto.viescolaire.AbsenceDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.ObservationDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.PassageInfirmerieDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.PunitionDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.RetardDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.SanctionDto;
import fr.recia.pronote.pronoteapi.model.PageVieScolaire;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class VieScolaireDto implements IWidgetCountable{

    public VieScolaireDto(PageVieScolaire pageVieScolaire){

        if(Objects.isNull(pageVieScolaire)){
            return;
        }

        if(Objects.nonNull(pageVieScolaire.getAbsenceList())){
            this.absenceList = pageVieScolaire.getAbsenceList().stream().map(AbsenceDto::new).toList();
        }

        if(Objects.nonNull(pageVieScolaire.getRetardList())){
            this.retardList = pageVieScolaire.getRetardList().stream().map(RetardDto::new).toList();
        }

        if(Objects.nonNull(pageVieScolaire.getPassageInfirmerieList())){
            this.passageInfirmerieList = pageVieScolaire.getPassageInfirmerieList().stream().map(PassageInfirmerieDto::new).toList();
        }

        if(Objects.nonNull(pageVieScolaire.getPunitionList())){
            this.punitionList = pageVieScolaire.getPunitionList().stream().map(PunitionDto::new).toList();
        }

        if(Objects.nonNull(pageVieScolaire.getSanctionList())){
            this.sanctionList = pageVieScolaire.getSanctionList().stream().map(SanctionDto::new).toList();
        }

        if(Objects.nonNull(pageVieScolaire.getObservation())){
            this.observationList = pageVieScolaire.getObservation().stream().map(ObservationDto::new).toList();
        }


    }

    protected List<AbsenceDto> absenceList;

    protected List<RetardDto> retardList;

    protected List<PassageInfirmerieDto> passageInfirmerieList;

    protected List<PunitionDto> punitionList;

    protected List<SanctionDto> sanctionList;

    protected List<ObservationDto> observationList;

    @Override
    public Map<String, Integer> widgetCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("visites_infirmerie", passageInfirmerieList != null ? passageInfirmerieList.size() : 0);
        int absences = absenceList != null ? absenceList.size() : 0;
        int retards = retardList != null ? retardList.size() : 0;
        counts.put("absences_et_retards", absences + retards);
        int punitions = punitionList != null ? punitionList.size() : 0;
        int sanctions = sanctionList != null ? sanctionList.size() : 0;
        counts.put("punitions_et_sanctions", punitions + sanctions);
        return counts;
    }
}
