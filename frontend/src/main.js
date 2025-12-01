// /src/main.js
import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import { sendVisitBeacon } from './analytics/client';

sendVisitBeacon();
createApp(App).mount('#app');