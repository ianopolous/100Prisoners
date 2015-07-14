import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Guesses
{
    public static int maxWinPerms = 0;
    public static List<Set<Integer>> candidates;

    public static void main(String[] args) {
        int nPeople = 6;
        int nGuesses = 3;

        // pre generate  p choose g enumeration
        candidates = getCandidates(nPeople, nGuesses);
        // for every possible guess, find pr(win), keeping track of best
        generateGuesses(nPeople, new ArrayList<>());

        System.out.println("Best guess has Pr(win) = " + (double)maxWinPerms/factorial(nPeople));
    }

    public static void generateGuesses(int p, List<Set<Integer>> res) {
        if (res.size() == p)
        {
            int winPerms = numberOfWinningPermutations(res);
            if (winPerms > maxWinPerms) {
                maxWinPerms = winPerms;
                System.out.println(res);
            }
            return;
        }

        for (int i=0; i < candidates.size(); i++)
        {
            res.add(candidates.get(i));
            generateGuesses(p, res);
            res.remove(candidates.get(i));
        }
    }

    // p choose g, enumerated
    private static List<Set<Integer>> getCandidates(int p, int g)
    {
        List<Set<Integer>> c = new ArrayList<>();
        int max = 1 << p;
        for (int i=0; i < max; i++) {
            int nBits = Integer.bitCount(i);
            if (nBits != g)
                continue;
            Set<Integer> elem = new HashSet<>();
            for (int j=0; j < p; j++)
                if ((i & (1 << j)) != 0)
                    elem.add(j);
            c.add(elem);
        }
        return c;
    }

    public static class Counter implements Function<List<Integer>, Void> {
        public int total = 0;
        private final List<Set<Integer>> guesses;
	
        public Counter(List<Set<Integer>> guesses) {
            this.guesses = guesses;
        }
	
        public Void apply(List<Integer> perm) {
            if (winPerm(perm, guesses))
                total++;
            return null;
        }
    }

    public static int numberOfWinningPermutations(List<Set<Integer>> guesses) {
        List<Integer> order = IntStream.range(0, guesses.size()).mapToObj(Integer::valueOf).collect(Collectors.toList());
        Counter c = new Counter(guesses);
        generatePermutations(order, c);

        return c.total;
    }

    public static void generatePermutations(List<Integer> s, Function<List<Integer>, Void> doSomething)
    {
        List<Integer> res = new ArrayList<>();
        generatePermutations(s, res, doSomething);
    }

    public static void generatePermutations(List<Integer> s, List<Integer> res, Function<List<Integer>, Void> doSomething)
    {
        if (res.size() == s.size())
        {
            doSomething.apply(res);
        }

        List<Integer> candidates = getCandidates(s, res);
        for (int i=0; i < candidates.size(); i++)
        {
            res.add(candidates.get(i));
            generatePermutations(s, res, doSomething);
            res.remove(candidates.get(i));
        }
    }

    private static List<Integer> getCandidates(List<Integer> s, List res)
    {
        return s.stream().filter(x -> !res.contains(x)).collect(Collectors.toList());
    }

    public static boolean winPerm(List<Integer> perm, List<Set<Integer>> guesses) {
        for (int i=0; i < perm.size(); i++)
            if (!guesses.get(i).contains(perm.get(i)))
                return false;
        return true;
    }

    public static int factorial(int n) {
        if (n<2)
            return 1;
        return n * factorial(n-1);
    }
}