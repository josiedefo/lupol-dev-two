<!-- /src/pages/ChatAssistant.vue -->
<template>
  <div class="assistant-page">
    <header class="assistant-header">
      <button
        class="back-btn"
        type="button"
        @click="goHome"
        aria-label="Back to Home"
      >
        <svg class="back-ico" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414L7.293 10.707a1 1 0 010-1.414l4.999-5a1 1 0 011.415 0z"/>
        </svg>
        Back
      </button>
      <h1 class="assistant-title">Career Chat Assistant</h1>
      <span class="assistant-status" aria-live="polite">
        <span class="dot"></span> Online
      </span>
    </header>

    <div class="chat-wrapper" role="main">
      <h2 class="chat-title">Your AI Career Coach</h2>
      <p class="chat-subtitle">
        Ask anything about your career, skills, job search, or next step.
      </p>

      <div class="chat-card" role="region" aria-label="Chat messages">
        <div class="messages" ref="messagesContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message-row"
            :class="msg.from"
          >
            <div class="bubble">
              <div class="sender">
                {{ msg.from === 'user' ? 'You' : 'Assistant' }}
              </div>
              <div class="text">
                {{ msg.text }}
              </div>
            </div>
          </div>

          <div v-if="loading" class="message-row bot">
            <div class="bubble typing">
              <div class="sender">Assistant</div>
              <div class="text">Typing...</div>
            </div>
          </div>
        </div>

        <!-- Brighter, higher-contrast input section -->
        <form class="input-area" @submit.prevent="sendMessage">
          <textarea
            v-model="currentMessage"
            class="input"
            placeholder="Ask me anything and press Enter..."
            @keydown.enter.exact.prevent="sendMessage"
            @keydown.shift.enter.stop
            :aria-busy="loading ? 'true' : 'false'"
          ></textarea>

          <div class="actions">
            <span v-if="error" class="error" role="alert">Error: {{ error }}</span>

            <button
              type="submit"
              class="send-button"
              :disabled="!currentMessage.trim() || loading"
              :aria-disabled="!currentMessage.trim() || loading"
            >
              {{ loading ? 'Sending...' : 'Send' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch, onMounted, onBeforeUnmount } from 'vue'
import { goHome } from '../viewState'

const messages = ref([
  { from: 'bot', text: 'Hi! I’m your AI career assistant. What would you like to explore today?' },
])

const currentMessage = ref('')
const loading = ref(false)
const error = ref('')
const messagesContainer = ref(null)

async function sendMessage() {
  const text = currentMessage.value.trim()
  if (!text || loading.value) return
  error.value = ''

  messages.value.push({ from: 'user', text })
  currentMessage.value = ''
  scrollToBottom()
  loading.value = true

  try {
    const res = await fetch(
      '/lupoldevtwo/career/chat?userInput=' + encodeURIComponent(text),
      { method: 'POST' }
    )
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const replyText = await res.text()
    messages.value.push({ from: 'bot', text: replyText })
  } catch (e) {
    console.error(e)
    error.value = e.message || 'Something went wrong'
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    const el = messagesContainer.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

watch(() => messages.value.length, () => scrollToBottom())

function onKey(e) { if (e.key === 'Escape') goHome() }
onMounted(() => window.addEventListener('keydown', onKey))
onBeforeUnmount(() => window.removeEventListener('keydown', onKey))
</script>

<style scoped>
/* Page/header */
.assistant-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #151130, #0f0a26);
  color: #111827;
}
.assistant-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: .75rem;
  padding: .75rem 1rem;
  background: rgba(15,10,38,.6);
  backdrop-filter: blur(6px);
  border-bottom: 1px solid rgba(255,255,255,.08);
  color: #fff;
}
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: .4rem;
  padding: .35rem .6rem;
  border-radius: .5rem;
  background: rgba(255,255,255,.06);
  color: #fff;
  border: 1px solid rgba(255,255,255,.1);
  cursor: pointer;
}
.back-btn:hover { background: rgba(255,255,255,.1); }
.back-ico { width: 16px; height: 16px; }
.assistant-title { font-weight: 600; }
.assistant-status {
  margin-left: auto;
  font-size: .75rem;
  opacity: .85;
  display: inline-flex;
  align-items: center;
  gap: .4rem;
}
.dot { width: 8px; height: 8px; border-radius: 50%; background: #34d399; }

/* Existing chat styles */
.chat-wrapper { display: flex; flex-direction: column; gap: 1rem; color: #111827; padding: 1rem; }
.chat-title { font-size: 1.5rem; font-weight: 600; color: #fff; }
.chat-subtitle { color: #cbd5e1; font-size: 0.95rem; }
.chat-card { background: #ffffff; border-radius: 16px; box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08); padding: 1rem; display: flex; flex-direction: column; min-height: 350px; }
.messages { flex: 1; overflow-y: auto; max-height: 60vh; padding: 0.5rem; box-sizing: border-box; }
.message-row { display: flex; margin-bottom: 0.5rem; }
.message-row.user { justify-content: flex-end; }
.message-row.bot { justify-content: flex-start; }
.bubble { max-width: 75%; padding: 0.5rem 0.75rem; border-radius: 12px; }
.message-row.user .bubble { background: #2563eb; color: #ffffff; border-bottom-right-radius: 4px; }
.message-row.bot .bubble { background: #e5e7eb; color: #111827; border-bottom-left-radius: 4px; }
.sender { font-size: .75rem; opacity: .75; margin-bottom: .25rem; }
.text { white-space: pre-wrap; font-size: .9rem; }
.typing .text { font-style: italic; }

/* >>> Brighter input section <<< */
.input-area {
  border-top: 1px solid #e5e7eb;
  background: #f8fafc;                 /* light panel to separate from dark page */
  padding-top: .75rem;
  margin-top: .75rem;
  display: flex;
  flex-direction: column;
  gap: .5rem;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.input {
  width: 100%;
  min-height: 56px;
  max-height: 160px;
  resize: vertical;
  padding: .65rem .85rem;
  font-size: .95rem;
  line-height: 1.4;
  background: #ffffff;                 /* solid white for maximum contrast */
  color: #111827;                      /* dark text */
  border-radius: 10px;
  border: 1px solid #94a3b8;           /* higher-contrast border */
  box-shadow: 0 1px 2px rgba(0,0,0,.04);
}

.input::placeholder {
  color: #64748b;                      /* clearer placeholder */
}

.input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow:
    0 0 0 1px rgba(37, 99, 235, .45),
    0 0 0 4px rgba(37, 99, 235, .10);  /* visible focus ring */
}

.input[disabled] {
  background: #f1f5f9;
  color: #475569;
  border-color: #cbd5e1;
}

.actions { display: flex; justify-content: space-between; align-items: center; }
.error { color: #b91c1c; font-size: .8rem; }
.send-button { background: #2563eb; color: white; border: none; border-radius: 999px; padding: .45rem 1.25rem; font-size: .9rem; font-weight: 500; cursor: pointer; }
.send-button:disabled { background: #9ca3af; }
</style>
