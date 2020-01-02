import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class GenAlgo {
        public static void main(String args[]){

            GenAlgo genAlgo=new GenAlgo();

            File inFile=new File("C:\\Users\\unal\\Desktop\\Java\\Minimum-Weighted-Vertex-Cover-Problem-with-Genetic-Algorithm\\src\\003.txt");
            int numberOfNodes=0, populationSize=100;
            double numberOfEdges;

            Map<String,Node> nodes=new HashMap<>();
            ArrayList<String> population=new ArrayList<>();

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


            for (int i=0;i<populationSize;i++){
                String solution=genAlgo.generateSolution(numberOfNodes);
                System.out.println(solution);
                genAlgo.fillEdgeMatrix(edgeMatrix, nodes, numberOfNodes, solution);
                genAlgo.printEdgeMatrix(edgeMatrix);

                while (!genAlgo.isFeasible(edgeMatrix)){
                    solution=genAlgo.repair(solution);
                }

            }




        }
        private String repair(String solution){

            return "";
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
                for (int i=0;i<(int)numberOfEdges;i++){
                    line=bufferedReader.readLine();
                    stringTokenizer=new StringTokenizer(line," ");
                    nodes.get(stringTokenizer.nextToken()).getNeighbors().add(nodes.get(stringTokenizer.nextToken()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }

}
