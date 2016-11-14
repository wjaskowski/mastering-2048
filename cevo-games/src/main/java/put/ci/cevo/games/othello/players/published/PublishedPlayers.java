package put.ci.cevo.games.othello.players.published;

import java.util.Arrays;
import java.util.List;

import put.ci.cevo.games.othello.players.OthelloPlayer;

public class PublishedPlayers {
	private PublishedPlayers() {
		// Static class
	}

	/** Eleven players used for objective measure in one experiment */
	static public List<OthelloPlayer> eleven() {
		return Arrays.asList(
				new OthelloStandardWPCHeuristicPlayer().create(),
				new LucasRunnarson2006Player().create(),
				new SzubertJaskowskiKrawiec2009Player().create(),
				new SzubertJaskowskiKrawiec2011Player().create(),
				new SzubertJaskowskiKrawiec2013CTDLPlayer().create(),
				new SzubertJaskowskiKrawiec2013ETDLPlayer().create(),
				new Burrow2011ETDLPlayer().create(),
				new Manning2010Nash70Player().create(),
				new Manning2010TCIAG2Player().create(),
				new RunnarsonLucas2014IPref1Player().create(),
				new RunnarsonLucas2014IPrefNPlayer().create());
	}

	static public List<OthelloPlayer> published() {
		return Arrays.asList(
				new OthelloStandardWPCHeuristicPlayer().create(),
				new LucasRunnarson2006Player().create(),
				new SzubertJaskowskiKrawiec2009Player().create(),
				new SzubertJaskowskiKrawiec2011Player().create(),
				new SzubertJaskowskiKrawiec2013CTDLPlayer().create(),
				new SzubertJaskowskiKrawiec2013ETDLPlayer().create(),
				new Burrow2011ETDLPlayer().create(),
				new Manning2010Nash70Player().create(),
				new Manning2010TCIAG2Player().create(),
				new Manning2010TCIAG1Player().create(),
				new Manning2010GECCOPlayer().create(),
				new RunnarsonLucas2014IPref1Player().create(),
				new RunnarsonLucas2014IPrefNPlayer().create());
		//TODO: More Lucas players
	}

	static public List<OthelloPlayer> all() {
		return Arrays.asList(
				new OthelloStandardWPCHeuristicPlayer().create(),
				new LucasRunnarson2006Player().create(),
				new SzubertJaskowskiKrawiec2009Player().create(),
				new SzubertJaskowskiKrawiec2011Player().create(),
				new SzubertJaskowskiKrawiec2013CTDLPlayer().create(),
				new SzubertJaskowskiKrawiec2013ETDLPlayer().create(),
				new Burrow2011ETDLPlayer().create(),
				new Manning2010Nash70Player().create(),
				new Manning2010TCIAG2Player().create(),
				new Manning2010TCIAG1Player().create(),
				new Manning2010GECCOPlayer().create(),
				new RunnarsonLucas2014IPref1Player().create(),
				new RunnarsonLucas2014IPrefNPlayer().create(),
				new Jaskowski2014All123Player().create(),
				new Jaskowski2014All2Player().create());
		//TODO: More Lucas players
	}
}
