/*
Mansill Smith
ID: 1341291

Alex Grant
ID: 1350168

Solve a given text file map using the A* method
*/
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class AStar{
    static int numInputs = 1;

    //A queue which orders its contents based on a value
    private class PriorityQueue{
        private ArrayList<Path> queue;

        public PriorityQueue(){
            queue = new ArrayList<Path>();
        }

        //Adds a new path to the queue
        public void Add(Path p){
            double value = p.GetTotalCost();
            for (int i = 0; i < queue.size(); i++){
                //If the added path should go here
                if (value < queue.get(i).GetTotalCost()){
                    queue.add(i,p);
                    return;
                }
            }
            //The cost of this path was greater than all other paths currently in the queue
            queue.add(p);
        }

        //Removes the first path from the queue
        public Path Remove(){
            return queue.remove(0);
        }

        public ArrayList<Path> GetListOfPaths(){
            return queue;
        }
    }

    //Records a state
    private class State{
        private String character;
        private int x;
        private int y;
        public double distFromGoal;

        //Constructs a State
        public State(int x, int y, String character){
            this.x = x;
            this.y = y;
            this.distFromGoal = 0;
            this.character = character;
        }

        public int GetX(){
            return x;
        }

        public int GetY(){
            return y;
        }

        public String GetCharacter(){
            return character;
        }

        public String SetCharacter(String character){
            return this.character = character;
        }

        //Expands the state into the frontier
        public void Expand(PriorityQueue frontier, Path thisPath, ArrayList<ArrayList<State>> map){
            //Generate the new paths
            ArrayList<Path> newPaths = new ArrayList<Path>();            

            //
            //Check if the values are too large or too small
            //

            newPaths.add(new Path(thisPath, map.get(this.GetY()).get(this.GetX() -1)));
            newPaths.add(new Path(thisPath, map.get(this.GetY()).get(this.GetX() + 1)));
            newPaths.add(new Path(thisPath, map.get(this.GetY() -1).get(this.GetX())));
            newPaths.add(new Path(thisPath, map.get(this.GetY() +1).get(this.GetX())));

            ArrayList<Integer> pathsToRemove = new ArrayList<Integer>();
            //Check if the paths are valid
            for(int i = 0; i < newPaths.size(); i++){
                //remove the invalid paths
                if(!newPaths.get(i).IsValid(frontier)){
                    pathsToRemove.add(i);
                }
            }

            //Removes the invalid paths
            for(int i = 0; i < pathsToRemove.size(); i++){
                newPaths.remove(pathsToRemove.get(i));
            }

            //Add the new paths to the frontier
            for(int i = 0; i < newPaths.size(); i++){
                frontier.Add(newPaths.get(i));
            }              
        }

        //Checks if the State is a valid move
        public Boolean IsValidMove(){
            return (GetCharacter().equals(" ") || GetCharacter().equals("S") || GetCharacter().equals("G"));
        }

        @Override
        public String toString(){
            return GetX() + ", " + GetY() + "," + GetCharacter();
        }
    }

    //Records a path
    private class Path{
        private ArrayList<State> listOfStates;

        //Constructs a path
        public Path(State s){
            listOfStates = new ArrayList<State>();
            listOfStates.add(s);
        }

        //Create a new path from another path
        public Path(Path p, State s){
            listOfStates = p.GetListOfStates();
            this.AddState(s);
        }

        public ArrayList<State> GetListOfStates(){
            return listOfStates;
        }

        //Adds another state to a path
        public void AddState(State s){
            listOfStates.add(s);
        }

        //Calculates the total cost of the Path
        public double GetTotalCost(){
            return listOfStates.size() + GetLastState().distFromGoal;
        }

        //Calculates if the path contains the goal
        public Boolean ContainsGoal(){
            return GetLastState().GetCharacter().equals("G");
        }

        //Expands the path
        public void Expand(PriorityQueue frontier, ArrayList<ArrayList<State>> map){
            GetLastState().Expand(frontier, this, map);
        }

        //Calculates if the path is valid
        public Boolean IsValid(PriorityQueue frontier){
            //If the final state of the path is a valid move
            //And if the path does not contain a loop
            //And the path is unique
            return (GetLastState().IsValidMove() && !DoesThePathLoop() && UniquePath(frontier));
        }

        private State GetLastState(){
            return listOfStates.get(listOfStates.size() -1);
        }

        //Checks if the path loops on itself
        private Boolean DoesThePathLoop(){
            State s = listOfStates.get(listOfStates.size() -1);
            for(int i = 0; i < listOfStates.size() - 1; i++){
                if(s.equals(listOfStates.get(i))){
                    return true;
                }
            }
            return false;
        }

        //Checks that no paths overlap
        private Boolean UniquePath(PriorityQueue frontier){
            ArrayList<Path> listOfPaths = frontier.GetListOfPaths();
            for(int i = 0; i < listOfPaths.size(); i++){
                //If the last state of this path is contained in any other path
                if(listOfPaths.get(i).listOfStates.contains(this.GetLastState())){
                    //Remove the path with the highest total cost, return if this had the lowest
                    if(this.GetTotalCost() < listOfPaths.get(i).GetTotalCost()){
                        listOfPaths.remove(i);
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            }
            return true;
        }
    }


    public static void main(String[] args) {
        if(args.length != numInputs){
            System.err.println("Please input the txt map file");
        }
        else{
            ArrayList<ArrayList<State>> map = new ArrayList<ArrayList<State>>();
            String mapFile = args[0];

            //Finds the start and goal state
            State goalState = null;
            State startState = null;

            AStar astar = new AStar();

            try{
                //Opens the file
                BufferedReader reader = new BufferedReader(new FileReader(mapFile));
                int numlines = 0;

                String line = reader.readLine();
                while(line != null){
                    ArrayList<State> temp = new ArrayList<State>();
                    for(int i = 0; i < line.length(); i++){
                        String c = Character.toString(line.charAt(i));

                        //Sets a special reference to the start and end states
                        State s = astar.new State(i, numlines, c);
                        if(c.equals("G")){
                            goalState = s;
                        }
                        else if(c.equals("S")){
                            startState = s;
                        }
                        temp.add(s);

                    }
                    map.add(temp);
                    line = reader.readLine();
                    numlines ++;
                }
                reader.close();
            }
            catch(Exception e){
                System.err.println("Error: " + e);
            }

            //Set all the heuristic value for the states
            for (int y = 0; y < map.size(); y++){
                ArrayList<State> listOfStates = map.get(y);
                for(int x = 0; x < listOfStates.size(); x++){
                    State thisState = listOfStates.get(x);
                    thisState.distFromGoal = CalculateDistance(thisState.GetX(), thisState.GetY(), goalState.GetX(), goalState.GetY());
                }
            }

            //Stores the frontier of the search algorithm
            PriorityQueue frontier = astar.new PriorityQueue();
            //Initialise the frontier
            frontier.Add(astar.new Path(startState));

            Path correctPath = null;
            //Loop until the answer has been found
            while (correctPath == null){
                //Get a path from the frontier
                Path p = frontier.Remove();

                //Check that this path doesn't contain the goal
                if(p.ContainsGoal()){
                    correctPath = p;
                }
                else{
                    p.Expand(frontier, map);
                    PrintFrontier(frontier);
                    break;
                }
            }

            if(correctPath != null){
                ArrayList<State> listOfCorrectStates = correctPath.listOfStates;
                for(int i = 0; i< listOfCorrectStates.size(); i++){
                    if(!listOfCorrectStates.get(i).GetCharacter().equals("G") && !listOfCorrectStates.get(i).GetCharacter().equals("S")){
                        listOfCorrectStates.get(i).SetCharacter("·");
                    }
                }
            }
            else{
                System.err.println("A Path was not found");
            }
            


            PrintMap(map);
            //PrintHeuristic(map);
        }
    }

    private static void PrintFrontier(PriorityQueue frontier){
        ArrayList<Path> listOfPaths = frontier.GetListOfPaths();
        for(int i = 0; i <listOfPaths.size(); i++){
            ArrayList<State> listOfStates = listOfPaths.get(i).listOfStates;
            for(int j = 0; j < listOfStates.size(); j++){
                System.out.print(listOfStates.get(j).toString() + " | ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();
    }

    public static void PrintMap(ArrayList<ArrayList<State>> list){
        for (int y = 0; y < list.size(); y++){
            ArrayList<State> listOfStates = list.get(y);
            for(int x = 0; x < listOfStates.size(); x++){
                State thisState = listOfStates.get(x);
                System.out.print(thisState.GetCharacter());
            }
            System.out.println();
        }
    }

    public static void PrintHeuristic(ArrayList<ArrayList<State>> list){
        for (int y = 0; y < list.size(); y++){
            ArrayList<State> listOfStates = list.get(y);
            for(int x = 0; x < listOfStates.size(); x++){
                State thisState = listOfStates.get(x);
                System.out.print(Math.round(thisState.distFromGoal) + ",");
            }
            System.out.println();
        }
    }

    //Calculates the distance from a given state to the goal state
    private static double CalculateDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
    }
}