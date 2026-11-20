(function () {
    var dlg = document.getElementById('dlg');

    // ----- Fenêtre « Nouvelle conversation » -----
    if (dlg) {
        var ouvrir = function () { dlg.showModal(); };

        ['newBtn', 'newBtn2', 'emptyNew'].forEach(function (id) {
            var b = document.getElementById(id);
            if (b) b.onclick = ouvrir;
        });

        document.getElementById('cancel').onclick = function () { dlg.close(); };
    }

    // ----- Suggestions : elles remplissent le champ de saisie -----
    document.querySelectorAll('.chip').forEach(function (b) {
        b.onclick = function () {
            var inp = document.getElementById('inp');
            if (!inp) return;
            inp.value = b.textContent;
            inp.focus();
        };
    });

    // ----- Suppression en deux clics : « × » puis « Supprimer ? » -----
    document.querySelectorAll('.cdel').forEach(function (b) {
        b.addEventListener('click', function (e) {
            if (b.classList.contains('arm')) return;   // 2e clic : le formulaire part
            e.preventDefault();                        // 1er clic : on demande confirmation
            document.querySelectorAll('.cdel.arm').forEach(function (o) {
                o.classList.remove('arm');
                o.textContent = '×';
            });
            b.classList.add('arm');
            b.textContent = 'Supprimer ?';
        });
    });

    // ----- Rendu Markdown des réponses de l'IA -----
    if (window.marked && window.DOMPurify) {

        // Les liens s'ouvrent dans un nouvel onglet
        DOMPurify.addHook('afterSanitizeAttributes', function (n) {
            if (n.tagName === 'A') {
                n.setAttribute('target', '_blank');
                n.setAttribute('rel', 'noopener noreferrer');
            }
        });

        document.querySelectorAll('.md').forEach(function (el) {
            const brut = el.textContent;                      // texte Markdown brut
            const html = marked.parse(brut);                  // Markdown → HTML
            el.innerHTML = DOMPurify.sanitize(html);          // HTML nettoyé (anti-XSS)
            el.classList.add('rendu');
        });

    } else {
        console.warn('marked ou DOMPurify non chargé : Markdown non converti');
    }

    // ----- Se placer sur le dernier message -----
    if (document.querySelector('.m')) {
        window.scrollTo(0, document.body.scrollHeight);
    }
})();