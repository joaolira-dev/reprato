import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    strictPort: true,
    proxy: {
      '/api/offers': 'http://localhost:18081',
      '/api/stock': 'http://localhost:18082',
      '/api/orders': 'http://localhost:18083',
    },
  },
})
