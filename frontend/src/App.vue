<template>
  <div class="app">
    <h1 class="title">Lupol Career Chat</h1>

    <div class="chat-container">
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
          placeholder="Type your question here and press Enter..."
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
    text: 'Hi! Ask me anything about your career.',
  },
])

const currentMessage = ref('')
const loading = ref(false)
const error = ref('')
const messagesContainer = ref(null)

async function sendMessage() {
  const text = currentMessage.value.trim()
  if (!text || loading.value) return

  // Clear error
  error.value = ''

  // Add user message to chat
  messages.value.push({
    from: 'user',
    text,
  })

  currentMessage.value = ''
  scrollToBottom()

  loading.value = true

  console.log("Sending message to backend: ", text)

  try {
    const res = await fetch('/lupoldevtwo/career/chat?userInput=' + encodeURIComponent(text), {
      method: 'POST',
    })
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`)
    }

    // ASSUMPTION: backend returns JSON like { reply: "..." }
    //const data = await res.json()
    //const replyText = data.reply ?? JSON.stringify(data)
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
    if (el) {
      el.scrollTop = el.scrollHeight
    }
  })
}

// Also auto-scroll whenever messages change
watch(
  () => messages.value.length,
  () => scrollToBottom()
)
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
