/**
 * Copyright (C) 2026 GIP-RECIA, Inc.
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

const BCP47_LOCALES: Record<string, string> = {
  fr: 'fr-FR',
  en: 'en-GB',
}

function toBcp47(locale: string): string {
  return BCP47_LOCALES[locale] ?? locale
}

function formatFullDate(date: string, locale: string): string {
  return new Date(date).toLocaleDateString(toBcp47(locale), { day: 'numeric', month: 'long', year: 'numeric' })
}

function formatFullWeekday(date: string, locale: string): string {
  return new Date(date).toLocaleDateString(toBcp47(locale), { weekday: 'long', day: 'numeric', month: 'long' })
}

function formatTimeRange(start: string, end: string, locale: string): string {
  const opts: Intl.DateTimeFormatOptions = { hour: '2-digit', minute: '2-digit' }
  const bcp47 = toBcp47(locale)
  const startTime = new Date(start).toLocaleTimeString(bcp47, opts)
  const endTime = new Date(end).toLocaleTimeString(bcp47, opts)
  return `${startTime} – ${endTime}`
}

export {
  formatFullDate,
  formatFullWeekday,
  formatTimeRange,
}
