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

import fr.recia.pronote.pronoteapi.model.cahierdetextes.TravailAFaire;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class TravailAFaireDto
{

    protected String coursId;

    // à récupérer lors de creation
    protected String matiere;

    @Nullable
    protected String descriptif;
    protected Date pourLe;
    @Nullable
    protected List<String> pieceJointeList;
    @Nullable
    protected List<String> siteInternetList;

    public TravailAFaireDto(TravailAFaire travailAFaire, String id, String matiere) {
        this.descriptif = travailAFaire.getDescriptif();
        this.pourLe = travailAFaire.getPourLe();
        this.coursId = id;
        this.matiere = matiere;

        if(Objects.nonNull(travailAFaire.getPieceJointeList())){
            this.pieceJointeList = new ArrayList<>(travailAFaire.getPieceJointeList());
        }

        if(Objects.nonNull(travailAFaire.getSiteInternetList())){
            this.siteInternetList = new ArrayList<>(travailAFaire.getSiteInternetList());
        }
    }

    @Override
    public String toString() {
        return "TravailAfaireDto{" +
                "coursId='" + coursId + '\'' +
                ", matiere='" + matiere + '\'' +
                ", descriptif='" + descriptif + '\'' +
                ", pourLe=" + pourLe +
                ", pieceJointeList=" + pieceJointeList +
                ", siteInternetList=" + siteInternetList +
                '}';
    }
}
