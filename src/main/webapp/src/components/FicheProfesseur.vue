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
import { faCircleInfo } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'

defineProps<{
  professeur: Professeur
}>()

const { t } = useI18n()
</script>

<template>
  <article class="fiche-professeur">
    <p class="data-source-note r-card">
      <FontAwesomeIcon :icon="faCircleInfo" aria-hidden="true" />
      {{ t('app.dataSourceNote') }}
    </p>
    <section class="r-card messagerie" aria-labelledby="messagerie-heading">
      <h2 id="messagerie-heading">
        {{ t('messagerie.heading') }}
      </h2>
      <div class="stats">
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
    </section>
  </article>
</template>

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;

.fiche-professeur {
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
}

.messagerie {
  .stats {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-top: 4px;
  }

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
.agenda-link {
  display: inline-flex;
  margin-bottom: 16px;

  &--hidden {
    visibility: hidden;
  }
}
</style>
