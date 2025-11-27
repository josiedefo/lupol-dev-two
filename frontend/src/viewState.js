// /src/viewState.js
import { ref } from 'vue';

/** Global view state (no router) */
export const view = ref(/** @type {'home' | 'assistant'} */('home'));

export function goHome() {
  view.value = 'home';
  updateURL('home');
}

export function goAssistant() {
  view.value = 'assistant';
  updateURL('assistant');
}

/** Read initial view from URL on app start */
export function initFromURL() {
  const v = new URL(location.href).searchParams.get('view');
  if (v === 'assistant') view.value = 'assistant';
  else view.value = 'home';
}

/** Keep the URL shareable without reloading */
function updateURL(v) {
  const url = new URL(location.href);
  if (v === 'assistant') url.searchParams.set('view', 'assistant');
  else url.searchParams.delete('view');
  history.replaceState({}, '', url);
}
