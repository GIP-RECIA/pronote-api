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
import type { ResumeDeCours, TravailAFaire } from '@/types/pronote'
import { faLink, faPaperclip } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'

const props = defineProps<{
  resumeDeCoursList: ResumeDeCours[] | null
  travailAFaireList: TravailAFaire[] | null
}>()

function normalizeUrl(url: string): string {
  return /^https?:\/\//.test(url) ? url : `https://${url}`
}

const sortedResumes = computed(() => [...(props.resumeDeCoursList ?? [])].sort((a, b) => b.date.localeCompare(a.date)))

function formatFullWeekday(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' })
}

function formatCategorie(categorie: string | null): string {
  const mapping: Record<string, string> = {
    'Cours important': 'Cours',
  }
  return categorie ? (mapping[categorie] ?? categorie) : ''
}

function formatFullDate(date: string): string {
  return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}

interface DayGroup {
  date: string
  resumes: ResumeDeCours[]
}

const groupedByDay = computed<DayGroup[]>(() => {
  const groups: DayGroup[] = []
  for (const resume of sortedResumes.value) {
    const last = groups[groups.length - 1]
    if (last && last.date === resume.date) {
      last.resumes.push(resume)
    }
    else {
      groups.push({ date: resume.date, resumes: [resume] })
    }
  }
  return groups
})
</script>

<template>
  <section class="cours r-card" aria-labelledby="cours-recents-heading">
    <h2 id="cours-recents-heading">
      Cours récents
    </h2>
    <template v-if="groupedByDay.length">
      <template v-for="(group, i) in groupedByDay" :key="group.date">
        <hr v-if="i > 0">
        <div class="day-group">
          <h3 class="day-heading">
            {{ formatFullWeekday(group.date) }}
          </h3>
          <div class="day-content">
            <div v-for="(resume, j) in group.resumes" :key="`${resume.id}-${j}`" class="matiere-block">
              <h4 class="matiere">
                {{ resume.matiere }}
              </h4>
              <div v-if="resume.contenuDeCoursList" class="contenus">
                <div v-for="(contenu, k) in resume.contenuDeCoursList" :key="k" class="contenu">
                  <div class="contenu-main">
                    <span class="categorie">{{ formatCategorie(contenu.categorie) }}</span> - {{ contenu.titre }}
                  </div>
                  <p v-if="contenu.descriptif" class="descriptif">
                    {{ contenu.descriptif }}
                  </p>
                  <ul
                    v-if="(contenu.pieceJointeList?.length ?? 0) + (contenu.siteInternetList?.length ?? 0) > 0"
                    class="attachments"
                  >
                    <li v-for="(piece, p) in contenu.pieceJointeList ?? []" :key="`pj-${p}`">
                      <FontAwesomeIcon :icon="faPaperclip" class="attach-icon" aria-hidden="true" />
                      <a :href="normalizeUrl(piece)" target="_blank" rel="noopener noreferrer">
                        Pièce jointe {{ p + 1 }}
                        <span class="sr-only"> (ouvre dans un nouvel onglet)</span>
                      </a>
                    </li>
                    <li v-for="(site, s) in contenu.siteInternetList ?? []" :key="`site-${s}`">
                      <FontAwesomeIcon :icon="faLink" class="attach-icon" aria-hidden="true" />
                      <a :href="normalizeUrl(site)" target="_blank" rel="noopener noreferrer">
                        {{ site }}
                        <span class="sr-only"> (ouvre dans un nouvel onglet)</span>
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </template>
    <p v-else>
      Aucun cours récent
    </p>
  </section>
  <section class="travail-a-faire r-card" aria-labelledby="travail-a-faire-heading">
    <h2 id="travail-a-faire-heading">
      Travail à faire
    </h2>
    <template v-if="travailAFaireList && travailAFaireList.length">
      <template v-for="(taf, i) in travailAFaireList" :key="i">
        <hr v-if="i > 0">
        <div class="taf-item">
          <h3 class="matiere">
            {{ taf.matiere }}
          </h3>
          <span class="desc">{{ taf.descriptif }}</span>
          <span class="due">À rendre le {{ formatFullDate(taf.pourLe) }}</span>
          <ul v-if="(taf.pieceJointeList?.length ?? 0) + (taf.siteInternetList?.length ?? 0) > 0" class="attachments">
            <li v-for="(piece, p) in taf.pieceJointeList ?? []" :key="`pj-${p}`">
              <FontAwesomeIcon :icon="faPaperclip" class="attach-icon" aria-hidden="true" />
              <a :href="normalizeUrl(piece)" target="_blank" rel="noopener noreferrer">
                Pièce jointe {{ p + 1 }}
                <span class="sr-only"> (ouvre dans un nouvel onglet)</span>
              </a>
            </li>
            <li v-for="(site, s) in taf.siteInternetList ?? []" :key="`site-${s}`">
              <FontAwesomeIcon :icon="faLink" class="attach-icon" aria-hidden="true" />
              <a :href="normalizeUrl(site)" target="_blank" rel="noopener noreferrer">
                {{ site }}
                <span class="sr-only"> (ouvre dans un nouvel onglet)</span>
              </a>
            </li>
          </ul>
        </div>
      </template>
    </template>
    <p v-else>
      Aucun travail à faire
    </p>
  </section>
</template>
