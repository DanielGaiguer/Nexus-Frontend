/**
 * nexus-map.js — Inicialização e controle do mapa Leaflet para Nexus
 * Suporta pins de profissionais, empresas e oportunidades.
 *
 * Variáveis esperadas injetadas pelo template via th:inline="javascript":
 *   var PROFESSIONALS  = [...];
 *   var COMPANIES      = [...];
 *   var OPPORTUNITIES  = [...];
 *   var CENTER_LAT     = -23.5505;
 *   var CENTER_LNG     = -46.6333;
 */

(function() {
  'use strict';

  // ── Guardar referências globais dos layers ────────────────
  var map;
  var layerPros     = null;
  var layerCos      = null;
  var layerOpps     = null;
  var layerClusters = null; // marcadores agrupados (mesma localização/CEP)

  var currentRadius     = (typeof DEFAULT_RADIUS !== 'undefined') ? DEFAULT_RADIUS : 50;
  var currentType       = 'all';      // 'all' | 'professionals' | 'companies' | 'opportunities'
  var currentOppType    = '';         // '' | 'PROJECT' | 'JOB'

  // Zoom mínimo a partir do qual pontos coincidentes deixam de ser
  // mostrados como um único "cluster" numerado e passam a ser espalhados
  // individualmente. Abaixo disso, mesmo com o espalhamento em metros,
  // a separação em pixels seria pequena demais para notar sem dar zoom.
  var CLUSTER_ZOOM_THRESHOLD = 17;

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

  // ── Ícone de cluster (vários cadastros na mesma localização) ──
  function makeClusterPin(count) {
    var size = count > 9 ? 44 : 38;
    var html =
      '<div style="width:' + size + 'px;height:' + size + 'px;border-radius:50%;' +
      'background:radial-gradient(circle at 35% 30%,#5558e0,#20234f);' +
      'border:3px solid rgba(255,255,255,0.55);display:flex;align-items:center;' +
      'justify-content:center;box-shadow:0 0 0 6px rgba(107,110,255,0.16),0 0 18px rgba(0,0,0,0.55);' +
      'color:#fff;font-weight:800;font-size:0.92rem;font-family:Inter,sans-serif;">' +
      count +
      '</div>';
    return L.divIcon({
      className: '', iconSize: [size, size], iconAnchor: [size / 2, size / 2],
      popupAnchor: [0, -size / 2 - 4], html: html
    });
  }

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
    var baseUrl = document.querySelector('meta[name="nexus-map-professional-base"]');
    var detailBase = baseUrl ? baseUrl.content : '';
    var detailUrl  = detailBase ? detailBase + '/' + p.id : '';

    var skills = (p.skills || []).slice(0, 3).join(', ');
    return '<div style="min-width:180px;font-family:Inter,sans-serif">' +
      '<div style="font-weight:700;color:#fff;margin-bottom:3px">' + (p.name || '—') + '</div>' +
      '<div style="color:#64748b;font-size:0.78rem;margin-bottom:6px">' +
        (p.city || '') + (p.uf ? ', ' + p.uf : '') +
      '</div>' +
      '<span style="font-size:0.7rem;background:rgba(107,110,255,0.15);color:#a5b4fc;' +
        'padding:2px 7px;border-radius:20px">Profissional</span>' +
      (skills ? '<div style="margin-top:6px;font-size:0.75rem;color:#94a3b8">' + skills + '</div>' : '') +
      (detailUrl ? '<a href="' + detailUrl + '"' +
        ' style="display:block;background:linear-gradient(135deg,#6b6eff,#5558e0);' +
        'color:#fff;text-decoration:none;border-radius:7px;padding:0.4rem 0.75rem;' +
        'font-size:0.78rem;font-weight:600;text-align:center;margin-top:8px">' +
        '<i class="ti ti-arrow-right" style="margin-right:4px"></i>Ver profissional' +
        '</a>' : '') +
      '</div>';
  }

  // ── Popup de empresa ──────────────────────────────────────
  function popupCo(c) {
    var baseUrl = document.querySelector('meta[name="nexus-map-company-base"]');
    var detailBase = baseUrl ? baseUrl.content : '';
    var detailUrl  = detailBase ? detailBase + '/' + c.id : '';

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
      (detailUrl ? '<a href="' + detailUrl + '"' +
        ' style="display:block;background:linear-gradient(135deg,#6b6eff,#5558e0);' +
        'color:#fff;text-decoration:none;border-radius:7px;padding:0.4rem 0.75rem;' +
        'font-size:0.78rem;font-weight:600;text-align:center;margin-top:8px">' +
        '<i class="ti ti-arrow-right" style="margin-right:4px"></i>Ver empresa' +
        '</a>' : '') +
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

  // ── Agrupa marcadores que caem em coordenadas praticamente idênticas ──
  // (ex: dois cadastros com o mesmo CEP)
  function groupByCoord(items) {
    var groups = {};
    var order = [];
    items.forEach(function(it) {
      // ~11m de tolerância — suficiente para agrupar o mesmo CEP sem
      // confundir endereços vizinhos distintos
      var key = it.lat.toFixed(4) + ',' + it.lng.toFixed(4);
      if (!groups[key]) { groups[key] = []; order.push(key); }
      groups[key].push(it);
    });
    return order.map(function(key) { return groups[key]; });
  }

  // ── Espalha os itens de um grupo num pequeno círculo ao redor do ponto
  // original, para que nenhum pin fique escondido atrás de outro.
  function spreadGroup(group) {
    if (group.length < 2) return;

    var radiusMeters    = 20 + Math.min(group.length, 8) * 4;
    var metersPerDegLat = 111320;
    var metersPerDegLng = metersPerDegLat * Math.cos(group[0].lat * Math.PI / 180) || metersPerDegLat;

    group.forEach(function(it, i) {
      var angle = (2 * Math.PI * i) / group.length;
      it.lat += (radiusMeters * Math.cos(angle)) / metersPerDegLat;
      it.lng += (radiusMeters * Math.sin(angle)) / metersPerDegLng;
    });
  }

  function addIndividualMarker(it) {
    if (it.kind === 'pro') {
      L.marker([it.lat, it.lng], { icon: PIN_PRO })
       .bindPopup(popupPro(it.data), { className: 'nexus-popup' })
       .addTo(layerPros);
    } else if (it.kind === 'co') {
      L.marker([it.lat, it.lng], { icon: PIN_CO })
       .bindPopup(popupCo(it.data), { className: 'nexus-popup' })
       .addTo(layerCos);
    } else {
      var pin = it.data.opportunityType === 'JOB' ? PIN_JOB : PIN_PROJ;
      L.marker([it.lat, it.lng], { icon: pin })
       .bindPopup(popupOpp(it.data), { className: 'nexus-popup' })
       .addTo(layerOpps);
    }
  }

  // ── Renderizar markers ────────────────────────────────────
  function renderMarkers() {
    if (!map) return;

    // Limpa layers anteriores
    if (layerPros)     { map.removeLayer(layerPros);     layerPros     = null; }
    if (layerCos)      { map.removeLayer(layerCos);      layerCos      = null; }
    if (layerOpps)     { map.removeLayer(layerOpps);     layerOpps     = null; }
    if (layerClusters) { map.removeLayer(layerClusters); layerClusters = null; }

    layerPros     = L.layerGroup();
    layerCos      = L.layerGroup();
    layerOpps     = L.layerGroup();
    layerClusters = L.layerGroup();

    var centerLat = (typeof CENTER_LAT !== 'undefined') ? CENTER_LAT : -23.5505;
    var centerLng = (typeof CENTER_LNG !== 'undefined') ? CENTER_LNG : -46.6333;

    // Coleta todos os pontos visíveis (de qualquer tipo) antes de desenhar,
    // para poder detectar coordenadas coincidentes entre si.
    var pending = [];

    // Profissionais
    if (currentType === 'all' || currentType === 'professionals') {
      var pros = (typeof PROFESSIONALS !== 'undefined') ? PROFESSIONALS : [];
      pros.forEach(function(p) {
        if (!p.latitude || !p.longitude) return;
        if (distanceKm(centerLat, centerLng, p.latitude, p.longitude) > currentRadius) return;
        pending.push({ kind: 'pro', lat: p.latitude, lng: p.longitude, data: p });
      });
    }

    // Empresas
    if (currentType === 'all' || currentType === 'companies') {
      var cos = (typeof COMPANIES !== 'undefined') ? COMPANIES : [];
      cos.forEach(function(c) {
        if (!c.latitude || !c.longitude) return;
        if (distanceKm(centerLat, centerLng, c.latitude, c.longitude) > currentRadius) return;
        pending.push({ kind: 'co', lat: c.latitude, lng: c.longitude, data: c });
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
        pending.push({ kind: 'opp', lat: o.latitude, lng: o.longitude, data: o });
      });
    }

    var zoom = map.getZoom();
    var cntPros = 0, cntCos = 0, cntOpps = 0;

    groupByCoord(pending).forEach(function(group) {
      group.forEach(function(it) {
        if (it.kind === 'pro') cntPros++;
        else if (it.kind === 'co') cntCos++;
        else cntOpps++;
      });

      // Zoom ainda afastado e há mais de um cadastro no mesmo ponto:
      // mostra um único pin numerado, bem visível, em vez de espalhar
      // marcadores que ficariam separados por poucos pixels.
      if (group.length > 1 && zoom < CLUSTER_ZOOM_THRESHOLD) {
        var lat = group[0].lat, lng = group[0].lng;
        L.marker([lat, lng], { icon: makeClusterPin(group.length) })
         .bindTooltip(group.length + ' cadastros neste local — clique para separar',
                      { direction: 'top', offset: [0, -4], className: 'nexus-tooltip' })
         .on('click', function() {
           map.setView([lat, lng], CLUSTER_ZOOM_THRESHOLD, { animate: true });
         })
         .addTo(layerClusters);
        return;
      }

      spreadGroup(group);
      group.forEach(addIndividualMarker);
    });

    layerPros.addTo(map);
    layerCos.addTo(map);
    layerOpps.addTo(map);
    layerClusters.addTo(map);

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
    var centerLat = (typeof CENTER_LAT !== 'undefined') ? CENTER_LAT : -23.5505;
    var centerLng = (typeof CENTER_LNG !== 'undefined') ? CENTER_LNG : -46.6333;

    // Inicia os pins depois do DOM (Leaflet usa classes CSS)
    PIN_PRO  = makePin('#6b6eff', 'ti-user');
    PIN_CO   = makePin('#f59e0b', 'ti-building');
    PIN_PROJ = makePin('#a78bfa', 'ti-briefcase');
    PIN_JOB  = makePin('#22c55e', 'ti-file-text');

    var worldBounds = L.latLngBounds(L.latLng(-90, -180), L.latLng(90, 180));
    map = L.map('map', {
      zoomControl: true,
      minZoom: 2,
      maxBounds: worldBounds,
      maxBoundsViscosity: 1.0
    }).setView([centerLat, centerLng], 10);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://carto.com/">CARTO</a>',
      subdomains: 'abcd', maxZoom: 19, noWrap: true
    }).addTo(map);

    // Popup style
    var style = document.createElement('style');
    style.textContent =
      '.nexus-popup .leaflet-popup-content-wrapper{' +
        'background:rgba(9,14,31,0.97);border:1px solid rgba(107,110,255,0.2);' +
        'border-radius:10px;color:#e2e8f0;box-shadow:0 8px 32px rgba(0,0,0,0.5)}' +
      '.nexus-popup .leaflet-popup-tip{background:rgba(9,14,31,0.97)}' +
      '.nexus-popup .leaflet-popup-close-button{color:#64748b}' +
      '.nexus-tooltip{background:rgba(9,14,31,0.97);border:1px solid rgba(107,110,255,0.25);' +
        'color:#e2e8f0;font-size:0.75rem;font-weight:600;border-radius:6px;padding:4px 8px}' +
      '.nexus-tooltip::before{border-top-color:rgba(9,14,31,0.97)}';
    document.head.appendChild(style);

    // Ativa "Ambos" como padrão
    var defaultEl = document.querySelector('.type-filter-option[data-type="all"]');
    if (defaultEl) defaultEl.style.background = 'rgba(107,110,255,0.1)';

    renderMarkers();

    // Re-renderiza ao dar zoom, para alternar entre o pin agrupado
    // (numerado) e os marcadores individuais espalhados.
    map.on('zoomend', renderMarkers);
  });

})();