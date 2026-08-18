# Pronote API

Api qui interroge Pronote (via le protocole Proxy Ticket de CAS) afin de récupérer les informations des 7 derniers jours concernant la personne, et filtre les informations pour ne renvoyer que celles que l'on souhaite restituer sur l'ENT.

### Commandes pour notice et license

- `mvn notice:check`
- `mvn notice:generate`
- `mvn license:check`
- `mvn license:format`
- `mvn license:remove`


### Run with local profile

- `mvn spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments="--spring.config.additional-location=file:${PATH_PROPERTIES}/PronoteWidgets/"`
