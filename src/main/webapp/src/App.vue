<!--
 Copyright © 2026 GIP-RECIA (https://www.recia.fr/)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import type { PronotePageResponse } from '@/types/pronote'
import { onMounted, ref } from 'vue'
import { fetchPronotePage } from '@/api/pronote'
import { initConfiguration, useConfiguration } from '@/composables/useConfiguration'

const { configuration, isInit } = useConfiguration()

const appName = __APP_NAME__

const data = ref<PronotePageResponse | null>(null)
const error = ref<string | null>(null)
const loading = ref(true)

onMounted(async () => {
  initConfiguration()

  try {
    data.value = await fetchPronotePage()
  }
  catch (e) {
    error.value = e instanceof Error ? e.message : String(e)
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <header>
    <extended-uportal-header
      v-if="isInit" :service-name="appName"
      v-bind="configuration!.front.extendedUportal?.header?.props"
    />
  </header>

  <main>
    <div class="container">
      <p>a</p>
      <p>a</p>
      <p>a</p>
      <p>a</p>

      <p v-if="loading">
        Chargement...
      </p>
      <p v-else-if="error">
        Erreur : {{ error }}
      </p>
      <template v-else-if="data">
        <div v-for="(eleve, index) in data.eleveDtoList" :key="index">
          <h1>{{ eleve.prenom ?? 'Élève' }} {{ eleve.nom ?? '' }}</h1>
          <p v-if="eleve.etablissement">
            {{ eleve.etablissement }}
          </p>

          <section>
            <h2>Devoirs</h2>
            <ul v-if="eleve.devoirDtoList">
              <li v-for="(devoir, i) in eleve.devoirDtoList" :key="i">
                {{ devoir.matiere }} — {{ devoir.note }}/{{ devoir.bareme }}
              </li>
            </ul>
            <p v-else>
              Aucun devoir
            </p>
          </section>

          <section v-if="eleve.vieScolaireDto?.absenceList">
            <h2>Absences</h2>
            <ul>
              <li v-for="(absence, i) in eleve.vieScolaireDto.absenceList" :key="i">
                Du {{ absence.dateDebut }} — {{ absence.justifie ? 'justifiée' : 'non justifiée' }}
                <span v-if="absence.motif">({{ absence.motif }})</span>
              </li>
            </ul>
          </section>

          <section v-if="eleve.messagerieDto">
            <h2>Messagerie</h2>
            <p>{{ eleve.messagerieDto.nombreMessagesNonLus }} messages non lus</p>
            <p>{{ eleve.messagerieDto.nombreInformationsNonLus }} informations non lues</p>
          </section>

          <section v-if="eleve.competencesDto">
            <h2>Compétences</h2>
            <p>{{ eleve.competencesDto.nombreEvaluations }} évaluations</p>
          </section>
        </div>
      </template>
    </div>
  </main>

  <footer>
    <extended-uportal-footer v-if="isInit" v-bind="configuration!.front.extendedUportal?.footer?.props" />
  </footer>
</template>
