package bu.cs622.sequence.generator.filters;

public interface Filter {
    void insert(String record);

    boolean checkMembership(String record);

    long getApprxoimateSize();

    long getPeakMemory();
}
