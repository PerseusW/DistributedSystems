package model;

import java.util.ArrayList;
import java.util.List;

public class Matches {
    private final List<String> matchList = new ArrayList<>();

    public void addMatch(String match) {
        matchList.add(match);
    }

    @Override
    public String toString() {
        return "Matches{" +
                "matchList=" + matchList +
                '}';
    }
}
