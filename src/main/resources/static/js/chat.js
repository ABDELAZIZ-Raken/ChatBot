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

    // ----- Se placer sur le dernier message -----
    if (document.querySelector('.m')) {
        window.scrollTo(0, document.body.scrollHeight);
    }
})();