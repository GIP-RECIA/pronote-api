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
import type { Devoir } from '@/types/pronote'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { formatFullDate } from '@/utils/dateUtils'

const props = defineProps<{
  devoirs: Devoir[] | null
  index: number
}>()

const { t, locale } = useI18n()

const sortedDevoirs = computed(() =>
  [...(props.devoirs ?? [])].sort((a, b) => a.date.localeCompare(b.date)),
)
</script>

<template>
  <section class="devoirs" :aria-labelledby="`releve-notes-heading-${index}`">
    <h2 :id="`releve-notes-heading-${index}`">
      {{ t('devoirs.heading') }}
    </h2>

    <template v-if="sortedDevoirs.length">
      <template v-for="(devoir, i) in sortedDevoirs" :key="i">
        <hr v-if="i > 0">
        <div class="devoir">
          <span class="matiere">{{ devoir.matiere }}</span>
          <span class="note">{{ devoir.note }}/{{ devoir.bareme }}</span>
          <span class="date">{{ formatFullDate(devoir.date, locale) }}</span>
        </div>
      </template>
    </template>
    <p v-else>
      {{ t('devoirs.empty') }}
    </p>
  </section>
</template>
