package bu.cs622.sequence.generator.filters;

public interface Filter {
    public void insert(String record);
    public boolean checkMembership(String record);
    public long getApprxoimateSize();
    public long getPeakMemory();
}
