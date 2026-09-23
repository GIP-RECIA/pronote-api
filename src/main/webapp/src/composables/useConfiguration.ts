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

import type { Configuration } from '@/types/configuration'
import { ref } from 'vue'

const configuration = ref<Configuration | null>(null)
const isInit = ref(false)

function loadExtendedUportalScript(src: string) {
  const script = document.createElement('script')
  script.setAttribute('src', src)
  script.setAttribute('charset', 'utf-8')
  document.head.appendChild(script)
}

export async function initConfiguration() {
  const response = await fetch(`${import.meta.env.VITE_API_URI}/api/config`, { credentials: 'include' })
  if (!response.ok) {
    throw new Error(`configuration request failed: ${response.status}`)
  }
  configuration.value = await response.json() as Configuration

  const extendedUportal = configuration.value.front.extendedUportal
  if (extendedUportal?.header) {
    loadExtendedUportalScript(extendedUportal.header.componentPath)
  }
  if (extendedUportal?.footer) {
    loadExtendedUportalScript(extendedUportal.footer.componentPath)
  }

  isInit.value = true
}
export function useConfiguration() {
  return { configuration, isInit }
}
