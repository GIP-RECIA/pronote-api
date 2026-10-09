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
  /** Quel champ de complément afficher en badge sur chaque cours - le prof pour un élève/parent, la classe pour un professeur. */
  badge: 'professeur' | 'classe'
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

// Plage réellement couverte par le flux iCal, déduite des dates présentes dans les
// événements eux-mêmes (le backend ne renvoie pas de borne explicite). Un jour hors de
// cette plage est "inconnu" (pas d'information), pas "vérifié sans cours".
const plageConnue = computed(() => {
  const dates = props.evenements.flatMap(evenement => [
    evenement.debut.slice(0, 10),
    evenement.fin.slice(0, 10),
  ])
  if (dates.length === 0) {
    return null
  }
  return { min: dates.reduce((a, b) => (a < b ? a : b)), max: dates.reduce((a, b) => (a > b ? a : b)) }
})

function estWeekend(date: string): boolean {
  const parts = date.split('-').map(Number)
  const jsDay = new Date(Date.UTC(parts[0]!, parts[1]! - 1, parts[2]!)).getUTCDay()
  return jsDay === 0 || jsDay === 6
}

function estDansLaPlageConnue(date: string): boolean {
  const plage = plageConnue.value
  return plage !== null && plage.min <= date && date <= plage.max
}

const jours = computed(() => {
  return [0, 1, 2, 3, 4, 5, 6].map((offset) => {
    const date = addDays(semaineAffichee.value, offset)
    const jourFerie = joursFeries.value.find(evenement =>
      evenement.debut.slice(0, 10) <= date && date < evenement.fin.slice(0, 10),
    ) ?? null
    const cours = props.evenements
      .filter(evenement => evenement.categorie === 'COURS')
      .filter(evenement => evenement.debut.slice(0, 10) === date)
      .sort((a, b) => a.debut.localeCompare(b.debut))
    return {
      date,
      jourFerie,
      cours,
      weekend: estWeekend(date),
      dansLaPlageConnue: estDansLaPlageConnue(date),
    }
  })
})

const libelleSemaine = computed(() => {
  const dernierJour = addDays(semaineAffichee.value, 6)
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

      <!-- Vue tableau - desktop/tablette -->
      <div class="table-wrapper vue-tableau">
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
              <td v-for="jour in jours" :key="jour.date" :class="{ weekend: jour.weekend && !jour.cours.length }">
                <p v-if="jour.jourFerie" class="jour-ferie">
                  {{ t('agendaSemaine.jourFerie') }}
                </p>
                <ul v-else-if="jour.cours.length" class="evenements">
                  <li v-for="(evenement, i) in jour.cours" :key="i" class="evenement">
                    <div class="ligne-principale">
                      <span class="matiere">{{ evenement.matiere }}</span>
                      <span v-if="evenement[badge]" class="tag small">{{ evenement[badge] }}</span>
                    </div>
                    <span class="horaire">{{ formatTimeRange(evenement.debut, evenement.fin, locale) }}</span>
                    <span v-if="evenement.salle" class="salle">{{ evenement.salle }}</span>
                  </li>
                </ul>
                <p v-else-if="jour.weekend" class="weekend-label">
                  {{ t('agendaSemaine.weekend') }}
                </p>
                <p v-else-if="jour.dansLaPlageConnue" class="aucun-cours">
                  {{ t('agendaSemaine.aucunCours') }}
                </p>
                <p v-else class="empty">
                  {{ t('agendaSemaine.empty') }}
                </p>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Vue jour par jour - mobile -->
      <div class="vue-jours">
        <div v-for="jour in jours" :key="jour.date" class="jour-carte" :class="{ weekend: jour.weekend && !jour.cours.length }">
          <h3>{{ formatFullWeekday(jour.date, locale) }}</h3>
          <p v-if="jour.jourFerie" class="jour-ferie">
            {{ t('agendaSemaine.jourFerie') }}
          </p>
          <ul v-else-if="jour.cours.length" class="evenements">
            <li v-for="(evenement, i) in jour.cours" :key="i" class="evenement">
              <div class="ligne-principale">
                <span class="matiere">{{ evenement.matiere }}</span>
                <span v-if="evenement[badge]" class="tag small">{{ evenement[badge] }}</span>
              </div>
              <span class="horaire">{{ formatTimeRange(evenement.debut, evenement.fin, locale) }}</span>
              <span v-if="evenement.salle" class="salle">{{ evenement.salle }}</span>
            </li>
          </ul>
          <p v-else-if="jour.weekend" class="weekend-label">
            {{ t('agendaSemaine.weekend') }}
          </p>
          <p v-else-if="jour.dansLaPlageConnue" class="aucun-cours">
            {{ t('agendaSemaine.aucunCours') }}
          </p>
          <p v-else class="empty">
            {{ t('agendaSemaine.empty') }}
          </p>
        </div>
      </div>
    </template>
    <p v-else class="empty">
      {{ t('agendaSemaine.noApercu') }}
    </p>
  </section>
</template>

<style lang="scss" scoped>
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;

.agenda-semaine {
  // Vue tableau par défaut (desktop/tablette), bascule vers la vue jour par jour empilée
  // en dessous du seuil "md" - un tableau à 7 colonnes devient illisible sur mobile.
  .vue-jours {
    display: none;
  }

  @media (width < map.get($grid-breakpoints, md)) {
    .vue-tableau {
      display: none;
    }

    .vue-jours {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
  }

  .jour-carte {
    padding: 8px 0;
    border-bottom: 1px solid var(--#{$prefix}stroke);

    &:last-child {
      border-bottom: none;
    }

    &.weekend {
      background-color: var(--#{$prefix}basic-grey);
      border-radius: 6px;
      padding: 8px;
    }

    h3 {
      font-size: var(--#{$prefix}font-size-xs);
      font-weight: 600;
      text-transform: capitalize;
      margin: 0 0 6px;
    }
  }

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

  .table-wrapper {
    overflow-x: auto;
  }

  table {
    width: 100%;
    min-width: 700px;
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

    &.weekend {
      background-color: var(--#{$prefix}basic-grey);
    }
  }

  .jour-ferie,
  .aucun-cours,
  .empty {
    color: var(--#{$prefix}basic-black-lighter);
    font-style: italic;
    font-size: var(--#{$prefix}font-size-xs);
  }

  // Sur fond gris (.weekend) : basic-black-lighter tombe à 4.31:1, sous le seuil AA (4.5:1)
  // pour du texte de cette taille. basic-black donne 15:1, largement suffisant.
  .weekend-label {
    color: var(--#{$prefix}basic-black);
    font-style: italic;
    font-size: var(--#{$prefix}font-size-xs);
  }

  .evenements {
    list-style: none;
    margin: 0;
    padding: 0;
  }

  .evenement {
    display: flex;
    flex-direction: column;
    gap: 2px;
    padding-bottom: 8px;

    .ligne-principale {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 4px;
    }

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
