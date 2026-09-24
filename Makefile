.PHONY: test redis-start redis-stop redis-restart run run-mock-eleve run-mock-parent-1 run-mock-parent-2 mock-webcomponents

REDIS_CONTAINER := redis-local

test:
	./mvnw test

# Démarre un Redis local jetable (Docker) si besoin, ou relance le conteneur existant.
redis-start:
	@if [ -n "$$(docker ps -q -f name=^/$(REDIS_CONTAINER)$$)" ]; then \
		echo "Redis already running ($(REDIS_CONTAINER))"; \
	elif [ -n "$$(docker ps -aq -f name=^/$(REDIS_CONTAINER)$$)" ]; then \
		docker start $(REDIS_CONTAINER); \
	else \
		docker run -d --name $(REDIS_CONTAINER) -p 6379:6379 redis:7-alpine; \
	fi

redis-stop:
	docker stop $(REDIS_CONTAINER)

redis-restart: redis-stop redis-start

# Lance l'appli avec le vrai CAS (nécessite Redis + config CAS réelle).
run: redis-start
	./mvnw spring-boot:run

# Copie les webcomponents extended-uPortal (déjà présents dans node_modules) là où Vite
# les sert réellement en dev (préfixés par base), pour remplacer le vrai resource-server distant.
WEBCOMPONENTS_DIST := src/main/webapp/public/resource-server/webjars/gip-recia__ui-webcomponents/dist

mock-webcomponents:
	mkdir -p $(WEBCOMPONENTS_DIST)
	cp node_modules/@gip-recia/ui-webcomponents/dist/r-header.js* node_modules/@gip-recia/ui-webcomponents/dist/r-footer.js* $(WEBCOMPONENTS_DIST)/

# Lance l'appli en mode mock (pas de vrai CAS), un scénario par cible.
# Combine le profil "local" (namespace Redis, noms de profils...) et "mock-no-cas",
# en forçant redis.hostName/port vers le Redis local (Docker) plutôt que le Redis distant,
# et componentPath vers l'URL que Vite sert réellement (préfixée par sa base /claire/ui).
MOCK_ARGS = --app.mock.enabled=true --redis.hostName=localhost --redis.port=6379 --redis.userName= --redis.password= --server.servlet.context-path=/claire \
	--app.front.extended-uportal.header.component-path=/claire/ui/resource-server/webjars/gip-recia__ui-webcomponents/dist/r-header.js \
	--app.front.extended-uportal.footer.component-path=/claire/ui/resource-server/webjars/gip-recia__ui-webcomponents/dist/r-footer.js

run-mock-eleve: redis-start mock-webcomponents
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local,mock-no-cas -Dspring-boot.run.arguments="$(MOCK_ARGS) --app.mock.scenario=eleve"

run-mock-parent-1: redis-start mock-webcomponents
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local,mock-no-cas -Dspring-boot.run.arguments="$(MOCK_ARGS) --app.mock.scenario=parentUnEnfant"

run-mock-parent-2: redis-start mock-webcomponents
	./mvnw spring-boot:run -Dspring-boot.run.profiles=local,mock-no-cas -Dspring-boot.run.arguments="$(MOCK_ARGS) --app.mock.scenario=parentDeuxEnfants"
