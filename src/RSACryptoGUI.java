import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSACryptoGUI extends Application {

    private CryptoImplementRSA crypto = new CryptoImplementRSA();
    private KeyPair currentKeyPair = null;
    private PublicKey encryptionPublicKey = null;
    private PrivateKey decryptionPrivateKey = null;

    private TextArea publicKeyArea;
    private TextArea privateKeyArea;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextArea decryptedOutputTextArea; // Nouveau: pour le déchiffrement
    private Label statusLabel;
    private ComboBox<Integer> keySizeCombo;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔐 Chiffrement RSA - Application Sécurisée");

        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // En-tête
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Contenu principal avec onglets
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab keyTab = createKeyManagementTab();
        Tab encryptTab = createEncryptionTab();
        Tab decryptTab = createDecryptionTab();
        Tab signeTab   = createSignatureTab();

        keyTab.setClosable(false);
        encryptTab.setClosable(false);
        decryptTab.setClosable(false);
        signeTab.setClosable(false);

        tabPane.getTabs().addAll(keyTab, encryptTab, decryptTab, signeTab);

        mainLayout.setCenter(tabPane);

        // Barre de statut avec bouton retour
        HBox bottomBar = new HBox(20);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setStyle("-fx-background-color: rgba(78, 204, 163, 0.05); -fx-border-color: #4ecca3; -fx-border-width: 2 0 0 0;");

        statusLabel = new Label("Prêt");
        statusLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 13px;");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        Button backButton = createStyledButton("⬅ Retour au Menu Principal", "#f39c12");
        backButton.setOnAction(e -> {
            new Main().start(new Stage());
            primaryStage.close();
        });

        bottomBar.getChildren().addAll(statusLabel, backButton);
        mainLayout.setBottom(bottomBar);

        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-border-color: #4ecca3; -fx-border-width: 0 0 2 0;");

        Label titleLabel = new Label("🔐 Système de Chiffrement RSA");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        Label subtitleLabel = new Label("Chiffrement asymétrique sécurisé - 2048/3072 bits");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#93a8ac"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private Tab createKeyManagementTab() {
        Tab tab = new Tab("🔑 Gestion des Clés");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // Section génération de clés
        VBox genSection = createStyledSection("Génération de Paire de Clés RSA");

        HBox keySizeBox = new HBox(10);
        keySizeBox.setAlignment(Pos.CENTER_LEFT);
        Label keySizeLabel = new Label("Taille de la clé :");
        keySizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        keySizeCombo = new ComboBox<>();
        keySizeCombo.getItems().addAll(2048, 3072);
        keySizeCombo.setValue(2048);
        keySizeCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        Button generateButton = createStyledButton("🎲 Générer Nouvelle Paire de Clés", "#4ecca3");
        generateButton.setOnAction(e -> generateKeys());

        keySizeBox.getChildren().addAll(keySizeLabel, keySizeCombo, generateButton);
        genSection.getChildren().add(keySizeBox);

        // Section affichage des clés
        VBox keyDisplaySection = createStyledSection("Clés Générées");

        Label publicKeyLabel = new Label("🔓 Clé Publique (pour chiffrer) :");
        publicKeyLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold;");
        publicKeyArea = new TextArea();
        publicKeyArea.setWrapText(true);
        publicKeyArea.setEditable(false);
        publicKeyArea.setPrefRowCount(4);
        publicKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New';");

        Label privateKeyLabel = new Label("🔒 Clé Privée (pour déchiffrer) - À garder secrète :");
        privateKeyLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        privateKeyArea = new TextArea();
        privateKeyArea.setWrapText(true);
        privateKeyArea.setEditable(false);
        privateKeyArea.setPrefRowCount(4);
        privateKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #ff6b6b; -fx-font-family: 'Courier New';");

        keyDisplaySection.getChildren().addAll(publicKeyLabel, publicKeyArea, privateKeyLabel, privateKeyArea);

        // Section sauvegarde/chargement - MODIFIÉE
        VBox ioSection = createStyledSection("Sauvegarde et Chargement des Clés");

        // Ligne 1: Sauvegarder les deux clés ensemble
        HBox saveAllBox = new HBox(10);
        saveAllBox.setAlignment(Pos.CENTER);
        Button saveAllButton = createStyledButton("💾 Sauvegarder les Deux Clés", "#4ecca3");
        saveAllButton.setOnAction(e -> saveKeys());
        saveAllBox.getChildren().add(saveAllButton);

        // Ligne 2: Sauvegarder séparément
        HBox saveSeparateBox = new HBox(10);
        saveSeparateBox.setAlignment(Pos.CENTER);
        Button savePublicButton = createStyledButton("💾 Sauvegarder Clé Publique", "#3498db");
        savePublicButton.setOnAction(e -> savePublicKeyOnly());
        Button savePrivateButton = createStyledButton("💾 Sauvegarder Clé Privée", "#e74c3c");
        savePrivateButton.setOnAction(e -> savePrivateKeyOnly());
        saveSeparateBox.getChildren().addAll(savePublicButton, savePrivateButton);

        // Ligne 3: Charger les clés
        HBox loadBox = new HBox(10);
        loadBox.setAlignment(Pos.CENTER);
        Button loadBothButton = createStyledButton("📂 Charger les Deux Clés", "#f39c12");
        loadBothButton.setOnAction(e -> loadKeys());
        loadBox.getChildren().add(loadBothButton);

        ioSection.getChildren().addAll(saveAllBox, saveSeparateBox, loadBox);

        content.getChildren().addAll(genSection, keyDisplaySection, ioSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createEncryptionTab() {
        Tab tab = new Tab("🔒 Chiffrement");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // NOUVELLE SECTION: Charger la clé pour le chiffrement
        VBox keySection = createStyledSection("Clé de Chiffrement");
        Label keyInfoLabel = new Label("Clé publique chargée : Aucune");
        keyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox keyButtonBox = new HBox(10);
        keyButtonBox.setAlignment(Pos.CENTER);
        Button loadPublicKeyButton = createStyledButton("📂 Charger Clé Publique", "#3498db");
        loadPublicKeyButton.setOnAction(e -> {
            loadPublicKeyForEncryption();
            if (encryptionPublicKey != null) {
                keyInfoLabel.setText("Clé publique chargée : ✅ Prête pour le chiffrement");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGeneratedKeyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGeneratedKeyButton.setOnAction(e -> {
            if (currentKeyPair != null) {
                encryptionPublicKey = currentKeyPair.getPublic();
                keyInfoLabel.setText("Clé publique chargée : ✅ Utilisation de la clé générée");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé publique de l'onglet 'Gestion' utilisée pour le chiffrement", "#4ecca3");
            } else {
                showError("Aucune clé n'a été générée dans l'onglet 'Gestion des Clés'");
            }
        });

        keyButtonBox.getChildren().addAll(loadPublicKeyButton, useGeneratedKeyButton);
        keySection.getChildren().addAll(keyInfoLabel, keyButtonBox);

        VBox inputSection = createStyledSection("Texte à Chiffrer");

        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Entrez le texte que vous souhaitez chiffrer...");
        inputTextArea.setPrefRowCount(6);
        inputTextArea.setWrapText(true);
        inputTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        // NOUVEAUX BOUTONS pour charger le texte
        HBox inputButtonBox = new HBox(10);
        inputButtonBox.setAlignment(Pos.CENTER);
        Button loadTextButton = createStyledButton("📂 Charger Texte depuis Fichier", "#9b59b6");
        loadTextButton.setOnAction(e -> loadPlainTextFromFile(inputTextArea));
        Button encryptButton = createStyledButton("🔒 Chiffrer avec la Clé Publique", "#4ecca3");
        encryptButton.setOnAction(e -> encryptText());
        inputButtonBox.getChildren().addAll(loadTextButton, encryptButton);

        inputSection.getChildren().addAll(inputTextArea, inputButtonBox);

        VBox outputSection = createStyledSection("Texte Chiffré (Base64)");

        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setPrefRowCount(6);
        outputTextArea.setWrapText(true);
        outputTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New';");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button copyButton = createStyledButton("📋 Copier", "#3498db");
        copyButton.setOnAction(e -> copyToClipboard(outputTextArea.getText()));
        Button saveToFileButton = createStyledButton("💾 Sauvegarder dans Fichier", "#f39c12");
        saveToFileButton.setOnAction(e -> saveTextToFile(outputTextArea.getText(), "Texte Chiffré"));
        Button clearButton = createStyledButton("🗑️ Effacer", "#e74c3c");
        clearButton.setOnAction(e -> {
            inputTextArea.clear();
            outputTextArea.clear();
        });
        buttonBox.getChildren().addAll(copyButton, saveToFileButton, clearButton);

        outputSection.getChildren().addAll(outputTextArea, buttonBox);

        content.getChildren().addAll(keySection, inputSection, outputSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createDecryptionTab() {
        Tab tab = new Tab("🔓 Déchiffrement");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // NOUVELLE SECTION: Charger la clé pour le déchiffrement
        VBox keySection = createStyledSection("Clé de Déchiffrement");
        Label keyInfoLabel = new Label("Clé privée chargée : Aucune");
        keyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox keyButtonBox = new HBox(10);
        keyButtonBox.setAlignment(Pos.CENTER);
        Button loadPrivateKeyButton = createStyledButton("📂 Charger Clé Privée", "#e74c3c");
        loadPrivateKeyButton.setOnAction(e -> {
            loadPrivateKeyForDecryption();
            if (decryptionPrivateKey != null) {
                keyInfoLabel.setText("Clé privée chargée : ✅ Prête pour le déchiffrement");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGeneratedKeyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGeneratedKeyButton.setOnAction(e -> {
            if (currentKeyPair != null) {
                decryptionPrivateKey = currentKeyPair.getPrivate();
                keyInfoLabel.setText("Clé privée chargée : ✅ Utilisation de la clé générée");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé privée de l'onglet 'Gestion' utilisée pour le déchiffrement", "#4ecca3");
            } else {
                showError("Aucune clé n'a été générée dans l'onglet 'Gestion des Clés'");
            }
        });

        keyButtonBox.getChildren().addAll(loadPrivateKeyButton, useGeneratedKeyButton);
        keySection.getChildren().addAll(keyInfoLabel, keyButtonBox);

        VBox inputSection = createStyledSection("Texte Chiffré à Déchiffrer");

        TextArea encryptedTextArea = new TextArea();
        encryptedTextArea.setPromptText("Collez le texte chiffré en Base64...");
        encryptedTextArea.setPrefRowCount(6);
        encryptedTextArea.setWrapText(true);
        encryptedTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        // NOUVEAUX BOUTONS pour charger le texte chiffré
        HBox inputButtonBox = new HBox(10);
        inputButtonBox.setAlignment(Pos.CENTER);
        Button loadCipherButton = createStyledButton("📂 Charger Texte Chiffré depuis Fichier", "#9b59b6");
        loadCipherButton.setOnAction(e -> loadCipherTextFromFile(encryptedTextArea));
        Button decryptButton = createStyledButton("🔓 Déchiffrer avec la Clé Privée", "#f39c12");
        decryptButton.setOnAction(e -> decryptText(encryptedTextArea));
        inputButtonBox.getChildren().addAll(loadCipherButton, decryptButton);

        inputSection.getChildren().addAll(encryptedTextArea, inputButtonBox);

        VBox outputSection = createStyledSection("Texte Déchiffré");

        decryptedOutputTextArea = new TextArea();
        decryptedOutputTextArea.setEditable(false);
        decryptedOutputTextArea.setPrefRowCount(6);
        decryptedOutputTextArea.setWrapText(true);
        decryptedOutputTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button copyButton = createStyledButton("📋 Copier", "#3498db");
        copyButton.setOnAction(e -> copyToClipboard(decryptedOutputTextArea.getText()));
        Button saveToFileButton = createStyledButton("💾 Sauvegarder dans Fichier", "#f39c12");
        saveToFileButton.setOnAction(e -> saveTextToFile(decryptedOutputTextArea.getText(), "Texte Déchiffré"));
        Button clearButton = createStyledButton("🗑️ Effacer", "#e74c3c");
        clearButton.setOnAction(e -> {
            encryptedTextArea.clear();
            decryptedOutputTextArea.clear();
        });
        buttonBox.getChildren().addAll(copyButton, saveToFileButton, clearButton);

        outputSection.getChildren().addAll(decryptedOutputTextArea, buttonBox);

        content.getChildren().addAll(keySection, inputSection, outputSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createSignatureTab() {
        Tab tab = new Tab("🔏 Signature Numérique");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // ========== SECTION SIGNATURE ==========
        VBox signSection = createStyledSection("📝 Signer un Document");

        // Section clé pour signature
        VBox signKeySection = new VBox(10);
        Label signKeyInfoLabel = new Label("Clé privée chargée : Aucune");
        signKeyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox signKeyButtonBox = new HBox(10);
        signKeyButtonBox.setAlignment(Pos.CENTER);
        Button loadSignKeyButton = createStyledButton("📂 Charger Clé Privée", "#e74c3c");
        loadSignKeyButton.setOnAction(e -> {
            loadPrivateKeyForSigning();
            if (decryptionPrivateKey != null) {
                signKeyInfoLabel.setText("Clé privée chargée : ✅ Prête pour signer");
                signKeyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGenKeyForSignButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGenKeyForSignButton.setOnAction(e -> {
            if (currentKeyPair != null) {
                decryptionPrivateKey = currentKeyPair.getPrivate();
                signKeyInfoLabel.setText("Clé privée chargée : ✅ Utilisation de la clé générée");
                signKeyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé privée prête pour signature", "#4ecca3");
            } else {
                showError("Aucune clé n'a été générée dans l'onglet 'Gestion des Clés'");
            }
        });

        signKeyButtonBox.getChildren().addAll(loadSignKeyButton, useGenKeyForSignButton);
        signKeySection.getChildren().addAll(signKeyInfoLabel, signKeyButtonBox);

        // Texte à signer
        Label signTextLabel = new Label("Document à signer :");
        signTextLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextArea signTextArea = new TextArea();
        signTextArea.setPromptText("Entrez le texte que vous souhaitez signer...");
        signTextArea.setPrefRowCount(4);
        signTextArea.setWrapText(true);
        signTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        HBox signButtonBox = new HBox(10);
        signButtonBox.setAlignment(Pos.CENTER);
        Button loadDocButton = createStyledButton("📂 Charger Document", "#9b59b6");
        loadDocButton.setOnAction(e -> loadPlainTextFromFile(signTextArea));
        Button signButton = createStyledButton("🔏 Signer le Document", "#e74c3c");
        signButton.setOnAction(e -> signDocument(signTextArea));
        signButtonBox.getChildren().addAll(loadDocButton, signButton);

        // Signature générée
        Label signatureLabel = new Label("Signature (Base64) :");
        signatureLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold;");
        TextArea signatureArea = new TextArea();
        signatureArea.setEditable(false);
        signatureArea.setPrefRowCount(3);
        signatureArea.setWrapText(true);
        signatureArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New';");

        HBox signOutputButtonBox = new HBox(10);
        signOutputButtonBox.setAlignment(Pos.CENTER);
        Button copySignButton = createStyledButton("📋 Copier", "#3498db");
        copySignButton.setOnAction(e -> copyToClipboard(signatureArea.getText()));
        Button saveSignButton = createStyledButton("💾 Sauvegarder", "#f39c12");
        saveSignButton.setOnAction(e -> saveTextToFile(signatureArea.getText(), "Signature"));
        signOutputButtonBox.getChildren().addAll(copySignButton, saveSignButton);

        signSection.getChildren().addAll(signKeySection, signTextLabel, signTextArea, signButtonBox, signatureLabel, signatureArea, signOutputButtonBox);

        // ========== SECTION VÉRIFICATION ==========
        VBox verifySection = createStyledSection("✅ Vérifier une Signature");

        // Section clé pour vérification
        VBox verifyKeySection = new VBox(10);
        Label verifyKeyInfoLabel = new Label("Clé publique chargée : Aucune");
        verifyKeyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox verifyKeyButtonBox = new HBox(10);
        verifyKeyButtonBox.setAlignment(Pos.CENTER);
        Button loadVerifyKeyButton = createStyledButton("📂 Charger Clé Publique", "#3498db");
        loadVerifyKeyButton.setOnAction(e -> {
            loadPublicKeyForVerifying();
            if (encryptionPublicKey != null) {
                verifyKeyInfoLabel.setText("Clé publique chargée : ✅ Prête pour vérifier");
                verifyKeyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGenKeyForVerifyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGenKeyForVerifyButton.setOnAction(e -> {
            if (currentKeyPair != null) {
                encryptionPublicKey = currentKeyPair.getPublic();
                verifyKeyInfoLabel.setText("Clé publique chargée : ✅ Utilisation de la clé générée");
                verifyKeyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé publique prête pour vérification", "#4ecca3");
            } else {
                showError("Aucune clé n'a été générée dans l'onglet 'Gestion des Clés'");
            }
        });

        verifyKeyButtonBox.getChildren().addAll(loadVerifyKeyButton, useGenKeyForVerifyButton);
        verifyKeySection.getChildren().addAll(verifyKeyInfoLabel, verifyKeyButtonBox);

        // Document original
        Label verifyDocLabel = new Label("Document original :");
        verifyDocLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextArea verifyDocArea = new TextArea();
        verifyDocArea.setPromptText("Entrez le document original...");
        verifyDocArea.setPrefRowCount(3);
        verifyDocArea.setWrapText(true);
        verifyDocArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        Button loadVerifyDocButton = createStyledButton("📂 Charger Document", "#9b59b6");
        loadVerifyDocButton.setOnAction(e -> loadPlainTextFromFile(verifyDocArea));

        // Signature à vérifier
        Label verifySignLabel = new Label("Signature à vérifier (Base64) :");
        verifySignLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextArea verifySignArea = new TextArea();
        verifySignArea.setPromptText("Collez la signature en Base64...");
        verifySignArea.setPrefRowCount(2);
        verifySignArea.setWrapText(true);
        verifySignArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        HBox verifyInputButtonBox = new HBox(10);
        verifyInputButtonBox.setAlignment(Pos.CENTER);
        Button loadSignFileButton = createStyledButton("📂 Charger Signature", "#9b59b6");
        loadSignFileButton.setOnAction(e -> loadCipherTextFromFile(verifySignArea));
        verifyInputButtonBox.getChildren().addAll(loadVerifyDocButton, loadSignFileButton);

        // Bouton de vérification
        Button verifyButton = createStyledButton("✅ Vérifier la Signature", "#4ecca3");
        verifyButton.setOnAction(e -> verifySignature(verifyDocArea, verifySignArea));

        // Résultat de la vérification
        Label resultLabel = new Label("Résultat de la vérification :");
        resultLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label resultText = new Label("");
        resultText.setFont(Font.font("System", FontWeight.BOLD, 16));
        resultText.setWrapText(true);
        resultText.setPadding(new Insets(10));
        resultText.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-background-radius: 5;");

        verifySection.getChildren().addAll(
                verifyKeySection,
                verifyDocLabel, verifyDocArea,
                verifySignLabel, verifySignArea,
                verifyInputButtonBox,
                verifyButton,
                resultLabel, resultText
        );

        content.getChildren().addAll(signSection, verifySection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        // Stocker les références pour les méthodes
        tab.setUserData(new Object[]{signatureArea, resultText});

        return tab;
    }

    private VBox createStyledSection(String title) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: rgba(78, 204, 163, 0.05); -fx-border-color: #4ecca3; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        section.getChildren().add(titleLabel);
        return section;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + color + ", -20%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        ));

        return button;
    }

    private void generateKeys() {
        try {
            int keySize = keySizeCombo.getValue();
            currentKeyPair = crypto.genKey("RSA", keySize);

            publicKeyArea.setText(crypto.publicKeyToString(currentKeyPair.getPublic()));
            privateKeyArea.setText(crypto.privateKeyToString(currentKeyPair.getPrivate()));

            showStatus("✅ Paire de clés RSA " + keySize + " bits générée avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors de la génération des clés: " + e.getMessage());
        }
    }

    private void saveKeys() {
        if (currentKeyPair == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer une paire de clés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique", "*.pub"));
        File publicFile = fileChooser.showSaveDialog(null);

        if (publicFile != null) {
            fileChooser.setTitle("Sauvegarder la Clé Privée");
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée", "*.key"));
            File privateFile = fileChooser.showSaveDialog(null);

            if (privateFile != null) {
                try {
                    crypto.saveKeyPair(currentKeyPair, publicFile.getAbsolutePath(), privateFile.getAbsolutePath());
                    showStatus("✅ Clés sauvegardées avec succès!", "#4ecca3");
                } catch (Exception e) {
                    showError("Erreur lors de la sauvegarde: " + e.getMessage());
                }
            }
        }
    }

    // NOUVELLE MÉTHODE: Sauvegarder uniquement la clé publique
    private void savePublicKeyOnly() {
        if (currentKeyPair == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer une paire de clés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique", "*.pub"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                crypto.savePublicKey(currentKeyPair.getPublic(), file.getAbsolutePath());
                showStatus("✅ Clé publique sauvegardée avec succès!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    // NOUVELLE MÉTHODE: Sauvegarder uniquement la clé privée
    private void savePrivateKeyOnly() {
        if (currentKeyPair == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer une paire de clés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Privée");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée", "*.key"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                crypto.savePrivateKey(currentKeyPair.getPrivate(), file.getAbsolutePath());
                showStatus("✅ Clé privée sauvegardée avec succès!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    private void loadKeys() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique", "*.pub"));
        File publicFile = fileChooser.showOpenDialog(null);

        if (publicFile != null) {
            fileChooser.setTitle("Charger la Clé Privée");
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée", "*.key"));
            File privateFile = fileChooser.showOpenDialog(null);

            if (privateFile != null) {
                try {
                    currentKeyPair = crypto.loadKeyPair(publicFile.getAbsolutePath(), privateFile.getAbsolutePath());
                    publicKeyArea.setText(crypto.publicKeyToString(currentKeyPair.getPublic()));
                    privateKeyArea.setText(crypto.privateKeyToString(currentKeyPair.getPrivate()));
                    showStatus("✅ Clés chargées avec succès!", "#4ecca3");
                } catch (Exception e) {
                    showError("Erreur lors du chargement: " + e.getMessage());
                }
            }
        }
    }

    // NOUVELLE MÉTHODE: Charger uniquement la clé publique pour le chiffrement
    private void loadPublicKeyForEncryption() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Publique pour Chiffrement");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique", "*.pub"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                encryptionPublicKey = crypto.loadPublicKey(file.getAbsolutePath());
                showStatus("✅ Clé publique chargée pour le chiffrement!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement de la clé publique: " + e.getMessage());
            }
        }
    }

    // NOUVELLE MÉTHODE: Charger uniquement la clé privée pour le déchiffrement
    private void loadPrivateKeyForDecryption() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Privée pour Déchiffrement");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée", "*.key"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                decryptionPrivateKey = crypto.loadPrivateKey(file.getAbsolutePath());
                showStatus("✅ Clé privée chargée pour le déchiffrement!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement de la clé privée: " + e.getMessage());
            }
        }
    }

    // NOUVELLE MÉTHODE: Charger un texte clair depuis un fichier
    private void loadPlainTextFromFile(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un Texte Clair");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                textArea.setText(content);
                showStatus("✅ Texte chargé depuis: " + file.getName(), "#4ecca3");
            } catch (IOException e) {
                showError("Erreur lors du chargement du fichier: " + e.getMessage());
            }
        }
    }

    // NOUVELLE MÉTHODE: Charger un texte chiffré depuis un fichier
    private void loadCipherTextFromFile(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un Texte Chiffré");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Chiffrés", "*.enc", "*.encrypted"),
                new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                textArea.setText(content);
                showStatus("✅ Texte chiffré chargé depuis: " + file.getName(), "#4ecca3");
            } catch (IOException e) {
                showError("Erreur lors du chargement du fichier: " + e.getMessage());
            }
        }
    }

    // NOUVELLE MÉTHODE: Sauvegarder un texte dans un fichier
    private void saveTextToFile(String text, String title) {
        if (text == null || text.isEmpty()) {
            showError("Aucun texte à sauvegarder!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder " + title);

        if (title.contains("Chiffré")) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers Chiffrés", "*.enc"),
                    new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt")
            );
        } else {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"));
        }

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(text);
                showStatus("✅ Texte sauvegardé dans: " + file.getName(), "#4ecca3");
            } catch (IOException e) {
                showError("Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    private void encryptText() {
        // Utiliser la clé de chiffrement ou la clé courante
        PublicKey keyToUse = encryptionPublicKey != null ? encryptionPublicKey :
                (currentKeyPair != null ? currentKeyPair.getPublic() : null);

        if (keyToUse == null) {
            showError("Veuillez d'abord générer ou charger une clé publique!");
            return;
        }

        String plainText = inputTextArea.getText();
        if (plainText.isEmpty()) {
            showError("Veuillez entrer un texte à chiffrer!");
            return;
        }

        try {
            // Créer une paire temporaire avec la clé publique
            KeyPair tempPair = new KeyPair(keyToUse, null);
            String encrypted = crypto.encrypt(tempPair, plainText, "RSA/ECB/PKCS1Padding");
            outputTextArea.setText(encrypted);
            showStatus("✅ Texte chiffré avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du chiffrement: " + e.getMessage());
        }
    }

    private void decryptText(TextArea encryptedTextArea) {
        // Utiliser la clé de déchiffrement ou la clé courante
        PrivateKey keyToUse = decryptionPrivateKey != null ? decryptionPrivateKey :
                (currentKeyPair != null ? currentKeyPair.getPrivate() : null);

        if (keyToUse == null) {
            showError("Veuillez d'abord générer ou charger une clé privée!");
            return;
        }

        String cipherText = encryptedTextArea.getText();
        if (cipherText.isEmpty()) {
            showError("Veuillez entrer un texte chiffré à déchiffrer!");
            return;
        }

        try {
            // Créer une paire temporaire avec la clé privée
            KeyPair tempPair = new KeyPair(null, keyToUse);
            String decrypted = crypto.decrypt(tempPair, cipherText, "RSA/ECB/PKCS1Padding");
            decryptedOutputTextArea.setText(decrypted);
            showStatus("✅ Texte déchiffré avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du déchiffrement: " + e.getMessage());
        }
    }

    private void loadPublicKeyForVerifying() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Publique pour Vérification");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique", "*.pub"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                encryptionPublicKey = crypto.loadPublicKey(file.getAbsolutePath());
                showStatus("✅ Clé publique chargée pour la vérification!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement de la clé publique: " + e.getMessage());
            }
        }
    }

    private void loadPrivateKeyForSigning() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Privée pour Signature");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée", "*.key"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                decryptionPrivateKey = crypto.loadPrivateKey(file.getAbsolutePath());
                showStatus("✅ Clé privée chargée pour la signature!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement de la clé privée: " + e.getMessage());
            }
        }
    }

    private void signDocument(TextArea documentArea) {
        PrivateKey keyToUse = decryptionPrivateKey != null ? decryptionPrivateKey :
                (currentKeyPair != null ? currentKeyPair.getPrivate() : null);

        if (keyToUse == null) {
            showError("Veuillez d'abord générer ou charger une clé privée!");
            return;
        }

        String document = documentArea.getText();
        if (document.isEmpty()) {
            showError("Veuillez entrer un document à signer!");
            return;
        }

        try {
            String signature = crypto.sign(keyToUse, document);

            // Récupérer la zone de signature depuis l'onglet
            TabPane tabPane = (TabPane) ((BorderPane) documentArea.getScene().getRoot()).getCenter();
            Tab signatureTab = tabPane.getTabs().get(3);
            Object[] userData = (Object[]) signatureTab.getUserData();
            TextArea signatureArea = (TextArea) userData[0];

            signatureArea.setText(signature);
            showStatus("✅ Document signé avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors de la signature: " + e.getMessage());
        }
    }

    private void verifySignature(TextArea documentArea, TextArea signatureArea) {
        PublicKey keyToUse = encryptionPublicKey != null ? encryptionPublicKey :
                (currentKeyPair != null ? currentKeyPair.getPublic() : null);

        if (keyToUse == null) {
            showError("Veuillez d'abord générer ou charger une clé publique!");
            return;
        }

        String document = documentArea.getText();
        String signature = signatureArea.getText();

        if (document.isEmpty()) {
            showError("Veuillez entrer le document original!");
            return;
        }

        if (signature.isEmpty()) {
            showError("Veuillez entrer la signature à vérifier!");
            return;
        }

        try {
            boolean isValid = crypto.verify(keyToUse, document, signature);

            // Récupérer le label de résultat depuis l'onglet
            TabPane tabPane = (TabPane) ((BorderPane) documentArea.getScene().getRoot()).getCenter();
            Tab signatureTab = tabPane.getTabs().get(3);
            Object[] userData = (Object[]) signatureTab.getUserData();
            Label resultLabel = (Label) userData[1];

            if (isValid) {
                resultLabel.setText("✅ SIGNATURE VALIDE - Le document n'a pas été modifié");
                resultLabel.setTextFill(Color.web("#4ecca3"));
                showStatus("✅ Signature valide!", "#4ecca3");
            } else {
                resultLabel.setText("❌ SIGNATURE INVALIDE - Le document a été modifié ou la signature est incorrecte");
                resultLabel.setTextFill(Color.web("#e74c3c"));
                showStatus("❌ Signature invalide!", "#e74c3c");
            }
        } catch (Exception e) {
            showError("Erreur lors de la vérification: " + e.getMessage());
        }
    }

    private void copyToClipboard(String text) {
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        showStatus("📋 Copié dans le presse-papiers!", "#3498db");
    }

    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px; -fx-padding: 10px;");
    }

    private void showError(String message) {
        showStatus("❌ " + message, "#e74c3c");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}