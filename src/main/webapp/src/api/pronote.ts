import type { PronotePageResponse } from '@/types/pronote'

const PRONOTE_PAGE_URL = '/mocks/pronote-page.json'

export async function fetchPronotePage(): Promise<PronotePageResponse> {
  const response = await fetch(PRONOTE_PAGE_URL, { credentials: 'include' })

  if (!response.ok) {
    throw new Error(`pronotePage request failed: ${response.status}`)
  }

  return response.json() as Promise<PronotePageResponse>
}
