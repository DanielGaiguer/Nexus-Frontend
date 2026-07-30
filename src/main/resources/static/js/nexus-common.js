
async function nexusFetch(url, options = {}) {
    const fullUrl = url.startsWith('/app-api') ? url : '/app-api' + url;
    const defaultHeaders = { 'Content-Type': 'application/json' };
    const mergedOptions = {
        ...options,
        headers: { ...defaultHeaders, ...(options.headers || {}) }
    };
    try {
        const res = await fetch(fullUrl, mergedOptions);
        const text = await res.text();
        let data;
        try { data = JSON.parse(text); } catch { data = text; }
        if (!res.ok) {
            const msg = (data && typeof data === 'object' && data.message)
                ? data.message
                : 'Erro na requisição (' + res.status + ')';
            nexusToast(msg, 'error');
            throw new Error(msg);
        }
        return data;
    } catch (err) {
        if (!err._toastShown) {
            nexusToast(err.message || 'Erro de conexão', 'error');
        }
        throw err;
    }
}

function nexusToast(message, type) {
    type = type || 'success';
    var container = document.getElementById('nexus-toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'nexus-toast-container';
        container.style.cssText = 'position:fixed;top:1rem;right:1rem;z-index:10900;display:flex;flex-direction:column;gap:0.5rem;';
        document.body.appendChild(container);
    }
    var toast = document.createElement('div');
    toast.className = 'toast show';
    toast.setAttribute('role', 'alert');
    var bgClass = type === 'error' ? 'bg-danger-lt text-danger' : 'bg-success-lt text-success';
    var icon = type === 'error' ? 'ti ti-alert-circle' : 'ti ti-check';
    toast.innerHTML =
        '<div class="toast-body d-flex align-items-center gap-2 ' + bgClass + '" style="border-radius:0.5rem;padding:0.75rem 1rem;">' +
            '<i class="' + icon + '"></i>' +
            '<span>' + message + '</span>' +
        '</div>';
    container.appendChild(toast);
    setTimeout(function() {
        toast.classList.remove('show');
        setTimeout(function() { toast.remove(); }, 300);
    }, 4000);
}

document.addEventListener('DOMContentLoaded', function() {
  if (typeof TomSelect === 'undefined') return;

  var origPositionDropdown = TomSelect.prototype.positionDropdown;
  TomSelect.prototype.positionDropdown = function() {
    origPositionDropdown.call(this);
    var rect = this.wrapper.getBoundingClientRect();
    this.dropdown.style.position = 'fixed';
    this.dropdown.style.top = (rect.bottom + 4) + 'px';
    this.dropdown.style.left = rect.left + 'px';
    this.dropdown.style.width = rect.width + 'px';
    this.dropdown.style.maxHeight = '260px';
    this.dropdown.style.height = 'auto';
    this.dropdown.style.overflow = 'hidden';
    var dc = this.dropdown.querySelector('.ts-dropdown-content');
    if (dc) {
      dc.style.maxHeight = '260px';
      dc.style.height = 'auto';
      dc.style.overflowY = 'auto';
    }
  };

  document.querySelectorAll('select.nexus-select').forEach(function(el) {
    var isMulti = el.hasAttribute('multiple');
    var placeholderText = isMulti
      ? (el.getAttribute('data-placeholder') || 'Todos os níveis')
      : (el.getAttribute('data-placeholder') || '');
    var ts = new TomSelect(el, {
      maxOptions: null,
      openOnFocus: true,
      allowEmptyOption: true,
      highlight: true,
      copyClassesToDropdown: false,
      dropdownParent: 'body',
      placeholder: placeholderText,
      plugins: isMulti ? ['remove_button'] : [],
      onDropdownOpen: function() {
        this.wrapper.classList.add('ts-dropdown-active');
      },
      onDropdownClose: function() {
        this.wrapper.classList.remove('ts-dropdown-active');
      }
    });
    syncPlaceholder(ts);
    ts.on('change', function() { syncPlaceholder(ts); });
  });

  function syncPlaceholder(ts) {
    var control = ts.wrapper.querySelector('.ts-control');
    var input = control ? control.querySelector('input') : null;
    if (!input) return;
    var hasItems = ts.items.length > 0;
    if (hasItems) {
      input.removeAttribute('placeholder');
      input.style.opacity = '0';
      input.style.width = '0';
      input.style.minWidth = '0';
      input.style.padding = '0';
    } else {
      input.setAttribute('placeholder', ts.settings.placeholder || '');
      input.style.opacity = '';
      input.style.width = '';
      input.style.minWidth = '';
      input.style.padding = '';
    }
  }
});

function initDeadlines() {
  document.querySelectorAll('[data-deadline]').forEach(function(el) {
    var raw      = el.dataset.deadline;
    var deadline = new Date(raw + 'T23:59:59');
    var now      = new Date();
    var diffMs   = deadline - now;
    var diffDays = Math.ceil(diffMs / (1000 * 60 * 60 * 24));

    el.innerHTML = '';

    if (diffMs < 0) {
      el.innerHTML =
        '<span style="color:#ef4444;font-size:0.75rem;font-weight:600">' +
        '<i class="ti ti-clock-x" style="font-size:0.7rem;margin-right:2px"></i>' +
        'Prazo encerrado</span>';
    } else if (diffDays <= 3) {
      el.innerHTML =
        '<span style="color:#ef4444;font-size:0.75rem;font-weight:600;' +
        'animation:urgentPulse 1.5s ease-in-out infinite">' +
        '<i class="ti ti-flame" style="font-size:0.7rem;margin-right:2px"></i>' +
        'Encerra em ' + diffDays + ' dia' + (diffDays !== 1 ? 's' : '') + '</span>';
    } else if (diffDays <= 7) {
      el.innerHTML =
        '<span style="color:#f59e0b;font-size:0.75rem;font-weight:600">' +
        '<i class="ti ti-clock-hour-4" style="font-size:0.7rem;margin-right:2px"></i>' +
        'Encerra em ' + diffDays + ' dias</span>';
    } else if (diffDays <= 30) {
      el.innerHTML =
        '<span style="color:#64748b;font-size:0.75rem">' +
        '<i class="ti ti-calendar" style="font-size:0.7rem;margin-right:2px"></i>' +
        'Encerra em ' + diffDays + ' dias</span>';
    } else {
      var opts = { day:'2-digit', month:'short', year:'numeric' };
      el.innerHTML =
        '<span style="color:#64748b;font-size:0.75rem">' +
        '<i class="ti ti-calendar" style="font-size:0.7rem;margin-right:2px"></i>' +
        deadline.toLocaleDateString('pt-BR', opts) + '</span>';
    }
  });
}

document.addEventListener('DOMContentLoaded', initDeadlines);

var notifPanelOpen = false;

var NOTIF_ICONS = {
  NEW_INVITE:                'ti-mail',
  NEW_INTEREST_RECEIVED:     'ti-send',
  MATCH_CONFIRMED:           'ti-heart-handshake',
  INVITE_REJECTED:           'ti-x',
  NEW_REVIEW_RECEIVED:       'ti-star',
  HIGH_SCORE_OPPORTUNITY:    'ti-sparkles',
  HIGH_SCORE_CANDIDATE:      'ti-sparkles',
  COMPANY_APPROVED:          'ti-shield-check',
  COMPANY_REJECTED:          'ti-shield-x',
  PROJECT_CLOSED:            'ti-lock',
  COMPLETE_YOUR_PROFILE:     'ti-user-exclamation',
  NEW_COMPANY_REGISTRATION:  'ti-building-plus'
};

var NOTIF_COLORS = {
  NEW_INVITE:                '#6b6eff',
  NEW_INTEREST_RECEIVED:     '#a78bfa',
  MATCH_CONFIRMED:           '#22c55e',
  INVITE_REJECTED:           '#ef4444',
  NEW_REVIEW_RECEIVED:       '#f59e0b',
  HIGH_SCORE_OPPORTUNITY:    '#67e8f9',
  HIGH_SCORE_CANDIDATE:      '#67e8f9',
  COMPANY_APPROVED:          '#22c55e',
  COMPANY_REJECTED:          '#ef4444',
  PROJECT_CLOSED:            '#f59e0b',
  COMPLETE_YOUR_PROFILE:     '#f59e0b',
  NEW_COMPANY_REGISTRATION:  '#6b6eff'
};

function toggleNotifPanel() {
  var panel = document.getElementById('notifPanel');
  notifPanelOpen = !notifPanelOpen;
  panel.style.display = notifPanelOpen ? 'block' : 'none';
  if (notifPanelOpen) loadNotifications();
}

document.addEventListener('click', function(e) {
  var wrapper = document.getElementById('notifBtnWrapper');
  if (notifPanelOpen && wrapper && !wrapper.contains(e.target)) {
    document.getElementById('notifPanel').style.display = 'none';
    notifPanelOpen = false;
  }
});

function loadNotifications() {
  fetch('/notifications')
  .then(function(r) { return r.ok ? r.json() : Promise.reject(r.status); })
  .then(function(data) {
    renderNotifications(data.notifications || []);
    updateBadge(data.unreadCount || 0);
  })
  .catch(function() {
    document.getElementById('notifList').innerHTML =
      '<div style="padding:1.5rem;text-align:center;color:#334155;font-size:0.875rem">' +
      'Não foi possível carregar as notificações.</div>';
  });
}

function renderNotifications(notifications) {
  var list = document.getElementById('notifList');
  if (!notifications.length) {
    list.innerHTML =
      '<div style="padding:2rem;text-align:center">' +
      '<i class="ti ti-bell-off" style="font-size:2rem;color:#334155;display:block;margin-bottom:0.5rem"></i>' +
      '<span style="color:#334155;font-size:0.875rem">Nenhuma notificação</span></div>';
    return;
  }

  list.innerHTML = notifications.map(function(n) {
    var icon  = NOTIF_ICONS[n.type]  || 'ti-bell';
    var color = NOTIF_COLORS[n.type] || '#6b6eff';
    var bg    = n.read ? 'transparent' : 'rgba(107,110,255,0.05)';
    var dot   = n.read ? '' :
      '<span style="width:7px;height:7px;border-radius:50%;background:#6b6eff;' +
      'flex-shrink:0;margin-top:4px"></span>';

    var timeAgo = formatTimeAgo(n.createdAt);

    return '<div class="notif-item" data-id="' + n.id + '" data-url="' + (n.actionUrl||'') + '"' +
      'onclick="handleNotifClick(this)"' +
      'style="display:flex;gap:0.75rem;padding:0.85rem 1rem;cursor:pointer;' +
      'background:' + bg + ';transition:background 0.2s;border-bottom:1px solid rgba(255,255,255,0.04)"' +
      'onmouseenter="this.style.background=\'rgba(107,110,255,0.06)\'"' +
      'onmouseleave="this.style.background=\'' + bg + '\'">' +

      '<div style="width:34px;height:34px;border-radius:9px;flex-shrink:0;' +
      'background:' + color + '18;display:flex;align-items:center;justify-content:center">' +
      '<i class="ti ' + icon + '" style="color:' + color + ';font-size:0.9rem"></i></div>' +

      '<div style="flex:1;min-width:0">' +
        '<div style="font-weight:600;color:#e2e8f0;font-size:0.82rem;' +
        'white-space:normal;word-break:break-word">' + n.title + '</div>' +
        '<div style="color:#64748b;font-size:0.75rem;margin-top:1px;' +
        'white-space:normal;word-break:break-word">' + n.message + '</div>' +
        '<div style="color:#334155;font-size:0.7rem;margin-top:3px">' + timeAgo + '</div>' +
      '</div>' +

      dot +
      '</div>';
  }).join('');
}

function handleNotifClick(el) {
  var id  = el.dataset.id;
  var url = el.dataset.url;
  if (id) {
    fetch('/notifications/' + id + '/read', { method:'POST' })
    .then(function() { loadBadgeCount(); })
    .catch(function() {});
    el.style.background = 'transparent';
    var dot = el.querySelector('[style*="border-radius:50%"]');
    if (dot) dot.remove();
  }
  if (url && url !== 'null' && url !== '') {
    document.getElementById('notifPanel').style.display = 'none';
    notifPanelOpen = false;
    window.location.href = url;
  }
}

function markAllRead() {
  fetch('/notifications/read-all', { method:'POST' })
  .then(function() {
    updateBadge(0);
    loadNotifications();
  })
  .catch(function() {});
}

function updateBadge(count) {
  var badge = document.getElementById('notifBadge');
  if (!badge) return;
  if (count > 0) {
    badge.textContent = count > 99 ? '99+' : count;
    badge.style.display = '';
  } else {
    badge.style.display = 'none';
  }
}

function loadBadgeCount() {
  fetch('/notifications')
  .then(function(r) { return r.ok ? r.json() : null; })
  .then(function(data) {
    if (data) updateBadge(data.unreadCount || 0);
  })
  .catch(function() {});
}

function formatTimeAgo(dateStr) {
  if (!dateStr) return '';
  var now  = new Date();
  var date = new Date(dateStr);
  var diff = Math.floor((now - date) / 1000);
  if (diff < 60)   return 'agora mesmo';
  if (diff < 3600) return Math.floor(diff/60) + ' min atrás';
  if (diff < 86400) return Math.floor(diff/3600) + 'h atrás';
  var days = Math.floor(diff/86400);
  return days === 1 ? 'ontem' : days + ' dias atrás';
}

document.addEventListener('DOMContentLoaded', function() {
  var badge = document.getElementById('notifBadge');
  if (badge) loadBadgeCount();
});

setInterval(function() {
  var badge = document.getElementById('notifBadge');
  if (badge) loadBadgeCount();
}, 60000);

function loadChatUnreadTotal() {
  fetch('/app-api/chat/unread-total')
    .then(function(r) { return r.ok ? r.json() : null; })
    .then(function(total) {
      if (total === null) return;
      var badges = document.querySelectorAll('.chat-unread-badge');
      badges.forEach(function(badge) {
        if (total > 0) {
          badge.textContent = total > 99 ? '99+' : total;
          badge.style.display = '';
        } else {
          badge.style.display = 'none';
        }
      });
    })
    .catch(function() {});
}

document.addEventListener('DOMContentLoaded', function() {
  if (document.querySelector('.chat-unread-badge')) {
    loadChatUnreadTotal();
  }

  if (/^\/chat\/\d+$/.test(window.location.pathname)) {
    setTimeout(loadChatUnreadTotal, 1000);
  }
});

setInterval(function() {
  if (document.querySelector('.chat-unread-badge')) {
    loadChatUnreadTotal();
  }
}, 30000);
