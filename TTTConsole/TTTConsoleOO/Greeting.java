import java.util.Scanner;

public class Greeting {
    public static void show() {
        Scanner trans = new Scanner(System.in);
        System.out.println("WELCOME TO THE NON-OO TTT!");
        System.out.println("Press enter to continue");
        String transition = trans.nextLine();
        System.out.println("Proceeding to the game");
        transition = trans.nextLine();
        System.out.println("Have Fun!");
        transition = trans.nextLine();
    }
}
