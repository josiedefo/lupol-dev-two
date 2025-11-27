<!-- /src/pages/Home.vue -->
<template>
  <div class="relative min-h-screen overflow-hidden text-white bg-lupol-950">
    <div class="absolute inset-0 -z-10 bg-gradient-to-b from-[#2a1d79] via-[#2b1a6b] to-[#160e3a]" aria-hidden="true"></div>
    <div class="absolute inset-x-0 top-0 h-[60vh] -z-10"
         style="background: radial-gradient(60% 60% at 50% 0%, rgba(122,91,255,0.35) 0%, rgba(122,91,255,0.05) 50%, rgba(0,0,0,0) 100%);" aria-hidden="true" />

    <header class="container mx-auto px-6 py-6">
      <nav class="flex items-center justify-between gap-6">
        <div class="flex items-center gap-3">
          <LogoMark class="w-10 h-10" />
          <span class="font-extrabold tracking-tight text-xl">LUPOL</span>
        </div>

        <ul class="hidden md:flex items-center gap-8 text-sm text-white/80">
          <li><a class="hover:text-white transition" href="#">Features</a></li>
          <li><a class="hover:text-white transition" href="#">How it Works</a></li>
          <li><a class="hover:text-white transition" href="#">Community</a></li>
          <li><a class="hover:text-white transition" href="#">Sign In</a></li>
        </ul>

        <button
          @click="goAssistant"
          class="inline-flex items-center justify-center rounded-2xl bg-[#6F7DFF] px-5 py-3 font-semibold text-[15px] shadow-[0_8px_24px_rgba(111,125,255,0.45)] hover:shadow-[0_12px_28px_rgba(111,125,255,0.6)] active:translate-y-[1px] transition"
          aria-label="Open Career Chat Assistant"
        >
          Get Started Free
          <svg class="ml-2 h-4 w-4" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
            <path d="M7 4l6 6-6 6" />
          </svg>
        </button>
      </nav>
    </header>

    <main class="container mx-auto px-6 pt-6 pb-24 md:pb-36">
      <div class="inline-flex items-center gap-3 rounded-full bg-white/5 ring-1 ring-white/10 px-4 py-2 backdrop-blur">
        <span class="relative inline-flex h-2.5 w-2.5">
          <span class="absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75 animate-ping"></span>
          <span class="relative inline-flex h-2.5 w-2.5 rounded-full bg-emerald-400"></span>
        </span>
        <span class="text-sm text-white/90">Trusted by 5,000+ professionals</span>
      </div>

      <h1 class="mt-8 font-extrabold tracking-tight leading-[0.95]">
        <span class="block text-5xl sm:text-6xl md:text-7xl lg:text-8xl">Transition Your Career.</span>
        <span class="block text-5xl sm:text-6xl md:text-7xl lg:text-8xl text-white/90">Start Earning Faster.</span>
      </h1>

      <p class="mt-8 max-w-5xl text-lg sm:text-xl text-white/80">
        AI-powered career matching with geo-specific licensing roadmaps.
        Discover high-revenue pivots tailored to your skills.
      </p>
    </main>
  </div>
</template>

<script setup>
import { goAssistant } from '../viewState';

const LogoMark = {
  name: 'LogoMark',
  props: { class: { type: String, default: '' } },
  template: `
    <svg :class="class" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg" aria-label="LUPOL logo">
      <defs>
        <linearGradient id="g" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stop-color="#9D7BFF"/>
          <stop offset="100%" stop-color="#6F7DFF"/>
        </linearGradient>
      </defs>
      <circle cx="32" cy="32" r="28" fill="url(#g)"/>
      <path d="M27 20l13 12-13 12v-6l7-6-7-6v-6z" fill="white"/>
    </svg>
  `
};
</script>

<style scoped>
h1 { letter-spacing: -0.02em; }
</style>
