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

import fr.recia.pronote.pronoteapi.config.bean.AppConfProperties;
import fr.recia.pronote.pronoteapi.service.IPronoteMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PronoteMappingServiceImpl implements IPronoteMappingService {

    private static final String DEFAULT_KEY = "DEFAULT";
    private static final String REGEX_LINK_KEY = "REGEX_LINK";
    private static final String PRONOTE_DOMAIN_MARKER = "index-education.net";
    private static final String UAI_PLACEHOLDER = "%ESCOUAICourant%";
    private static final String FALLBACK_DEFAULT_TEMPLATE = "https://" + UAI_PLACEHOLDER + ".index-education.net/pronote/";

    private final AppConfProperties appConfProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    private volatile Map<String, String> uaiToBaseUrl = Map.of();
    private volatile String defaultUrlTemplate = FALLBACK_DEFAULT_TEMPLATE;

    @Override
    public String getBaseUrl(String uai) {
        String url = uaiToBaseUrl.get(uai);
        if (url != null) {
            return url;
        }
        String fallback = appConfProperties.getUaiReplacementMap().get(uai);
        if (fallback != null) {
            return defaultUrlTemplate.replace(UAI_PLACEHOLDER, fallback);
        }
        return defaultUrlTemplate.replace(UAI_PLACEHOLDER, uai);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRate = 86_400_000L)
    @Override
    public void refresh() {
        String mappingUrl = appConfProperties.getPronoteMappingUrl();
        if (mappingUrl == null || mappingUrl.isBlank()) {
            log.warn("app.conf.pronote-mapping-url is not configured, keeping default UAI resolution only");
            return;
        }

        try {
            String body = restTemplate.getForObject(mappingUrl, String.class);
            Map<String, String> raw = jsonMapper.readValue(body, new TypeReference<Map<String, String>>() {
            });

            Map<String, String> filtered = raw.entrySet().stream()
                    .filter(entry -> !DEFAULT_KEY.equals(entry.getKey()) && !REGEX_LINK_KEY.equals(entry.getKey()))
                    .filter(entry -> entry.getValue() != null && entry.getValue().contains(PRONOTE_DOMAIN_MARKER))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            this.uaiToBaseUrl = filtered;
            if (raw.containsKey(DEFAULT_KEY)) {
                this.defaultUrlTemplate = raw.get(DEFAULT_KEY);
            }

            log.info("Pronote UAI mapping refreshed: {} établissement(s)", filtered.size());
        } catch (Exception e) {
            log.error("Failed to refresh Pronote UAI mapping from {}, keeping previous cache", mappingUrl, e);
        }
    }
}
