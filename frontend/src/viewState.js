// /src/viewState.js
import { ref } from 'vue';

/** Global view state (no router) */
export const view = ref(/** @type {'home' | 'assistant' | 'admin'} */('home'));

export function goHome() {
  view.value = 'home';
  updateURL('home');
}

export function goAssistant() {
  view.value = 'assistant';
  updateURL('assistant');
}

export function goAdmin() {
  view.value = 'admin';
  updateURL('admin');
}

/** Read initial view from URL on app start */
export function initFromURL() {
  const v = new URL(location.href).searchParams.get('view');
  if (v === 'assistant') view.value = 'assistant';
  else if (v === 'admin') view.value = 'admin';
  else view.value = 'home';
}

/** Keep the URL shareable without reloading */
function updateURL(v) {
  const url = new URL(location.href);
  if (v === 'home') url.searchParams.delete('view');
  else url.searchParams.set('view', v);
  history.replaceState({}, '', url);
}
