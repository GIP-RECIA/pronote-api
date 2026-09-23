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

import type { PronotePageResponse } from '@/types/pronote'

const PRONOTE_PAGE_URL = './public/mocks/pronote-page.json'

export async function fetchPronotePage(): Promise<PronotePageResponse> {
  const response = await fetch(PRONOTE_PAGE_URL, { credentials: 'include' })

  if (!response.ok) {
    throw new Error(`pronotePage request failed: ${response.status}`)
  }

  return response.json() as Promise<PronotePageResponse>
}
