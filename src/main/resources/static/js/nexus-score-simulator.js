var NexusScoreSimulator = (function() {

  var _opts = null;

  function calcSkillScore(numSkills) {
    if (numSkills === 0) return 0;
    var assumed = 4;
    return Math.min((numSkills / assumed) * 100, 100);
  }

  function calcBudgetScore(minSalary, maxSalary, avgBudget) {
    if (!minSalary || !maxSalary || !avgBudget) return 50;
    var avgPret = (minSalary + maxSalary) / 2;
    if (avgPret <= avgBudget) {
      var ratio = avgPret / avgBudget;
      return Math.min(100, 80 + (ratio * 20));
    } else {
      var excess = (avgPret - avgBudget) / avgBudget;
      return Math.max(0, 100 - (excess * 100));
    }
  }

  function calcHistoryScore(numProjects) {
    return Math.min(numProjects * 10, 100);
  }

  function calcReputationScore(reputation) {
    if (!reputation) return 50;
    return (reputation / 5.0) * 100;
  }

  function calcAvailabilityScore(available) {
    return available ? 100 : 0;
  }

  function simulate() {
    if (!_opts) return;

    var skills     = _opts.getSkills()     || 0;
    var minSalary  = _opts.getMinSalary()  || 0;
    var maxSalary  = _opts.getMaxSalary()  || 0;
    var history    = _opts.getHistory()    || 0;
    var reputation = _opts.getReputation() || 0;
    var available  = _opts.getAvailable()  !== false;
    var avgBudget  = _opts.avgProjectBudget || 10000;

    var s = (calcSkillScore(skills)                       * 0.35) +
            (calcBudgetScore(minSalary, maxSalary, avgBudget) * 0.25) +
            (calcHistoryScore(history)                     * 0.20) +
            (calcReputationScore(reputation)               * 0.10) +
            (calcAvailabilityScore(available)              * 0.10);

    var score = Math.round(Math.min(Math.max(s, 0), 100));
    render(score, skills, history, available, minSalary, maxSalary, avgBudget);
  }

  function getBarColor(val) {
    if (val >= 80) return '#22c55e';
    if (val >= 60) return '#6b6eff';
    if (val >= 40) return '#f59e0b';
    return '#ef4444';
  }

  function makeBar(label, value, weight) {
    var color = getBarColor(value);
    var pct   = Math.round(value);
    return '<div style="margin-bottom:0.5rem">' +
      '<div style="display:flex;justify-content:space-between;margin-bottom:3px">' +
        '<span style="color:#64748b;font-size:0.75rem">' + label +
          ' <span style="color:#334155;font-size:0.68rem">×' + (weight * 100).toFixed(0) + '%</span>' +
        '</span>' +
        '<span style="color:#e2e8f0;font-size:0.75rem;font-weight:600">' + pct + '</span>' +
      '</div>' +
      '<div style="height:4px;background:rgba(255,255,255,0.05);border-radius:4px">' +
        '<div style="height:100%;border-radius:4px;background:' + color + ';' +
        'width:' + pct + '%;transition:width 0.4s ease"></div>' +
      '</div>' +
    '</div>';
  }

  function render(score, skills, history, available, minSal, maxSal, avgBudget) {
    var container = _opts.containerEl;
    if (!container) return;

    var color     = score >= 85 ? '#22c55e' : (score >= 70 ? '#6b6eff' : (score >= 50 ? '#f59e0b' : '#ef4444'));
    var label     = score >= 85 ? 'Excelente' : (score >= 70 ? 'Bom' : (score >= 50 ? 'Regular' : 'Baixo'));

    var sSkill  = Math.round(calcSkillScore(skills));
    var sBudget = Math.round(calcBudgetScore(minSal, maxSal, avgBudget));
    var sHist   = Math.round(calcHistoryScore(history));
    var sRep    = Math.round(calcReputationScore(_opts.getReputation() || 0));
    var sAvail  = available ? 100 : 0;

    container.innerHTML =
      '<div style="background:rgba(13,19,41,0.9);border:1px solid rgba(107,110,255,0.15);' +
      'border-radius:12px;padding:1rem">' +

        '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:0.75rem">' +
          '<div>' +
            '<div style="font-size:0.72rem;font-weight:700;text-transform:uppercase;' +
            'letter-spacing:0.07em;color:#64748b">Score simulado</div>' +
            '<div style="font-size:0.65rem;color:#334155;margin-top:1px">' +
            'baseado nas vagas abertas</div>' +
          '</div>' +
          '<div style="text-align:right">' +
            '<div style="font-size:1.75rem;font-weight:800;color:' + color + ';line-height:1;' +
            'transition:color 0.4s">' + score + '%</div>' +
            '<div style="font-size:0.7rem;color:' + color + ';font-weight:600">' + label + '</div>' +
          '</div>' +
        '</div>' +

        '<div style="height:6px;background:rgba(255,255,255,0.05);border-radius:4px;margin-bottom:1rem">' +
          '<div style="height:100%;border-radius:4px;background:' + color + ';' +
          'width:' + score + '%;transition:width 0.5s ease,background 0.4s"></div>' +
        '</div>' +

        makeBar('Skills',        sSkill,  0.35) +
        makeBar('Orçamento',     sBudget, 0.25) +
        makeBar('Histórico',     sHist,   0.20) +
        makeBar('Reputação',     sRep,    0.10) +
        makeBar('Disponibilidade', sAvail, 0.10) +

        '<div style="margin-top:0.75rem;padding-top:0.6rem;' +
        'border-top:1px solid rgba(255,255,255,0.05);' +
        'font-size:0.7rem;color:#334155;text-align:center">' +
        '⚡ Simulação baseada no Cenário 1 da fórmula — modalidade remota sem nível de experiência</div>' +

      '</div>';
  }

  function init(opts) {
    _opts = opts;
    simulate();
  }

  return { init: init, simulate: simulate };
})();