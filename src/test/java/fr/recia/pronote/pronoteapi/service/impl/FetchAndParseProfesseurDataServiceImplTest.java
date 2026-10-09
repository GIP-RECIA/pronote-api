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

import fr.recia.pronote.pronoteapi.dto.ProfesseurDto;
import fr.recia.pronote.pronoteapi.mapper.impl.EtablissementMapperImpl;
import fr.recia.pronote.pronoteapi.mapper.impl.PronoteUrlResolverImpl;
import fr.recia.pronote.pronoteapi.service.IAgendaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchAndParseProfesseurDataServiceImplTest {

    private static final String XML_PROFESSEUR_COMPLET = """
            <Professeur>
                <PageMessagerie>
                    <Discussions>
                        <NombreMessagesNonLus>3</NombreMessagesNonLus>
                    </Discussions>
                    <Informations>
                        <NombreInformationsNonLus>1</NombreInformationsNonLus>
                    </Informations>
                    <DocumentsCasier>
                        <NombreDocumentsCasierNonLus>2</NombreDocumentsCasierNonLus>
                    </DocumentsCasier>
                </PageMessagerie>
                <PagePronote page="1" nom="Collège Jean Moulin"/>
                <ICal>BEGIN:VCALENDAR
            END:VCALENDAR</ICal>
            </Professeur>
            """;

    private static final String XML_PROFESSEUR_SANS_MESSAGERIE = """
            <Professeur>
                <PagePronote page="1" nom="Lycée Voltaire"/>
                <ICal>BEGIN:VCALENDAR
            END:VCALENDAR</ICal>
            </Professeur>
            """;

    @Mock
    FetchPronoteServiceImpl fetchPronoteService;
    @Mock
    IAgendaService agendaService;

    @Test
    void getDto_parsesFullXml_buildsProfesseurDto() {
        when(fetchPronoteService.getPronoteXmlAsString()).thenReturn(XML_PROFESSEUR_COMPLET);
        when(agendaService.getEvenementsFromUrl(any(), any())).thenReturn(List.of());
        FetchAndParseProfesseurDataServiceImpl service = new FetchAndParseProfesseurDataServiceImpl(fetchPronoteService, new EtablissementMapperImpl(), new PronoteUrlResolverImpl(), agendaService);

        ProfesseurDto result = service.getDto("some-uid");

        assertThat(result.getEtablissement()).isEqualTo("Collège Jean Moulin");
        assertThat(result.getICal()).contains("BEGIN:VCALENDAR");
        assertThat(result.getMessagerieDto()).isNotNull();
        assertThat(result.getMessagerieDto().getNombreMessagesNonLus()).isEqualTo(3);
        assertThat(result.getMessagerieDto().getNombreInformationsNonLus()).isEqualTo(1);
        assertThat(result.getMessagerieDto().getNombreDocumentsCasierNonLus()).isEqualTo(2);
    }

    @Test
    void getDto_withoutPageMessagerie_leavesMessagerieDtoNull() {
        when(fetchPronoteService.getPronoteXmlAsString()).thenReturn(XML_PROFESSEUR_SANS_MESSAGERIE);
        when(agendaService.getEvenementsFromUrl(any(), any())).thenReturn(List.of());
        FetchAndParseProfesseurDataServiceImpl service = new FetchAndParseProfesseurDataServiceImpl(fetchPronoteService, new EtablissementMapperImpl(), new PronoteUrlResolverImpl(), agendaService);

        ProfesseurDto result = service.getDto("some-uid");

        assertThat(result.getEtablissement()).isEqualTo("Lycée Voltaire");
        assertThat(result.getMessagerieDto()).isNull();
    }
}
