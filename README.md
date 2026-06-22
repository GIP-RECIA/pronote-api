### Commandes pour notice et license

- `mvn notice:check`
- `mvn notice:generate`
- `mvn license:check`
- `mvn license:format`
- `mvn license:remove`


# Run with local profile

- `mvn spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.arguments="--spring.config.additional-location=file:${PATH_PROPERTIES}/PronoteWidgets/"`
