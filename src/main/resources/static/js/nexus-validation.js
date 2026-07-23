/**
 * nexus-validation.js — Utilitários de validação com mensagens em português
 */

function validateEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test((email || '').trim());
}

function validateCEP(cep) {
  return /^\d{8}$/.test((cep || '').replace(/\D/g, ''));
}

function validateCNPJ(cnpj) {
  return /^\d{14}$/.test((cnpj || '').replace(/\D/g, ''));
}

function validatePhone(phone) {
  var digits = (phone || '').replace(/\D/g, '');
  return digits.length >= 10 && digits.length <= 11;
}

function validatePassword(password) {
  return (password || '').length >= 6;
}

/**
 * Retorna uma mensagem de erro legível para um campo, ou null se válido.
 */
function getFieldError(name, value) {
  var v = (value || '').trim();
  switch (name) {
    case 'email':
      if (!v) return 'O e-mail é obrigatório.';
      if (!validateEmail(v)) return 'Informe um e-mail válido (ex: nome@dominio.com).';
      return null;
    case 'password':
      if (!v) return 'A senha é obrigatória.';
      if (!validatePassword(v)) return 'A senha deve ter no mínimo 6 caracteres.';
      return null;
    case 'name':
      if (!v) return 'O nome é obrigatório.';
      if (v.split(' ').filter(function(p) { return p.length > 0; }).length < 2)
        return 'Informe o nome completo (nome e sobrenome).';
      return null;
    case 'companyName':
      if (!v) return 'O nome da empresa é obrigatório.';
      if (v.length < 2) return 'O nome da empresa deve ter ao menos 2 caracteres.';
      return null;
    case 'phone':
      if (!v) return 'O telefone é obrigatório.';
      if (!validatePhone(v)) return 'Informe um telefone válido com DDD (ex: 43 99999-9999).';
      return null;
    case 'cep':
      if (!v) return 'O CEP é obrigatório.';
      if (!validateCEP(v)) return 'CEP inválido. Informe somente 8 números (ex: 86010100).';
      return null;
    case 'taxId':
    case 'cnpj':
      if (!v) return 'O CNPJ é obrigatório.';
      if (!validateCNPJ(v)) return 'CNPJ inválido. Informe 14 dígitos sem pontuação.';
      return null;
    default:
      return null;
  }
}

/**
 * Exibe inline o erro de um campo específico.
 * O campo deve ter um irmão seguinte com class="field-error-msg" para exibir o texto.
 */
function showFieldError(inputEl, message) {
  var errEl = inputEl.parentElement.querySelector('.field-error-msg');
  if (!errEl) {
    errEl = document.createElement('div');
    errEl.className = 'field-error-msg';
    errEl.style.cssText =
      'color:#f87171;font-size:0.75rem;margin-top:4px;display:flex;' +
      'align-items:center;gap:4px';
    inputEl.parentElement.appendChild(errEl);
  }
  if (message) {
    errEl.innerHTML = '<i class="ti ti-alert-triangle" style="font-size:0.7rem"></i>' + message;
    errEl.style.display = 'flex';
    inputEl.style.borderColor = 'rgba(239,68,68,0.5)';
    inputEl.style.boxShadow   = '0 0 0 3px rgba(239,68,68,0.1)';
  } else {
    errEl.style.display = 'none';
    inputEl.style.borderColor = '';
    inputEl.style.boxShadow   = '';
  }
}

/**
 * Valida um input ao perder o foco e ao digitar.
 */
function attachInlineValidation(inputEl) {
  function validate() {
    var err = getFieldError(inputEl.name, inputEl.value);
    showFieldError(inputEl, err);
  }
  inputEl.addEventListener('blur',  validate);
  inputEl.addEventListener('input', function() {
    // Limpa o erro enquanto digita (só mostra de novo no blur)
    showFieldError(inputEl, null);
  });
}

/**
 * attachSubmitGuard — desabilita o botão de submit até validatorFn() retornar true.
 * Re-avalia a cada evento "input".
 */
function attachSubmitGuard(formEl, validatorFn) {
  if (!formEl) return;
  var btn = formEl.querySelector('[type="submit"]');
  function check() {
    var valid = validatorFn();
    if (btn) btn.disabled = !valid;
  }
  formEl.addEventListener('input',  check);
  formEl.addEventListener('change', check);
  check();
}

/**
 * initFormValidation — inicializa validação inline em todos os campos
 * de um formulário que tenham o atributo name reconhecido.
 */
function initFormValidation(formEl) {
  if (!formEl) return;
  var fields = formEl.querySelectorAll('input[name]');
  fields.forEach(function(field) {
    attachInlineValidation(field);
  });
}
