// Página dedicada de avaliações — filtro por estrela via fetch, sem recarregar a página.
// Espera window.REVIEWS_PROFILE_TYPE, window.REVIEWS_PROFILE_ID e window.REVIEWS_DATA
// (primeira carga, já vinda do servidor) injetados pelo template.
(function () {
  if (typeof window.REVIEWS_PROFILE_ID === 'undefined' || window.REVIEWS_PROFILE_ID === null) return;

  var type = window.REVIEWS_PROFILE_TYPE;
  var id = window.REVIEWS_PROFILE_ID;
  var listEl = document.getElementById('reviews-list');
  var emptyEl = document.getElementById('reviews-empty');
  var buttons = document.querySelectorAll('.review-filter-btn');

  if (!listEl) return;

  function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str == null ? '' : String(str);
    return div.innerHTML;
  }

  function starsHtml(rating) {
    var out = '';
    for (var i = 1; i <= 5; i++) {
      out += '<i class="ti ' + (i <= rating ? 'ti-star-filled' : 'ti-star') +
        '" style="color:#f59e0b;font-size:1rem"></i>';
    }
    return out;
  }

  function formatDate(iso) {
    if (!iso) return '';
    var d = new Date(iso);
    if (isNaN(d.getTime())) return '';
    var day = String(d.getDate()).padStart(2, '0');
    var month = String(d.getMonth() + 1).padStart(2, '0');
    return day + '/' + month + '/' + d.getFullYear();
  }

  function reasonBadges(reasons, positive) {
    if (!reasons || !reasons.length) return '';
    var style = positive
      ? 'background:rgba(34,197,94,0.12);color:#22c55e;border:1px solid rgba(34,197,94,0.25)'
      : 'background:rgba(239,68,68,0.12);color:#ef4444;border:1px solid rgba(239,68,68,0.25)';
    return '<div class="d-flex flex-wrap gap-1 mt-2">' + reasons.map(function (r) {
      return '<span style="' + style + ';border-radius:6px;padding:3px 10px;font-size:0.75rem">' +
        escapeHtml(r) + '</span>';
    }).join('') + '</div>';
  }

  function reviewCardHtml(r) {
    var photo = r.reviewerPhotoUrl
      ? '<img src="' + escapeHtml(r.reviewerPhotoUrl) + '" style="width:44px;height:44px;border-radius:50%;object-fit:cover;flex-shrink:0" alt="">'
      : '<div style="width:44px;height:44px;border-radius:50%;background:#312e81;color:#fff;' +
        'display:flex;align-items:center;justify-content:center;font-size:1rem;font-weight:700;flex-shrink:0">' +
        escapeHtml((r.reviewerName || '?').substring(0, 1).toUpperCase()) + '</div>';

    var comment = r.comment
      ? '<p class="mb-0 mt-2" style="color:#94a3b8;font-size:0.85rem;font-style:italic">"' + escapeHtml(r.comment) + '"</p>'
      : '';

    return '' +
      '<div class="nexus-card p-3 mb-3">' +
      '  <div class="d-flex align-items-start gap-3">' +
      photo +
      '    <div class="flex-fill overflow-hidden">' +
      '      <div class="d-flex align-items-center justify-content-between flex-wrap gap-1">' +
      '        <div class="fw-semibold text-white">' + escapeHtml(r.reviewerName) + '</div>' +
      '        <div>' + starsHtml(r.rating) + '</div>' +
      '      </div>' +
      '      <div style="color:#64748b;font-size:0.78rem">sobre: ' + escapeHtml(r.opportunityTitle) +
             ' · ' + formatDate(r.createdAt) + '</div>' +
      reasonBadges(r.positiveReasons, true) +
      reasonBadges(r.negativeReasons, false) +
      comment +
      '    </div>' +
      '  </div>' +
      '</div>';
  }

  function renderList(reviews) {
    if (!reviews || !reviews.length) {
      listEl.innerHTML = '';
      if (emptyEl) emptyEl.style.display = '';
      return;
    }
    if (emptyEl) emptyEl.style.display = 'none';
    listEl.innerHTML = reviews.map(reviewCardHtml).join('');
  }

  function setActiveButton(rating) {
    buttons.forEach(function (btn) {
      var btnRating = btn.getAttribute('data-rating');
      var isActive = (rating === null && btnRating === '') || (rating !== null && btnRating === String(rating));
      btn.classList.toggle('active', isActive);
    });
  }

  function applyFilter(rating) {
    setActiveButton(rating);
    var url = '/app-api/reviews/' + type + '/' + id + '/all' + (rating !== null ? '?rating=' + rating : '');
    fetch(url)
      .then(function (r) { return r.ok ? r.json() : null; })
      .then(function (page) { renderList(page ? page.reviews : []); })
      .catch(function () { renderList([]); });
  }

  buttons.forEach(function (btn) {
    btn.addEventListener('click', function () {
      var raw = btn.getAttribute('data-rating');
      applyFilter(raw === '' ? null : parseInt(raw, 10));
    });
  });

  // Primeira renderização usa os dados já vindos do servidor (sem fetch extra).
  renderList(window.REVIEWS_DATA && window.REVIEWS_DATA.reviews ? window.REVIEWS_DATA.reviews : []);
  setActiveButton(null);
})();
