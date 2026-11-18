(function () {

    // ----- Suppression en deux clics : « Supprimer » puis « Confirmer » -----
    document.querySelectorAll('.arm-btn').forEach(function (b) {
        var texteInitial = b.textContent;
        b.addEventListener('click', function (e) {
            if (b.classList.contains('arm')) return;   // 2e clic : le formulaire part
            e.preventDefault();                        // 1er clic : on demande confirmation
            document.querySelectorAll('.arm-btn.arm').forEach(function (o) {
                o.classList.remove('arm');
                o.textContent = 'Supprimer';
            });
            b.classList.add('arm');
            b.textContent = b.getAttribute('data-arm') || 'Confirmer';
        });
    });

    var dlg = document.getElementById('aD');
    if (!dlg) return;   // page Détails ou onglet Utilisateurs : pas de fenêtre

    var form = document.getElementById('aFm');
    var champs = {
        nom: document.getElementById('fN'),
        fournisseur: document.getElementById('fP'),
        identifiant: document.getElementById('fI'),
        url: document.getElementById('fU'),
        description: document.getElementById('fD'),
        actif: document.getElementById('fA'),
        logo: document.getElementById('fL'),
        erreur: document.getElementById('fErr')
    };

    // Initiales du modèle (« Mistral Small 4 » → « MS »)
    function initiales(nom) {
        return nom.trim().split(/\s+/).map(function (w) { return w[0] || ''; })
            .join('').slice(0, 2).toUpperCase();
    }

    // m = null : création / sinon : modification du modèle m
    function ouvrir(m) {
        var base = form.getAttribute('data-base');

        document.getElementById('aH').textContent = m ? 'Modifier ' + m.nom : 'Créer un modèle d’IA';
        document.getElementById('fOk').textContent = m ? 'Enregistrer les modifications' : 'Créer le modèle';
        form.action = m ? base + '/' + m.id : base;

        champs.nom.value = m ? m.nom : '';
        champs.fournisseur.value = m ? m.fournisseur : champs.fournisseur.options[0].value;
        champs.identifiant.value = m ? m.identifiant : '';
        champs.url.value = m ? m.url : '';
        champs.description.value = m ? m.description : '';
        champs.actif.checked = m ? m.actif : true;
        champs.logo.textContent = m && m.nom ? initiales(m.nom) : '?';
        champs.erreur.hidden = true;

        dlg.showModal();
    }

    document.getElementById('aAdd').onclick = function () { ouvrir(null); };
    document.getElementById('fNo').onclick = function () { dlg.close(); };

    // Boutons « Modifier » : les données de la ligne sont dans les attributs data-*
    document.querySelectorAll('[data-modifier]').forEach(function (b) {
        b.onclick = function () {
            ouvrir({
                id: b.dataset.id,
                nom: b.dataset.nom,
                fournisseur: b.dataset.fournisseur,
                identifiant: b.dataset.identifiant,
                url: b.dataset.url,
                description: b.dataset.description,
                actif: b.dataset.actif === 'true'
            });
        };
    });

    // Le rond du logo suit le nom saisi
    champs.nom.addEventListener('input', function () {
        champs.logo.textContent = champs.nom.value.trim() ? initiales(champs.nom.value) : '?';
    });

    // Nom et identifiant obligatoires
    form.addEventListener('submit', function (e) {
        if (!champs.nom.value.trim() || !champs.identifiant.value.trim()) {
            e.preventDefault();
            champs.erreur.hidden = false;
        }
    });

    // ----- Recherche et filtre par fournisseur -----
    var recherche = document.getElementById('aQ');
    var filtre = document.getElementById('aF');
    var vide = document.getElementById('aE');
    var lignes = document.querySelectorAll('.ligne-modele');

    function filtrer() {
        var q = recherche.value.toLowerCase();
        var f = filtre.value;
        var n = 0;
        lignes.forEach(function (l) {
            var ok = (!f || l.dataset.fournisseur === f) &&
                (!q || l.dataset.recherche.toLowerCase().indexOf(q) >= 0);
            l.hidden = !ok;
            if (ok) n++;
        });
        vide.hidden = n > 0;
    }

    recherche.oninput = filtrer;
    filtre.onchange = filtrer;
})();