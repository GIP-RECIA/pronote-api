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

import fr.recia.pronote.pronoteapi.dto.viescolaire.AbsenceDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.PassageInfirmerieDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.PunitionDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.RetardDto;
import fr.recia.pronote.pronoteapi.dto.viescolaire.SanctionDto;
import fr.recia.pronote.pronoteapi.enums.UserProfile;
import fr.recia.pronote.pronoteapi.model.viescolaire.Absence;
import fr.recia.pronote.pronoteapi.model.viescolaire.PassageInfirmerie;
import fr.recia.pronote.pronoteapi.model.viescolaire.Punition;
import fr.recia.pronote.pronoteapi.model.viescolaire.Retard;
import fr.recia.pronote.pronoteapi.model.viescolaire.Sanction;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PronoteWidgetSummaryResponseDtoTest {

    private int countFor(List<PronoteWidgetSummaryResponseDto.SummaryElement> elements,
                         PronoteWidgetSummaryResponseDto.SummaryElement.Description description) {
        return elements.stream()
                .filter(e -> e.getDescription() == description)
                .findFirst()
                .orElseThrow()
                .getCount();
    }

    @Test
    void constructor_withFullVieScolaire_sumsAbsencesRetardsAndPunitionsSanctions() {
        VieScolaireDto vieScolaireDto = createVieScolaireDto();

        EleveDto eleveDto = new EleveDto("Alice", null, null, vieScolaireDto,
                List.of(new DevoirDto(), new DevoirDto(), new DevoirDto()));

        PronoteWidgetSummaryResponseDto summary =
                new PronoteWidgetSummaryResponseDto(UserProfile.eleve, List.of(eleveDto));

        List<PronoteWidgetSummaryResponseDto.SummaryElement> elements = summary.getData().get("Alice");

        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.devoirs)).isEqualTo(3);
        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.visites_infirmerie)).isEqualTo(1);
        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.absences_et_retards)).isEqualTo(3);
        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.punitions_et_sanctions)).isEqualTo(3);
    }

    private static @NonNull VieScolaireDto createVieScolaireDto() {
        VieScolaireDto vieScolaireDto = new VieScolaireDto(null);
        vieScolaireDto.setAbsenceList(List.of(new AbsenceDto(new Absence()), new AbsenceDto(new Absence())));
        vieScolaireDto.setRetardList(List.of(new RetardDto(new Retard())));
        vieScolaireDto.setPassageInfirmerieList(List.of(new PassageInfirmerieDto(new PassageInfirmerie())));
        vieScolaireDto.setPunitionList(List.of(new PunitionDto(new Punition())));
        vieScolaireDto.setSanctionList(List.of(new SanctionDto(new Sanction()), new SanctionDto(new Sanction())));
        return vieScolaireDto;
    }

    @Test
    void constructor_withoutVieScolaire_defaultsToZeroForVieScolaireCounts() {
        EleveDto eleveDto = new EleveDto("Bob", null, null, null, null);

        PronoteWidgetSummaryResponseDto summary =
                new PronoteWidgetSummaryResponseDto(UserProfile.eleve, List.of(eleveDto));

        List<PronoteWidgetSummaryResponseDto.SummaryElement> elements = summary.getData().get("Bob");

        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.devoirs)).isZero();
        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.visites_infirmerie)).isZero();
        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.absences_et_retards)).isZero();
        assertThat(countFor(elements, PronoteWidgetSummaryResponseDto.SummaryElement.Description.punitions_et_sanctions)).isZero();
    }

    @Test
    void constructor_withMultipleEleves_keysDataByPrenom() {
        EleveDto alice = new EleveDto("Alice", null, null, null, null);
        EleveDto bob = new EleveDto("Bob", null, null, null, null);

        PronoteWidgetSummaryResponseDto summary =
                new PronoteWidgetSummaryResponseDto(UserProfile.parent, List.of(alice, bob));

        Map<String, List<PronoteWidgetSummaryResponseDto.SummaryElement>> data = summary.getData();
        assertThat(data).containsOnlyKeys("Alice", "Bob");
    }
}