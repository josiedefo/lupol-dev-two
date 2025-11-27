<!-- /src/App.vue -->
<template>
  <component :is="Current" />
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { view, initFromURL } from './viewState';
import Home from './components/Home.vue';
import ChatAssistant from './components/ChatAssistant.vue';

const views = { home: Home, assistant: ChatAssistant };
const Current = computed(() => views[view.value]);

onMounted(() => initFromURL());
</script>
