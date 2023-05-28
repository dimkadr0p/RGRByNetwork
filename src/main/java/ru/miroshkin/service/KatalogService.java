package ru.miroshkin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.miroshkin.entities.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class KatalogService {

    public List<Product> parsing(String url, int fromPage, int upToPage) throws IOException {
        List<Document> documents = getExistingDocuments(url, fromPage, upToPage);
        Document document = unionDocuments(documents);

        return parsingHtml(document);
    }

    private List<Document> getExistingDocuments(String url, int fromPage, int upToPage) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        List<Document> documents = new ArrayList<>();

        for (int i = fromPage; i <= upToPage; i++) {
            Document document = getHtmlDocument(stringBuilder.append(url).append("page-").append(i).toString());
            if (document.select("div.model-short-div").isEmpty()) {
                return documents;
            }
            documents.add(document);
            stringBuilder.setLength(0);
        }

        return documents;
    }

    private Document unionDocuments(List<Document> documents) {
        Document combinedDoc = Jsoup.parse("");

        for (Document document : documents) {
            combinedDoc.body().append(document.body().html());
        }

        return Jsoup.parse(combinedDoc.html());
    }

    private List<Product> parsingHtml(Document document) {
        List<Product> products = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        Elements listProducts = document.select("div.model-short-div");
        final String HOST = "https://n-katalog.ru";

        for (Element product : listProducts) {
            String name = product.select("a").attr("title");
            String url = stringBuilder.append(HOST).append(product.select("a").attr("href")).toString();
            stringBuilder.setLength(0);
            String img = stringBuilder.append(HOST).append(product.select("div.list-img").select("a").select("img").attr("src")).toString();
            stringBuilder.setLength(0);

            Integer price = null;
            Elements getPrice = product.select("div.model-price-range").select("a").select("span");

            if (!getPrice.isEmpty()) {
                price = Integer.parseInt(getPrice.first().text());
            }

            products.add(new Product(name, price, url, img));
        }

        return products;
    }


    private Document getHtmlDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .get();

    }


}
