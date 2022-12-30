import java.util.*;

public class Solver {
    public static final int TABU_TENURE = 5;

    public static int[] computeStartingSolution(int[] w, int[] p, int[] d) {
        int[][] difference_DMinusP = new int[2][w.length];

        for (int k = 0; k < w.length; k++) {
                difference_DMinusP[0][k] = k;
                difference_DMinusP[1][k] = d[k] - p[k];
        }
        int[][] orderedDifferences = orderByAscendingDifference(difference_DMinusP, 1);

        return orderedDifferences[0];
    }

    private static int[][] orderByAscendingDifference(int[][] matrixDifferences, int indexColumn) {
        boolean isChanged;
        int[] tempCouple = new int[2];
        for (int i = 0; i < matrixDifferences[indexColumn].length - 1; i++) {
            isChanged = false;
            for (int j = 0; j < matrixDifferences[indexColumn].length - 1; j++) {
                int firstElement = matrixDifferences[indexColumn][j];
                int secondElement = matrixDifferences[indexColumn][j+1];
                if (firstElement > secondElement) {
                    tempCouple[0] = matrixDifferences[0][j];
                    tempCouple[1] = matrixDifferences[1][j];
                    matrixDifferences[0][j] = matrixDifferences[0][j+1];
                    matrixDifferences[1][j] = matrixDifferences[1][j+1];
                    matrixDifferences[0][j+1] = tempCouple[0];
                    matrixDifferences[1][j+1] = tempCouple[1];

                    isChanged = true;
                }
            }

            if (!isChanged)
                break;
        }
        return matrixDifferences;
    }

    public static Model executeTabuSearch(Model model) {
        int tabuTenure = TABU_TENURE;
        int[] currentSolutionTS = model.getCurrentSolution();

        //neighborhood exploration
        ArrayList<int[]> neighborhood = new ArrayList<int[]>();
        for (int i = 0; i < currentSolutionTS.length; i++) {
            int[] solutionWithAppliedMove = applyMove(currentSolutionTS, i);
            neighborhood.add(solutionWithAppliedMove);
        }

        ArrayList<int[]> tabuList = model.getTabuList(); //contains tabu moves indexes [a,b,c]: job a (from 0 to 5) has been transferred from position b (from 0 to 5) and has a tabu tenure of c
        ArrayList<int[]> neighborhoodWithNonTabuMoves = new ArrayList<>();
        ArrayList<Integer> indexesNonTabuMoves = new ArrayList<>();
        do {
            for (int i = 0; i < neighborhood.size(); i++) {
                int[] solutionOfNeighborhoodToCheck = neighborhood.get(i);
                if (checkIfTabuOK(solutionOfNeighborhoodToCheck, tabuList)) {
                    neighborhoodWithNonTabuMoves.add(solutionOfNeighborhoodToCheck);
                    indexesNonTabuMoves.add(i);
                }
            }
            if (neighborhoodWithNonTabuMoves.isEmpty()) {
                tabuList = defaultAspirationCriteria(tabuList);
            }
        } while (neighborhoodWithNonTabuMoves.isEmpty());


        int[] bestSolutionAmongNeighborhood = neighborhoodWithNonTabuMoves.get(0);
        int objFunValueBestSolutionAmongNeighborhood = model.computeObjFunValue(bestSolutionAmongNeighborhood);
        int indexFirstElementBestSolution = indexesNonTabuMoves.get(0);
        int indexSecondElementBestSolution;
        for (int i = 0; i < neighborhoodWithNonTabuMoves.size(); i++) {
            int[] solutionToCheck = neighborhoodWithNonTabuMoves.get(i);
            int objFunValueToCheck = model.computeObjFunValue(solutionToCheck);

            if (objFunValueToCheck < objFunValueBestSolutionAmongNeighborhood) {
                bestSolutionAmongNeighborhood = solutionToCheck;
                objFunValueBestSolutionAmongNeighborhood = objFunValueToCheck;
                indexFirstElementBestSolution = indexesNonTabuMoves.get(i);
            }
        }

        //add to tabu list
        int[] tabuMove1 = new int[3];
        int[] tabuMove2 = new int[3];

        indexSecondElementBestSolution = indexFirstElementBestSolution + 1;
        if (indexSecondElementBestSolution > 5) {
            indexSecondElementBestSolution = 0;
        }

        tabuMove1[0] = currentSolutionTS[indexFirstElementBestSolution];
        tabuMove1[1] = indexFirstElementBestSolution;
        tabuMove1[2] = tabuTenure;
        tabuMove2[0] = currentSolutionTS[indexSecondElementBestSolution];
        tabuMove2[1] = indexSecondElementBestSolution;
        tabuMove2[2] = tabuTenure;

        //decrement tabu tenure
        for (int i = 0; i < tabuList.size(); i++) {
            int[] tabuMove = tabuList.get(i);
            tabuMove[2] = tabuMove[2] - 1;
            if (tabuMove[2] == 0)
                tabuList.remove(tabuMove);
        }
        tabuList.add(tabuMove1);
        tabuList.add(tabuMove2);

        model.setCurrentSolution(bestSolutionAmongNeighborhood);
        model.setTabuList(tabuList);

        return model;


    }

    private static ArrayList<int[]> defaultAspirationCriteria(ArrayList<int[]> oldTabuList) {
        System.out.println("\n***** BEGIN OF DEFAULT ASPIRATION CRITERIA *****\nSince there were no more free moves, the deault aspiration criteria has been invocated\nAll moves has been made free again\n***** END OF DEFAULT ASPIRATION CRITERIA *****\n");

        return new ArrayList<int[]>();
    }

    private static int[] applyMove(int[] soluzione, int indicePrimoElemento) {
        int[] soluzioneConMossa = new int[soluzione.length];
        for (int i = 0; i < soluzione.length; i++) {
            soluzioneConMossa[i] = soluzione[i];
        }
        int indiceSecondoElemento;
        if (indicePrimoElemento == 5)
            indiceSecondoElemento = 0;
        else
            indiceSecondoElemento = indicePrimoElemento + 1;


        int temp = soluzione[indicePrimoElemento];
        soluzioneConMossa[indicePrimoElemento] = soluzione[indiceSecondoElemento];
        soluzioneConMossa[indiceSecondoElemento] = temp;

        return soluzioneConMossa;
    }

    private static boolean checkIfTabuOK(int[] soluzioneTemporaneaConMossa, ArrayList<int[]> tabuList) {
        int indicePerControllo;
        for (int i = 0; i < tabuList.size(); i++) {
            int lavoroTabu = tabuList.get(i)[0];
            int indiceTabu = tabuList.get(i)[1];

            int lavoroPerControllo = soluzioneTemporaneaConMossa[indiceTabu];
            if (lavoroPerControllo == lavoroTabu) {
                return false;
            }
        }

        return true;
    }
}
