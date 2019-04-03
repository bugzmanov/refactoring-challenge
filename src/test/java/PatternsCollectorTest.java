import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class PatternsCollectorTest {

    @Test
    public void test() throws Exception {

        final HashSet<String> domains = new HashSet<String>();
        final StringBuilder errors = new StringBuilder();

        for (final String file : Arrays.asList("blacklist.txt", "whitelist.txt")) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        PatternsCollector.parsePatternFile(
                                getClass().getResource(file).getFile(),
                                new StringBuilder(),
                                errors,
                                domains
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        Thread.sleep(5000L);

        if(errors.toString().isEmpty()) {
            assertEquals(PatternsCollector.getAllPatterns().size(), 9);
            assertEquals(domains.size(), 9);
        } else {
            fail();
        }

    }
}
