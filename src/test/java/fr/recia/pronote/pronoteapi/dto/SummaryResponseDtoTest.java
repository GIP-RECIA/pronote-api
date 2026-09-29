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

import fr.recia.pronote.pronoteapi.dto.viescolaire.*;
import fr.recia.pronote.pronoteapi.model.viescolaire.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryResponseDtoTest {

    @Test
    void constructor_withFullVieScolaire_sumsAbsencesRetardsAndPunitionsSanctions() {
        VieScolaireDto vieScolaireDto = createVieScolaireDto();

        EleveDto eleveDto = EleveDto.builder()
                .prenom("Alice")
                .nom("Martin")
                .vieScolaireDto(vieScolaireDto)
                .devoirDtoList(List.of(new DevoirDto(), new DevoirDto(), new DevoirDto()))
                .build();

        SummaryResponseDto summary =
                new SummaryResponseDto(List.of(eleveDto));

        Map<String, Integer> items = summary.getFirst().items();

        assertThat(items)
                .containsEntry(WidgetItemKeys.DEVOIRS, 3)
                .containsEntry(WidgetItemKeys.VISITES_INFIRMERIE, 1)
                .containsEntry(WidgetItemKeys.ABSENCES_ET_RETARDS, 3)
                .containsEntry(WidgetItemKeys.PUNITIONS_ET_SANCTIONS, 3);
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
        EleveDto eleveDto = EleveDto.builder().prenom("Bob").nom("Dupont").build();

        SummaryResponseDto summary =
                new SummaryResponseDto(List.of(eleveDto));

        Map<String, Integer> items = summary.getFirst().items();

        assertThat(items)
                .containsEntry(WidgetItemKeys.DEVOIRS, 0)
                .containsEntry(WidgetItemKeys.VISITES_INFIRMERIE, 0)
                .containsEntry(WidgetItemKeys.ABSENCES_ET_RETARDS, 0)
                .containsEntry(WidgetItemKeys.PUNITIONS_ET_SANCTIONS, 0);
    }

    @Test
    void constructor_withMultipleEleves_keysDataByPrenom() {
        EleveDto alice = EleveDto.builder().prenom("Alice").nom("Martin").build();
        EleveDto bob = EleveDto.builder().prenom("Bob").nom("Dupont").build();
        SummaryResponseDto summary =
                new SummaryResponseDto(List.of(alice, bob));

        assertThat(summary)
                .extracting(SummaryResponseDto.EleveSummary::displayName)
                .containsExactly("Alice Martin", "Bob Dupont");
    }
}