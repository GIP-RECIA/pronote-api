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
package fr.recia.pronote.pronoteapi.web.rest.exception;

import fr.recia.pronote.pronoteapi.exception.LostTicketException;
import fr.recia.pronote.pronoteapi.exception.MissingUserAttributeException;
import fr.recia.pronote.pronoteapi.exception.UnexpectedProfilException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MESSAGE = "message";
    private static final String DEFAULT_MESSAGE = "Erreur inattendue";

    @ExceptionHandler(UnexpectedProfilException.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedProfil(UnexpectedProfilException ex) {
        log.warn("Unexpected profil", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorBody(ex));
    }

    @ExceptionHandler(MissingUserAttributeException.class)
    public ResponseEntity<Map<String, Object>> handleMissingUserAttribute(MissingUserAttributeException ex) {
        log.warn("Missing user attribute", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorBody(ex));
    }

    @ExceptionHandler(LostTicketException.class)
    public ResponseEntity<Map<String, Object>> handleLostTicketException(LostTicketException ex) {
        log.warn("Lost ticket", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildErrorBody(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorBody(ex));
    }



    private Map<String, Object> buildErrorBody(Exception ex) {
        return Map.of(MESSAGE, Objects.requireNonNullElse(ex.getMessage(), DEFAULT_MESSAGE));
    }
}