import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WebCrawlerV4 {
    static Scanner scanner;
    private static Set<String> allLink = new HashSet<>();
    private static LinkedList<String> filtredLink = new LinkedList<>();

    public static void main(String[] args) {
        getAllLink(getUserInputRootResource());
        filterLinkByDepth(allLink);
        searchWords(filtredLink, getUserInputSeachingWords());
    }


    static void searchWords(LinkedList<String> linksForSearch, List<String> wordForSearch) {
//сделать фильтрацию на вхождения, подсунуть отфильтрованые URL в connection url.substring(0, url.lastIndexOf('/'));

        HashMap<String, Map<String, Integer>> resultMap = new HashMap<>();
        try {

            for (int i = 0; i < linksForSearch.size(); i++) {
                Connection connection = Jsoup.connect(linksForSearch.get(i)).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0").timeout(100).ignoreHttpErrors(true);
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
                resultMap.put(linksForSearch.get(i), coincidenceMap);
            }
        } catch (Exception e) {
            System.out.println(e);

        }
        printResult(resultMap);
    }

    static LinkedList<String> filterLinkByDepth(Set<String> allLinkForScanning) {
        int maxDepth = 2;
        LinkedList<String> allP = new LinkedList<>(allLinkForScanning);
        Set<String> ad = new HashSet<>();
        try {

            for (int i = 0; i < allP.size(); i++) {

                Connection connection = Jsoup.connect(allP.get(i)).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0").timeout(100);
                Document htmlDocument = connection.get();
                Elements linksOnPage = htmlDocument.select("a[href]");
                if (connection.response().statusCode() == 200) {
                    System.out.println("Received web page at " + allP.get(i));
                }
                for (Element link : linksOnPage) {
                    if (ad.size() < maxDepth) {
                        ad.add(link.absUrl("href"));
                        if (!filtredLink.contains(ad)) {
                            filtredLink.add(String.valueOf(ad));
                        }
                    } else {
                        ad.clear();
                        break;
                    }
                }

            }
            /*for (String l : filtredLink) {
                System.out.println(l);
            }*/

        } catch (IOException e) {
            System.out.println(e);
        }
        return filtredLink;
    }


    static Set<String> getAllLink(String rootUrl) {
        int maxVisitedPages = 5;
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
        return allLink;
    }

    static int getUserInputMaxVisitedLink() {
        int maxVisitedPage = 5;
        /*scanner = new Scanner(System.in);
        System.out.println("Enter the maximum number of pages visited. Default value = 10_000");
        maxVisitedPage = scanner.nextInt();*/
        return maxVisitedPage;
    }

    static int getUserInputLinkDepth() {
        int defaultLinkDepth = 2;
/*        scanner = new Scanner(System.in);
        System.out.println("Enter search depth. Default value = 8");
        defaultLinkDepth = scanner.nextInt();*/
        return defaultLinkDepth;
    }

    static LinkedList<String> getUserInputSeachingWords() {
        LinkedList<String> searching = new LinkedList<>();
        /*scanner = new Scanner(System.in);
        System.out.println("Enter the words you want to find on the page. To end the list, enter the command" + " \"done\"");
        while (true) {
            String input = scanner.nextLine();
            if (input.toLowerCase().equals("done"))
                break;
            searching.add(input);
        }*/
        searching.add("Elon");
        searching.add("Elon Musk");
        searching.add("Tesla");
        searching.add("Gigafactory");
        return searching;
    }

    static String getUserInputRootResource() {
        String defaultLink = "https://en.wikipedia.org/wiki/Elon_Musk";
        /*System.out.println("please enter the link what you want to crawling. Example : https://en.wikipedia.org/wiki/Elon_Musk");
        scanner = new Scanner(System.in);
        defaultLink = scanner.nextLine();*/
        return defaultLink;
    }

    private static void printResult(HashMap<String, Map<String, Integer>> resultMap) {
        resultMap.forEach((k, v) ->
                System.out.println(k + " - " +
                        v.entrySet().stream()
                                .map(e -> e.getKey() + " = " + e.getValue())
                                .collect(Collectors.joining(", "))
                ));
    }

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
