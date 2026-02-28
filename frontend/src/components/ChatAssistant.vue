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
            <!-- Bot avatar -->
            <div v-if="msg.from === 'bot'" class="avatar bot-avatar" aria-hidden="true">
              <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <defs>
                  <linearGradient id="ag" x1="0" y1="0" x2="1" y2="1">
                    <stop offset="0%" stop-color="#9D7BFF"/>
                    <stop offset="100%" stop-color="#6F7DFF"/>
                  </linearGradient>
                </defs>
                <circle cx="32" cy="32" r="28" fill="url(#ag)"/>
                <path d="M27 20l13 12-13 12v-6l7-6-7-6v-6z" fill="white"/>
              </svg>
            </div>

            <div class="bubble">
              <div class="sender">{{ msg.from === 'user' ? 'You' : 'Lupol' }}</div>
              <div class="text" v-if="msg.from === 'user'">{{ msg.text }}</div>
              <div class="text markdown" v-else v-html="parseMarkdown(msg.text)"></div>
            </div>

            <!-- User avatar -->
            <div v-if="msg.from === 'user'" class="avatar user-avatar" aria-hidden="true">
              <span>You</span>
            </div>
          </div>

          <div v-if="loading" class="message-row bot">
            <div class="avatar bot-avatar" aria-hidden="true">
              <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <defs><linearGradient id="ag2" x1="0" y1="0" x2="1" y2="1"><stop offset="0%" stop-color="#9D7BFF"/><stop offset="100%" stop-color="#6F7DFF"/></linearGradient></defs>
                <circle cx="32" cy="32" r="28" fill="url(#ag2)"/>
                <path d="M27 20l13 12-13 12v-6l7-6-7-6v-6z" fill="white"/>
              </svg>
            </div>
            <div class="bubble typing-bubble">
              <div class="sender">Lupol</div>
              <div class="typing-dots">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- Brighter, higher-contrast input section -->
        <form class="input-area" @submit.prevent="sendMessage">
          <textarea
            v-model="currentMessage"
            class="input"
            :placeholder="isMobile ? 'Type your message and tap Send...' : 'Ask me anything and press Enter...'"
            @keydown.enter.exact="handleEnter"
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
import { ensureVisitorId } from '../analytics/client'

const visitorId = ref(ensureVisitorId()) // why: stable browser id reused across features
const isMobile = ref(false)

const messages = ref([
  { from: 'bot', text: 'Hi! I’m Lupol, your AI career assistant. I\'m here to help you switch careers with confidence. Tell me your strengths and passions—let’s find your best fit' },
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
      { method: 'POST', headers: { 'X-Visitor-Id': visitorId.value } }
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

function handleEnter(e) {
  if (!isMobile.value) {
    e.preventDefault()
    sendMessage()
  }
  // on mobile: let the default textarea behaviour create a new line
}

function onKey(e) { if (e.key === 'Escape') goHome() }
onMounted(() => {
  window.addEventListener('keydown', onKey)
  isMobile.value = window.matchMedia('(pointer: coarse)').matches
})
onBeforeUnmount(() => window.removeEventListener('keydown', onKey))

/** Lightweight markdown parser for bot responses */
function parseMarkdown(text) {
  if (!text) return ''

  // Step 1: Extract markdown links [label](url) before HTML escaping, replace with placeholders
  const links = []
  let html = text.replace(/\[([^\]]+)\]\((https?:\/\/[^)]+)\)/g, (_, label, url) => {
    const i = links.length
    links.push({ label, url })
    return `\x00LINK${i}\x00`
  })

  // Step 2: Escape remaining HTML to prevent XSS
  html = html
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // Step 3: Restore links as safe <a> tags (only http/https URLs allowed, already enforced above)
  html = html.replace(/\x00LINK(\d+)\x00/g, (_, i) => {
    const { label, url } = links[Number(i)]
    const safeLabel = label.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    return `<a href="${url}" target="_blank" rel="noopener noreferrer">${safeLabel}</a>`
  })

  // Step 4: Apply remaining markdown rules
  html = html
    // Bold: **text** or __text__
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/__(.+?)__/g, '<strong>$1</strong>')
    // Italic: *text* or _text_
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/_(.+?)_/g, '<em>$1</em>')
    // Inline code: `code`
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    // Headers: ### text
    .replace(/^### (.+)$/gm, '<h4>$1</h4>')
    .replace(/^## (.+)$/gm, '<h3>$1</h3>')
    .replace(/^# (.+)$/gm, '<h2>$1</h2>')
    // Unordered lists: - item or * item
    .replace(/^[\-\*] (.+)$/gm, '<li>$1</li>')
    // Numbered lists: 1. item
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
    // Wrap consecutive <li> in <ul>
    .replace(/((?:<li>.*<\/li>\n?)+)/g, '<ul>$1</ul>')
    // Line breaks
    .replace(/\n/g, '<br>')
    // Remove <br> immediately before or after block elements (headings, lists)
    .replace(/(<br>)+(<\/?(?:h[2-4]|ul|li))/g, '$2')
    .replace(/(<\/(?:h[2-4]|ul|li)>)(<br>)+/g, '$1')
    // Collapse 3+ consecutive <br> down to 2
    .replace(/(<br>){3,}/g, '<br><br>')

  return html
}
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

/* Chat layout */
.chat-wrapper { display: flex; flex-direction: column; gap: 1rem; color: #111827; padding: 1rem; }
.chat-title { font-size: 1.5rem; font-weight: 600; color: #fff; }
.chat-subtitle { color: #cbd5e1; font-size: 0.95rem; }
.chat-card { background: #ffffff; border-radius: 16px; box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08); padding: 1rem; display: flex; flex-direction: column; min-height: 350px; }
.messages { flex: 1; overflow-y: auto; max-height: 60vh; padding: 0.75rem 0.5rem; box-sizing: border-box; display: flex; flex-direction: column; gap: 1rem; }

/* Message rows */
.message-row {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  animation: msgIn 0.25s ease-out both;
}
.message-row.user { flex-direction: row-reverse; }
@keyframes msgIn {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* Avatars */
.avatar {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 700;
}
.bot-avatar svg { width: 32px; height: 32px; border-radius: 50%; }
.user-avatar {
  background: linear-gradient(135deg, #6F7DFF, #9D7BFF);
  color: #fff;
  letter-spacing: -0.03em;
}

/* Bubbles */
.bubble {
  max-width: 78%;
  padding: 0.65rem 0.9rem;
  border-radius: 18px;
  line-height: 1.6;
}

/* Bot bubble — clean light card with left accent */
.message-row.bot .bubble {
  background: #f8f7ff;
  color: #1e1b3a;
  border: 1px solid rgba(111, 125, 255, 0.18);
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(111, 125, 255, 0.08);
}

/* User bubble — brand gradient */
.message-row.user .bubble {
  background: linear-gradient(135deg, #6F7DFF 0%, #9D7BFF 100%);
  color: #ffffff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 4px 14px rgba(111, 125, 255, 0.4);
}

.sender {
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  opacity: 0.55;
  margin-bottom: 0.3rem;
}
.message-row.user .sender { text-align: right; }

.text { font-size: 0.9rem; white-space: pre-wrap; }

/* Animated typing dots */
.typing-bubble { display: flex; flex-direction: column; }
.typing-dots {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 4px 2px;
}
.typing-dots span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #9D7BFF;
  animation: bounce 1.2s infinite ease-in-out;
}
.typing-dots span:nth-child(1) { animation-delay: 0s; }
.typing-dots span:nth-child(2) { animation-delay: 0.2s; }
.typing-dots span:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30%            { transform: translateY(-6px); opacity: 1; }
}

/* Markdown inside bot bubbles */
.text.markdown { white-space: normal; font-size: 0.9rem; }

.text.markdown :deep(h2),
.text.markdown :deep(h3),
.text.markdown :deep(h4) {
  margin: 0.5rem 0 0.1rem;
  font-weight: 700;
  color: #4c3fcf;
  padding-left: 0.6rem;
  border-left: 3px solid #9D7BFF;
  line-height: 1.3;
}
.text.markdown :deep(h2) { font-size: 1rem; }
.text.markdown :deep(h3) { font-size: 0.95rem; }
.text.markdown :deep(h4) { font-size: 0.9rem; }

.text.markdown :deep(strong) { font-weight: 700; color: #3730a3; }
.text.markdown :deep(em) { font-style: italic; color: #6b5fc7; }

.text.markdown :deep(ul) {
  margin: 0.4rem 0;
  padding-left: 0;
  list-style: none;
}
.text.markdown :deep(li) {
  position: relative;
  padding-left: 1.1rem;
  margin: 0.3rem 0;
  line-height: 1.5;
}
.text.markdown :deep(li)::before {
  content: '›';
  position: absolute;
  left: 0;
  color: #9D7BFF;
  font-weight: 700;
  font-size: 1.1em;
}

.text.markdown :deep(code) {
  background: rgba(111, 125, 255, 0.1);
  color: #4c3fcf;
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.85em;
}

.text.markdown :deep(a) {
  color: #6F7DFF;
  text-decoration: none;
  font-weight: 500;
  border-bottom: 1px solid rgba(111, 125, 255, 0.4);
  word-break: break-word;
  transition: color 0.15s, border-color 0.15s;
}
.text.markdown :deep(a:hover) {
  color: #4c3fcf;
  border-bottom-color: #4c3fcf;
}

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
