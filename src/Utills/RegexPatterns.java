package Utills;

import java.util.regex.Pattern;

public class RegexPatterns {
    public static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("CREATE\\s+TABLE\\s+(\\w+)\\s*\\(([^;]+)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final Pattern INSERT_INTO_PATTERN = Pattern.compile("INSERT INTO (\\w+) VALUES \\(([^;]+)\\)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final Pattern DELETE_FROM_PATTERN = Pattern.compile("DELETE\\s+FROM\\s+(\\w+)\\s+WHERE\\s+(\\w+)\\s*=\\s*'(.*?)'", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static final Pattern UPDATE_TABLE_PATTERN = Pattern.compile(
            "UPDATE\\s+(\\w+)\\s+SET\\s+(\\w+)\\s*=\\s*'([^']*)'\\s+WHERE\\s+(\\w+)\\s*=\\s*'([^']*)'",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    public static final Pattern SELECT_FROM_PATTERN = Pattern.compile(
            "\\s*SELECT\\s+(\\*|\\w+(?:\\s*,\\s*\\w+)*)\\s+FROM\\s+(\\w+)(?:\\.txt)?\\s*(?:WHERE\\s+(\\w+)\\s*=\\s*'([^']*)')?\\s*",
            Pattern.CASE_INSENSITIVE);
}