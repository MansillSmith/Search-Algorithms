public class foursTesting {
    public static void main(String[] args){

        for(int i = 0; i <= 100; i++){
            String[] s = {Integer.toString(i)};
            System.out.print(i + ": ");
            Fours.main(s);
        }
    }
}