import { createApp } from 'vue'
import App from './App.vue'
import './assets/main.scss'
const HEADER_URL = 'https://... (la vraie URL trouvée)'
const FOOTER_URL = 'https://... (idem)'

function loadExtendedUportalScript(src: string) {
  const script = document.createElement('script')
  script.setAttribute('src', src)
  script.setAttribute('charset', 'utf-8')
  document.head.appendChild(script)
}

loadExtendedUportalScript(HEADER_URL)
loadExtendedUportalScript(FOOTER_URL)

createApp(App).mount('#app')
