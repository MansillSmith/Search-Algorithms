/*
Mansill Smith
ID: 1341291

Alex Grant
ID: 1350168

Solve a given text file map using the A* method
*/
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;

public class AStar{
    static int numInputs = 1;

    //Records a state
    private class State{
        private int x;
        private int y;
        private double distFromGoal;

        //Constructs a State
        public State(int x, int y, int distFromGoal){
            this.x = x;
            this.y = y;
            this.distFromGoal = distFromGoal;
        }

        public int GetX(){
            return x;
        }

        public int GetY(){
            return y;
        }

        public double GetDistFromGoal(){
            return distFromGoal;
        }
    }

    //Records a path
    private class Path{
        private LinkedList<State> listOfStates;

        //Constructs a path
        public Path(State s){
            listOfStates = new LinkedList<State>();
            listOfStates.add(s);
        }

        //Adds another state to a path
        public void AddState(State s){
            listOfStates.add(s);
        }
    }


    public static void main(String[] args) {
        if(args.length != numInputs){
            System.err.println("Please input the txt map file");
        }
        else{
            LinkedList<String> masterList = new LinkedList<String>();
            String mapFile = args[0];

            try{
                //Opens the file
                BufferedReader reader = new BufferedReader(new FileReader(mapFile));
                String line = reader.readLine();
                while(line != null){
                    masterList.add(line);
                    line = reader.readLine();
                }
                reader.close();
            }
            catch(Exception e){
                System.err.println("Error: " + e);
            }

            //PrintMap2(masterList);

            AStar astar = new AStar();
            //Initialises the map list
            final State[][] map = astar.new State[masterList.size()][masterList.get(0).length()];

            //Finds the start and goal state
            State goalState;
            State startState;
            for(int i=0; i < masterList.size(); i++){
                String s = masterList.get(i);
                for(int j=0; j < s.length(); j++){
                    if (s.charAt(j).equals('G')){
                        goalState = astar.new State(j,i,0);
                    }
                    else if(s.charAt(j).equals('S')){
                        startState = astar.new State(j,i,0);
                    }
                }
            }

            //Will set the start state distance from the goal correctly
            //As the start state may have been found before the goal state
            if(startState.distFromGoal == 0){
                int thisX = startState.GetX;
                int thisY = startState.GetY;

                startState = astar.new State(thisX, thisY, CalculateDistance(thisX, thisY, goalState.GetX, goalState.GetY));
            }

            //Writes the linkedlist into a 2d array
            for(int i=0; i < masterList.size(); i++){
                String s = masterList.get(i);
                for(int j=0; j < s.length(); j++){
                    map[i][j] = astar.new State(j,i, CalculateDistance(j,i,goalState.GetX, goalState.GetY));
                }
            }
            PrintMap(map);
        }
    }

    public static void PrintMap2(LinkedList<String> map){
        for(int i = 0; i < map.size(); i++){
            System.out.println(map.get(i));
        }
    }

    public static void PrintMap(String[][] map){
        for(int i=0; i< map.length; i++){
            for(int j=0; j< map[0].length; j++){
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    private static void CalculateDistance(int x1, int y1, int x2, int y2){
        Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
    }
}