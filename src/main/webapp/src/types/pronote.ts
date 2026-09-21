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

export interface DevoirDto {
  note: string
  bareme: string
  matiere: string
  date: string
}

export interface ContenuDeCoursDto {
  titre: string | null
  categorie: string | null
  descriptif: string | null
  pieceJointeList: string[] | null
  siteInternet: string[] | null
}

export interface ResumeDeCoursDto {
  id: string
  matiere: string
  date: string
  contenuDeCoursList: ContenuDeCoursDto[] | null
}

export interface TravailAFaireDto {
  coursId: string
  matiere: string
  descriptif: string | null
  pourLe: string
  pieceJointeList: string[] | null
  siteInternetList: string[] | null
}

export interface AbsenceDto {
  dateDebut: string
  dateFin: string
  estOuverte: boolean
  justifie: boolean
  motif: string
}

export interface RetardDto {
  date: string
  justifie: boolean
  motif: string
}

export interface PassageInfirmerieDto {
  date: string
}

export interface PunitionDto {
  date: string
  nature: string
  matiere: string | null
  motif: string
  circonstances: string | null
}

export interface SanctionDto {
  date: string
  nature: string
  motif: string
  circonstances: string | null
  duree: number | null
}

export interface ObservationDto {
  date: string
  demandeur: string
  matiere: string
  observation: string
}

export interface VieScolaireDto {
  absenceList: AbsenceDto[] | null
  retardList: RetardDto[] | null
  passageInfirmerieList: PassageInfirmerieDto[] | null
  punitionList: PunitionDto[] | null
  sanctionList: SanctionDto[] | null
  observationList: ObservationDto[] | null
}

export interface MessagerieDto {
  nombreMessagesNonLus: number
  nombreInformationsNonLus: number
}

export interface CompetencesDto {
  nombreEvaluations: number
}

export interface EleveDto {
  prenom: string | null
  nom: string | null
  resumeDeCoursDtoList: ResumeDeCoursDto[] | null
  travailAFaireDtoList: TravailAFaireDto[] | null
  vieScolaireDto: VieScolaireDto | null
  devoirDtoList: DevoirDto[] | null
  messagerieDto: MessagerieDto | null
  competencesDto: CompetencesDto | null
  etablissement: string | null
  iCal: string | null
}

export interface PronotePageResponse {
  profil: 'Eleve' | 'Parent'
  eleveDtoList: EleveDto[]
}
