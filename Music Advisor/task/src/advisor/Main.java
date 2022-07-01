package advisor;

import java.io.IOException;
import java.util.*;

public class Main {

    private static final AppHttpServer server = new AppHttpServer();
    public static final spotify spotifyServices = new spotify();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean flag = false;
        boolean flag1 = false;
        PrintPage printPage = null;
        String[] choice = scanner.nextLine().split(" ");

        do {
            switch (choice[0]) {

                case "featured":

                    if (flag) {
                        printPage.printFeatured(spotifyServices.featured());
                    } else {
                        System.out.println("Please, provide access for application.");
                    }
                    break;

                case "new":
                    if (flag) {
                        printPage.printFeatured(spotifyServices.new1());
                    } else {
                        System.out.println("Please, provide access for application.");
                    }
                    break;

                case "categories":
                    if (flag) {
                        printPage.print(spotifyServices.categories());
                    } else {
                        System.out.println("Please, provide access for application.");
                    }
                    break;

                case "playlists":
                    if (flag) {
                        StringBuilder category = new StringBuilder();
                        for(int i = 1; i < choice.length; i++){
                            category.append(choice[i]).append(" ");
                        }
                        printPage.printFeatured(spotifyServices.playlist(category.toString().trim()));
                    } else {
                        System.out.println("Please, provide access for application.");
                    }

                    break;

                case "auth": flag = true;
                try {

                    spotifyServices.authorize(args);
                    server.configureServer();
                    System.out.println("waiting for code...");
                    System.out.println("code received");
                    System.out.println("making http request for access_token...");
                    spotifyServices.getAccessToken();
                    printPage = new PrintPage(spotifyServices.page);

                } catch (Exception e) {
                    System.out.println("Exception: " + e);
                }

                System.out.println("Success!");
                break;

                case ("next"):
                    printPage.printNext();
                    break;

                case ("prev"):
                    printPage.printPrev();
                    break;
            }
            if (choice[0].equals("next") || choice[0].equals("prev")) {
                flag1 = true;
            }
            choice = scanner.nextLine().split(" ");
            if (flag1) {
                if (choice[0].equals("exit")) {
                    choice = scanner.nextLine().split(" ");
                }
            }
        } while(!choice[0].equals("exit"));

    }

}

