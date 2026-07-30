(function () {

  function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str || '';
    return div.innerHTML;
  }

  function hueFromName(name) {
    var hash = 0;
    for (var i = 0; i < (name || '').length; i++) {
      hash = (name.charCodeAt(i) + ((hash << 5) - hash)) | 0;
    }
    return ((hash % 360) + 360) % 360;
  }

  function avatarHtml(name, photoUrl) {
    if (photoUrl) {
      return '<img src="' + photoUrl + '" alt="Foto" ' +
        'style="width:72px;height:72px;border-radius:14px;object-fit:cover;flex-shrink:0;' +
        'border:2px solid rgba(107,110,255,0.2)">';
    }
    var letter = name ? name.charAt(0).toUpperCase() : '?';
    return '<div class="nexus-avatar-initials flex-shrink-0" ' +
      'style="width:72px;height:72px;border-radius:14px;font-size:1.4rem;background:hsl(' +
      hueFromName(name) + ',50%,38%)">' + letter + '</div>';
  }

  function starsHtml(reputation) {
    var r = reputation || 0;
    var html = '';
    for (var i = 1; i <= 5; i++) {
      html += '<i class="ti ' + (i <= Math.round(r) ? 'ti-star-filled' : 'ti-star') +
        '" style="color:#f59e0b;font-size:0.85rem"></i>';
    }
    return html +
      '<span style="color:#64748b;font-size:0.78rem;margin-left:3px">' +
      Number(r).toFixed(1) + '</span>';
  }

  function companyCardHtml(c) {
    return '' +
      '<div class="d-flex align-items-start gap-3">' +
        avatarHtml(c.companyName, c.profilePhotoUrl) +
        '<div class="flex-fill" style="min-width:0">' +
          '<div class="fw-semibold text-white text-truncate" style="font-size:1.1rem">' +
            escapeHtml(c.companyName) + '</div>' +
          (c.city ? '<div style="color:#64748b;font-size:0.85rem;margin-top:3px">' +
            '<i class="ti ti-map-pin" style="color:#6b6eff"></i> ' +
            escapeHtml(c.city) + (c.uf ? ', ' + escapeHtml(c.uf) : '') + '</div>' : '') +
          '<div class="d-flex align-items-center gap-1 mt-2">' + starsHtml(c.reputation) + '</div>' +
          (c.description ? '<div style="color:#94a3b8;font-size:0.85rem;margin-top:8px;line-height:1.5;' +
            'display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden">' +
            escapeHtml(c.description) + '</div>' : '') +
        '</div>' +
      '</div>';
  }

  function professionalCardHtml(p) {
    var skills = (p.skills || []).slice(0, 5).map(function (s) {
      return '<span class="nexus-chip nexus-chip-tech" style="font-size:0.72rem;padding:0.2em 0.6em">' +
        escapeHtml(s) + '</span>';
    }).join('');
    return '' +
      '<div class="d-flex align-items-start gap-3">' +
        avatarHtml(p.name, p.profilePhotoUrl) +
        '<div class="flex-fill" style="min-width:0">' +
          '<div class="fw-semibold text-white text-truncate" style="font-size:1.1rem">' +
            escapeHtml(p.name) + '</div>' +
          (p.city ? '<div style="color:#64748b;font-size:0.85rem;margin-top:3px">' +
            '<i class="ti ti-map-pin" style="color:#6b6eff"></i> ' +
            escapeHtml(p.city) + (p.uf ? ', ' + escapeHtml(p.uf) : '') + '</div>' : '') +
          '<div class="d-flex align-items-center gap-1 mt-2">' + starsHtml(p.reputation) + '</div>' +
          (skills ? '<div class="d-flex flex-wrap gap-1 mt-2">' + skills + '</div>' : '') +
        '</div>' +
      '</div>';
  }

  function initNexusDirectory(config) {
    var type       = config.type; // 'companies' | 'professionals'
    var detailBase = config.type === 'companies' ? '/public/company/' : '/public/professional/';
    var buildCard  = config.type === 'companies' ? companyCardHtml : professionalCardHtml;

    var container    = document.getElementById('directoryList');
    var searchInput  = document.getElementById('directorySearch');
    var emptyState   = document.getElementById('directoryEmpty');
    var loadingEl    = document.getElementById('directoryLoading');
    var sentinel     = document.getElementById('directorySentinel');
    if (!container) return;

    var page = 0;
    var loading = false;
    var hasMore = true;
    var searchTerm = '';
    var searchDebounceId = null;
    var requestToken = 0;

    function setLoading(v) {
      loading = v;
      if (loadingEl) loadingEl.style.display = v ? 'block' : 'none';
    }

    function appendItems(items) {
      items.forEach(function (item) {
        var col = document.createElement('div');
        col.className = 'col-12 col-md-6';
        var card = document.createElement('div');
        card.className = 'nexus-card p-4 h-100';
        card.style.cursor = 'pointer';
        card.innerHTML = buildCard(item);
        card.addEventListener('click', function () {
          window.location.href = detailBase + item.id;
        });
        col.appendChild(card);
        container.appendChild(col);
      });
    }

    function loadNextPage() {
      if (loading || !hasMore) return;
      setLoading(true);
      var myToken = requestToken;
      var url = '/directory/' + type + '?page=' + page + '&size=50' +
        (searchTerm ? '&search=' + encodeURIComponent(searchTerm) : '');

      nexusFetch(url)
        .then(function (data) {
          if (myToken !== requestToken) return; // resposta obsoleta (nova busca já iniciou)
          var items = (data && data.content) || [];
          appendItems(items);
          hasMore = !!(data && data.hasMore);
          page += 1;
          emptyState.style.display = container.children.length === 0 ? 'block' : 'none';
        })
        .catch(function () {})
        .finally(function () { if (myToken === requestToken) setLoading(false); });
    }

    function resetAndLoad() {
      requestToken += 1;
      container.innerHTML = '';
      page = 0;
      hasMore = true;
      emptyState.style.display = 'none';
      loadNextPage();
    }

    if (searchInput) {
      searchInput.addEventListener('input', function () {
        clearTimeout(searchDebounceId);
        var value = searchInput.value;
        searchDebounceId = setTimeout(function () {
          searchTerm = value.trim();
          resetAndLoad();
        }, 350);
      });
    }

    if ('IntersectionObserver' in window && sentinel) {
      var observer = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
          if (entry.isIntersecting) loadNextPage();
        });
      }, { rootMargin: '300px' });
      observer.observe(sentinel);
    } else {
      window.addEventListener('scroll', function () {
        if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 300) {
          loadNextPage();
        }
      });
    }

    loadNextPage();
  }

  window.initNexusDirectory = initNexusDirectory;
})();
