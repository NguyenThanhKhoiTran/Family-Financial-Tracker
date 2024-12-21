import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

//Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
public class FTTController {
    private ObservableList<TransactionModel> transactionModels;
    private FTTViewBuilder builder;
    private Stage stage;
    private StringProperty filePathProperty;

    // Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
    public FTTController(ObservableList<TransactionModel> transactionModelsIn, Stage stageIn) {
        this.transactionModels = transactionModelsIn;
        this.stage = stageIn;
        this.filePathProperty = new SimpleStringProperty("No file selected");
        this.builder = new FTTViewBuilder(transactionModelsIn,
                this::handleBrowseButton,
                filePathProperty,
                () -> exportFile(true),
                () -> exportFile(false),
                () -> exportTransactionsSummary(transactionModelsIn),
                () -> exportTXT(transactionModelsIn));
        ;
    }

    // Contributing Authors: Queszel Martin
    public Scene createScene() {
        return new Scene(builder.build(), 1000, 700);
    }

    // Contributing Authors: Queszel Martin
    public void handleBrowseButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        ExtensionFilter filter1 = new ExtensionFilter("CSV Files", "*.csv");
        ExtensionFilter filter2 = new ExtensionFilter("XLSX Files", "*.xlsx");
        fileChooser.getExtensionFilters().addAll(filter1, filter2);

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile == null) {
            showMessage("No file selected", "Please select a valid CSV or XLSX file.", AlertType.WARNING);
            return;
        }

        String filePath = selectedFile.getPath();
        if (!(filePath.endsWith(".csv") || filePath.endsWith(".xlsx"))) {
            showMessage("Unsupported file type", "Only CSV or XLSX files are supported. Please try again.",
                    AlertType.ERROR);
            return;
        }

        filePathProperty.set(filePath);

        try {
            ArrayList<Transaction> dataList = processFile(filePath);
            ArrayList<TransactionModel> modelList = TransactionModel.convertToModelList(dataList);
            transactionModels.setAll(modelList);
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("File Processing Error", "An error occurred while processing the file. Please try again.",
                    AlertType.ERROR);
        }
    }

    // Contributing Authors: Queszel Martin
    private void showMessage(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Contributing Authors: Queszel Martin
    private ArrayList<Transaction> processFile(String filePath) throws Exception {
        String fileType = Reader.determineFileType(filePath);
        if (fileType.equals("EXCEL")) {
            return Reader.readExcelFile(filePath);
        } else if (fileType.equals("CSV")) {
            return Reader.readCSVFile(filePath);
        } else {
            throw new NullPointerException();
        }
    }

    // Contributing Authors: Queszel Martin
    public String getFilePath() {
        return filePathProperty.get();
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    private void exportFile(boolean isCSV) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(isCSV ? "Save as CSV" : "Save as XLSX");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(isCSV ? "CSV files (*.csv)" : "Excel files (*.xlsx)",
                            isCSV ? "*.csv" : "*.xlsx"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                String filePath = file.getAbsolutePath();

                if (isCSV && !filePath.endsWith(".csv")) {
                    filePath += ".csv";
                } else if (!isCSV && !filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                Exporter exporter = new Exporter();
                List<Transaction> transactions = TransactionModel.convertToTransactionList(transactionModels);

                if (isCSV) {
                    exporter.exportToCSV(transactions, filePath);
                } else {
                    exporter.exportToXLSX(transactions, filePath);
                }

                showMessage("Export Successful", "File saved successfully: " + filePath, AlertType.INFORMATION);
            }
        } catch (Exception ex) {
            showMessage("Export Error", "An error occurred while exporting the file: " + ex.getMessage(),
                    AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void exportTransactionsSummary(ObservableList<TransactionModel> transactions) {
        Exporter e = new Exporter();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            List<Transaction> transactionsList = TransactionModel.convertToTransactionList(transactions);
            e.exportSummaryToXLSXDynamic(transactionsList, filePath);
        } else {
            System.out.println("Export cancelled.");
        }
    }

    // Contributing Authors: D Tran Gia Khanh, T Nguyen Thanh Khoi
    public void exportTXT(ObservableList<TransactionModel> transactionModelsIn) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            Exporter.exportTxtFileListingCategoryFunction(transactionModelsIn, filePath);
        } else {
            System.out.println("Export cancelled.");
        }
    }
}