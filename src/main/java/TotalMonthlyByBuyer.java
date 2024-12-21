import java.util.List;

// Contributing authors: D Tran Gia Khanh
public class TotalMonthlyByBuyer {

    // Contributing authors: D Tran Gia Khanh
    public static double calculateTotalMonthly(List<Transaction> dataListIn, String buyerIn, boolean calDebit) {
        double totalMonthly = 0.0;
        if (calDebit == true) {
            for (Transaction data : dataListIn) {
                if (data.getBuyer().equals(buyerIn)) {
                    totalMonthly += data.getDebit();
                }
            }
        } else {
            for (Transaction data : dataListIn) {
                if (data.getBuyer().equals(buyerIn)) {
                    totalMonthly += data.getCredit();
                }
            }
        }
        return totalMonthly;
    }
}