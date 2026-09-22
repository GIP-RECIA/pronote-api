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
import type { Competences } from '@/types/pronote'

defineProps<{
  competences: Competences | null
}>()
</script>

<template>
  <section class="competences">
    <h2>Compétences</h2>

    <template v-if="competences?.evaluationDtoList?.length">
      <template v-for="(evaluation, i) in competences.evaluationDtoList" :key="i">
        <hr v-if="i > 0">
        <div class="evaluation">
          <span class="code">{{ evaluation.competence }}</span>
          <span class="matiere">{{ evaluation.matiere }}</span>
          <span class="niveau">{{ evaluation.niveauDAcquisition ?? 'Non renseigné' }}</span>
          <span class="intitule">{{ evaluation.intitule }}</span>
        </div>
      </template>
    </template>
    <p v-else>
      Aucune évaluation de compétence lors des 7 derniers jours.
    </p>
  </section>
</template>
