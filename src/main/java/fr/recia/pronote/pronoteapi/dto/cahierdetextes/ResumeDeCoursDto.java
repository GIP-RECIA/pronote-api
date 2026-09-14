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
package fr.recia.pronote.pronoteapi.dto.cahierdetextes;

import fr.recia.pronote.pronoteapi.model.cahierdetextes.CahierDeTextes;
import jakarta.annotation.Nullable;
import lombok.Data;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class ResumeDeCoursDto {


    public ResumeDeCoursDto(CahierDeTextes cahierDeTextes){
        if(Objects.nonNull(cahierDeTextes.getContenuDeCoursList())){
            this.contenuDeCoursList = cahierDeTextes.getContenuDeCoursList().stream().map(x ->  new ContenuDeCoursDto(
                    x.getTitre(),
                    x.getCategorie(),
                    x.getDescriptif(),
                    Objects.nonNull(x.getPieceJointeList()) ? new ArrayList<>(x.getPieceJointeList()) : null,
                    Objects.nonNull(x.getSiteInternet()) ? new ArrayList<>(x.getSiteInternet()) : null
            )).toList();
        }


        this.id = UUID.randomUUID().toString();
        this.matiere = cahierDeTextes.getMatiere();
        this.date = cahierDeTextes.getDate();
    }


    String id;

    protected String matiere;

    protected Date date;

    @Nullable
    List<ContenuDeCoursDto> contenuDeCoursList;

    @Override
    public String toString() {
        return "ResumeDeCoursDto{" +
                "id='" + id + '\'' +
                ", matiere='" + matiere + '\'' +
                ", date=" + date +
                ", contenuDeCoursList=" + contenuDeCoursList +
                '}';
    }
}
