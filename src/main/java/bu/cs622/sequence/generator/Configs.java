package bu.cs622.sequence.generator;

/**
 * Name: Reaz W. Rahman
 * Course: CS 622: Advanced Programming Techniques
 * Date: 2/19/2025
 * File name: Configs.java
 * Description: This class contains the configurations for the sequence generator program.
 */

public class Configs {
    public static final boolean DEBUG_MODE = false;
    public static final boolean SINGLE_THREAD = true;
    public static final boolean MULTI_THREAD = false;

    public static final boolean USE_FILTER = true; // generates unique sequences if set to true

    public static final Integer TOTAL_SEQUENCES = 1000;
    public static final Integer THREAD_COUNT = 5;

    public static final ThreadCreationTypes THREAD_CREATION_METHOD = ThreadCreationTypes.EXECUTOR_FUTURE; // different types of thread creation methods
    public static final FilterTypes FILTER_TYPE = FilterTypes.HASH_SET_FILTER; // different types of filters to generate unique sequences

    public enum ThreadCreationTypes {
        THREAD_CLASS,
        EXECUTOR_RUNNABLE,
        EXECUTOR_FUTURE
    }

    public enum FilterTypes {
        BLOOM_FILTER,
        HASH_SET_FILTER
    }
}
