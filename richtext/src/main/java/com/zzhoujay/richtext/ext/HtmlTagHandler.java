package com.zzhoujay.richtext.ext;

import android.text.Editable;
import android.text.Html;
import android.widget.TextView;

import org.xml.sax.XMLReader;

import java.lang.ref.SoftReference;
import java.util.Stack;

/**
 * Created by zhou on 16-10-20.
 * 自定义标签的处理
 */
public class HtmlTagHandler implements Html.TagHandler {

    private Stack<Integer> stack;
    private Stack<Boolean> list;

    public HtmlTagHandler(TextView textView) {
        stack = new Stack<>();
        list = new Stack<>();
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            startTag(tag, output, xmlReader);
            stack.push(output.length());
        } else {
            int len;
            if (stack.isEmpty()) {
                len = 0;
            } else {
                len = stack.pop();
            }
            reallyHandler(len, output.length(), tag.toLowerCase(), output, xmlReader);
        }
    }

    private void startTag(String tag, Editable out, XMLReader reader) {
        switch (tag.toLowerCase()) {
            case "ul":
                list.push(true);
                out.append('\n');
                break;
            case "ol":
                list.push(false);
                out.append('\n');
                break;
            case "pre":
                break;
            default:
                break;
        }
    }

    private void reallyHandler(int start, int end, String tag, Editable out, XMLReader reader) {
        switch (tag.toLowerCase()) {
            case "ol":
            case "ul":
                out.append('\n');
                if (!list.isEmpty())
                    list.pop();
                break;
            default:
                break;
        }
    }
}
