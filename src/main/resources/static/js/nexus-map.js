/* nexus-map.js — Leaflet map with professionals and companies pins */
(function() {
  'use strict';

  // Parse JSON data if strings
  if (typeof PROFESSIONALS === 'string') PROFESSIONALS = JSON.parse(PROFESSIONALS);
  if (typeof COMPANIES === 'string') COMPANIES = JSON.parse(COMPANIES);

  // Initialize map
  var map = L.map('map', {
    zoomControl: false
  }).setView([USER_LAT, USER_LNG], 11);

  L.control.zoom({ position: 'topright' }).addTo(map);

  // Dark tile layer
  L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; OpenStreetMap &copy; CARTO',
    maxZoom: 19
  }).addTo(map);

  // User marker
  var userIcon = L.divIcon({
    className: '',
    html: '<div style="width:16px;height:16px;border-radius:50%;background:#ef4444;border:3px solid rgba(239,68,68,0.3);box-shadow:0 0 12px rgba(239,68,68,0.4)"></div>',
    iconSize: [16, 16],
    iconAnchor: [8, 8]
  });
  L.marker([USER_LAT, USER_LNG], { icon: userIcon }).addTo(map)
    .bindPopup('<div style="font-size:0.85rem;font-weight:600">Sua localização</div>');

  var profLayer = L.layerGroup();
  var compLayer = L.layerGroup();

  // Haversine distance in km
  function haversine(lat1, lng1, lat2, lng2) {
    var R = 6371;
    var dLat = (lat2 - lat1) * Math.PI / 180;
    var dLng = (lng2 - lng1) * Math.PI / 180;
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  }

  // Experience level label
  function expLabel(level) {
    var map = {
      'INTERNSHIP': 'Estágio', 'TRAINEE': 'Trainee', 'JUNIOR': 'Júnior',
      'PLENO': 'Pleno', 'SENIOR': 'Sênior'
    };
    return map[level] || level || '—';
  }

  // Render professionals
  (PROFESSIONALS || []).forEach(function(p) {
    if (p.latitude == null || p.longitude == null) return;
    var icon = L.divIcon({
      className: '',
      html: '<div style="width:32px;height:32px;border-radius:50%;background:#6b6eff;display:flex;align-items:center;justify-content:center;box-shadow:0 2px 8px rgba(107,110,255,0.4);border:2px solid rgba(107,110,255,0.2)"><i class="ti ti-user" style="color:#fff;font-size:14px"></i></div>',
      iconSize: [32, 32],
      iconAnchor: [16, 16]
    });

    var skills = (p.skills || []).join(', ') || 'Sem skills';
    var popup = '<div style="min-width:160px">' +
      '<div style="font-weight:600;font-size:0.9rem;margin-bottom:4px">' + (p.name || 'Profissional') + '</div>' +
      '<div style="color:#64748b;font-size:0.78rem;margin-bottom:4px">' + (p.city || '') + (p.state ? ', ' + p.state : '') + '</div>' +
      '<div style="color:#6b6eff;font-size:0.75rem;margin-bottom:4px">' + expLabel(p.experienceLevel) + '</div>' +
      '<div style="color:#94a3b8;font-size:0.75rem">' + skills + '</div>' +
      (p.reputation ? '<div style="color:#f59e0b;font-size:0.75rem;margin-top:4px">★ ' + p.reputation.toFixed(1) + '</div>' : '') +
      '</div>';

    var marker = L.marker([p.latitude, p.longitude], { icon: icon });
    marker.bindPopup(popup);
    marker._nexusType = 'professional';
    marker._nexusDist = haversine(USER_LAT, USER_LNG, p.latitude, p.longitude);
    profLayer.addLayer(marker);
  });

  // Render companies
  (COMPANIES || []).forEach(function(c) {
    if (c.latitude == null || c.longitude == null) return;
    var icon = L.divIcon({
      className: '',
      html: '<div style="width:32px;height:32px;border-radius:50%;background:#f59e0b;display:flex;align-items:center;justify-content:center;box-shadow:0 2px 8px rgba(245,158,11,0.4);border:2px solid rgba(245,158,11,0.2)"><i class="ti ti-building" style="color:#fff;font-size:14px"></i></div>',
      iconSize: [32, 32],
      iconAnchor: [16, 16]
    });

    var popup = '<div style="min-width:160px">' +
      '<div style="font-weight:600;font-size:0.9rem;margin-bottom:4px">' + (c.companyName || 'Empresa') + '</div>' +
      '<div style="color:#64748b;font-size:0.78rem;margin-bottom:4px">' + (c.city || '') + (c.state ? ', ' + c.state : '') + '</div>' +
      (c.openProjects > 0 ? '<div style="color:#94a3b8;font-size:0.75rem">' + c.openProjects + ' projeto(s) aberto(s)</div>' : '') +
      (c.reputation ? '<div style="color:#f59e0b;font-size:0.75rem;margin-top:4px">★ ' + c.reputation.toFixed(1) + '</div>' : '') +
      '</div>';

    var marker = L.marker([c.latitude, c.longitude], { icon: icon });
    marker.bindPopup(popup);
    marker._nexusType = 'company';
    marker._nexusDist = haversine(USER_LAT, USER_LNG, c.latitude, c.longitude);
    compLayer.addLayer(marker);
  });

  profLayer.addTo(map);
  compLayer.addTo(map);

  // Current filters
  var currentType = 'all';
  var currentDistance = 50; // default 50km

  function applyFilters() {
    // Remove all markers
    map.removeLayer(profLayer);
    map.removeLayer(compLayer);

    // Re-add filtered
    var newProf = L.layerGroup();
    var newComp = L.layerGroup();

    profLayer.eachLayer(function(m) {
      var typeOk = currentType === 'all' || currentType === 'professionals';
      var distOk = currentDistance === 0 || m._nexusDist <= currentDistance;
      if (typeOk && distOk) newProf.addLayer(m);
    });

    compLayer.eachLayer(function(m) {
      var typeOk = currentType === 'all' || currentType === 'companies';
      var distOk = currentDistance === 0 || m._nexusDist <= currentDistance;
      if (typeOk && distOk) newComp.addLayer(m);
    });

    profLayer = newProf;
    compLayer = newComp;
    profLayer.addTo(map);
    compLayer.addTo(map);
  }

  // Global filter functions
  window.filterMapType = function(type) {
    currentType = type;
    document.querySelectorAll('.map-type-btn').forEach(function(btn) {
      btn.classList.remove('nexus-btn-primary');
      btn.classList.add('nexus-btn-ghost');
    });
    document.querySelector('.map-type-btn[data-type="' + type + '"]').classList.remove('nexus-btn-ghost');
    document.querySelector('.map-type-btn[data-type="' + type + '"]').classList.add('nexus-btn-primary');
    applyFilters();
  };

  window.filterDistance = function(km) {
    currentDistance = km;
    document.querySelectorAll('.dist-btn').forEach(function(btn) {
      btn.classList.remove('nexus-btn-primary');
      btn.classList.add('nexus-btn-ghost');
    });
    document.querySelector('.dist-btn[data-km="' + km + '"]').classList.remove('nexus-btn-ghost');
    document.querySelector('.dist-btn[data-km="' + km + '"]').classList.add('nexus-btn-primary');
    applyFilters();
  };

})();
