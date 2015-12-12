package com.timurkaSoft.AntiAgent;


import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlHelper {
    TagNode rootNode;

    public HtmlHelper(URL htmlPage) throws IOException {
            HtmlCleaner cleaner = new HtmlCleaner();
            //Загружаем html код сайта
            rootNode = cleaner.clean(htmlPage);
    }

    public List<TagNode> getLinksByClass(String CSSClassname) {
        List<TagNode> linkList = new ArrayList<>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("a", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }

    public List<TagNode> getPriceByClass(String CSSClassname) {
        List<TagNode> linkList = new ArrayList<>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("span", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }

    public String getDivByClass(String CSSClassname) {
        String s = "";

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName("div", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName("class");
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType != null && classType.equals(CSSClassname))
            {
                s =linkElements[i].getText().toString();
            }
        }

        return s;
    }

    public List<String> getTel() {
        List<String> list = new ArrayList<>();
        TagNode elements[] = rootNode.getElementsByName("a", true);
        for (int i = 0; elements != null && i < elements.length; i++) {
            //получаем атрибут по имени
            String href = elements[i].getAttributes().toString();
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (href.contains("tel:")) {
                href = href.replace("{href=tel:", "");
                href = href.replace("}", "");
                String s = "";
                for (int j = 0; j < href.length(); j++) {
                    s += href.charAt(j);
                    if (j == 1) s += "(";
                    if (j == 4) s += ")";
                    if (j == 7) s += "-";
                    if (j == 9) s += "-";
                }
                list.add(s);
            }
        }
        return list;
    }

    public List<String> getImg() {
        List<String> list = new ArrayList<>();
        TagNode elements[] = rootNode.getElementsByName("img", true);
        for (int i = 0; elements != null && i < elements.length; i++) {
            //получаем атрибут по имени
            String href = elements[i].getAttributes().toString();
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (href.contains("photo")) {
                href = href.replace("{src=", "");
                href = href.replace("}", "");
                list.add(href);
            }
        }
        return list;
    }

    public boolean getResponse() {
        TagNode elements[] = rootNode.getAllElements(true);
        for (int i = 0; elements != null && i < elements.length; i++) {
            String response = String.valueOf(elements[i].getText());
            if (response.equals("ERROR")) {
                return true;
//                return false;
            }
        }
        return true;
    }
}
