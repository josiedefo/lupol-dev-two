<!-- /src/components/TestimonialCard.vue -->
<template>
  <blockquote class="rounded-2xl bg-white text-slate-900 p-5 shadow-xl">
    <div class="flex items-center gap-1 text-[#F59E0B] mb-2" aria-hidden="true">
      <span v-for="i in 5" :key="i">★</span>
    </div>
    <p class="text-sm leading-relaxed">"{{ text }}"</p>
    <footer class="mt-3 text-xs text-slate-500">{{ author }}</footer>
  </blockquote>
</template>

<script setup>
import { defineProps } from 'vue'
defineProps({
  text: { type: String, required: true },
  author: { type: String, required: true }
})
</script>
