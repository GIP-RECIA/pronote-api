package fr.recia.pronote.pronoteapi.dto.competences;

import fr.recia.pronote.pronoteapi.dto.IWidgetCountable;
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
        counts.put("evaluations_de_competences", nombreEvaluations);
        return counts;
    }
}