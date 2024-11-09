import org.junit.Test;

public class ApproximationTest {

    @Test
    public void test1() {
        int[] testSupplies = {
                25, 35, 40
        };

        int[] testDemands = {
                20, 30, 25, 25
        };

        double[][] testCosts = {
                {5, 8, 6, 7},
                {6, 4, 3, 5},
                {7, 3, 4, 2}
        };
        Main.execute(testSupplies, testDemands, testCosts);
    }

    @Test
    public void test2() {
        int[] testSupplies = {
                10, 50, 40
        };

        int[] testDemands = {
                30, 20, 40, 10
        };

        double[][] testCosts = {
                {7, 4, 8, 6},
                {5, 6, 7, 3},
                {9, 5, 4, 8}
        };
        Main.execute(testSupplies, testDemands, testCosts);
    }

    @Test
    public void test3() {
        int[] testSupplies = {
                30, 20, 50
        };

        int[] testDemands = {
                20, 30, 25, 25
        };

        double[][] testCosts = {
                {6, 9, 5, 7},
                {8, 6, 7, 5},
                {5, 8, 4, 6}
        };
        Main.execute(testSupplies, testDemands, testCosts);
    }

}
