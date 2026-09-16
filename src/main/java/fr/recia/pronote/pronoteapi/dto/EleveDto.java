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

import jakarta.annotation.Nullable;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.ResumeDeCoursDto;
import fr.recia.pronote.pronoteapi.dto.cahierdetextes.TravailAFaireDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class EleveDto {

    String prenom;
    @Nullable
    String nom;
    @Nullable
    List<ResumeDeCoursDto> resumeDeCoursDtoList;
    @Nullable
    List<TravailAFaireDto> travailAFaireDtoList;
    @Nullable
    VieScolaireDto vieScolaireDto;
    @Nullable
    List<DevoirDto> devoirDtoList;

    @Override
    public String toString() {
        return "ResponseEleveDto{" +
                "resumeDeCoursDtoList=" + resumeDeCoursDtoList +
                ", travailAfaireDtoList=" + travailAFaireDtoList +
                ", vieScolaireDto=" + vieScolaireDto +
                ", devoirDtoList=" + devoirDtoList +
                '}';
    }
}
