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
package fr.recia.pronote.pronoteapi.dto.messagerie;

import fr.recia.pronote.pronoteapi.dto.IWidgetCountable;
import fr.recia.pronote.pronoteapi.dto.WidgetItemKeys;
import fr.recia.pronote.pronoteapi.model.PageMessagerie;
import lombok.Data;

import java.util.Map;

@Data
public class MessagerieDto implements IWidgetCountable {

    private Integer nombreMessagesNonLus;
    private Integer nombreInformationsNonLus;

    public MessagerieDto(PageMessagerie pageMessagerie) {
        this.nombreMessagesNonLus = pageMessagerie.getDiscussions() != null
                ? pageMessagerie.getDiscussions().getNombreMessagesNonLus() : 0;
        this.nombreInformationsNonLus = pageMessagerie.getInformations() != null
                ? pageMessagerie.getInformations().getNombreInformationsNonLus() : 0;
    }

    @Override
    public Map<String, Integer> widgetCounts() {
        Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        counts.put(WidgetItemKeys.MESSAGES_NON_LUS, nombreMessagesNonLus);
        counts.put(WidgetItemKeys.INFORMATIONS_NON_LUES, nombreInformationsNonLus);
        return counts;
    }
}