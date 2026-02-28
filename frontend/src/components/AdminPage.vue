<!-- /src/components/AdminPage.vue -->
<template>
  <div class="admin-page">

    <!-- Header -->
    <header class="admin-header">
      <button class="back-btn" @click="goHome">
        <svg class="back-ico" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414L7.293 10.707a1 1 0 010-1.414l4.999-5a1 1 0 011.415 0z"/>
        </svg>
        Back
      </button>
      <h1 class="admin-title">Admin — Conversations</h1>
    </header>

    <!-- Token gate -->
    <div v-if="!authenticated" class="gate-wrapper">
      <div class="gate-card">
        <h2 class="gate-heading">Admin Access</h2>
        <p class="gate-sub">Enter your admin token to view conversations.</p>
        <form @submit.prevent="submitToken" class="gate-form">
          <input
            v-model="tokenInput"
            type="password"
            class="gate-input"
            placeholder="Admin token"
            autocomplete="off"
          />
          <button type="submit" class="gate-btn">Unlock</button>
        </form>
        <p v-if="authError" class="gate-error">{{ authError }}</p>
      </div>
    </div>

    <!-- Main admin UI -->
    <div v-else class="admin-body">

      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="sidebar-toolbar">
          <span class="sidebar-label">Conversations</span>
          <select v-model="hours" @change="fetchConversations" class="hours-select">
            <option value="24">Last 24 h</option>
            <option value="168">Last 7 days</option>
            <option value="720">Last 30 days</option>
          </select>
        </div>

        <div v-if="loading" class="sidebar-empty">Loading…</div>
        <div v-else-if="error" class="sidebar-error">{{ error }}</div>
        <div v-else-if="conversations.length === 0" class="sidebar-empty">No conversations found.</div>

        <ul v-else class="visitor-list">
          <li
            v-for="conv in conversations"
            :key="conv.visitorId"
            class="visitor-item"
            :class="{ active: selectedId === conv.visitorId }"
            @click="selectedId = conv.visitorId"
          >
            <div class="visitor-id">{{ shortId(conv.visitorId) }}</div>
            <div class="visitor-meta">
              {{ conv.messageCount }} messages · {{ timeAgo(conv.lastActive) }}
            </div>
          </li>
        </ul>
      </aside>

      <!-- Conversation thread -->
      <main class="thread-panel">
        <div v-if="!selectedId" class="thread-empty">
          Select a conversation to view it.
        </div>

        <template v-else>
          <div class="thread-header">
            <span class="thread-visitor-id">{{ selectedConversation?.visitorId }}</span>
          </div>

          <div class="thread-messages">
            <div
              v-for="(msg, i) in selectedConversation?.messages"
              :key="i"
              class="message-row"
              :class="msg.role"
            >
              <div class="bubble">
                <div class="sender">{{ msg.role === 'user' ? 'User' : 'Lupol' }}</div>
                <div class="text" v-if="msg.role === 'user'">{{ msg.text }}</div>
                <div class="text markdown" v-else v-html="parseMarkdown(msg.text)"></div>
                <div class="msg-time">{{ formatTime(msg.timestamp) }}</div>
              </div>
            </div>
          </div>
        </template>
      </main>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { goHome } from '../viewState'

const TOKEN_KEY = 'lupol_admin_token'

const tokenInput = ref('')
const authError = ref('')
const authenticated = ref(false)
const conversations = ref([])
const selectedId = ref(null)
const loading = ref(false)
const error = ref('')
const hours = ref('24')

const selectedConversation = computed(() =>
  conversations.value.find(c => c.visitorId === selectedId.value)
)

onMounted(() => {
  const saved = sessionStorage.getItem(TOKEN_KEY)
  if (saved) {
    authenticated.value = true
    fetchConversations()
  }
})

async function submitToken() {
  authError.value = ''
  const ok = await fetchConversations(tokenInput.value)
  if (ok) {
    sessionStorage.setItem(TOKEN_KEY, tokenInput.value)
    authenticated.value = true
  } else {
    authError.value = 'Invalid token or server error.'
  }
}

async function fetchConversations(token) {
  const adminToken = token ?? sessionStorage.getItem(TOKEN_KEY)
  loading.value = true
  error.value = ''
  try {
    const res = await fetch(
      `/lupoldevtwo/admin/conversations?hours=${hours.value}`,
      { headers: { 'X-Admin-Token': adminToken } }
    )
    if (res.status === 401) { loading.value = false; return false }
    if (!res.ok) {
      const body = await res.json().catch(() => ({}))
      error.value = body.error ?? `Server error ${res.status}`
      loading.value = false
      return false
    }
    conversations.value = await res.json()
    if (conversations.value.length > 0 && !selectedId.value) {
      selectedId.value = conversations.value[0].visitorId
    }
    loading.value = false
    return true
  } catch (e) {
    error.value = e.message
    loading.value = false
    return false
  }
}

function shortId(id) {
  if (!id) return '(anonymous)'
  return id.length > 16 ? id.slice(0, 8) + '…' + id.slice(-6) : id
}

function timeAgo(ms) {
  if (!ms) return ''
  const diff = Date.now() - ms
  const m = Math.floor(diff / 60000)
  if (m < 1) return 'just now'
  if (m < 60) return `${m}m ago`
  const h = Math.floor(m / 60)
  if (h < 24) return `${h}h ago`
  return `${Math.floor(h / 24)}d ago`
}

function formatTime(ms) {
  if (!ms) return ''
  return new Date(ms).toLocaleString()
}

/** Reuse the same markdown parser as ChatAssistant */
function parseMarkdown(text) {
  if (!text) return ''
  const links = []
  let html = text.replace(/\[([^\]]+)\]\((https?:\/\/[^)]+)\)/g, (_, label, url) => {
    const i = links.length
    links.push({ label, url })
    return `\x00LINK${i}\x00`
  })
  html = html.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  html = html.replace(/\x00LINK(\d+)\x00/g, (_, i) => {
    const { label, url } = links[Number(i)]
    const safe = label.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    return `<a href="${url}" target="_blank" rel="noopener noreferrer">${safe}</a>`
  })
  html = html
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/__(.+?)__/g, '<strong>$1</strong>')
    .replace(/\*(.+?)\*/g, '<em>$1</em>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/^### (.+)$/gm, '<h4>$1</h4>')
    .replace(/^## (.+)$/gm, '<h3>$1</h3>')
    .replace(/^# (.+)$/gm, '<h2>$1</h2>')
    .replace(/^[\-\*] (.+)$/gm, '<li>$1</li>')
    .replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
    .replace(/((?:<li>.*<\/li>\n?)+)/g, '<ul>$1</ul>')
    .replace(/\n/g, '<br>')
    .replace(/(<br>)+(<\/?(?:h[2-4]|ul|li))/g, '$2')
    .replace(/(<\/(?:h[2-4]|ul|li)>)(<br>)+/g, '$1')
    .replace(/(<br>){3,}/g, '<br><br>')
  return html
}
</script>

<style scoped>
.admin-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #151130, #0f0a26);
  color: #fff;
  display: flex;
  flex-direction: column;
}

/* Header */
.admin-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: .75rem;
  padding: .75rem 1rem;
  background: rgba(15,10,38,.7);
  backdrop-filter: blur(6px);
  border-bottom: 1px solid rgba(255,255,255,.08);
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
.back-btn:hover { background: rgba(255,255,255,.12); }
.back-ico { width: 16px; height: 16px; }
.admin-title { font-weight: 600; font-size: 1rem; }

/* Token gate */
.gate-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
.gate-card {
  background: rgba(255,255,255,.04);
  border: 1px solid rgba(255,255,255,.1);
  border-radius: 16px;
  padding: 2rem;
  width: 100%;
  max-width: 380px;
  text-align: center;
}
.gate-heading { font-size: 1.3rem; font-weight: 700; margin-bottom: .5rem; }
.gate-sub { color: #94a3b8; font-size: .9rem; margin-bottom: 1.25rem; }
.gate-form { display: flex; gap: .5rem; }
.gate-input {
  flex: 1;
  padding: .55rem .85rem;
  border-radius: 10px;
  border: 1px solid rgba(255,255,255,.15);
  background: rgba(255,255,255,.06);
  color: #fff;
  font-size: .9rem;
}
.gate-input::placeholder { color: #64748b; }
.gate-input:focus { outline: none; border-color: #6F7DFF; }
.gate-btn {
  padding: .55rem 1.1rem;
  border-radius: 10px;
  background: linear-gradient(135deg, #6F7DFF, #9D7BFF);
  color: #fff;
  border: none;
  font-weight: 600;
  cursor: pointer;
}
.gate-error { margin-top: .75rem; color: #f87171; font-size: .85rem; }

/* Main layout */
.admin-body {
  flex: 1;
  display: flex;
  overflow: hidden;
  height: calc(100vh - 50px);
}

/* Sidebar */
.sidebar {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid rgba(255,255,255,.08);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.sidebar-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: .75rem 1rem;
  border-bottom: 1px solid rgba(255,255,255,.06);
}
.sidebar-label { font-size: .8rem; font-weight: 600; text-transform: uppercase; letter-spacing: .05em; opacity: .6; }
.hours-select {
  background: rgba(255,255,255,.06);
  border: 1px solid rgba(255,255,255,.12);
  color: #fff;
  font-size: .8rem;
  border-radius: 6px;
  padding: .2rem .5rem;
  cursor: pointer;
}
.sidebar-empty, .sidebar-error {
  padding: 1rem;
  font-size: .85rem;
  opacity: .6;
  text-align: center;
}
.sidebar-error { color: #f87171; opacity: 1; }
.visitor-list { flex: 1; overflow-y: auto; list-style: none; padding: 0; margin: 0; }
.visitor-item {
  padding: .75rem 1rem;
  border-bottom: 1px solid rgba(255,255,255,.04);
  cursor: pointer;
  transition: background .15s;
}
.visitor-item:hover { background: rgba(111,125,255,.1); }
.visitor-item.active { background: rgba(111,125,255,.2); border-left: 3px solid #9D7BFF; }
.visitor-id { font-size: .85rem; font-weight: 600; font-family: monospace; }
.visitor-meta { font-size: .75rem; opacity: .55; margin-top: .15rem; }

/* Thread panel */
.thread-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.thread-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: .4;
  font-size: .95rem;
}
.thread-header {
  padding: .65rem 1rem;
  border-bottom: 1px solid rgba(255,255,255,.06);
  font-size: .75rem;
  font-family: monospace;
  opacity: .55;
}
.thread-messages {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: .75rem;
}

/* Bubbles — same style as ChatAssistant */
.message-row { display: flex; }
.message-row.user { justify-content: flex-end; }
.message-row.assistant { justify-content: flex-start; }

.bubble {
  max-width: 78%;
  padding: .65rem .9rem;
  border-radius: 18px;
  line-height: 1.6;
}
.message-row.user .bubble {
  background: linear-gradient(135deg, #6F7DFF 0%, #9D7BFF 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.message-row.assistant .bubble {
  background: #f8f7ff;
  color: #1e1b3a;
  border: 1px solid rgba(111,125,255,.18);
  border-bottom-left-radius: 4px;
}
.sender {
  font-size: .7rem;
  font-weight: 600;
  letter-spacing: .04em;
  text-transform: uppercase;
  opacity: .5;
  margin-bottom: .25rem;
}
.message-row.user .sender { text-align: right; }
.text { font-size: .88rem; white-space: pre-wrap; }
.msg-time { font-size: .7rem; opacity: .4; margin-top: .3rem; text-align: right; }

/* Markdown in assistant bubbles */
.text.markdown { white-space: normal; }
.text.markdown :deep(strong) { font-weight: 700; color: #3730a3; }
.text.markdown :deep(em) { font-style: italic; }
.text.markdown :deep(h2), .text.markdown :deep(h3), .text.markdown :deep(h4) {
  margin: .4rem 0 .1rem; font-weight: 700; color: #4c3fcf;
  padding-left: .5rem; border-left: 3px solid #9D7BFF; line-height: 1.3;
}
.text.markdown :deep(ul) { margin: .3rem 0; padding-left: 0; list-style: none; }
.text.markdown :deep(li) { position: relative; padding-left: 1rem; margin: .2rem 0; }
.text.markdown :deep(li)::before { content: '›'; position: absolute; left: 0; color: #9D7BFF; font-weight: 700; }
.text.markdown :deep(a) { color: #6F7DFF; border-bottom: 1px solid rgba(111,125,255,.4); text-decoration: none; font-weight: 500; word-break: break-word; }
.text.markdown :deep(a:hover) { color: #4c3fcf; }
.text.markdown :deep(code) { background: rgba(111,125,255,.1); color: #4c3fcf; padding: .1rem .3rem; border-radius: 4px; font-family: monospace; font-size: .85em; }
</style>
