document.addEventListener("DOMContentLoaded", () => {
  console.log("app.js загружен");

  const startBtn = document.getElementById('start');
  const cancelBtn = document.getElementById('cancel');
  console.log("startBtn =", startBtn, "cancelBtn =", cancelBtn);

  // если кнопки не найдены — дальше нет смысла
  if (!startBtn || !cancelBtn) {
    console.error("Кнопки не найдены. Проверь id='start' и id='cancel' в index.html");
    return;
  }

  // Навешиваем клики СРАЗУ — даже если SockJS/Stomp не загрузились
  startBtn.addEventListener('click', () => {
    console.log("НАЖАЛИ START");

    if (typeof SockJS === "undefined" || typeof Stomp === "undefined") {
      console.error("SockJS/Stomp не загрузились (CDN таймаут). Нужен локальный fallback.");
      return;
    }

    const duration = +document.getElementById('duration').value;
    const interval = +document.getElementById('interval').value;

    stomp.send('/app/timer/start', {}, JSON.stringify({ duration, interval }));
  });

  cancelBtn.addEventListener('click', () => {
    console.log("НАЖАЛИ CANCEL");
    if (typeof stomp !== "undefined") {
      stomp.send('/app/timer/cancel');
    }
  });

  // --- WebSocket часть ---
  if (typeof SockJS === "undefined" || typeof Stomp === "undefined") {
    console.error("SockJS/Stomp НЕ загружены — WebSocket не будет работать");
    return;
  }

  const socket = new SockJS('/ws');
  const stomp = Stomp.over(socket);
  let total = 900;

  stomp.connect({}, () => {
    console.log("STOMP connected");

    stomp.subscribe('/user/queue/timer', msg => {
      const data = JSON.parse(msg.body);
      total = data.total;
      updateTimer(data.remaining, total);
      if (data.finished) setTimeout(() => location.reload(), 2000);
    });

    stomp.subscribe('/user/queue/sound', msg => {
      const sound = msg.body;
      if (sound === 'start') document.getElementById('start-sound')?.play();
      if (sound === 'tick') document.getElementById('tick-sound')?.play();
      if (sound === 'end') document.getElementById('end-sound')?.play();
    });
  });

  function updateTimer(seconds, totalSeconds) {
    const m = String(Math.floor(seconds / 60)).padStart(2, '0');
    const s = String(seconds % 60).padStart(2, '0');
    document.getElementById('timer').textContent = `${m}:${s}`;
    document.getElementById('progress').value = ((totalSeconds - seconds) / totalSeconds) * 100;
  }

  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/sw.js');
  }
});
