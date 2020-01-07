import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class GenAlgo {
        public static void main(String args[]){

            GenAlgo genAlgo=new GenAlgo();  // Gen Algo class to use non-static methods

            Map<Integer, Double> generationAverageFitness=new HashMap<>();  // save generation average fitness and export to text file to create graph

            String nameOfTheGraph=args[0];                          // first argument of command line, graph name
            int numberOfGenerations=Integer.parseInt(args[1]);      // second argument of command line, number of generations
            int populationSize=Integer.parseInt(args[2]);           // third argument of command line, population size
            double crossoverProbability=Double.parseDouble(args[3]);    // fourth argument of command line, crossover probability
            double mutationProbability=Double.parseDouble(args[4]);     // fifth argument of command line, mutation probability

            int totalNumberOfGeneration=numberOfGenerations;    // save number of generation to save map

            File inFile=new File("C:\\Users\\unal\\Desktop\\Java\\Minimum-Weighted-Vertex-Cover-Problem-with-Genetic-Algorithm\\src\\"+nameOfTheGraph+".txt");  // create file to be open
            int numberOfNodes=0;        // number of nodes will be written from text file
            double numberOfEdges;       // number of edge will be written from text file


            Map<String,Node> nodes=new HashMap<>();             // all nodes in graph
            ArrayList<String> population=new ArrayList<>();     // population list
            ArrayList<String> populationCrossOver=new ArrayList<>();    // population list after crossover
            ArrayList<String> populationMutation=new ArrayList<>();     // population list after mutation
            ArrayList<String> bestSolutions=new ArrayList<>();          // best solution of every generation
            ArrayList<String> matchingPool=new ArrayList<>();           // matching pool list



            try {
                BufferedReader bufferedReader=new BufferedReader(new FileReader(inFile));       // reader to read file
                String line=bufferedReader.readLine();                      // read first line of the file
                numberOfNodes=Integer.parseInt(line);                       // first line is number of nodes
                line=bufferedReader.readLine();                             // read second line of the file
                numberOfEdges=Double.parseDouble(line);                     // second line is number of edges

                genAlgo.generateNodes(nodes, bufferedReader, numberOfNodes, numberOfEdges);     // generates all nodes, and add to nodes hashmap

            }catch (Exception e){
                e.printStackTrace();
            }

            for (Map.Entry<String, Node> entry : nodes.entrySet()) {
                System.out.println(entry.toString());                   // print all nodes, debug purposes
            }


            for (int i=0;i<populationSize;i++){           // generate solution population times
                String solution=genAlgo.generateSolution(numberOfNodes);    // generate solution

                while (!genAlgo.isFeasible(solution, nodes))        // check feasibility of the solution and repair until it becomes feasible
                    solution=genAlgo.repair(solution, nodes);       // repair solution
                population.add(solution);                           // add solution to population list
            }

            for (int i=0;i<populationSize;i++)
                System.out.println(genAlgo.isFeasible(population.get(i), nodes));   // print feasibility of all solutions, debug purposes


            while (numberOfGenerations!=0){         // loop over number of generations

                System.out.println("~~~~~~~~~~~~");
                System.out.println(numberOfGenerations);        // print current generation number


                for (int i=0;i<populationSize;i++)              // select solution from population list and add to matching pool list
                    matchingPool.add(genAlgo.selectSolution(population, nodes, genAlgo));

                /*   Crossover   */
                for(int i=0;i<populationSize/2;i++){            // select from matching pool and add to crossover population list, each loop we add two solution to crossover list so loop over half of the  population size
                    String firstParent=genAlgo.selectSolution(matchingPool, nodes, genAlgo);    // select first parent
                    String secondParent=genAlgo.selectSolution(matchingPool, nodes, genAlgo);   // select second parent

                    double crossOverRand=Math.random();     // generate random number for crossover probability
                    if(crossOverRand<crossoverProbability){     //  do crossover
                        int crossOverPoint=(int)(Math.random()*matchingPool.get(i).length());   // select crossover point
                        String firstChild=firstParent.substring(0, crossOverPoint)+secondParent.substring(crossOverPoint);  // generate first child
                        String secondChild=secondParent.substring(0, crossOverPoint)+firstParent.substring(crossOverPoint); // generate second child
                        populationCrossOver.add(firstChild);        // add first child to crossover population list
                        populationCrossOver.add(secondChild);       // add second child to crossover population list
                    }
                    else{               // do not crossover
                        populationCrossOver.add(firstParent);   // add first parent to crossover population list
                        populationCrossOver.add(secondParent);  // add second parent to crossover population list
                    }
                }

                /*   Mutation   */
                for(int i=0;i<populationSize;i++){          // try to mutate each bit of every solution
                    char[] solutionCharArray=populationCrossOver.get(i).toCharArray();  // generate char array from solution
                    for (int j=0;j<populationCrossOver.get(i).length();j++){        // loop over each bit of the current solution
                        double mutationRand=Math.random();          // generate random number for  mutation
                        if(mutationRand<mutationProbability){       // do mutation
                            if(solutionCharArray[j]=='0')           // bitwise mutation
                                solutionCharArray[j]='1';
                            else
                                solutionCharArray[j]='0';
                        }
                    }
                    populationMutation.add(String.valueOf(solutionCharArray));      // add current solution to population mutation list
                }

                for (int i=0;i<populationSize;i++){         // check feasibility of population after mutation
                    while (!genAlgo.isFeasible(populationMutation.get(i), nodes))      // repair until becomes feasible, if not feasible
                        populationMutation.set(i, genAlgo.repair(populationMutation.get(i), nodes));
                }

                bestSolutions.add(genAlgo.getBestSolution(populationMutation, nodes, genAlgo));     // add best solution from current generation to best solution list

                double totalFitnessOfGeneration=0, averageFitnessOfGeneration;

                for(int i=0;i<populationMutation.size();i++) {      // sum all fitness values of current generation
                    totalFitnessOfGeneration += genAlgo.getFitnessValueOfSolution(populationMutation.get(i), nodes);
                }
                averageFitnessOfGeneration=totalFitnessOfGeneration/populationMutation.size();  // average fitness of current generation
                System.out.println("Average Fitness -> "+averageFitnessOfGeneration);
                generationAverageFitness.put(totalNumberOfGeneration-numberOfGenerations, averageFitnessOfGeneration);
                System.out.println("~~~~~~~~~~~~");

                populationCrossOver.clear();        // clear list
                matchingPool.clear();               // clear list
                population.clear();                 // clear list
                population.addAll(populationMutation);  // generated generation is new population for next generation so add to population list
                populationMutation.clear();         // clear list

                numberOfGenerations--;          // decrease number of generations
            }
            String bestSolution=genAlgo.getBestSolution(bestSolutions, nodes, genAlgo);     // get best solution among all generations

            /*    print parameters      */
            System.out.println("Name of the text : "+args[0]);
            System.out.println("Generation size : "+args[1]);
            System.out.println("Population size : "+args[2]);
            System.out.println("Crossover prob. : "+args[3]);
            System.out.println("Mutation prob. : "+args[4]);

            System.out.println("Best Solution Weight : "+genAlgo.getWeightOfSolution(bestSolution, nodes));

            try {
                PrintWriter writer = new PrintWriter("g1.txt", "UTF_8");
                String s="";
                for (Map.Entry<Integer,Double> node:generationAverageFitness.entrySet()){
                    s+=node.getKey()+" "+node.getValue();
                    writer.println(s);
                    s="";
                }
                    writer.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private boolean isFeasible(String solution, Map<String, Node> nodes){
            boolean test=true;

            for (int i=0;i<solution.length();i++){

                if(solution.charAt(i)=='0'){
                    for (int j=0;j<nodes.get(""+i).getNeighbors().size();j++){
                        if(solution.charAt(nodes.get(""+i).getNeighbors().get(j).getLabel())=='0')
                            return false;
                    }

                }

            }
            return test;
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
            int numberOfZeroWeight=0;
            String repairedSol="";
            for (int i=0;i<solution.length();i++){
                if(solution.charAt(i)=='0'){
                    double fitnessValue=0, numberOfDarkRoads=0;
                    Node currentNode=nodes.get(""+i);

                    for(int j=0;j<currentNode.getNeighbors().size();j++)
                        if (solution.charAt(currentNode.getNeighbors().get(j).getLabel()) != '1')
                            numberOfDarkRoads++;

                    fitnessValue=numberOfDarkRoads/currentNode.getWeight();
                    if(fitnessValue!=0) {
                        if(currentNode.getWeight()==0)
                            candidateNodes.put(currentNode, -1.0);
                        else {
                            totalFitnessValue += fitnessValue;
                            candidateNodes.put(currentNode, fitnessValue);
                        }
                    }
                }
            }
            double maxFitness=0, candidate=Math.random();
            Map<Node, Double> sortedByValue=sortByValue(candidateNodes);
            for (Map.Entry<Node,Double> node:sortedByValue.entrySet())
                maxFitness = node.getValue();
            for (Map.Entry<Node,Double> node:sortedByValue.entrySet())
                if(node.getValue()==-1)
                    node.setValue(maxFitness);

            totalFitnessValue=totalFitnessValue+(numberOfZeroWeight*maxFitness);

            double eachPortionSize=1/totalFitnessValue;

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
