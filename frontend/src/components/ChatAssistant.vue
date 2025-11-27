<template>
  <div class="chat-wrapper">
    <h2 class="chat-title">Your AI Career Coach</h2>
    <p class="chat-subtitle">
      Ask anything about your career, skills, job search, or next step.
    </p>

    <div class="chat-card">
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

      <form class="input-area" @submit.prevent="sendMessage">
        <textarea
          v-model="currentMessage"
          class="input"
          placeholder="Ask me anything and press Enter..."
          @keydown.enter.exact.prevent="sendMessage"
          @keydown.shift.enter.stop
        ></textarea>

        <div class="actions">
          <span v-if="error" class="error">Error: {{ error }}</span>

          <button
            type="submit"
            class="send-button"
            :disabled="!currentMessage.trim() || loading"
          >
            {{ loading ? 'Sending...' : 'Send' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'

const messages = ref([
  {
    from: 'bot',
    text: 'Hi! I’m your AI career assistant. What would you like to explore today?',
  },
])

const currentMessage = ref('')
const loading = ref(false)
const error = ref('')
const messagesContainer = ref(null)

async function sendMessage() {
  const text = currentMessage.value.trim()
  if (!text || loading.value) return

  error.value = ''

  messages.value.push({
    from: 'user',
    text,
  })

  currentMessage.value = ''
  scrollToBottom()

  loading.value = true

  try {
    const res = await fetch(
      '/lupoldevtwo/career/chat?userInput=' + encodeURIComponent(text),
      {
        method: 'POST',
      }
    )

    if (!res.ok) throw new Error(`HTTP ${res.status}`)

    const replyText = await res.text()

    messages.value.push({
      from: 'bot',
      text: replyText,
    })
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

watch(
  () => messages.value.length,
  () => scrollToBottom()
)
</script>

<style scoped>
/* same styling as before */
.chat-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chat-title {
  font-size: 1.5rem;
  font-weight: 600;
}

.chat-subtitle {
  color: #4b5563;
  font-size: 0.95rem;
}

.chat-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
  padding: 1rem;
  display: flex;
  flex-direction: column;
  min-height: 350px;
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
  max-width: 75%;
  padding: 0.5rem 0.75rem;
  border-radius: 12px;
}

.message-row.user .bubble {
  background: #2563eb;
  color: #ffffff;
  border-bottom-right-radius: 4px;
}

.message-row.bot .bubble {
  background: #e5e7eb;
  color: #111827;
  border-bottom-left-radius: 4px;
}

.sender {
  font-size: .75rem;
  opacity: .75;
  margin-bottom: .25rem;
}

.text {
  white-space: pre-wrap;
  font-size: .9rem;
}

.typing .text {
  font-style: italic;
}

.input-area {
  border-top: 1px solid #e5e7eb;
  padding-top: .75rem;
  margin-top: .75rem;
  display: flex;
  flex-direction: column;
  gap: .5rem;
}

.input {
  width: 100%;
  min-height: 50px;
  max-height: 140px;
  resize: vertical;
  padding: .5rem .75rem;
  font-size: .9rem;
  border-radius: 8px;
  border: 1px solid #d1d5db;
}

.input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, .3);
}

.actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error {
  color: #b91c1c;
  font-size: .8rem;
}

.send-button {
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 999px;
  padding: .4rem 1.25rem;
  font-size: .9rem;
  font-weight: 500;
  cursor: pointer;
}

.send-button:disabled {
  background: #9ca3af;
}
</style>
