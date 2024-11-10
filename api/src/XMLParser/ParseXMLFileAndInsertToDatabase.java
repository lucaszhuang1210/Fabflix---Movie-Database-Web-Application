package XMLParser;


public class ParseXMLFileAndInsertToDatabase {

    public static void main(String[] args) {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "java:comp/env/jdbc/moviedb";
        String errorFile = "../logs/inconsistency_entries.txt";

        ActorParser actorParser = new ActorParser(loginUser, loginPasswd, loginUrl, errorFile);
        actorParser.parse("../../../stanford-movies/actors_test.xml");
    }
}