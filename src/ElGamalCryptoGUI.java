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
import java.math.BigInteger;
import java.nio.file.Files;

public class ElGamalCryptoGUI extends Application {

    private ElGamalImplement crypto = new ElGamalImplement();
    private IElGamal.ElGamalKeys currentKeys = null;
    private IElGamal.ElGamalKeys encryptionKeys = null;
    private IElGamal.ElGamalKeys decryptionKeys = null;

    private TextArea publicKeyArea;
    private TextArea privateKeyArea;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextArea decryptedOutputTextArea;
    private Label statusLabel;
    private ComboBox<Integer> keySizeCombo;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔐 Chiffrement ElGamal - Application Sécurisée");

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
        Tab infoTab = createInfoTab();

        keyTab.setClosable(false);
        encryptTab.setClosable(false);
        decryptTab.setClosable(false);
        infoTab.setClosable(false);

        tabPane.getTabs().addAll(keyTab, encryptTab, decryptTab, infoTab);

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

        Label titleLabel = new Label("🔐 Système de Chiffrement ElGamal");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        Label subtitleLabel = new Label("Chiffrement asymétrique basé sur le logarithme discret");
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
        VBox genSection = createStyledSection("Génération de Clés ElGamal");

        HBox keySizeBox = new HBox(10);
        keySizeBox.setAlignment(Pos.CENTER_LEFT);
        Label keySizeLabel = new Label("Taille de la clé :");
        keySizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        keySizeCombo = new ComboBox<>();
        keySizeCombo.getItems().addAll(512, 1024, 2048);
        keySizeCombo.setValue(1024);
        keySizeCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        Label warningLabel = new Label("⚠️ La génération peut prendre quelques secondes");
        warningLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 11px; -fx-font-style: italic;");

        Button generateButton = createStyledButton("🎲 Générer Nouvelles Clés", "#4ecca3");
        generateButton.setOnAction(e -> generateKeys());

        keySizeBox.getChildren().addAll(keySizeLabel, keySizeCombo, generateButton);
        genSection.getChildren().addAll(keySizeBox, warningLabel);

        // Section affichage des clés
        VBox keyDisplaySection = createStyledSection("Clés Générées");

        Label publicKeyLabel = new Label("🔓 Clé Publique (p, g, y) :");
        publicKeyLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold;");
        publicKeyArea = new TextArea();
        publicKeyArea.setWrapText(true);
        publicKeyArea.setEditable(false);
        publicKeyArea.setPrefRowCount(4);
        publicKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New'; -fx-font-size: 10px;");

        Label privateKeyLabel = new Label("🔒 Clé Privée (p, x) - À garder secrète :");
        privateKeyLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        privateKeyArea = new TextArea();
        privateKeyArea.setWrapText(true);
        privateKeyArea.setEditable(false);
        privateKeyArea.setPrefRowCount(3);
        privateKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #ff6b6b; -fx-font-family: 'Courier New'; -fx-font-size: 10px;");

        keyDisplaySection.getChildren().addAll(publicKeyLabel, publicKeyArea, privateKeyLabel, privateKeyArea);

        // Section sauvegarde/chargement
        VBox ioSection = createStyledSection("Sauvegarde et Chargement des Clés");

        HBox saveBox = new HBox(10);
        saveBox.setAlignment(Pos.CENTER);
        Button saveAllButton = createStyledButton("💾 Sauvegarder les Deux Clés", "#4ecca3");
        saveAllButton.setOnAction(e -> saveKeys());

        Button savePublicButton = createStyledButton("💾 Sauvegarder Clé Publique", "#3498db");
        savePublicButton.setOnAction(e -> savePublicKeyOnly());

        Button savePrivateButton = createStyledButton("💾 Sauvegarder Clé Privée", "#e74c3c");
        savePrivateButton.setOnAction(e -> savePrivateKeyOnly());

        saveBox.getChildren().addAll(saveAllButton, savePublicButton, savePrivateButton);

        HBox loadBox = new HBox(10);
        loadBox.setAlignment(Pos.CENTER);
        Button loadBothButton = createStyledButton("📂 Charger les Deux Clés", "#f39c12");
        loadBothButton.setOnAction(e -> loadKeys());
        loadBox.getChildren().add(loadBothButton);

        ioSection.getChildren().addAll(saveBox, loadBox);

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

        // Section clé de chiffrement
        VBox keySection = createStyledSection("Clé de Chiffrement");
        Label keyInfoLabel = new Label("Clé publique chargée : Aucune");
        keyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox keyButtonBox = new HBox(10);
        keyButtonBox.setAlignment(Pos.CENTER);
        Button loadPublicKeyButton = createStyledButton("📂 Charger Clé Publique", "#3498db");
        loadPublicKeyButton.setOnAction(e -> {
            loadPublicKeyForEncryption();
            if (encryptionKeys != null) {
                keyInfoLabel.setText("Clé publique chargée : ✅ Prête pour le chiffrement");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGeneratedKeyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGeneratedKeyButton.setOnAction(e -> {
            if (currentKeys != null) {
                encryptionKeys = currentKeys;
                keyInfoLabel.setText("Clé publique chargée : ✅ Utilisation de la clé générée");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé publique prête pour le chiffrement", "#4ecca3");
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

        HBox inputButtonBox = new HBox(10);
        inputButtonBox.setAlignment(Pos.CENTER);
        Button loadTextButton = createStyledButton("📂 Charger Texte depuis Fichier", "#9b59b6");
        loadTextButton.setOnAction(e -> loadPlainTextFromFile(inputTextArea));
        Button encryptButton = createStyledButton("🔒 Chiffrer avec ElGamal", "#4ecca3");
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

        // Section clé de déchiffrement
        VBox keySection = createStyledSection("Clé de Déchiffrement");
        Label keyInfoLabel = new Label("Clé privée chargée : Aucune");
        keyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox keyButtonBox = new HBox(10);
        keyButtonBox.setAlignment(Pos.CENTER);
        Button loadPrivateKeyButton = createStyledButton("📂 Charger Clé Privée", "#e74c3c");
        loadPrivateKeyButton.setOnAction(e -> {
            loadPrivateKeyForDecryption();
            if (decryptionKeys != null) {
                keyInfoLabel.setText("Clé privée chargée : ✅ Prête pour le déchiffrement");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGeneratedKeyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGeneratedKeyButton.setOnAction(e -> {
            if (currentKeys != null) {
                decryptionKeys = currentKeys;
                keyInfoLabel.setText("Clé privée chargée : ✅ Utilisation de la clé générée");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé privée prête pour le déchiffrement", "#4ecca3");
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

        HBox inputButtonBox = new HBox(10);
        inputButtonBox.setAlignment(Pos.CENTER);
        Button loadCipherButton = createStyledButton("📂 Charger Texte Chiffré depuis Fichier", "#9b59b6");
        loadCipherButton.setOnAction(e -> loadCipherTextFromFile(encryptedTextArea));
        Button decryptButton = createStyledButton("🔓 Déchiffrer avec ElGamal", "#f39c12");
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

    private Tab createInfoTab() {
        Tab tab = new Tab("ℹ️ Informations");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        VBox infoSection = createStyledSection("Qu'est-ce qu'ElGamal ?");

        Label info1 = new Label("ElGamal est un système de chiffrement asymétrique basé sur le problème du logarithme discret.");
        Label info2 = new Label("• Inventé par Taher ElGamal en 1985");
        Label info3 = new Label("• Utilise une paire de clés : publique (pour chiffrer) et privée (pour déchiffrer)");
        Label info4 = new Label("• Chaque chiffrement produit un résultat différent (probabiliste)");
        Label info5 = new Label("• Sécurité basée sur la difficulté du logarithme discret");

        info1.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-wrap-text: true;");
        info2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info5.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        infoSection.getChildren().addAll(info1, info2, info3, info4, info5);

        VBox compSection = createStyledSection("ElGamal vs RSA");

        Label comp1 = new Label("Similitudes :");
        comp1.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold;");
        Label comp2 = new Label("• Les deux sont asymétriques (clé publique + clé privée)");
        Label comp3 = new Label("• Même usage : chiffrement et signatures numériques");

        Label comp4 = new Label("\nDifférences :");
        comp4.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        Label comp5 = new Label("• ElGamal est probabiliste (même texte → chiffrés différents)");
        Label comp6 = new Label("• RSA est déterministe (même texte → même chiffré)");
        Label comp7 = new Label("• ElGamal produit des chiffrés plus longs (2x la taille)");

        comp2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        comp3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        comp5.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        comp6.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        comp7.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        compSection.getChildren().addAll(comp1, comp2, comp3, comp4, comp5, comp6, comp7);

        VBox usageSection = createStyledSection("Cas d'Usage");

        Label usage1 = new Label("✅ Communications sécurisées");
        Label usage2 = new Label("✅ Signatures numériques");
        Label usage3 = new Label("✅ Cryptographie à courbes elliptiques (variant)");
        Label usage4 = new Label("✅ Protocoles d'échange de clés");

        usage1.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        usageSection.getChildren().addAll(usage1, usage2, usage3, usage4);

        content.getChildren().addAll(infoSection, compSection, usageSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

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
            showStatus("⏳ Génération des clés en cours (cela peut prendre quelques secondes)...", "#f39c12");

            // Générer les clés dans un thread séparé pour ne pas bloquer l'UI
            new Thread(() -> {
                try {
                    IElGamal.ElGamalKeys keys = crypto.generateKeys(keySize);

                    javafx.application.Platform.runLater(() -> {
                        currentKeys = keys;
                        publicKeyArea.setText(crypto.keysToString(new IElGamal.ElGamalKeys(keys.p, keys.g, keys.y, null)));
                        privateKeyArea.setText(crypto.keysToString(new IElGamal.ElGamalKeys(keys.p, null, null, keys.x)));
                        showStatus("✅ Clés ElGamal " + keySize + " bits générées avec succès!", "#4ecca3");
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                            showError("Erreur lors de la génération: " + ex.getMessage())
                    );
                }
            }).start();

        } catch (Exception e) {
            showError("Erreur lors de la génération des clés: " + e.getMessage());
        }
    }

    private void saveKeys() {
        if (currentKeys == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer des clés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique ElGamal", "*.pub"));
        File publicFile = fileChooser.showSaveDialog(null);

        if (publicFile != null) {
            fileChooser.setTitle("Sauvegarder la Clé Privée");
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée ElGamal", "*.key"));
            File privateFile = fileChooser.showSaveDialog(null);

            if (privateFile != null) {
                try {
                    crypto.saveKeys(currentKeys, publicFile.getAbsolutePath(), privateFile.getAbsolutePath());
                    showStatus("✅ Clés sauvegardées avec succès!", "#4ecca3");
                } catch (Exception e) {
                    showError("Erreur lors de la sauvegarde: " + e.getMessage());
                }
            }
        }
    }

    private void savePublicKeyOnly() {
        if (currentKeys == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer des clés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique ElGamal", "*.pub"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                crypto.savePublicKey(currentKeys.p, currentKeys.g, currentKeys.y, file.getAbsolutePath());
                showStatus("✅ Clé publique sauvegardée avec succès!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    private void savePrivateKeyOnly() {
        if (currentKeys == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer des clés.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Privée");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée ElGamal", "*.key"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                crypto.savePrivateKey(currentKeys.p, currentKeys.x, file.getAbsolutePath());
                showStatus("✅ Clé privée sauvegardée avec succès!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    private void loadKeys() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique ElGamal", "*.pub"));
        File publicFile = fileChooser.showOpenDialog(null);

        if (publicFile != null) {
            fileChooser.setTitle("Charger la Clé Privée");
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée ElGamal", "*.key"));
            File privateFile = fileChooser.showOpenDialog(null);

            if (privateFile != null) {
                try {
                    IElGamal.ElGamalKeys pubKeys = crypto.loadPublicKey(publicFile.getAbsolutePath());
                    IElGamal.ElGamalKeys privKeys = crypto.loadPrivateKey(privateFile.getAbsolutePath());

                    currentKeys = new IElGamal.ElGamalKeys(pubKeys.p, pubKeys.g, pubKeys.y, privKeys.x);

                    publicKeyArea.setText(crypto.keysToString(new IElGamal.ElGamalKeys(pubKeys.p, pubKeys.g, pubKeys.y, null)));
                    privateKeyArea.setText(crypto.keysToString(new IElGamal.ElGamalKeys(privKeys.p, null, null, privKeys.x)));

                    showStatus("✅ Clés chargées avec succès!", "#4ecca3");
                } catch (Exception e) {
                    showError("Erreur lors du chargement: " + e.getMessage());
                }
            }
        }
    }

    private void loadPublicKeyForEncryption() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Publique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Publique ElGamal", "*.pub"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                encryptionKeys = crypto.loadPublicKey(file.getAbsolutePath());
                showStatus("✅ Clé publique chargée pour le chiffrement!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement: " + e.getMessage());
            }
        }
    }

    private void loadPrivateKeyForDecryption() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Privée");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé Privée ElGamal", "*.key"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                decryptionKeys = crypto.loadPrivateKey(file.getAbsolutePath());
                showStatus("✅ Clé privée chargée pour le déchiffrement!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement: " + e.getMessage());
            }
        }
    }

    private void encryptText() {
        IElGamal.ElGamalKeys keysToUse = encryptionKeys != null ? encryptionKeys : currentKeys;

        if (keysToUse == null || keysToUse.p == null || keysToUse.g == null || keysToUse.y == null) {
            showError("Veuillez d'abord générer ou charger une clé publique!");
            return;
        }

        String plainText = inputTextArea.getText();
        if (plainText.isEmpty()) {
            showError("Veuillez entrer un texte à chiffrer!");
            return;
        }

        try {
            String encrypted = crypto.encrypt(keysToUse.p, keysToUse.g, keysToUse.y, plainText);
            outputTextArea.setText(encrypted);
            showStatus("✅ Texte chiffré avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du chiffrement: " + e.getMessage());
        }
    }

    private void decryptText(TextArea encryptedTextArea) {
        IElGamal.ElGamalKeys keysToUse = decryptionKeys != null ? decryptionKeys : currentKeys;

        if (keysToUse == null || keysToUse.p == null || keysToUse.x == null) {
            showError("Veuillez d'abord générer ou charger une clé privée!");
            return;
        }

        String cipherText = encryptedTextArea.getText();
        if (cipherText.isEmpty()) {
            showError("Veuillez entrer un texte chiffré à déchiffrer!");
            return;
        }

        try {
            String decrypted = crypto.decrypt(keysToUse.p, keysToUse.x, cipherText);
            decryptedOutputTextArea.setText(decrypted);
            showStatus("✅ Texte déchiffré avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du déchiffrement: " + e.getMessage());
        }
    }

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

    private void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) {
            showError("Rien à copier!");
            return;
        }
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        showStatus("📋 Copié dans le presse-papiers!", "#3498db");
    }

    private void showStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13px;");
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