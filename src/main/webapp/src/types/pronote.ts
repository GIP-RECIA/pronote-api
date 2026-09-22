/**
 * Copyright © 2026 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export interface Devoir {
  note: string
  bareme: string
  matiere: string
  date: string
}

export interface ContenuDeCours {
  titre: string | null
  categorie: string | null
  descriptif: string | null
  pieceJointeList: string[] | null
  siteInternetList: string[] | null
}

export interface ResumeDeCours {
  id: string
  matiere: string
  date: string
  categorie: string
  contenuDeCoursList: ContenuDeCours[] | null
}

export interface TravailAFaire {
  coursId: string
  matiere: string
  descriptif: string | null
  pourLe: string
  pieceJointeList: string[] | null
  siteInternetList: string[] | null
}

export interface Absence {
  dateDebut: string
  dateFin: string
  estOuverte: boolean
  justifie: boolean
  motif: string
}

export interface Retard {
  date: string
  justifie: boolean
  motif: string
}

export interface PassageInfirmerie {
  date: string
}

export interface Punition {
  date: string
  nature: string
  matiere: string | null
  motif: string
  circonstances: string | null
}

export interface Sanction {
  date: string
  nature: string
  motif: string
  circonstances: string | null
  duree: number | null
}

export interface Observation {
  date: string
  demandeur: string
  matiere: string
  observation: string
}

export interface VieScolaire {
  absenceList: Absence[] | null
  retardList: Retard[] | null
  passageInfirmerieList: PassageInfirmerie[] | null
  punitionList: Punition[] | null
  sanctionList: Sanction[] | null
  observationList: Observation[] | null
}

export interface Messagerie {
  nombreMessagesNonLus: number
  nombreInformationsNonLus: number
}

export interface Evaluation {
  competence: string
  matiere: string
  intitule: string
  niveauDAcquisition: string | null
  date: string
}

export interface Competences {
  nombreEvaluations: number
  evaluationDtoList: Evaluation[] | null
}

export interface Eleve {
  prenom: string | null
  nom: string | null
  resumeDeCoursDtoList: ResumeDeCours[] | null
  travailAFaireDtoList: TravailAFaire[] | null
  vieScolaireDto: VieScolaire | null
  devoirDtoList: Devoir[] | null
  messagerieDto: Messagerie | null
  competencesDto: Competences | null
  etablissement: string | null
  iCal: string | null
}

export interface PronotePageResponse {
  profil: 'Eleve' | 'Parent'
  eleveDtoList: Eleve[]
}
