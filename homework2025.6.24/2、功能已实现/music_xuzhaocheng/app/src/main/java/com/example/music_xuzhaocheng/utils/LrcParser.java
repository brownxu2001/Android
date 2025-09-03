package com.example.music_xuzhaocheng.utils;

import com.example.music_xuzhaocheng.module.LrcLine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcParser {
    private static final Pattern TIME_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})(\\.\\d{2,3})?]");

    public static List<LrcLine> parse(String lrcText) {
        List<LrcLine> result = new ArrayList<>();
        String[] lines = lrcText.split("\n");

        for (String line : lines) {
            Matcher matcher = TIME_PATTERN.matcher(line);
            while (matcher.find()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int millis = 0;
                if (matcher.group(3) != null) {
                    millis = (int)(Float.parseFloat(matcher.group(3)) * 1000);
                }
                long timeMs = minutes * 60 * 1000 + seconds * 1000 + millis;
                String text = line.substring(matcher.end()).trim();
                result.add(new LrcLine(timeMs, text));
            }
        }

        return result;
    }
}
