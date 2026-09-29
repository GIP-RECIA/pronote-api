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

import { createI18n } from 'vue-i18n'
import en from '@/locales/en.json'
import fr from '@/locales/fr.json'

const config: {
  fallbackLocale: string
  supportLocales: string[]
} = {
  fallbackLocale: 'fr',
  supportLocales: ['en', 'fr'],
}

function findLanguage(): string {
  const candidates = [window.navigator.language, ...window.navigator.languages]
  const match = candidates
    .map(lang => lang.split('-')[0] ?? lang)
    .find(lang => config.supportLocales.includes(lang))
  return match ?? config.fallbackLocale
}

export default createI18n({
  legacy: false,
  locale: findLanguage(),
  fallbackLocale: config.fallbackLocale,
  messages: { fr, en },
})
