/**
 * Calcula e renderiza o badge de completude do perfil.
 * Recebe um objeto profileData e um elemento container onde renderizar.
 *
 * Uso:
 *   NexusCompleteness.render(profileData, document.getElementById('completenessWidget'));
 */
var NexusCompleteness = ( function() {

  var CRITERIA_PRO = [
    { key: 'name',           label: 'Nome completo',           weight: 10 },
    { key: 'phone',          label: 'Telefone',                weight: 8  },
    { key: 'cep',            label: 'Localização (CEP)',       weight: 10 },
    { key: 'profilePhotoUrl',label: 'Foto de perfil',          weight: 12 },
    { key: 'skills',         label: 'Skills (mín. 3)',         weight: 18, minLength: 3 },
    { key: 'projects',       label: 'Projetos anteriores (mín. 1)', weight: 15, minLength: 1 },
    { key: 'experienceLevel',label: 'Nível de experiência',   weight: 10 },
    { key: 'salaryFilled',   label: 'Pretensão salarial',      weight: 10 },
    { key: 'preferredTypes', label: 'Tipos de oportunidade',   weight: 7,  minLength: 1 },
    { key: 'preferredOpportunityTypes', label: 'Regime desejado (Vaga/Projeto)', weight: 5, minLength: 1 },
    { key: 'linkedinUrl',    label: 'LinkedIn',                weight: 8  },
  ];

  var CRITERIA_CO = [
    { key: 'companyName',    label: 'Nome da empresa',         weight: 10 },
    { key: 'phone',          label: 'Telefone',                weight: 8  },
    { key: 'cep',            label: 'Localização (CEP)',       weight: 10 },
    { key: 'profilePhotoUrl',label: 'Logo/foto da empresa',    weight: 15 },
    { key: 'description',    label: 'Descrição da empresa',    weight: 20 },
    { key: 'taxId',          label: 'CNPJ',                    weight: 12 },
    { key: 'email',          label: 'E-mail de contato',       weight: 10 },
    { key: 'city',           label: 'Cidade e estado',         weight: 15 },
    { key: 'linkedinUrl',    label: 'LinkedIn',                weight: 8  },
  ];

  function isFilled(profile, criterion) {
    var val = profile[criterion.key];
    if (val === null || val === undefined || val === '') return false;
    if (criterion.minLength && Array.isArray(val)) return val.length >= criterion.minLength;
    if (criterion.minLength && typeof val === 'number') return val > 0;
    if (typeof val === 'number') return val > 0;
    if (typeof val === 'string') return val.trim().length > 0;
    if (Array.isArray(val)) return val.length > 0;
    return !!val;
  }

  function calculate(profile, isCompany) {
    var criteria = isCompany ? CRITERIA_CO : CRITERIA_PRO;
    var totalWeight = criteria.reduce(function(s, c) { return s + c.weight; }, 0);
    var earnedWeight = 0;
    var missing = [];
    var filled  = [];

    criteria.forEach(function(c) {
      if (isFilled(profile, c)) {
        earnedWeight += c.weight;
        filled.push(c);
      } else {
        missing.push(c);
      }
    });

    return {
      percent: Math.round((earnedWeight / totalWeight) * 100),
      missing: missing,
      filled:  filled
    };
  }

  function getColor(pct) {
    if (pct >= 90) return '#22c55e';
    if (pct >= 70) return '#6b6eff';
    if (pct >= 50) return '#f59e0b';
    return '#ef4444';
  }

  function getLabel(pct) {
    if (pct >= 90) return 'Perfil completo';
    if (pct >= 70) return 'Quase lá';
    if (pct >= 50) return 'Em andamento';
    return 'Perfil incompleto';
  }

  function render(profile, container, isCompany) {
    if (!container) return;
    var result = calculate(profile, !!isCompany);
    var color  = getColor(result.percent);
    var label  = getLabel(result.percent);

    var missingHtml = '';
    if (result.missing.length > 0) {
      missingHtml = '<div style="margin-top:0.75rem">' +
        '<div style="color:#64748b;font-size:0.72rem;text-transform:uppercase;' +
        'letter-spacing:0.05em;margin-bottom:0.4rem">O que está faltando</div>' +
        result.missing.map(function(c) {
          return '<div style="display:flex;align-items:center;gap:0.4rem;' +
            'color:#94a3b8;font-size:0.8rem;margin-bottom:3px">' +
            '<i class="ti ti-circle-dashed" style="color:#475569;font-size:0.7rem"></i>' +
            c.label + '</div>';
        }).join('') + '</div>';
    }

    container.innerHTML =
      '<div style="background:rgba(13,19,41,0.9);border:1px solid rgba(107,110,255,0.1);' +
      'border-radius:12px;padding:1rem">' +

        '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:0.5rem">' +
          '<span style="font-size:0.78rem;font-weight:600;color:#94a3b8;text-transform:uppercase;' +
          'letter-spacing:0.05em">Completude do perfil</span>' +
          '<span style="font-size:1rem;font-weight:700;color:' + color + '">' +
          result.percent + '%</span>' +
        '</div>' +

        '<div style="height:6px;background:rgba(255,255,255,0.06);border-radius:4px;margin-bottom:0.4rem">' +
          '<div style="height:100%;border-radius:4px;background:' + color + ';' +
          'width:' + result.percent + '%;transition:width 0.8s ease"></div>' +
        '</div>' +

        '<div style="font-size:0.75rem;color:' + color + ';font-weight:500">' + label + '</div>' +

        missingHtml +

      '</div>';
  }

  return { render: render, calculate: calculate };
})();