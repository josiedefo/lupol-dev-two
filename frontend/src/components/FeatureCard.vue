<!-- /src/components/FeatureCard.vue -->
<template>
  <article class="rounded-2xl bg-white text-slate-900 p-5 shadow-xl">
    <div class="h-10 w-10 rounded-xl bg-[#EAE9FF] flex items-center justify-center mb-3">
      <Icon :name="icon" />
    </div>
    <h3 class="font-semibold text-lg mb-1">{{ title }}</h3>
    <p class="text-sm text-slate-600 leading-relaxed">{{ text }}</p>
  </article>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  icon: { type: String, required: true },
  title: { type: String, required: true },
  text:  { type: String, required: true }
})

// lightweight inline icon set
const Icon = {
  props: { name: { type: String, required: true } },
  template: `
    <svg v-if="name==='sparkles'" class="h-5 w-5 text-[#6F7DFF]" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l1.8 4.3L18 8l-4.2 1.7L12 14l-1.8-4.3L6 8l4.2-1.7L12 2zM20 14l.9 2.1L23 17l-2.1.9L20 20l-.9-2.1L17 17l2.1-.9L20 14zM5 15l.7 1.6L7 17l-1.3.4L5 19l-.7-1.6L3 17l1.3-.4L5 15z"/></svg>
    <svg v-else-if="name==='chart'" class="h-5 w-5 text-[#6F7DFF]" viewBox="0 0 24 24" fill="currentColor"><path d="M3 3h2v18H3V3zm4 10h2v8H7v-8zm4-6h2v14h-2V7zm4 4h2v10h-2V11zm4-8h2v18h-2V3z"/></svg>
    <svg v-else-if="name==='pin'" class="h-5 w-5 text-[#6F7DFF]" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2a7 7 0 00-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 00-7-7zm0 9.5A2.5 2.5 0 1112 6a2.5 2.5 0 010 5.5z"/></svg>
    <svg v-else class="h-5 w-5 text-[#6F7DFF]" viewBox="0 0 24 24" fill="currentColor"><path d="M13 2a1 1 0 011 1v6h5a1 1 0 011 1v2h-7V3a1 1 0 011-1zM4 13h7v8H5a1 1 0 01-1-1v-7z"/></svg>
  `
}
</script>
