package bu.cs622.sequence.generator;

import bu.cs622.sequence.generator.filters.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SequenceGenerator implements Runnable{
    public static final Character[] SEQUENCE_LETTERS = {'A', 'T', 'G', 'C'};
    public static final int SEQUENCE_LENGTH = 10;
    public Integer m_numOfSequences = null;
    public Integer m_threadId = null;
    public List<StringBuilder> m_synchronizedOutput = null;
    private final Filter m_filter;

    public SequenceGenerator(int threadId, Integer numOfSequences,
                             List<StringBuilder> synchOutput,
                             Filter filter) {
        m_numOfSequences = numOfSequences;
        m_threadId = threadId;
        m_synchronizedOutput = synchOutput;
        m_filter = filter;
    }


    @Override
    public void run(){
        ArrayList<StringBuilder> sequences;
        if (Configs.USE_FILTER) {
            sequences = generateUniqueSequences();
        } else {
            sequences = generateSequences();
        }
        if (Configs.DEBUG_MODE) {
            printSequences(sequences);
        }
    }

    public ArrayList<StringBuilder> generateUniqueSequences() {
        ArrayList<StringBuilder> sequences = new ArrayList<>();

        int i = 0;
        while (i < m_numOfSequences) {
            StringBuilder sequence = new StringBuilder();
            for (int j = 0; j < SEQUENCE_LENGTH; j++) {
                sequence.append(SEQUENCE_LETTERS[getRandomIndex()]);
            }
//            if (!m_filter.checkMembership(sequence.toString())) {
                sequences.add(sequence);
                m_filter.insert(sequence.toString());
                m_synchronizedOutput.add(sequence);
                i++;
//            }
        }

        return sequences;
    }

    public ArrayList<StringBuilder> generateSequences(){
            ArrayList<StringBuilder> sequences = new ArrayList<>();
        for (int i=0; i<m_numOfSequences; i++) {
            StringBuilder test = new StringBuilder();
            for (int j = 0; j < SEQUENCE_LENGTH; j++) {
                test.append(SEQUENCE_LETTERS[getRandomIndex()]);
            }
            sequences.add(test);
            m_synchronizedOutput.add(test);
        }

        return sequences;
    }

    public void printSequences(ArrayList<StringBuilder> sequences){
        int offset = (m_threadId - 1) * m_numOfSequences;
        for (int i=0; i < sequences.size(); i++) {
            int number = (i+1)+offset;
            System.out.println("Thread ID: " + m_threadId.toString() +
                    ", Sequence no " + number + ": " +
                    sequences.get(i));
        }
    }

    private static int getRandomIndex(){
        return ThreadLocalRandom.current().nextInt(0, SEQUENCE_LETTERS.length);
    }
}
