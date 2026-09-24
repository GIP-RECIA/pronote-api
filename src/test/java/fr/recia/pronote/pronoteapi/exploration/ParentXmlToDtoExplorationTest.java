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
package fr.recia.pronote.pronoteapi.exploration;

import fr.recia.pronote.pronoteapi.dto.EleveDto;
import fr.recia.pronote.pronoteapi.dto.PageResponseDto;
import fr.recia.pronote.pronoteapi.dto.SummaryResponseDto;
import fr.recia.pronote.pronoteapi.dto.factory.ResumeCoursEtTravailAFaireAllDtoFactory;
import fr.recia.pronote.pronoteapi.enums.UserProfile;
import fr.recia.pronote.pronoteapi.service.impl.FetchAndParseEleveDataServiceFromParentImpl;
import fr.recia.pronote.pronoteapi.service.impl.FetchPronoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentXmlToDtoExplorationTest {

    @Mock
    FetchPronoteServiceImpl fetchPronoteService;

    @Test
    void printJsonFromXml() throws IOException {
        String xml;
        try (InputStream in = getClass().getResourceAsStream("/mock/pronote-mock-parent.xml")) {
            xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        when(fetchPronoteService.getPronoteXmlAsString()).thenReturn(xml);

        var service = new FetchAndParseEleveDataServiceFromParentImpl(
                fetchPronoteService, new ResumeCoursEtTravailAFaireAllDtoFactory());

        List<EleveDto> eleveDtoList = service.getDto("test-uid");

        JsonMapper jsonMapper = JsonMapper.builder().build();

        System.out.println("=== /api/page (parent) ===");
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new PageResponseDto(UserProfile.parent, eleveDtoList)));

        System.out.println("=== /api/summary (parent) ===");
        System.out.println(jsonMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(new SummaryResponseDto(eleveDtoList)));
    }
}