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

import fr.recia.pronote.pronoteapi.exception.IcsFetchException;
import fr.recia.pronote.pronoteapi.service.IFetchIcsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Profile("!mock-no-cas")
@Service
public class FetchIcsServiceImpl implements IFetchIcsService {
    @Override
    public InputStream fetchIcs(String icalUrl) {
        try {
            byte[] bytes = new RestTemplate().getForObject(icalUrl, byte[].class);
            if (bytes == null) {
                throw new IcsFetchException("Pronote returned an empty ICS response");
            }
            return new ByteArrayInputStream(bytes);
        } catch (IcsFetchException e) {
            throw e;
        } catch (Exception e) {
            throw new IcsFetchException("Failed to fetch ICS: " + e.getMessage(), e);
        }
    }
}
