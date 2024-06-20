package Utills;

import java.util.regex.Pattern;

public class RegexPatterns {
    public static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("CREATE\\s+TABLE\\s+(\\w+)\\s*\\(([^;]+)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final Pattern INSERT_INTO_PATTERN = Pattern.compile("INSERT INTO (\\w+) VALUES \\(([^;]+)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
}