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


import fr.recia.pronote.pronoteapi.dto.agenda.EvenementAgendaDto;
import fr.recia.pronote.pronoteapi.enums.CategorieEvenement;
import fr.recia.pronote.pronoteapi.enums.UserProfile;
import fr.recia.pronote.pronoteapi.exception.IcsParsingException;
import fr.recia.pronote.pronoteapi.ical.IcsCalendarParser;
import fr.recia.pronote.pronoteapi.ical.IcsEvent;
import fr.recia.pronote.pronoteapi.service.IFetchIcsService;
import net.fortuna.ical4j.data.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaServiceImplTest {
    @Mock
    IcsCalendarParser icsCalendarParser;
    @Mock
    IFetchIcsService fetchIcsService;

    private static Stream<Arguments> coursSummaries() {
        return Stream.of(
                Arguments.of("MATHEMATIQUES - CUNAFO D.", "MATHEMATIQUES"),
                Arguments.of("MATHEMATIQUES - CUNAFO D. - [Groupe 1] - <T1> test partie 1", "MATHEMATIQUES")
        );
    }

    @ParameterizedTest(name = "summary={0}")
    @MethodSource("coursSummaries")
    void getEvenements_coursEvent_mapsMatiereFromSummary(String summary, String expectedMatiere) throws Exception {
        IcsEvent icsEvent = new IcsEvent(
                "uid1",
                OffsetDateTime.parse("2026-10-07T07:00:00Z"),
                OffsetDateTime.parse("2026-10-07T08:00:00Z"),
                summary,
                "une description",
                "L12",
                List.of("Cours")
        );
        when(icsCalendarParser.parse(any())).thenReturn(List.of(icsEvent));

        AgendaServiceImpl service = new AgendaServiceImpl(icsCalendarParser, fetchIcsService);
        List<EvenementAgendaDto> result = service.getEvenements(InputStream.nullInputStream(), UserProfile.ELEVE);

        EvenementAgendaDto dto = result.getFirst();
        assertThat(dto.getCategorie()).isEqualTo(CategorieEvenement.COURS);
        assertThat(dto.getMatiere()).isEqualTo(expectedMatiere);
        assertThat(dto.getSalle()).isEqualTo("L12");
    }

    @Test
    void getEvenements_jourFerieEvent_setsMatiereNull() throws ParserException, IOException {
        IcsEvent icsEvent = new IcsEvent(
                "uid2",
                OffsetDateTime.parse("2026-10-07T07:00:00Z"),
                OffsetDateTime.parse("2026-10-07T08:00:00Z"),
                "Vacances",
                null,
                null,
                List.of("Jours fériés")
        );
        when(icsCalendarParser.parse(any())).thenReturn(List.of(icsEvent));

        AgendaServiceImpl service = new AgendaServiceImpl(icsCalendarParser, fetchIcsService);
        List<EvenementAgendaDto> result = service.getEvenements(InputStream.nullInputStream(),UserProfile.ELEVE);

        EvenementAgendaDto dto = result.getFirst();
        assertThat(dto.getCategorie()).isEqualTo(CategorieEvenement.JOUR_FERIE);
        assertThat(dto.getMatiere()).isNull();
        assertThat(dto.getSalle()).isNull();
    }

    @Test
    void getEvenements_whenParserThrowsParserException_wrapsInIcsParsingException() throws ParserException, IOException {
        when(icsCalendarParser.parse(any())).thenThrow(new ParserException("flux ICS invalide", 0));

        AgendaServiceImpl service = new AgendaServiceImpl(icsCalendarParser, fetchIcsService);

        try (InputStream icsStream = InputStream.nullInputStream()) {
            assertThatThrownBy(() -> service.getEvenements(icsStream, UserProfile.ELEVE))
                    .isInstanceOf(IcsParsingException.class)
                    .hasCauseInstanceOf(ParserException.class);
        }
    }

    @Test
    void getEvenements_whenParserThrowsIOException_wrapsInIcsParsingException() throws ParserException, IOException {
        when(icsCalendarParser.parse(any())).thenThrow(new IOException("flux illisible"));

        AgendaServiceImpl service = new AgendaServiceImpl(icsCalendarParser, fetchIcsService);

        try (InputStream icsStream = InputStream.nullInputStream()) {
            assertThatThrownBy(() -> service.getEvenements(icsStream, UserProfile.ELEVE))
                    .isInstanceOf(IcsParsingException.class)
                    .hasCauseInstanceOf(IOException.class);
        }
    }

}
