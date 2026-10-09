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
import type { EvenementAgenda } from '@/types/pronote'
import { faCheck, faChevronLeft, faChevronRight, faCopy } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { formatFullDate, formatFullWeekday, formatTimeRange } from '@/utils/dateUtils'

const props = defineProps<{
  evenements: EvenementAgenda[]
  icalUrl: string | null
  index: number
}>()

const { t, locale } = useI18n()

const agendaLinkCopied = ref(false)

async function copyAgendaLink(url: string) {
  try {
    await navigator.clipboard.writeText(url)
    agendaLinkCopied.value = true
    setTimeout(() => {
      agendaLinkCopied.value = false
    }, 2000)
  }
  catch (error) {
    console.error('Unable to copy agenda link', error)
  }
}

function today(): string {
  return new Date().toISOString().slice(0, 10)
}

function addDays(dateKey: string, delta: number): string {
  const parts = dateKey.split('-').map(Number)
  return new Date(Date.UTC(parts[0]!, parts[1]! - 1, parts[2]! + delta)).toISOString().slice(0, 10)
}

function mondayOf(dateKey: string): string {
  const parts = dateKey.split('-').map(Number)
  const jsDay = new Date(Date.UTC(parts[0]!, parts[1]! - 1, parts[2]!)).getUTCDay()
  const diffToMonday = jsDay === 0 ? -6 : 1 - jsDay
  return addDays(dateKey, diffToMonday)
}

const semaineAffichee = ref(mondayOf(today()))

function semainePrecedente() {
  semaineAffichee.value = addDays(semaineAffichee.value, -7)
}

function semaineSuivante() {
  semaineAffichee.value = addDays(semaineAffichee.value, 7)
}

const joursFeries = computed(() =>
  props.evenements.filter(evenement => evenement.categorie === 'JOUR_FERIE'),
)

const jours = computed(() => {
  return [0, 1, 2, 3, 4, 5].map((offset) => {
    const date = addDays(semaineAffichee.value, offset)
    const jourFerie = joursFeries.value.find(evenement =>
      evenement.debut.slice(0, 10) <= date && date < evenement.fin.slice(0, 10),
    ) ?? null
    const cours = props.evenements
      .filter(evenement => evenement.categorie === 'COURS')
      .filter(evenement => evenement.debut.slice(0, 10) === date)
      .sort((a, b) => a.debut.localeCompare(b.debut))
    return { date, jourFerie, cours }
  })
})

const libelleSemaine = computed(() => {
  const dernierJour = addDays(semaineAffichee.value, 5)
  return t('agendaSemaine.plage', {
    debut: formatFullDate(semaineAffichee.value, locale.value),
    fin: formatFullDate(dernierJour, locale.value),
  })
})
</script>

<template>
  <section v-if="icalUrl" class="agenda-semaine" :aria-labelledby="`agenda-heading-${index}`">
    <h2 :id="`agenda-heading-${index}`">
      {{ t('agendaSemaine.heading') }}
    </h2>

    <button
      type="button"
      class="btn-secondary small agenda-link"
      @click="copyAgendaLink(icalUrl)"
    >
      <FontAwesomeIcon :icon="agendaLinkCopied ? faCheck : faCopy" aria-hidden="true" />
      {{ agendaLinkCopied ? t('agendaSemaine.linkCopied') : t('agendaSemaine.copyLink') }}
    </button>
    <span class="sr-only" role="status" aria-live="polite">
      {{ agendaLinkCopied ? t('agendaSemaine.linkCopied') : '' }}
    </span>

    <template v-if="evenements.length">
      <div class="navigation">
        <button type="button" class="btn-secondary small circle" @click="semainePrecedente">
          <FontAwesomeIcon :icon="faChevronLeft" aria-hidden="true" />
          <span class="sr-only">{{ t('agendaSemaine.semainePrecedente') }}</span>
        </button>
        <span class="semaine-courante" role="status" aria-live="polite">
          {{ libelleSemaine }}
        </span>
        <button type="button" class="btn-secondary small circle" @click="semaineSuivante">
          <FontAwesomeIcon :icon="faChevronRight" aria-hidden="true" />
          <span class="sr-only">{{ t('agendaSemaine.semaineSuivante') }}</span>
        </button>
      </div>

      <table>
        <caption class="sr-only">
          {{ libelleSemaine }}
        </caption>
        <thead>
          <tr>
            <th v-for="jour in jours" :key="jour.date" scope="col">
              {{ formatFullWeekday(jour.date, locale) }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td v-for="jour in jours" :key="jour.date">
              <p v-if="jour.jourFerie" class="jour-ferie">
                {{ t('agendaSemaine.jourFerie') }}
              </p>
              <template v-else-if="jour.cours.length">
                <div v-for="(evenement, i) in jour.cours" :key="i" class="evenement">
                  <span class="matiere">{{ evenement.matiere }}</span>
                  <span class="horaire">{{ formatTimeRange(evenement.debut, evenement.fin, locale) }}</span>
                  <span v-if="evenement.salle" class="salle">{{ evenement.salle }}</span>
                </div>
              </template>
              <p v-else class="empty">
                {{ t('agendaSemaine.empty') }}
              </p>
            </td>
          </tr>
        </tbody>
      </table>
    </template>
    <p v-else class="empty">
      {{ t('agendaSemaine.noApercu') }}
    </p>
  </section>
</template>

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;

.agenda-semaine {
  .agenda-link {
    display: inline-flex;
    margin-bottom: 12px;
  }

  .navigation {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 12px;

    .semaine-courante {
      font-weight: 600;
      text-align: center;
      flex: 1;
    }
  }

  table {
    width: 100%;
    border-collapse: collapse;
    table-layout: fixed;
  }

  th {
    text-align: left;
    font-size: var(--#{$prefix}font-size-xs);
    font-weight: 600;
    text-transform: capitalize;
    padding: 4px 8px;
    border-bottom: 1px solid var(--#{$prefix}stroke);
  }

  td {
    vertical-align: top;
    padding: 8px;
    border-right: 1px solid var(--#{$prefix}stroke);

    &:last-child {
      border-right: none;
    }
  }

  .jour-ferie,
  .empty {
    color: var(--#{$prefix}basic-black-lighter);
    font-style: italic;
    font-size: var(--#{$prefix}font-size-xs);
  }

  .evenement {
    display: flex;
    flex-direction: column;
    gap: 2px;
    padding-bottom: 8px;

    .matiere {
      font-weight: 600;
      font-size: var(--#{$prefix}font-size-xs);
    }

    .horaire,
    .salle {
      font-size: var(--#{$prefix}font-size-xs);
      color: var(--#{$prefix}basic-black-lighter);
    }
  }
}
</style>
