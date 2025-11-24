<template>
  <!--div>
    <a href="https://vite.dev" target="_blank">
      <img src="/vite.svg" class="logo" alt="Vite logo" />
    </a>
    <a href="https://vuejs.org/" target="_blank">
      <img src="./assets/vue.svg" class="logo vue" alt="Vue logo" />
    </a>
  </div>
  <HelloWorld msg="Vite + Vue" /-->
  <div>
    <h1>My Spring + Vue App</h1>

    <button @click="loadGreeting">
      Load greeting from backend
    </button>

    <p v-if="loading">Loading...</p>
    <p v-else-if="error" style="color:red;">Error: {{ error }}</p>
    <p v-else-if="greeting">
      Backend says: <strong>{{ greeting }}</strong>
    </p>
  </div>
</template>

<script setup>
//import HelloWorld from './components/HelloWorld.vue'
import { ref } from 'vue'

const greeting = ref('')
const loading = ref(false)
const error = ref('')

async function loadGreeting() {
  loading.value = true
  error.value = ''
  greeting.value = ''

  try {
    const res = await fetch('/career/chat') // goes through Vite proxy
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`)
    }
    // if your Spring endpoint returns plain text:
    greeting.value = await res.text()
    // if it returns JSON like { message: "Hello" }, use:
    // const data = await res.json()
    // greeting.value = data.message
    console.log('Received greeting:', greeting.value);
  } catch (e) {
    error.value = e.message || 'Unknown error'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
