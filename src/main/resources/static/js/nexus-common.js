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
