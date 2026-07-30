
(function() {
  'use strict';

  var map;
  var layerPros     = null;
  var layerCos      = null;
  var layerOpps     = null;
  var layerClusters = null;

  var currentRadius     = (typeof DEFAULT_RADIUS !== 'undefined') ? DEFAULT_RADIUS : 50;
  var currentType       = 'all';
  var currentOppType    = '';

  var CLUSTER_ZOOM_THRESHOLD = 17;

  function distanceKm(lat1, lng1, lat2, lng2) {
    var R = 6371;
    var dLat = (lat2 - lat1) * Math.PI / 180;
    var dLng = (lng2 - lng1) * Math.PI / 180;
    var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }

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

  var PIN_PRO  = null;
  var PIN_CO   = null;
  var PIN_PROJ = null;
  var PIN_JOB  = null;
  var PIN_ME   = null;

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

  function translateWorkMode(wm) {
    var m = { 'REMOTE':'Remoto', 'ONSITE':'Presencial', 'HYBRID':'Híbrido' };
    return m[wm] || wm || '—';
  }

  function translateOppType(t) {
    return t === 'JOB' ? 'Vaga de emprego' : 'Projeto';
  }

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

  function popupOpp(o) {
    var typeColor = o.opportunityType === 'JOB' ? '#86efac' : '#a5b4fc';
    var typeBg    = o.opportunityType === 'JOB' ? 'rgba(34,197,94,0.12)' : 'rgba(107,110,255,0.12)';
    var typeLabel = o.opportunityType === 'JOB' ? 'Vaga de emprego' : 'Projeto';

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

  function groupByCoord(items) {
    var groups = {};
    var order = [];
    items.forEach(function(it) {
      var key = it.lat.toFixed(4) + ',' + it.lng.toFixed(4);
      if (!groups[key]) { groups[key] = []; order.push(key); }
      groups[key].push(it);
    });
    return order.map(function(key) { return groups[key]; });
  }

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

  function renderMarkers() {
    if (!map) return;

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

    var pending = [];

    if (currentType === 'all' || currentType === 'professionals') {
      var pros = (typeof PROFESSIONALS !== 'undefined') ? PROFESSIONALS : [];
      pros.forEach(function(p) {
        if (!p.latitude || !p.longitude) return;
        if (distanceKm(centerLat, centerLng, p.latitude, p.longitude) > currentRadius) return;
        pending.push({ kind: 'pro', lat: p.latitude, lng: p.longitude, data: p });
      });
    }

    if (currentType === 'all' || currentType === 'companies') {
      var cos = (typeof COMPANIES !== 'undefined') ? COMPANIES : [];
      cos.forEach(function(c) {
        if (!c.latitude || !c.longitude) return;
        if (distanceKm(centerLat, centerLng, c.latitude, c.longitude) > currentRadius) return;
        pending.push({ kind: 'co', lat: c.latitude, lng: c.longitude, data: c });
      });
    }

    if (currentType === 'all' || currentType === 'opportunities') {
      var opps = (typeof OPPORTUNITIES !== 'undefined') ? OPPORTUNITIES : [];
      opps.forEach(function(o) {
        if (!o.latitude || !o.longitude) return;
        if (distanceKm(centerLat, centerLng, o.latitude, o.longitude) > currentRadius) return;
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

    document.querySelectorAll('.type-filter-option').forEach(function(l) {
      l.style.background = '';
    });
    if (el) el.style.background = 'rgba(107,110,255,0.1)';

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

  document.addEventListener('DOMContentLoaded', function() {
    var centerLat = (typeof CENTER_LAT !== 'undefined') ? CENTER_LAT : -23.5505;
    var centerLng = (typeof CENTER_LNG !== 'undefined') ? CENTER_LNG : -46.6333;

    PIN_PRO  = makePin('#6b6eff', 'ti-user');
    PIN_CO   = makePin('#f59e0b', 'ti-building');
    PIN_PROJ = makePin('#a78bfa', 'ti-briefcase');
    PIN_JOB  = makePin('#22c55e', 'ti-file-text');
    PIN_ME   = makePin('#ef4444', 'ti-map-pin');

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

    var myLat = (typeof MY_LAT !== 'undefined') ? MY_LAT : null;
    var myLng = (typeof MY_LNG !== 'undefined') ? MY_LNG : null;
    if (myLat != null && myLng != null) {
      L.marker([myLat, myLng], { icon: PIN_ME, zIndexOffset: 1000 })
       .bindPopup(
         '<div style="font-family:Inter,sans-serif;font-weight:700;color:#fff">' +
         'Você está aqui</div>',
         { className: 'nexus-popup' })
       .addTo(map);
    }

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

    var defaultEl = document.querySelector('.type-filter-option[data-type="all"]');
    if (defaultEl) defaultEl.style.background = 'rgba(107,110,255,0.1)';

    renderMarkers();

    map.on('zoomend', renderMarkers);
  });

})();