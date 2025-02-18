package bu.cs622.sequence.generator;


public class Configs {
    public static final boolean DEBUG_MODE = false;
    public static final boolean SINGLE_THREAD = false;
    public static final boolean MULTI_THREAD = true;

    public static final boolean USE_FILTER = true;

    public static final Integer TOTAL_SEQUENCES = 10;
    public static final Integer THREAD_COUNT = 5;

    public enum ThreadCreationTypes {
        THREAD_CLASS,
        EXECUTOR_RUNNABLE,
        EXECUTOR_FUTURE
    }

    public static final ThreadCreationTypes THREAD_TYPE = ThreadCreationTypes.EXECUTOR_FUTURE;
}
