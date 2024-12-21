import java.util.Map;

// Contributing Authors: T Nguyen Thanh Khoi
public class ExportPreviewRow {
    private final String month;
    private final Map<String, Double[]> buyerData;
    private double statementPreviousBalance;
    private double totalRun;
    private final Double[] statementData;

    // Contributing Authors: T Nguyen Thanh Khoi
    public ExportPreviewRow(String month, Map<String, Double[]> buyerData) {
        this.month = month;
        this.buyerData = buyerData;
        this.statementPreviousBalance = 0.0;
        this.statementData = new Double[] { 0.0, 0.0 };
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public String getMonth() {
        return month;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public Double[] getBuyerData(String buyer) {
        return buyerData.get(buyer);
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public double getStatementPreviousBalance() {
        return statementPreviousBalance;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setStatementPreviousBalance(double statementPreviousBalance) {
        this.statementPreviousBalance = statementPreviousBalance;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public Double[] getStatementData() {
        return statementData;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setHadPaid(double hadPaid) {
        this.statementData[0] = hadPaid;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setPurchased(double purchased) {
        this.statementData[1] = purchased;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public double getTotalRun() {
        return totalRun;
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public void setTotalRun(double totalRun) {
        this.totalRun = totalRun;
    }
}
