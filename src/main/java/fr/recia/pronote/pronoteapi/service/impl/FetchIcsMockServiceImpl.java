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
import fr.recia.pronote.pronoteapi.exception.IcsFetchException;
import fr.recia.pronote.pronoteapi.service.IFetchIcsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Profile("mock-no-cas")
@Service
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class FetchIcsMockServiceImpl implements IFetchIcsService {

    private final MockProperties mockProperties;
    @Override
    public InputStream fetchIcs(String icalUrl) {
        String fileName = mockProperties.getScenario().icsFixtureFileName();
        InputStream in = getClass().getResourceAsStream("/mock/" + fileName);
        if (in == null) {
            throw new IcsFetchException("Mock ICS fixture not found on classpath: " + fileName);
        }
        return in;
    }
}
