const CACHE_NAME = 'nothing-doing-v3';

const ASSETS = [
  '/',
  '/manifest.json',
  '/icon-192.png',
  '/icon-512.png',
  '/js/app.js',
  '/sounds/start.mp3',
  '/sounds/end.mp3',
  '/sounds/tick.mp3'
];

// Установка — кешируем ассеты
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS))
  );
  self.skipWaiting();
});

// Активация — чистим старый кеш
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE_NAME).map((k) => caches.delete(k)))
    )
  );
  self.clients.claim();
});

// Fetch: HTML — network-first, ассеты — cache-first
self.addEventListener('fetch', (event) => {
  const req = event.request;
  const url = new URL(req.url);

  // только наш домен
  if (url.origin !== self.location.origin) return;

  // HTML: сначала сеть, потом кеш
  if (req.mode === 'navigate') {
    event.respondWith(
      fetch(req).then((res) => {
        const copy = res.clone();
        caches.open(CACHE_NAME).then((cache) => cache.put('/', copy));
        return res;
      }).catch(() => caches.match('/'))
    );
    return;
  }

  // Остальное: сначала кеш
  event.respondWith(
    caches.match(req).then((cached) => cached || fetch(req))
  );
});
