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
import { faCircleExclamation, faCircleInfo } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { fetchPronotePage } from '@/api/pronote'
import FicheEleve from '@/components/FicheEleve.vue'
import FicheEleveSkeleton from '@/components/FicheEleveSkeleton.vue'
import FicheProfesseur from '@/components/FicheProfesseur.vue'
import { initConfiguration, useConfiguration } from '@/composables/useConfiguration'

import '@gip-recia/ui-webcomponents/dist/r-page-layout.js'
import '@gip-recia/ui-webcomponents/dist/r-tabs.js'

const { configuration, isInit } = useConfiguration()
const { t } = useI18n()

const appName = __APP_NAME__

const data = ref<PronotePageResponse | null>(null)
const error = ref(false)
const loading = ref(true)

const backLink = computed(() => ({
  href: '/portail',
  name: t('app.backToPortal'),
}))

onMounted(async () => {
  initConfiguration().catch((e) => {
    console.error('Échec du chargement de la configuration extended-uPortal', e)
  })

  try {
    data.value = await fetchPronotePage()
  }
  catch (e) {
    console.error('Échec du chargement de la page Pronote', e)
    error.value = true
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <nav role="navigation" :aria-label="t('app.quickAccess')" class="skip-links">
    <ul>
      <li>
        <a href="#main">{{ t('app.skipToContent') }}</a>
      </li>
    </ul>
  </nav>

  <header>
    <extended-uportal-header
      v-if="isInit" :service-name="appName"
      v-bind="configuration!.front.extendedUportal?.header?.props"
    />
  </header>

  <main id="main" tabindex="-1">
    <div class="container">
      <span class="sr-only" aria-live="polite">
        {{ loading ? t('app.loading') : '' }}
      </span>

      <r-page-layout :page-title="t('app.pageTitle')" :back-link="JSON.stringify(backLink)">
        <p class="data-source-note">
          <FontAwesomeIcon :icon="faCircleInfo" aria-hidden="true" />
          {{ t('app.dataSourceNote') }}
        </p>
        <FicheEleveSkeleton v-if="loading" />
        <div v-else-if="error" class="r-card error-card" role="alert">
          <FontAwesomeIcon :icon="faCircleExclamation" aria-hidden="true" />
          <p>{{ t('app.error') }}</p>
        </div>
        <template v-else-if="data && data.profil === 'Professeur'">
          <FicheProfesseur :professeur="data.professeurDto" />
        </template>
        <template v-else-if="data && data.eleveDtoList.length > 1">
          <r-tablist
            id-prefix="eleves"
            :tabs="data.eleveDtoList.map(eleve => eleve.prenom ?? t('app.defaultEleveLabel'))" active-tab="0"
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

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;

r-tabpanel:not([active]) {
  display: none;
}

.data-source-note {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 8px;
  font-size: var(--#{$prefix}font-size-xs);
  color: var(--#{$prefix}basic-black-lighter);
  margin-bottom: 20px;

  svg {
    flex: none;
    width: 14px;
    color: var(--#{$prefix}basic-black-lighter);
  }
}

.error-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  max-width: 480px;
  margin: 40px auto;
  padding: 32px 24px;
  text-align: center;

  svg {
    width: 32px;
    height: 32px;
    color: var(--#{$prefix}system-red);
  }

  p {
    margin: 0;
    color: var(--#{$prefix}basic-black-lighter);
  }
}
</style>
