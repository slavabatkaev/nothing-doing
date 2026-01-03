const CACHE_NAME = 'nothing-doing-v1';
const urlsToCache = [
  '/',
  '/styles.css',
  '/icon.png',
  '/sounds/start.mp3',
  '/sounds/end.mp3',
  '/sounds/tick.mp3'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(urlsToCache))
  );
});

self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => response || fetch(event.request))
  );
});