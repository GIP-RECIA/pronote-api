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
package fr.recia.pronote.pronoteapi.enums;

import fr.recia.pronote.pronoteapi.config.bean.ProfilsProperties;

import java.util.function.Function;

public enum MockScenario {
    ELEVE("pronote-mock-eleve.xml", ProfilsProperties::getEleveProfilName),
    PARENT_UN_ENFANT("pronote-mock-parent-un-enfant.xml", ProfilsProperties::getParentProfilName),
    PARENT_DEUX_ENFANTS("pronote-mock-parent.xml", ProfilsProperties::getParentProfilName),
    PROFESSEUR("pronote-mock-professeur.xml", ProfilsProperties::getProfesseurProfilName);

    private final String fixtureFileName;
    private final Function<ProfilsProperties, String> profilNameExtractor;

    MockScenario(String fixtureFileName, Function<ProfilsProperties, String> profilNameExtractor) {
        this.fixtureFileName = fixtureFileName;
        this.profilNameExtractor = profilNameExtractor;
    }

    public String fixtureFileName() {
        return fixtureFileName;
    }

    public String profilName(ProfilsProperties profilsProperties) {
        return profilNameExtractor.apply(profilsProperties);
    }
}