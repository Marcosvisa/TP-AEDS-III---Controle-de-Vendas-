package util;

public interface PatternMatcher {
    String getName();
    int search(String text, String pattern);
}