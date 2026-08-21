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

import fr.recia.pronote.pronoteapi.enums.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class PronoteWidgetSummaryResponseDto {


    private final UserProfile profil;

    private final Map<String, List<SummaryElement>> data;

    public PronoteWidgetSummaryResponseDto(UserProfile profil, List<EleveDto> eleveDtoList) {
        this.profil = profil;
        this.data = new HashMap<>();

        for (EleveDto eleveDto : eleveDtoList) {

            List<SummaryElement> summaryElementList = new ArrayList<>();

            summaryElementList.add(new SummaryElement(
                    SummaryElement.Description.devoirs,
                    Objects.nonNull( eleveDto.getDevoirDtoList()) ?  eleveDto.getDevoirDtoList().size() : 0));

            if(Objects.nonNull(eleveDto.getVieScolaireDto())){
                summaryElementList.add(new SummaryElement(
                        SummaryElement.Description.visites_infirmerie,
                        Objects.nonNull(eleveDto.getVieScolaireDto().getPassageInfirmerieList())  ? eleveDto.getVieScolaireDto().getPassageInfirmerieList().size() : 0));

                int absences = Objects.nonNull(eleveDto.getVieScolaireDto().getAbsenceList())  ? eleveDto.getVieScolaireDto().getAbsenceList().size() : 0;
                int retards = Objects.nonNull(eleveDto.getVieScolaireDto().getRetardList())  ? eleveDto.getVieScolaireDto().getRetardList().size() : 0;

                summaryElementList.add(new SummaryElement(
                        SummaryElement.Description.absences_et_retards,
                        absences + retards));

                int punitions = Objects.nonNull(eleveDto.getVieScolaireDto().getPunitionList())  ? eleveDto.getVieScolaireDto().getPunitionList().size() : 0;

                int sanctions = Objects.nonNull(eleveDto.getVieScolaireDto().getSanctionList())  ? eleveDto.getVieScolaireDto().getSanctionList().size() : 0;

                summaryElementList.add(new SummaryElement(
                        SummaryElement.Description.punitions_et_sanctions,
                        punitions + sanctions));

            }else {
                summaryElementList.add(new SummaryElement(SummaryElement.Description.visites_infirmerie, 0));
                summaryElementList.add(new SummaryElement(SummaryElement.Description.absences_et_retards, 0));
                summaryElementList.add(new SummaryElement(SummaryElement.Description.punitions_et_sanctions, 0));
            }
            data.put(eleveDto.getPrenom(), summaryElementList);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SummaryElement {

        Description description;
        int count;

        public enum Description {
            devoirs("devoirs"),
            visites_infirmerie("visites_infirmerie"),
            messages_non_lu("messages_non_lu"),
            absences_et_retards("absences_et_retards"),
            punitions_et_sanctions("punitions_et_sanctions");

            public final String label;

            Description(String label) {
                this.label = label;
            }
        }
    }
}
