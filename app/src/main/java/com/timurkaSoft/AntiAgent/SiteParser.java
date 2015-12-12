package com.timurkaSoft.AntiAgent;

import org.htmlcleaner.TagNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuMoH on 04.04.2015.
 */
public class SiteParser {

    public static List<ShortInfo> getShortInfoList(String url) throws Exception {
        List<ShortInfo> elements = new ArrayList<>();
        HtmlHelper hh = new HtmlHelper(new URL(url));
        List<TagNode> links = hh.getLinksByClass("b-link b-link_redir_yes b-serp-item__offer-link");
        List<TagNode> prices = hh.getPriceByClass("b-serp-item__amount");
        List<TagNode> photo = hh.getPriceByClass("b-serp-item__photo-cover");

        for (int i = 0; i < links.size(); i++) {
            ShortInfo element = new ShortInfo.Builder()
                    .setText(links.get(i).getText().toString())
                    .setPrice(prices.get(i).getText().toString())
                    .setPhoto(photo.get(i).getText().toString())
                    .setHref(cleanHref(links.get(i).getAttributes().toString()))
                    .build();
            elements.add(element);
        }
        return elements;
    }

    private static String cleanHref(String href) {
        StringBuilder cleanHref = new StringBuilder();
        for (int i = 6; i < href.length(); i++) {
            if (href.charAt(i) == ',') break;
            cleanHref.append(href.charAt(i));
        }
        return cleanHref.toString();
    }

}
