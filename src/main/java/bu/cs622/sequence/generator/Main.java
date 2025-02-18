package bu.cs622.sequence.generator;

import javax.management.InvalidAttributeValueException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import static bu.cs622.sequence.generator.Configs.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, InvalidAttributeValueException, ExecutionException {
        validateConfigs();
        ThreadHelper threadHelper = new ThreadHelper();
        if(Configs.SINGLE_THREAD) {
            threadHelper.initiateSingleThread();
        }
        if (Configs.MULTI_THREAD) {
            threadHelper.initiateMultiThread();
        }
    }

    public static void validateConfigs() throws InvalidAttributeValueException {
        if (Configs.USE_FILTER && TOTAL_SEQUENCES > 1000000) {
            throw new InvalidAttributeValueException("Only 1 Million unique sequences can be generated");
        }
    }
}
