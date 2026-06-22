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
package fr.recia.pronote.pronoteapi.model;

import fr.recia.pronote.pronoteapi.model.abs.TitreMessageElementsPageAttribute;
import fr.recia.pronote.pronoteapi.model.viescolaire.Absence;
import fr.recia.pronote.pronoteapi.model.viescolaire.Observation;
import fr.recia.pronote.pronoteapi.model.viescolaire.PassageInfirmerie;
import fr.recia.pronote.pronoteapi.model.viescolaire.Punition;
import fr.recia.pronote.pronoteapi.model.viescolaire.Retard;
import fr.recia.pronote.pronoteapi.model.viescolaire.Sanction;
import jakarta.annotation.Nullable;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "PageVieScolaire")
public class PageVieScolaire extends TitreMessageElementsPageAttribute {

    @Nullable
    @JacksonXmlProperty(localName = "Absence")
    protected List<Absence> absenceList;

    @Nullable
    @JacksonXmlProperty(localName = "Retard")
    protected List<Retard> retardList;

    @Nullable
    @JacksonXmlProperty(localName = "PassageInfirmerie")
    protected List<PassageInfirmerie> passageInfirmerieList;

    @Nullable
    @JacksonXmlProperty(localName = "Punition")
    protected List<Punition> punitionList;

    @Nullable
    @JacksonXmlProperty(localName = "Sanction")
    protected List<Sanction> sanctionList;

    @Nullable
    @JacksonXmlProperty(localName = "Observation")
    protected List<Observation> observation;




}
