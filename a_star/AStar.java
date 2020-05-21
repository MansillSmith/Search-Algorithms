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
import java.text.DecimalFormat;

public class AStar{
    static int numInputs = 1;

    //A queue which orders its contents based on a value
    private class PriorityQueue{
        //Stores the paths in the queue
        private ArrayList<Path> queue;

        //Constructs a priority queue
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

        //Gets the list of paths from the queue
        public ArrayList<Path> GetListOfPaths(){
            return queue;
        }

        //Calculates if the queue is empty
        public Boolean IsEmpty(){
            return queue.size() == 0;
        }

        //Gets the size of the queue
        public int Size(){
            return queue.size();
        }
    }

    //Records a state
    private class State{
        //Records the character of the state
        private String character;
        //Records the position of the state
        private int x;
        private int y;
        //Records how far away from the goal this state is
        public double distFromGoal;

        //Constructs a State
        public State(int x, int y, String character){
            this.x = x;
            this.y = y;
            this.distFromGoal = 0;
            this.character = character;
        }

        //Gets the X coordinate of the state
        public int GetX(){
            return x;
        }

        //Gets the Y coordinate of the state
        public int GetY(){
            return y;
        }

        //Gets the character of the state
        public String GetCharacter(){
            return character;
        }

        //Gets the character of the state
        //Used at the end to mark the correct path
        public String SetCharacter(String character){
            return this.character = character;
        }

        //Expands the state into the frontier
        public void Expand(PriorityQueue frontier, Path thisPath, ArrayList<ArrayList<State>> map){
            //Generate the new paths
            ArrayList<Path> newPaths = new ArrayList<Path>();            

            newPaths.add(new Path(thisPath, map.get(this.GetY()).get(this.GetX() -1)));
            newPaths.add(new Path(thisPath, map.get(this.GetY()).get(this.GetX() + 1)));
            newPaths.add(new Path(thisPath, map.get(this.GetY() -1).get(this.GetX())));
            newPaths.add(new Path(thisPath, map.get(this.GetY() +1).get(this.GetX())));

            //For all the new paths
            for(int i = 0; i < newPaths.size(); i++){
                //If the path is not valid
                if(!newPaths.get(i).IsValid(frontier)){
                    //Remove the path
                    newPaths.remove(i);
                    i--;
                }
            }

            //Add the surviving new paths to the frontier
            for(int i = 0; i < newPaths.size(); i++){
                frontier.Add(newPaths.get(i));
            }              
        }

        //Checks if the State is a valid move
        public Boolean IsValidMove(){
            return (GetCharacter().equals(" ") || GetCharacter().equals("S") || GetCharacter().equals("G"));
        }

        //Prints out the values which define a state
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
            listOfStates = CopyList(p.GetListOfStates());
            this.AddState(s);
        }

        //Gets the list of states from the path
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

        //Gets the final state of the path
        private State GetLastState(){
            return listOfStates.get(listOfStates.size() -1);
        }

        //Checks if the path loops on itself
        private Boolean DoesThePathLoop(){
            State s = listOfStates.get(listOfStates.size() -1);
            for(int i = 0; i < listOfStates.size() - 1; i++){
                //If the final state exists within the list of of states
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
                    if(this.GetTotalCost() < listOfPaths.get(i).GetPathSubset(this.GetLastState()).GetTotalCost()){
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

        //Used for calculating which path gets removed when a collision occurs
        private Path GetPathSubset(State s){
            int index = this.listOfStates.indexOf(s);
            Path p = new Path(this.listOfStates.get(0));
            for(int i = 1; i <= index; i++){
                p.AddState(this.listOfStates.get(i));
            }
            return p;
        }

        //Creates a copy of the list of states
        private ArrayList<State> CopyList(ArrayList<State> list){
            ArrayList<State> l = new ArrayList<State>();
            for(int i = 0; i < list.size(); i++){
                l.add(list.get(i));
            }
            return l;
        }
    }


    public static void main(String[] args) {
        //If the user has inputted the wrong number of inputs
        if(args.length != numInputs){
            System.err.println("Please input the txt map file");
        }
        else{
            //Stores the map
            ArrayList<ArrayList<State>> map = new ArrayList<ArrayList<State>>();
            String mapFile = args[0];

            //Finds the start and goal state
            State goalState = null;
            State startState = null;

            //Used to create new objects
            AStar astar = new AStar();

            try{
                //Opens the file
                BufferedReader reader = new BufferedReader(new FileReader(mapFile));
                int numlines = 0;

                String line = reader.readLine();
                //While there are lines left in the file
                while(line != null){
                    ArrayList<State> temp = new ArrayList<State>();
                    //For each character in the line
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
                    //Adds the list of states to the map
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
            //For each list of states
            for (int y = 0; y < map.size(); y++){
                ArrayList<State> listOfStates = map.get(y);
                //For each state
                for(int x = 0; x < listOfStates.size(); x++){
                    State thisState = listOfStates.get(x);
                    thisState.distFromGoal = CalculateDistance(thisState.GetX(), thisState.GetY(), goalState.GetX(), goalState.GetY());
                }
            }

            //Stores the frontier of the search algorithm
            PriorityQueue frontier = astar.new PriorityQueue();
            //Initialise the frontier
            frontier.Add(astar.new Path(startState));

            //Loop until the answer has been found
            Path correctPath = null;
            while (correctPath == null){
                //Get a path from the frontier
                Path p = null;
                if(!frontier.IsEmpty()){
                    p = frontier.Remove();
                }
                else{
                    break;
                }

                //Check that this path doesn't contain the goal
                if(p.ContainsGoal()){
                    correctPath = p;
                }
                else{
                    p.Expand(frontier, map);
                    System.out.println(frontier.Size());
                    //PrintFrontier(frontier);
                }
            }
            System.out.println();
            PrintMap(map);
            System.out.println();
            //If the correct path was found
            if(correctPath != null){
                //Change all of the states which are in the correct path to a dot
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
        }
    }

    //Prints the frontier to the screen
    private static void PrintFrontier(PriorityQueue frontier){
            ArrayList<Path> listOfPaths = frontier.GetListOfPaths();
            //For each path
            for(int i = 0; i <listOfPaths.size(); i++){
                ArrayList<State> listOfStates = listOfPaths.get(i).listOfStates;
                //For each state
                for(int j = 0; j < listOfStates.size(); j++){
                    System.out.print(listOfStates.get(j).toString() + " | ");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
            System.out.println();
    }

    //Prints the map
    public static void PrintMap(ArrayList<ArrayList<State>> list){
        //For each list of states
        for (int y = 0; y < list.size(); y++){
            ArrayList<State> listOfStates = list.get(y);
            //for each state
            for(int x = 0; x < listOfStates.size(); x++){
                State thisState = listOfStates.get(x);
                System.out.print(thisState.GetCharacter());
            }
            System.out.println();
        }
    }

    //Prints out the Heuristic values for all of the states
    public static void PrintHeuristic(ArrayList<ArrayList<State>> list){
        for (int y = 0; y < list.size(); y++){
            ArrayList<State> listOfStates = list.get(y);
            for(int x = 0; x < listOfStates.size(); x++){
                State thisState = listOfStates.get(x);
                //System.out.print(Math.round(thisState.distFromGoal) + ",");
                DecimalFormat df = new DecimalFormat("##.###");
                System.out.print(df.format(thisState.distFromGoal) + ",");
            }
            System.out.println();
        }
    }

    //Calculates the distance from a given state to the goal state
    private static double CalculateDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
    }
}