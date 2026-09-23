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
import '@gip-recia/ui-webcomponents/dist/r-filters.js'

const props = defineProps<{
  vieScolaire: VieScolaire | null
  index: number
}>()

interface TimelineEntry {
  date: string
  label: string
  icon: IconDefinition
  meta?: string
}

interface Category {
  key: string
  title: string
  icon: IconDefinition
  entries: TimelineEntry[]
}

interface FilterItem {
  key: string
  value: string
  checked?: boolean
}

interface FilterSection {
  id: string
  name: string
  type: 'checkbox' | 'radio'
  items: FilterItem[]
}

function formatTimeRange(start: string, end: string): string {
  const opts: Intl.DateTimeFormatOptions = { hour: '2-digit', minute: '2-digit' }
  const startTime = new Date(start).toLocaleTimeString('fr-FR', opts)
  const endTime = new Date(end).toLocaleTimeString('fr-FR', opts)
  return `${startTime} – ${endTime}`
}

const categories = computed<Category[]>(() => {
  const vs = props.vieScolaire
  if (!vs)
    return []

  const result: Category[] = []

  const absenceEntries: TimelineEntry[] = (vs.absenceList ?? []).map(absence => ({
    date: absence.dateDebut,
    label: `Absence${absence.justifie ? '' : ' - non justifiée'} : ${absence.motif || 'sans motif'}`,
    icon: faCalendarXmark,
    meta: `${formatTimeRange(absence.dateDebut, absence.dateFin)}${absence.estOuverte ? ' · en cours' : ''}`,
  }))

  if (absenceEntries.length) {
    result.push({
      key: 'absences',
      title: 'Absences',
      icon: faCalendarXmark,
      entries: absenceEntries.sort((a, b) => b.date.localeCompare(a.date)),
    })
  }

  const retardEntries: TimelineEntry[] = (vs.retardList ?? []).map(retard => ({
    date: retard.date,
    label: `Retard${retard.justifie ? '' : ' - non justifié'} : ${retard.motif}`,
    icon: faClock,
  }))

  if (retardEntries.length) {
    result.push({
      key: 'retards',
      title: 'Retards',
      icon: faClock,
      entries: retardEntries.sort((a, b) => b.date.localeCompare(a.date)),
    })
  }

  const infirmerieEntries: TimelineEntry[] = (vs.passageInfirmerieList ?? []).map(passage => ({
    date: passage.date,
    label: 'Passage infirmerie',
    icon: faSuitcaseMedical,
  }))

  if (infirmerieEntries.length) {
    result.push({
      key: 'infirmerie',
      title: 'Infirmerie',
      icon: faSuitcaseMedical,
      entries: infirmerieEntries.sort((a, b) => b.date.localeCompare(a.date)),
    })
  }

  const punitionEntries: TimelineEntry[] = (vs.punitionList ?? []).map((punition) => {
    const details = [punition.matiere, punition.circonstances].filter(Boolean).join(' · ')
    return {
      date: punition.date,
      label: `${punition.nature} - ${punition.motif}`,
      icon: faGavel,
      meta: details || undefined,
    }
  })

  if (punitionEntries.length) {
    result.push({
      key: 'punitions',
      title: 'Punitions',
      icon: faGavel,
      entries: punitionEntries.sort((a, b) => b.date.localeCompare(a.date)),
    })
  }

  const sanctionEntries: TimelineEntry[] = (vs.sanctionList ?? []).map((sanction) => {
    const details = [sanction.circonstances, sanction.duree ? `${sanction.duree} jour${sanction.duree > 1 ? 's' : ''}` : null].filter(Boolean).join(' · ')
    return {
      date: sanction.date,
      label: `${sanction.nature} - ${sanction.motif}`,
      icon: faScaleBalanced,
      meta: details || undefined,
    }
  })

  if (sanctionEntries.length) {
    result.push({
      key: 'sanction',
      title: 'Sanction',
      icon: faScaleBalanced,
      entries: sanctionEntries.sort((a, b) => b.date.localeCompare(a.date)),
    })
  }

  const observationEntries: TimelineEntry[] = (vs.observationList ?? []).map(observation => ({
    date: observation.date,
    label: `Observation (${observation.matiere}) - ${observation.observation}`,
    icon: faCommentDots,
    meta: observation.demandeur ? `Signalée par ${observation.demandeur}` : undefined,
  }))

  if (observationEntries.length) {
    result.push({
      key: 'observation',
      title: 'Observation',
      icon: faCommentDots,
      entries: observationEntries.sort((a, b) => b.date.localeCompare(a.date)),
    })
  }
  return result
})

const filterSections = computed<FilterSection[]>(() => [
  {
    id: 'categorie',
    name: 'Catégorie',
    type: 'radio',
    items: [
      { key: 'all', value: `Tout (${categories.value.reduce((sum, c) => sum + c.entries.length, 0)})` },
      ...categories.value.map(c => ({ key: c.key, value: `${c.title} (${c.entries.length})` })),
    ],
  },
])

const activeCategoryKey = ref('all')

function handleUpdateFilters(event: Event): void {
  const detail = (event as CustomEvent<{ activeFilters: { id: string, checked: string[] }[] }>).detail
  const categorieFilter = detail.activeFilters.find(f => f.id === 'categorie')
  activeCategoryKey.value = categorieFilter?.checked[0] ?? 'all'
}

const visibleCategories = computed(() =>
  activeCategoryKey.value === 'all'
    ? categories.value
    : categories.value.filter(c => c.key === activeCategoryKey.value),
)

function formatFullDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}
</script>

<template>
  <section class="vie-scolaire r-card" :aria-labelledby="`vie-scolaire-heading-${index}`">
    <h2 :id="`vie-scolaire-heading-${index}`">
      Vie scolaire
    </h2>
    <template v-if="categories.length">
      <r-filters :data.prop="filterSections" @update-filters="handleUpdateFilters" />

      <template v-for="category in visibleCategories" :key="category.key">
        <h3 class="category-title">
          <FontAwesomeIcon :icon="category.icon" aria-hidden="true" />
          {{ category.title }}
        </h3>
        <hr class="category-divider">

        <div v-for="(entry, i) in category.entries" :key="i" class="entry">
          <div class="txt">
            <span class="label">{{ entry.label }}</span>
            <span class="date">{{ formatFullDate(entry.date) }}</span>
            <span v-if="entry.meta" class="meta">{{ entry.meta }}</span>
          </div>
        </div>
      </template>
    </template>
    <p v-else>
      Aucun événement
    </p>
  </section>
</template>

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;

.vie-scolaire {
  .category-divider {
    border: none;
    border-top: 1px solid var(--#{$prefix}stroke);
    width: 80%;
    margin: 6px 0 4px;
  }

  .category-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 0.9rem;
    margin: 16px 0 0;

    &:first-of-type {
      margin-top: 12px;
    }

    svg {
      color: var(--#{$prefix}primary);
      width: 14px;
    }
  }

  .entry {
    display: flex;
    align-items: flex-start;
    padding: 6px 0;

    .txt {
      display: flex;
      flex-direction: column;

      .meta {
        font-size: 0.76rem;
        color: var(--#{$prefix}basic-black-lighter);
        font-style: italic;
      }

      .label {
        font-size: 0.88rem;
      }

      .date {
        font-size: 0.76rem;
        color: var(--#{$prefix}basic-black-lighter);
        margin-top: 2px;
      }
    }
  }
}
</style>
