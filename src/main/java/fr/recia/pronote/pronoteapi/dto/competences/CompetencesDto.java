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
package fr.recia.pronote.pronoteapi.dto.competences;

import fr.recia.pronote.pronoteapi.dto.IWidgetCountable;
import fr.recia.pronote.pronoteapi.dto.WidgetItemKeys;
import fr.recia.pronote.pronoteapi.model.PageCompetences;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class CompetencesDto implements IWidgetCountable {

    Integer nombreEvaluations;

    public CompetencesDto(PageCompetences pageCompetences) {
        this.nombreEvaluations = Objects.nonNull(pageCompetences.getEvaluationList())
                ? pageCompetences.getEvaluationList().size() : 0;
    }

    @Override
    public Map<String, Integer> widgetCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put(WidgetItemKeys.EVALUATIONS_DE_COMPETENCES, nombreEvaluations);
        return counts;
    }
}