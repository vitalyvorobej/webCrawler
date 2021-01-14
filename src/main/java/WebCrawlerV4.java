import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WebCrawlerV4 {
    private static final Set<String> allLink = new HashSet<>();
    private static final LinkedList<String> filteredLink = new LinkedList<>();

    public static void main(String[] args) {
        getAllLink(getUserInputRootResource());
        filterLinkByDepth();
        searchWords(getUserInputSearchingWords());
    }

/**
 * method scanning filtered list @filteredLink searching needed words and printing result.
 * @ resultMap - result map contains data where (String) - web_resource, Map<String(contains words for searching),
 * Integer(counter of words).
 * HashMap<String(word for search),Integer(result of search)>coincidentMap - map with results of searching.
 * */
    static void searchWords(List<String> wordForSearch) {
//TODO сделать фильтрацию на вхождения, подсунуть отфильтрованые URL в connection url.substring(0, url.lastIndexOf('/'));

        HashMap<String, Map<String, Integer>> resultMap = new HashMap<>();
        try {

            for (int i = 0; i < WebCrawlerV4.filteredLink.size(); i++) {
                /*Connection connection = Jsoup.connect(WebCrawlerV4.filteredLink.get(i)
                        .substring(0, WebCrawlerV4.filteredLink.get(i).lastIndexOf(":")))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");*/
                String url = filteredLink.get(i).substring(1,filteredLink.get(i).lastIndexOf("#"));
                System.out.println(url);
                Connection connection = Jsoup.connect(url);
                Document htmlDocument = connection.get();
                String bodyText = htmlDocument.body().text();
                HashMap<String, Integer> coincidenceMap = new HashMap<>();
                for (int j = 0; j < wordForSearch.size(); j++) {
                    coincidenceMap.put(wordForSearch.get(j), KMPSearch(bodyText, wordForSearch.get(j)).size());
                    if (j + 1 >= wordForSearch.size()) {
                        int counter = 0;
                        for (int value : coincidenceMap.values()) {
                            counter = counter + value;
                        }
                        coincidenceMap.put("Total hits", counter);
                    }

                }
                resultMap.put(WebCrawlerV4.filteredLink.get(i), coincidenceMap);
            }
        } catch (Exception e) {
          e.printStackTrace();

        }
        printResult(resultMap);
    }
/**
 * searching all link by depth and return result list of sorted links
 * */
    static void filterLinkByDepth() {
        int maxDepth = 2;
        LinkedList<String> allPagesForScan = new LinkedList<>(WebCrawlerV4.allLink);
        Set<String> chekIsNotUsedLink = new HashSet<>();
        try {

            for (String s : allPagesForScan) {

                Connection connection = Jsoup.connect(s).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
                Document htmlDocument = connection.get();
                Elements linksOnPage = htmlDocument.select("a[href]");
                if (connection.response().statusCode() == 200) {
                    System.out.println("Received web page at " + s);
                }
                for (Element link : linksOnPage) {
                    if (chekIsNotUsedLink.size() < maxDepth) {
                        chekIsNotUsedLink.add(link.absUrl("href"));
                        if (!filteredLink.contains(chekIsNotUsedLink.toString())) {
                            filteredLink.add(String.valueOf(chekIsNotUsedLink));
                        }
                    } else {
                        chekIsNotUsedLink.clear();
                        break;
                    }
                }

            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

/**
 * Scanning rootUrl and returned all found links with @Param maxVisitedPages
 * @ Return Set<String> allLink
 * */
    static void getAllLink(String rootUrl) {
        int maxVisitedPages = 5;
        try {
            Connection connection = getConnection(rootUrl);
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
 * Getting connection using JSOUP
 * */
    private static Connection getConnection(String rootUrl) {
        return Jsoup.connect(rootUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0");
    }


/**
 * Return list with words for search.
 * */
    static LinkedList<String> getUserInputSearchingWords() {
        LinkedList<String> searchingWords = new LinkedList<>();
        searchingWords.add("Elon");
        searchingWords.add("Elon Musk");
        searchingWords.add("Tesla");
        searchingWords.add("Gigafactory");
        return searchingWords;
    }
/**
 * Return root url
 * */
    static String getUserInputRootResource() {
        return "https://en.wikipedia.org/wiki/Elon_Musk";
    }
/**
 * Method print final result map with name of web page,words,counter for words
 * */
    private static void printResult(HashMap<String, Map<String, Integer>> resultMap) {
        resultMap.forEach((k, v) ->
                System.out.println(k + " - " +
                        v.entrySet().stream()
                                .map(e -> e.getKey() + " = " + e.getValue())
                                .collect(Collectors.joining(", "))
                ));
    }
/**
 * Method for KMPSearch
 * */
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
 * */
    public static ArrayList<Integer> KMPSearch(String text, String sample) {
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
