import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//Contributing Authors: Queszel Martin, T Nguyen Thanh Khoi
public class Category {

    private HashMap<String, Double> categoryWithPrices;

    public Category() {
        categoryWithPrices = new HashMap<>();
        categoryWithPrices.put("Groceries", 0.0);
        categoryWithPrices.put("Gas", 0.0);
        categoryWithPrices.put("Entertainment", 0.0);
        categoryWithPrices.put("Pharmacy", 0.0);
        categoryWithPrices.put("Dining", 0.0);
        categoryWithPrices.put("Household", 0.0);
        categoryWithPrices.put("Leisure", 0.0);
        categoryWithPrices.put("Bills", 0.0);
        categoryWithPrices.put("Pets", 0.0);
        categoryWithPrices.put("Miscelaneous", 0.0);
    }

    // Contributing Authors: Queszel Martin
    public void addTransaction(Transaction transactionIn) {
        if (categoryWithPrices.containsKey(transactionIn.getCategory())) {
            double currentPrice = categoryWithPrices.get(transactionIn.getCategory());
            double updatedPrice = currentPrice + transactionIn.getDebit() - transactionIn.getCredit();
            categoryWithPrices.put(transactionIn.getCategory(), updatedPrice);
        } else {
            System.out.println("Category: " + transactionIn.getCategory()
                    + " is not in our HashMap, please use the addCategory method if you wish to resolve this.");
        }
    }

    // Contributing Authors: Queszel Martin
    public void addCategory(String newCategory) {
        if (!categoryWithPrices.containsKey(newCategory)) {
            categoryWithPrices.put(newCategory, 0.0);
        } else {
            System.out.println("Category: " + newCategory + " already exists.");
        }
    }

    // Contributing Authors: Queszel Martin
    public void addCategory(String newCategory, double owedInNewCategory) {
        categoryWithPrices.put(newCategory, owedInNewCategory);
    }

    // Contributing Authors: Queszel Martin
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Category List:\n");
        for (String category : categoryWithPrices.keySet()) {
            builder.append(String.format("%s: %.2f\n", category, categoryWithPrices.get(category)));
        }
        return builder.toString();
    }

    // Contributing Authors: T Nguyen Thanh Khoi
    public List<String> getCategories() {
        return new ArrayList<>(categoryWithPrices.keySet());
    }
    // Contributing Authors: Queszel Martin
    public void autoAssignCategories(ObservableList<TransactionModel> transactionModels, ObservableList<CategoryModel> categoryModels, Map<String, String> categoryMap) {
        for (TransactionModel t : transactionModels) {

            String category = t.categoryProperty().get();
            if (category == null || category.isEmpty()) {
                String transactionDesc = t.descriptionProperty().get().toLowerCase();
                boolean hasMatch = false;
    
                for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
                    String keyword = entry.getKey().toLowerCase();
                    String categoryName = entry.getValue();
    
                    if (transactionDesc.contains(keyword)) {
                        t.setCategory(categoryName);
                        hasMatch = true;
    
                        for (CategoryModel categoryModel : categoryModels) {
                            if (categoryModel.getCategory().equals(categoryName)) {
                                String buyer = t.buyerProperty().get();
                                double amount = t.valueProperty().get();
                                if (buyer != null && !buyer.isEmpty()) {
                                    categoryModel.setTotalForBuyer(buyer, categoryModel.getBuyerTotal(buyer) + amount);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
    
                if (!hasMatch) {
                    String miscCategory = "miscelaneous";
                    t.setCategory(miscCategory);
                    boolean miscExists = false;
    
                    for (CategoryModel categoryModel : categoryModels) {
                        if (categoryModel.getCategory().equals(miscCategory)) {
                            String buyer = t.buyerProperty().get();
                            double amount = t.valueProperty().get();
                            if (buyer != null && !buyer.isEmpty()) {
                                categoryModel.setTotalForBuyer(buyer, categoryModel.getBuyerTotal(buyer) + amount);
                            }
                            miscExists = true;
                            break;
                        }
                    }
    
                    if (!miscExists) {
                        CategoryModel miscCategoryModel = new CategoryModel(miscCategory, FXCollections.observableArrayList(t.buyerProperty().get()));
                        miscCategoryModel.setTotalForBuyer(t.buyerProperty().get(), t.valueProperty().get());
                        categoryModels.add(miscCategoryModel);
                    }
                }
            }
        }
    }
    
}