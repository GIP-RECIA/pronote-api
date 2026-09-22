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
import { computed, ref } from 'vue'

const props = defineProps<{
  vieScolaire: VieScolaire | null
}>()

interface TimelineEntry {
  date: string
  label: string
  icon: IconDefinition
  meta?: string
}

function formatTimeRange(start: string, end: string): string {
  const opts: Intl.DateTimeFormatOptions = { hour: '2-digit', minute: '2-digit' }
  const startTime = new Date(start).toLocaleTimeString('fr-FR', opts)
  const endTime = new Date(end).toLocaleTimeString('fr-FR', opts)
  return `${startTime} – ${endTime}`
}

const entries = computed<TimelineEntry[]>(() => {
  const vs = props.vieScolaire
  if (!vs)
    return []

  const result: TimelineEntry[] = []

  for (const absence of vs.absenceList ?? []) {
    result.push({
      date: absence.dateDebut,
      label: `Absence${absence.justifie ? '' : ' - non justifiée'} : ${absence.motif || 'sans motif'}`,
      icon: faCalendarXmark,
      meta: `${formatTimeRange(absence.dateDebut, absence.dateFin)}${absence.estOuverte ? ' · en cours' : ''}`,
    })
  }

  for (const retard of vs.retardList ?? []) {
    result.push({
      date: retard.date,
      label: `Retard${retard.justifie ? '' : ' - non justifié'} : ${retard.motif}`,
      icon: faClock,
    })
  }

  for (const passage of vs.passageInfirmerieList ?? []) {
    result.push({ date: passage.date, label: 'Passage infirmerie', icon: faSuitcaseMedical })
  }

  for (const punition of vs.punitionList ?? []) {
    const details = [punition.matiere, punition.circonstances].filter(Boolean).join(' · ')
    result.push({
      date: punition.date,
      label: `${punition.nature} - ${punition.motif}`,
      icon: faGavel,
      meta: details || undefined,
    })
  }

  for (const sanction of vs.sanctionList ?? []) {
    const details = [
      sanction.circonstances,
      sanction.duree ? `${sanction.duree} jour${sanction.duree > 1 ? 's' : ''}` : null,
    ].filter(Boolean).join(' · ')
    result.push({
      date: sanction.date,
      label: `${sanction.nature} - ${sanction.motif}`,
      icon: faScaleBalanced,
      meta: details || undefined,
    })
  }

  for (const observation of vs.observationList ?? []) {
    result.push({
      date: observation.date,
      label: `Observation (${observation.matiere}) - ${observation.observation}`,
      icon: faCommentDots,
      meta: observation.demandeur ? `Signalée par ${observation.demandeur}` : undefined,
    })
  }

  return result.sort((a, b) => b.date.localeCompare(a.date))
})

const VISIBLE_COUNT = 5
const expanded = ref(false)

const visibleEntries = computed(() =>
  expanded.value ? entries.value : entries.value.slice(0, VISIBLE_COUNT),
)

const hiddenCount = computed(() => entries.value.length - VISIBLE_COUNT)

function formatFullDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}
</script>

<template>
  <section class="vie-scolaire r-card">
    <h2>Vie scolaire</h2>
    <template v-if="entries.length">
      <template v-for="(entry, i) in visibleEntries" :key="i">
        <hr v-if="i > 0">
        <div class="entry">
          <FontAwesomeIcon :icon="entry.icon" class="icon" aria-hidden="true" />
          <div class="txt">
            <span class="label">{{ entry.label }}</span>
            <span class="date">{{ formatFullDate(entry.date) }}</span>
            <span v-if="entry.meta" class="meta">{{ entry.meta }}</span>
          </div>
        </div>
      </template>
      <button v-if="!expanded && hiddenCount > 0" type="button" class="show-more" @click="expanded = true">
        Afficher {{ hiddenCount }} événement{{ hiddenCount > 1 ? 's' : '' }} de plus
      </button>
    </template>
    <p v-else>
      Aucun événement
    </p>
  </section>
</template>
