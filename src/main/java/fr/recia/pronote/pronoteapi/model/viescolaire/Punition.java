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
package fr.recia.pronote.pronoteapi.model.viescolaire;

import fr.recia.pronote.pronoteapi.model.abs.PageAttribute;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@JacksonXmlRootElement(localName = "Punition")
public class Punition extends PageAttribute {

    @JacksonXmlProperty(localName = "Date")
    protected Date date;

    @JacksonXmlProperty(localName = "Nature")
    protected String nature;

    @Nullable
    @JacksonXmlProperty(localName = "Matiere")
    protected String matiere;

    @JacksonXmlProperty(localName = "Motif")
    protected String motif;

    @Nullable
    @JacksonXmlProperty(localName = "Circonstances")
    protected String circonstances;



}
