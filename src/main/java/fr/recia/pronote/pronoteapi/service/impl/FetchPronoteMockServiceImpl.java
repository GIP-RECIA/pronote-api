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
package fr.recia.pronote.pronoteapi.service.impl;

import fr.recia.pronote.pronoteapi.config.bean.MockProperties;
import fr.recia.pronote.pronoteapi.exception.PronoteXmlFetchException;
import fr.recia.pronote.pronoteapi.service.IFetchPronoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@Profile("mock-no-cas")
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class FetchPronoteMockServiceImpl implements IFetchPronoteService {

    private final MockProperties mockProperties;

    @Override
    public String getPronoteXmlAsString() {
        String fileName = switch (mockProperties.getScenario()) {
            case eleve -> "pronote-mock-eleve.xml";
            case parentUnEnfant -> "pronote-mock-parent-un-enfant.xml";
            case parentDeuxEnfants -> "pronote-mock-parent.xml";
        };

        log.debug("Serving mock Pronote XML for scenario {} ({})", mockProperties.getScenario(), fileName);

        try (InputStream in = getClass().getResourceAsStream("/mock/" + fileName)) {
            if (in == null) {
                throw new PronoteXmlFetchException("Mock Pronote fixture not found on classpath: " + fileName);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PronoteXmlFetchException("Unable to read mock Pronote fixture: " + fileName);
        }
    }
}
