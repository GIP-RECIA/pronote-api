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
import type { IconDefinition } from '@fortawesome/fontawesome-svg-core'
import type { VieScolaire } from '@/types/pronote'
import {
  faCalendarXmark,
  faClock,
  faCommentDots,
  faGavel,
  faScaleBalanced,
  faSuitcaseMedical,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'

const props = defineProps<{
  vieScolaire: VieScolaire | null
}>()

interface TimelineEntry {
  date: string
  label: string
  icon: IconDefinition
}

const entries = computed<TimelineEntry[]>(() => {
  const vs = props.vieScolaire
  if (!vs)
    return []

  const result: TimelineEntry[] = []

  for (const absence of vs.absenceList ?? []) {
    result.push({
      date: absence.dateDebut,
      label: `Absence${absence.justifie ? '' : ' — non justifiée'} : ${absence.motif || 'sans motif'}`,
      icon: faCalendarXmark,
    })
  }

  for (const retard of vs.retardList ?? []) {
    result.push({ date: retard.date, label: `Retard — ${retard.motif}`, icon: faClock })
  }

  for (const passage of vs.passageInfirmerieList ?? []) {
    result.push({ date: passage.date, label: 'Passage infirmerie', icon: faSuitcaseMedical })
  }

  for (const punition of vs.punitionList ?? []) {
    result.push({ date: punition.date, label: `Punition — ${punition.motif}`, icon: faGavel })
  }

  for (const sanction of vs.sanctionList ?? []) {
    result.push({ date: sanction.date, label: `Sanction — ${sanction.motif}`, icon: faScaleBalanced })
  }

  for (const observation of vs.observationList ?? []) {
    result.push({ date: observation.date, label: `Observation (${observation.matiere}) — ${observation.observation}`, icon: faCommentDots })
  }

  return result.sort((a, b) => b.date.localeCompare(a.date))
})

function formatFullDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}
</script>

<template>
  <section class="vie-scolaire r-card">
    <h2>Vie scolaire</h2>
    <template v-if="entries.length">
      <template v-for="(entry, i) in entries" :key="i">
        <hr v-if="i > 0">
        <div class="entry">
          <FontAwesomeIcon :icon="entry.icon" class="icon" />
          <div class="txt">
            <span class="label">{{ entry.label }}</span>
            <span class="date">{{ formatFullDate(entry.date) }}</span>
          </div>
        </div>
      </template>
    </template>
    <p v-else>
      Aucun événement
    </p>
  </section>
</template>
