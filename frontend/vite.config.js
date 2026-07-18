import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/',

  plugins: [vue()],
  build: {
    target: 'es2018',
    cssCodeSplit: true,
    modulePreload: { polyfill: false },
    sourcemap: false,
    rollupOptions: {
      output: {
        // Rolldown (Vite 8) only accepts the function form of manualChunks
        manualChunks(id) {
          if (id.includes('node_modules/vue/') || id.includes('node_modules/@vue/')) return 'vue'
        },
        chunkFileNames: 'assets/[name]-[hash].js',
        entryFileNames: 'assets/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash][extname]'
      }
    }
  },
  optimizeDeps: { include: ['vue'] },
  server: {
    proxy: {
      '/career': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/analytics': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api/feedback': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
