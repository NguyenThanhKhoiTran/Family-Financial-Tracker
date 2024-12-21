import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

import javafx.util.converter.DoubleStringConverter;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.Duration;

//Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi, D Tran Gia Khanh, Gen AI
public class FTTViewBuilder implements Builder<Region> {
    private BorderPane mainPane;
    private ObservableList<TransactionModel> transactionModels;
    private ObservableList<String> categoriesList = FXCollections.observableArrayList("groceries", "gas", "pharmacy",
            "dining", "household", "carcare", "clothes",
            "leisure", "bills", "miscelaneous");
    private ObservableList<String> buyerList = FXCollections.observableArrayList();
    private ObservableList<CategoryModel> categoryModels = FXCollections.observableArrayList();
    private StringProperty filePathProperty;
    private TableView<TransactionModel> table = new TableView<>();
    private TableView<TransactionModel> tableSummary = new TableView<>();
    private Map<String, Double> previousBalances = new LinkedHashMap<>();
    private Runnable browseHandler;
    private Runnable exportHandlerCSV;
    private Runnable exportHandlerXLSX;
    private Runnable exportSummary;
    private Runnable exportTXT;

    // Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
    public FTTViewBuilder(ObservableList<TransactionModel> transactionModelsIn,
            Runnable browseHandlerIn,
            StringProperty filePathPropertyIn,
            Runnable exportHandlerCSV,
            Runnable exportHandlerXLSX,
            Runnable exportSummary,
            Runnable exportTXT) {
        this.transactionModels = transactionModelsIn;
        this.browseHandler = browseHandlerIn;
        this.filePathProperty = filePathPropertyIn;
        this.exportHandlerCSV = exportHandlerCSV;
        this.exportHandlerXLSX = exportHandlerXLSX;
        this.exportSummary = exportSummary;
        this.exportTXT = exportTXT;
    }

    // Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
    @Override
    public Region build() {
        StackPane sp = new StackPane();
        Node introPane = createIntroPane();
        sp.getChildren().addAll(introPane);
        return sp;
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void addMoneyAnimation(StackPane introPane) {
        for (int i = 0; i < 20; i++) {
            Image moneyImage = new Image("file:src/main/java/10944923.png");
            ImageView moneyView = new ImageView(moneyImage);
            moneyView.setFitWidth(50);
            moneyView.setPreserveRatio(true);

            double randomX = Math.random() * 800;
            double randomY = -(Math.random() * 20);

            moneyView.setLayoutX(randomX);
            moneyView.setLayoutY(randomY);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(3 + Math.random() * 2),
                    moneyView);
            transition.setFromX(randomX);
            transition.setFromY(randomY);
            transition.setToX(800);
            transition.setToY(800);
            transition.setCycleCount(TranslateTransition.INDEFINITE);
            transition.play();

            introPane.getChildren().add(moneyView);
        }
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private Node createIntroPane() {
        VBox vb = new VBox(10);
        vb.setPadding(new Insets(20));
        vb.setAlignment(Pos.CENTER);

        StackPane introPane = new StackPane();
        introPane.setStyle("-fx-background-color: #87ceeb;");
        introPane.setPrefSize(800, 600);

        Label introLabel = new Label("Financial Transaction Tracker");
        introLabel.setStyle("-fx-text-fill: #ffffff; -fx-background-color: #008400;");
        introLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        introLabel.setAlignment(Pos.CENTER);

        Button importButton = new Button("Import File");
        importButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #008400; -fx-font-weight: bold;");

        vb.getChildren().addAll(introLabel, importButton);

        importButton.setOnAction(e -> {
            browseHandler.run();
            if (transactionModels.isEmpty()) {
                showMessage("Invalid Data", "The file did not load valid transactions. Please try again.",
                        AlertType.WARNING);
                return;
            }
            StackPane sp = (StackPane) introPane.getParent();
            sp.getChildren().clear();
            sp.getChildren().add(createMainPane());
        });

        introPane.getChildren().add(vb);

        addMoneyAnimation(introPane);
        return introPane;
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh, Queszel Martin
    private Node createMainPane() {
        mainPane = new BorderPane();

        Button switchToSummarySpending = new Button("Switch to Summary Spending Page");
        Button switchToExportPane = new Button("Switch to Export Full Version");
        mainPane.setTop(createToolBar());

        Button exportTxtFile = new Button("Export Categories List (.txt)");
        Button addBuyer = new Button("Add Buyer");
        Button addCategory = new Button("Add Category");
        Button editCategoryButton = new Button("Edit Category");
        Button deleteCategoryButton = new Button("Delete Category");
        Button editBuyerButton = new Button("Edit Buyer");
        Button deleteBuyerButton = new Button("Delete Buyer");
        Button assignBuyerToCategoryButton = new Button("Assign Buyer to Category");

        VBox categoryButtonBox = new VBox(8, addCategory, editCategoryButton, deleteCategoryButton);

        VBox buyerButtonBox = new VBox(8, addBuyer, editBuyerButton, deleteBuyerButton, assignBuyerToCategoryButton);

        Region spacer = new Region();
        spacer.setMinHeight(20);

        VBox buttonBox = new VBox(8, categoryButtonBox, spacer, buyerButtonBox);

        VBox leftBox = new VBox(8);
        leftBox.getChildren().addAll(
                createTitle(),
                createStatementField(),
                switchToSummarySpending,
                switchToExportPane,
                buttonBox,
                exportTxtFile);

        mainPane.setLeft(leftBox);
        mainPane.setPadding(new Insets(20));
        mainPane.setCenter(createTransactionTable());
        handleAutoAssignCategories(categoriesList, buyerList);

        switchToSummarySpending.setOnAction(e -> switchToSummarySpendingPage(mainPane));
        switchToExportPane.setOnAction(e -> switchToExportPage(mainPane));
        addBuyer.setOnAction(e -> addBuyerFunction());
        addCategory.setOnAction(e -> addCategoryFunction());
        editCategoryButton.setOnAction(e -> handleEditCategory(categoriesList));
        deleteCategoryButton.setOnAction(e -> handleDeleteCategory(categoriesList));
        editBuyerButton.setOnAction(e -> handleEditBuyer(buyerList));
        deleteBuyerButton.setOnAction(e -> handleDeleteBuyer(buyerList));
        assignBuyerToCategoryButton.setOnAction(e -> handleAutoAssignBuyers(buyerList, categoryModels));
        exportTxtFile.setOnAction(e -> exportTXT.run());

        return mainPane;
    }

    // Contributing Authors: D Tran Gia Khanh, T Nguyen Thanh Khoi, Queszel Martin
    @SuppressWarnings("unchecked")
    private Node createExportPane() {
        BorderPane exportPane = new BorderPane();
        TableView<TransactionModel> table2 = new TableView<>();

        TableColumn<TransactionModel, String> dateColumn = new TableColumn<>("Date");
        TableColumn<TransactionModel, String> descColumn = new TableColumn<>("Description");
        TableColumn<TransactionModel, Double> debitColumn = new TableColumn<>("Debit");
        TableColumn<TransactionModel, Double> creditColumn = new TableColumn<>("Credit");
        TableColumn<TransactionModel, Double> balanceColumn = new TableColumn<>("Balance");
        TableColumn<TransactionModel, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<TransactionModel, String> buyerColumn = new TableColumn<>("Buyer");

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().transDateProperty());
        descColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        debitColumn.setCellValueFactory(cellData -> cellData.getValue().debitProperty().asObject());
        creditColumn.setCellValueFactory(cellData -> cellData.getValue().creditProperty().asObject());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        buyerColumn.setCellValueFactory(cellData -> cellData.getValue().buyerProperty());

        table2.getColumns().addAll(dateColumn, descColumn, debitColumn, creditColumn, balanceColumn, categoryColumn,
                buyerColumn);
        table2.setItems(transactionModels);
        table2.setEditable(false);

        Button exportFileToCSV = new Button("Export to CSV");
        Button exportFileToXLSX = new Button("Export to Excel");
        Button backToMenu = new Button("Back to Menu");
        exportFileToCSV.setOnAction(e -> {
            if (exportHandlerCSV != null)
                exportHandlerCSV.run();
            else
                System.err.println("CSV Export Handler is not set!");
        });

        exportFileToXLSX.setOnAction(e -> {
            if (exportHandlerXLSX != null)
                exportHandlerXLSX.run();
            else
                System.err.println("XLSX Export Handler is not set!");
        });

        backToMenu.setOnAction(e -> switchToMainPage(exportPane));

        VBox xlsxBox = new VBox(10);
        xlsxBox.setPadding(new Insets(20));
        xlsxBox.getChildren().clear();
        xlsxBox.getChildren().addAll(table2, exportFileToXLSX, exportFileToCSV);
        xlsxBox.setAlignment(Pos.CENTER);

        VBox leftSummaryBox = new VBox(10);
        leftSummaryBox.setPadding(new Insets(10));
        leftSummaryBox.setAlignment(Pos.CENTER);
        leftSummaryBox.getChildren().addAll(summarizedExportTables(), handleExportButton());

        VBox vb = new VBox(10);
        vb.setPadding(new Insets(20));
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(backToMenu, createInstructions());

        exportPane.setTop(vb);
        exportPane.setRight(xlsxBox);
        exportPane.setLeft(leftSummaryBox);

        BorderPane.setAlignment(vb, Pos.CENTER);
        BorderPane.setAlignment(xlsxBox, Pos.CENTER_RIGHT);
        BorderPane.setAlignment(leftSummaryBox, Pos.CENTER_LEFT);

        return exportPane;
    }

    // Contributing Authors: D Tran Gia Khanh, Queszel Martin
    private void switchToSummarySpendingPage(BorderPane mainPane) {
        StackPane sp = (StackPane) mainPane.getParent();
        sp.getChildren().remove(mainPane);

        BorderPane summaryPane = new BorderPane();
        summaryPane.setCenter(createSummaryTable(transactionModels));

        VBox leftBox = new VBox(8);
        leftBox.setPadding(new Insets(20));
        Button buttonToMain = new Button("Back to menu");
        Button exportSummaryButton = new Button("Export Spending Summary");

        buttonToMain.setOnAction(e -> {
            sp.getChildren().clear();
            sp.getChildren().add(mainPane);
        });

        exportSummaryButton.setOnAction(e -> exportSummary.run());
        Button moveToBalanceSheetButton = new Button("Move to Balance Sheet");
        moveToBalanceSheetButton.setOnAction(e -> {
            if (buyerList.isEmpty()) {
                showMessage("No Buyers", "No buyers exist to export balance sheet.", AlertType.WARNING);
                return;
            }

            transactionModels.forEach(transaction -> {
                String buyer = transaction.buyerProperty().get();

                if (!previousBalances.containsKey(buyer)) {
                    TextInputDialog balanceDialog = new TextInputDialog();
                    balanceDialog.setTitle("Enter Previous Balance");
                    balanceDialog.setHeaderText("Enter the previous balance for " + buyer);
                    balanceDialog.setContentText("Previous Balance:");

                    Optional<String> result = balanceDialog.showAndWait();

                    if (result.isPresent()) {
                        try {
                            double inputBalance = Double.parseDouble(result.get());
                            previousBalances.put(buyer, inputBalance);
                        } catch (NumberFormatException ex) {
                            showMessage("Invalid Input", "Please enter a valid number for the balance.",
                                    AlertType.ERROR);
                        }
                    }
                }
            });

            double totalBalance = previousBalances.values().stream().mapToDouble(Double::doubleValue).sum();
            previousBalances.put("Total", totalBalance);

            Stage balanceSheetStage = new Stage();
            balanceSheetStage.setTitle("Balance Sheet Preview");
            TableView<ExportPreviewRow> tableView = createExportPreviewTable(
                    TransactionModel.convertToModelList(transactionModels), previousBalances);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.getChildren().add(tableView);

            Button exportBalanceSheetButton = createExportBalanceSheetButton();
            layout.getChildren().add(exportBalanceSheetButton);

            Scene scene = new Scene(layout, 800, 600);
            balanceSheetStage.setScene(scene);
            balanceSheetStage.show();
        });

        leftBox.getChildren().addAll(buttonToMain, exportSummaryButton, moveToBalanceSheetButton);
        summaryPane.setLeft(leftBox);
        sp.getChildren().add(summaryPane);
    }

    // Contributing Authors: D Tran Gia Khanh
    private void switchToExportPage(BorderPane mainPane) {
        StackPane sp = (StackPane) mainPane.getParent();
        sp.getChildren().clear();
        sp.getChildren().add(createExportPane());
    }

    // Contributing Authors: Queszel Martin
    private void switchToMainPage(BorderPane exportPane) {
        StackPane sp = (StackPane) exportPane.getParent();
        sp.getChildren().clear();
        mainPane.setCenter(table);
        sp.getChildren().add(mainPane);
    }

    // Contributing Authors: T Nguyen Thanh Khoi, Queszel Martin
    @SuppressWarnings("unchecked")
    public TableView<ExportPreviewRow> createExportPreviewTable(
            ArrayList<Transaction> transactions, Map<String, Double> previousBalances) {
        TableView<ExportPreviewRow> tableView = new TableView<>();
        TableColumn<ExportPreviewRow, String> monthColumn = new TableColumn<>("Month");
        monthColumn.setCellValueFactory(row -> new SimpleStringProperty(row.getValue().getMonth()));
        tableView.getColumns().add(monthColumn);
        tableView.setEditable(true);

        LinkedHashSet<String> buyers = new LinkedHashSet<>(previousBalances.keySet());
        buyers.add("Total");

        for (String buyer : buyers) {
            TableColumn<ExportPreviewRow, String> buyerColumn = new TableColumn<>(
                    buyer + " (Prev Balance: " + previousBalances.getOrDefault(buyer, 0.0) + ")");

            TableColumn<ExportPreviewRow, Double> hadPaidColumn = new TableColumn<>("Had Paid");
            hadPaidColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getBuyerData(buyer)[0]));

            TableColumn<ExportPreviewRow, Double> purchasedColumn = new TableColumn<>("Purchased");
            purchasedColumn
                    .setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getBuyerData(buyer)[1]));

            TableColumn<ExportPreviewRow, Double> curRunColumn = new TableColumn<>("Cur Run");
            curRunColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getBuyerData(buyer)[2]));

            buyerColumn.getColumns().addAll(hadPaidColumn, purchasedColumn, curRunColumn);
            tableView.getColumns().add(buyerColumn);
        }

        ObservableList<ExportPreviewRow> data = FXCollections.observableArrayList();
        String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
        HashMap<String, Double> curRunMap = new HashMap<>(previousBalances);

        for (int i = 0; i < months.length; i++) {
            Map<String, Double[]> buyerData = new HashMap<>();
            double totalCredit = 0.0;
            double totalDebit = 0.0;
            double totalCurRun = 0.0;

            for (String buyer : buyers) {
                if (!"Total".equals(buyer)) {
                    double monthlyCredit = (calculateMonthlyTotal(transactions, buyer, i + 1, "credit") * 100.0)
                            / 100.0;
                    double monthlyDebit = (calculateMonthlyTotal(transactions, buyer, i + 1, "debit") * 100.0)
                            / 100.0;
                    double curRun = ((curRunMap.getOrDefault(buyer, 0.0) + monthlyDebit - monthlyCredit) * 100.0)
                            / 100.0;
                    curRunMap.put(buyer, curRun);
                    buyerData.put(buyer, new Double[] { monthlyCredit, monthlyDebit, curRun });

                    totalCredit += monthlyCredit;
                    totalDebit += monthlyDebit;
                    totalCurRun += curRun;
                }
            }

            buyerData.put("Total", new Double[] { totalCredit, totalDebit, totalCurRun });
            data.add(new ExportPreviewRow(months[i], buyerData));
        }

        String newBuyer = "Statement Transactions";
        if (!previousBalances.containsKey(newBuyer)) {
            TextInputDialog balanceDialog = new TextInputDialog();
            balanceDialog.setTitle("Enter Previous Balance");
            balanceDialog.setHeaderText("Enter the previous balance for " + newBuyer);
            balanceDialog.setContentText("Previous Balance:");

            Optional<String> result = balanceDialog.showAndWait();

            if (result.isPresent()) {
                try {
                    double inputBalance = Double.parseDouble(result.get());
                    previousBalances.put(newBuyer, inputBalance);
                } catch (NumberFormatException ex) {
                    showMessage("Invalid Input", "Please enter a valid number for the balance.",
                            AlertType.ERROR);
                }
            }
        }

        TableColumn<ExportPreviewRow, String> statementTransactionsColumn = new TableColumn<>(
                newBuyer + " (Prev Balance: " + previousBalances.getOrDefault(newBuyer, 0.0) + ")");

        TableColumn<ExportPreviewRow, Double> hadPaidStatementColumn = new TableColumn<>("Had Paid");
        hadPaidStatementColumn
                .setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getStatementData()[0]));
        hadPaidStatementColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        hadPaidStatementColumn.setOnEditCommit(event -> {
            Double newValue = event.getNewValue() != null ? event.getNewValue() : 0.0;
            event.getRowValue().getStatementData()[0] = newValue;
            computeTotalRun(tableView.getItems(), previousBalances.get("Statement Transactions"));
            tableView.refresh();
        });

        TableColumn<ExportPreviewRow, Double> purchasedStatementColumn = new TableColumn<>("Purchased");
        purchasedStatementColumn
                .setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getStatementData()[1]));
        purchasedStatementColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        purchasedStatementColumn.setOnEditCommit(event -> {
            Double newValue = event.getNewValue() != null ? event.getNewValue() : 0.0;
            event.getRowValue().getStatementData()[1] = newValue;
            computeTotalRun(tableView.getItems(), previousBalances.get("Statement Transactions"));
            tableView.refresh();
        });

        TableColumn<ExportPreviewRow, Double> interestColumn = new TableColumn<>("Interest");
        interestColumn.setCellValueFactory(row -> {
            double purchasedTotal = row.getValue().getBuyerData("Total")[1];
            double purchasedStatement = row.getValue().getStatementData()[1];
            return new SimpleObjectProperty<>(Math.max(0, purchasedTotal - purchasedStatement));
        });

        TableColumn<ExportPreviewRow, Double> purchaseWithIntColumn = new TableColumn<>("Purchase w Int");
        purchaseWithIntColumn.setCellValueFactory(row -> {
            double purchasedStatement = row.getValue().getStatementData()[1];
            double interest = Math.max(0, row.getValue().getBuyerData("Total")[1] - purchasedStatement);
            return new SimpleObjectProperty<>(purchasedStatement + interest);
        });

        TableColumn<ExportPreviewRow, Double> totalRunColumn = new TableColumn<>("Total Run");
        totalRunColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getTotalRun()));

        statementTransactionsColumn.getColumns().addAll(hadPaidStatementColumn, purchasedStatementColumn,
                interestColumn, purchaseWithIntColumn, totalRunColumn);

        tableView.getColumns().add(statementTransactionsColumn);

        Map<String, Double[]> totalBuyerData = new HashMap<>();
        double totalCreditSum = 0.0;
        double totalDebitSum = 0.0;
        for (String buyer : buyers) {
            if (!"Total".equals(buyer)) {
                double buyerTotalCredit = calculateTotal(transactions, buyer, "credit");
                double buyerTotalDebit = calculateTotal(transactions, buyer, "debit");
                totalCreditSum += buyerTotalCredit;
                totalDebitSum += buyerTotalDebit;

                totalBuyerData.put(buyer, new Double[] { buyerTotalCredit, buyerTotalDebit, null });
            }
        }

        totalBuyerData.put("Total", new Double[] { totalCreditSum, totalDebitSum, null });
        data.add(new ExportPreviewRow("Total", totalBuyerData));

        tableView.setRowFactory(tv -> {
            TableRow<ExportPreviewRow> row = new TableRow<>();
            row.setStyle("-fx-font-weight: bold");
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && "Total".equals(newItem.getMonth())) {
                    row.setStyle("-fx-font-weight: bold");
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });

        tableView.setItems(data);
        return tableView;
    }

    // Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi, Gen AI
    @SuppressWarnings({ "unchecked", "deprecation" })
    private Node createTransactionTable() {

        TableColumn<TransactionModel, String> dateColumn = new TableColumn<>("Date");
        TableColumn<TransactionModel, String> descColumn = new TableColumn<>("Description");
        TableColumn<TransactionModel, Double> debitColumn = new TableColumn<>("Debit");
        TableColumn<TransactionModel, Double> creditColumn = new TableColumn<>("Credit");
        TableColumn<TransactionModel, Double> balanceColumn = new TableColumn<>("Balance");
        TableColumn<TransactionModel, String> categoryColumn = new TableColumn<>("Category");
        TableColumn<TransactionModel, String> buyerColumn = new TableColumn<>("Buyer");

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().transDateProperty());
        descColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        debitColumn.setCellValueFactory(cellData -> cellData.getValue().debitProperty().asObject());
        creditColumn.setCellValueFactory(cellData -> cellData.getValue().creditProperty().asObject());
        balanceColumn.setCellValueFactory(cellData -> cellData.getValue().balanceProperty().asObject());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        buyerColumn.setCellValueFactory(cellData -> cellData.getValue().buyerProperty());

        categoryColumn.setCellFactory(column -> {
            return new TableCell<TransactionModel, String>() {
                private final ComboBox<String> comboBox = new ComboBox<>();
                {
                    comboBox.setItems(categoriesList);
                    comboBox.setEditable(false);

                    comboBox.setOnAction(e -> {
                        if (getTableRow() != null && getTableRow().getItem() != null) {
                            TransactionModel item = getTableRow().getItem();
                            item.setCategory(comboBox.getValue());
                            table.refresh();
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                    }
                }
            };
        });

        buyerColumn.setCellFactory(column -> {
            return new TableCell<TransactionModel, String>() {

                private final ComboBox<String> comboBox = new ComboBox<>();
                {
                    comboBox.setItems(buyerList);
                    comboBox.setEditable(false);
                    comboBox.setOnAction(e -> {
                        if (getTableRow() != null && getTableRow().getItem() != null) {
                            TransactionModel item = getTableRow().getItem();
                            item.setBuyer(comboBox.getValue());
                            table.refresh();
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                    }
                }

            };
        });

        table.getColumns().addAll(dateColumn, descColumn, debitColumn, creditColumn, balanceColumn, categoryColumn,
                buyerColumn);
        table.setItems(transactionModels);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    // Contributing Authors: Queszel Martin, GenAI
    @SuppressWarnings({ "deprecation", "unchecked" })
    private Node summarizedExportTables() {
        TableView<TransactionModel> tableEx = new TableView<>();

        TableColumn<TransactionModel, String> dateCol = new TableColumn<>("Date");
        TableColumn<TransactionModel, String> buyerColumn = new TableColumn<>("Buyer");
        TableColumn<TransactionModel, Double> amountColumn = new TableColumn<>("Amount");
        TableColumn<TransactionModel, String> categoryColumn = new TableColumn<>("Category");

        dateCol.setCellValueFactory(cellData -> cellData.getValue().transDateProperty());
        buyerColumn.setCellValueFactory(cellData -> cellData.getValue().buyerProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        tableEx.getColumns().addAll(dateCol, buyerColumn, amountColumn, categoryColumn);

        tableEx.setItems(transactionModels);
        tableEx.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableView<MonthlyTotalModel> monthlyTotalTable = new TableView<>();

        TableColumn<MonthlyTotalModel, String> monthColumn = new TableColumn<>("Month");
        TableColumn<MonthlyTotalModel, Double> totalColumn = new TableColumn<>("Total");

        monthColumn.setCellValueFactory(cellData -> cellData.getValue().monthProperty());
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());

        monthlyTotalTable.getColumns().addAll(monthColumn, totalColumn);

        Map<String, Double> monthlyTotals = new HashMap<>();
        for (TransactionModel transaction : transactionModels) {
            String dateStr = transaction.transDateProperty().get();
            LocalDate date = parseDate(dateStr);
            String month = date.getMonth().toString();
            double amount = transaction.valueProperty().get();
            monthlyTotals.put(month, monthlyTotals.getOrDefault(month, 0.0) + amount);
        }

        ObservableList<MonthlyTotalModel> monthlyTotalList = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : monthlyTotals.entrySet()) {
            BigDecimal roundedTotal = new BigDecimal(entry.getValue()).setScale(2, RoundingMode.HALF_UP);
            monthlyTotalList.add(new MonthlyTotalModel(entry.getKey(), roundedTotal.doubleValue()));
        }

        monthlyTotalTable.setItems(monthlyTotalList);
        monthlyTotalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        int rowCount = monthlyTotalList.size();
        monthlyTotalTable.setMinHeight((rowCount + 0.5) * 30);
        monthlyTotalTable.setMaxHeight((rowCount + 0.5) * 30);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(tableEx, monthlyTotalTable);
        vbox.setPadding(new Insets(10));

        return vbox;
    }

    // Contributing Authors: D Tran Gia Khanh, T Nguyen Thanh Khoi, Queszel Martin
    @SuppressWarnings("deprecation")
    public Node createSummaryTable(ObservableList<TransactionModel> transactionModels) {
        if (transactionModels == null || transactionModels.isEmpty()) {
            return new VBox();
        }

        ObservableList<String> uniqueCategories = FXCollections.observableArrayList();
        ObservableList<String> uniqueBuyers = FXCollections.observableArrayList();
        for (TransactionModel transaction : transactionModels) {
            String category = transaction.categoryProperty().get();
            String buyer = transaction.buyerProperty().get();

            if (category != null && !category.isEmpty() && !uniqueCategories.contains(category)) {
                uniqueCategories.add(category);
            }

            if (buyer != null && !buyer.isEmpty() && !uniqueBuyers.contains(buyer)) {
                uniqueBuyers.add(buyer);
            }
        }
        TableView<CategoryModel> tableSummary = new TableView<>();
        TableColumn<CategoryModel, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        ObservableList<TableColumn<CategoryModel, Double>> buyerColumns = FXCollections.observableArrayList();
        for (String buyer : uniqueBuyers) {
            TableColumn<CategoryModel, Double> buyerColumn = new TableColumn<>(buyer);
            buyerColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalForBuyerProperty(buyer).asObject());
            buyerColumns.add(buyerColumn);
        }

        TableColumn<CategoryModel, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCategory().asObject());

        tableSummary.getColumns().add(categoryColumn);
        tableSummary.getColumns().addAll(buyerColumns);
        tableSummary.getColumns().add(totalColumn);

        ObservableList<CategoryModel> data = FXCollections.observableArrayList();

        double grandTotal = 0.0;
        for (String category : uniqueCategories) {
            CategoryModel categorySummary = new CategoryModel(category, uniqueBuyers);

            for (String buyer : uniqueBuyers) {
                double total = 0;
                for (TransactionModel transaction : transactionModels) {
                    if (category.equals(transaction.categoryProperty().get())
                            && buyer.equals(transaction.buyerProperty().get())) {
                        total += transaction.totalProperty().get();
                    }
                }
                String formatTotal = String.format("%.2f", total);
                categorySummary.setTotalForBuyer(buyer, Double.parseDouble(formatTotal));
            }

            categorySummary.calculateTotal();
            grandTotal += categorySummary.getTotalCategory().get();
            grandTotal = Math.round(grandTotal * 100) / 100;
            data.add(categorySummary);
        }
        CategoryModel totalSummary = new CategoryModel("TOTAL SPENDING", uniqueBuyers);
        for (String buyer : uniqueBuyers) {
            double totalForBuyer = 0;
            for (CategoryModel categorySummary : data) {
                totalForBuyer += categorySummary.getBuyerTotal(buyer);
            }
            String formatTotal = String.format("%.2f", totalForBuyer);
            totalSummary.setTotalForBuyer(buyer, Double.parseDouble(formatTotal));
        }
        totalSummary.calculateTotal();
        data.add(totalSummary);

        tableSummary.setItems(data);
        tableSummary.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox vBox = new VBox(tableSummary);
        return vBox;
    }

    // Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi, GenAI
    private Button createExportBalanceSheetButton() {
        Button exportBalanceSheetButton = new Button("Export Balance Sheet");

        exportBalanceSheetButton.setOnAction(e -> {
            if (buyerList.isEmpty()) {
                showMessage("No Buyers", "No buyers exist to export balance sheet.", AlertType.WARNING);
                return;
            }
            transactionModels.forEach(transaction -> {
                String buyer = transaction.buyerProperty().get();

                if (!previousBalances.containsKey(buyer)) {
                    TextInputDialog balanceDialog = new TextInputDialog();
                    balanceDialog.setTitle("Enter Previous Balance");
                    balanceDialog.setHeaderText("Enter the previous balance for " + buyer);
                    balanceDialog.setContentText("Previous Balance:");

                    Optional<String> result = balanceDialog.showAndWait();

                    if (result.isPresent()) {
                        try {
                            double inputBalance = Double.parseDouble(result.get());
                            previousBalances.put(buyer, inputBalance);
                        } catch (NumberFormatException ex) {
                            showMessage("Invalid Input", "Please enter a valid number for the balance.",
                                    AlertType.ERROR);
                        }
                    }
                }
            });

            double totalBalance = previousBalances.values().stream().mapToDouble(Double::doubleValue).sum();
            previousBalances.put("Total", totalBalance);

            Stage stage = (Stage) exportBalanceSheetButton.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Balance Sheet");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XLSX Files", "*.xlsx");
            fileChooser.getExtensionFilters().add(filter);

            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile != null) {
                try {
                    ArrayList<Transaction> transactions = TransactionModel.convertToTransactionList(transactionModels);

                    Exporter.exportMultiBuyerBalanceSheet(transactions, previousBalances,
                            selectedFile.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                showMessage("No file selected", "Please select a valid file to save the balance sheet.",
                        AlertType.WARNING);
            }
        });

        return exportBalanceSheetButton;
    }

    // Contributing Authors: Queszel Martin
    public Button handleExportButton() {
        Button exportButton = new Button("Export Summary By Month");

        exportButton.setOnAction(e -> {
            Stage stage = (Stage) exportButton.getScene().getWindow();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Export File");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            ExtensionFilter filter2 = new ExtensionFilter("XLSX Files", "*.xlsx");
            fileChooser.getExtensionFilters().addAll(filter2);

            File selectedFile = fileChooser.showSaveDialog(stage);

            if (selectedFile == null) {
                showMessage("No file selected", "Please select a valid location to save the file.", AlertType.WARNING);
                return;
            }

            String filePath = selectedFile.getPath();
            Exporter export = new Exporter();

            if (!filePath.endsWith(".xlsx") && !filePath.endsWith(".csv")) {
                showMessage("Unsupported file type", "Only CSV or XLSX files are supported. Please try again.",
                        AlertType.ERROR);
                return;
            }

            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) {
                    showMessage("Error", "Failed to delete the existing file.", AlertType.ERROR);
                    return;
                }
            }

            if (filePath.endsWith(".xlsx")) {
                export.exportSummaryToXLSXDynamic(TransactionModel.convertToTransactionList(transactionModels),
                        filePath);
                export.addSheetsForMonthlyTransactions(filePath,
                        TransactionModel.convertToTransactionList(transactionModels));
            } else if (filePath.endsWith(".csv")) {
                export.exportSummaryToCSVDynamic(TransactionModel.convertToTransactionList(transactionModels),
                        filePath);
            }
        });

        return exportButton;
    }

    // Contributing Authors: Queszel Martin
    private Node createStatementField() {
        TextField results = new TextField();
        results.setEditable(false);
        results.setPrefWidth(200);
        results.textProperty().bind(filePathProperty);
        return results;
    }

    // Contributing Authors: Queszel Martin
    private Node createTitle() {
        Label label = new Label("File: ");
        return label;
    }

    // Contributing Authors: Queszel Martin
    private Node createToolBar() {
        ToolBar toolBar = new ToolBar();
        MenuBar bar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As...");

        openItem.setOnAction(e -> browseHandler.run());
        fileMenu.getItems().addAll(openItem, saveItem, saveAsItem);

        bar.getMenus().add(fileMenu);
        toolBar.getItems().add(bar);

        return toolBar;
    }

    // Contributing Authors: Queszel Martin
    public Node createInstructions() {
        Label descriptionLabel = new Label(
                "You can generate a normal transaction list using 'Export to Excel' or 'Export to CSV', or select 'Export Summary' to generate a monthly-transactions list.");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(400);
        return descriptionLabel;
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void handleEditCategory(ObservableList<String> categoriesList) {
        Stage editCategoryStage = new Stage();
        editCategoryStage.initModality(Modality.APPLICATION_MODAL);
        editCategoryStage.setTitle("Edit Category");
        editCategoryStage.setResizable(false);

        Label selectCategoryLabel = new Label("Select a category to edit:");
        ComboBox<String> categorySelectionBox = new ComboBox<>();
        categorySelectionBox.getItems().addAll(categoriesList);
        categorySelectionBox.setValue("Select Category");

        Label newNameLabel = new Label("Enter the new name:");
        TextField newNameField = new TextField();
        newNameField.setPromptText("New category name");

        Button confirmButton = new Button("CONFIRM");
        confirmButton.setOnAction(e -> {
            String selectedCategory = categorySelectionBox.getValue();
            String newCategoryName = newNameField.getText().toLowerCase();

            if ("Select Category".equals(selectedCategory) || selectedCategory == null) {
                showMessage("No Category Selected", "Please select a category to edit.", AlertType.WARNING);
            } else if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
                showMessage("Invalid Input", "Please enter a valid new name for the category.", AlertType.WARNING);
            } else {
                if (categoriesList.contains(newCategoryName)) {
                    showMessage("Duplicate Category", "The category name already exists. Please enter a new name.",
                            AlertType.WARNING);
                    return;
                } else {
                    categoriesList.remove(selectedCategory);
                    categoriesList.add(newCategoryName);
                    categorySelectionBox.setItems(categoriesList);
                    categorySelectionBox.setValue(newCategoryName);

                    for (TransactionModel transaction : transactionModels) {
                        if (selectedCategory.equals(transaction.categoryProperty().get())) {
                            transaction.setCategory(newCategoryName);
                        }
                    }
                    editCategoryStage.close();
                }
            }
        });

        newNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(selectCategoryLabel,
                categorySelectionBox,
                newNameLabel,
                newNameField,
                confirmButton);

        Scene scene = new Scene(layout, 300, 200);
        editCategoryStage.setScene(scene);
        editCategoryStage.showAndWait();
        table.refresh();
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void handleDeleteCategory(ObservableList<String> categoriesList) {
        Stage deleteCategoryStage = new Stage();
        deleteCategoryStage.initModality(Modality.APPLICATION_MODAL);
        deleteCategoryStage.setTitle("Delete Category");
        deleteCategoryStage.setResizable(false);

        Label selectCategoryLabel = new Label("Select a category to delete:");
        ComboBox<String> categorySelectionBox = new ComboBox<>();
        categorySelectionBox.getItems().addAll(categoriesList);
        categorySelectionBox.setPromptText("Select Category");

        Button confirmButton = new Button("DELETE");
        confirmButton.setOnAction(e -> {
            String selectedCategory = categorySelectionBox.getValue();

            if (selectedCategory == null) {
                showMessage("No Category Selected", "Please select a category to delete.", AlertType.WARNING);
            } else {
                categoriesList.remove(selectedCategory);
                categorySelectionBox.getSelectionModel().clearSelection();
                for (TransactionModel transaction : transactionModels) {
                    if (selectedCategory.equals(transaction.categoryProperty().get())) {
                        transaction.setCategory("");
                    }
                }
                deleteCategoryStage.close();
            }
        });

        categorySelectionBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(selectCategoryLabel, categorySelectionBox, confirmButton);

        Scene scene = new Scene(layout, 300, 150);
        deleteCategoryStage.setScene(scene);
        deleteCategoryStage.showAndWait();
        table.refresh();
    }

    // Contributing Authors: Queszel Martin
    private void handleAutoAssignCategories(ObservableList<String> categoryList, ObservableList<String> buyerList) {
        Map<String, String> keywordCategoryMap = new HashMap<>();
        // bills
        keywordCategoryMap.put("bell", "bills");
        keywordCategoryMap.put("bill", "bills");
        keywordCategoryMap.put("bills", "bills");
        keywordCategoryMap.put("payment", "bills");
        keywordCategoryMap.put("rent", "bills");
        keywordCategoryMap.put("electricity", "bills");
        // car
        keywordCategoryMap.put("carcare", "carcare");
        // gas
        keywordCategoryMap.put("circle k", "gas");
        keywordCategoryMap.put("fuel", "gas");
        keywordCategoryMap.put("gas", "gas");
        keywordCategoryMap.put("petro", "gas");
        // clothes
        keywordCategoryMap.put("clothes", "clothes");
        // groceries
        keywordCategoryMap.put("foods", "groceries");
        keywordCategoryMap.put("grocery", "groceries");
        keywordCategoryMap.put("sobeys", "groceries");
        keywordCategoryMap.put("superstore", "groceries");
        keywordCategoryMap.put("baker", "groceries");
        // household
        keywordCategoryMap.put("pet", "household");
        keywordCategoryMap.put("household", "household");
        // leisure
        keywordCategoryMap.put("diner", "leisure");
        keywordCategoryMap.put("leisure", "leisure");
        keywordCategoryMap.put("coffee", "leisure");
        keywordCategoryMap.put("dining", "leisure");
        // pharmacy/medicine
        keywordCategoryMap.put("pharmacy", "pharmacy");
        // misc
        keywordCategoryMap.put("miscelaneous", "miscelaneous");

        Category manager = new Category();
        manager.autoAssignCategories(transactionModels, categoryModels, keywordCategoryMap);

        refreshTransactionTable();
        refreshSummaryTable();
    }

    // Contributing Authors: Queszel Martin
    private void handleAutoAssignBuyers(ObservableList<String> buyerList,
            ObservableList<CategoryModel> categoryModels) {

        Stage autoAssignStage = new Stage();
        autoAssignStage.initModality(Modality.APPLICATION_MODAL);
        autoAssignStage.setTitle("Assign Buyer to Category");
        autoAssignStage.setResizable(false);

        Label selectBuyerLabel = new Label("Select a buyer:");
        ComboBox<String> buyerSelectionBox = new ComboBox<>(buyerList);
        buyerSelectionBox.setPromptText("Select Buyer");

        Label selectCategoryLabel = new Label("Select a category:");
        ComboBox<String> categorySelectionBox = new ComboBox<>();
        categorySelectionBox.setPromptText("Select Category");
        categorySelectionBox.setItems(FXCollections.observableArrayList(categoriesList));

        Button confirmButton = new Button("Assign");
        confirmButton.setOnAction(e -> {
            String selectedBuyer = buyerSelectionBox.getValue();
            String selectedCategory = categorySelectionBox.getValue();

            if (selectedBuyer == null || selectedCategory == null) {
                showMessage("Invalid Selection", "Please select both a buyer and a category.", AlertType.WARNING);
                return;
            }

            for (TransactionModel transaction : transactionModels) {
                if (transaction.categoryProperty().get().equals(selectedCategory)) {
                    transaction.setBuyer(selectedBuyer);
                }
            }

            for (CategoryModel categoryModel : categoryModels) {
                if (categoryModel.getCategory().equals(selectedCategory)) {
                    double totalAmount = 0;
                    for (TransactionModel transaction : transactionModels) {
                        if (transaction.categoryProperty().get().equals(selectedCategory)
                                && transaction.buyerProperty().get().equals(selectedBuyer)) {
                            totalAmount += transaction.valueProperty().get();
                        }
                    }
                    categoryModel.setTotalForBuyer(selectedBuyer, totalAmount);
                }
            }

            refreshTransactionTable();
            refreshSummaryTable();

        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(selectBuyerLabel, buyerSelectionBox, selectCategoryLabel, categorySelectionBox,
                confirmButton);

        Scene scene = new Scene(layout, 300, 200);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });
        autoAssignStage.setScene(scene);
        autoAssignStage.showAndWait();
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void handleEditBuyer(ObservableList<String> buyerListIn) {
        Stage editBuyerStage = new Stage();
        editBuyerStage.initModality(Modality.APPLICATION_MODAL);
        editBuyerStage.setTitle("Edit Buyer");
        editBuyerStage.setResizable(false);

        Label selectBuyerLabel = new Label("Select a buyer to edit:");
        ComboBox<String> buyerSelectionBox = new ComboBox<>(buyerListIn);
        buyerSelectionBox.setPromptText("Select Buyer");

        Label newNameLabel = new Label("Enter the new name:");
        TextField newNameField = new TextField();
        newNameField.setPromptText("New buyer name");

        Button confirmButton = new Button("CONFIRM");
        confirmButton.setOnAction(e -> {
            String selectedBuyer = buyerSelectionBox.getValue();
            String newBuyerName = newNameField.getText().trim().toUpperCase();

            if (selectedBuyer == null) {
                showMessage("No Buyer Selected", "Please select a buyer to edit.", AlertType.WARNING);
            } else if (newBuyerName.isEmpty()) {
                showMessage("Invalid Input", "Please enter a valid new name for the buyer.", AlertType.WARNING);
            } else if (buyerListIn.contains(newBuyerName)) {
                showMessage("Duplicate Name", "The new name already exists. Please use a different name.",
                        AlertType.WARNING);
            } else {
                int index = buyerListIn.indexOf(selectedBuyer);
                if (index != -1) {
                    buyerListIn.set(index, newBuyerName);
                    for (TransactionModel transaction : transactionModels) {
                        if (selectedBuyer.equals(transaction.buyerProperty().get())) {
                            transaction.setBuyer(newBuyerName);
                        }
                    }
                    editBuyerStage.close();
                }
            }
        });

        newNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(selectBuyerLabel, buyerSelectionBox, newNameLabel, newNameField, confirmButton);

        Scene scene = new Scene(layout, 300, 200);
        editBuyerStage.setScene(scene);
        editBuyerStage.showAndWait();
        table.refresh();
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void handleDeleteBuyer(ObservableList<String> buyerList) {
        Stage deleteBuyerStage = new Stage();
        deleteBuyerStage.initModality(Modality.APPLICATION_MODAL);
        deleteBuyerStage.setTitle("Delete Buyer");
        deleteBuyerStage.setResizable(false);

        Label selectBuyerLabel = new Label("Select a buyer to delete:");
        ComboBox<String> buyerSelectionBox = new ComboBox<>(buyerList);
        buyerSelectionBox.setPromptText("Select Buyer");

        Button confirmButton = new Button("DELETE");
        confirmButton.setOnAction(e -> {
            String selectedBuyer = buyerSelectionBox.getValue();

            if (selectedBuyer == null) {
                showMessage("No Buyer Selected", "Please select a buyer to delete.", AlertType.WARNING);
            } else {
                buyerList.remove(selectedBuyer);
                buyerSelectionBox.getSelectionModel().clearSelection();
                for (TransactionModel transaction : transactionModels) {
                    if (selectedBuyer.equals(transaction.buyerProperty().get())) {
                        transaction.setBuyer("");
                    }
                }
                deleteBuyerStage.close();
            }
        });

        buyerSelectionBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(selectBuyerLabel, buyerSelectionBox, confirmButton);

        Scene scene = new Scene(layout, 300, 150);
        deleteBuyerStage.setScene(scene);
        deleteBuyerStage.showAndWait();
        table.refresh();
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void addCategoryFunction() {
        Stage popUpAddCategory = new Stage();
        popUpAddCategory.initModality(Modality.APPLICATION_MODAL);
        popUpAddCategory.setTitle("Add Category");

        Label label = new Label("Please input the name of the category:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter category name");

        Label feedbackLabel = new Label();

        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setOnAction(e -> {
            String categoryName = nameField.getText().trim().toLowerCase();
            if (categoryName.isEmpty()) {
                feedbackLabel.setText("Category name cannot be empty. Please try again.");
            } else if (categoriesList.contains(categoryName)) {
                feedbackLabel.setText("Category already exists. Please enter a new name.");
            } else if (categoryName.contains(" ")) {
                feedbackLabel.setText("Category name cannot contain spaces. Please try again.");
            } else {
                categoriesList.add(categoryName);
                popUpAddCategory.close();
            }
        });

        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addCategoryButton.fire();
            }
        });

        VBox popupLayout = new VBox(10, label, nameField, addCategoryButton, feedbackLabel);
        popupLayout.setAlignment(Pos.CENTER);

        Scene popupCategoryScene = new Scene(popupLayout, 350, 250);
        popUpAddCategory.setScene(popupCategoryScene);
        popUpAddCategory.showAndWait();
    }

    // Contributing Authors: T Nguyen Thanh Khoi, D Tran Gia Khanh
    private void addBuyerFunction() {
        Stage popUpAddBuyer = new Stage();
        popUpAddBuyer.initModality(Modality.APPLICATION_MODAL);
        popUpAddBuyer.setTitle("Add Buyers");

        Label label = new Label("Please input the name of the buyer:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter buyer name");

        Label feedbackLabel = new Label();

        Button addBuyerButton = new Button("Add Buyer");
        addBuyerButton.setOnAction(e -> {
            String buyerName = nameField.getText().trim().toUpperCase();

            if (buyerName.isEmpty()) {
                feedbackLabel.setText("Buyer name cannot be empty. Please try again.");
            } else if (buyerList.contains(buyerName)) {
                feedbackLabel.setText("Buyer already exists. Please enter a new name.");
            } else {
                buyerList.add(buyerName);
                popUpAddBuyer.close();
            }
        });

        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addBuyerButton.fire();
            }
        });

        VBox popupLayout = new VBox(10, label, nameField, addBuyerButton, feedbackLabel);
        popupLayout.setAlignment(Pos.CENTER);

        Scene popupBuyerScene = new Scene(popupLayout, 350, 250);
        popUpAddBuyer.setScene(popupBuyerScene);
        popUpAddBuyer.showAndWait();
    }

    // Contributing Authors: Queszel Martin
    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter csvFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter xlsxFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            return LocalDate.parse(dateStr, csvFormat);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr, xlsxFormat);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid date format: " + dateStr, ex);
            }
        }
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    private void showMessage(String title, String message, AlertType alertType) {
        Alert a = new Alert(alertType);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // Contributing Authors: Queszel Martin
    private void refreshTransactionTable() {
        if (table != null) {
            table.refresh();
        }
    }

    // Contributing Authors: Queszel Martin
    private void refreshSummaryTable() {
        if (tableSummary != null) {
            tableSummary.refresh();
        }
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    private static double calculateTotal(ArrayList<Transaction> transactions, String buyer, String type) {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getBuyer().equals(buyer)) {
                if ("credit".equalsIgnoreCase(type)) {
                    total += transaction.getCredit();
                } else if ("debit".equalsIgnoreCase(type)) {
                    total += transaction.getDebit();
                }
            }
        }
        return total;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    private static double calculateMonthlyTotal(
            ArrayList<Transaction> transactions,
            String buyer, int month, String type) {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getBuyer().equals(buyer) && transaction.getMonthDate() == month) {
                if ("credit".equalsIgnoreCase(type)) {
                    total += (transaction.getCredit() * 100.0) / 100.0;
                } else if ("debit".equalsIgnoreCase(type)) {
                    total += (transaction.getDebit() * 100.0) / 100.0;
                }
            }
        }
        return total;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    private void computeTotalRun(ObservableList<ExportPreviewRow> rows, double previousBalance) {
        double runningTotal = previousBalance;

        for (ExportPreviewRow row : rows) {
            double purchased = row.getStatementData()[1];
            double hadPaid = row.getStatementData()[0];
            runningTotal = runningTotal + purchased - hadPaid;
            row.setTotalRun(runningTotal);
        }
    }
}