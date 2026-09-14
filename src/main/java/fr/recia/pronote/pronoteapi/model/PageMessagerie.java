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
import fr.recia.pronote.pronoteapi.model.messagerie.Discussions;
import fr.recia.pronote.pronoteapi.model.messagerie.Informations;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@EqualsAndHashCode(callSuper = true)
@Data
@JacksonXmlRootElement(localName = "PageMessagerie")
public class PageMessagerie extends TitreMessageElementsPageAttribute {
    // pas le page attribute apparement d'apres le eleve xsd
    // todo voir si créer parent différent

    @Nullable
    @JacksonXmlProperty(localName = "Discussions")
    protected Discussions discussions;

    @Nullable
    @JacksonXmlProperty(localName = "Informations")
    protected Informations informations;

}
