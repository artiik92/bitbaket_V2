package com.example.artiik92.bitbucket;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by artiik92 on 28.01.2017.
 */

public class RssHendler extends DefaultHandler {

    private StringBuilder stringBuilder;
    private ArrayList<String> titles;
    private ArrayList<String> links;
    private ArrayList<String> descriptions;
    private ArrayList<String> dates;
    private String currentTitle;
    private String currentUrl;
    private String currentDescription;
    private String currentDate;
    private enum TAG {ENTRY, TITLE, URL, DESCRIPTION, DATE, NONE}
    private TAG currentTag;
    private Stack<TAG> tagStack;

    public RssHendler(ArrayList<String> titles, ArrayList<String> links,
                      ArrayList<String> descriptions, ArrayList<String> dates) {
        super();
        this.titles = titles;
        this.links = links;
        this.descriptions = descriptions;
        this.dates = dates;
        stringBuilder = new StringBuilder();
        tagStack = new Stack<TAG>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("entry") || qName.equals("item")) {
            currentTag = TAG.ENTRY;
        } else {
            if (qName.equals("title")) {
                currentTag = TAG.TITLE;
            } else if (qName.equals("link")) {
                currentTag = TAG.URL;
            } else if (qName.equals("description") || qName.equals("summary")) {
                currentTag = TAG.DESCRIPTION;
            } else if (qName.equals("pubDate") || qName.equals("published")) {
                currentTag = TAG.DATE;
            } else {
                currentTag = TAG.NONE;
            }
            stringBuilder = new StringBuilder();
        }
        tagStack.push(currentTag);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        currentTag = tagStack.peek();
        switch (currentTag) {
            case ENTRY:
                titles.add(currentTitle);
                links.add(currentUrl);
                descriptions.add(currentDescription);
                dates.add(currentDate);
                break;
            case TITLE:
                if (stringBuilder.length() > 0) {
                    currentTitle = stringBuilder.toString();
                }
                break;
            case URL:
                if (stringBuilder.length() > 0) {
                    currentUrl = stringBuilder.toString();
                }
                break;
            case DESCRIPTION:
                if (stringBuilder.length() > 0) {
                    currentDescription = stringBuilder.toString();
                }
                break;
            case DATE:
                if (stringBuilder.length() > 0) {
                    currentDate = stringBuilder.toString();
                }
                break;
        }
        tagStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        stringBuilder.append(ch, start, length);
    }

}
