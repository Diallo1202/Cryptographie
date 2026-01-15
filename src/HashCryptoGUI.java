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

public class HashCryptoGUI extends Application {

    private HashImplement hashImpl = new HashImplement();

    private TextArea inputTextArea;
    private TextArea hashOutputArea;
    private TextArea verifyTextArea;
    private TextArea verifyHashArea;
    private Label verifyResultLabel;
    private Label fileHashLabel;
    private Label fileVerifyResultLabel;
    private Label statusLabel;
    private ComboBox<String> algorithmCombo;
    private ComboBox<String> verifyAlgorithmCombo;
    private ComboBox<String> fileAlgorithmCombo;
    private ComboBox<String> fileVerifyAlgorithmCombo;
    private File selectedFile;
    private File verifySelectedFile;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔐 Fonctions de Hashage - Application Sécurisée");

        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // En-tête
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Contenu principal avec onglets
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab hashTab = createHashTab();
        Tab verifyTab = createVerifyTab();
        Tab fileHashTab = createFileHashTab();
        Tab infoTab = createInfoTab();

        hashTab.setClosable(false);
        verifyTab.setClosable(false);
        fileHashTab.setClosable(false);
        infoTab.setClosable(false);

        tabPane.getTabs().addAll(hashTab, verifyTab, fileHashTab, infoTab);

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

        Label titleLabel = new Label("🔐 Système de Hashage Cryptographique");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        Label subtitleLabel = new Label("MD5, SHA-1, SHA-256, SHA-512 - Intégrité et Empreintes");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#93a8ac"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private Tab createHashTab() {
        Tab tab = new Tab("# Calculer un Hash");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // Section algorithme
        VBox algoSection = createStyledSection("Algorithme de Hashage");
        HBox algoBox = new HBox(10);
        algoBox.setAlignment(Pos.CENTER_LEFT);

        Label algoLabel = new Label("Sélectionnez l'algorithme :");
        algoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        algorithmCombo = new ComboBox<>();
        algorithmCombo.getItems().addAll(
                "SHA-256 (Recommandé)",
                "SHA-512 (Très sécurisé)",
                "SHA-1 (Déprécié)",
                "MD5 (Déprécié)"
        );
        algorithmCombo.setValue("SHA-256 (Recommandé)");
        algorithmCombo.setPrefWidth(200);
        algorithmCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        Label warningLabel = new Label("⚠️ MD5 et SHA-1 sont vulnérables, utilisez-les uniquement pour des tests");
        warningLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 11px; -fx-font-style: italic;");

        algoBox.getChildren().addAll(algoLabel, algorithmCombo);
        algoSection.getChildren().addAll(algoBox, warningLabel);

        // Section texte à hasher
        VBox inputSection = createStyledSection("Texte à Hasher");

        inputTextArea = new TextArea();
        inputTextArea.setPromptText("Entrez le texte dont vous voulez calculer le hash...");
        inputTextArea.setPrefRowCount(6);
        inputTextArea.setWrapText(true);
        inputTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        HBox inputButtonBox = new HBox(10);
        inputButtonBox.setAlignment(Pos.CENTER);
        Button loadTextButton = createStyledButton("📂 Charger Texte depuis Fichier", "#9b59b6");
        loadTextButton.setOnAction(e -> loadPlainTextFromFile(inputTextArea));
        Button hashButton = createStyledButton("# Calculer le Hash", "#4ecca3");
        hashButton.setOnAction(e -> calculateHash());
        inputButtonBox.getChildren().addAll(loadTextButton, hashButton);

        inputSection.getChildren().addAll(inputTextArea, inputButtonBox);

        // Section hash résultat
        VBox outputSection = createStyledSection("Hash Calculé (Hexadécimal)");

        hashOutputArea = new TextArea();
        hashOutputArea.setEditable(false);
        hashOutputArea.setPrefRowCount(3);
        hashOutputArea.setWrapText(true);
        hashOutputArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New';");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button copyButton = createStyledButton("📋 Copier", "#3498db");
        copyButton.setOnAction(e -> copyToClipboard(hashOutputArea.getText()));
        Button saveButton = createStyledButton("💾 Sauvegarder", "#f39c12");
        saveButton.setOnAction(e -> saveTextToFile(hashOutputArea.getText(), "Hash"));
        Button clearButton = createStyledButton("🗑️ Effacer", "#e74c3c");
        clearButton.setOnAction(e -> {
            inputTextArea.clear();
            hashOutputArea.clear();
        });
        buttonBox.getChildren().addAll(copyButton, saveButton, clearButton);

        outputSection.getChildren().addAll(hashOutputArea, buttonBox);

        content.getChildren().addAll(algoSection, inputSection, outputSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createVerifyTab() {
        Tab tab = new Tab("✅ Vérifier un Hash");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // Section algorithme
        VBox algoSection = createStyledSection("Algorithme de Hashage");
        HBox algoBox = new HBox(10);
        algoBox.setAlignment(Pos.CENTER_LEFT);

        Label algoLabel = new Label("Sélectionnez l'algorithme :");
        algoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        verifyAlgorithmCombo = new ComboBox<>();
        verifyAlgorithmCombo.getItems().addAll(
                "SHA-256 (Recommandé)",
                "SHA-512 (Très sécurisé)",
                "SHA-1 (Déprécié)",
                "MD5 (Déprécié)"
        );
        verifyAlgorithmCombo.setValue("SHA-256 (Recommandé)");
        verifyAlgorithmCombo.setPrefWidth(200);
        verifyAlgorithmCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        algoBox.getChildren().addAll(algoLabel, verifyAlgorithmCombo);
        algoSection.getChildren().add(algoBox);

        // Section texte original
        VBox textSection = createStyledSection("Texte Original");

        verifyTextArea = new TextArea();
        verifyTextArea.setPromptText("Entrez le texte original...");
        verifyTextArea.setPrefRowCount(4);
        verifyTextArea.setWrapText(true);
        verifyTextArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        Button loadVerifyTextButton = createStyledButton("📂 Charger Texte", "#9b59b6");
        loadVerifyTextButton.setOnAction(e -> loadPlainTextFromFile(verifyTextArea));

        textSection.getChildren().addAll(verifyTextArea, loadVerifyTextButton);

        // Section hash à vérifier
        VBox hashSection = createStyledSection("Hash à Vérifier");

        verifyHashArea = new TextArea();
        verifyHashArea.setPromptText("Collez le hash à vérifier (hexadécimal)...");
        verifyHashArea.setPrefRowCount(2);
        verifyHashArea.setWrapText(true);
        verifyHashArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        Button loadHashButton = createStyledButton("📂 Charger Hash", "#9b59b6");
        loadHashButton.setOnAction(e -> loadHashFromFile(verifyHashArea));

        hashSection.getChildren().addAll(verifyHashArea, loadHashButton);

        // Bouton vérifier
        Button verifyButton = createStyledButton("✅ Vérifier l'Intégrité", "#4ecca3");
        verifyButton.setOnAction(e -> verifyHash());

        // Résultat
        VBox resultSection = createStyledSection("Résultat de la Vérification");
        verifyResultLabel = new Label("");
        verifyResultLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        verifyResultLabel.setWrapText(true);
        verifyResultLabel.setPadding(new Insets(15));
        verifyResultLabel.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-background-radius: 5;");
        resultSection.getChildren().add(verifyResultLabel);

        content.getChildren().addAll(algoSection, textSection, hashSection, verifyButton, resultSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createFileHashTab() {
        Tab tab = new Tab("📁 Hash de Fichiers");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // ========== SECTION CALCULER HASH FICHIER ==========
        VBox calcSection = createStyledSection("📊 Calculer le Hash d'un Fichier");

        HBox algoBox1 = new HBox(10);
        algoBox1.setAlignment(Pos.CENTER_LEFT);
        Label algoLabel1 = new Label("Algorithme :");
        algoLabel1.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        fileAlgorithmCombo = new ComboBox<>();
        fileAlgorithmCombo.getItems().addAll(
                "SHA-256 (Recommandé)",
                "SHA-512 (Très sécurisé)",
                "SHA-1 (Déprécié)",
                "MD5 (Déprécié)"
        );
        fileAlgorithmCombo.setValue("SHA-256 (Recommandé)");
        fileAlgorithmCombo.setPrefWidth(200);
        fileAlgorithmCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        algoBox1.getChildren().addAll(algoLabel1, fileAlgorithmCombo);

        Label fileSelectLabel = new Label("Aucun fichier sélectionné");
        fileSelectLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        HBox fileButtonBox = new HBox(10);
        fileButtonBox.setAlignment(Pos.CENTER);
        Button selectFileButton = createStyledButton("📂 Sélectionner un Fichier", "#3498db");
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un Fichier");
            selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                fileSelectLabel.setText("Fichier : " + selectedFile.getName());
                fileSelectLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        Button calcFileHashButton = createStyledButton("# Calculer Hash", "#4ecca3");
        calcFileHashButton.setOnAction(e -> calculateFileHash());

        fileButtonBox.getChildren().addAll(selectFileButton, calcFileHashButton);

        fileHashLabel = new Label("");
        fileHashLabel.setWrapText(true);
        fileHashLabel.setFont(Font.font("Courier New", 12));
        fileHashLabel.setStyle("-fx-text-fill: #4ecca3; -fx-padding: 10; -fx-background-color: rgba(78, 204, 163, 0.1); -fx-background-radius: 5;");

        HBox copyBox = new HBox(10);
        copyBox.setAlignment(Pos.CENTER);
        Button copyFileHashButton = createStyledButton("📋 Copier Hash", "#3498db");
        copyFileHashButton.setOnAction(e -> {
            if (!fileHashLabel.getText().isEmpty()) {
                copyToClipboard(fileHashLabel.getText());
            }
        });
        copyBox.getChildren().add(copyFileHashButton);

        calcSection.getChildren().addAll(algoBox1, fileSelectLabel, fileButtonBox, fileHashLabel, copyBox);

        // ========== SECTION VÉRIFIER HASH FICHIER ==========
        VBox verifySection = createStyledSection("✅ Vérifier l'Intégrité d'un Fichier");

        HBox algoBox2 = new HBox(10);
        algoBox2.setAlignment(Pos.CENTER_LEFT);
        Label algoLabel2 = new Label("Algorithme :");
        algoLabel2.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        fileVerifyAlgorithmCombo = new ComboBox<>();
        fileVerifyAlgorithmCombo.getItems().addAll(
                "SHA-256 (Recommandé)",
                "SHA-512 (Très sécurisé)",
                "SHA-1 (Déprécié)",
                "MD5 (Déprécié)"
        );
        fileVerifyAlgorithmCombo.setValue("SHA-256 (Recommandé)");
        fileVerifyAlgorithmCombo.setPrefWidth(200);
        fileVerifyAlgorithmCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        algoBox2.getChildren().addAll(algoLabel2, fileVerifyAlgorithmCombo);

        Label verifyFileSelectLabel = new Label("Aucun fichier sélectionné");
        verifyFileSelectLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        Button selectVerifyFileButton = createStyledButton("📂 Sélectionner un Fichier", "#3498db");
        selectVerifyFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sélectionner un Fichier");
            verifySelectedFile = fileChooser.showOpenDialog(null);
            if (verifySelectedFile != null) {
                verifyFileSelectLabel.setText("Fichier : " + verifySelectedFile.getName());
                verifyFileSelectLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        TextArea expectedHashArea = new TextArea();
        expectedHashArea.setPromptText("Collez le hash attendu...");
        expectedHashArea.setPrefRowCount(2);
        expectedHashArea.setWrapText(true);
        expectedHashArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

        Button verifyFileButton = createStyledButton("✅ Vérifier l'Intégrité", "#4ecca3");
        verifyFileButton.setOnAction(e -> verifyFileHash(expectedHashArea));

        fileVerifyResultLabel = new Label("");
        fileVerifyResultLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        fileVerifyResultLabel.setWrapText(true);
        fileVerifyResultLabel.setPadding(new Insets(15));
        fileVerifyResultLabel.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-background-radius: 5;");

        verifySection.getChildren().addAll(algoBox2, verifyFileSelectLabel, selectVerifyFileButton, expectedHashArea, verifyFileButton, fileVerifyResultLabel);

        content.getChildren().addAll(calcSection, verifySection);

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

        VBox infoSection = createStyledSection("Qu'est-ce qu'une Fonction de Hashage ?");

        Label info1 = new Label("Une fonction de hashage convertit des données de taille variable en une empreinte de taille fixe.");
        Label info2 = new Label("• Déterministe : Le même texte produit toujours le même hash");
        Label info3 = new Label("• Rapide à calculer");
        Label info4 = new Label("• Impossible à inverser (fonction à sens unique)");
        Label info5 = new Label("• Résistant aux collisions (deux textes différents = deux hash différents)");

        info1.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-wrap-text: true;");
        info2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info5.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        infoSection.getChildren().addAll(info1, info2, info3, info4, info5);

        VBox algoSection = createStyledSection("Algorithmes Disponibles");

        Label md5Info = new Label("• MD5 (128 bits) : ❌ Déprécié - Vulnérable aux collisions");
        Label sha1Info = new Label("• SHA-1 (160 bits) : ❌ Déprécié - Vulnérabilités connues");
        Label sha256Info = new Label("• SHA-256 (256 bits) : ✅ Recommandé - Sécurisé et standard");
        Label sha512Info = new Label("• SHA-512 (512 bits) : ✅ Très sécurisé - Pour haute sécurité");

        md5Info.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        sha1Info.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        sha256Info.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px;");
        sha512Info.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px;");

        algoSection.getChildren().addAll(md5Info, sha1Info, sha256Info, sha512Info);

        VBox usageSection = createStyledSection("Cas d'Usage");

        Label usage1 = new Label("✅ Vérification de l'intégrité de fichiers téléchargés");
        Label usage2 = new Label("✅ Stockage sécurisé de mots de passe");
        Label usage3 = new Label("✅ Détection de modifications dans des documents");
        Label usage4 = new Label("✅ Signatures numériques");
        Label usage5 = new Label("✅ Blockchain et cryptomonnaies");

        usage1.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage5.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        usageSection.getChildren().addAll(usage1, usage2, usage3, usage4, usage5);

        content.getChildren().addAll(infoSection, algoSection, usageSection);

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

    private void calculateHash() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            showError("Veuillez entrer un texte à hasher!");
            return;
        }

        try {
            String selectedAlgo = algorithmCombo.getValue();
            String algorithm = extractAlgorithm(selectedAlgo);

            String hash = hashImpl.hash(text, algorithm);
            hashOutputArea.setText(hash);

            int bits = hashImpl.getHashSize(algorithm);
            showStatus("✅ Hash calculé avec " + algorithm + " (" + bits + " bits)", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du calcul du hash: " + e.getMessage());
        }
    }

    private void verifyHash() {
        String text = verifyTextArea.getText();
        String expectedHash = verifyHashArea.getText();

        if (text.isEmpty() || expectedHash.isEmpty()) {
            showError("Veuillez remplir le texte et le hash à vérifier!");
            return;
        }

        try {
            String selectedAlgo = verifyAlgorithmCombo.getValue();
            String algorithm = extractAlgorithm(selectedAlgo);

            boolean isValid = hashImpl.verify(text, expectedHash, algorithm);

            if (isValid) {
                verifyResultLabel.setText("✅ HASH VALIDE - Le texte n'a pas été modifié");
                verifyResultLabel.setTextFill(Color.web("#4ecca3"));
                showStatus("✅ Hash valide!", "#4ecca3");
            } else {
                verifyResultLabel.setText("❌ HASH INVALIDE - Le texte a été modifié ou le hash est incorrect");
                verifyResultLabel.setTextFill(Color.web("#e74c3c"));
                showStatus("❌ Hash invalide!", "#e74c3c");
            }
        } catch (Exception e) {
            showError("Erreur lors de la vérification: " + e.getMessage());
        }
    }

    private void calculateFileHash() {
        if (selectedFile == null) {
            showError("Veuillez d'abord sélectionner un fichier!");
            return;
        }

        try {
            String selectedAlgo = fileAlgorithmCombo.getValue();
            String algorithm = extractAlgorithm(selectedAlgo);

            String hash = hashImpl.hashFile(selectedFile, algorithm);
            fileHashLabel.setText(hash);

            showStatus("✅ Hash du fichier calculé avec " + algorithm, "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors du calcul du hash: " + e.getMessage());
        }
    }

    private void verifyFileHash(TextArea expectedHashArea) {
        if (verifySelectedFile == null) {
            showError("Veuillez d'abord sélectionner un fichier!");
            return;
        }

        String expectedHash = expectedHashArea.getText();
        if (expectedHash.isEmpty()) {
            showError("Veuillez entrer le hash attendu!");
            return;
        }

        try {
            String selectedAlgo = fileVerifyAlgorithmCombo.getValue();
            String algorithm = extractAlgorithm(selectedAlgo);

            boolean isValid = hashImpl.verifyFile(verifySelectedFile, expectedHash, algorithm);

            if (isValid) {
                fileVerifyResultLabel.setText("✅ FICHIER VALIDE - Le fichier n'a pas été modifié");
                fileVerifyResultLabel.setTextFill(Color.web("#4ecca3"));
                showStatus("✅ Fichier valide!", "#4ecca3");
            } else {
                fileVerifyResultLabel.setText("❌ FICHIER INVALIDE - Le fichier a été modifié ou le hash est incorrect");
                fileVerifyResultLabel.setTextFill(Color.web("#e74c3c"));
                showStatus("❌ Fichier invalide!", "#e74c3c");
            }
        } catch (Exception e) {
            showError("Erreur lors de la vérification: " + e.getMessage());
        }
    }

    private String extractAlgorithm(String selected) {
        if (selected.startsWith("MD5")) return "MD5";
        if (selected.startsWith("SHA-1")) return "SHA-1";
        if (selected.startsWith("SHA-256")) return "SHA-256";
        if (selected.startsWith("SHA-512")) return "SHA-512";
        return "SHA-256";
    }

    private void loadPlainTextFromFile(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un Texte");
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

    private void loadHashFromFile(TextArea textArea) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un Hash");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Hash", "*.hash", "*.md5", "*.sha256"),
                new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                String content = Files.readString(file.toPath()).trim();
                textArea.setText(content);
                showStatus("✅ Hash chargé depuis: " + file.getName(), "#4ecca3");
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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Hash", "*.hash"),
                new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt")
        );

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(text);
                showStatus("✅ Hash sauvegardé dans: " + file.getName(), "#4ecca3");
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