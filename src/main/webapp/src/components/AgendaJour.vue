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
import { formatFullWeekday, formatTimeRange } from '@/utils/dateUtils'

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

const jourAffiche = ref(today())

function jourPrecedent() {
  jourAffiche.value = addDays(jourAffiche.value, -1)
}

function jourSuivant() {
  jourAffiche.value = addDays(jourAffiche.value, 1)
}

const joursFeries = computed(() =>
  props.evenements.filter(evenement => evenement.categorie === 'JOUR_FERIE'),
)

const jourFerieDuJour = computed(() =>
  joursFeries.value.find(evenement =>
    evenement.debut.slice(0, 10) <= jourAffiche.value && jourAffiche.value < evenement.fin.slice(0, 10),
  ) ?? null,
)

const coursDuJour = computed(() =>
  props.evenements
    .filter(evenement => evenement.categorie === 'COURS')
    .filter(evenement => evenement.debut.slice(0, 10) === jourAffiche.value)
    .sort((a, b) => a.debut.localeCompare(b.debut)),
)
</script>

<template>
  <section v-if="icalUrl" class="agenda-jour" :aria-labelledby="`agenda-heading-${index}`">
    <h2 :id="`agenda-heading-${index}`">
      {{ t('agendaJour.heading') }}
    </h2>

    <button
      type="button"
      class="btn-secondary small agenda-link"
      @click="copyAgendaLink(icalUrl)"
    >
      <FontAwesomeIcon :icon="agendaLinkCopied ? faCheck : faCopy" aria-hidden="true" />
      {{ agendaLinkCopied ? t('agendaJour.linkCopied') : t('agendaJour.copyLink') }}
    </button>
    <span class="sr-only" role="status" aria-live="polite">
      {{ agendaLinkCopied ? t('agendaJour.linkCopied') : '' }}
    </span>

    <template v-if="evenements.length">
      <div class="navigation">
        <button type="button" class="btn-secondary small circle" @click="jourPrecedent">
          <FontAwesomeIcon :icon="faChevronLeft" aria-hidden="true" />
          <span class="sr-only">{{ t('agendaJour.jourPrecedent') }}</span>
        </button>
        <span class="jour-courant" role="status" aria-live="polite">
          {{ formatFullWeekday(jourAffiche, locale) }}
        </span>
        <button type="button" class="btn-secondary small circle" @click="jourSuivant">
          <FontAwesomeIcon :icon="faChevronRight" aria-hidden="true" />
          <span class="sr-only">{{ t('agendaJour.jourSuivant') }}</span>
        </button>
      </div>

      <p v-if="jourFerieDuJour" class="jour-ferie">
        {{ t('agendaJour.jourFerie') }}
      </p>
      <template v-else-if="coursDuJour.length">
        <template v-for="(evenement, i) in coursDuJour" :key="i">
          <hr v-if="i > 0">
          <div class="evenement">
            <span class="matiere">{{ evenement.matiere }}</span>
            <span class="horaire">{{ formatTimeRange(evenement.debut, evenement.fin, locale) }}</span>
            <span v-if="evenement.salle" class="salle">{{ evenement.salle }}</span>
          </div>
        </template>
      </template>
      <p v-else class="empty">
        {{ t('agendaJour.empty') }}
      </p>
    </template>
    <p v-else class="empty">
      {{ t('agendaJour.noApercu') }}
    </p>
  </section>
</template>

<style lang="scss" scoped>
@use '@gip-recia/ui/core/variables' as *;

.agenda-jour {
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

    .jour-courant {
      font-weight: 600;
      text-align: center;
      flex: 1;
      text-transform: capitalize;
    }
  }

  .jour-ferie,
  .empty {
    color: var(--#{$prefix}basic-black-lighter);
    font-style: italic;
  }

  .evenement {
    display: flex;
    flex-direction: column;
    gap: 2px;
    padding: 8px 0;

    .matiere {
      font-weight: 600;
    }

    .horaire,
    .salle {
      font-size: var(--#{$prefix}font-size-xs);
      color: var(--#{$prefix}basic-black-lighter);
    }
  }

  hr {
    border: none;
    border-top: 1px solid var(--#{$prefix}stroke);
    margin: 0;
  }
}
</style>
