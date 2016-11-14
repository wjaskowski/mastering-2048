package put.ci.cevo.games.connect4.thill.c4;

import java.io.Serializable;

/**
 * This class contains all params needed for a TD-Agent with a N-Tuple Value function. The Interface Serializable is
 * implemented, so that these params can be saved to HDD.
 * 
 * @author Markus Thill
 */
public class TDParams implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8359202059091063197L;

	public static final double IDBD_BETA_LOWER_BOUND = -10.0;
	public static final double IDBD_BETA_CHANGE_MAX = 2;

	// All Values are Defaults
	public int trainGameNum = 10000000;

	// The rewards can be given after x moves. This decreases the Complexity of
	// the training
	public int stopAfterMoves = 100;

	// each field of the board can have 3 or 4 different values
	// 3: empty, yellow, red
	// 4: empty and directly reachable, empty and not reachable, yellow, red
	public int posVals = 4;

	// learning-rate alpha
	public double alphaInit = 0.01;
	public double alphaFinal = 0.001;

	// There are different methods of changing alpha
	// 0: exponential
	// 1: linear
	// 2: Usage of tanh
	// 3: Simple Jump
	public int alphaChangeMethod = 0;
	public double alphaChangeParam = 0.0;

	// prob. for explor. moves
	public double epsilonInit = 0.95;
	public double epsilonFinal = 0.1;

	// Methods like above
	public int epsilonMethod = 2;
	public double epsilonChangeParam = 2000000;

	public double gamma = 1.0;
	public double lambda = 0.0;
	public boolean replacingTraces = true;
	public boolean resetEligOnRandomMove = true;

	// n-Ply look-ahead
	public int nPlyLookAhead = 0;

	// Use this Board representation:
	// 5 11 17 23 29 35 41
	// 4 10 16 22 28 34 40
	// 3 9 15 21 27 33 39
	// 2 8 14 20 26 32 38
	// 1 7 13 19 25 31 37
	// 0 6 12 18 24 30 36

	// Standard Tuple-Set
	public Integer nTuples[][] = new Integer[][] { //
	{ 0, 6, 7, 12, 13, 14, 19, 21 }, //
		{ 13, 18, 19, 20, 21, 26, 27, 33 }, //
		{ 1, 3, 4, 6, 7, 8, 9, 10 }, //
		{ 7, 8, 9, 12, 15, 19, 25, 30 }, //
		{ 4, 5, 9, 10, 11, 15, 16, 17 }, //
		{ 1, 2, 3, 8, 9, 10, 16, 17 }, //
		{ 3, 8, 9, 10, 11, 14, 15, 16 }, //
		{ 0, 1, 2, 6, 8, 12, 13, 18 }, //
		{ 25, 26, 27, 32, 33, 37, 38, 39 }, //
		{ 3, 4, 8, 9, 11, 14, 15, 21 }, //
		{ 2, 3, 4, 8, 9, 14, 15, 20 }, //
		{ 18, 19, 24, 30, 31, 32, 36, 37 }, //
		{ 3, 4, 8, 9, 10, 14, 15, 16 }, //
		{ 5, 10, 11, 16, 17, 21, 22, 27 }, //
		{ 4, 10, 15, 20, 21, 22, 27, 28 }, //
		{ 18, 24, 25, 30, 31, 32, 37, 38 }, //
		{ 11, 17, 21, 23, 27, 28, 33, 39 }, //
		{ 21, 25, 26, 27, 32, 34, 35, 41 }, //
		{ 22, 25, 26, 27, 30, 32, 33, 37 }, //
		{ 4, 10, 11, 16, 20, 21, 22, 23 }, //
		{ 0, 6, 7, 8, 12, 13, 14, 15 }, //
		{ 17, 23, 28, 29, 32, 33, 34, 35 }, //
		{ 0, 6, 7, 12, 18, 25, 32, 38 }, //
		{ 2, 3, 4, 5, 8, 9, 10, 11 }, //
		{ 27, 32, 33, 34, 37, 38, 39, 40 }, //
		{ 4, 10, 16, 21, 26, 32, 33, 38 }, //
		{ 0, 6, 7, 12, 13, 20, 27, 28 }, //
		{ 0, 6, 12, 19, 25, 31, 32, 33 }, //
		{ 1, 2, 6, 7, 13, 14, 15, 20 }, //
		{ 1, 2, 5, 8, 11, 15, 16, 17 }, //
		{ 13, 14, 16, 18, 21, 22, 23, 24 }, //
		{ 2, 3, 9, 10, 11, 16, 17, 22 }, //
		{ 15, 16, 17, 20, 22, 23, 25, 31 }, //
		{ 15, 16, 17, 21, 22, 23, 28, 29 }, //
		{ 24, 26, 30, 31, 32, 33, 36, 37 }, //
		{ 12, 13, 18, 19, 20, 26, 27, 33 }, //
		{ 1, 2, 3, 8, 9, 13, 14, 21 }, //
		{ 13, 14, 18, 20, 24, 25, 31, 37 }, //
		{ 14, 15, 16, 21, 26, 31, 38, 39 }, //
		{ 1, 2, 6, 7, 12, 13, 14, 20 }, //
		{ 4, 5, 10, 11, 17, 22, 23, 29 }, //
		{ 2, 4, 5, 7, 9, 10, 14, 19 }, //
		{ 5, 9, 10, 11, 15, 16, 21, 27 }, //
		{ 1, 2, 3, 7, 8, 13, 14, 20 }, //
		{ 1, 2, 8, 9, 14, 15, 21, 26 }, //
		{ 22, 23, 29, 33, 34, 35, 38, 41 }, //
		{ 13, 18, 19, 24, 25, 26, 31, 32 }, //
		{ 27, 28, 29, 31, 32, 33, 37, 38 }, //
		{ 10, 14, 15, 16, 17, 20, 21, 23 }, //
		{ 4, 5, 9, 10, 15, 20, 21, 22 }, //
		{ 13, 20, 25, 26, 27, 32, 34, 41 }, //
		{ 30, 31, 33, 34, 36, 37, 38, 39 }, //
		{ 11, 16, 23, 28, 34, 35, 40, 41 }, //
		{ 3, 4, 10, 11, 14, 15, 16, 17 }, //
		{ 15, 20, 21, 22, 26, 32, 33, 39 }, //
		{ 18, 19, 25, 26, 31, 32, 34, 39 }, //
		{ 4, 9, 11, 15, 16, 22, 23, 29 }, //
		{ 26, 27, 31, 32, 33, 37, 38, 39 }, //
		{ 20, 27, 28, 33, 34, 35, 40, 41 }, //
		{ 1, 2, 7, 14, 20, 27, 28, 29 }, //
		{ 8, 9, 10, 15, 16, 17, 22, 23 }, //
		{ 9, 14, 15, 20, 21, 22, 27, 32 }, //
		{ 1, 2, 3, 6, 7, 8, 9, 13 }, //
		{ 10, 14, 15, 16, 20, 23, 25, 26 }, //
		{ 0, 1, 2, 6, 7, 8, 13, 14 }, //
		{ 1, 6, 7, 12, 13, 20, 26, 27 }, //
		{ 8, 14, 20, 25, 26, 31, 33, 38 }, //
		{ 20, 21, 26, 27, 28, 33, 35, 40 }, //
		{ 2, 3, 4, 8, 9, 11, 16, 21 }, //
		{ 1, 2, 3, 4, 5, 6, 11, 12 }, //
	}; //

	// set true, if random tuples shall be generated
	public boolean randNTuples = false;

	// Method of generating the single tuples
	// 0: random sample-points, length of all tuples equal
	// 1: random sample-points, length of all tuples random (2 .. tupleLen)
	// 2: Random Walk, length of all tuples equal
	// 3: Random Walk, length of all tuples random (2 .. tupleLen)
	public int randMode = 2;

	// max. length of the tuples
	public int randTupleLen = 8;

	// Number of N-Tuples
	public int tupleNum = 70;

	// Set True, if board shall be mirrored at the center-column
	public boolean useSymmetry = true;

	// True, if weights shall be initialized randomly
	public boolean randInitWeights = false;

	// For Single-Training (Infos on the Console)
	public int infoInterval = 100000;
	public int numEvaluationMatches = 200;
	public boolean measureStrength = false;

	// Factor k in V(x)=tanh(f(x)*k)
	public double sigOutputFac = 1.0;

	// Evaluate agent after infoInterval games (50 Games)
	public boolean evaluate50Games = true;

	// Evaluate agent after infoInterval games (Boards with 0,1,2 Positions)
	public boolean evaluate012 = false;

	// Hash-Table
	public boolean useHashExtern = true;
	public boolean useHashIntern = true;
	public int hashSize = 4194304 / 128;

	// TODO: Added on 06.12.2013
	// public int updateMethod = 0; // Weight Update Method:TDL
	// Update-Methods:
	// 1. Standard TDL-Method: Simply use dw=alpha*delta*elig
	// 2. Use Temporal Coherence dw(i)=alpha*alpha_w(i)*delta*elig
	// 3. Use Incremental Delta-Bar-Delta
	public enum UpdateMethod {
		TDL,
		TCL,
		IDBD
	};

	public UpdateMethod updateMethod = UpdateMethod.TCL;
	public double epsilonSlope = 10.0; // Slope of tanh in epsilon
	public boolean useActivationFunc = true; // use squashing function
	public boolean useBiasWeight = true; // use bias-value
	public double idbdBetaInit = -5.8;
	public double idbdTheta = 1.0;
	public double tclTablesInitValue = Double.NEGATIVE_INFINITY; // Usage is deprecated, do not use
	public boolean tclUseUpdateEpisodes = false;
	public int tclEpisodeLength = 10;
	public double tclMuInit = 1.0;
	public double tclMuFinal = 1.0;
	public double tclMuInitBias = 1.0;
	public double tclMuFinalBias = 1.0;
	public boolean tclUpdate1WeightFactors2Weights = false;
	public boolean tclUseErrorSignal = false;
	public boolean tclUseExpScheme = false;
	public double tclExpSchemeFacA = 2.7;
	public double tclExpSchemeFacB = 0.0;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
