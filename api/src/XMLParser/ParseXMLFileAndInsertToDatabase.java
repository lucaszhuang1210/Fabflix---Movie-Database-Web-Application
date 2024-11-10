package XMLParser;


public class ParseXMLFileAndInsertToDatabase {

    public static void main(String[] args) {
        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "java:comp/env/jdbc/moviedb";
        String errorFile = "inconsistency_entries.txt";

        ActorParser actorParser = new ActorParser(loginUser, loginPasswd, loginUrl, errorFile);
        actorParser.parse("actors.xml");
    }
}