/*
 * Copyright © ${project.inceptionYear} GIP-RECIA (https://www.recia.fr/)
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

import com.fasterxml.jackson.annotation.JsonGetter;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.CahierDeTextes;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.ContenuDeCours;
import fr.recia.pronote.pronoteapi.model.cahierdetextes.TravailAFaire;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class CahierDeTextesDto {


    public CahierDeTextesDto(CahierDeTextes cahierDeTextes){
        this.contenuDeCoursList = cahierDeTextes.getContenuDeCoursList();
        this.travailAFaireList = cahierDeTextes.getTravailAFaireList();
        this.matiere = cahierDeTextes.getMatiere();
        this.date = cahierDeTextes.getDate();
    }


    protected String matiere;

    protected Date date;

    @Nullable
    List<ContenuDeCours> contenuDeCoursList;

    @Nullable
    List<TravailAFaire> travailAFaireList;

    @JsonGetter("isPast")
    public boolean isPast(){
        return date.toInstant().isBefore(Instant.now());
    }


}
