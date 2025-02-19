package bu.cs622.sequence.generator;

public class Configs {
    public static final boolean DEBUG_MODE = false;
    public static final boolean SINGLE_THREAD = false;
    public static final boolean MULTI_THREAD = true;

    public static final boolean USE_FILTER = false; // generates unique sequences if set to true

    public static final Integer TOTAL_SEQUENCES = 10000000;
    public static final Integer THREAD_COUNT = 5;

    public static final ThreadCreationTypes THREAD_CREATION_METHOD = ThreadCreationTypes.EXECUTOR_FUTURE; // different types of thread creation methods
    public static final FilterTypes FILTER_TYPE = FilterTypes.BLOOM_FILTER; // different types of filters to generate unique sequences

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
