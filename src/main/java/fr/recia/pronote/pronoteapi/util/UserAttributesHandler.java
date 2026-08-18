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
package fr.recia.pronote.pronoteapi.util;

import fr.recia.pronote.pronoteapi.config.custom.impl.UserCustomImplementation;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

public class UserAttributesHandler {

  @Autowired
  private HttpSession session;

  public static final String ENT_PERSON_PROFILS = "ENTPersonProfils";
  public static final String UAI_CURRENT = "ESCOUAICourant";
  public static final String UID = "uid";

  private Object getAttributeRaw(String attributeKey) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication.getPrincipal() instanceof UserCustomImplementation) {
      UserCustomImplementation userCustomImplementation = (UserCustomImplementation)authentication.getPrincipal();
      log.trace("getAttributeRaw {}, {} ", attributeKey, userCustomImplementation.getUsername());

      return userCustomImplementation.getAttributes().get(attributeKey);
    }
    return null;
  }

  public String getAttribute(String attributeKey){


    Object attributeRaw = getAttributeRaw(attributeKey);

    if (attributeRaw instanceof String) {
      return (String)attributeRaw;
    }
//    throw new UserAttributeNotFoundException(attributeKey);
    throw new RuntimeException(attributeKey);
  }

  public List<String> getAttributeList(String key) {

    Object value = getAttributeRaw(key);

    if (value instanceof List ) {

      return ((List<?>)value).stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .collect(Collectors.toList())
      ;
    }
    String stringValue = getAttribute(key);

    return List.of(stringValue);

  }
}
