import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class GenAlgo {
        public static void main(String args[]){

            GenAlgo genAlgo=new GenAlgo();

            String nameOfTheGraph=args[0];
            int numberOfGenerations=Integer.parseInt(args[1]);
            int populationSize=Integer.parseInt(args[2]);
            double crossoverProbability=Double.parseDouble(args[3]);
            double mutationProbability=Double.parseDouble(args[4]);


            File inFile=new File("C:\\Users\\unal\\Desktop\\Java\\Minimum-Weighted-Vertex-Cover-Problem-with-Genetic-Algorithm\\src\\"+nameOfTheGraph+".txt");
            int numberOfNodes=0;
            double numberOfEdges;


            Map<String,Node> nodes=new HashMap<>();
            ArrayList<String> population=new ArrayList<>();
            ArrayList<String> populationCrossOver=new ArrayList<>();
            ArrayList<String> populationMutation=new ArrayList<>();
            ArrayList<String> bestSolutions=new ArrayList<>();
            ArrayList<String> matchingPool=new ArrayList<>();



            try {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(inFile));
                String line=bufferedReader.readLine();
                numberOfNodes=Integer.parseInt(line);
                line=bufferedReader.readLine();
                numberOfEdges=Double.parseDouble(line);

                genAlgo.generateNodes(nodes, bufferedReader, numberOfNodes, numberOfEdges);

            }catch (Exception e){
                e.printStackTrace();
            }

            for (Map.Entry<String, Node> entry : nodes.entrySet()) {
                System.out.println(entry.toString());
            }
            int[][] edgeMatrix=new int[numberOfNodes][numberOfNodes];

            while (numberOfGenerations!=0){

                System.out.println("~~~~~~~~~~~~");
                System.out.println(numberOfGenerations);
                System.out.println("~~~~~~~~~~~~");


                for (int i=0;i<populationSize;i++){
                    String solution=genAlgo.generateSolution(numberOfNodes);
                    genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, solution);

                    while (!genAlgo.isFeasible(edgeMatrix)){
                        solution=genAlgo.repair(solution, nodes);
                        genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, solution);
                    }
                    population.add(solution);
                }

                for (int i=0;i<populationSize;i++)
                    matchingPool.add(genAlgo.selectSolution(population, nodes, genAlgo));


                for(int i=0;i<populationSize;i++){
                    String firstParent=genAlgo.selectSolution(matchingPool, nodes, genAlgo);
                    String secondParent=genAlgo.selectSolution(matchingPool, nodes, genAlgo);

                    double crossOverRand=Math.random();
                    if(crossOverRand<crossoverProbability){
                        int crossOverPoint=(int)(Math.random()*matchingPool.get(i).length());
                        String firstChild=firstParent.substring(0, crossOverPoint)+secondParent.substring(crossOverPoint);
                        String secondChild=secondParent.substring(0, crossOverPoint)+firstParent.substring(crossOverPoint);
                        populationCrossOver.add(firstChild);
                        populationCrossOver.add(secondChild);
                    }
                    else{
                        populationCrossOver.add(firstParent);
                        populationCrossOver.add(secondParent);
                    }
                }

                for (int i=0;i<populationSize;i++){
                    int mutationPoint=(int)(Math.random()*populationCrossOver.get(i).length());
                    double mutationRand=Math.random();
                    if(mutationProbability>mutationRand){
                        char[] solutionCharArray=populationCrossOver.get(i).toCharArray();
                        solutionCharArray[mutationPoint]='1';
                        populationMutation.add(String.valueOf(solutionCharArray));
                    }
                    else{
                        populationMutation.add(populationCrossOver.get(i));
                    }
                }

                for (int i=0;i<populationSize;i++){
                    genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, populationMutation.get(i));
                    while (!genAlgo.isFeasible(edgeMatrix)){
                        populationMutation.set(i,genAlgo.repair(populationMutation.get(i), nodes));
                        genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, populationMutation.get(i));
                    }
                }

                bestSolutions.add(genAlgo.getBestSolution(populationMutation, nodes, genAlgo));

                numberOfGenerations--;
            }
            String bestSolution=genAlgo.getBestSolution(bestSolutions, nodes, genAlgo);

            System.out.println("Name of the text : "+args[0]);
            System.out.println("Generation size : "+args[1]);
            System.out.println("Population size : "+args[2]);
            System.out.println("Crossover prob. : "+args[3]);
            System.out.println("Mutatin prob. : "+args[4]);

            System.out.println("Best Solution Weight : "+genAlgo.getWeightOfSolution(bestSolution, nodes));

        }
        private double getWeightOfSolution(String solution, Map<String, Node> nodes){
            double solutionTotalWeight=0;

            for (int i=0;i<solution.length();i++){
                if(solution.charAt(i)=='1'){
                    solutionTotalWeight+=nodes.get(""+i).getWeight();
                }
            }
            return solutionTotalWeight;
        }
        private double getFitnessValueOfSolution(String solution, Map<String, Node> nodes){
            int solutionOneCount=0;
            double solutionTotalWeight=0;

            for (int i=0;i<solution.length();i++){
                if(solution.charAt(i)=='1'){
                    solutionOneCount++;
                    solutionTotalWeight+=nodes.get(""+i).getWeight();
                }
            }
            return solutionOneCount/solutionTotalWeight;
        }
        private int getMaxIndex(double[] list){
            int maxIndex = 0;
            for (int i = 0; i < list.length; i++) {
                maxIndex = list[i] > list[maxIndex] ? i : maxIndex;
            }
            return maxIndex;
        }
        private String getBestSolution(ArrayList<String> solutions, Map<String, Node> nodes, GenAlgo genAlgo){
            double fitnessValues[]=new double[solutions.size()];
            double totalFitnessValue=0;

            for(int i=0;i<solutions.size();i++){
                fitnessValues[i]=genAlgo.getFitnessValueOfSolution(solutions.get(i), nodes);
                totalFitnessValue+=fitnessValues[i];
            }
            return solutions.get(genAlgo.getMaxIndex(fitnessValues));
        }
        private String selectSolution(ArrayList<String> population, Map<String, Node> nodes, GenAlgo genAlgo){
            double fitnessValues[]=new double[population.size()];
            double totalFitnessValue=0;
            for(int i=0;i<population.size();i++){
                fitnessValues[i]=genAlgo.getFitnessValueOfSolution(population.get(i), nodes);
                totalFitnessValue+=fitnessValues[i];
            }
            double candidate=Math.random();
            double eachPortionSize=1/totalFitnessValue;
            Arrays.sort(fitnessValues);

            String selectedParent="";
            double threshold=0;

            for (int i=0;i<population.size();i++){
                if((threshold+(eachPortionSize*fitnessValues[i]))>candidate) {
                    selectedParent = population.get(i);
                    break;
                }
                threshold+=eachPortionSize*fitnessValues[i];
            }
            return selectedParent;
        }
        private String repair(String solution,Map<String, Node> nodes){
            Map<Node,Double> candidateNodes=new HashMap<>();
            double totalFitnessValue=0;
            for (int i=0;i<solution.length();i++){
                if(solution.charAt(i)=='0'){
                    double fitnessValue=0, numberOfDarkRoads=0;
                    Node currentNode=nodes.get(""+i);

                    for(int j=0;j<currentNode.getNeighbors().size();j++)
                        if (solution.charAt(currentNode.getNeighbors().get(j).getLabel()) != '1')
                            numberOfDarkRoads++;

                    fitnessValue=numberOfDarkRoads/currentNode.getWeight();
                    if(Double.isInfinite(fitnessValue))
                        fitnessValue=Double.MAX_VALUE;
                    if(fitnessValue!=0) {
                        totalFitnessValue += fitnessValue;
                        candidateNodes.put(currentNode, fitnessValue);
                    }
                }
            }
            String repairedSol="";
            double candidate=Math.random();
            double eachPortionSize=1/totalFitnessValue;
            Map<Node, Double> sortedByValue=sortByValue(candidateNodes);

            double threshold=0;

            for (Map.Entry<Node,Double> node:sortedByValue.entrySet()){
                if((threshold+(eachPortionSize*node.getValue()))>candidate){
                    char[] solutionCharArray=solution.toCharArray();
                    solutionCharArray[node.getKey().getLabel()]='1';
                    repairedSol=String.valueOf(solutionCharArray);
                    break;
                }
                threshold+=eachPortionSize*node.getValue();
            }

                return repairedSol;
        }
        private Map<Node, Double> sortByValue(Map<Node,Double> candidateNodes){
            return candidateNodes.entrySet()
                    .stream()
                    .sorted((Map.Entry.<Node, Double>comparingByValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        private boolean isFeasible(int[][] edgeMatrix){

            boolean isFeasible;

            for (int i=0;i<edgeMatrix.length;i++) {
                isFeasible=false;
                for (int j = 0; j < edgeMatrix.length; j++) {
                    if (edgeMatrix[i][j] == 1) {
                        isFeasible = true;
                        break;
                    }
                }
                if(!isFeasible)
                    return false;
            }
            return true;
        }
        private void printEdgeMatrix(int[][] edgeMatrix){
            for (int i=0;i<edgeMatrix.length;i++){
                for (int j=0;j<edgeMatrix.length;j++)
                    System.out.print(edgeMatrix[i][j]);
                System.out.println();
            }
        }
        private String generateSolution(int numberOfNodes){
            String s="";
            double rand;
            for(int i=0;i<numberOfNodes;i++){
                rand=Math.random();
                if(rand<0.5)
                    s+="0";
                else
                    s+="1";

            }
            return s;
        }
        private void fillEdgeMatrix(int[][] edgeMatrix, Map<String, Node> nodes, int numberOfNodes, String solution){
            for (int i=0;i<numberOfNodes;i++){
                if(solution.charAt(i)=='1'){
                    for(int j=0;j<nodes.get(""+i).getNeighbors().size();j++){
                        edgeMatrix[i][nodes.get(""+i).getNeighbors().get(j).getLabel()]=1;
                        edgeMatrix[nodes.get(""+i).getNeighbors().get(j).getLabel()][i]=1;
                    }
                }
            }
        }

        private void generateNodes(Map<String, Node> nodes, BufferedReader bufferedReader, int numberOfNodes, double numberOfEdges){

            StringTokenizer stringTokenizer;
            String line;

            try {
                for (int i=0;i<numberOfNodes;i++){
                    Node node=new Node();
                    line=bufferedReader.readLine();
                    stringTokenizer=new StringTokenizer(line," ");
                    node.setLabel(Integer.parseInt(stringTokenizer.nextToken()));
                    node.setWeight(Double.parseDouble(stringTokenizer.nextToken().replaceAll(",",".")));
                    nodes.put(""+i,node);
                }
                while ((line=bufferedReader.readLine())!=null){
                    stringTokenizer=new StringTokenizer(line," ");
                    nodes.get(stringTokenizer.nextToken()).getNeighbors().add(nodes.get(stringTokenizer.nextToken()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }

}
