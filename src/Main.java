import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🔐 Système de Chiffrement - Menu Principal");

        // Conteneur principal
        VBox mainLayout = new VBox(30);
        mainLayout.setPadding(new Insets(50));
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");

        // En-tête avec icône et titre
        VBox header = createHeader();

        // Section de choix
        VBox choiceSection = createChoiceSection(primaryStage);

        // Section informative
        VBox infoSection = createInfoSection();

        // Bouton Quitter
        Button quitButton = createStyledButton("❌ Quitter", "#e74c3c");
        quitButton.setPrefWidth(350);
        quitButton.setOnAction(e -> primaryStage.close());

        mainLayout.getChildren().addAll(header, choiceSection, infoSection, quitButton);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.setStyle(
                "-fx-background: #1a1a2e;" +
                        "-fx-background-color: #1a1a2e;"
        );

        Scene scene = new Scene(scrollPane, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: rgba(78, 204, 163, 0.1); -fx-border-color: #4ecca3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Titre principal
        Label titleLabel = new Label("🔐 SYSTÈME DE CHIFFREMENT");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.web("#4ecca3"));

        // Sous-titre
        Label subtitleLabel = new Label("Choisissez votre méthode de chiffrement et d'échange de clés");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitleLabel.setTextFill(Color.web("#93a8ac"));

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private VBox createChoiceSection(Stage primaryStage) {
        VBox choiceSection = new VBox(20);
        choiceSection.setAlignment(Pos.CENTER);

        // Ligne 1: AES et RSA
        HBox row1 = new HBox(20);
        row1.setAlignment(Pos.CENTER);

        VBox aesBox = createAlgorithmBox(
                "🔒 AES",
                "Chiffrement Symétrique",
                "• Une seule clé secrète\n• Très rapide\n• Idéal pour gros fichiers\n• Tailles: 128/192/256 bits",
                "#3498db",
                primaryStage,
                "AES"
        );

        VBox rsaBox = createAlgorithmBox(
                "🔑 RSA",
                "Chiffrement Asymétrique",
                "• Deux clés (publique + privée)\n• Sécurité maximale\n• Idéal pour échange de clés\n• Tailles: 2048/3072 bits",
                "#9b59b6",
                primaryStage,
                "RSA"
        );

        row1.getChildren().addAll(aesBox, rsaBox);

        // Ligne 2: ElGamal et Diffie-Hellman
        HBox row2 = new HBox(20);
        row2.setAlignment(Pos.CENTER);

        VBox elgamalBox = createAlgorithmBox(
                "🔐 ElGamal",
                "Chiffrement Asymétrique",
                "• Basé sur logarithme discret\n• Chiffrement probabiliste\n• Très sécurisé\n• Tailles: 512/1024/2048 bits",
                "#e74c3c",
                primaryStage,
                "ElGamal"
        );

        VBox dhBox = createAlgorithmBox(
                "📡 Diffie-Hellman",
                "Échange de Clés",
                "• Protocole d'accord de clé\n• Canal public non sécurisé\n• Dérivation de clés\n• Tailles: 512/1024/2048 bits",
                "#f39c12",
                primaryStage,
                "DH"
        );

        row2.getChildren().addAll(elgamalBox, dhBox);

        // Ligne 3: Hashage
        HBox row3 = new HBox(20);
        row3.setAlignment(Pos.CENTER);
        row3.setPrefWidth(600);

        VBox hashBox = createAlgorithmBox(
                "# Hash",
                "Fonctions de Hashage",
                "• Intégrité des données\n• MD5, SHA-1, SHA-256, SHA-512\n• Vérification de fichiers\n• Signatures numériques",
                "#1abc9c",
                primaryStage,
                "Hash"
        );

        row3.getChildren().add(hashBox);

        choiceSection.getChildren().addAll(row1, row2, row3);
        return choiceSection;
    }

    private VBox createAlgorithmBox(String title, String subtitle, String description,
                                    String color, Stage mainStage, String type) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPrefWidth(520);
        box.setPrefHeight(220);
        box.setStyle(
                "-fx-background-color: rgba(78, 204, 163, 0.05);" +
                        "-fx-border-color: " + color + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        // Titre de l'algorithme
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(color));

        // Sous-titre
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        subtitleLabel.setTextFill(Color.web("#4ecca3"));

        // Description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("System", 12));
        descLabel.setTextFill(Color.web("#93a8ac"));
        descLabel.setWrapText(true);

        // Bouton de sélection
        Button selectButton = createStyledButton("▶ Utiliser " + type, color);
        selectButton.setPrefWidth(200);
        selectButton.setOnAction(e -> {
            openApplication(type, mainStage);
        });

        // Effet de survol
        box.setOnMouseEntered(e -> {
            box.setStyle(
                    "-fx-background-color: rgba(78, 204, 163, 0.15);" +
                            "-fx-border-color: " + color + ";" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;" +
                            "-fx-scale-x: 1.02;" +
                            "-fx-scale-y: 1.02;"
            );
        });

        box.setOnMouseExited(e -> {
            box.setStyle(
                    "-fx-background-color: rgba(78, 204, 163, 0.05);" +
                            "-fx-border-color: " + color + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;"
            );
        });

        // Clic sur la box entière
        box.setOnMouseClicked(e -> selectButton.fire());

        box.getChildren().addAll(titleLabel, subtitleLabel, descLabel, selectButton);
        return box;
    }

    private VBox createInfoSection() {
        VBox infoSection = new VBox(10);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setPadding(new Insets(20));
        infoSection.setStyle(
                "-fx-background-color: rgba(78, 204, 163, 0.05);" +
                        "-fx-border-color: #f39c12;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        Label infoTitle = new Label("💡 Besoin d'aide pour choisir ?");
        infoTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        infoTitle.setTextFill(Color.web("#f39c12"));

        Label info1 = new Label("• AES : Choisissez si vous avez une clé secrète partagée (plus rapide)");
        Label info2 = new Label("• RSA : Communication première fois, échange de clés, signatures numériques");
        Label info3 = new Label("• ElGamal : Alternative à RSA avec chiffrement probabiliste");
        Label info4 = new Label("• Diffie-Hellman : Établir une clé secrète partagée via un canal public");
        Label info5 = new Label("• Hash : Vérifier l'intégrité de fichiers, stocker mots de passe, blockchain");

        info1.setFont(Font.font("System", 12));
        info2.setFont(Font.font("System", 12));
        info3.setFont(Font.font("System", 12));
        info4.setFont(Font.font("System", 12));
        info5.setFont(Font.font("System", 12));

        info1.setTextFill(Color.web("#93a8ac"));
        info2.setTextFill(Color.web("#93a8ac"));
        info3.setTextFill(Color.web("#93a8ac"));
        info4.setTextFill(Color.web("#93a8ac"));
        info5.setTextFill(Color.web("#93a8ac"));

        infoSection.getChildren().addAll(infoTitle, info1, info2, info3, info4, info5);
        return infoSection;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 30 12 30;" +
                        "-fx-cursor: hand;" +
                        "-fx-background-radius: 8;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: derive(" + color + ", -20%);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30 12 30;" +
                            "-fx-cursor: hand;" +
                            "-fx-background-radius: 8;" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + color + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30 12 30;" +
                            "-fx-cursor: hand;" +
                            "-fx-background-radius: 8;"
            );
        });

        return button;
    }

    private void openApplication(String type, Stage mainStage) {
        try {
            switch (type) {
                case "AES":
                    new AESCryptoGUI().start(new Stage());
                    break;
                case "RSA":
                    new RSACryptoGUI().start(new Stage());
                    break;
                case "ElGamal":
                    new ElGamalCryptoGUI().start(new Stage());
                    break;
                case "DH":
                    new DiffieHellmanGUI().start(new Stage());
                    break;
                case "Hash":
                    new HashCryptoGUI().start(new Stage());
                    break;
                default:
                    break;
            }
            mainStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}