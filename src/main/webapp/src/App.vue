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
import FicheEleve from '@/components/FicheEleve.vue'
import FicheEleveSkeleton from '@/components/FicheEleveSkeleton.vue'
import { initConfiguration, useConfiguration } from '@/composables/useConfiguration'
import '@gip-recia/ui-webcomponents/dist/r-tabs.js'
import '@gip-recia/ui-webcomponents/dist/r-page-layout.js'

const { configuration, isInit } = useConfiguration()

const appName = __APP_NAME__

const data = ref<PronotePageResponse | null>(null)
const error = ref<string | null>(null)
const loading = ref(true)

const backLink = {
  href: '/portail',
  name: 'Retour à l\'accueil',
}

onMounted(async () => {
  initConfiguration().catch((e) => {
    console.error('Échec du chargement de la configuration extended-uPortal', e)
  })

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
      <r-page-layout page-title="Détail Pronote" :back-link="JSON.stringify(backLink)">
        <FicheEleveSkeleton v-if="loading" />
        <p v-else-if="error">
          Erreur : {{ error }}
        </p>
        <template v-else-if="data && data.eleveDtoList.length > 1">
          <r-tablist
            id-prefix="eleves" :tabs="data.eleveDtoList.map(eleve => eleve.prenom ?? 'Élève')" active-tab="0"
            switch-tabpanel
          />
          <r-tabpanel
            v-for="(eleve, index) in data.eleveDtoList" :key="index" id-prefix="eleves" :index.attr="index"
            :active.attr="index === 0 ? true : undefined"
          >
            <FicheEleve :eleve="eleve" :index="index" />
          </r-tabpanel>
        </template>
        <FicheEleve v-else-if="data && data.eleveDtoList[0]" :eleve="data.eleveDtoList[0]" :index="0" />
      </r-page-layout>
    </div>
  </main>

  <footer>
    <extended-uportal-footer v-if="isInit" v-bind="configuration!.front.extendedUportal?.footer?.props" />
  </footer>
</template>
