/**
 * nexus-chat.js — Lógica da tela de chat individual (/chat/{matchId}).
 * Depende de nexus-chat-socket.js (createChatSocket) e da variável
 * global NEXUS_CHAT injetada pelo template chat/chat.html.
 */
(function () {
  var cfg = window.NEXUS_CHAT || {};
  // Fonte de verdade para o estado do match — injetada pelo servidor
  // (Opção B: histórico sempre acessível, envio bloqueado se encerrado)
  var matchActive = window.NEXUS_MATCH_ACTIVE !== false;
  var messagesEl = document.getElementById('chatMessages');
  var inputEl = document.getElementById('chatInput');
  var sendBtn = document.getElementById('chatSendBtn');
  var counterEl = document.getElementById('chatCharCounter');
  var wsStatusBadge = document.getElementById('wsStatusBadge');
  var wsErrorBanner = document.getElementById('wsErrorBanner');
  var wsErrorText = document.getElementById('wsErrorText');

  var MAX_LEN = 2000;
  var chat = null;

  function isSameDay(a, b) {
    return a.getFullYear() === b.getFullYear()
        && a.getMonth() === b.getMonth()
        && a.getDate() === b.getDate();
  }

  function formatTime(sentAtStr) {
    var date = new Date(sentAtStr);
    var now = new Date();
    var hhmm = String(date.getHours()).padStart(2, '0') + ':' + String(date.getMinutes()).padStart(2, '0');
    return isSameDay(date, now) ? hhmm : ('ontem ' + hhmm);
  }

  function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  function renderMessage(message) {
    var mine = String(message.senderId) === String(cfg.myUserId);

    var row = document.createElement('div');
    row.className = 'nexus-chat-row ' + (mine ? 'mine' : 'theirs');

    if (!mine) {
      var avatar = document.createElement('img');
      avatar.className = 'nexus-chat-avatar';
      avatar.src = message.senderPhotoUrl || '';
      avatar.alt = message.senderName || '';
      avatar.onerror = function () { this.style.visibility = 'hidden'; };
      row.appendChild(avatar);
    }

    var wrap = document.createElement('div');
    wrap.className = 'nexus-chat-bubble-wrap';

    if (!mine) {
      var nameEl = document.createElement('div');
      nameEl.className = 'nexus-chat-sender-name';
      nameEl.textContent = message.senderName || '';
      wrap.appendChild(nameEl);
    }

    var bubble = document.createElement('div');
    bubble.className = 'nexus-chat-bubble';
    bubble.innerHTML = escapeHtml(message.content).replace(/\n/g, '<br>');
    wrap.appendChild(bubble);

    var meta = document.createElement('div');
    meta.className = 'nexus-chat-meta';
    meta.textContent = formatTime(message.sentAt);
    if (mine && message.read) {
      meta.textContent += ' ✓✓';
    }
    wrap.appendChild(meta);

    row.appendChild(wrap);
    messagesEl.appendChild(row);
  }

  function scrollToBottom() {
    messagesEl.scrollTop = messagesEl.scrollHeight;
  }

  function loadHistory() {
    fetch('/app-api/chat/' + cfg.matchId + '/messages')
      .then(function (res) {
        if (!res.ok) throw new Error('access-denied');
        return res.json();
      })
      .then(function (messages) {
        messagesEl.innerHTML = '';
        messages.forEach(renderMessage);
        scrollToBottom();
      })
      .catch(function () {
        window.location.href = cfg.matchesUrl;
      });
  }

  function applyInputDisabledState() {
    var disabled = !matchActive;
    inputEl.disabled = disabled;
    sendBtn.disabled = disabled;
    inputEl.classList.toggle('chat-input--disabled', disabled);
    sendBtn.classList.toggle('chat-input--disabled', disabled);
  }

  function updateConnectionUi() {
    if (!matchActive) {
      applyInputDisabledState();
      return;
    }
    if (!chat) return;
    var connected = chat.isConnected;
    wsStatusBadge.style.display = connected ? 'none' : '';
    sendBtn.disabled = !connected;
    inputEl.disabled = false;
  }

  function showError(message) {
    wsErrorText.textContent = message || 'Sem conexão. Tentando reconectar...';
    wsErrorBanner.style.display = '';
  }

  function hideError() {
    wsErrorBanner.style.display = 'none';
  }

  function connectSocket() {
    chat = createChatSocket(cfg.matchId, {
      wsBaseUrl: cfg.wsBaseUrl,
      onConnected: function () {
        hideError();
        updateConnectionUi();
      },
      onMessageReceived: function (message) {
        renderMessage(message);
        scrollToBottom();
      },
      onError: function (errorMessage) {
        showError(errorMessage);
      }
    });

    updateConnectionUi();

    // Enquanto não conectar, mostra "Conectando..." e reavalia periodicamente
    var pollConnection = setInterval(function () {
      updateConnectionUi();
      if (!chat.isConnected) {
        showError('Sem conexão. Tentando reconectar...');
      } else {
        hideError();
      }
    }, 3000);

    window.addEventListener('beforeunload', function () {
      clearInterval(pollConnection);
    });
  }

  function updateCounter() {
    var len = inputEl.value.length;
    counterEl.textContent = len + '/' + MAX_LEN;
    counterEl.classList.toggle('over-limit', len >= MAX_LEN);
  }

  function autoGrow() {
    inputEl.style.height = 'auto';
    inputEl.style.height = Math.min(inputEl.scrollHeight, 120) + 'px';
  }

  function sendCurrentMessage() {
    var content = inputEl.value.trim();
    if (!content || !matchActive || !chat || !chat.isConnected) return;

    chat.sendMessage(content.slice(0, MAX_LEN));
    inputEl.value = '';
    updateCounter();
    autoGrow();
    inputEl.focus();
  }

  if (matchActive) {
    inputEl.addEventListener('input', function () {
      updateCounter();
      autoGrow();
    });

    inputEl.addEventListener('keydown', function (e) {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendCurrentMessage();
      }
    });

    sendBtn.addEventListener('click', sendCurrentMessage);
  }

  document.addEventListener('DOMContentLoaded', function () {
    updateCounter();
    loadHistory();
    applyInputDisabledState();

    // Se o match está encerrado, o histórico ainda é buscado acima,
    // mas a conexão STOMP nunca é iniciada — só leitura.
    if (matchActive) {
      connectSocket();
    }
  });
})();
