
/*
Mansill Smith
ID: 1341291

Alex Grant
ID: 1350168

Calculate the shortest equation only consisting of fours for a given number
*/
import java.util.Queue;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class Fours{
    //The number of inputs
    static int numInputs = 1;

    //Stores the information about a state
    private class State{
        //Stores the equation
        private String equation;
        //Stores if the state is deemed legal
        public Boolean legal;

        //Constructs a state object
        public State(String equation){
            this.equation=equation;
            this.legal = true;
        }

        //Expands the state
        public void Expand(Queue<State> queue){
            if(legal){
                queue.add(new State(this.equation + "+4"));
                queue.add(new State(this.equation + "-4"));
                queue.add(new State(this.equation + "*4"));
                queue.add(new State(this.equation + "/4"));
                //queue.add(new State(this.equation + "**4"));
                queue.add(new State(this.equation + "4"));
                queue.add(new State(this.equation + ".4"));
                queue.add(new State("(" + this.equation + ")"));
            }
        }

        //Gets the equation
        public String getEquation(){
            return equation;
        }
    }

    public static void main(String[] args){
        //If the user entered the wrong arguements
        if(!validInput(args, numInputs)){
            System.err.println("The input is a number");
        }
        else{
            //Gets the inputted number
            double input = Double.parseDouble(args[0]);

            //Initialise the equation parser
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");

            //Creates the state
            Queue<State> queue = new LinkedList<State>();

            //Generates the first state
            Fours fours = new Fours();
            queue.add(fours.new State("4"));
            
            String equation = "";
            while(equation.equals("")){
                //Get a state
                State state = null;
                try{
                    state = queue.remove();
                }
                catch (NoSuchElementException e){
                    System.err.println("The queue is somehow empty");
                }

                //Is this the goal state?
                if(IsThisAGoal(state, input, engine)){
                    equation = state.getEquation();
                }
                else{
                    //expand them, add to the queue
                    try{
                        state.Expand(queue);
                    }
                    catch(OutOfMemoryError e){
                        break;
                    }
                }              
            }

            //Checks the reason why the loop stopped
            if(equation.equals("")){
                System.out.println("Ran out of memory");
            }
            else{
                System.out.println(equation);   
            }          
        }
    }

    //Tests if the program has valid inputs
    public static Boolean validInput(String[] args, int numInputs){
        if(args.length == numInputs){
            try{
                Double.parseDouble(args[0]);
                return true;
            }
            catch(Exception e){
                return false;
            }
        }
        return false;
    }

    //Calculates if the state is the goal
    public static Boolean IsThisAGoal(State s, double value, ScriptEngine engine){
        try{
            return Double.parseDouble(engine.eval(s.getEquation()).toString()) == value;
        }
        catch(Exception e){
            //System.err.println("ERROR: " + e);
            s.legal = false;
            return false;
        }
    }
}