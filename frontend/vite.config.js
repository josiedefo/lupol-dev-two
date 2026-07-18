import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/',

  plugins: [vue()],
  esbuild: { target: 'es2018' },
  build: {
    target: 'es2018',
    cssCodeSplit: true,
    modulePreload: { polyfill: false },
    sourcemap: false,
    minify: 'esbuild',
    treeshake: 'smallest',
    rollupOptions: {
      output: {
        manualChunks: { vue: ['vue'] },
        chunkFileNames: 'assets/[name]-[hash].js',
        entryFileNames: 'assets/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash][extname]'
      }
    }
  },
  optimizeDeps: { include: ['vue'], esbuildOptions: { target: 'es2018' } },
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
