package net.krusher.datalinks.handler.tasks;

import lombok.Setter;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LinkProcessorHelper {

    private final PageService pageService;

    @Setter
    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    public LinkProcessorHelper(PageService pageService) {
        this.pageService = pageService;
    }

    @Transactional
    public void processPage(Page page, List<String> titles) {
        String initialContent = page.getContent();
        Document doc = Jsoup.parse(page.getContent());
        processNodes(doc.body().childNodes(), titles);
        String after = doc.body().html();
        if (!initialContent.equals(after)) {
            page.setContent(after);
            pageService.save(page, null, null);
        }
    }

    public void processNodes(List<Node> nodes, List<String> titles) {
        for (Node node : nodes) {
            if (node instanceof TextNode textNode) {
                String text = textNode.text();
                String initialText = text;
                if (node.parent().hasAttr("href")) {
                    continue;
                }
                for (String title : titles) {
                    if (title.length() < 4) {
                        continue;
                    }
                    String regex = "\\b(" + Pattern.quote(title) + ")\\b";
                    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(text);
                    if (!matcher.find()) {
                        continue;
                    }
                    text = matcher.replaceAll("<a href=\"" + applicationUrl + "/page/" + title + "\">$1</a>");
                }
                if (initialText.equals(text)) {
                    continue;
                }
                Element newElement = Jsoup.parse(text).body();
                node.replaceWith(newElement);
            } else {
                processNodes(node.childNodes(), titles);
            }
        }
    }
}
