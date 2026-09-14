package fr.recia.pronote.pronoteapi.dto.factory;

import fr.recia.pronote.pronoteapi.dto.ResumeCoursEtTravailAFaireAllDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.TravailAFaireDto;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.CahierDeTextes;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.TravailAFaire;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeCoursEtTravailAFaireAllDtoFactoryTest {

    private final ResumeCoursEtTravailAFaireAllDtoFactory factory = new ResumeCoursEtTravailAFaireAllDtoFactory();

    @Test
    void create_splitsResumesAndTravailAFaire_andLinksThemByCoursId() {
        TravailAFaire travailAFaire = new TravailAFaire();
        travailAFaire.setDescriptif("Exercices p.42");
        travailAFaire.setPourLe(new Date());

        CahierDeTextes coursMaths = new CahierDeTextes();
        coursMaths.setMatiere("Mathématiques");
        coursMaths.setDate(new Date());
        coursMaths.setTravailAFaireList(List.of(travailAFaire));

        // un cours de français sans travail à faire
        CahierDeTextes coursFrancais = new CahierDeTextes();
        coursFrancais.setMatiere("Français");
        coursFrancais.setDate(new Date());

        ResumeCoursEtTravailAFaireAllDto result = factory.create(List.of(coursMaths, coursFrancais));

        assertThat(result.getResumeCoursDtoList()).hasSize(2);
        assertThat(result.getResumeCoursDtoList().get(0).getMatiere()).isEqualTo("Mathématiques");
        assertThat(result.getResumeCoursDtoList().get(1).getMatiere()).isEqualTo("Français");

        assertThat(result.getTravailAFaireDtoList()).hasSize(1);
        TravailAFaireDto travailAFaireDto = result.getTravailAFaireDtoList().get(0);
        assertThat(travailAFaireDto.getDescriptif()).isEqualTo("Exercices p.42");
        assertThat(travailAFaireDto.getMatiere()).isEqualTo("Mathématiques");
        // le travail à faire doit être lié à l'id généré pour le résumé de SON cours
        assertThat(travailAFaireDto.getCoursId()).isEqualTo(result.getResumeCoursDtoList().get(0).getId());
    }

    @Test
    void create_withEmptyList_returnsEmptyLists() {
        ResumeCoursEtTravailAFaireAllDto result = factory.create(List.of());

        assertThat(result.getResumeCoursDtoList()).isEmpty();
        assertThat(result.getTravailAFaireDtoList()).isEmpty();
    }
}
