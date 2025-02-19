package bu.cs622.sequence.generator.filters;

/**
 * Name: Reaz W. Rahman
 * Course: CS 622: Advanced Programming Techniques
 * Date: 2/19/2025
 * File name: Filter.java
 * Description: This is the interface for the filters that will be used to generate unique sequences.
 */

public interface Filter {
    void insert(String record);

    boolean checkMembership(String record);

    long getApprxoimateSize();

    long getPeakMemory();
}
