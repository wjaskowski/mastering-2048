package put.ci.cevo.games.connect4.thill.c4;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * An Interface-Implementation for Connect-Four Agents
 * 
 * @author Markus Thill
 * 
 */
public interface Agent {
	/**
	 * @param table
	 *            current board
	 * @return Best move for the current Player
	 */
	public int getBestMove(int[][] table, RandomDataGenerator random);

	/**
	 * @param player
	 *            player, to find best move for
	 * @param colHeight
	 *            an Array containing the heights of all columns
	 * @param vTable
	 *            Values for all columns
	 * @return Best move for a player using the param vTable
	 */
	public int getBestMove(int player, int[] colHeight, double[] vTable, RandomDataGenerator random);

	/**
	 * @param table
	 *            7x6-board
	 * @param useSigmoid
	 *            put score in range -1 .. +1
	 * @return Score (Value) for the given board
	 */
	public double getScore(int[][] table, boolean useSigmoid);

	/**
	 * @param table
	 *            7x6-board
	 * @param useSigmoid
	 *            put values in range -1 .. +1
	 * @return table containing the values for each column
	 */
	public double[] getNextVTable(int[][] table, boolean useSigmoid);

	/**
	 * @return Name of the Agent
	 */
	public String getName();

	/**
	 * If Threads are used, which multiply access the agents, then ensure that only one thread has exclusive access.
	 * This is the DOWN-Operation on the semaphore (MUTEX)
	 */
	public void semOpDown();

	/**
	 * If Threads are used, which multiply access the agents, then ensure that only one thread has exclusive access.
	 * This is the UP-Operation on the semaphore (MUTEX)
	 */
	public void semOpUp();

}
