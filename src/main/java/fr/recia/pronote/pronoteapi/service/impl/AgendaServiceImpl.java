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
import fr.recia.pronote.pronoteapi.exception.IcsFetchException;
import fr.recia.pronote.pronoteapi.exception.IcsParsingException;
import fr.recia.pronote.pronoteapi.ical.IcsCalendarParser;
import fr.recia.pronote.pronoteapi.ical.IcsEvent;
import fr.recia.pronote.pronoteapi.service.IAgendaService;
import fr.recia.pronote.pronoteapi.service.IFetchIcsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public final class AgendaServiceImpl implements IAgendaService {

    private final IcsCalendarParser icsCalendarParser;
    private final IFetchIcsService fetchIcsService;
    @Override
    public List<EvenementAgendaDto> getEvenements(InputStream icsStream, UserProfile profile) {
        List<EvenementAgendaDto> dtos;
        try {
            List<IcsEvent> events = icsCalendarParser.parse(icsStream);
            dtos = events.stream().map(event -> evenementAgendaDto(event, profile)).toList();
        } catch (ParserException | IOException e) {
            throw new IcsParsingException(e.getMessage(), e);
        }
        return dtos;
    }

    @Override
    public List<EvenementAgendaDto> getEvenementsFromUrl(@Nullable String icalUrl, UserProfile profile) {
        if (icalUrl == null) {
            return List.of();
        }
        try {
            return getEvenements(fetchIcsService.fetchIcs(icalUrl), profile);
        } catch (IcsFetchException | IcsParsingException e) {
            log.warn("Unable to build agenda for ical url {}: {}", icalUrl, e.getMessage());
            return List.of();
        }
    }

    private EvenementAgendaDto evenementAgendaDto(IcsEvent event, UserProfile profile) {
        CategorieEvenement categorie = event.categories().contains("Cours")
                ? CategorieEvenement.COURS
                : CategorieEvenement.JOUR_FERIE;

        String matiere = null;
        String professeur = null;
        String classe = null;

        if (categorie == CategorieEvenement.COURS) {
            String[] parts = event.summary().split(" - ", 3);
            matiere = parts[0];
            String complement = parts.length > 1 ? parts[1] : null;
            if (profile == UserProfile.PROFESSEUR) {
                classe = complement;
            } else {
                professeur = complement;
            }
        }

        return EvenementAgendaDto.builder()
                .matiere(matiere)
                .categorie(categorie)
                .salle(event.location())
                .debut(event.start())
                .fin(event.end())
                .professeur(professeur)
                .classe(classe)
                .build();
    }
}
