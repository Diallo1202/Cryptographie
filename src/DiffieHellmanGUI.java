import javafx.application.Application;

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
import java.math.BigInteger;

public class DiffieHellmanGUI extends Application {

    private DiffieHellmanImplement dh = new DiffieHellmanImplement();
    private IDiffieHellman.DHParameters currentParams = null;
    private IDiffieHellman.DHKeyPair aliceKeys = null;
    private IDiffieHellman.DHKeyPair bobKeys = null;
    private BigInteger aliceSharedSecret = null;
    private BigInteger bobSharedSecret = null;

    private TextArea paramsArea;
    private TextArea alicePrivateKeyArea;
    private TextArea alicePublicKeyArea;
    private TextArea bobPrivateKeyArea;
    private TextArea bobPublicKeyArea;
    private TextArea aliceSecretArea;
    private TextArea bobSecretArea;
    private Label statusLabel;
    private Label matchLabel;
    private ComboBox<Integer> keySizeCombo;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔐 Échange de Clés Diffie-Hellman - Application Sécurisée");

        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // En-tête
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Contenu principal avec onglets
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab paramsTab = createParametersTab();
        Tab exchangeTab = createExchangeTab();
        Tab infoTab = createInfoTab();

        paramsTab.setClosable(false);
        exchangeTab.setClosable(false);
        infoTab.setClosable(false);

        tabPane.getTabs().addAll(paramsTab, exchangeTab, infoTab);

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

        Scene scene = new Scene(mainLayout, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-border-color: #4ecca3; -fx-border-width: 0 0 2 0;");

        Label titleLabel = new Label("🔐 Protocole d'Échange de Clés Diffie-Hellman");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        Label subtitleLabel = new Label("Établir une clé secrète partagée via un canal public non sécurisé");
        subtitleLabel.setFont(Font.font("System", 14));
        subtitleLabel.setTextFill(Color.web("#93a8ac"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private Tab createParametersTab() {
        Tab tab = new Tab("⚙️ Paramètres Publics");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // Section génération de paramètres
        VBox genSection = createStyledSection("Génération des Paramètres Publics (p, g)");

        Label infoLabel = new Label("Ces paramètres sont publics et peuvent être partagés avec tout le monde");
        infoLabel.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px; -fx-font-style: italic;");

        HBox keySizeBox = new HBox(10);
        keySizeBox.setAlignment(Pos.CENTER_LEFT);
        Label keySizeLabel = new Label("Taille du modulus (p) :");
        keySizeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        keySizeCombo = new ComboBox<>();
        keySizeCombo.getItems().addAll(512, 1024, 2048);
        keySizeCombo.setValue(1024);
        keySizeCombo.setStyle("-fx-background-color: #16213e; -fx-text-fill: white;");

        Label warningLabel = new Label("⚠️ La génération peut prendre quelques secondes");
        warningLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 11px; -fx-font-style: italic;");

        Button generateButton = createStyledButton("🎲 Générer Paramètres", "#4ecca3");
        generateButton.setOnAction(e -> generateParameters());

        keySizeBox.getChildren().addAll(keySizeLabel, keySizeCombo, generateButton);
        genSection.getChildren().addAll(infoLabel, keySizeBox, warningLabel);

        // Section affichage des paramètres
        VBox paramsDisplaySection = createStyledSection("Paramètres Générés (Publics)");

        paramsArea = new TextArea();
        paramsArea.setWrapText(true);
        paramsArea.setEditable(false);
        paramsArea.setPrefRowCount(4);
        paramsArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New'; -fx-font-size: 10px;");

        HBox paramsButtonBox = new HBox(10);
        paramsButtonBox.setAlignment(Pos.CENTER);
        Button saveParamsButton = createStyledButton("💾 Sauvegarder Paramètres", "#3498db");
        saveParamsButton.setOnAction(e -> saveParameters());
        Button loadParamsButton = createStyledButton("📂 Charger Paramètres", "#f39c12");
        loadParamsButton.setOnAction(e -> loadParameters());
        paramsButtonBox.getChildren().addAll(saveParamsButton, loadParamsButton);

        paramsDisplaySection.getChildren().addAll(paramsArea, paramsButtonBox);

        content.getChildren().addAll(genSection, paramsDisplaySection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private Tab createExchangeTab() {
        Tab tab = new Tab("🔄 Échange de Clés");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // Titre de la démo
        Label demoLabel = new Label("Démonstration : Alice et Bob échangent une clé secrète");
        demoLabel.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Conteneur horizontal pour Alice et Bob
        HBox mainBox = new HBox(20);
        mainBox.setAlignment(Pos.TOP_CENTER);

        // ========== ALICE ==========
        VBox aliceBox = createPartyBox("👩 ALICE", "#3498db");

        alicePrivateKeyArea = new TextArea();
        alicePrivateKeyArea.setPromptText("Clé privée d'Alice (secrète)");
        alicePrivateKeyArea.setEditable(false);
        alicePrivateKeyArea.setPrefRowCount(2);
        alicePrivateKeyArea.setWrapText(true);
        alicePrivateKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #ff6b6b; -fx-font-family: 'Courier New'; -fx-font-size: 9px;");

        alicePublicKeyArea = new TextArea();
        alicePublicKeyArea.setPromptText("Clé publique d'Alice (partagée)");
        alicePublicKeyArea.setEditable(false);
        alicePublicKeyArea.setPrefRowCount(2);
        alicePublicKeyArea.setWrapText(true);
        alicePublicKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New'; -fx-font-size: 9px;");

        aliceSecretArea = new TextArea();
        aliceSecretArea.setPromptText("Clé secrète partagée d'Alice");
        aliceSecretArea.setEditable(false);
        aliceSecretArea.setPrefRowCount(1);
        aliceSecretArea.setWrapText(true);
        aliceSecretArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #f39c12; -fx-font-family: 'Courier New'; -fx-font-size: 9px;");

        Button aliceGenButton = createStyledButton("🎲 Générer Clés Alice", "#3498db");
        aliceGenButton.setOnAction(e -> generateAliceKeys());

        Button aliceComputeButton = createStyledButton("🔐 Calculer Secret Alice", "#9b59b6");
        aliceComputeButton.setOnAction(e -> computeAliceSecret());

        aliceBox.getChildren().addAll(
                new Label("🔒 Clé Privée (secrète)"), alicePrivateKeyArea,
                new Label("🔓 Clé Publique (partagée)"), alicePublicKeyArea,
                aliceGenButton,
                new Label("🔑 Clé Secrète Partagée"), aliceSecretArea,
                aliceComputeButton
        );

        // ========== BOB ==========
        VBox bobBox = createPartyBox("👨 BOB", "#e74c3c");

        bobPrivateKeyArea = new TextArea();
        bobPrivateKeyArea.setPromptText("Clé privée de Bob (secrète)");
        bobPrivateKeyArea.setEditable(false);
        bobPrivateKeyArea.setPrefRowCount(2);
        bobPrivateKeyArea.setWrapText(true);
        bobPrivateKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #ff6b6b; -fx-font-family: 'Courier New'; -fx-font-size: 9px;");

        bobPublicKeyArea = new TextArea();
        bobPublicKeyArea.setPromptText("Clé publique de Bob (partagée)");
        bobPublicKeyArea.setEditable(false);
        bobPublicKeyArea.setPrefRowCount(2);
        bobPublicKeyArea.setWrapText(true);
        bobPublicKeyArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #4ecca3; -fx-font-family: 'Courier New'; -fx-font-size: 9px;");

        bobSecretArea = new TextArea();
        bobSecretArea.setPromptText("Clé secrète partagée de Bob");
        bobSecretArea.setEditable(false);
        bobSecretArea.setPrefRowCount(1);
        bobSecretArea.setWrapText(true);
        bobSecretArea.setStyle("-fx-control-inner-background: #16213e; -fx-text-fill: #f39c12; -fx-font-family: 'Courier New'; -fx-font-size: 9px;");

        Button bobGenButton = createStyledButton("🎲 Générer Clés Bob", "#e74c3c");
        bobGenButton.setOnAction(e -> generateBobKeys());

        Button bobComputeButton = createStyledButton("🔐 Calculer Secret Bob", "#9b59b6");
        bobComputeButton.setOnAction(e -> computeBobSecret());

        bobBox.getChildren().addAll(
                new Label("🔒 Clé Privée (secrète)"), bobPrivateKeyArea,
                new Label("🔓 Clé Publique (partagée)"), bobPublicKeyArea,
                bobGenButton,
                new Label("🔑 Clé Secrète Partagée"), bobSecretArea,
                bobComputeButton
        );

        mainBox.getChildren().addAll(aliceBox, bobBox);

        // Section vérification
        VBox verifySection = createStyledSection("✅ Vérification");
        Button verifyButton = createStyledButton("🔍 Vérifier que les Secrets Correspondent", "#4ecca3");
        verifyButton.setOnAction(e -> verifySecrets());

        matchLabel = new Label("");
        matchLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        matchLabel.setWrapText(true);
        matchLabel.setPadding(new Insets(15));
        matchLabel.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-background-radius: 5;");

        verifySection.getChildren().addAll(verifyButton, matchLabel);

        // Bouton de processus complet
        Button autoButton = createStyledButton("🚀 Processus Complet Automatique", "#f39c12");
        autoButton.setOnAction(e -> runCompleteExchange());

        content.getChildren().addAll(demoLabel, mainBox, verifySection, autoButton);

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

        VBox infoSection = createStyledSection("Qu'est-ce que Diffie-Hellman ?");

        Label info1 = new Label("Diffie-Hellman est un protocole d'échange de clés permettant à deux parties de s'accorder sur une clé secrète partagée via un canal public non sécurisé.");
        Label info2 = new Label("• Inventé par Whitfield Diffie et Martin Hellman en 1976");
        Label info3 = new Label("• Première solution pratique au problème d'échange de clés");
        Label info4 = new Label("• Ne permet PAS le chiffrement direct, uniquement l'échange de clés");
        Label info5 = new Label("• Sécurité basée sur le problème du logarithme discret");

        info1.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-wrap-text: true;");
        info2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        info5.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        infoSection.getChildren().addAll(info1, info2, info3, info4, info5);

        VBox processSection = createStyledSection("Le Processus Étape par Étape");

        Label step1 = new Label("1️⃣ Génération des paramètres publics (p, g) - Partagés entre tous");
        Label step2 = new Label("2️⃣ Alice génère sa clé privée a et calcule A = g^a mod p (clé publique)");
        Label step3 = new Label("3️⃣ Bob génère sa clé privée b et calcule B = g^b mod p (clé publique)");
        Label step4 = new Label("4️⃣ Alice et Bob échangent leurs clés publiques (A et B)");
        Label step5 = new Label("5️⃣ Alice calcule le secret : s = B^a mod p");
        Label step6 = new Label("6️⃣ Bob calcule le secret : s = A^b mod p");
        Label step7 = new Label("7️⃣ Alice et Bob ont maintenant la même clé secrète s !");

        step1.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        step2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        step3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        step4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        step5.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        step6.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        step7.setStyle("-fx-text-fill: #4ecca3; -fx-font-size: 12px; -fx-font-weight: bold;");

        processSection.getChildren().addAll(step1, step2, step3, step4, step5, step6, step7);

        VBox usageSection = createStyledSection("Cas d'Usage");

        Label usage1 = new Label("✅ Établir une connexion sécurisée (SSL/TLS)");
        Label usage2 = new Label("✅ VPN et tunnels sécurisés");
        Label usage3 = new Label("✅ Messagerie sécurisée (Signal, WhatsApp)");
        Label usage4 = new Label("✅ SSH et connexions à distance");

        usage1.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage2.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage3.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");
        usage4.setStyle("-fx-text-fill: #93a8ac; -fx-font-size: 12px;");

        usageSection.getChildren().addAll(usage1, usage2, usage3, usage4);

        content.getChildren().addAll(infoSection, processSection, usageSection);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        tab.setContent(scrollPane);

        return tab;
    }

    private VBox createPartyBox(String title, String color) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setPrefWidth(500);
        box.setStyle("-fx-background-color: rgba(78, 204, 163, 0.05); -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web(color));

        // Ajouter le titre comme premier enfant
        box.getChildren().add(titleLabel);

        // Styliser tous les labels
        box.getChildren().addListener((javafx.collections.ListChangeListener<javafx.scene.Node>) c -> {
            while (c.next()) {
                for (javafx.scene.Node node : c.getAddedSubList()) {
                    if (node instanceof Label && node != titleLabel) {
                        ((Label) node).setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                    }
                }
            }
        });

        return box;
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
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: derive(" + color + ", -20%);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 5;"
        ));

        return button;
    }

    private void generateParameters() {
        try {
            int keySize = keySizeCombo.getValue();
            showStatus("⏳ Génération des paramètres en cours...", "#f39c12");

            new Thread(() -> {
                try {
                    IDiffieHellman.DHParameters params = dh.generateParameters(keySize);

                    javafx.application.Platform.runLater(() -> {
                        currentParams = params;
                        paramsArea.setText(dh.parametersToString(params));
                        showStatus("✅ Paramètres " + keySize + " bits générés avec succès!", "#4ecca3");
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                            showError("Erreur lors de la génération: " + ex.getMessage())
                    );
                }
            }).start();

        } catch (Exception e) {
            showError("Erreur lors de la génération: " + e.getMessage());
        }
    }

    private void generateAliceKeys() {
        if (currentParams == null) {
            showError("Veuillez d'abord générer les paramètres publics!");
            return;
        }

        try {
            aliceKeys = dh.generateKeyPair(currentParams);
            alicePrivateKeyArea.setText(aliceKeys.privateKey.toString(16));
            alicePublicKeyArea.setText(aliceKeys.publicKey.toString(16));
            showStatus("✅ Clés d'Alice générées!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur génération Alice: " + e.getMessage());
        }
    }

    private void generateBobKeys() {
        if (currentParams == null) {
            showError("Veuillez d'abord générer les paramètres publics!");
            return;
        }

        try {
            bobKeys = dh.generateKeyPair(currentParams);
            bobPrivateKeyArea.setText(bobKeys.privateKey.toString(16));
            bobPublicKeyArea.setText(bobKeys.publicKey.toString(16));
            showStatus("✅ Clés de Bob générées!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur génération Bob: " + e.getMessage());
        }
    }

    private void computeAliceSecret() {
        if (aliceKeys == null || bobKeys == null) {
            showError("Alice et Bob doivent d'abord générer leurs clés!");
            return;
        }

        try {
            aliceSharedSecret = dh.computeSharedSecret(bobKeys.publicKey, aliceKeys.privateKey, currentParams.p);
            String derivedKey = dh.deriveKey(aliceSharedSecret, 256);
            aliceSecretArea.setText(derivedKey);
            showStatus("✅ Secret partagé d'Alice calculé!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur calcul Alice: " + e.getMessage());
        }
    }

    private void computeBobSecret() {
        if (aliceKeys == null || bobKeys == null) {
            showError("Alice et Bob doivent d'abord générer leurs clés!");
            return;
        }

        try {
            bobSharedSecret = dh.computeSharedSecret(aliceKeys.publicKey, bobKeys.privateKey, currentParams.p);
            String derivedKey = dh.deriveKey(bobSharedSecret, 256);
            bobSecretArea.setText(derivedKey);
            showStatus("✅ Secret partagé de Bob calculé!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur calcul Bob: " + e.getMessage());
        }
    }

    private void verifySecrets() {
        if (aliceSharedSecret == null || bobSharedSecret == null) {
            showError("Alice et Bob doivent d'abord calculer leurs secrets!");
            return;
        }

        if (aliceSharedSecret.equals(bobSharedSecret)) {
            matchLabel.setText("✅ SUCCÈS ! Les secrets correspondent parfaitement !\n" +
                    "Alice et Bob peuvent maintenant utiliser cette clé pour chiffrer leurs communications.");
            matchLabel.setTextFill(Color.web("#4ecca3"));
            showStatus("✅ Les secrets correspondent!", "#4ecca3");
        } else {
            matchLabel.setText("❌ ERREUR ! Les secrets ne correspondent pas.");
            matchLabel.setTextFill(Color.web("#e74c3c"));
            showStatus("❌ Les secrets ne correspondent pas!", "#e74c3c");
        }
    }

    private void runCompleteExchange() {
        if (currentParams == null) {
            showError("Veuillez d'abord générer les paramètres publics!");
            return;
        }

        try {
            // Générer les clés d'Alice
            aliceKeys = dh.generateKeyPair(currentParams);
            alicePrivateKeyArea.setText(aliceKeys.privateKey.toString(16));
            alicePublicKeyArea.setText(aliceKeys.publicKey.toString(16));

            // Générer les clés de Bob
            bobKeys = dh.generateKeyPair(currentParams);
            bobPrivateKeyArea.setText(bobKeys.privateKey.toString(16));
            bobPublicKeyArea.setText(bobKeys.publicKey.toString(16));

            // Calculer les secrets
            aliceSharedSecret = dh.computeSharedSecret(bobKeys.publicKey, aliceKeys.privateKey, currentParams.p);
            String aliceDerivedKey = dh.deriveKey(aliceSharedSecret, 256);
            aliceSecretArea.setText(aliceDerivedKey);

            bobSharedSecret = dh.computeSharedSecret(aliceKeys.publicKey, bobKeys.privateKey, currentParams.p);
            String bobDerivedKey = dh.deriveKey(bobSharedSecret, 256);
            bobSecretArea.setText(bobDerivedKey);

            // Vérifier
            verifySecrets();

            showStatus("✅ Échange complet effectué avec succès!", "#4ecca3");
        } catch (Exception e) {
            showError("Erreur lors de l'échange: " + e.getMessage());
        }
    }

    private void saveParameters() {
        if (currentParams == null) {
            showError("Aucun paramètre à sauvegarder!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder les Paramètres");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Paramètres DH", "*.dhp"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                dh.saveParameters(currentParams, file.getAbsolutePath());
                showStatus("✅ Paramètres sauvegardés!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur sauvegarde: " + e.getMessage());
            }
        }
    }

    private void loadParameters() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger les Paramètres");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Paramètres DH", "*.dhp"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                currentParams = dh.loadParameters(file.getAbsolutePath());
                paramsArea.setText(dh.parametersToString(currentParams));
                showStatus("✅ Paramètres chargés!", "#4ecca3");
            } catch (Exception e) {
                showError("Erreur chargement: " + e.getMessage());
            }
        }
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
