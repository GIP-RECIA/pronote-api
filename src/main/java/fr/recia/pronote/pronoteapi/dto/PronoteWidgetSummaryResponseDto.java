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
package fr.recia.pronote.pronoteapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

public class PronoteWidgetSummaryResponseDto extends ArrayList<PronoteWidgetSummaryResponseDto.EleveSummary> {

    public PronoteWidgetSummaryResponseDto(List<EleveDto> eleveDtoList) {
        for (EleveDto eleveDto : eleveDtoList) {
            Identity id = buildIdentity(eleveDto);
            this.add(new EleveSummary(id, buildItems(eleveDto)));
        }
    }

    private static Identity buildIdentity(EleveDto eleveDto) {
        if (eleveDto.getPrenom() == null && eleveDto.getNom() == null) {
            return null;
        }
        return new Identity(eleveDto.getPrenom(), eleveDto.getNom());
    }

    private static Map<String, Integer> buildItems(EleveDto eleveDto) {
        VieScolaireDto vieScolaireDto = eleveDto.getVieScolaireDto();

        Map<String, Integer> items = new LinkedHashMap<>();
        items.put("devoirs", sizeOrZero(eleveDto.getDevoirDtoList()));
        items.put("visites_infirmerie", vieScolaireDto == null ? 0 : sizeOrZero(vieScolaireDto.getPassageInfirmerieList()));
        items.put("absences_et_retards", vieScolaireDto == null ? 0 :
                sizeOrZero(vieScolaireDto.getAbsenceList()) + sizeOrZero(vieScolaireDto.getRetardList()));
        items.put("punitions_et_sanctions", vieScolaireDto == null ? 0 :
                sizeOrZero(vieScolaireDto.getPunitionList()) + sizeOrZero(vieScolaireDto.getSanctionList()));

        return items;
    }

    private static int sizeOrZero(List<?> list) {
        return Objects.isNull(list) ? 0 : list.size();
    }

    public record Identity(String firstname, String lastname) {}

    public record EleveSummary(@JsonInclude(JsonInclude.Include.NON_NULL) Identity id, Map<String, Integer> items) {}
}
