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
package fr.recia.pronote.pronoteapi.service.impl;

import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.factory.ResumeCoursEtTravailAFaireAllDtoFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchAndParseEleveDataServiceFromParentImplTest {

    private static final String XML_DEUX_ENFANTS_MEME_PRENOM = """
            <Parent>
                <Eleve>
                    <Nom>Martin</Nom>
                    <Prenom>Lucas</Prenom>
                </Eleve>
                <Eleve>
                    <Nom>Martin</Nom>
                    <Prenom>Lucas</Prenom>
                </Eleve>
            </Parent>
            """;

    @Mock
    FetchPronoteServiceImpl fetchPronoteService;

    private final ResumeCoursEtTravailAFaireAllDtoFactory factory = new ResumeCoursEtTravailAFaireAllDtoFactory();

    @Test
    void getDto_withTwoChildrenSharingTheSameFirstName_keepsBothAsSeparateEntries() {
        when(fetchPronoteService.getPronoteXmlAsString()).thenReturn(XML_DEUX_ENFANTS_MEME_PRENOM);

        FetchAndParseEleveDataServiceFromParentImpl service =
                new FetchAndParseEleveDataServiceFromParentImpl(fetchPronoteService, factory);

        List<EleveDto> result = service.getDto("some-uid");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(EleveDto::getPrenom).containsExactly("Lucas", "Lucas");
        assertThat(result).extracting(EleveDto::getNom).containsExactly("Martin", "Martin");
    }
}