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
import type { Messagerie } from '@/types/pronote'
import { useI18n } from 'vue-i18n'

defineProps<{
  messagerie: Messagerie | null
  index: number
}>()

const { t } = useI18n()
</script>

<template>
  <section class="r-card messagerie" :aria-labelledby="`messagerie-heading-${index}`">
    <h2 :id="`messagerie-heading-${index}`">
      {{ t('messagerie.heading') }}
    </h2>

    <div class="stats">
      <div class="stat">
        <span class="num">{{ messagerie?.nombreMessagesNonLus ?? 0 }}</span>
        <span class="lbl">{{ t('messagerie.unreadMessages') }}</span>
      </div>
      <div class="stat">
        <span class="num">{{ messagerie?.nombreInformationsNonLus ?? 0 }}</span>
        <span class="lbl">{{ t('messagerie.unreadInfos') }}</span>
      </div>
    </div>
  </section>
</template>

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;

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
      font-size: 1.3rem;
      font-weight: 700;
      color: var(--#{$prefix}primary);
    }

    .lbl {
      font-size: 0.78rem;
      color: var(--#{$prefix}basic-black-lighter);
    }
  }
}
</style>
