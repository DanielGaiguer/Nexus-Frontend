/**
 * nexus-common.js — Shared utilities for authenticated pages
 */

/**
 * fetch wrapper for /app-api/** proxy endpoints.
 * Handles HTTP errors with a standardized toast notification.
 * @param {string} url - path relative to /app-api/ (e.g. '/matches/1/accept')
 * @param {object} [options] - fetch options (method, headers, body, etc.)
 * @returns {Promise<any>} parsed JSON response
 */
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

/**
 * Show a Bootstrap toast notification.
 * @param {string} message - text to display
 * @param {'success'|'error'} [type='success'] - toast style
 */
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

/* ── Tom Select: estiliza todos os <select class="nexus-select"> ── */
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
    this.dropdown.style.maxHeight = 'none';
    this.dropdown.style.height = 'auto';
    this.dropdown.style.overflow = 'visible';
    var dc = this.dropdown.querySelector('.ts-dropdown-content');
    if (dc) {
      dc.style.maxHeight = 'none';
      dc.style.height = 'auto';
      dc.style.overflow = 'visible';
    }
  };

  document.querySelectorAll('select.nexus-select').forEach(function(el) {
    var isMulti = el.hasAttribute('multiple');
    var placeholderText = isMulti ? 'Todos os níveis' : (el.getAttribute('data-placeholder') || '');
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
