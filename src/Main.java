import java.util.ArrayList;

public class Main {
    public static final int NUMBER_TABU_SEARCH_ITERATIONS = 10;

    public static void main(String[] args) {
        int w_penaltyTardiness [] = {1,1,1,1,1,1};
        int p_processingTime [] = {6,4,8,2,10,3};
        int d_dueDate [] = {9,12,15,8,20,22};

        Model model = new Model(w_penaltyTardiness, p_processingTime, d_dueDate);
        model.setCurrentSolution(Solver.computeStartingSolution(w_penaltyTardiness,p_processingTime,d_dueDate));
        System.out.println("The starting solution, computed with a greedy method, is:");
        model.printSolution(model.getCurrentSolution());

        ArrayList<int[]> foundSolutions = new ArrayList<>();
        ArrayList<Integer> foundObjFunValues = new ArrayList<>();

        foundSolutions.add(model.getCurrentSolution());
        foundObjFunValues.add(model.computeObjFunValue(model.getCurrentSolution()));
        int numberTabuSearchIterations = NUMBER_TABU_SEARCH_ITERATIONS;
        for (int i = 1; i < numberTabuSearchIterations+1; i++) {
            model = Solver.executeTabuSearch(model);
            System.out.println("At iteration " + i + " the following results have been obtained:");
            model.printSolution(model.getCurrentSolution());
            foundSolutions.add(model.getCurrentSolution());
            foundObjFunValues.add(model.computeObjFunValue(model.getCurrentSolution()));
        }

        int[] optSolution = foundSolutions.get(0);
        int optObjFunValue = foundObjFunValues.get(0);
        for (int i = 1; i < foundSolutions.size(); i++) {
            if (foundObjFunValues.get(i) < optObjFunValue) {
                optSolution = foundSolutions.get(i);
                optObjFunValue = foundObjFunValues.get(i);
            }
        }

        StringBuilder strBuilderSolutions = new StringBuilder();
        strBuilderSolutions.append("At the end of ").append(numberTabuSearchIterations).append(" iterations the best solution found is'\n[");
        for (int i = 0; i < optSolution.length - 1; i++) {
            strBuilderSolutions.append(optSolution[i] + 1).append(", ");
        }
        strBuilderSolutions.append(optSolution[optSolution.length - 1] + 1).append("]\nwith an objective function value of: ").append(optObjFunValue);
        System.out.println(strBuilderSolutions);

    }
}
