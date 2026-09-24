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
import type { Eleve } from '@/types/pronote'
import { faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import Competences from '@/components/Competences.vue'
import Cours from '@/components/Cours.vue'
import Devoirs from '@/components/Devoirs.vue'
import Messagerie from '@/components/Messagerie.vue'
import VieScolaire from '@/components/VieScolaire.vue'

const props = defineProps<{
  eleve: Eleve
  index: number
}>()

const { t } = useI18n()

const competencesCount = computed(() => props.eleve.competencesDto?.nombreEvaluations ?? 0)

const vieScolaireCount = computed(() => {
  const vs = props.eleve.vieScolaireDto
  if (!vs)
    return 0
  return (vs.absenceList?.length ?? 0)
    + (vs.retardList?.length ?? 0)
    + (vs.passageInfirmerieList?.length ?? 0)
    + (vs.punitionList?.length ?? 0)
    + (vs.sanctionList?.length ?? 0)
    + (vs.observationList?.length ?? 0)
})

const travailAFaireCount = computed(() => props.eleve.travailAFaireDtoList?.length ?? 0)
const devoirsCount = computed(() => props.eleve.devoirDtoList?.length ?? 0)
</script>

<template>
  <article class="fiche-eleve">
    <a
      :href="eleve.iCal ?? undefined" class="btn-secondary small agenda-link"
      :class="{ 'agenda-link--hidden': !eleve.iCal }"
      :aria-hidden="!eleve.iCal || undefined"
    >
      <FontAwesomeIcon :icon="faPlus" aria-hidden="true" />
      {{ t('ficheEleve.addToAgenda') }}
    </a>

    <div class="stats-strip">
      <a :href="`#devoirs-${index}`" class="stat-tile r-card">
        <span class="num">{{ devoirsCount }}</span>
        <span class="lbl">{{ t('ficheEleve.stats.notesRecues') }}</span>
      </a>
      <a :href="`#vie-scolaire-${index}`" class="stat-tile r-card">
        <span class="num">{{ vieScolaireCount }}</span>
        <span class="lbl">{{ t('ficheEleve.stats.vieScolaire') }}</span>
      </a>
      <a :href="`#travail-a-faire-${index}`" class="stat-tile r-card">
        <span class="num">{{ travailAFaireCount }}</span>
        <span class="lbl">{{ t('ficheEleve.stats.travauxAFaire') }}</span>
      </a>
      <a :href="`#competences-${index}`" class="stat-tile r-card">
        <span class="num">{{ competencesCount }}</span>
        <span class="lbl">{{ t('ficheEleve.stats.evaluationsCompetences') }}</span>
      </a>
    </div>

    <div class="layout">
      <div :id="`cours-${index}`" class="main-col" tabindex="-1">
        <Cours
          :resume-de-cours-list="eleve.resumeDeCoursDtoList" :travail-a-faire-list="eleve.travailAFaireDtoList"
          :index="index"
        />
      </div>

      <div class="sidebar-col">
        <div :id="`messagerie-${index}`" tabindex="-1">
          <Messagerie :messagerie="eleve.messagerieDto" :index="index" />
        </div>
        <div :id="`devoirs-${index}`" class="r-card">
          <Devoirs :devoirs="eleve.devoirDtoList" :index="index" />
        </div>
        <div :id="`competences-${index}`" class="r-card" tabindex="-1">
          <Competences :competences="eleve.competencesDto" :index="index" />
        </div>
        <div :id="`vie-scolaire-${index}`" tabindex="-1">
          <VieScolaire :vie-scolaire="eleve.vieScolaireDto" :index="index" />
        </div>
      </div>
    </div>
  </article>
</template>

<style lang="scss" scoped>
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;

.fiche-eleve {
  .stats-strip {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 12px;
    margin-bottom: 24px;
  }

  .stat-tile {
    gap: 2px;
    text-decoration: none;
    color: inherit;
    &:focus-visible {
      outline: 4px solid var(--#{$prefix}primary);
      outline-offset: 2px;
    }

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

  .agenda-link {
    display: inline-flex;
    margin-bottom: 16px;

    &--hidden {
      visibility: hidden;
    }
  }

  .layout {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 380px;
    gap: 24px;
    align-items: start;
  }

  @media (width < map.get($grid-breakpoints, lg)) {
    .layout {
      grid-template-columns: 1fr;
    }
  }

  .sidebar-col {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}
</style>
