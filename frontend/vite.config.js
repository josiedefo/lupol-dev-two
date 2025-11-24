import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/lupoldevtwo/',

  plugins: [vue()],
  server: {
    proxy: {
      '/lupoldevtwo': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
