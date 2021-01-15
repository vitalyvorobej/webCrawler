import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class WebCrawler {
    private static final Set<String> allLink = new HashSet<>();
    private static final LinkedList<String> filteredLink = new LinkedList<>();

    public static void main(String[] args) {
        findAllLinkOnTheRootURL();
        filteringAllLinksByDepth();
        findMatchesInTextWriteFileAndPrintResult(getUserInputSearchingWords());
    }

    /**
     * Searches for word matches in filtered links. Calls the method for printing the result and writing to a file
     *
     * @param wordForSearch - List of words for search
     * @value resultMap - result map contains data where (String) - web_resource, Map<String(contains words for searching),Integer(counter of words).
     * @value coincidenceMap - HashMap <String (name_of_word_for_search),Integer (hit counter)> coincidentMap - map with results of searching.
     * @value collect - sorts List of resultMap
     */
    static void findMatchesInTextWriteFileAndPrintResult(List<String> wordForSearch) {
        HashMap<String, Map<String, Integer>> resultMap = new HashMap<>();
        try {

            for (int i = 0; i < WebCrawler.filteredLink.size(); i++) {
                String url = filteredLink.get(i);
                Connection connection = Jsoup.connect(url);
                Document htmlDocument = connection.get();
                String bodyText = htmlDocument.body().text();
                HashMap<String, Integer> coincidenceMap = new HashMap<>();
                for (int j = 0; j < wordForSearch.size(); j++) {
                    coincidenceMap.put(wordForSearch.get(j), getResultOfKMPSearch(bodyText, wordForSearch.get(j)).size());
                    if (j + 1 >= wordForSearch.size()) {
                        int counter = 0;
                        for (int value : coincidenceMap.values()) {
                            counter = counter + value;
                        }
                        coincidenceMap.put("Total hits", counter);
                    }

                }
                resultMap.put(WebCrawler.filteredLink.get(i), coincidenceMap);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        LinkedHashMap<String, Map<String, Integer>> collect = resultMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(
                        Comparator.comparingInt(v ->
                                v.values().stream().mapToInt(Integer::intValue).sum()
                        )
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));


        writeFile(collect);
        printResult(collect);

    }

    /**
     * Writing to csv file format
     *
     * @param collect - takes as input a sorted List for writing
     */
    static void writeFile(LinkedHashMap<String, Map<String, Integer>> collect) {
        try (Writer writer = new FileWriter("d:\\logs\\resultOfWebCrawling.csv")) {
            for (Map.Entry<String, Map<String, Integer>> entry : collect.entrySet()) {
                writer.append(entry.getKey())
                        .append('-')
                        .append("line.separator");
                writer.append(entry.getValue()
                        .toString())
                        .append(',')
                        .append("line.separator");

            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Method for searching links by depth and adding to LinkedList<String> allLink
     *
     * @value maxDepth - max depth for searching from start url
     * @value depth - counter for start url
     */
    static void filteringAllLinksByDepth() {
        int maxDepth = 2;
        int depth = 0;
        try {

            for (String s : allLink) {

                Connection connection = Jsoup.connect(s).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
                Document htmlDocument = connection.get();
                Elements linksOnPage = htmlDocument.select("a[href]");
                if (connection.response().statusCode() == 200) {
                    System.out.println("Received web page at " + s);
                }
                for (Element link : linksOnPage) {
                    if (depth < maxDepth) {
                        depth++;
                        filteredLink.add(link.absUrl("href"));
                    } else {
                        depth = 0;
                        break;
                    }
                }

            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * Finds all links at a given URL
     * searches all links from the start page and adds them to @value allLink
     *
     * @value rootUrl - start page for search
     * @value maxVisitedPages - search width
     */
    static void findAllLinkOnTheRootURL() {
        String rootUrl = "https://en.wikipedia.org/wiki/Elon_Musk";
        int maxVisitedPages = 10;
        try {
            Connection connection = Jsoup.connect(rootUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
            Document htmlDocument = connection.get();
            Elements linksOnPage = htmlDocument.select("a[href]");
            if (connection.response().statusCode() == 200) {
                System.out.println("Received web page at " + rootUrl);
            }
            for (Element link : linksOnPage) {
                allLink.add(link.absUrl("href"));
                if (allLink.size() >= maxVisitedPages) {
                    break;
                }
            }
            for (String l : allLink) {
                System.out.println(l);
            }
        } catch (IOException e) {
            System.out.println("Error in out HTTP request " + e);
        }
    }


    /**
     * Returns a sheet of words to search for
     *
     * @return LinkedList<String>searchingWords - list of words for search
     */
    static LinkedList<String> getUserInputSearchingWords() {
        LinkedList<String> searchingWords = new LinkedList<>();
        searchingWords.add("Elon");
        searchingWords.add("Elon Musk");
        searchingWords.add("Tesla");
        searchingWords.add("Gigafactory");
        return searchingWords;
    }

    /**
     * Prints the final sheet with the page name and hit count
     *
     * @param resultMap - a sheet containing a list of all results
     */
    private static void printResult(HashMap<String, Map<String, Integer>> resultMap) {
        resultMap.forEach((k, v) ->
                System.out.println(k + " - " +
                        v.entrySet().stream()
                                .map(e -> e.getKey() + " = " + e.getValue())
                                .collect(Collectors.joining(", "))
                ));

    }

    /**
     * Algorithm for calculating the value of a prefix function
     * Using for KMPSearch
     */
    static int[] prefixFunction(String sample) {
        int[] values = new int[sample.length()];
        for (int i = 1; i < sample.length(); i++) {
            int j = 0;
            while (i + j < sample.length() && sample.charAt(j) == sample.charAt(i + j)) {
                values[i + j] = Math.max(values[i + j], j + 1);
                j++;
            }
        }
        return values;
    }

    /**
     * Algorithm of Knuth-Morris-Pratt for searching words in text
     * https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm
     *
     * @return ArrayList<Integer> found - a sheet containing the positions of matches in the text
     */
    public static ArrayList<Integer> getResultOfKMPSearch(String text, String sample) {
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
    }
}
