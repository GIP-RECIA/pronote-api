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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

public class SummaryResponseDto extends ArrayList<SummaryResponseDto.EleveSummary> {

    public SummaryResponseDto(List<EleveDto> eleveDtoList) {
        for (EleveDto eleveDto : eleveDtoList) {
            String displayName = buildDisplayName(eleveDto);
            this.add(new EleveSummary(displayName, buildItems(eleveDto)));
        }
    }

    private static String buildDisplayName(EleveDto eleveDto) {
        if (eleveDto.getPrenom() == null && eleveDto.getNom() == null) {
            return null;
        }
        return eleveDto.getPrenom() + " " + eleveDto.getNom();
    }

    private static Map<String, Integer> buildItems(EleveDto eleveDto) {
        Map<String, Integer> items = new LinkedHashMap<>();
        items.put(WidgetItemKeys.DEVOIRS, sizeOrZero(eleveDto.getDevoirDtoList()));
        items.put(WidgetItemKeys.VISITES_INFIRMERIE, 0);
        items.put(WidgetItemKeys.ABSENCES_ET_RETARDS, 0);
        items.put(WidgetItemKeys.PUNITIONS_ET_SANCTIONS, 0);

        for (IWidgetCountable countable : eleveDto.countableComponents()) {
            items.putAll(countable.widgetCounts());
        }

        return items;
    }

    private static int sizeOrZero(List<?> list) {
        return Objects.isNull(list) ? 0 : list.size();
    }


    public record EleveSummary(@JsonInclude(JsonInclude.Include.NON_NULL) String displayName, Map<String, Integer> items) {}
}
