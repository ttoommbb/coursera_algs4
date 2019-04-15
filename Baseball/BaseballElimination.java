/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.PatriciaST;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BaseballElimination {

    private int idGen = 0;

    // private final int numberOfTeams;
    // private final Team[] teams;
    private final PatriciaST<Team> liveTeams = new PatriciaST<>();
    private final PatriciaST<Team> eliminatedTeams = new PatriciaST<>();

    private final String[] teamNames;

    private final class Team implements Comparable<Team> {
        private final String name;
        private final int id = idGen++;
        private int wins;
        private int loses;
        private int left;
        private Bag<String> eliminators;
        // private Boolean eliminated;
        private Bag<GameBetween> gameBetweens = new Bag<>();

        public Team(String name, int wins, int loses, int left) {
            this.name = name;
            this.wins = wins;
            this.loses = loses;
            this.left = left;
        }

        public void addBetween(GameBetween between) {
            gameBetweens.add(between);
        }

        public int against(Team team2) {
            if (this == team2) {
                return 0;
            }
            for (GameBetween between : gameBetweens) {
                if (between.team2 == team2
                        || between.team1 == team2) {
                    return between.left;
                }
            }
            return 0;
        }

        @Override
        public int compareTo(Team o) {
            return Integer.compare(id, o.id);
        }
    }

    private final class GameBetween {
        private final int id = idGen++;
        private Team team1;
        private Team team2;
        private int left;

        public GameBetween(Team team1, Team team2, int gameLeft) {
            this.team1 = team1;
            this.team2 = team2;
            left = gameLeft;
        }
    }

    /** create a baseball division from given filename in format specified below */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int numberOfTeams = Integer.parseInt(in.readLine());
        teamNames = new String[numberOfTeams];
        int[][] gameLeft = new int[numberOfTeams][numberOfTeams];
        Team[] teams = new Team[numberOfTeams];
        for (int i = 0; i < numberOfTeams; i++) {
            String[] splits = in.readLine().trim().split("\\s+");
            String name = splits[0];
            teamNames[i] = name;
            int wins = Integer.parseInt(splits[1]);
            int loses = Integer.parseInt(splits[2]);
            int left = Integer.parseInt(splits[3]);
            Team team = new Team(name, wins, loses, left);
            teams[i] = team;
            liveTeams.put(team.name, team);
            for (int part = 4; part < splits.length; part++) {
                gameLeft[i][part - 4] = Integer.parseInt(splits[part]);
            }
        }
        for (int teamId1 = 0; teamId1 < numberOfTeams - 1; teamId1++) {
            for (int teamId2 = teamId1 + 1; teamId2 < numberOfTeams; teamId2++) {
                int gameLeftBetween = gameLeft[teamId1][teamId2];
                if (gameLeftBetween != 0) {
                    GameBetween between = new GameBetween(teams[teamId1], teams[teamId2],
                            gameLeftBetween);
                    teams[teamId1].addBetween(between);
                    teams[teamId2].addBetween(between);
                }
            }
        }
    }

    /** number of teams */
    public int numberOfTeams() {
        return teamNames.length;
    }

    /** all teams */
    public Iterable<String> teams() {
        return Arrays.asList(teamNames);
    }

    /** number of wins for given team */
    public int wins(String team) {
        return getTeamByName(team).wins;
    }

    private Team getTeamByName(String team) {
        Team liveTeam = liveTeams.get(team);
        if (liveTeam != null) {
            return liveTeam;
        }
        Team eliminatedTeam = eliminatedTeams.get(team);
        if (eliminatedTeam != null) {
            return eliminatedTeam;
        }
        throw new IllegalArgumentException("team [" + team + "] not exist");
    }

    /** number of losses for given team */
    public int losses(String team) {
        return getTeamByName(team).loses;
    }

    /** number of remaining games for given team */
    public int remaining(String team) {
        return getTeamByName(team).left;
    }

    /** number of remaining games between team1 and team2 */
    public int against(String team1, String team2) {
        return getTeamByName(team1).against(getTeamByName(team2));
    }

    /** is given team eliminated? */
    public boolean isEliminated(String teamName) {
        return !getNonNullEliminartors(teamName).isEmpty();
    }

    /** subset R of teams that eliminates given team; null if not eliminated */
    public Iterable<String> certificateOfElimination(String teamName) {
        Bag<String> eliminartors = getNonNullEliminartors(teamName);
        return eliminartors.isEmpty() ? null : eliminartors;
    }

    private Bag<String> getNonNullEliminartors(String teamName) {
        Team eliminated = eliminatedTeams.get(teamName);
        if (eliminated != null) {
            return eliminated.eliminators;
        }
        Team liveTeam = liveTeams.get(teamName);
        if (liveTeam == null) {
            throw new IllegalArgumentException();
        }
        if (liveTeam.eliminators != null) {
            return liveTeam.eliminators;
        }

        if (checkTrivialEliminated(liveTeam)) {
            liveTeams.delete(teamName);
            eliminatedTeams.put(teamName, liveTeam);
            return liveTeam.eliminators;
        }
        checkNonTrivialEliminated(liveTeam);
        return liveTeam.eliminators;
    }

    private boolean checkTrivialEliminated(Team team) {
        int winPlusLeft = team.wins + team.left;
        team.eliminators = new Bag<>();
        for (String name : liveTeams.keys()) {
            Team eachTeam = liveTeams.get(name);
            if (eachTeam.wins > winPlusLeft) {
                team.eliminators.add(eachTeam.name);
                return true;
            }
        }
        return false;
    }

    private Bag<String> checkNonTrivialEliminated(Team team) {
        FlowNetwork g = initG(team);
        FordFulkerson fordFulkerson = new FordFulkerson(g, g.V() - 2, g.V() - 1);
        // Bag<String> eliminators = new PatriciaSET();
        for (String liveTeamName : liveTeams.keys()) {
            Team eachTeam = liveTeams.get(liveTeamName);
            if (fordFulkerson.inCut(eachTeam.id)) {
                // eliminators.add(liveTeamName);
                team.eliminators.add(liveTeamName);
            }
        }
        if (!team.eliminators.isEmpty()) {
            liveTeams.delete(team.name);
            eliminatedTeams.put(team.name, team);
        }
        return team.eliminators;
    }

    private FlowNetwork initG(Team team) {
        int winPlusLeft = team.wins + team.left;
        FlowNetwork g = new FlowNetwork(idGen + 2);
        for (String name : liveTeams.keys()) {
            Team eachLiveTeam = liveTeams.get(name);
            if (eachLiveTeam == team) {
                continue;
            }
            for (GameBetween between : eachLiveTeam.gameBetweens) {
                if (between.team1 != eachLiveTeam) {
                    continue; // ensure only handle each between once
                }
                if (between.team2 == team) {
                    continue; // exclude team under test
                }
                if (eliminatedTeams.contains(between.team2.name)) {
                    continue; // exclude eliminated teams.
                }
                g.addEdge(new FlowEdge(g.V() - 2, between.id,
                        between.left)); // start to game vertex
                g.addEdge(new FlowEdge(between.id, between.team1.id,
                        Double.POSITIVE_INFINITY)); // game vertex to team1 vertex
                g.addEdge(new FlowEdge(between.id, between.team2.id,
                        Double.POSITIVE_INFINITY)); // game vertex to team2 vertex
            }
            g.addEdge(new FlowEdge(eachLiveTeam.id, g.V() - 1,
                    winPlusLeft - eachLiveTeam.wins)); // team vertex to end
        }
        return g;
    }

    public static void main(String[] args) {
        // testFile("teams4.txt");
        // testFile("teams5.txt");
        testFile("teams30.txt");
        // testFile("teams12.txt");
        // testFile(args[0]);
        //     for (File file : new File(".").listFiles((dir, name) -> name.matches(".+\\.txt"))) {
        //         testFile(file.getName());
        //         // System.out.println("file = " + file.getName());
        //     }
    }

    private static void testFile(String file) {
        System.out.println("file = " + file);
        BaseballElimination division = new BaseballElimination(file);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
