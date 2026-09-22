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
import { computed } from 'vue'
import Competences from '@/components/Competences.vue'
import Cours from '@/components/Cours.vue'
import Devoirs from '@/components/Devoirs.vue'
import Messagerie from '@/components/Messagerie.vue'
import VieScolaire from '@/components/VieScolaire.vue'

const props = defineProps<{
  eleve: Eleve
}>()

const messagerieCount = computed(() => {
  const m = props.eleve.messagerieDto
  if (!m)
    return 0
  return m.nombreMessagesNonLus + m.nombreInformationsNonLus
})

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
</script>

<template>
  <article class="fiche-eleve">
    <a v-if="eleve.iCal" :href="eleve.iCal" class="btn-secondary small agenda-link">
      Ajouter à mon agenda
    </a>

    <div class="stats-strip">
      <a href="#vie-scolaire" class="stat-tile r-card">
        <span class="num">{{ vieScolaireCount }}</span>
        <span class="lbl">événements de vie scolaire</span>
      </a>
      <a href="#cours" class="stat-tile r-card">
        <span class="num">{{ travailAFaireCount }}</span>
        <span class="lbl">travaux à faire</span>
      </a>
      <a href="#messagerie" class="stat-tile r-card">
        <span class="num">{{ messagerieCount }}</span>
        <span class="lbl">messages / infos non lus</span>
      </a>
      <a href="#competences" class="stat-tile r-card">
        <span class="num">{{ competencesCount }}</span>
        <span class="lbl">évaluations de compétences</span>
      </a>
    </div>

    <div class="layout">
      <div id="cours" class="main-col">
        <Cours :resume-de-cours-list="eleve.resumeDeCoursDtoList" :travail-a-faire-list="eleve.travailAFaireDtoList" />
      </div>

      <div class="sidebar-col">
        <div id="devoirs" class="r-card">
          <Devoirs :devoirs="eleve.devoirDtoList" />
        </div>
        <div id="competences" class="r-card">
          <Competences :competences="eleve.competencesDto" />
        </div>
        <div id="vie-scolaire">
          <VieScolaire :vie-scolaire="eleve.vieScolaireDto" />
        </div>
        <div id="messagerie">
          <Messagerie :messagerie="eleve.messagerieDto" />
        </div>
      </div>
    </div>
  </article>
</template>
