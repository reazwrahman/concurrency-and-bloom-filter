package bu.cs622.sequence.generator;

import javax.management.InvalidAttributeValueException;
import java.util.concurrent.ExecutionException;

import static bu.cs622.sequence.generator.Configs.TOTAL_SEQUENCES;

public class Main {

    public static void main(String[] args) throws InterruptedException, InvalidAttributeValueException, ExecutionException {
        validateConfigs();
        ThreadHelper threadHelper = new ThreadHelper();
        if (Configs.SINGLE_THREAD) {
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
        if (Configs.SINGLE_THREAD && Configs.MULTI_THREAD) {
            throw new InvalidAttributeValueException("Only one of the thread types should be selected at a time for accurate metric");
        }
        if (!Configs.SINGLE_THREAD && !Configs.MULTI_THREAD) {
            throw new InvalidAttributeValueException("At least one of the thread types should be selected");
        }
    }
}
