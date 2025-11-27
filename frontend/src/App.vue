<template>
  <div class="app">
    <header class="app-header">
      <div class="brand">
        <span class="logo-dot"></span>
        <span class="brand-text">Lupol Careers</span>
      </div>

      <button
        v-if="started"
        class="ghost-button"
        @click="started = false"
      >
        ⟵ Back to home
      </button>
    </header>

    <main class="app-main">
      <!-- Landing page -->
      <section v-if="!started" class="landing">
        <div class="hero">
          <h1 class="hero-title">Welcome to the best career site</h1>
          <p class="hero-subtitle">
            Turn your passions into a clear, confident career path.
            Our AI career coach helps you explore options, uncover your strengths,
            and plan your next step — in minutes, not months.
          </p>

          <ul class="hero-list">
            <li>✨ Discover careers that match your passions</li>
            <li>🧭 Get personalized guidance for transitions and promotions</li>
            <li>📌 Explore reskilling, upskilling, and side projects</li>
          </ul>

          <div class="hero-actions">
            <button class="primary-button" @click="started = true">
              Get started
            </button>
            <p class="hero-footnote">
              No signup. No forms. Just practical guidance.
            </p>
          </div>
        </div>

        <div class="hero-panel">
          <h2>What can I ask?</h2>
          <ul class="hero-panel-list">
            <li>“What career fits someone who loves dancing?”</li>
            <li>“How can I move from teaching to tech?”</li>
            <li>“What jobs can I work remotely with my skills?”</li>
          </ul>
        </div>
      </section>

      <!-- Chat page -->
      <section v-else class="chat-section">
        <ChatAssistant />
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChatAssistant from './components/ChatAssistant.vue'

const started = ref(false)
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: left;
  background: #f3f4f6;
  padding: 2rem 1rem;
  box-sizing: border-box;
}

.title {
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI',
    sans-serif;
  font-weight: 600;
  margin-bottom: 1rem;
  color: blue;
}

.chat-container {
  width: 100%;
  max-width: 800px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  padding: 1rem;
  box-sizing: border-box;
}

.messages {
  flex: 1;
  overflow-y: auto;
  max-height: 60vh;
  padding: 0.5rem;
  box-sizing: border-box;
}

.message-row {
  display: flex;
  margin-bottom: 0.5rem;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.bot {
  justify-content: flex-start;
}

.bubble {
  max-width: 70%;
  padding: 0.5rem 0.75rem;
  border-radius: 12px;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI',
    sans-serif;
}

.message-row.user .bubble {
  background: #2563eb;
  color: white;
  border-bottom-right-radius: 2px;
}

.message-row.bot .bubble {
  background: #e5e7eb;
  color: #111827;
  border-bottom-left-radius: 2px;
}

.sender {
  font-size: 0.75rem;
  opacity: 0.75;
  margin-bottom: 0.25rem;
}

.text {
  white-space: pre-wrap;
  font-size: 0.9rem;
}

.typing .text {
  font-style: italic;
}

.input-area {
  border-top: 1px solid #e5e7eb;
  padding-top: 0.75rem;
  margin-top: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input {
  width: 100%;
  min-height: 50px;
  max-height: 140px;
  resize: vertical;
  padding: 0.5rem 0.75rem;
  font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI',
    sans-serif;
  font-size: 0.9rem;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  box-sizing: border-box;
}

.input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.3);
}

.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error {
  color: #b91c1c;
  font-size: 0.8rem;
}

.send-button {
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 999px;
  padding: 0.4rem 1.25rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
}

.send-button:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}
</style>
