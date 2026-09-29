import { readFileSync } from 'node:fs'
import { fileURLToPath, URL } from 'node:url'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'
import vue from '@vitejs/plugin-vue'
import { defineConfig, loadEnv } from 'vite'
import vueDevTools from 'vite-plugin-vue-devtools'
import { parseString } from 'xml2js'
import { slugify } from './src/main/webapp/src/utils/stringUtils.ts'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())

  const {
    VITE_APP_NAME,
    VITE_APP_SLUG,
    VITE_BASE_URI,
    VITE_ALLOWED_HOSTS,
    VITE_PROXY_API_URL,
  } = env

  const appName = JSON.stringify(VITE_APP_NAME)
  const appSlug = JSON.stringify(VITE_APP_SLUG || slugify(appName))

  const backVersion = (): string => {
    let version
    const pomXml = readFileSync('./pom.xml', 'utf8')
    parseString(pomXml, (err, result) => {
      if (err)
        console.error(err)
      else version = result.project.version[0]
    })
    return JSON.stringify(version)
  }

  return {
    base: `${VITE_BASE_URI}/ui`,
    root: './src/main/webapp',
    envDir: '../../../',
    plugins: [
      vue({
        template: {
          compilerOptions: {
            isCustomElement: tag =>
              ['r-filters', 'r-tablist', 'r-tabpanel', 'r-page-layout', 'extended-uportal-header', 'extended-uportal-footer'].includes(tag),
          },
        },
      }),
      VueI18nPlugin({
        include: [fileURLToPath(new URL('./src/main/webapp/src/locales/**', import.meta.url))],
      }),
      vueDevTools(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src/main/webapp/src', import.meta.url)),
      },
    },
    server: {
      allowedHosts: JSON.parse(VITE_ALLOWED_HOSTS ?? '[]'),
      proxy: {
        '^(?:/[^/]*)?/(?=api|app)': {
          target: VITE_PROXY_API_URL,
          changeOrigin: true,
        },
      },
    },
    define: {
      __APP_NAME__: appName,
      __APP_SLUG__: appSlug,
      __BACK_VERSION__: backVersion(),
    },
  }
})
