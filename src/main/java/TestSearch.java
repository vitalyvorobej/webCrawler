import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TestSearch {
    public static void main(String[] args) {
        Set<String> allPages = new HashSet<>();

  /*      try {
            int maxVisitedPages = 10;
            String rootUrl = "https://en.wikipedia.org/wiki/Elon_Musk";
            Connection connection = Jsoup.connect(rootUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
            Document htmlDocument = connection.get();
            Elements linksOnPage = htmlDocument.select("a[href]");
            if (connection.response().statusCode() == 200) {
                System.out.println("Received web page at " + rootUrl);
            }
            for (Element link : linksOnPage) {
                allPages.add(link.absUrl("href"));
                if (allPages.size() >= maxVisitedPages) {
                    break;
                }
            }
            for (String l : allPages) {
                System.out.println(l);
            }
        } catch (IOException e) {
            System.out.println("Error in out HTTP request " + e);
        }
        LinkedList<String> ww = new LinkedList<>(allPages);
        List<String> list = new LinkedList<>();
        list.add("Elon");
        list.add("Tesla");
        list.add("Gigafactory");
        list.add("Elon Musk");

        searchWords(ww, list);
    }*/

    /*static void searchWords(LinkedList<String> linksForSearch, List<String> words) {
        HashMap<String, Map<String, Integer>> resultMap = new HashMap<>();
        try {

            for (int i = 0; i < linksForSearch.size(); i++) {
                Connection connection = Jsoup.connect(linksForSearch.get(i)).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0").timeout(100);
                Document htmlDocument = connection.get();
                String bodyText = htmlDocument.body().text();
                HashMap<String, Integer> coincidenceMap = new HashMap<>();
                for (int j = 0; j < words.size(); j++) {
                    coincidenceMap.put(words.get(j), KMPSearch(bodyText, words.get(j)).size());
                    if (j + 1 >= words.size()) {
                        int counter = 0;
                        for (int value : coincidenceMap.values()) {
                            counter = counter + value;
                        }
                        coincidenceMap.put("Total hits", counter);
                    }

                }
                coincidenceMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(System.out::println);
                resultMap.put(linksForSearch.get(i), coincidenceMap);


            }

        } catch (IOException e) {
            System.out.println(e);

        }
        printResult(resultMap);

    }
*/
  /*  private static void printResult(HashMap<String, Map<String, Integer>> resultMap) {
        resultMap.forEach((k, v) ->
                System.out.println(k + " - " +
                        v.entrySet().stream()
                                .map(e -> e.getKey() + " = " + e.getValue())
                                .collect(Collectors.joining(", "))
                ));
    }*/

    /*static int[] prefixFunction(String sample) {
        int[] values = new int[sample.length()];
        for (int i = 1; i < sample.length(); i++) {
            int j = 0;
            while (i + j < sample.length() && sample.charAt(j) == sample.charAt(i + j)) {
                values[i + j] = Math.max(values[i + j], j + 1);
                j++;
            }
        }
        return values;
    }*/

    /*public static ArrayList<Integer> KMPSearch(String text, String sample) {
                ArrayList<Integer> found = new ArrayList<>();

                int[] prefixFunc = prefixFunction(sample);

                int i = 0;
                int j = 0;

                while (i < text.length()) {
                    if (sample.charAt(j) == text.charAt(i)) {
                        j++;
                        i++;
                    }
                    if (j == sample.length()) {
                        found.add(i - j);
                        j = prefixFunc[j - 1];
                    } else if (i < text.length() && sample.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = prefixFunc[j - 1];
                } else {
                    i = i + 1;
                }
            }
        }

        return found;
    }*/
    }
}
