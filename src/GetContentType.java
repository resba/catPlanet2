import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class GetContentType {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Shows the value of the " +
                "\"content-type\" header field of a remote file.");
            System.out.println("Usage: GetContentType (url)");
            System.out.println(
                "Example: GetContentType http://www.yahoo.com");
        }
        else {
            URL url = new URL(args[0]);
            URLConnection connection = url.openConnection();
            System.out.println(connection.getContentType());
        }
    }
}
