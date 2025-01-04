import java.util.*;
import java.awt.Point;

public class test_genetic{
	private static final double ALPHA = 0.5;
	private static final int GENETIC = 100;
	private static final int POBLACIO = 16;

    private static ArrayList<Point> constructGreedyRandomizedSolution(Random rand, int size) {
        ArrayList<Point> solution = new ArrayList<>();
        boolean[] usedInst = new boolean[size];

        for (int loc = 0; loc < size; loc++) {
            ArrayList<Integer> notUsedInst = getUsable(usedInst);

            if (!notUsedInst.isEmpty()) {
                double randomValue = rand.nextDouble();
                double threshold = ALPHA * randomValue;

                int selectedInst = 0;
                double cumulativeProbability = 0.0;

                while (selectedInst < notUsedInst.size() - 1 &&
                        cumulativeProbability + 1.0 / notUsedInst.size() < threshold) {
                    cumulativeProbability += 1.0 / notUsedInst.size();
                    ++selectedInst;
                }

                Point p = new Point(loc, notUsedInst.get(selectedInst));
                solution.add(p);
                usedInst[p.y] = true;
            }
        }

        return solution;
    }

    private static ArrayList<Integer> getUsable(boolean[] b) {
        ArrayList<Integer> res = new ArrayList<>();

        for(int i = 0; i < b.length; ++i) {
            if (!b[i]) {
                res.add(i);
            }
        }
        return res;
    }

    protected static double costBtw2Assig(double[][] distLoc, int[][] traficoInst, Point a, Point b) {
        return (distLoc[a.x][b.x]*traficoInst[a.y][b.y]);
    }
	
	private static double totalCostSolution(double[][] distLoc, int[][] traficoInst, ArrayList<Point> l) {
	    double sum = 0;
	    for (int i = 0; i < l.size(); ++i) {
	        Point p1 = l.get(i);
	        for (int j = i + 1; j < l.size(); ++j) {
	        	Point p2 = l.get(j);
	            sum += costBtw2Assig(distLoc, traficoInst, p1, p2);
	        }
	    }
	    return sum;
	}


	public static void selectSolutions(ArrayList<ArrayList<Point>> solucions, ArrayList<Double> valors){
        
        for (int i = 0; i < POBLACIO/2; i++) {
            double max = Double.MIN_VALUE;
            for (double num : valors) {
                if (num > max) {
                    max = num;
                }
            }
            int indice = valors.indexOf(max);
            solucions.remove(indice);
            valors.remove(indice);
        }
	}


	public static void generarFills(ArrayList<ArrayList<Point>> solucions, ArrayList<Double> valors, double[][] distLoc, int[][] traficoInst){
        ArrayList<ArrayList<Point>> solucions2 = new ArrayList<ArrayList<Point>>();
        for (int i = 0; i < POBLACIO/2; i++) {
            Random rand = new Random();
         	int r = rand.nextInt(solucions.size());        
         	ArrayList<Point> fill = mezclarSolucions(solucions.get(i), solucions.get(r));
         	if(rand.nextInt(2) == 1){
               
         		aplicarMutacio(fill,solucions);
               
         	}
         	solucions2.add(fill);
        }
        for (ArrayList<Point> a: solucions2) {
            solucions.add(a);
            valors.add(totalCostSolution(distLoc, traficoInst, a));
        }


	}

	public static void aplicarMutacio(ArrayList<Point> a, ArrayList<ArrayList<Point>> solucions){

		Random rand = new Random();
		int tecla1 = rand.nextInt(a.size());
		int tecla2 = rand.nextInt(a.size());
		Point p1 = a.get(tecla1);
		Point p2 = a.get(tecla2);
		int temp = (int) p1.getX();                                                                    
        Point p3 = new Point((int) p2.getX(), (int) p1.getY());
        Point p4 = new Point(temp, (int) p2.getY());
        a.set(tecla1,p3);
        a.set(tecla2,p4);
	}


	private static Point tecla(int index, ArrayList<Point> x){ //retorna el point de a que te el valor x = i
		for (int i=0; i<x.size(); i++){
			Point p = new Point();
			p = x.get(i);
			if( index == p.getX()) return p;
		}
		return new Point(); //rarete
	}

	public static ArrayList<Point> mezclarSolucions(ArrayList<Point> a, ArrayList<Point> b){

		ArrayList<Point> c = new ArrayList<Point>();
		Random rand = new Random();
		int tecles_a = rand.nextInt(a.size());
		int tecles_b = a.size()-tecles_a;
		ArrayList<Integer> por_colocar = new ArrayList<Integer>();
		ArrayList<Integer> tecla_vacia = new ArrayList<Integer>();
		Point p = new Point();
		for(int i=0; i<a.size(); i++){
			por_colocar.add(i);
		}
		for(int i=0; i<a.size(); i++){
			if(i < tecles_a){ //colocar les tecles de a
				p = tecla(i, a);
				por_colocar.remove(Integer.valueOf((int) p.getY()));
             	c.add(p);
			} else{ //colocar les tecles de b que no tenen una lletra posada al teclat per a
				p = tecla(i, b);
                
				if(por_colocar.remove(Integer.valueOf((int) p.getY()))){
					c.add(p);
				}else{
					tecla_vacia.add((int) p.getX());
				}
			}
		}
		for(int i=0; i<tecla_vacia.size(); i++){ //assigna les lletres per colocar 
			p = new Point(tecla_vacia.get(i), por_colocar.get(i));
			c.add(p);
		}
		return c;
	}


    public static ArrayList<Point> crearLayout(double[][] distLoc, int[][] traficoInst) {
    	ArrayList<ArrayList<Point>> solucions = new ArrayList<ArrayList<Point>>();
    	ArrayList<Double> valors = new ArrayList<>();
    	int n = distLoc.length;
    	for (int i = 0; i < POBLACIO; ++i) {
    		Random rand = new Random();
    		ArrayList<Point> sol_aux = constructGreedyRandomizedSolution(rand, n);
    		solucions.add(sol_aux);
    		valors.add(totalCostSolution(distLoc, traficoInst, sol_aux));
            for (Double punto : valors) {
                 System.out.println(punto);
            }
    	}
            
    	for(int generacio = 0; generacio<GENETIC; ++generacio){

    		selectSolutions(solucions, valors);
    		generarFills(solucions,valors, distLoc, traficoInst);
    	}
    	
    	double min = Double.MAX_VALUE;
    	for (double num : valors) {
                if (num < min) {
                    min = num;
            }
        }
        System.out.println(min);
        return solucions.get(valors.indexOf(min)); 
    }

	public static void main(String[] args) {
		//Input para QAP n = 10
        int[][] traficoInst = {
            {0, 13, 11, 12, 14, 15, 12, 11, 13, 14},
            {13, 0, 2, 1, 2, 3, 1, 2, 4, 2},
            {11, 2, 0, 4, 1, 2, 3, 1, 2, 1},
            {12, 1, 4, 0, 2, 1, 4, 2, 1, 3},
            {14, 2, 1, 2, 0, 3, 1, 3, 2, 2},
            {15, 3, 2, 1, 3, 0, 2, 4, 1, 1},
            {12, 1, 3, 4, 1, 2, 0, 3, 1, 2},
            {11, 2, 1, 2, 3, 4, 3, 0, 2, 4},
            {13, 4, 2, 1, 2, 1, 1, 2, 0, 3},
            {14, 2, 1, 3, 2, 1, 2, 4, 3, 0}
          };

      double[][] distLoc = {
            {0, 11, 12, 13, 14, 15, 16, 17, 18, 19},
            {11, 0, 1, 2, 3, 4, 5, 6, 7, 8},
            {12, 1, 0, 1, 2, 3, 4, 5, 6, 7},
            {13, 2, 1, 0, 1, 2, 3, 4, 5, 6},
            {14, 3, 2, 1, 0, 1, 2, 3, 4, 5},
            {15, 4, 3, 2, 1, 0, 1, 2, 3, 4},
            {16, 5, 4, 3, 2, 1, 0, 1, 2, 3},
            {17, 6, 5, 4, 3, 2, 1, 0, 1, 2},
            {18, 7, 6, 5, 4, 3, 2, 1, 0, 1},
            {19, 8, 7, 6, 5, 4, 3, 2, 1, 0}};
        
        ArrayList<Point> a = crearLayout(distLoc, traficoInst);
            System.out.println("final");
            for (Point punto : a) {
                 System.out.println(punto);
        }
	}
}
