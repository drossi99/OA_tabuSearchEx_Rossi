import java.util.ArrayList;

public class Model {
    private int[] w;
    private int[] p;
    private int[] d;
    private int[] currentSolution;
    private ArrayList<int[]> tabuList;

    public Model(int[] w, int[] p, int[] d) {
        this.w = w;
        this.p = p;
        this.d = d;
        this.tabuList = new ArrayList<>();
    }

    public int computeObjFunValue(int[] solutionForObjFunComputing) {
        int objFunValue = 0;

        for (int i = 0; i < 6; i++) {
            int c_timeTillNow = 0;
            for (int j = 0; j <= i; j++) {
                c_timeTillNow += p[solutionForObjFunComputing[j]];
            }
            objFunValue += w[i] * Integer.max(0, c_timeTillNow - d[solutionForObjFunComputing[i]]);
        }

        return objFunValue;
    }

    public void printSolution(int[] solutionForPrinting) {
        StringBuilder strBuilderSolution = new StringBuilder();
        strBuilderSolution.append("\tCurrent solution: [");
        for (int i = 0; i < solutionForPrinting.length-1; i++) {
            strBuilderSolution.append((solutionForPrinting[i] + 1) + ", "); //+1 since jobs are from 1 to 6, but theis array is from 0 to 5
        }
        strBuilderSolution.append((solutionForPrinting[solutionForPrinting.length-1] + 1) + "]");
        strBuilderSolution.append("\n\tObjcetive function value: " + this.computeObjFunValue(solutionForPrinting));
        if (!this.tabuList.isEmpty()) {
            strBuilderSolution.append("\n\tTabu list contain folliwing elements:");
            for (int[] tabuMove : this.tabuList) {
                strBuilderSolution.append("\n\t\tJob " + (tabuMove[0]+1) + " can't be placed at position " + (tabuMove[1]+1) + ": this move has still a tabu tenure of " + tabuMove[2]);
            }
        }
        strBuilderSolution.append("\n**********");
        System.out.println(strBuilderSolution);
    }

    public int[] getW() {
        return w;
    }

    public void setW(int[] w) {
        this.w = w;
    }

    public int[] getP() {
        return p;
    }

    public void setP(int[] p) {
        this.p = p;
    }

    public int[] getD() {
        return d;
    }

    public void setD(int[] d) {
        this.d = d;
    }

    public int[] getCurrentSolution() {
        return currentSolution;
    }

    public void setCurrentSolution(int[] currentSolution) {
        this.currentSolution = currentSolution;
    }

    public ArrayList<int[]> getTabuList() {
        return tabuList;
    }

    public void setTabuList(ArrayList<int[]> tabuList) {
        this.tabuList = tabuList;
    }
}
