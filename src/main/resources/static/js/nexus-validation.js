/**
 * nexus-validation.js — Client-side validation utilities
 */

function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validateCEP(cep) {
    return /^\d{8}$/.test(cep.replace(/\D/g, ''));
}

function validateCNPJ(cnpj) {
    return /^\d{14}$/.test(cnpj.replace(/\D/g, ''));
}

function validatePhone(phone) {
    var digits = phone.replace(/\D/g, '');
    return digits.length >= 10 && digits.length <= 11;
}

/**
 * Attaches a submit guard to a form element.
 * The submit button is disabled until validatorFn() returns true.
 * Re-evaluates on every input event.
 * @param {HTMLFormElement} formEl - the form element
 * @param {function} validatorFn - returns true when form is valid
 */
function attachSubmitGuard(formEl, validatorFn) {
    if (!formEl) return;
    var btn = formEl.querySelector('[type="submit"]');
    function check() {
        var valid = validatorFn();
        if (btn) btn.disabled = !valid;
    }
    formEl.addEventListener('input', check);
    check();
}
