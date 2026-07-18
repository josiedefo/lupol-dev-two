<template>
  <Transition name="widget-fade">
    <div v-if="hasMessages && !dismissed" class="feedback-widget">

      <!-- Expanded card -->
      <Transition name="card-slide">
        <div v-if="open && !submitted" class="feedback-card" role="dialog" aria-label="Share your feedback">
          <button class="card-close" @click="close" aria-label="Close feedback">✕</button>

          <p class="card-question">Was Lupol helpful?</p>
          <div class="helpful-row">
            <button
              class="helpful-btn"
              :class="{ selected: helpful === true }"
              @click="helpful = true"
              aria-label="Yes, helpful"
            >👍</button>
            <button
              class="helpful-btn"
              :class="{ selected: helpful === false }"
              @click="helpful = false"
              aria-label="Not helpful"
            >👎</button>
          </div>

          <p class="card-question">How can we improve?</p>
          <textarea
            v-model="comment"
            class="feedback-textarea"
            placeholder="Optional — tell us more..."
            maxlength="300"
            rows="3"
          ></textarea>

          <div class="card-actions">
            <button class="skip-btn" @click="skip">Skip</button>
            <button class="submit-btn" @click="submit" :disabled="submitting">
              {{ submitting ? 'Sending…' : 'Submit' }}
            </button>
          </div>
        </div>
      </Transition>

      <!-- Thank you state -->
      <Transition name="card-slide">
        <div v-if="submitted" class="feedback-card thankyou">
          <span class="thankyou-text">Thank you! 🎉</span>
        </div>
      </Transition>

      <!-- Floating toggle button -->
      <button
        v-if="!submitted"
        class="fab"
        :class="{ active: open }"
        @click="toggleOpen"
        aria-label="Share feedback"
        title="Share feedback"
      >
        <svg v-if="!open" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="fab-ico">
          <path d="M4.913 2.658c2.075-.27 4.19-.408 6.337-.408 2.147 0 4.262.139 6.337.408 1.922.25 3.291 1.861 3.405 3.727a4.403 4.403 0 00-1.032-.211 50.89 50.89 0 00-8.42 0c-2.358.196-4.04 2.19-4.04 4.434v4.286a4.47 4.47 0 002.433 3.984L7.28 21.53A.75.75 0 016 21v-4.03a48.527 48.527 0 01-1.087-.128C2.905 16.58 1.5 14.833 1.5 12.862V6.638c0-1.97 1.405-3.718 3.413-3.979z" />
          <path d="M15.75 7.5c-1.376 0-2.739.057-4.086.169C10.124 7.797 9 9.103 9 10.609v4.285c0 1.507 1.128 2.814 2.67 2.94 1.243.102 2.5.157 3.768.165l2.782 2.781a.75.75 0 001.28-.53v-2.39l.33-.026c1.542-.125 2.67-1.433 2.67-2.94v-4.286c0-1.505-1.125-2.811-2.664-2.94A49.392 49.392 0 0015.75 7.5z" />
        </svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="fab-ico">
          <path fill-rule="evenodd" d="M5.47 5.47a.75.75 0 011.06 0L12 10.94l5.47-5.47a.75.75 0 111.06 1.06L13.06 12l5.47 5.47a.75.75 0 11-1.06 1.06L12 13.06l-5.47 5.47a.75.75 0 01-1.06-1.06L10.94 12 5.47 6.53a.75.75 0 010-1.06z" clip-rule="evenodd" />
        </svg>
      </button>

    </div>
  </Transition>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visitorId: { type: String, default: '' },
  hasMessages: { type: Boolean, default: false },
  autoOpen: { type: Boolean, default: false }
})

const emit = defineEmits(['done'])

const open = ref(false)
const helpful = ref(null)
const comment = ref('')
const submitting = ref(false)
const submitted = ref(false)
const dismissed = ref(false)

watch(() => props.autoOpen, (val) => {
  if (val && !submitted.value) open.value = true
})

function toggleOpen() {
  open.value = !open.value
}

function close() {
  open.value = false
}

function skip() {
  open.value = false
  dismissed.value = true
  emit('done')
}

async function submit() {
  submitting.value = true
  try {
    const res = await fetch('/api/feedback', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        visitorId: props.visitorId,
        helpful: helpful.value,
        comment: comment.value.trim() || null
      })
    })
    if (!res.ok) console.error('[FeedbackWidget] submission failed:', res.status, res.statusText)
  } catch (e) {
    console.error('[FeedbackWidget] network error:', e)
  }
  submitting.value = false
  submitted.value = true
  setTimeout(() => {
    dismissed.value = true
    emit('done')
  }, 2000)
}
</script>

<style scoped>
.feedback-widget {
  position: fixed;
  bottom: 90px;
  right: 16px;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

/* FAB button */
.fab {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #6F7DFF 0%, #9D7BFF 100%);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 14px rgba(111, 125, 255, 0.5);
  transition: transform .15s, box-shadow .15s;
  flex-shrink: 0;
}
.fab:hover { transform: scale(1.08); box-shadow: 0 6px 18px rgba(111,125,255,.65); }
.fab.active { background: rgba(255,255,255,.15); box-shadow: none; }
.fab-ico { width: 20px; height: 20px; }

/* Feedback card */
.feedback-card {
  background: #1e1b3a;
  border: 1px solid rgba(111,125,255,.25);
  border-radius: 14px;
  padding: 1rem;
  width: 268px;
  box-shadow: 0 8px 32px rgba(0,0,0,.45);
  color: #e8e6f8;
}

.card-close {
  float: right;
  background: none;
  border: none;
  color: rgba(255,255,255,.4);
  cursor: pointer;
  font-size: .85rem;
  line-height: 1;
  padding: 0;
  margin: -2px -2px 0 0;
}
.card-close:hover { color: #fff; }

.card-question {
  font-size: .82rem;
  font-weight: 600;
  color: rgba(255,255,255,.75);
  margin: .6rem 0 .4rem;
  clear: both;
}
.card-question:first-of-type { margin-top: 0; }

/* 👍 👎 buttons */
.helpful-row { display: flex; gap: .5rem; margin-bottom: .75rem; }
.helpful-btn {
  flex: 1;
  padding: .35rem 0;
  border-radius: 8px;
  border: 1px solid rgba(255,255,255,.12);
  background: rgba(255,255,255,.06);
  font-size: 1.1rem;
  cursor: pointer;
  transition: background .15s, border-color .15s;
}
.helpful-btn:hover { background: rgba(111,125,255,.2); border-color: rgba(111,125,255,.4); }
.helpful-btn.selected { background: rgba(111,125,255,.35); border-color: #6F7DFF; }

/* Textarea */
.feedback-textarea {
  width: 100%;
  box-sizing: border-box;
  background: rgba(255,255,255,.06);
  border: 1px solid rgba(255,255,255,.12);
  border-radius: 8px;
  color: #fff;
  font-size: .82rem;
  padding: .5rem .6rem;
  resize: none;
  outline: none;
  margin-bottom: .75rem;
}
.feedback-textarea::placeholder { color: rgba(255,255,255,.3); }
.feedback-textarea:focus { border-color: rgba(111,125,255,.5); }

/* Action buttons */
.card-actions { display: flex; gap: .5rem; justify-content: flex-end; }
.skip-btn {
  background: none;
  border: none;
  color: rgba(255,255,255,.45);
  font-size: .82rem;
  cursor: pointer;
  padding: .35rem .6rem;
  border-radius: 6px;
}
.skip-btn:hover { color: #fff; background: rgba(255,255,255,.06); }
.submit-btn {
  background: linear-gradient(135deg, #6F7DFF 0%, #9D7BFF 100%);
  border: none;
  color: #fff;
  font-size: .82rem;
  font-weight: 600;
  padding: .35rem .85rem;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity .15s;
}
.submit-btn:disabled { opacity: .5; cursor: default; }
.submit-btn:not(:disabled):hover { opacity: .88; }

/* Thank you */
.thankyou {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem 1.25rem;
}
.thankyou-text { font-size: .95rem; font-weight: 600; color: #a5b4fc; }

/* Transitions */
.widget-fade-enter-active { transition: opacity .3s, transform .3s; }
.widget-fade-enter-from { opacity: 0; transform: translateY(8px); }

.card-slide-enter-active { transition: opacity .2s, transform .2s; }
.card-slide-leave-active { transition: opacity .15s, transform .15s; }
.card-slide-enter-from { opacity: 0; transform: translateY(10px) scale(.97); }
.card-slide-leave-to { opacity: 0; transform: translateY(6px) scale(.98); }
</style>
