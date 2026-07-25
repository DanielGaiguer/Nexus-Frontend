/**
 * nexus-map.js — Inicialização e controle do mapa Leaflet para Nexus
 * Suporta pins de profissionais, empresas e oportunidades.
 *
 * Variáveis esperadas injetadas pelo template via th:inline="javascript":
 *   var PROFESSIONALS  = [...];
 *   var COMPANIES      = [...];
 *   var OPPORTUNITIES  = [...];
 *   var CENTER_LAT     = -23.3045;
 *   var CENTER_LNG     = -51.1696;
 */

(function() {
  'use strict';

  // ── Guardar referências globais dos layers ────────────────
  var map;
  var layerPros  = null;
  var layerCos   = null;
  var layerOpps  = null;

  var currentRadius     = 50;
  var currentType       = 'all';      // 'all' | 'professionals' | 'companies' | 'opportunities'
  var currentOppType    = '';         // '' | 'PROJECT' | 'JOB'

  // ── Haversine distance (km) ───────────────────────────────
  function distanceKm(lat1, lng1, lat2, lng2) {
    var R = 6371;
    var dLat = (lat2 - lat1) * Math.PI / 180;
    var dLng = (lng2 - lng1) * Math.PI / 180;
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }

  // ── Criar ícone div personalizado ─────────────────────────
  function makePin(color, iconClass) {
    var html =
      '<div style="width:36px;height:36px;border-radius:50%;' +
      'background:' + color + ';border:2px solid rgba(255,255,255,0.18);' +
      'display:flex;align-items:center;justify-content:center;' +
      'box-shadow:0 0 14px ' + color + '66;">' +
      '<i class="ti ' + iconClass + '" style="color:#fff;font-size:0.85rem"></i>' +
      '</div>';
    return L.divIcon({ className:'', iconSize:[36,36], iconAnchor:[18,36], popupAnchor:[0,-38], html:html });
  }

  var PIN_PRO  = null; // inicializado depois do DOM
  var PIN_CO   = null;
  var PIN_PROJ = null;
  var PIN_JOB  = null;

  // ── Tradução de campos ────────────────────────────────────
  function translateWorkMode(wm) {
    var m = { 'REMOTE':'Remoto', 'ONSITE':'Presencial', 'HYBRID':'Híbrido' };
    return m[wm] || wm || '—';
  }

  function translateOppType(t) {
    return t === 'JOB' ? 'Vaga de emprego' : 'Projeto';
  }

  // ── Popup de profissional ─────────────────────────────────
  function popupPro(p) {
    var skills = (p.skills || []).slice(0, 3).join(', ');
    return '<div style="min-width:180px;font-family:Inter,sans-serif">' +
      '<div style="font-weight:700;color:#fff;margin-bottom:3px">' + (p.name || '—') + '</div>' +
      '<div style="color:#64748b;font-size:0.78rem;margin-bottom:6px">' +
        (p.city || '') + (p.uf ? ', ' + p.uf : '') +
      '</div>' +
      '<span style="font-size:0.7rem;background:rgba(107,110,255,0.15);color:#a5b4fc;' +
        'padding:2px 7px;border-radius:20px">Profissional</span>' +
      (skills ? '<div style="margin-top:6px;font-size:0.75rem;color:#94a3b8">' + skills + '</div>' : '') +
      '</div>';
  }

  // ── Popup de empresa ──────────────────────────────────────
  function popupCo(c) {
    return '<div style="min-width:180px;font-family:Inter,sans-serif">' +
      '<div style="font-weight:700;color:#fff;margin-bottom:3px">' + (c.companyName || '—') + '</div>' +
      '<div style="color:#64748b;font-size:0.78rem;margin-bottom:6px">' +
        (c.city || '') + (c.uf ? ', ' + c.uf : '') +
      '</div>' +
      '<span style="font-size:0.7rem;background:rgba(245,158,11,0.15);color:#f59e0b;' +
        'padding:2px 7px;border-radius:20px">Empresa</span>' +
      (c.description ? '<div style="margin-top:6px;font-size:0.75rem;color:#94a3b8;' +
        'max-width:180px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">' +
        c.description + '</div>' : '') +
      '</div>';
  }

  // ── Popup de oportunidade ─────────────────────────────────
  function popupOpp(o) {
    var typeColor = o.opportunityType === 'JOB' ? '#86efac' : '#a5b4fc';
    var typeBg    = o.opportunityType === 'JOB' ? 'rgba(34,197,94,0.12)' : 'rgba(107,110,255,0.12)';
    var typeLabel = o.opportunityType === 'JOB' ? 'Vaga de emprego' : 'Projeto';

    // URL de detalhe — rota diferente por papel (lida de uma meta tag injetada pelo template)
    var baseUrl = document.querySelector('meta[name="nexus-map-base"]');
    var detailBase = baseUrl ? baseUrl.content : '/public/opportunity';
    var detailUrl  = detailBase + '/' + o.id;

    var skills = (o.requiredSkills || []).slice(0, 3).join(', ');
    var more   = (o.requiredSkills || []).length > 3
                 ? ' +' + ((o.requiredSkills || []).length - 3) + ' mais'
                 : '';

    return '<div style="min-width:200px;max-width:240px;font-family:Inter,sans-serif">' +
      '<span style="font-size:0.68rem;background:' + typeBg + ';color:' + typeColor + ';' +
        'padding:2px 7px;border-radius:20px;display:inline-block;margin-bottom:6px">' +
        typeLabel + '</span>' +
      '<div style="font-weight:700;color:#fff;font-size:0.9rem;line-height:1.3;margin-bottom:3px">' +
        (o.title || '—') + '</div>' +
      '<div style="color:#94a3b8;font-size:0.78rem;margin-bottom:2px">' +
        (o.companyName || '—') + '</div>' +
      '<div style="color:#64748b;font-size:0.75rem;margin-bottom:6px">' +
        (o.city || '') + (o.uf ? ', ' + o.uf : '') +
        (o.workMode ? ' · ' + translateWorkMode(o.workMode) : '') +
      '</div>' +
      (skills ? '<div style="font-size:0.72rem;color:#6b6eff;margin-bottom:8px">' +
        skills + more + '</div>' : '') +
      '<a href="' + detailUrl + '"' +
        ' style="display:block;background:linear-gradient(135deg,#6b6eff,#5558e0);' +
        'color:#fff;text-decoration:none;border-radius:7px;padding:0.4rem 0.75rem;' +
        'font-size:0.78rem;font-weight:600;text-align:center;margin-top:4px">' +
        '<i class="ti ti-arrow-right" style="margin-right:4px"></i>Ver oportunidade' +
      '</a>' +
      '</div>';
  }

  // ── Renderizar markers ────────────────────────────────────
  function renderMarkers() {
    if (!map) return;

    // Limpa layers anteriores
    if (layerPros) { map.removeLayer(layerPros); layerPros = null; }
    if (layerCos)  { map.removeLayer(layerCos);  layerCos  = null; }
    if (layerOpps) { map.removeLayer(layerOpps); layerOpps = null; }

    layerPros = L.layerGroup();
    layerCos  = L.layerGroup();
    layerOpps = L.layerGroup();

    var cntPros = 0, cntCos = 0, cntOpps = 0;
    var centerLat = (typeof CENTER_LAT !== 'undefined') ? CENTER_LAT : -23.3045;
    var centerLng = (typeof CENTER_LNG !== 'undefined') ? CENTER_LNG : -51.1696;

    // Profissionais
    if (currentType === 'all' || currentType === 'professionals') {
      var pros = (typeof PROFESSIONALS !== 'undefined') ? PROFESSIONALS : [];
      pros.forEach(function(p) {
        if (!p.latitude || !p.longitude) return;
        if (distanceKm(centerLat, centerLng, p.latitude, p.longitude) > currentRadius) return;
        cntPros++;
        L.marker([p.latitude, p.longitude], { icon: PIN_PRO })
         .bindPopup(popupPro(p), { className: 'nexus-popup' })
         .addTo(layerPros);
      });
    }

    // Empresas
    if (currentType === 'all' || currentType === 'companies') {
      var cos = (typeof COMPANIES !== 'undefined') ? COMPANIES : [];
      cos.forEach(function(c) {
        if (!c.latitude || !c.longitude) return;
        if (distanceKm(centerLat, centerLng, c.latitude, c.longitude) > currentRadius) return;
        cntCos++;
        L.marker([c.latitude, c.longitude], { icon: PIN_CO })
         .bindPopup(popupCo(c), { className: 'nexus-popup' })
         .addTo(layerCos);
      });
    }

    // Oportunidades
    if (currentType === 'all' || currentType === 'opportunities') {
      var opps = (typeof OPPORTUNITIES !== 'undefined') ? OPPORTUNITIES : [];
      opps.forEach(function(o) {
        if (!o.latitude || !o.longitude) return;
        if (distanceKm(centerLat, centerLng, o.latitude, o.longitude) > currentRadius) return;
        // Filtro de subtipo
        if (currentOppType && o.opportunityType !== currentOppType) return;
        cntOpps++;
        var pin = o.opportunityType === 'JOB' ? PIN_JOB : PIN_PROJ;
        L.marker([o.latitude, o.longitude], { icon: pin })
         .bindPopup(popupOpp(o), { className: 'nexus-popup' })
         .addTo(layerOpps);
      });
    }

    layerPros.addTo(map);
    layerCos.addTo(map);
    layerOpps.addTo(map);

    var total = cntPros + cntCos + cntOpps;
    updateCounts(cntPros, cntCos, cntOpps, total);
  }

  function updateCounts(pros, cos, opps, total) {
    var els = {
      'count-all':   total,
      'count-pros':  pros,
      'count-cos':   cos,
      'count-opps':  opps,
      'visible-count': total,
      'filter-badge': null
    };
    Object.keys(els).forEach(function(id) {
      var el = document.getElementById(id);
      if (el && els[id] !== null) el.textContent = els[id];
    });
    var radiusText = isFinite(currentRadius) ? currentRadius + ' km' : 'sem limite de distância';
    var badge = document.getElementById('filter-badge');
    if (badge) badge.textContent = 'Filtro: ' + radiusText;
    var rlabel = document.getElementById('radius-label');
    if (rlabel) rlabel.textContent = isFinite(currentRadius)
      ? 'Mostrando raio de ' + radiusText
      : 'Mostrando todos, sem limite de distância';
  }

  // ── API pública: chamada pelos botões do HTML ─────────────
  window.setRadius = function(r, btn) {
    currentRadius = r;
    document.querySelectorAll('.radius-btn').forEach(function(b) {
      b.classList.remove('nexus-btn-primary', 'active-radius');
      b.classList.add('nexus-btn-ghost');
    });
    if (btn) {
      btn.classList.add('nexus-btn-primary', 'active-radius');
      btn.classList.remove('nexus-btn-ghost');
    }
    renderMarkers();
  };

  window.setTypeFilter = function(type, el) {
    currentType = type;

    // Destaca a opção ativa
    document.querySelectorAll('.type-filter-option').forEach(function(l) {
      l.style.background = '';
    });
    if (el) el.style.background = 'rgba(107,110,255,0.1)';

    // Mostra ou esconde o sub-select de tipo de oportunidade
    var subSelect = document.getElementById('oppTypeSubSelect');
    if (subSelect) {
      subSelect.style.display = type === 'opportunities' ? 'block' : 'none';
    }

    renderMarkers();
  };

  window.setOppTypeFilter = function(type) {
    currentOppType = type;
    renderMarkers();
  };

  // ── Inicialização ─────────────────────────────────────────
  document.addEventListener('DOMContentLoaded', function() {
    var centerLat = (typeof CENTER_LAT !== 'undefined') ? CENTER_LAT : -23.3045;
    var centerLng = (typeof CENTER_LNG !== 'undefined') ? CENTER_LNG : -51.1696;

    // Inicia os pins depois do DOM (Leaflet usa classes CSS)
    PIN_PRO  = makePin('#6b6eff', 'ti-user');
    PIN_CO   = makePin('#f59e0b', 'ti-building');
    PIN_PROJ = makePin('#a78bfa', 'ti-briefcase');
    PIN_JOB  = makePin('#22c55e', 'ti-file-text');

    map = L.map('map', { zoomControl: true }).setView([centerLat, centerLng], 10);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://carto.com/">CARTO</a>',
      subdomains: 'abcd', maxZoom: 19
    }).addTo(map);

    // Popup style
    var style = document.createElement('style');
    style.textContent =
      '.nexus-popup .leaflet-popup-content-wrapper{' +
        'background:rgba(9,14,31,0.97);border:1px solid rgba(107,110,255,0.2);' +
        'border-radius:10px;color:#e2e8f0;box-shadow:0 8px 32px rgba(0,0,0,0.5)}' +
      '.nexus-popup .leaflet-popup-tip{background:rgba(9,14,31,0.97)}' +
      '.nexus-popup .leaflet-popup-close-button{color:#64748b}';
    document.head.appendChild(style);

    // Ativa "Ambos" como padrão
    var defaultEl = document.querySelector('.type-filter-option[data-type="all"]');
    if (defaultEl) defaultEl.style.background = 'rgba(107,110,255,0.1)';

    renderMarkers();
  });

})();