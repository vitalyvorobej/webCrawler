import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {

    private static final char DEFAULT__SEPARATOR = ',';

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT__SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        if (separators == ' ') {
            separators = DEFAULT__SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());


    }

}

class T {
    public static void main(String[] args) throws IOException {
        String csvFile = "d:\\logs\\resultOfWebCrawling.csv";
        FileWriter writer = new FileWriter(csvFile);
        CSVUtils.writeLine(writer, Arrays.asList("a", "b", "c", "d"));

        //custom separator + quote
        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ',', '"');

        //custom separator + quote
        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc,c"), '|', '\'');

        //double-quotes
        CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc\"c"));


        writer.flush();
        writer.close();
    }
}