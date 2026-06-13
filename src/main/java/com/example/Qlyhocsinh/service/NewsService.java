package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.response.NewsResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NewsService {

    private static final String VNEXPRESS_EDU_RSS = "https://vnexpress.net/rss/giao-duc.rss";

    public List<NewsResponse> getEducationNews() {
        List<NewsResponse> newsList = new ArrayList<>();
        try {
            // 1. Gọi API lấy chuỗi XML từ VnExpress
            RestTemplate restTemplate = new RestTemplate();
            String xmlString = restTemplate.getForObject(VNEXPRESS_EDU_RSS, String.class);

            // 2. Parse chuỗi XML
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xmlString)));

            // 3. Lấy toàn bộ các bài báo (thẻ <item>)
            NodeList itemNodes = document.getElementsByTagName("item");

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element itemElement = (Element) itemNodes.item(i);

                String title = getElementValue(itemElement, "title");
                String link = getElementValue(itemElement, "link");
                String description = getElementValue(itemElement, "description");

                // 4. Trích xuất link ảnh từ thẻ description
                String imageUrl = extractImageUrl(description);

                newsList.add(new NewsResponse(title, link, imageUrl));
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đọc RSS VnExpress: " + e.getMessage());
        }
        return newsList;
    }

    // Hàm phụ trợ lấy text từ thẻ XML
    private String getElementValue(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    // Hàm phụ trợ dùng Regex để móc thẻ <img src="..."> ra khỏi description
    private String extractImageUrl(String description) {
        String imgRegex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        Pattern pattern = Pattern.compile(imgRegex);
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return matcher.group(1); // Trả về URL ảnh
        }
        return "https://via.placeholder.com/300x180?text=No+Image"; // Ảnh mặc định nếu bài không có ảnh
    }
}