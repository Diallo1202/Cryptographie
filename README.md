# 🔐 Système de Chiffrement Multialgoritmes

Une application JavaFX complète offrant plusieurs méthodes de chiffrement, d'échange de clés et de vérification d'intégrité.

## 📋 Table des matières

- [Fonctionnalités](#fonctionnalités)
- [Algorithmes Disponibles](#algorithmes-disponibles)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Architecture](#architecture)
- [Démonstrations](#démonstrations)
- [Licence](#licence)

## ✨ Fonctionnalités

✅ **Chiffrement Symétrique (AES)**
- Clé secrète unique partagée
- Modes : CBC, ECB
- Tailles : 128, 192, 256 bits
- Très rapide pour gros fichiers

✅ **Chiffrement Asymétrique (RSA)**
- Paire de clés (publique + privée)
- Signatures numériques
- Vérification d'authenticité
- Tailles : 2048, 3072 bits

✅ **Chiffrement ElGamal**
- Basé sur logarithme discret
- Chiffrement probabiliste
- Alternative sécurisée à RSA
- Tailles : 512, 1024, 2048 bits

✅ **Échange de Clés (Diffie-Hellman)**
- Accord de clé secrète partagée
- Canal de communication public
- Dérivation de clés sécurisée
- Démonstration interactive (Alice & Bob)

✅ **Hashage Cryptographique**
- MD5 (déprécié ⚠️)
- SHA-1 (déprécié ⚠️)
- SHA-256 (recommandé ✅)
- SHA-512 (très sécurisé ✅)
- Vérification intégrité fichiers

## 🔑 Algorithmes Disponibles

### AES (Advanced Encryption Standard)
```
Symbole: 🔒
Type: Symétrique
Clé: Une seule clé secrète
Vitesse: Très rapide ⚡
Usage: Chiffrement données massives
```

### RSA (Rivest-Shamir-Adleman)
```
Symbole: 🔑
Type: Asymétrique
Clés: Publique + Privée
Sécurité: Maximale
Usage: Échange clés, signatures
```

### ElGamal
```
Symbole: 🔐
Type: Asymétrique
Basé sur: Logarithme discret
Particularité: Probabiliste
Usage: Communication sécurisée
```

### Diffie-Hellman
```
Symbole: 📡
Type: Échange de clés
Découvreurs: Whitfield Diffie & Martin Hellman (1976)
Cas d'usage: Établir clé secrète via canal public
```

### Fonctions de Hash
```
Symbole: #
Types: MD5, SHA-1, SHA-256, SHA-512
Usage: Intégrité, signatures, stockage mots de passe
```

## 🚀 Installation

### Prérequis
- **Java 11+**
- **JavaFX 11+**
- Maven ou Gradle (optionnel)

### Compilation
```bash
javac -cp ".:lib/javafx-sdk/lib/*" *.java
```

### Exécution
```bash
java -cp ".:lib/javafx-sdk/lib/*" --add-modules javafx.controls Main
```

Ou avec Maven :
```bash
mvn clean javafx:run
```

## 💻 Utilisation

### Lancer l'application
```bash
java Main
```

### Menu Principal
Au démarrage, vous accédez au menu avec 5 options principales.

### Workflow Typique - AES

**1. Générer une clé secrète**
```
Onglet "Gestion de la Clé"
→ Sélectionner taille (128/192/256 bits)
→ Cliquer "Générer Nouvelle Clé Secrète"
→ Sauvegarder la clé en .key
```

**2. Chiffrer un texte**
```
Onglet "Chiffrement"
→ Charger/entrer le texte à chiffrer
→ Sélectionner mode (CBC/ECB)
→ Cliquer "Chiffrer"
→ Copier ou sauvegarder le résultat
```

**3. Déchiffrer**
```
Onglet "Déchiffrement"
→ Coller le texte chiffré (Base64)
→ Sélectionner même mode qu'au chiffrement
→ Cliquer "Déchiffrer"
→ Récupérer le texte clair
```

### Workflow Typique - RSA

**1. Générer paire de clés**
```
Onglet "Gestion des Clés"
→ Sélectionner taille (2048/3072 bits)
→ Cliquer "Générer Nouvelle Paire de Clés"
→ Sauvegarder clé publique (.pub) et privée (.key)
```

**2. Chiffrer avec clé publique**
```
Onglet "Chiffrement"
→ Charger clé publique du destinataire
→ Entrer texte à chiffrer
→ Envoyer texte chiffré au destinataire
```

**3. Déchiffrer avec clé privée**
```
Onglet "Déchiffrement"
→ Charger votre clé privée
→ Coller texte chiffré reçu
→ Récupérer message original
```

**4. Signer un document**
```
Onglet "Signature Numérique"
→ Charger clé privée
→ Entrer document à signer
→ Cliquer "Signer le Document"
→ Envoyer document + signature
```

**5. Vérifier une signature**
```
Onglet "Signature Numérique" (Vérification)
→ Charger clé publique du signataire
→ Coller document et signature
→ Cliquer "Vérifier la Signature"
→ Affirmation d'authenticité
```

### Workflow Typique - Diffie-Hellman

**1. Générer paramètres publics**
```
Onglet "Paramètres Publics"
→ Sélectionner taille du modulus (512/1024/2048)
→ Cliquer "Générer Paramètres"
⏳ Attendre quelques secondes
→ Sauvegarder pour partage
```

**2. Alice génère ses clés**
```
Onglet "Échange de Clés"
→ Section Alice
→ Cliquer "Générer Clés Alice"
→ Partager clé publique d'Alice avec Bob
```

**3. Bob génère ses clés**
```
Section Bob
→ Cliquer "Générer Clés Bob"
→ Partager clé publique de Bob avec Alice
```

**4. Calculer secrets partagés**
```
Alice: Cliquer "Calculer Secret Alice"
Bob: Cliquer "Calculer Secret Bob"
```

**5. Vérifier la clé partagée**
```
Cliquer "Vérifier que les Secrets Correspondent"
✅ Message de succès si identiques
```

### Workflow Typique - Hashage

**1. Calculer le hash d'un texte**
```
Onglet "Calculer un Hash"
→ Sélectionner algorithme (SHA-256 recommandé)
→ Entrer/charger le texte
→ Cliquer "Calculer le Hash"
→ Copier ou sauvegarder
```

**2. Vérifier l'intégrité**
```
Onglet "Vérifier un Hash"
→ Entrer texte original
→ Coller le hash à vérifier
→ Cliquer "Vérifier l'Intégrité"
✅ ou ❌ Résultat
```

**3. Hash de fichiers**
```
Onglet "Hash de Fichiers"
→ Sélectionner fichier
→ Cliquer "Calculer Hash"
→ Vérifier avec hash fourni
```

## 🏗️ Architecture

### Structure du Projet

```
📦 Projet Chiffrement
├── 📄 Main.java                 # Menu principal
├── 🔒 Chiffrement AES
│   ├── AESCryptoGUI.java       # Interface utilisateur
│   ├── CryptoImplementAES.java # Implémentation
│   └── IcryptoAES.java         # Interface
├── 🔑 Chiffrement RSA
│   ├── RSACryptoGUI.java
│   ├── CryptoImplementRSA.java
│   └── IcryptoRSA.java
├── 🔐 Chiffrement ElGamal
│   ├── ElGamalCryptoGUI.java
│   ├── ElGamalImplement.java
│   └── IElGamal.java
├── 📡 Diffie-Hellman
│   ├── DiffieHellmanGUI.java
│   ├── DiffieHellmanImplement.java
│   └── IDiffieHellman.java
└── # Hashage
    ├── HashCryptoGUI.java
    ├── HashImplement.java
    └── IHash.java
```

### Pattern MVC

- **Vue** : `*GUI.java` - Interface JavaFX
- **Contrôleur** : Logique dans GUI
- **Modèle** : `*Implement.java` - Algorithmes
- **Interface** : `I*.java` - Contrats

## 📊 Démonstrations

### Cas d'Usage 1️⃣ : Communication Sécurisée

**Scénario** : Alice veut envoyer message secret à Bob

```
1️⃣ Alice génère paire RSA 2048 bits
   - Envoie clé publique à Bob (non sécurisé)
   - Garde clé privée secrète

2️⃣ Bob reçoit clé publique d'Alice
   - Écrit message : "Secret important"
   - Chiffre avec clé publique d'Alice
   - Envoie texte chiffré (interceptable)

3️⃣ Alice reçoit texte chiffré
   - Déchiffre avec SA clé privée
   - Lit message : "Secret important"
   - ✅ Seule Alice peut lire !
```

### Cas d'Usage 2️⃣ : Signature Numérique

**Scénario** : Alice signe un contrat

```
1️⃣ Alice rédige contrat
   - Calcule signature avec clé privée
   - Envoie contrat + signature

2️⃣ Bob reçoit contrat + signature
   - Vérifie signature avec clé publique d'Alice
   - ✅ Signature valide = Alice a signé
   - ✅ Contrat non modifié depuis signature

3️⃣ Sécurité garantie
   - Authentification ✅
   - Non-répudiation ✅
   - Intégrité ✅
```

### Cas d'Usage 3️⃣ : Établir Clé Secrète (Diffie-Hellman)

**Scénario** : Alice & Bob établissent clé secrète via internet (non sécurisé)

```
1️⃣ Alice & Bob conviennent : p = grand nombre premier, g = générateur
   ⚠️ Ces paramètres peuvent être vus par attaquant

2️⃣ Alice génère secret aléatoire a
   - Calcule A = g^a mod p
   - Envoie A à Bob (peut être vu par attaquant)

3️⃣ Bob génère secret aléatoire b
   - Calcule B = g^b mod p
   - Envoie B à Alice (peut être vu par attaquant)

4️⃣ Alice calcule secret partagé
   - s = B^a mod p

5️⃣ Bob calcule secret partagé
   - s = A^b mod p

🎉 RÉSULTAT : Alice et Bob ont même secret !
   s_Alice = s_Bob ✅
   Attaquant ne peut pas calculer s 🛡️
   (Logarithme discret est calculatoirement impossible)
```

### Cas d'Usage 4️⃣ : Intégrité de Fichiers

**Scénario** : Vérifier qu'un téléchargement n'a pas été altéré

```
1️⃣ Serveur: Hash du fichier
   - Calcule SHA-256 du fichier
   - Affiche: 3a4f2b9e...

2️⃣ Utilisateur télécharge fichier
   - Calcule SHA-256 du fichier reçu
   - Obtient: 3a4f2b9e...

3️⃣ Vérification
   ✅ Hashes identiques → Fichier intègre
   ❌ Hashes différents → Fichier corrompu/altéré
```

### Cas d'Usage 5️⃣ : Chiffrement Rapide (AES)

**Scénario** : Chiffrer base de données client

```
1️⃣ Entreprise génère clé AES 256 bits
   - Clé secrète stockée sécurisée

2️⃣ Chiffre tous dossiers clients avec AES
   - Très rapide même pour 1 TB ⚡

3️⃣ Déchiffre à la demande
   - Accès client = décrypt à la volée
   - Performance conservée
```

## 📈 Comparaison Algorithmes

| Algorithme | Type | Vitesse | Sécurité | Cas d'Usage |
|-----------|------|---------|----------|------------|
| **AES** | Symétrique | ⚡⚡⚡ | ✅✅✅ | Gros fichiers |
| **RSA** | Asymétrique | ⚠️ | ✅✅✅ | Échange clés |
| **ElGamal** | Asymétrique | ⚠️ | ✅✅✅ | Alternative RSA |
| **DH** | Échange clés | ⚠️ | ✅✅✅ | Accord clé |
| **SHA-256** | Hash | ⚡⚡⚡ | ✅✅✅ | Intégrité |
| **SHA-512** | Hash | ⚡⚡ | ✅✅✅✅ | Haute sécurité |

## 🛠️ Dépendances

```xml
<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>

<!-- Java Crypto (inclus dans JDK) -->
<dependency>
    <groupId>javax.crypto</groupId>
    <artifactId>crypto</artifactId>
    <version>1.0</version>
</dependency>
```

## ⚠️ Avertissements Sécurité

- ❌ **MD5** : Vulnérable aux collisions - NE PAS utiliser
- ❌ **SHA-1** : Faiblesses découvertes - ÉVITER
- ✅ **SHA-256** : Sûr pour la plupart des usages
- ✅ **SHA-512** : Recommandé pour haute sécurité
- 🔐 **Ne jamais partager clé privée**
- 🔐 **Utiliser HTTPS pour transmettre clés publiques**

## 📚 Ressources

- [NIST Cryptography Standards](https://csrc.nist.gov/)
- [OWASP Cryptographic Storage](https://owasp.org/)
- [RFC 3394 - AES Key Wrap](https://tools.ietf.org/html/rfc3394)
- [RFC 3526 - DH Parameters](https://tools.ietf.org/html/rfc3526)

## 👨‍💻 Auteur

Développé comme projet d'étude en cryptographie moderne.

## 📄 Licence

MIT License - Libre d'utilisation pour projets personnels et commerciaux.

---

**Dernière mise à jour** : 2025  
**Version** : 1.0  
⭐ Si vous trouvez ce projet utile, n'hésitez pas à le mettre en favori !
