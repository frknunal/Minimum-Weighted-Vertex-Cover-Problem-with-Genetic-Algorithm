import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class GenAlgo {
        public static void main(String args[]){

            GenAlgo genAlgo=new GenAlgo();

            File inFile=new File("C:\\Users\\unal\\Desktop\\Java\\Minimum-Weighted-Vertex-Cover-Problem-with-Genetic-Algorithm\\src\\003.txt");
            int numberOfNodes=0, populationSize=100, numberOfGenerations=100;
            double numberOfEdges, crossoverProbability=0.5, mutationProbability;


            Map<String,Node> nodes=new HashMap<>();
            ArrayList<String> population=new ArrayList<>();
            ArrayList<String> populationCrossOver=new ArrayList<>();
            ArrayList<String> populationMutation=new ArrayList<>();
            ArrayList<String> bestSolutions=new ArrayList<>();

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
            mutationProbability=1/numberOfNodes;
            int[][] edgeMatrix=new int[numberOfNodes][numberOfNodes];

            while (numberOfGenerations!=0){

                for (int i=0;i<populationSize;i++){
                    String solution=genAlgo.generateSolution(numberOfNodes);
                    genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, solution);

                    while (!genAlgo.isFeasible(edgeMatrix)){
                        solution=genAlgo.repair(solution, nodes);
                        genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, solution);
                    }
                    population.add(solution);
                }

                for(int i=0;i<populationSize;i++){
                    String firstParent=genAlgo.selectParent(population, nodes);
                    String secondParent=genAlgo.selectParent(population, nodes);

                    double crossOverRand=Math.random();
                    if(crossOverRand<crossoverProbability){
                        int crossOverPoint=(int)(Math.random()*population.get(i).length());
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

                bestSolutions.add(genAlgo.getBestSolution(populationMutation, nodes));

                numberOfGenerations--;
            }


        }
        private String getBestSolution(ArrayList<String> solutions, Map<String, Node> nodes){
            double fitnessValues[]=new double[solutions.size()];
            int solutionOneCount;
            double solutionTotalWeight,totalFitnessValue=0;

            for(int i=0;i<solutions.size();i++){
                solutionOneCount=0;
                solutionTotalWeight=0;
                for (int j=0;j<solutions.get(i).length();j++) {
                    if (solutions.get(i).charAt(j) == '1') {
                        solutionOneCount++;
                        solutionTotalWeight += nodes.get("" + j).getWeight();
                    }
                }
                fitnessValues[i]=solutionOneCount/solutionTotalWeight;
                totalFitnessValue+=(solutionOneCount/solutionTotalWeight);
            }

            int maxIndex = 0;

            for (int i = 0; i < fitnessValues.length; i++) {
                maxIndex = fitnessValues[i] > fitnessValues[maxIndex] ? i : maxIndex;
            }

            return solutions.get(maxIndex);
        }
        private String selectParent(ArrayList<String> population, Map<String, Node> nodes){
            double fitnessValues[]=new double[population.size()];
            double solutionTotalWeight,totalFitnessValue=0;
            int solutionOneCount;
            for(int i=0;i<population.size();i++){
                solutionOneCount=0;
                solutionTotalWeight=0;
                for (int j=0;j<population.get(i).length();j++) {
                    if (population.get(i).charAt(j) == '1') {
                        solutionOneCount++;
                        solutionTotalWeight += nodes.get("" + j).getWeight();
                    }
                }
                fitnessValues[i]=solutionOneCount/solutionTotalWeight;
                totalFitnessValue+=(solutionOneCount/solutionTotalWeight);
            }
            double candidate=Math.random();
            double eachPortionSize=1/totalFitnessValue;
            Arrays.sort(fitnessValues);

            String selectedParent="";

            for (int i=0;i<population.size();i++){
                if(eachPortionSize*fitnessValues[i]>candidate)
                    selectedParent=population.get(i);
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

            for (Map.Entry<Node,Double> node:sortedByValue.entrySet())
                System.out.println(node.getKey().getLabel()+" -> "+node.getValue());

            for (Map.Entry<Node,Double> node:sortedByValue.entrySet()){
                if(eachPortionSize*node.getValue()>candidate){
                    char[] solutionCharArray=solution.toCharArray();
                    solutionCharArray[node.getKey().getLabel()]='1';
                    repairedSol=String.valueOf(solutionCharArray);
                    break;
                }
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
