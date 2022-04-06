//Jan Brkic
//janbrkic3@gmail.com
//Faculty of electrical engineering and computing Zagreb

package ui;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Solution {
    static Map<String, Double> heuristicOfNodes = new TreeMap<>();
    static Map <String, String> generalNodes = new HashMap<>();
    static Set<String> finalNodes = new HashSet<>();
    static String startingStateNode = null;
    static String pathFinder = null;
    static int stateVisited = 0;
    static double totalCost = 0.0, totalCost2 = 0;
    static List<Solution.Node> closed = new LinkedList<>();

    static class Node implements Comparable<Node>{
        private String state;
        private Node parent;
        private double cost;
        private double costForPassingInOtherState;

        public Node(String state, Node parent, double cost, double costForPassingInOtherState) {
            this.state = state;
            this.parent = parent;
            this.cost = cost;
            this.costForPassingInOtherState = costForPassingInOtherState;
        }

        public String getState() {
            return state;
        }

        public Node getParent() {
            return parent;
        }

        public double getCost() {
            return cost;
        }

        public int getDepth () {
            int depth = 0;
            Node current = this.getParent();
            while (current != null) {
                depth++;
                current = current.getParent();
            }
            return depth;
        }

        public static void getFinalPath (Node finalNode) { //get the path for the final node
            int cnt = 0;
            Stack ispis = new Stack();
            Node current = finalNode;
            while (current.getParent() != null) {
                totalCost2 += current.getCostForPassingInOtherState();
                ispis.push (current.getState());
                current = current.getParent();
            }
            while (!ispis.empty()) {
                if (cnt == 0) {
                    pathFinder += " => " + ispis.pop();
                    cnt++;
                }else {
                    pathFinder += " => " + ispis.pop();
                }
            }
        }

        public double getCostForPassingInOtherState() {
            return costForPassingInOtherState;
        }
        @Override
        public int compareTo(Node o) {
            return Double.compare(this.cost, o.cost);
        }

    }
    static public class HeuristicNode implements Comparable<HeuristicNode>{
        private String state;
        private HeuristicNode parent;
        private double cost;
        private double costForPassingInOtherState;
        private double heusristicTotalCost;

        public HeuristicNode(String state, HeuristicNode parent, double cost, double costForPassingInOtherState, double heusristicTotalCost) {
            this.state = state;
            this.parent = parent;
            this.cost = cost;
            this.costForPassingInOtherState = costForPassingInOtherState;
            this.heusristicTotalCost = heusristicTotalCost;
        }

        public String getState() {
            return state;
        }

        public HeuristicNode getParent() {
            return parent;
        }

        public double getCost() {
            return cost;
        }

        public static void getFinalPath (HeuristicNode finalNode) { //get the path for the final node
            int cnt = 0;
            Stack ispis = new Stack();
            HeuristicNode current = finalNode;
            while (current.getParent() != null) {
                totalCost2 += current.getCostForPassingInOtherState();
                ispis.push (current.getState());
                current = current.getParent();
            }
            while (!ispis.empty()) {
                if (cnt == 0) {
                    pathFinder += " => " + ispis.pop();
                    cnt++;
                }else {
                    pathFinder += " => " + ispis.pop();
                }
            }
        }
        public double getHeusristicTotalCost() {
            return heusristicTotalCost;
        }

        public double getCostForPassingInOtherState() {
            return costForPassingInOtherState;
        }
        @Override
        public int compareTo(HeuristicNode o) {
            return Double.compare(this.getHeusristicTotalCost(), o.getHeusristicTotalCost());
        }
    }

    public static void main (String[] args) {
        boolean found = false;
        boolean found2 = false;
        boolean optimistic = false;
        boolean consistent = false;
        String alg = null;
        String filePath = null, heuristicFilePath = null;
        Boolean checkOptimistic = false;
        Boolean checkConistent = false;

        for (int i = 0; i<args.length; i++) {
            if (args[i].equals("--alg")) {
                alg = args[i+1];
            }else if (args[i].equals("--ss")) {
                filePath = args [i+1];
            }else if (args[i].equals("--h")) {
                heuristicFilePath = args[i+1];
            }else if (args[i].equals("--check-optimistic")) {
                checkOptimistic = true;
            }else if (args[i].equals("--check-consistent")) {
                checkConistent = true;
            }
        }
        if (heuristicFilePath != null) {
            analyseHeuristic (heuristicFilePath);
        }
        readFile(filePath);
        if (alg != null) {

            if (alg.equals("bfs")) {
                System.out.println("# BFS");
                found2 = true;
                found = bfs();
            } else if (alg.equals("ucs")) {
                found2 = true;
                System.out.println("# UCS");
                found = ucs();
            } else if (alg.equals("astar")) {
                found2 = true;
                System.out.println("# A-STAR " + heuristicFilePath);
                found = astar();
            }
        }
        if (checkOptimistic) {
            System.out.println("# HEURISTIC-OPTIMISTIC " + heuristicFilePath);
            optimistic = checkOptimisticFunction ();
            if (optimistic)
                System.out.println("[CONCLUSION]: Heuristic is optimistic.");
            else {
                System.out.println("[CONCLUSION]: Heuristic is not optimistic.");
            }
        }
        if (checkConistent) {
            consistent = checkConistentFunction();
            if (consistent) {
                System.out.println("[CONCLUSION]: Heuristic is consistent.");
            }else {
                System.out.println("[CONCLUSION]: Heuristic is not consistent.");
            }
        }
        if (found && found2) {
            System.out.println("[FOUND_SOLUTION]: yes");
            System.out.println("[STATES_VISITED]: " + stateVisited);
            System.out.println("[PATH_LENGTH]: " + pathFinder.split("=>").length);
            System.out.println("[TOTAL_COST]: " + totalCost2);
            System.out.println("[PATH]: " + pathFinder);
        }else if (found2) {
            System.out.println("[FOUND_SOLUTION]: no");
        }

    }

    private static void readFile(String filePath) {
        //LinkedList<String, Integer> childrenNodes = new LinkedList<>();
        Map <String, Double> childrenNodes = new HashMap<>();
        String [] line = null;
        String subLine = null;
        int startfinal = 0;
        StringBuilder sb = new StringBuilder();


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                line = currentLine.split(" ");
                if (startfinal == 0 && !line[0].equals("#")) {
                    startingStateNode = line[0];
                    startfinal++;
                }else if (startfinal == 1 && !line[0].equals("#")) {
                    for (int i = 0; i<line.length; i++) {
                        finalNodes.add(line[i]);
                    }
                    startfinal++;
                }else if (!line[0].equals("#")) {
                    line[0] = line[0].replace(":", "");
                    for (int i = 1; i<line.length; i++) {
                        //subLine = line[i].split(",");
                        sb.append(line[i] + " ");
                        //childrenNodes.put(subLine[0], Double.parseDouble(subLine[1]));
                    }
                    subLine = sb.toString();
                    generalNodes.put (line[0], subLine);
                    //generalNodes2.put (line[0], )
                }
                //childrenNodes.clear();
                sb = new StringBuilder("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void analyseHeuristic(String heuristicFilePath) {
        String line[] = null;

        try (BufferedReader br = new BufferedReader(new FileReader(heuristicFilePath))) {
            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                line = currentLine.split(" ");
                heuristicOfNodes.put(line[0].replace(":", ""), Double.parseDouble(line[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkConistentFunction() {
        double currentHeuristic = 0;
        String expandedNodes = null;
        boolean ok = true;
        finalNodes.add("Buzet");

        for (String item : heuristicOfNodes.keySet()) {
            currentHeuristic = heuristicOfNodes.get(item);
            expandedNodes = generalNodes.get(item);
            for (String node : expandedNodes.split(" ")){
                if (!(currentHeuristic <= heuristicOfNodes.get(node.split(",")[0]) + Double.parseDouble(node.split(",")[1]))) {
                    System.out.println("[CONDITION]: [ERR] h(" + item + ") <= " + "h(" + node.split(",")[0] + ")" + " + c: " +
                            currentHeuristic + " <= " + heuristicOfNodes.get(node.split(",")[0]) + " + " + Double.parseDouble(node.split(",")[1]));
                    ok = false;
                }else {
                    System.out.println("[CONDITION]: [OK] h(" + item + ") <= " + "h(" + node.split(",")[0] + ")" + " + c: " +
                            currentHeuristic + " <= " + heuristicOfNodes.get(node.split(",")[0]) + " + " + Double.parseDouble(node.split(",")[1]));
                }
            }
        }
        return ok;
    }

    private static boolean checkOptimisticFunction() {
        boolean pass = true;
        finalNodes.add("Buzet");
        for (String item : heuristicOfNodes.keySet()) {
            startingStateNode = item;
            ucs();
            if (heuristicOfNodes.get(item) > totalCost2) {
                System.out.println("[CONDITION]: [ERR] h(" + item + ") <= h*: " +
                        heuristicOfNodes.get(item) + " <= " + Double.toString(totalCost2));
                pass = false;
            }
            else {
                System.out.println("[CONDITION]: [OK] h(" + item + ") <= h*: " +
                        heuristicOfNodes.get(item) + " <= " + Double.toString(totalCost2));
            }
            totalCost2 = 0;
        }
        finalNodes.remove("Buzet");
        return pass;
    }

    private static boolean astar() {
        String valueOfNode = null;
        //List<String> visited = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        HeuristicNode childNode = null;
        Queue <HeuristicNode> open = new PriorityQueue<>();
        boolean addToOpen = true;

        HeuristicNode n0 = new HeuristicNode (startingStateNode, null, 0, heuristicOfNodes.get(startingStateNode), heuristicOfNodes.get(startingStateNode));
        open.add (n0);

        while (!open.isEmpty()) {
            HeuristicNode n = open.remove();
            for (String finalStateNode : finalNodes) {
                if (n.getState().equals(finalStateNode)) {
                    visited.add(n.getState());
                    pathFinder = startingStateNode;
                    n.getFinalPath(n);
                    return true;
                }
            }
            visited.add(n.getState());
            valueOfNode = generalNodes.get(n.getState());
            stateVisited++;
            if (null != valueOfNode)
                for (String node : valueOfNode.split(" ")) {
                    if (visited.contains(node.split(",")[0])) continue;
                    if (valueOfNode != null) {
                        childNode = new HeuristicNode(node.split(",")[0], n,Double.parseDouble(node.split(",")[1]) + n.getCost(), Double.parseDouble(node.split(",")[1]), heuristicOfNodes.get(node.split(",")[0]) + Double.parseDouble(node.split(",")[1]) + n.getCost());
                    }else {
                        childNode = new HeuristicNode(node.split(",")[0], n,n.getCost(), 0,heuristicOfNodes.get(node.split(",")[0]) + Double.parseDouble(node.split(",")[1]) + n.getCost());
                    }
                    double cost = childNode.getCost() + n.getCost();
                    double totalHeuristicCost = cost + heuristicOfNodes.get(node.split(",")[0]);

                    for (HeuristicNode nodeInOpen : open) {
                        if (!childNode.getState().equals(nodeInOpen.getState())) continue;
                        if (nodeInOpen.getHeusristicTotalCost() <= childNode.getHeusristicTotalCost()) {
                            addToOpen = false;
                        }else {
                            open.remove (nodeInOpen);
                            break;
                        }
                    }
                    if (addToOpen)
                        open.add(childNode);
                    addToOpen = true;
                }
        }

        return false;
    }

    private static boolean ucs() {
        String valueOfNode = null;
        //List<String> visited = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Node childNode = null;
        Queue <Node> open = new PriorityQueue<>();
        boolean addToOpen = true;

        Node n0 = new Node (startingStateNode, null, 0, 0);
        open.add (n0);
        visited.add(n0.getState());

        while (!open.isEmpty()) {
            Node n = open.remove();
            for (String finalStateNode : finalNodes) {
                if (n.getState().equals(finalStateNode)) {
                    visited.add(n.getState());
                    pathFinder = startingStateNode;
                    n.getFinalPath(n);
                    return true;
                }
            }
            visited.add(n.getState());
            valueOfNode = generalNodes.get(n.getState());
            stateVisited++;
            for (String node : valueOfNode.split(" ")) {
                if (visited.contains(node.split(",")[0])) continue;
                if (valueOfNode != null) {
                    childNode = new Node(node.split(",")[0], n,Double.parseDouble(node.split(",")[1]) + n.getCost(), Double.parseDouble(node.split(",")[1]));
                }else {
                    childNode = new Node(node.split(",")[0], n,n.getCost(), 0);
                }

                Iterator <Node> it = open.iterator();
                while (it.hasNext()){
                    Node nodeInOpen = it.next();
                    if (!childNode.getState().equals(nodeInOpen.getState())) continue;
                    if (nodeInOpen.getCost() <= childNode.getCost()) {
                        addToOpen = false;
                    }else {
                        //open.remove (nodeInOpen);
                        it.remove();
                    }
                }
                if (addToOpen) {

                    open.add(childNode);
                }
                addToOpen = true;
            }
        }

        return false;
    }

    private static boolean bfs() {
        String valueOfNode = null;
        //List<String> visited = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Node childNode = null;
        Deque <Node> open = new LinkedList<>();

        Node n0 = new Node (startingStateNode, null, 0, 0);
        open.add (n0);

        while (!open.isEmpty()) {
            Node n = open.removeFirst();
            valueOfNode = generalNodes.get(n.getState());
            stateVisited++;
            for (String node : valueOfNode.split(" ")) {
                if (visited.contains(node.split(",")[0])) continue;
                if (valueOfNode != null) {
                    childNode = new Node(node.split(",")[0], n,0, Double.parseDouble(node.split(",")[1]));
                }else {
                    childNode = new Node(node.split(",")[0], n,0, 0);
                }
                for (String finalStateNode : finalNodes) {
                    if (childNode.getState().equals(finalStateNode)) {
                        visited.add(childNode.getState());
                        pathFinder = startingStateNode;
                        childNode.getFinalPath(childNode);
                        return true;
                    }
                }
                //if (!visited.contains(childNode.getState()))
                open.addLast(childNode);
            }
        }
        return false;
    }
}
