// /src/analytics/client.ts
// Minimal, privacy-conscious visit beacons + returning-visitor detection

/** RFC4122 v4 */
function uuidv4(): string {
  // crypto API for randomness
  return ([1e7] as any + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, (c: any) =>
    (c ^ (crypto.getRandomValues(new Uint8Array(1))[0] & (15 >> (c / 4)))).toString(16)
  );
}

function getCookie(name: string): string | null {
  const m = document.cookie.match('(?:^|; )' + encodeURIComponent(name) + '=([^;]*)');
  return m ? decodeURIComponent(m[1]) : null;
}

function setCookie(name: string, value: string, days = 400) {
  const d = new Date();
  d.setTime(d.getTime() + days * 864e5);
  document.cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}; Expires=${d.toUTCString()}; Path=/; SameSite=Lax`;
}

export function ensureVisitorId(): string {
  let id = getCookie('visitor_id') || localStorage.getItem('visitor_id') || '';
  if (!id) {
    id = uuidv4();
    setCookie('visitor_id', id);
    try { localStorage.setItem('visitor_id', id); } catch {}
  } else if (!getCookie('visitor_id')) {
    setCookie('visitor_id', id);
  }
  return id;
}

function readUTM(): Record<string, string> {
  const p = new URL(location.href).searchParams;
  const keys = ['utm_source','utm_medium','utm_campaign','utm_term','utm_content'];
  const out: Record<string, string> = {};
  keys.forEach(k => { const v = p.get(k); if (v) out[k] = v; });
  return out;
}

/** Fire-and-forget analytics beacon */
export function sendVisitBeacon() {
  const visitorId = ensureVisitorId();
  const payload = {
    visitorId,
    path: location.pathname + location.search,
    referrer: document.referrer || '',
    tz: Intl.DateTimeFormat().resolvedOptions().timeZone || '',
    lang: navigator.language || '',
    screen: `${window.screen.width}x${window.screen.height}`,
    utm: readUTM(),
    // Do NOT send IP; server derives it from request
  };

  const blob = new Blob([JSON.stringify(payload)], { type: 'application/json' });
  const ok = navigator.sendBeacon('/lupoldevtwo/api/analytics/visit', blob);
  console.log('Analytics beacon sent, ok=', ok);
  if (!ok) {
    // fallback
    fetch('/lupoldevtwo/api/analytics/visit', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload), keepalive: true }).catch(() => {});
  }
}
