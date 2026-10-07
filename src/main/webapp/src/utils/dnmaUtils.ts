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

import { useConfiguration } from '@/composables/useConfiguration'

const { configuration } = useConfiguration()

export class dnmaService {
  private static get fname(): string | undefined {
    return configuration.value?.front.extendedUportal?.header?.props?.fname
  }

  static openPieceJointe() {
    this.sendEvent('OUVERTURE_PIECE_JOINTE')
  }

  static openDocument() {
    this.sendEvent('OUVERTURE_DOCUMENT')
  }

  private static sendEvent(service: string) {
    document.dispatchEvent(new CustomEvent('click-portlet-card', {
      detail: { fname: this.fname, SERVICE: service },
    }))
  }
}
