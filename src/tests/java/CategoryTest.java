import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CategoryTest 
{

    @Test
    public void testDefaultCategories() {
        Category category = new Category();
        List<String> categories = category.getCategories();

        assertEquals(10, categories.size());
        assertTrue(categories.contains("Groceries"));
        assertTrue(categories.contains("Gas"));
        assertTrue(categories.contains("Miscelaneous"));
    }

    @Test
    public void testAddCategory() {
        Category category = new Category();

        category.addCategory("Electronics");
        

        category.addCategory("Electronics"); 
        assertEquals(11, category.getCategories().size());
    }

    @Test
    public void testAddCategoryWithAmount() {
        Category category = new Category();

        category.addCategory("Travel", 250.0);
        assertTrue(category.getCategories().contains("Travel"));
       
    }


}
