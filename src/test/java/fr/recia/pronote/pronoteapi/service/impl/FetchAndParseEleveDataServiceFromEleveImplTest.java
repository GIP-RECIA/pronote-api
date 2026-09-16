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

import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.ResumeDeCoursDto;
import fr.recia.pronote.pronoteapi.dto.factory.ResumeCoursEtTravailAFaireAllDtoFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchAndParseEleveDataServiceFromEleveImplTest {

    private static final String XML_COURS_AVEC_TRAVAIL_A_FAIRE = """
            <Eleve>
                <PageCahierDeTextes>
                    <CahierDeTextes>
                        <Matiere>Mathématiques</Matiere>
                        <Date>2026-09-10T00:00:00.000+0000</Date>
                        <TravailAFaire>
                            <Descriptif>Exercices p.42</Descriptif>
                            <PourLe>2026-09-15T00:00:00.000+0000</PourLe>
                        </TravailAFaire>
                    </CahierDeTextes>
                </PageCahierDeTextes>
            </Eleve>
            """;

    private static final String XML_COURS_SANS_TRAVAIL_A_FAIRE = """
            <Eleve>
                <PageCahierDeTextes>
                    <CahierDeTextes>
                        <Matiere>Français</Matiere>
                        <Date>2026-09-10T00:00:00.000+0000</Date>
                    </CahierDeTextes>
                </PageCahierDeTextes>
            </Eleve>
            """;

    private static final String XML_DEUX_MATIERES_UNE_AVEC_TRAVAIL_A_FAIRE = """
            <Eleve>
                <PageCahierDeTextes>
                    <CahierDeTextes>
                        <Matiere>Mathématiques</Matiere>
                        <Date>2026-09-10T00:00:00.000+0000</Date>
                        <TravailAFaire>
                            <Descriptif>Exercices p.42</Descriptif>
                            <PourLe>2026-09-15T00:00:00.000+0000</PourLe>
                        </TravailAFaire>
                    </CahierDeTextes>
                    <CahierDeTextes>
                        <Matiere>Français</Matiere>
                        <Date>2026-09-11T00:00:00.000+0000</Date>
                    </CahierDeTextes>
                </PageCahierDeTextes>
            </Eleve>
            """;

    @Mock
    FetchPronoteServiceImpl fetchPronoteService;

    private final ResumeCoursEtTravailAFaireAllDtoFactory factory = new ResumeCoursEtTravailAFaireAllDtoFactory();

    static Stream<Arguments> xmlScenarios() {
        return Stream.of(
                Arguments.of(XML_COURS_AVEC_TRAVAIL_A_FAIRE, List.of("Mathématiques"), 1),
                Arguments.of(XML_COURS_SANS_TRAVAIL_A_FAIRE, List.of("Français"), 0),
                Arguments.of(XML_DEUX_MATIERES_UNE_AVEC_TRAVAIL_A_FAIRE, List.of("Mathématiques", "Français"), 1)
        );
    }

    @ParameterizedTest(name = "matières={1}, nb travail à faire={2}")
    @MethodSource("xmlScenarios")
    void getDto_parsesXmlAndBuildsEleveDto(String xml, List<String> expectedMatieres, int expectedTravailAFaireCount) {
        when(fetchPronoteService.getPronoteXmlAsString()).thenReturn(xml);

        FetchAndParseEleveDataServiceFromEleveImpl service =
                new FetchAndParseEleveDataServiceFromEleveImpl(fetchPronoteService, factory);

        List<EleveDto> result = service.getDto("some-uid");
        EleveDto eleveDto = result.getFirst();

        List<String> actualMatieres = Objects.requireNonNull(eleveDto.getResumeDeCoursDtoList())
                .stream()
                .map(ResumeDeCoursDto::getMatiere)
                .toList();

        assertThat(actualMatieres).isEqualTo(expectedMatieres);
        assertThat(eleveDto.getTravailAFaireDtoList()).hasSize(expectedTravailAFaireCount);
    }
}