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
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class AESCryptoGUI extends Application {

    private CryptoImplementAES crypto = new CryptoImplementAES();
    private SecretKey currentKey = null;
    private SecretKey encryptionKey = null;
    private SecretKey decryptionKey = null;

    private TextArea keyArea;
    private TextArea inputTextArea;
    private TextArea outputTextArea;
    private TextArea decryptedOutputTextArea;
    private Label statusLabel;
    private ComboBox<Integer> keySizeCombo;
    private ComboBox<String> modeCombo;
    private ComboBox<String> decryptModeCombo;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔐 Chiffrement AES - Application Sécurisée");

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

        keyTab.setClosable(false);
        encryptTab.setClosable(false);
        decryptTab.setClosable(false);

        tabPane.getTabs().addAll(keyTab, encryptTab, decryptTab);

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

        Label titleLabel = new Label("🔐 Système de Chiffrement AES");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        Label subtitleLabel = new Label("Chiffrement symétrique rapide - 128/192/256 bits");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#93a8ac"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private Tab createKeyManagementTab() {
        Tab tab = new Tab("🔑 Gestion de la Clé");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // Section génération de clé
        VBox genSection = createStyledSection("Génération de Clé Secrète AES");

        HBox keySizeBox = new HBox(10);
        keySizeBox.setAlignment(Pos.CENTER_LEFT);
        Label keySizeLabel = new Label("Taille de la clé :");
        keySizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        keySizeCombo = new ComboBox<>();
        keySizeCombo.getItems().addAll(128, 192, 256);
        keySizeCombo.setValue(128);
        keySizeCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        Button generateButton = createStyledButton("🎲 Générer Nouvelle Clé Secrète", "#4ecca3");
        generateButton.setOnAction(e -> generateKey());

        keySizeBox.getChildren().addAll(keySizeLabel, keySizeCombo, generateButton);
        genSection.getChildren().add(keySizeBox);

        // Section affichage de la clé
        VBox keyDisplaySection = createStyledSection("Clé Secrète Générée");

        Label keyLabel = new Label("🔑 Clé Secrète (Base64) - À garder secrète :");
        keyLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-weight: bold;");
        keyArea = new TextArea();
        keyArea.setWrapText(true);
        keyArea.setEditable(false);
        keyArea.setPrefRowCount(3);
        keyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New';");

        Label warningLabel = new Label("⚠️ IMPORTANT : Cette clé doit être partagée de manière sécurisée avec vos correspondants");
        warningLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 12px; -fx-font-style: italic;");

        keyDisplaySection.getChildren().addAll(keyLabel, keyArea, warningLabel);

        // Section sauvegarde/chargement
        VBox ioSection = createStyledSection("Sauvegarde et Chargement de la Clé");

        HBox saveBox = new HBox(10);
        saveBox.setAlignment(Pos.CENTER);
        Button saveButton = createStyledButton("💾 Sauvegarder la Clé", "#4ecca3");
        saveButton.setOnAction(e -> saveKey());
        saveBox.getChildren().add(saveButton);

        HBox loadBox = new HBox(10);
        loadBox.setAlignment(Pos.CENTER);
        Button loadButton = createStyledButton("📂 Charger la Clé", "#f39c12");
        loadButton.setOnAction(e -> loadKey());
        loadBox.getChildren().add(loadButton);

        ioSection.getChildren().addAll(saveBox, loadBox);

        // Section info AES
        VBox infoSection = createStyledSection("ℹ️ À propos du chiffrement AES");
        Label info1 = new Label("• AES est un algorithme de chiffrement symétrique (une seule clé)");
        Label info2 = new Label("• La même clé est utilisée pour chiffrer ET déchiffrer");
        Label info3 = new Label("• Très rapide et adapté au chiffrement de grandes quantités de données");
        Label info4 = new Label("• 256 bits offre le niveau de sécurité le plus élevé");

        info1.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        infoSection.getChildren().addAll(info1, info2, info3, info4);

        content.getChildren().addAll(genSection, keyDisplaySection, ioSection, infoSection);

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
        Label keyInfoLabel = new Label("Clé secrète chargée : Aucune");
        keyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox keyButtonBox = new HBox(10);
        keyButtonBox.setAlignment(Pos.CENTER);
        Button loadKeyButton = createStyledButton("📂 Charger Clé Secrète", "#3498db");
        loadKeyButton.setOnAction(e -> {
            loadKeyForEncryption();
            if (encryptionKey != null) {
                keyInfoLabel.setText("Clé secrète chargée : ✅ Prête pour le chiffrement");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGeneratedKeyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGeneratedKeyButton.setOnAction(e -> {
            if (currentKey != null) {
                encryptionKey = currentKey;
                keyInfoLabel.setText("Clé secrète chargée : ✅ Utilisation de la clé générée");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé de l'onglet 'Gestion' utilisée pour le chiffrement", "#4ecca3");
            } else {
                showError("Aucune clé n'a été générée dans l'onglet 'Gestion de la Clé'");
            }
        });

        keyButtonBox.getChildren().addAll(loadKeyButton, useGeneratedKeyButton);
        keySection.getChildren().addAll(keyInfoLabel, keyButtonBox);


        // Section mode opératoire
        VBox modeSection = createStyledSection("Mode de Chiffrement AES");

        Label modeLabel = new Label("Mode opératoire :");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll(
                "AES/CBC/PKCS5Padding",
                "AES/ECB/PKCS5Padding"
        );
        modeCombo.setValue("AES/CBC/PKCS5Padding");
        modeCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        HBox modeBox = new HBox(10, modeLabel, modeCombo);
        modeBox.setAlignment(Pos.CENTER_LEFT);

        modeSection.getChildren().add(modeBox);


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
        Button encryptButton = createStyledButton("🔒 Chiffrer avec la Clé Secrète", "#4ecca3");
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

        content.getChildren().addAll(keySection, modeSection, inputSection, outputSection);

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
        Label keyInfoLabel = new Label("Clé secrète chargée : Aucune");
        keyInfoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox keyButtonBox = new HBox(10);
        keyButtonBox.setAlignment(Pos.CENTER);
        Button loadKeyButton = createStyledButton("📂 Charger Clé Secrète", "#e74c3c");
        loadKeyButton.setOnAction(e -> {
            loadKeyForDecryption();
            if (decryptionKey != null) {
                keyInfoLabel.setText("Clé secrète chargée : ✅ Prête pour le déchiffrement");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button useGeneratedKeyButton = createStyledButton("🔑 Utiliser Clé Générée", "#9b59b6");
        useGeneratedKeyButton.setOnAction(e -> {
            if (currentKey != null) {
                decryptionKey = currentKey;
                keyInfoLabel.setText("Clé secrète chargée : ✅ Utilisation de la clé générée");
                keyInfoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
                showStatus("✅ Clé de l'onglet 'Gestion' utilisée pour le déchiffrement", "#4ecca3");
            } else {
                showError("Aucune clé n'a été générée dans l'onglet 'Gestion de la Clé'");
            }
        });

        keyButtonBox.getChildren().addAll(loadKeyButton, useGeneratedKeyButton);
        keySection.getChildren().addAll(keyInfoLabel, keyButtonBox);


        // ================= MODE OPERATOIRE (DECHIFFREMENT) =================
        VBox decryptModeSection = createStyledSection("Mode de Déchiffrement AES");

        Label modeLabel = new Label("Mode opératoire :");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        decryptModeCombo = new ComboBox<>();
        decryptModeCombo.getItems().addAll(
                "AES/CBC/PKCS5Padding",
                "AES/ECB/PKCS5Padding"
        );
        decryptModeCombo.setValue("AES/CBC/PKCS5Padding");
        decryptModeCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        HBox modeBox = new HBox(10, modeLabel, decryptModeCombo);
        modeBox.setAlignment(Pos.CENTER_LEFT);

        decryptModeSection.getChildren().add(modeBox);


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
        Button decryptButton = createStyledButton("🔓 Déchiffrer avec la Clé Secrète", "#f39c12");
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

        content.getChildren().addAll(keySection, decryptModeSection, inputSection, outputSection);

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

    private void generateKey() {
        try {
            int keySize = keySizeCombo.getValue();
            currentKey = crypto.genKey("AES", keySize);

            keyArea.setText(crypto.keyToString(currentKey));

            showStatus("✅ Clé AES " + keySize + " bits générée avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors de la génération de la clé: " + e.getMessage());
        }
    }

    private void saveKey() {
        if (currentKey == null) {
            showError("Aucune clé à sauvegarder. Veuillez d'abord générer une clé.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder la Clé Secrète");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé AES", "*.key"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                crypto.saveKey(currentKey, file.getAbsolutePath());
                showStatus("✅ Clé sauvegardée avec succès!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    private void loadKey() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé Secrète");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé AES", "*.key"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                currentKey = crypto.loadKey(file.getAbsolutePath());
                keyArea.setText(crypto.keyToString(currentKey));
                showStatus("✅ Clé chargée avec succès!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement: " + e.getMessage());
            }
        }
    }

    private void loadKeyForEncryption() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé pour Chiffrement");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé AES", "*.key"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                encryptionKey = crypto.loadKey(file.getAbsolutePath());
                showStatus("✅ Clé chargée pour le chiffrement!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement de la clé: " + e.getMessage());
            }
        }
    }

    private void loadKeyForDecryption() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger la Clé pour Déchiffrement");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Clé AES", "*.key"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                decryptionKey = crypto.loadKey(file.getAbsolutePath());
                showStatus("✅ Clé chargée pour le déchiffrement!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur lors du chargement de la clé: " + e.getMessage());
            }
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

    private void encryptText() {
        SecretKey keyToUse = encryptionKey != null ? encryptionKey : currentKey;

        if (keyToUse == null) {
            showError("Veuillez d'abord générer ou charger une clé secrète!");
            return;
        }

        String plainText = inputTextArea.getText();
        if (plainText.isEmpty()) {
            showError("Veuillez entrer un texte à chiffrer!");
            return;
        }

        try {
            String mode = modeCombo.getValue();
            String encrypted = crypto.encrypt(keyToUse, plainText, mode);

            outputTextArea.setText(encrypted);
            showStatus("✅ Texte chiffré avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du chiffrement: " + e.getMessage());
        }
    }

    private void decryptText(TextArea encryptedTextArea) {
        SecretKey keyToUse = decryptionKey != null ? decryptionKey : currentKey;

        if (keyToUse == null) {
            showError("Veuillez d'abord générer ou charger une clé secrète!");
            return;
        }

        String cipherText = encryptedTextArea.getText();
        if (cipherText.isEmpty()) {
            showError("Veuillez entrer un texte chiffré à déchiffrer!");
            return;
        }

        try {
            String mode = decryptModeCombo.getValue();
            String decrypted = crypto.decrypt(keyToUse, cipherText, mode);

            decryptedOutputTextArea.setText(decrypted);
            showStatus("✅ Texte déchiffré avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du déchiffrement: " + e.getMessage());
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