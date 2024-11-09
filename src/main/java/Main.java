import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter supplies in a single line, separated by spaces:");
        String line = scanner.nextLine();
        String[] suppliesString = line.split(" ");
        int[] supplies = Arrays.stream(suppliesString).mapToInt(Integer::parseInt).toArray();

        System.out.println("Enter demands in a single line, separated by spaces:");
        line = scanner.nextLine();
        String[] demandsString = line.split(" ");
        int[] demands = Arrays.stream(demandsString).mapToInt(Integer::parseInt).toArray();


        double[][] costs = new double[supplies.length][demands.length];
        System.out.println("Enter costs. Each row should contain " + demands.length + " elements.");
        for (int i = 0; i < supplies.length; i++) {
            System.out.println("Enter costs for row " + (i + 1) + ":");
            line = scanner.nextLine();
            String[] costString = line.split(" ");
            if (costString.length != demands.length) {
                System.out.println("Error: the number of elements in the row does not match the number of demands.");
                return;
            }
            for (int j = 0; j < demands.length; j++) {
                costs[i][j] = Double.parseDouble(costString[j]);
            }
        }

        execute(supplies, demands, costs);
    }

    public static void execute(int[] S, int[] D, double[][] C) {

            if (!isBalanced(S, D)) {
                System.out.println("The problem is not balanced!");
                return;
            }

            printTransportationTable(C, S, D);

            // Northwest Corner Method
            double[][] allocationNW = northwestCornerMethod(S.clone(), D.clone());
            System.out.println("Initial basic feasible solution using the Northwest Corner Method:");
            for (double[] row : allocationNW) {
                for (double val : row) {
                    System.out.print(String.format("%-10.2f", val));
                }
                System.out.println();
            }
            System.out.println();

            // Vogel's Approximation Method
            int[][] allocationVogel = vogelsApproximationMethod(
                    Arrays.stream(C).map(row -> Arrays.stream(row).mapToInt(d -> (int) d).toArray()).toArray(int[][]::new),
                    S.clone(), D.clone()
            );
            System.out.println("Initial basic feasible solution using Vogel's Approximation Method:");
            for (int[] row : allocationVogel) {
                for (int val : row) {
                    System.out.print(String.format("%-10d", val));
                }
                System.out.println();
            }
            System.out.println();

            // Russell's Approximation Method
            double[][] allocationRussell = russellsApproximationMethod(C, S.clone(), D.clone());
            System.out.println("Initial basic feasible solution using Russell's Approximation Method:");
            for (double[] row : allocationRussell) {
                for (double val : row) {
                    System.out.print(String.format("%-10.2f", val));
                }
                System.out.println();
            }
            System.out.println();

    }

    public static boolean isBalanced(int[] supply, int[] demand) {
        return Arrays.stream(supply).sum() == Arrays.stream(demand).sum();
    }

    public static int[][] vogelsApproximationMethod(int[][] C, int[] S, int[] D) {
        int[] supply = S.clone();
        int[] demand = D.clone();
        int[][] costs = new int[C.length][C[0].length];
        for (int i = 0; i < C.length; i++) {
            System.arraycopy(C[i], 0, costs[i], 0, C[i].length);
        }

        int numSources = supply.length;
        int numDestinations = demand.length;
        int[][] allocation = new int[numSources][numDestinations];

        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        for (int i = 0; i < numSources; i++) {
            rows.add(i);
        }
        for (int j = 0; j < numDestinations; j++) {
            cols.add(j);
        }

        while (!rows.isEmpty() && !cols.isEmpty()) {
            // Calculate row penalties
            List<RowPenalty> rowPenalties = new ArrayList<>();
            for (int i : rows) {
                List<Integer> costsInRow = new ArrayList<>();
                for (int j : cols) {
                    costsInRow.add(costs[i][j]);
                }
                Collections.sort(costsInRow);
                int penalty = (costsInRow.size() >= 2) ? costsInRow.get(1) - costsInRow.get(0) : 0;
                rowPenalties.add(new RowPenalty(i, penalty));
            }

            // Calculate column penalties
            List<ColPenalty> colPenalties = new ArrayList<>();
            for (int j : cols) {
                List<Integer> costsInCol = new ArrayList<>();
                for (int i : rows) {
                    costsInCol.add(costs[i][j]);
                }
                Collections.sort(costsInCol);
                int penalty = (costsInCol.size() >= 2) ? costsInCol.get(1) - costsInCol.get(0) : 0;
                colPenalties.add(new ColPenalty(j, penalty));
            }

            // Find the maximum penalty
            int maxPenalty = Integer.MIN_VALUE;
            String type = "";
            int index = -1;
            for (RowPenalty rp : rowPenalties) {
                if (rp.penalty > maxPenalty) {
                    maxPenalty = rp.penalty;
                    type = "row";
                    index = rp.index;
                }
            }
            for (ColPenalty cp : colPenalties) {
                if (cp.penalty > maxPenalty) {
                    maxPenalty = cp.penalty;
                    type = "col";
                    index = cp.index;
                }
            }
// Allocate
            if (type.equals("row")) {
                int i = index;
                int minCostIndex = -1;
                int minCost = Integer.MAX_VALUE;
                for (int j : cols) {
                    if (costs[i][j] < minCost) {
                        minCost = costs[i][j];
                        minCostIndex = j;
                    }
                }
                int j = minCostIndex;
                int allocationValue = Math.min(supply[i], demand[j]);
                allocation[i][j] = allocationValue;
                supply[i] -= allocationValue;
                demand[j] -= allocationValue;
                if (supply[i] == 0) {
                    rows.remove(Integer.valueOf(i));
                }
                if (demand[j] == 0) {
                    cols.remove(Integer.valueOf(j));
                }
            } else {
                int j = index;
                int minCostIndex = -1;
                int minCost = Integer.MAX_VALUE;
                for (int i : rows) {
                    if (costs[i][j] < minCost) {
                        minCost = costs[i][j];
                        minCostIndex = i;
                    }
                }
                int i = minCostIndex;
                int allocationValue = Math.min(supply[i], demand[j]);
                allocation[i][j] = allocationValue;
                supply[i] -= allocationValue;
                demand[j] -= allocationValue;
                if (supply[i] == 0) {
                    rows.remove(Integer.valueOf(i));
                }
                if (demand[j] == 0) {
                    cols.remove(Integer.valueOf(j));
                }
            }
        }

        return allocation;
    }

    public static double[][] russellsApproximationMethod(double[][] costs, int[] supply, int[] demand) {
        int n = costs.length;
        int m = costs[0].length;
        int[] supplyCopy = supply.clone();
        int[] demandCopy = demand.clone();
        double[][] allocation = new double[n][m];

        double[] u = new double[n];
        double[] v = new double[m];
        double[][] e = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                e[i][j] = costs[i][j];
            }
        }


        while (Arrays.stream(supplyCopy).anyMatch(x -> x > 0) && Arrays.stream(demandCopy).anyMatch(x -> x > 0)) {
            u = new double[n];
            v = new double[m];
            for (int i = 0; i < n; i++) {
                u[i] = Double.POSITIVE_INFINITY;
                for (int j = 0; j < m; j++) {
                    if (demandCopy[j] > 0) {
                        u[i] = Math.min(u[i], e[i][j]);
                    }
                }
            }
            for (int j = 0; j < m; j++) {
                v[j] = Double.POSITIVE_INFINITY;
                for (int i = 0; i < n; i++) {
                    if (supplyCopy[i] > 0) {
                        v[j] = Math.min(v[j], e[i][j]);
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (demandCopy[j] == 0 || supplyCopy[i] == 0) {
                        e[i][j] = 999;
                    } else {
                        e[i][j] = e[i][j] - u[i] - v[j];
                    }
                }
            }
            double maxE = Double.NEGATIVE_INFINITY;
            int maxI = -1;
            int maxJ = -1;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (supplyCopy[i] > 0 && demandCopy[j] > 0 && e[i][j] > maxE) {
                        maxE = e[i][j];
                        maxI = i;
                        maxJ = j;
                    }
                }
            }

            int i = maxI;
            int j = maxJ;
            int alloc = Math.min(supplyCopy[i], demandCopy[j]);
            allocation[i][j] = alloc;
            supplyCopy[i] -= alloc;
            demandCopy[j] -= alloc;
        }

        return allocation;
    }

    public static double[][] northwestCornerMethod(int[] supply, int[] demand) {
        int n = supply.length;
        int m = demand.length;
        double[][] allocation = new double[n][m];
        int i = 0, j = 0;

        while (i < n && j < m) {
            int alloc = Math.min(supply[i], demand[j]);
            allocation[i][j] = alloc;
            supply[i] -= alloc;
            demand[j] -= alloc;

            if (supply[i] == 0 && demand[j] == 0) {
                i++;
                j++;
            } else if (supply[i] == 0) {
                i++;
            } else if (demand[j] == 0) {
                j++;
            }
        }
        return allocation;
    }

    static class RowPenalty {
        int index;
        int penalty;

        RowPenalty(int index, int penalty) {
            this.index = index;
            this.penalty = penalty;
        }
    }

    static class ColPenalty {
        int index;
        int penalty;

        ColPenalty(int index, int penalty) {
            this.index = index;
            this.penalty = penalty;
        }
    }

    public static void printTransportationTable(double[][] costs, int[] supply, int[] demand) {
        int n = costs.length;
        int m = costs[0].length;

        System.out.println("Transportation Table:");
        System.out.print(String.format("%-10s", " "));
        for (int j = 0; j < m; j++) {
            System.out.print(String.format("%-10s", "D" + (j + 1)));
        }
        System.out.println(String.format("%-10s", "Supply"));

        for (int i = 0; i < n; i++) {
            System.out.print(String.format("%-10s", "S" + (i + 1)));
            for (int j = 0; j < m; j++) {
                System.out.print(String.format("%-10.2f", costs[i][j]));
            }
            System.out.println(String.format("%-10d", supply[i]));
        }

        System.out.print(String.format("%-10s", "Demand"));
        for (int j = 0; j < m; j++) {
            System.out.print(String.format("%-10d", demand[j]));
        }
        System.out.println();
        System.out.println();
    }
}

