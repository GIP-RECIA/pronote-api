package fr.recia.pronote.pronoteapi.config.custom.impl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * Filtre CAS pour le Single Logout (SLO).
 */
@Slf4j
@RequiredArgsConstructor
public class SingleSignOutHandlerFilter extends OncePerRequestFilter {

    private final CustomSessionMappingStorage ticketSessionMappingStorage;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String logoutRequest = request.getParameter("logoutRequest");
        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.debug("[SLO] Requête entrante : {} {} depuis IP={}", method, uri, ip);

        if (logoutRequest != null) {
            log.trace("[SLO] URI appelée : {}", uri);
            log.trace("[SLO] Adresse IP appelante : {}", ip);
            log.trace("[SLO] XML logoutRequest brut :\n{}", logoutRequest);

            try {
                var factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                factory.setXIncludeAware(false);
                factory.setExpandEntityReferences(false);

                var builder = factory.newDocumentBuilder();
                var doc = builder.parse(new InputSource(new StringReader(logoutRequest)));

                doc.getDocumentElement().normalize();

                var nameIdNode = doc.getElementsByTagNameNS("*", "NameID").item(0);
                var sessionIndexNode = doc.getElementsByTagNameNS("*", "SessionIndex").item(0);

                String nameId = nameIdNode != null ? nameIdNode.getTextContent() : "inconnu";
                String ticket = sessionIndexNode != null ? sessionIndexNode.getTextContent() : "inconnu";

                int index = ticket.indexOf('-');
                boolean isSessionTicket = false;

                if (index != -1) {
                    String beforeDash = ticket.substring(0, index + 1);
                    if ("ST-".equals(beforeDash)) {
                        isSessionTicket = true;
                    }
                }

                if (isSessionTicket) {
                    log.debug("[SLO] Ticket Invalidation Request will be handled: {}", ticket);
                } else {
                    log.debug("[SLO] Ticket Invalidation Request will be ignored: {}", ticket);
                    filterChain.doFilter(request, response);
                    return;
                }

                String sessionId = ticketSessionMappingStorage.getSessionIdFromSessionTicket(ticket);

                log.debug("[SLO] Utilisateur CAS (NameID) : {}", nameId);
                log.debug("[SLO] Session id: {}", sessionId);

                ticketSessionMappingStorage.removeSessionTicket(ticket);
                log.debug("[SLO] Le cache associé au mappage ticket-sessionID [{}:{}] a été supprimé avec succès.", ticket, sessionId);
                ticketSessionMappingStorage.deleteSessionContext(sessionId);
                log.debug("[SLO] Invalidation réussie de la session [{}].", sessionId);

            } catch (Exception e) {
                log.error("[SLO] Erreur de parsing XML logoutRequest", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}