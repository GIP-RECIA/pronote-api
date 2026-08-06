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
package fr.recia.pronote.pronoteapi.model.cahierdetextes;

import fr.recia.pronote.pronoteapi.model.abs.PageAttribute;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JacksonXmlRootElement(localName = "ContenuDeCours")
public class ContenuDeCours extends PageAttribute {

    @Nullable
    @JacksonXmlProperty(localName = "Titre")
    protected String titre;

    @Nullable
    @JacksonXmlProperty(localName = "Categorie")
    protected String categorie;

    @Nullable
    @JacksonXmlProperty(localName = "Descriptif")
    protected String descriptif;

    @Nullable
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "PieceJointe")
    protected List<String> pieceJointeList;

    @Nullable
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SiteInternet")
    protected List<String> siteInternet;


}
