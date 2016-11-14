package put.ci.cevo.experiments.tournament;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayersTeam<S> {

	public final String name;
	public final List<S> players;

	public PlayersTeam(String name, List<S> players) {
		this.name = name;
		this.players = new ArrayList<>(players);
	}

	public PlayersTeam(String name, S player) {
		this.name = name;
		this.players = Arrays.asList(player);
	}

	@Override
	public String toString() {
		return name;
	}
}