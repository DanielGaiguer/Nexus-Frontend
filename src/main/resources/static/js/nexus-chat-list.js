(function () {
  var container = document.getElementById('chatListContainer');
  var emptyState = document.getElementById('chatListEmpty');
  var searchInput = document.getElementById('chatSearchInput');

  var POLL_INTERVAL_MS = 30000;
  var allChats = [];

  function isSameDay(a, b) {
    return a.getFullYear() === b.getFullYear()
        && a.getMonth() === b.getMonth()
        && a.getDate() === b.getDate();
  }

  function isYesterday(date, now) {
    var yesterday = new Date(now);
    yesterday.setDate(now.getDate() - 1);
    return isSameDay(date, yesterday);
  }

  function formatListTime(dateStr) {
    if (!dateStr) return '';
    var date = new Date(dateStr);
    var now = new Date();
    if (isSameDay(date, now)) {
      return String(date.getHours()).padStart(2, '0') + ':' + String(date.getMinutes()).padStart(2, '0');
    }
    if (isYesterday(date, now)) {
      return 'ontem';
    }
    return String(date.getDate()).padStart(2, '0') + '/' + String(date.getMonth() + 1).padStart(2, '0');
  }

  function truncate(text, max) {
    if (!text) return 'Nenhuma mensagem ainda';
    return text.length > max ? text.slice(0, max) + '…' : text;
  }

  function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str || '';
    return div.innerHTML;
  }

  function initialsAvatarHtml(name) {
    var letter = name ? name.charAt(0).toUpperCase() : '?';
    var hash = 0;
    for (var i = 0; i < (name || '').length; i++) {
      hash = (name.charCodeAt(i) + ((hash << 5) - hash)) | 0;
    }
    var hue = ((hash % 360) + 360) % 360;
    return '<div class="nexus-chat-item-avatar nexus-avatar-initials" ' +
        'style="background:hsl(' + hue + ',50%,40%)">' + letter + '</div>';
  }

  function renderChat(chat) {
    var isEnded = chat.matchActive === false || chat.daysUntilExpiration === -1;
    var isExpiringSoon = !isEnded && chat.daysUntilExpiration != null && chat.daysUntilExpiration <= 7;

    var item = document.createElement('div');
    item.className = 'nexus-chat-item' + (isEnded ? ' chat-item--encerrado' : '');
    item.addEventListener('click', function () {
      window.location.href = '/chat/' + chat.matchId;
    });

    var avatarHtml = chat.otherPartyPhotoUrl
      ? '<img class="nexus-chat-item-avatar" src="' + chat.otherPartyPhotoUrl + '" alt="Foto">'
      : initialsAvatarHtml(chat.otherPartyName);

    var timeHtml = isEnded
      ? '<span class="nexus-chat-item-badge-ended">Encerrado</span>'
      : '<span class="nexus-chat-item-time">' + formatListTime(chat.lastMessageAt) + '</span>';

    var unreadHtml = (chat.unreadCount > 0 && !isEnded)
      ? '<span class="nexus-chat-item-unread">' + (chat.unreadCount > 99 ? '99+' : chat.unreadCount) + '</span>'
      : '';

    var expiringHtml = isExpiringSoon
      ? '<div class="nexus-chat-item-expiring">Encerra em ' + chat.daysUntilExpiration + ' dia(s)</div>'
      : '';

    item.innerHTML =
      avatarHtml +
      '<div class="nexus-chat-item-body">' +
        '<div class="nexus-chat-item-top">' +
          '<div><span class="nexus-chat-item-name">' + escapeHtml(chat.otherPartyName) + '</span>' +
          '<span class="nexus-chat-item-project">' + escapeHtml(chat.projectTitle) + '</span></div>' +
          timeHtml +
        '</div>' +
        '<div class="nexus-chat-item-bottom">' +
          '<span class="nexus-chat-item-preview">' + escapeHtml(truncate(chat.lastMessage, 50)) + '</span>' +
          unreadHtml +
        '</div>' +
        expiringHtml +
      '</div>';

    container.appendChild(item);
  }

  function renderList(chats) {
    container.innerHTML = '';
    if (chats.length === 0) {
      emptyState.style.display = allChats.length === 0 ? '' : 'none';
      if (allChats.length > 0) {
        var noResult = document.createElement('div');
        noResult.className = 'nexus-chatlist-empty';
        noResult.textContent = 'Nenhuma conversa encontrada.';
        container.appendChild(noResult);
      }
      return;
    }
    emptyState.style.display = 'none';
    chats.forEach(renderChat);
  }

  function applyFilter() {
    var term = searchInput.value.trim().toLowerCase();
    if (!term) {
      renderList(allChats);
      return;
    }
    var filtered = allChats.filter(function (chat) {
      return (chat.otherPartyName || '').toLowerCase().indexOf(term) !== -1
          || (chat.projectTitle || '').toLowerCase().indexOf(term) !== -1;
    });
    renderList(filtered);
  }

  function loadChats() {
    fetch('/app-api/chat/matches')
      .then(function (res) {
        if (!res.ok) throw new Error('failed');
        return res.json();
      })
      .then(function (chats) {
        chats.sort(function (a, b) {
          if (!a.lastMessageAt) return 1;
          if (!b.lastMessageAt) return -1;
          return new Date(b.lastMessageAt) - new Date(a.lastMessageAt);
        });
        allChats = chats;
        applyFilter();
      })
      .catch(function () {
      });
  }

  searchInput.addEventListener('input', applyFilter);

  document.addEventListener('DOMContentLoaded', function () {
    loadChats();
    setInterval(loadChats, POLL_INTERVAL_MS);
  });
})();
