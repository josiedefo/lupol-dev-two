<!-- /src/pages/Home.vue (imports + usage cleaned) -->
<template>
  <div class="relative min-h-full overflow-hidden text-white bg-lupol-950">
    <div class="absolute inset-0 -z-10 bg-gradient-to-b from-[#2a1d79] via-[#2b1a6b] to-[#160e3a]" aria-hidden="true"></div>
    <div class="absolute inset-x-0 top-0 h-[60vh] -z-10"
         style="background: radial-gradient(60% 60% at 50% 0%, rgba(122,91,255,0.35) 0%, rgba(122,91,255,0.05) 50%, rgba(0,0,0,0) 100%);" aria-hidden="true" />

    <header class="container mx-auto px-6 py-6">
      <nav class="flex items-center justify-between gap-6">
        <div class="flex items-center gap-3">
          <LogoMark class="w-10 h-10" />
          <span class="font-extrabold tracking-tight text-xl">LUPOL</span>
        </div>
        <!--ul class="hidden md:flex items-center gap-8 text-sm text-white/80">
          <li><a class="hover:text-white transition" href="#">Empowering career<br class="hidden xl:block" /> transformations</a></li>
          <li><a class="hover:text-white transition" href="#">Features</a></li>
          <li><a class="hover:text-white transition" href="#">How it<br class="hidden xl:block" /> Works</a></li>
          <li><a class="hover:text-white transition" href="#">Community</a></li>
          <li><a class="hover:text-white transition" href="#">Sign In</a></li>
        </ul-->
        <button @click="goAssistant" class="inline-flex items-center justify-center rounded-2xl bg-[#6F7DFF] px-5 py-3 font-semibold text-[15px] shadow-[0_8px_24px_rgba(111,125,255,0.45)] hover:shadow-[0_12px_28px_rgba(111,125,255,0.6)] active:translate-y-[1px] transition">
          Get Started With Career Matching
          <svg class="ml-2 h-4 w-4" viewBox="0 0 20 20" fill="currentColor"><path d="M7 4l6 6-6 6"/></svg>
        </button>
      </nav>
    </header>

    <main class="container mx-auto px-6 pt-6 pb-16 md:pb-24">
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

      <p class="mt-8 max-w-4xl text-lg sm:text-xl text-white/80">
        AI-powered career matching with geo-specific licensing roadmaps.
        Discover high-revenue pivots tailored to your skills.
      </p>
    </main>

    <!-- Features -->
    <section class="container mx-auto px-6 pb-10">
      <h2 class="text-center text-3xl md:text-4xl font-extrabold tracking-tight">Everything You Need to Pivot</h2>
      <p class="text-center mt-2 text-white/80">Intelligent tools that guide you from discovery to your first paycheck</p>

      <div class="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        <FeatureCard
          icon="sparkles"
          title="AI Career Matching"
          text="Our intelligent system evaluates your skills and experience to identify high-probability career transitions. Get personalized recommendations ranked by salary potential, time-to-first-paycheck, and difficulty level."
        />
        <FeatureCard
          icon="chart"
          title="Revenue Insights"
          text="Access comprehensive salary data from entry to senior levels, including bonuses and benefits. See location-adjusted earnings and realistic timelines for your first paycheck."
        />
        <FeatureCard
          icon="pin"
          title="Licensing Intelligence"
          text="Navigate licensing requirements with detailed state-specific regulations, exam schedules, and costs. Get step-by-step guidance to ensure compliance and avoid costly mistakes."
        />
        <FeatureCard
          icon="clock"
          title="Action Roadmaps"
          text="Transform goals into reality with structured 90-day plans including weekly tasks, courses, and milestones. Track your progress toward certification and employment."
        />
      </div>
    </section>

    <!-- How it works -/->
    <section class="container mx-auto px-6 py-12">
      <h2 class="text-center text-3xl md:text-4xl font-extrabold tracking-tight">How it works</h2>
      <p class="text-center mt-2 text-white/80">
        Discover how LUPOL transforms your career goals into reality with AI-powered insights and actionable roadmaps.
      </p>
    </section-->

    <!-- Testimonials -/->
    <section class="container mx-auto px-6 pb-14">
      <h2 class="text-center text-3xl md:text-4xl font-extrabold tracking-tight">What professionals say</h2>
      <div class="mt-8 grid gap-6 md:grid-cols-3">
        <TestimonialCard text="Within 90 days I had an offer in a new field. The roadmap kept me accountable." author="Amara • Data Analyst (career pivot from Healthcare)" />
        <TestimonialCard text="Within 90 days I had an offer in a new field. The roadmap kept me accountable." author="Amara • Data Analyst (career pivot from Healthcare)" />
        <TestimonialCard text="Within 90 days I had an offer in a new field. The roadmap kept me accountable." author="Amara • Data Analyst (career pivot from Healthcare)" />
      </div>
    </section-->

    <!-- Bottom CTA -->
    <section class="container mx-auto px-6 pb-20">
      <div class="rounded-3xl bg-white/5 ring-1 ring-white/10 p-8 md:p-10 text-center">
        <h2 class="text-3xl md:text-4xl font-extrabold tracking-tight">Ready to Transform Your Career?</h2>
        <p class="mt-2 text-white/80">Join 5,000+ professionals who've discovered their ideal career pivot.</p>

        <button @click="goAssistant"
          class="mt-6 inline-flex items-center justify-center rounded-2xl bg-[#6F7DFF] px-6 py-3 font-semibold shadow-[0_8px_24px_rgba(111,125,255,0.45)] hover:shadow-[0_12px_28px_rgba(111,125,255,0.6)] active:translate-y-[1px] transition">
          Find My Career Match
          <svg class="ml-2 h-4 w-4" viewBox="0 0 20 20" fill="currentColor"><path d="M7 4l6 6-6 6"/></svg>
        </button>

        <div class="mt-2 text-xs text-white/70">Free account • No credit card required</div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { goAssistant } from '../viewState'
import FeatureCard from '../components/FeatureCard.vue'
import TestimonialCard from '../components/TestimonialCard.vue'
import { defineComponent } from 'vue'

const LogoMark = defineComponent({
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
})
</script>

<style scoped>
h1 { letter-spacing: -0.02em; }
</style>
