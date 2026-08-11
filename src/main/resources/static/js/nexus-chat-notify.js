// Conector WebSocket global — igual ao nexus-chat-socket.js, mas sem matchId e sem
// depender de estar na tela de chat. Roda em toda página autenticada (carregado pelo
// fragments/app-shell :: scripts) só pra ouvir /user/queue/chat-notification e manter
// o badge "Conversas" do menu atualizado em tempo real, sem precisar recarregar a página.
(function () {
  var badgeSelector = '.chat-unread-badge';

  function updateBadges(count) {
    document.querySelectorAll(badgeSelector).forEach(function (badge) {
      if (count > 0) {
        badge.textContent = count > 99 ? '99+' : count;
        badge.style.display = '';
      } else {
        badge.style.display = 'none';
      }
    });
  }

  // Sem badge nenhum na página (não deveria acontecer, já que fica no menu lateral
  // compartilhado, mas é uma checagem barata) — nem vale abrir a conexão.
  if (!document.querySelector(badgeSelector)) return;

  fetch('/app-api/chat/ws-token')
    .then(function (r) { return r.ok ? r.json() : null; })
    .then(function (data) {
      if (!data || !data.token) return; // não logado — nada a fazer

      var client = new StompJs.Client({
        webSocketFactory: function () {
          return new SockJS(data.wsBaseUrl + '/ws');
        },
        connectHeaders: {
          Authorization: 'Bearer ' + data.token
        },
        reconnectDelay: 5000,

        onConnect: function () {
          client.subscribe('/user/queue/chat-notification', function (frame) {
            var payload = JSON.parse(frame.body);
            if (typeof payload.unreadCount === 'number') {
              updateBadges(payload.unreadCount);
            }
          });
        }

        // Sem tratamento de erro aqui de propósito: essa conexão é só um "extra" pra
        // deixar o badge em tempo real. Se falhar ou cair, o badge continua funcionando
        // do jeito que já funcionava antes (via loadChatUnreadTotal() no load da página).
      });

      client.activate();
      window.addEventListener('beforeunload', function () { client.deactivate(); });
    })
    .catch(function () {});
})();
