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
package fr.recia.pronote.pronoteapi.dto.viescolaire;

import fr.recia.pronote.pronoteapi.model.viescolaire.Punition;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.Date;

@Data
public class PunitionDto {

    public PunitionDto(Punition punition){
        this.date = punition.getDate();
        this.nature = punition.getNature();
        this.matiere = punition.getMatiere();
        this.motif = punition.getMotif();
        this.circonstances = punition.getCirconstances();
    }

    protected Date date;

    protected String nature;

    @Nullable
    protected String matiere;

    protected String motif;

    @Nullable
    protected String circonstances;

}
