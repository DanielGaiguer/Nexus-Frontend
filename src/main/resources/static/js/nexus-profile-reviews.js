// Card de preview "Avaliações Recebidas" (top 3) — injetado em qualquer perfil de
// profissional ou empresa (próprio, admin ou público) que declare window.PROFILE_ID e
// window.PROFILE_TYPE. Carregado via fetch, não via Thymeleaf server-side, então funciona
// nos 8 contextos de perfil sem precisar alterar cada controller individualmente.
// Roda em toda página (carregado por fragments/app-shell e fragments/scripts) e não faz
// nada se a página não declarar PROFILE_ID/PROFILE_TYPE — mesmo padrão do nexus-chat-notify.js.
(function () {
  if (typeof window.PROFILE_ID === 'undefined' || window.PROFILE_ID === null) return;
  if (typeof window.PROFILE_TYPE === 'undefined' || window.PROFILE_TYPE === null) return;

  var type = window.PROFILE_TYPE; // "professional" ou "company"
  var id = window.PROFILE_ID;

  var mount = document.getElementById('reviews-preview-mount');
  if (!mount) return;

  var base = '/app-api/reviews/' + type + '/' + id;

  // top3 traz só as 3 melhores (LIMIT no banco, mais leve pro card); all traz o total e
  // a média geral — não tem endpoint só de estatística, então busca os dois em paralelo.
  Promise.all([
    fetch(base + '/top3').then(function (r) { return r.ok ? r.json() : []; }),
    fetch(base + '/all').then(function (r) { return r.ok ? r.json() : null; })
  ])
    .then(function (results) {
      render(Array.isArray(results[0]) ? results[0] : [], results[1]);
    })
    .catch(function () { render([], null); });

  function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str == null ? '' : String(str);
    return div.innerHTML;
  }

  function starsHtml(rating) {
    var out = '';
    for (var i = 1; i <= 5; i++) {
      out += '<i class="ti ' + (i <= rating ? 'ti-star-filled' : 'ti-star') +
        '" style="color:#f59e0b;font-size:0.85rem"></i>';
    }
    return out;
  }

  function reasonBadges(reasons, positive) {
    if (!reasons || !reasons.length) return '';
    var style = positive
      ? 'background:rgba(34,197,94,0.12);color:#22c55e;border:1px solid rgba(34,197,94,0.25)'
      : 'background:rgba(239,68,68,0.12);color:#ef4444;border:1px solid rgba(239,68,68,0.25)';
    return '<div class="d-flex flex-wrap gap-1 mt-1">' + reasons.map(function (r) {
      return '<span style="' + style + ';border-radius:6px;padding:2px 8px;font-size:0.7rem">' +
        escapeHtml(r) + '</span>';
    }).join('') + '</div>';
  }

  function reviewItemHtml(r) {
    var photo = r.reviewerPhotoUrl
      ? '<img src="' + escapeHtml(r.reviewerPhotoUrl) + '" style="width:36px;height:36px;border-radius:50%;object-fit:cover;flex-shrink:0" alt="">'
      : '<div style="width:36px;height:36px;border-radius:50%;background:#312e81;color:#fff;' +
        'display:flex;align-items:center;justify-content:center;font-size:0.85rem;font-weight:700;flex-shrink:0">' +
        escapeHtml((r.reviewerName || '?').substring(0, 1).toUpperCase()) + '</div>';

    var comment = r.comment
      ? '<p class="mb-0 mt-2" style="color:#94a3b8;font-size:0.82rem;font-style:italic">"' + escapeHtml(r.comment) + '"</p>'
      : '';

    return '' +
      '<div class="p-3" style="border-bottom:1px solid rgba(255,255,255,0.06)">' +
      '  <div class="d-flex align-items-start gap-2">' +
      photo +
      '    <div class="flex-fill overflow-hidden">' +
      '      <div class="fw-semibold text-white text-truncate" style="font-size:0.88rem">' + escapeHtml(r.reviewerName) + '</div>' +
      '      <div class="text-truncate" style="color:#64748b;font-size:0.75rem">sobre: ' + escapeHtml(r.opportunityTitle) + '</div>' +
      '    </div>' +
      '    <div class="flex-shrink-0">' + starsHtml(r.rating) + '</div>' +
      '  </div>' +
      reasonBadges(r.positiveReasons, true) +
      reasonBadges(r.negativeReasons, false) +
      comment +
      '</div>';
  }

  function render(top3, page) {
    var totalReviews = page ? page.totalReviews : 0;
    var averageRating = page ? page.averageRating : 0;

    var card = document.createElement('div');
    card.className = 'nexus-card p-0 mb-3';
    card.id = 'reviews-preview-card';

    if (!totalReviews) {
      card.innerHTML =
        '<div class="p-4 text-center" id="no-reviews-msg">' +
        '  <i class="ti ti-star" style="font-size:1.75rem;color:#334155;display:block;margin-bottom:0.5rem"></i>' +
        '  <div style="color:#64748b;font-size:0.85rem">Nenhuma avaliação recebida ainda.</div>' +
        '</div>';
      mount.appendChild(card);
      return;
    }

    var seeAllHref = '/public/' + type + '/' + id + '/reviews';
    var subtitle = totalReviews + ' avaliação' + (totalReviews === 1 ? '' : 'ões') +
      ' · média ' + averageRating.toFixed(1) + ' estrelas';

    card.innerHTML =
      '<div class="p-3" style="border-bottom:1px solid rgba(255,255,255,0.06)">' +
      '  <div class="fw-semibold text-white" style="font-size:0.95rem"><i class="ti ti-star-filled" style="color:#f59e0b"></i> Avaliações Recebidas</div>' +
      '  <div style="color:#64748b;font-size:0.75rem">' + escapeHtml(subtitle) + '</div>' +
      '</div>' +
      '<div>' + top3.map(reviewItemHtml).join('') + '</div>' +
      '<div class="p-3 d-flex justify-content-end" style="border-top:1px solid rgba(255,255,255,0.06)">' +
      '  <a href="' + seeAllHref + '" class="nexus-btn nexus-btn-ghost nexus-btn-sm">Ver todas</a>' +
      '</div>';

    mount.appendChild(card);
  }
})();
