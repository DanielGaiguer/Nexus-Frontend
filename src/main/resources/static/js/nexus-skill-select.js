// Componente reutilizável de seleção de skills — busca no catálogo (GET /app-api/skills),
// permite sugerir uma skill nova quando não encontrada (POST /app-api/skills/suggest) e
// mantém uma lista de badges removíveis. Chamado via initSkillSelect(config) em qualquer
// página que tenha um <div id="..."></div> reservado pro container.
//
// O componente NÃO gerencia nenhum input hidden de formulário sozinho — cada chamador
// decide isso via onSkillsChange(skills), porque o formato esperado pelo backend
// (@RequestParam List<Long> skillIds) é um multi-valor (vários <input name="skillIds">,
// um por id), não uma string única separada por vírgula.
(function () {

  var instances = {};

  function initSkillSelect(config) {
    var container = document.getElementById(config.containerId);
    if (!container) return;

    var state = {
      container: container,
      maxSkills: config.maxSkills || null,
      onSkillsChange: config.onSkillsChange || function () {},
      allSkills: [],
      categories: [],
      selected: [],
      selectedIds: (config.selectedSkillIds || []).map(function (id) { return Number(id); }),
      search: '',
      newSkillOpen: false
    };
    instances[config.containerId] = state;

    container.innerHTML =
      '<div class="skill-select-badges d-flex flex-wrap gap-2 mb-2"></div>' +
      '<div style="position:relative">' +
      '  <input type="text" class="nexus-input skill-select-search-input" placeholder="Buscar skill...">' +
      '  <div class="skill-select-dropdown" style="display:none;position:absolute;top:calc(100% + 4px);' +
      'left:0;right:0;background:#0d1329;border:1px solid rgba(107,110,255,0.25);border-radius:10px;' +
      'box-shadow:0 12px 32px rgba(0,0,0,0.45);z-index:2000;overflow:hidden"></div>' +
      '</div>';

    var input = container.querySelector('.skill-select-search-input');

    input.addEventListener('focus', function () {
      state.search = input.value;
      openDropdown(state);
    });
    input.addEventListener('input', function () {
      state.search = input.value;
      renderDropdown(state);
    });
    document.addEventListener('click', function (e) {
      if (!container.contains(e.target)) closeDropdown(state);
    });

    Promise.all([
      fetch('/app-api/skills').then(function (r) { return r.ok ? r.json() : []; }),
      fetch('/app-api/skills/categories').then(function (r) { return r.ok ? r.json() : []; })
    ]).then(function (results) {
      state.allSkills = Array.isArray(results[0]) ? results[0] : [];
      state.categories = Array.isArray(results[1]) ? results[1] : [];
      state.selected = state.allSkills.filter(function (sk) {
        return state.selectedIds.indexOf(sk.id) !== -1;
      });
      renderBadges(state);
      // Dispara mesmo sem interação do usuário — sem isso, um submit do formulário sem
      // mexer no seletor mandaria a lista de skills vazia e apagaria as já salvas.
      state.onSkillsChange(state.selected.slice());
    }).catch(function () {});
  }

  function openDropdown(state) {
    var dd = state.container.querySelector('.skill-select-dropdown');
    dd.style.display = 'block';
    renderDropdown(state);
  }

  function closeDropdown(state) {
    var dd = state.container.querySelector('.skill-select-dropdown');
    dd.style.display = 'none';
    state.newSkillOpen = false;
  }

  function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str == null ? '' : String(str);
    return div.innerHTML;
  }

  function atMax(state) {
    return state.maxSkills != null && state.selected.length >= state.maxSkills;
  }

  // ---- badges (skills já selecionadas) ----

  function renderBadges(state) {
    var wrap = state.container.querySelector('.skill-select-badges');
    if (!state.selected.length) {
      wrap.innerHTML = '<span style="color:#475569;font-size:0.82rem">Nenhuma skill selecionada ainda.</span>';
    } else {
      wrap.innerHTML = state.selected.map(function (sk) {
        return '<span class="nexus-chip d-inline-flex align-items-center gap-1" data-skill-id="' + sk.id + '">' +
          escapeHtml(sk.name) +
          '<i class="ti ti-x skill-remove-btn" style="cursor:pointer;font-size:0.8rem" data-skill-id="' + sk.id + '"></i>' +
          '</span>';
      }).join('');
      wrap.querySelectorAll('.skill-remove-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
          removeSkill(state, Number(btn.dataset.skillId));
        });
      });
    }
  }

  function addSkill(state, skill) {
    if (state.selected.some(function (sk) { return sk.id === skill.id; })) return;
    if (atMax(state)) return;
    state.selected.push(skill);
    renderBadges(state);
    renderDropdown(state);
    state.onSkillsChange(state.selected.slice());
  }

  function removeSkill(state, skillId) {
    state.selected = state.selected.filter(function (sk) { return sk.id !== skillId; });
    renderBadges(state);
    renderDropdown(state);
    state.onSkillsChange(state.selected.slice());
  }

  // ---- dropdown (busca + lista + rodapé "cadastrar nova") ----

  function renderDropdown(state) {
    var dd = state.container.querySelector('.skill-select-dropdown');
    if (dd.style.display === 'none') return;

    var term = state.search.trim().toLowerCase();
    var results = !term ? state.allSkills : state.allSkills.filter(function (sk) {
      return sk.name.toLowerCase().indexOf(term) !== -1 ||
        (sk.category && sk.category.toLowerCase().indexOf(term) !== -1);
    });
    results = results.slice(0, 50);

    var selectedIds = state.selected.map(function (sk) { return sk.id; });
    var maxed = atMax(state);

    var listHtml = !results.length
      ? '<div style="padding:0.6rem 0.85rem;color:#475569;font-size:0.82rem">Nenhuma skill encontrada.</div>'
      : results.map(function (sk) {
          var isSelected = selectedIds.indexOf(sk.id) !== -1;
          var disabled = isSelected || maxed;
          return '<div class="skill-option' + (disabled ? ' disabled' : '') + '"' +
            ' data-skill-id="' + sk.id + '"' +
            ' style="padding:0.5rem 0.85rem;cursor:' + (disabled ? 'default' : 'pointer') + ';' +
            'display:flex;align-items:center;justify-content:space-between;gap:0.5rem;' +
            (disabled ? 'opacity:0.45' : '') + '">' +
            '<span style="color:#e2e8f0;font-size:0.85rem">' + escapeHtml(sk.name) + '</span>' +
            '<span style="color:#64748b;font-size:0.72rem">' + escapeHtml(sk.category || '') +
            (isSelected ? ' · selecionada' : '') + '</span>' +
            '</div>';
        }).join('');

    dd.innerHTML =
      '<div class="skill-option-list" style="max-height:220px;overflow-y:auto">' + listHtml + '</div>' +
      '<div class="skill-select-footer" style="border-top:1px solid rgba(255,255,255,0.08);padding:0.6rem 0.85rem">' +
      '  <div class="skill-select-footer-prompt" style="color:#64748b;font-size:0.78rem;margin-bottom:0.4rem">' +
      (maxed
        ? 'Limite de ' + state.maxSkills + ' skills atingido.'
        : 'Skill não encontrada?') +
      '  </div>' +
      (maxed ? '' :
        '  <button type="button" class="nexus-btn nexus-btn-ghost nexus-btn-sm skill-new-toggle-btn">' +
        '    <i class="ti ti-plus"></i> Cadastrar nova skill' +
        '  </button>' +
        '  <div class="skill-new-form" style="display:none;margin-top:0.6rem"></div>'
      ) +
      '</div>';

    dd.querySelectorAll('.skill-option:not(.disabled)').forEach(function (el) {
      el.addEventListener('click', function () {
        var sk = state.allSkills.find(function (s) { return s.id === Number(el.dataset.skillId); });
        if (sk) addSkill(state, sk);
      });
    });

    var toggleBtn = dd.querySelector('.skill-new-toggle-btn');
    if (toggleBtn) {
      toggleBtn.addEventListener('click', function (e) {
        e.stopPropagation();
        state.newSkillOpen = !state.newSkillOpen;
        renderNewSkillForm(state, dd);
      });
    }
    if (state.newSkillOpen) renderNewSkillForm(state, dd);
  }

  // ---- mini-formulário "cadastrar nova skill" ----

  function renderNewSkillForm(state, dd) {
    var formWrap = dd.querySelector('.skill-new-form');
    if (!formWrap) return;

    if (!state.newSkillOpen) {
      formWrap.style.display = 'none';
      formWrap.innerHTML = '';
      return;
    }

    formWrap.style.display = 'block';
    formWrap.innerHTML =
      '<div class="mb-2">' +
      '  <label class="nexus-label">Nome da skill</label>' +
      '  <input type="text" class="nexus-input skill-new-name" maxlength="60" placeholder="Ex: GraphQL">' +
      '  <div class="skill-new-name-error" style="display:none;color:#ef4444;font-size:0.75rem;margin-top:3px"></div>' +
      '</div>' +
      '<div class="mb-2">' +
      '  <label class="nexus-label">Categoria</label>' +
      '  <select class="nexus-select skill-new-category">' +
      '    <option value="">Selecione...</option>' +
      state.categories.map(function (c) {
        return '<option value="' + escapeHtml(c) + '">' + escapeHtml(c) + '</option>';
      }).join('') +
      '  </select>' +
      '  <div class="skill-new-category-error" style="display:none;color:#ef4444;font-size:0.75rem;margin-top:3px"></div>' +
      '</div>' +
      '<div class="d-flex gap-2 justify-content-end">' +
      '  <button type="button" class="nexus-btn nexus-btn-ghost nexus-btn-sm skill-new-cancel">Cancelar</button>' +
      '  <button type="button" class="nexus-btn nexus-btn-primary nexus-btn-sm skill-new-submit">Cadastrar</button>' +
      '</div>';

    formWrap.querySelector('.skill-new-name').addEventListener('click', function (e) { e.stopPropagation(); });

    // Select criado dinamicamente aqui, depois do DOMContentLoaded — o upgrade automático
    // pro visual padrão (TomSelect) em nexus-common.js só roda uma vez, no carregamento da
    // página, então precisa ser chamado manualmente pra esse select não ficar com a aparência
    // nativa do navegador, destoando dos demais selects do sistema.
    var categorySelect = formWrap.querySelector('.skill-new-category');
    if (typeof nexusInitSelect === 'function') {
      var ts = nexusInitSelect(categorySelect);
      // TomSelect abre o dropdown de opções direto no <body> (dropdownParent:'body'), fora
      // do container do skill-select — sem isso, o clique numa categoria seria visto como
      // "clique fora" e fecharia o dropdown de skills inteiro antes de selecionar.
      if (ts) {
        ts.wrapper.addEventListener('click', function (e) { e.stopPropagation(); });
        ts.dropdown.addEventListener('click', function (e) { e.stopPropagation(); });
      }
    } else {
      categorySelect.addEventListener('click', function (e) { e.stopPropagation(); });
    }

    formWrap.querySelector('.skill-new-cancel').addEventListener('click', function (e) {
      e.stopPropagation();
      state.newSkillOpen = false;
      renderDropdown(state);
    });

    formWrap.querySelector('.skill-new-submit').addEventListener('click', function (e) {
      e.stopPropagation();
      submitNewSkill(state, formWrap);
    });
  }

  function submitNewSkill(state, formWrap) {
    var nameInput = formWrap.querySelector('.skill-new-name');
    var categorySelect = formWrap.querySelector('.skill-new-category');
    var nameError = formWrap.querySelector('.skill-new-name-error');
    var categoryError = formWrap.querySelector('.skill-new-category-error');
    var submitBtn = formWrap.querySelector('.skill-new-submit');

    var name = nameInput.value.trim();
    var category = categorySelect.value;

    nameError.style.display = 'none';
    categoryError.style.display = 'none';

    if (!name) {
      nameError.textContent = 'Informe o nome da skill.';
      nameError.style.display = 'block';
      return;
    }
    if (!category) {
      categoryError.textContent = 'Selecione uma categoria.';
      categoryError.style.display = 'block';
      return;
    }

    submitBtn.disabled = true;

    fetch('/app-api/skills/suggest', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: name, category: category })
    })
      .then(function (r) {
        return r.json().then(function (body) { return { status: r.status, body: body }; })
          .catch(function () { return { status: r.status, body: null }; });
      })
      .then(function (result) {
        submitBtn.disabled = false;

        if (result.status === 201 && result.body) {
          if (typeof nexusToast === 'function') nexusToast('Skill cadastrada com sucesso!', 'success');
          state.allSkills.push(result.body);
          addSkill(state, result.body);
          state.newSkillOpen = false;
          renderDropdown(state);
        } else if (result.status === 200 && result.body) {
          if (typeof nexusToast === 'function') {
            nexusToast('Skill já existe no catálogo, foi adicionada ao seu perfil.', 'success');
          }
          if (!state.allSkills.some(function (s) { return s.id === result.body.id; })) {
            state.allSkills.push(result.body);
          }
          addSkill(state, result.body);
          state.newSkillOpen = false;
          renderDropdown(state);
        } else if (result.status === 400) {
          var msg = (result.body && result.body.message) || 'Não foi possível cadastrar a skill.';
          if (msg.toLowerCase().indexOf('categor') !== -1) {
            categoryError.textContent = msg;
            categoryError.style.display = 'block';
          } else {
            nameError.textContent = msg;
            nameError.style.display = 'block';
          }
        } else {
          nameError.textContent = 'Não foi possível cadastrar a skill. Tente novamente.';
          nameError.style.display = 'block';
        }
      })
      .catch(function () {
        submitBtn.disabled = false;
        nameError.textContent = 'Não foi possível cadastrar a skill. Tente novamente.';
        nameError.style.display = 'block';
      });
  }

  window.initSkillSelect = initSkillSelect;
})();
