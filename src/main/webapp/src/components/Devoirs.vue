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

const props = defineProps<{
  devoirs: Devoir[] | null
}>()

const sortedDevoirs = computed(() =>
  [...(props.devoirs ?? [])].sort((a, b) => a.date.localeCompare(b.date)),
)

function formatFullDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}
</script>

<template>
  <section class="devoirs">
    <h2>Relevé de notes</h2>

    <template v-if="sortedDevoirs.length">
      <template v-for="(devoir, i) in sortedDevoirs" :key="i">
        <hr v-if="i > 0">
        <div class="devoir">
          <span class="matiere">{{ devoir.matiere }}</span>
          <span class="note">{{ devoir.note }}/{{ devoir.bareme }}</span>
          <span class="date">{{ formatFullDate(devoir.date) }}</span>
        </div>
      </template>
    </template>
    <p v-else>
      Aucun devoir n'a été noté lors des 7 derniers jours.
    </p>
  </section>
</template>
