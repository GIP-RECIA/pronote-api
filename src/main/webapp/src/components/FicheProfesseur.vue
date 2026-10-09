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
import type { Professeur } from '@/types/pronote'
import { useI18n } from 'vue-i18n'
import AgendaSemaine from '@/components/AgendaSemaine.vue'

defineProps<{
  professeur: Professeur
}>()

const { t } = useI18n()
</script>

<template>
  <article class="fiche-professeur">
    <div class="layout">
      <div v-if="professeur.iCal" class="r-card agenda-section">
        <AgendaSemaine :evenements="professeur.evenementsAgenda ?? []" :ical-url="professeur.iCal" :index="0" badge="classe" />
      </div>

      <div class="r-card messagerie" :aria-label="t('messagerie.heading')">
        <div class="stat">
          <span class="num">{{ professeur.messagerieDto?.nombreMessagesNonLus ?? 0 }}</span>
          <span class="lbl">{{ t('messagerie.unreadMessages') }}</span>
        </div>
        <div class="stat">
          <span class="num">{{ professeur.messagerieDto?.nombreInformationsNonLus ?? 0 }}</span>
          <span class="lbl">{{ t('messagerie.unreadInfos') }}</span>
        </div>
        <div class="stat">
          <span class="num">{{ professeur.messagerieDto?.nombreDocumentsCasierNonLus ?? 0 }}</span>
          <span class="lbl">{{ t('ficheProfesseur.unreadCasier') }}</span>
        </div>
      </div>
    </div>
  </article>
</template>

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;

.layout {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.messagerie {
  display: flex;
  flex-direction: column;
  gap: 8px;

  .stat {
    display: flex;
    align-items: baseline;
    gap: 8px;

    .num {
      font-size: var(--#{$prefix}font-size-xl);
      font-weight: 700;
      color: var(--#{$prefix}primary);
    }

    .lbl {
      font-size: var(--#{$prefix}font-size-xs);
      color: var(--#{$prefix}basic-black-lighter);
    }
  }
}
</style>
