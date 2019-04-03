import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * Collects regexp patterns from configuration files.
 * Typical use case:
 *
 * final Set<String> domains = new HashSet<String>();
 * final StringBuilder errors = new StringBuilder();
 *
 *
 * new Thread() { PatternsCollector.parsePatternFile("blacklists.txt", new StringBuilder(), errors, domains); }.start()
 * new Thread() { PatternsCollector.parsePatternFile("whitelists.txt", new StringBuilder(), errors, domains); }.start()
 *
 * // wait for completion
 *
 * if(errors.toString().isEmpty()) {
 *   process(domains, PatternCollector.getAllPatterns());
 *
 * }
 *
 *
 */
public class PatternsCollector implements GlobalConstants {

    private static ArrayList patterns;

    public static final ArrayList getAllPatterns() {
        return patterns;
    }

    public static final synchronized void parsePatternFile(
            String confFile,
            StringBuilder sb,
            StringBuilder errors,
            Set<String> domains) throws Exception {

        if (patterns == null) {
            patterns = new ArrayList();
        }

        if(confFile == null) {
            throw new NullPointerException("File can't be null");
        }

        if (errors == null) {
            errors = new StringBuilder();
        }

        BufferedReader br = new BufferedReader(new FileReader(confFile));
        String line = null;

        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }

            String domain = line;

            if (line.startsWith(DOMAIN_WILDCARD)) {
                domain = line.substring(DOMAIN_WILDCARD.length());
            }

            domain = domain.replace("\\.", ".");

            Matcher m = Pattern.compile(DOMAIN_NAME_REGEXP).matcher(domain);
            if (m.matches()) {
                domains.add(domain);
                patterns.add(line);
            }


            if (!patterns.contains(line)) {
                try {
                    Pattern.compile(line);
                    if (sb.length() > 0) {
                        sb.append('|');
                    }
                    sb.append(line);
                } catch (PatternSyntaxException e) {
                    errors.append("Failed to parse line");
                }
            }
        }
    }

    public static void clear() {
        patterns.clear();
    }

    public static boolean equals(PatternsCollector e) {
        return patterns.equals(e.patterns);
    }
}

interface GlobalConstants {
    String DOMAIN_WILDCARD = "(.*\\.)?";
    String DOMAIN_NAME_REGEXP = "[a-zA-Z0-9.]*";
}
