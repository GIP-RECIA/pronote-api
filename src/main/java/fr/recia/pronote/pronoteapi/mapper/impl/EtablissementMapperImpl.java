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
package fr.recia.pronote.pronoteapi.mapper.impl;

import fr.recia.pronote.pronoteapi.mapper.IEtablissementMapper;
import fr.recia.pronote.pronoteapi.model.PagePronote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class EtablissementMapperImpl implements IEtablissementMapper {

    @Override
    public String map(List<PagePronote> pagePronoteList) {
        return Objects.nonNull(pagePronoteList) && !pagePronoteList.isEmpty()
                ? pagePronoteList.getFirst().getNom()
                : null;
    }
}
