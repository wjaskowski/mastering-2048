package put.ci.cevo.games.connect4.thill.c4;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * Implementation of the basic methods needed for a Connect-Four-Game. The public methods should be sufficient for
 * normal use. Contains: the board-representation (Board is coded as bitboard internally). Methods to put pieces on the
 * Board and remove them again, to find Wins / Draw, to generate move-lists and much more
 * 
 * @author Markus Thill
 * 
 */
public class ConnectFour {

	/**
	 * Constants for both player
	 */
	public static final int PLAYER1 = 1; // Beginning player
	public static final int PLAYER2 = 2;

	/**
	 * Board Constants
	 */
	protected static final int COLCOUNT = 7;
	protected static final int ROWCOUNT = 6;
	protected static final long EVENROWS = 0x15555555555L;
	protected static final long ODDROWS = 0xA28A28A28AL;
	protected static final long TOPROW = 0x1041041041L;
	protected static final long FIELDFULL = 0x3FFFFFFFFFFL;
	public static final long fieldMask[][] = new long[COLCOUNT][ROWCOUNT];
	protected static final long columnMask[] = { 0x3F000000000L, 0xFC0000000L, 0x3F000000L, 0xFC0000L, 0x3F000L,
		0xFC0L, 0x3FL };

	/**
	 * All possible FourRows horizontally, diagonal and vertical
	 */
	protected static final long fourRows[] = { 0x1041040000L, 0x41041000L, 0x1041040L, 0x41041L, 0x2082080000L,
		0x82082000L, 0x2082080L, 0x82082L, 0x1084200000L, 0x42108000L, 0x1084200L, 0x42108L, 0x8102040000L,
		0x204081000L, 0x8102040L, 0x204081L, 0x4104100000L, 0x104104000L, 0x4104100L, 0x104104L, 0x2108400000L,
		0x84210000L, 0x2108400L, 0x84210L, 0x10204080000L, 0x408102000L, 0x10204080L, 0x408102L, 0x8208200000L,
		0x208208000L, 0x8208200L, 0x208208L, 0x4210800000L, 0x108420000L, 0x4210800L, 0x108420L, 0x20408100000L,
		0x810204000L, 0x20408100L, 0x810204L, 0x10410400000L, 0x410410000L, 0x10410400L, 0x410410L, 0x20820800000L,
		0x820820000L, 0x20820800L, 0x820820L, 0x3C000000000L, 0x1E000000000L, 0xF000000000L, 0xF00000000L,
		0x780000000L, 0x3C0000000L, 0x3C000000L, 0x1E000000L, 0xF000000L, 0xF00000L, 0x780000L, 0x3C0000L, 0x3C000L,
		0x1E000L, 0xF000L, 0xF00L, 0x780L, 0x3C0L, 0x3CL, 0x1EL, 0xFL };

	// Random-Numbers for Zobrist Keys
	protected static final long rnd[][] = {
		{ 19298943901485610L, 6548220796761019L, 1777628907278452803L, 11891769178478592L, 3564258696970080L,
			236708853179436288L, 349182760342233125L, 429289086240375L, 121921717543355343L, 31495429917193824L,
			5694462647075520L, 30758051047680284L, 1365712501364505581L, 17363511325679223L, 119226791868480L,
			6220173073409360L, 11647770880598424L, 24507207907919492L, 551903736927872L, 2097977134396858L,
			3108717381973636075L, 25389306976498643L, 254362479754036508L, 119080142037405540L, 65628472867223040L,
			116416206906490816L, 130703539652185785L, 1541174198942728362L, 37852277734190556L, 22426187114508354L,
			290694253237906321L, 3460150747465650L, 12108862858045894L, 124792798959719156L, 6572334569141376L,
			2726416766762766L, 378828340783306008L, 72087612995472200L, 113983283880328672L, 376285078915283L,
			62397498210717124L, 1193066389676430L },
		{ 2704506115994628L, 886077597871704230L, 49389502572435258L, 82333996817139652L, 263967204879563328L,
			81975673952415600L, 73398886193708064L, 1595863713887220963L, 46261610206381440L, 58705059883690202L,
			231696507446129885L, 22606427398883328L, 12595346104058426L, 4097820997223100L, 101324622437045280L,
			31779374605300125L, 35633797708573350L, 22416112427922675L, 36363358841386824L, 4173667863902779612L,
			9131869703157656L, 14138249969764235L, 214348955873908032L, 58547228054472360L, 55740094356093127L,
			1777939723684020L, 362858203316162568L, 28975890403315160L, 1242349240448115806L, 59601464106441712L,
			9110872168202946L, 10631234269963860L, 16888881020037981L, 1159792823631456L, 36205525950397736L,
			47068546447093808L, 375817237236357603L, 32189775283681470L, 1493718293429439L, 20793138156733824L,
			101478045365676084L, 110552240521049760L } };

	/**
	 * Board-representation as BitBoard
	 */
	protected long fieldP1 = 0;
	protected long fieldP2 = 0;
	protected int colHeight[] = new int[COLCOUNT];

	/**
	 * Generate an empty Board
	 */
	public ConnectFour() {
		computeFieldMasks();
		resetBoard();
	}

	/**
	 * @param field
	 *            a 7x6 Connect-Four Board: 1 -> Player 1 (Beginner) 2 -> Player 2
	 */
	public ConnectFour(int field[][]) {
		resetBoard();
		computeFieldMasks();
		setBoard(field);
	}

	/**
	 * Create a new Board
	 * 
	 * @param fieldP1
	 *            BitBoard of Player1
	 * @param fieldP2
	 *            BitBoard of Player2
	 */
	public ConnectFour(long fieldP1, long fieldP2) {
		computeFieldMasks();
		resetBoard();
		setBoard(fieldP1, fieldP2);
	}

	/**
	 * Set the Board to the new Values
	 * 
	 * @param fieldP1
	 *            Board of Player1 as BitBoard(Beginning player)
	 * @param fieldP2
	 *            Board of Player2 as BitBoard
	 */
	public void setBoard(long fieldP1, long fieldP2) {
		this.fieldP1 = fieldP1;
		this.fieldP2 = fieldP2;
		computeColHeight();
	}

	/**
	 * The internal representation of the board are two BitBoards an array containing the heights of each column. Arrays
	 * have to be converted in this form
	 * 
	 * @param field
	 *            7x6 Array-representation of the Board <br>
	 */
	public void setBoard(int field[][]) {
		// Konvertierung des Arrays in das Bitboard-Format
		// und Eintragen der einzelnen Spaltenh�hen in colHeight
		if (isLegalBoard(field)) {
			resetBoard();
			for (int i = 0; i < COLCOUNT; i++) {
				for (int j = 0; j < ROWCOUNT; j++) {
					if (field[i][j] == PLAYER1) {
						fieldP1 |= fieldMask[i][j];
						colHeight[i]++;
					} else if (field[i][j] == PLAYER2) {
						fieldP2 |= fieldMask[i][j];
						colHeight[i]++;
					}
				}
			}
		}
	}

	/**
	 * get the current board
	 * 
	 * @return 7x6-Array of the board
	 */
	public int[][] getBoard() {
		int board[][] = new int[COLCOUNT][ROWCOUNT];
		for (int i = 0; i < COLCOUNT; i++) {
			for (int j = 0; j < colHeight[i]; j++) {
				if ((fieldP1 & fieldMask[i][j]) != 0L) {
					board[i][j] = PLAYER1;
				} else if ((fieldP2 & fieldMask[i][j]) != 0L) {
					board[i][j] = PLAYER2;
				} else {
					board[i][j] = 0;
				}
			}
		}
		return board;
	}

	/**
	 * Get all Winning-Rows for one player (if existing)
	 * 
	 * @param player
	 *            search for this player
	 * @return Array of all pieces that are within a Winning-Row. Even indexes represent the column, even indexes the
	 *         row
	 */
	public int[] getWinRows(int player) {
		int[] winRow = new int[2];
		int[][] b = getBoard();
		int k = 0, i, j;
		for (i = 0; i < COLCOUNT; i++) {
			for (j = 0; j < colHeight[i]; j++) {
				if (canWin(player, i, j) && b[i][j] == player) {
					if (k == winRow.length) {
						int[] temp = winRow;
						winRow = new int[k + 2];
						for (int l = 0; l < temp.length; l++) {
							winRow[l] = temp[l];
						}
					}
					winRow[k++] = i;
					winRow[k++] = j;
				}
			}
		}
		// Vertikale Reihen werden von oberer Schleife nicht erkannt
		int count;
		for (i = 0; i < COLCOUNT; i++) {
			count = 0;
			for (j = 0; j < colHeight[i]; j++) {
				if (b[i][j] == player) {
					count++;
				} else {
					count = 0;
				}
				if (count == 4) {
					break;
				}
			}
			if (count == 4) {
				for (count = (j - 1); count > j - 4; count--) {
					if (k == winRow.length) {
						int[] temp = winRow;
						winRow = new int[k + 2];
						for (int l = 0; l < temp.length; l++) {
							winRow[l] = temp[l];
						}
					}
					winRow[k++] = i;
					winRow[k++] = count;
				}
			}

		}
		return winRow;
	}

	/**
	 * Print the current board to console
	 */
	public void printBoard() {
		for (int j = ROWCOUNT - 1; j >= 0; j--) {
			for (int i = 0; i < COLCOUNT; i++) {
				if ((fieldP1 & fieldMask[i][j]) != 0L) {
					System.out.print("1  ");
				} else if ((fieldP2 & fieldMask[i][j]) != 0L) {
					System.out.print("2  ");
				} else {
					System.out.print("0  ");
				}
			}
			System.out.print("\n");
		}
	}

	/**
	 * Print the current board to console
	 */
	public static void printBoard(int[][] board) {
		for (int j = ROWCOUNT - 1; j >= 0; j--) {
			for (int i = 0; i < COLCOUNT; i++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.print("\n");
		}
	}

	/**
	 * Get a Board as String
	 */
	public static String toString(int[][] board) {
		String str = new String();
		for (int j = ROWCOUNT - 1; j >= 0; j--) {
			for (int i = 0; i < COLCOUNT; i++) {
				str += (board[i][j] + " ");
			}
			str += ("\n");
		}
		return str;
	}

	/**
	 * Reomve all pieces from the board
	 */
	public void resetBoard() {
		fieldP1 = 0L;
		fieldP2 = 0L;
		for (int i = 0; i < COLCOUNT; i++) {
			colHeight[i] = 0;
		}
	}

	/**
	 * Get the BitBoard for one player
	 * 
	 * @param player
	 * @return all pieces of the player in BitBoard-representation
	 */
	public long getField(int player) {
		return (player == PLAYER1 ? fieldP1 : fieldP2);
	}

	/**
	 * Get the height of all columns
	 * 
	 * @return a 1x7 Array containing all heights
	 */
	public int[] getColHeight() {
		int[] arr = new int[COLCOUNT];
		for (int i = 0; i < COLCOUNT; i++) {
			arr[i] = colHeight[i];
		}
		return arr;
	}

	/**
	 * Get the height of one column
	 * 
	 * @param col
	 * @return height of column col
	 */
	public int getColHeight(int col) {
		return colHeight[col];
	}

	/**
	 * Get the BitBoard-mask for one field of the board
	 * 
	 * @param col
	 * @param row
	 * @return mask with one set bit
	 */
	protected long getFieldMask(int col, int row) {
		return fieldMask[col][row];
	}

	/**
	 * like {@link AlphaBetaAgent#getFieldMask} But the mask isn't taken from an array
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	protected long getMask(int col, int row) {
		// Ermittelt Maske F�r eine Zelle des Spielfeldes
		return 1L << (ROWCOUNT * COLCOUNT - 1 - (col * ROWCOUNT + row));
	}

	/**
	 * Generate all masks for the 42 fields of the board.
	 */
	protected void computeFieldMasks() {
		// Berechnet die Masken f�r alle 42 Zellen des Spielfeldes
		for (int i = 0; i < COLCOUNT; i++) {
			for (int j = 0; j < ROWCOUNT; j++) {
				fieldMask[i][j] = getMask(i, j);
			}
		}
	}

	/**
	 * Check, if the array contains a legal connect4-board
	 * 
	 * @param field
	 *            7x6 array
	 * @return true, if board is legal, false else
	 */
	public boolean isLegalBoard(int field[][]) {

		boolean lastPiece;

		// Ist das Gesetz der Gravitation erf�llt???
		for (int i = 0; i < COLCOUNT; i++) {
			lastPiece = false;
			for (int j = 0; j < ROWCOUNT; j++) {
				if (lastPiece && field[i][j] != 0) {
					return false;
				}
				if (field[i][j] == 0) {
					lastPiece = true;
				}
				// Ein Feld hat nur drei g�ltige Werte
				if (field[i][j] != 0 && field[i][j] != 1 && field[i][j] != 2) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check, whether it is possible to make a move in the column col
	 * 
	 * @param col
	 * @return true, if this is possible
	 */
	public boolean isLegalMove(int col) {
		return colHeight[col] < ROWCOUNT;
	}

	/**
	 * Make a move for one player in a column
	 * 
	 * @param player
	 * @param col
	 */
	public void putPiece(int player, int col) {
		// Es muss ggfs. gepr�ft werden, ob dieser Zug m�glich ist
		long mask = fieldMask[col][colHeight[col]++];

		if (player == PLAYER1) {
			fieldP1 |= mask;
		} else {
			fieldP2 |= mask;
		}
	}

	/**
	 * Place Piece on the Board for the current player
	 * 
	 * @param col
	 */
	public void putPiece(int col) {
		int player = (countPieces() % 2 == 0 ? PLAYER1 : PLAYER2);
		putPiece(player, col);
	}

	/**
	 * Take a move for one player back
	 * 
	 * @param player
	 * @param col
	 */
	public void removePiece(int player, int col) {
		// Es muss ggfs. gepr�ft werden, ob �berhaupt ein Stein in der Spalte
		// ist
		// Es wird eine invertierte Maske zum l�schen ben�tigt
		long mask = ~fieldMask[col][--colHeight[col]];

		if (player == PLAYER1) {
			fieldP1 &= mask;
		} else {
			fieldP2 &= mask;
		}
	}

	/**
	 * Compute the height of all columns, and put results in array colHeight[7].
	 */
	protected void computeColHeight() {
		// Berechnet die F�llh�he der einzelnen Spalten
		long mask;
		int i, j;
		for (i = 0; i < COLCOUNT; i++) {
			for (j = 0; j < ROWCOUNT; j++) {
				mask = getMask(i, j);
				if ((fieldP1 & mask) == 0 && (fieldP2 & mask) == 0) {
					break;
				}
			}
			colHeight[i] = j;
		}
	}

	/**
	 * @return Number of pieces on the board
	 */
	public int countPieces() {
		// Z�hlt die Anzahl der eingeworfenen Steine beider Spieler
		int count = 0;
		for (int i = 0; i < COLCOUNT; i++) {
			count += colHeight[i];
		}
		return count;
	}

	/**
	 * @param board
	 * @return Number of Pieces on board[][]
	 */
	public static int countPieces(int board[][]) {
		int count = 0;
		for (int i = 0; i < COLCOUNT; i++)
			for (int j = 0; j < ROWCOUNT; j++)
				if (board[i][j] != 0)
					count++;
		return count;
	}

	/**
	 * @return true, if board is completly filled
	 */
	public boolean isDraw() {
		return ((fieldP1 | fieldP2) & FIELDFULL) == FIELDFULL;
	}

	/**
	 * Check if current Player can win with move x
	 * 
	 * @param x
	 * @return true, if Win
	 */
	public boolean canWin(int x) {
		int player = countPieces() % 2 == 0 ? PLAYER1 : PLAYER2;
		return canWin(player, x);
	}

	/**
	 * Check, if the player can Win in the specified column
	 * 
	 * @param player
	 * @param x
	 * @return true, if a Win was found
	 */
	public boolean canWin(int player, int x) {
		return canWin(player, x, colHeight[x]);
	}

	/**
	 * Check if player can win in one field
	 * 
	 * @param player
	 * @param xx
	 *            column
	 * @param yy
	 *            row
	 * @return true, if that field completes a winning-row
	 */
	public boolean canWin(int player, int xx, int yy) {
		long x = (player == PLAYER1 ? ~fieldP1 : ~fieldP2);

		// Convert to 32-Bit, since a lot of the literals are only 32-Bit long
		int y = (int) x;
		// X und Y Position in eine feste Position(0-41) umgerechnet, da die
		// switch-anweisung schneller ausgef�hrt wird
		switch (xx * 6 + yy) {
		case 0:
			if (!((x & 0x408100000L) != 0 && (x & 0x820800000L) != 0))
				return true;
			break;
		case 1:
			if (!((x & 0x410400000L) != 0 && (x & 0x204080000L) != 0))
				return true;
			break;
		case 2:
			if (!((x & 0x208200000L) != 0 && (x & 0x102040000L) != 0))
				return true;
			break;
		case 3:
			if (!((x & 0x38000000000L) != 0 && (x & 0x210800000L) != 0 && (x & 0x104100000L) != 0))
				return true;
			break;
		case 4:
			if (!((x & 0x108400000L) != 0 && (x & 0x1C000000000L) != 0 && (y & 0x82080000) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x84200000) != 0 && (x & 0xE000000000L) != 0 && (y & 0x41040000) != 0))
				return true;
			break;
		case 6:
			if (!((y & 0x10204000) != 0 && (y & 0x20820000) != 0 && (x & 0x20020800000L) != 0))
				return true;
			break;
		case 7:
			if (!((x & 0x10010400000L) != 0 && (x & 0x20008100000L) != 0 && (y & 0x10410000) != 0 && (y & 0x8102000) != 0))
				return true;
			break;
		case 8:
			if (!((x & 0x8008200000L) != 0 && (x & 0x4010800000L) != 0 && (y & 0x8208000) != 0
				&& (x & 0x10004080000L) != 0 && (y & 0x4081000) != 0))
				return true;
			break;
		case 9:
			if (!((y & 0x8420000) != 0 && (x & 0xE00000000L) != 0 && (y & 0x4104000) != 0 && (x & 0x4004100000L) != 0
				&& (x & 0x8002040000L) != 0 && (x & 0x2008400000L) != 0))
				return true;
			break;
		case 10:
			if (!((y & 0x4210000) != 0 && (y & 0x2082000) != 0 && (x & 0x700000000L) != 0 && (x & 0x2002080000L) != 0 && (x & 0x1004200000L) != 0))
				return true;
			break;
		case 11:
			if (!((y & 0x2108000) != 0 && (y & 0x1041000) != 0 && (x & 0x380000000L) != 0 && (x & 0x1001040000L) != 0))
				return true;
			break;
		case 12:
			if (!((y & 0x408100) != 0 && (x & 0x20800800000L) != 0 && (y & 0x820800) != 0 && (x & 0x800820000L) != 0))
				return true;
			break;
		case 13:
			if (!((x & 0x4200800000L) != 0 && (x & 0x10400400000L) != 0 && (x & 0x800204000L) != 0
				&& (y & 0x410400) != 0 && (x & 0x400410000L) != 0 && (y & 0x204080) != 0))
				return true;
			break;
		case 14:
			if (!((x & 0x100420000L) != 0 && (x & 0x8200200000L) != 0 && (x & 0x20400100000L) != 0
				&& (x & 0x200208000L) != 0 && (y & 0x208200) != 0 && (x & 0x400102000L) != 0
				&& (x & 0x2100400000L) != 0 && (y & 0x102040) != 0))
				return true;
			break;
		case 15:
			if (!((y & 0x210800) != 0 && (x & 0x10200080000L) != 0 && (x & 0x4100100000L) != 0
				&& (x & 0x100104000L) != 0 && (x & 0x200081000L) != 0 && (y & 0x80210000) != 0 && (y & 0x38000000) != 0
				&& (y & 0x104100) != 0 && (x & 0x1080200000L) != 0))
				return true;
			break;
		case 16:
			if (!((y & 0x108400) != 0 && (x & 0x8100040000L) != 0 && (y & 0x80082000) != 0 && (y & 0x40108000) != 0
				&& (y & 0x82080) != 0 && (y & 0x1C000000) != 0 && (x & 0x2080080000L) != 0))
				return true;
			break;
		case 17:
			if (!((y & 0x84200) != 0 && (y & 0xE000000) != 0 && (y & 0x40041000) != 0 && (y & 0x41040) != 0 && (x & 0x1040040000L) != 0))
				return true;
			break;
		case 18:
			if (!((x & 0x20820000000L) != 0 && (x & 0x820020000L) != 0 && (y & 0x20020800) != 0 && (y & 0x20820) != 0
				&& (y & 0x10204) != 0 && (x & 0x4210000000L) != 0))
				return true;
			break;
		case 19:
			if (!((y & 0x20008100) != 0 && (y & 0x10410) != 0 && (x & 0x10410000000L) != 0 && (x & 0x2108000000L) != 0
				&& (x & 0x108020000L) != 0 && (y & 0x10010400) != 0 && (y & 0x8102) != 0 && (x & 0x410010000L) != 0))
				return true;
			break;
		case 20:
			if (!((x & 0x810004000L) != 0 && (y & 0x4010800) != 0 && (x & 0x8208000000L) != 0
				&& (x & 0x208008000L) != 0 && (y & 0x8008200) != 0 && (y & 0x8208) != 0 && (y & 0x84010000) != 0
				&& (y & 0x10004080) != 0 && (y & 0x4081) != 0 && (x & 0x1084000000L) != 0))
				return true;
			break;
		case 21:
			if (!((x & 0x20408000000L) != 0 && (y & 0x8420) != 0 && (y & 0xE00000) != 0 && (x & 0x4104000000L) != 0
				&& (y & 0x2008400) != 0 && (x & 0x408002000L) != 0 && (y & 0x4004100) != 0 && (x & 0x104004000L) != 0
				&& (y & 0x42008000) != 0 && (y & 0x8002040) != 0 && (y & 0x4104) != 0))
				return true;
			break;
		case 22:
			if (!((x & 0x10204000000L) != 0 && (y & 0x700000) != 0 && (y & 0x4210) != 0 && (x & 0x204001000L) != 0
				&& (x & 0x2082000000L) != 0 && (y & 0x1004200) != 0 && (y & 0x82002000) != 0 && (y & 0x2002080) != 0 && (y & 0x2082) != 0))
				return true;
			break;
		case 23:
			if (!((y & 0x380000) != 0 && (x & 0x8102000000L) != 0 && (y & 0x2108) != 0 && (x & 0x1041000000L) != 0
				&& (y & 0x41001000) != 0 && (y & 0x1001040) != 0 && (y & 0x1041) != 0))
				return true;
			break;
		case 24:
			if (!((x & 0x108400000L) != 0 && (y & 0x800820) != 0 && (y & 0x20800800) != 0 && (x & 0x820800000L) != 0))
				return true;
			break;
		case 25:
			if (!((y & 0x800204) != 0 && (y & 0x4200800) != 0 && (y & 0x400410) != 0 && (y & 0x10400400) != 0
				&& (x & 0x410400000L) != 0 && (y & 0x84200000) != 0))
				return true;
			break;
		case 26:
			if (!((y & 0x100420) != 0 && (y & 0x20400100) != 0 && (y & 0x8200200) != 0 && (x & 0x208200000L) != 0
				&& (y & 0x200208) != 0 && (y & 0x2100400) != 0 && (y & 0x400102) != 0 && (y & 0x42100000) != 0))
				return true;
			break;
		case 27:
			if (!((x & 0x810200000L) != 0 && (y & 0x80210) != 0 && (y & 0x38000) != 0 && (y & 0x4100100) != 0
				&& (x & 0x104100000L) != 0 && (y & 0x10200080) != 0 && (y & 0x1080200) != 0 && (y & 0x100104) != 0 && (y & 0x200081) != 0))
				return true;
			break;
		case 28:
			if (!((x & 0x408100000L) != 0 && (y & 0x40108) != 0 && (y & 0x1C000) != 0 && (y & 0x2080080) != 0
				&& (y & 0x82080000) != 0 && (y & 0x8100040) != 0 && (y & 0x80082) != 0))
				return true;
			break;
		case 29:
			if (!((x & 0x204080000L) != 0 && (y & 0xE000) != 0 && (y & 0x1040040) != 0 && (y & 0x41040000) != 0 && (y & 0x40041) != 0))
				return true;
			break;
		case 30:
			if (!((y & 0x4210000) != 0 && (y & 0x20820000) != 0 && (y & 0x820020) != 0))
				return true;
			break;
		case 31:
			if (!((y & 0x10410000) != 0 && (y & 0x410010) != 0 && (y & 0x108020) != 0 && (y & 0x2108000) != 0))
				return true;
			break;
		case 32:
			if (!((y & 0x8208000) != 0 && (y & 0x208008) != 0 && (y & 0x84010) != 0 && (y & 0x810004) != 0 && (y & 0x1084000) != 0))
				return true;
			break;
		case 33:
			if (!((y & 0x20408000) != 0 && (y & 0x4104000) != 0 && (y & 0xE00) != 0 && (y & 0x104004) != 0
				&& (y & 0x42008) != 0 && (y & 0x408002) != 0))
				return true;
			break;
		case 34:
			if (!((y & 0x10204000) != 0 && (y & 0x2082000) != 0 && (y & 0x700) != 0 && (y & 0x82002) != 0 && (y & 0x204001) != 0))
				return true;
			break;
		case 35:
			if (!((y & 0x8102000) != 0 && (y & 0x1041000) != 0 && (y & 0x380) != 0 && (y & 0x41001) != 0))
				return true;
			break;
		case 36:
			if (!((y & 0x108400) != 0 && (y & 0x820800) != 0))
				return true;
			break;
		case 37:
			if (!((y & 0x410400) != 0 && (y & 0x84200) != 0))
				return true;
			break;
		case 38:
			if (!((y & 0x208200) != 0 && (y & 0x42100) != 0))
				return true;
			break;
		case 39:
			if (!((y & 0x810200) != 0 && (y & 0x38) != 0 && (y & 0x104100) != 0))
				return true;
			break;
		case 40:
			if (!((y & 0x408100) != 0 && (y & 0x1C) != 0 && (y & 0x82080) != 0))
				return true;
			break;
		case 41:
			if (!((y & 0x204080) != 0 && (y & 0xE) != 0 && (y & 0x41040) != 0))
				return true;
			break;
		}
		return false;
	}

	/**
	 * @param player
	 * @return Winning-column for player, -1 else
	 */
	public int findImmediateThreat(int player) {
		// Suchen einer unmittelbaren Drohung
		if (hasWin(player)) {
			for (int i = 0; i < COLCOUNT; i++) {
				if (colHeight[i] < 6 && canWin(player, i, colHeight[i])) {
					return i;
				}
			}
		}
		return (-1);
	}

	/**
	 * Check if player can win with his next move
	 * 
	 * @param player
	 * @return true, if player can win within his next move
	 */
	public boolean hasWin(int player) {
		long x = (player == 1 ? ~fieldP1 : ~fieldP2);

		// Convert to 32-Bit, since a lot of the literals are only 32-Bit long
		int y = (int) x;
		switch (colHeight[3]) {
		case 0:
			if (!((x & 0x20820000000L) != 0 && (x & 0x820020000L) != 0 && (y & 0x20020800) != 0 && (y & 0x20820) != 0
				&& (y & 0x10204) != 0 && (x & 0x4210000000L) != 0))
				return true;
			break;
		case 1:
			if (!((y & 0x20008100) != 0 && (y & 0x10410) != 0 && (x & 0x10410000000L) != 0 && (x & 0x2108000000L) != 0
				&& (x & 0x108020000L) != 0 && (y & 0x10010400) != 0 && (y & 0x8102) != 0 && (x & 0x410010000L) != 0))
				return true;
			break;
		case 2:
			if (!((x & 0x810004000L) != 0 && (y & 0x4010800) != 0 && (x & 0x8208000000L) != 0
				&& (x & 0x208008000L) != 0 && (y & 0x8008200) != 0 && (y & 0x8208) != 0 && (y & 0x84010000) != 0
				&& (y & 0x10004080) != 0 && (y & 0x4081) != 0 && (x & 0x1084000000L) != 0))
				return true;
			break;
		case 3:
			if (!((x & 0x20408000000L) != 0 && (y & 0x8420) != 0 && (y & 0xE00000) != 0 && (x & 0x4104000000L) != 0
				&& (y & 0x2008400) != 0 && (x & 0x408002000L) != 0 && (y & 0x4004100) != 0 && (x & 0x104004000L) != 0
				&& (y & 0x42008000) != 0 && (y & 0x8002040) != 0 && (y & 0x4104) != 0))
				return true;
			break;
		case 4:
			if (!((x & 0x10204000000L) != 0 && (y & 0x700000) != 0 && (y & 0x4210) != 0 && (x & 0x204001000L) != 0
				&& (x & 0x2082000000L) != 0 && (y & 0x1004200) != 0 && (y & 0x82002000) != 0 && (y & 0x2002080) != 0 && (y & 0x2082) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x380000) != 0 && (x & 0x8102000000L) != 0 && (y & 0x2108) != 0 && (x & 0x1041000000L) != 0
				&& (y & 0x41001000) != 0 && (y & 0x1001040) != 0 && (y & 0x1041) != 0))
				return true;
			break;
		default:
			break;
		}
		switch (colHeight[4]) {
		case 0:
			if (!((x & 0x108400000L) != 0 && (y & 0x800820) != 0 && (y & 0x20800800) != 0 && (x & 0x820800000L) != 0))
				return true;
			break;
		case 1:
			if (!((y & 0x800204) != 0 && (y & 0x4200800) != 0 && (y & 0x400410) != 0 && (y & 0x10400400) != 0
				&& (x & 0x410400000L) != 0 && (y & 0x84200000) != 0))
				return true;
			break;
		case 2:
			if (!((y & 0x100420) != 0 && (y & 0x20400100) != 0 && (y & 0x8200200) != 0 && (x & 0x208200000L) != 0
				&& (y & 0x200208) != 0 && (y & 0x2100400) != 0 && (y & 0x400102) != 0 && (y & 0x42100000) != 0))
				return true;
			break;
		case 3:
			if (!((x & 0x810200000L) != 0 && (y & 0x80210) != 0 && (y & 0x38000) != 0 && (y & 0x4100100) != 0
				&& (x & 0x104100000L) != 0 && (y & 0x10200080) != 0 && (y & 0x1080200) != 0 && (y & 0x100104) != 0 && (y & 0x200081) != 0))
				return true;
			break;
		case 4:
			if (!((x & 0x408100000L) != 0 && (y & 0x40108) != 0 && (y & 0x1C000) != 0 && (y & 0x2080080) != 0
				&& (y & 0x82080000) != 0 && (y & 0x8100040) != 0 && (y & 0x80082) != 0))
				return true;
			break;
		case 5:
			if (!((x & 0x204080000L) != 0 && (y & 0xE000) != 0 && (y & 0x1040040) != 0 && (y & 0x41040000) != 0 && (y & 0x40041) != 0))
				return true;
			break;
		default:
			break;
		}
		switch (colHeight[2]) {
		case 0:
			if (!((y & 0x408100) != 0 && (x & 0x20800800000L) != 0 && (y & 0x820800) != 0 && (x & 0x800820000L) != 0))
				return true;
			break;
		case 1:
			if (!((x & 0x4200800000L) != 0 && (x & 0x10400400000L) != 0 && (x & 0x800204000L) != 0
				&& (y & 0x410400) != 0 && (x & 0x400410000L) != 0 && (y & 0x204080) != 0))
				return true;
			break;
		case 2:
			if (!((x & 0x100420000L) != 0 && (x & 0x8200200000L) != 0 && (x & 0x20400100000L) != 0
				&& (x & 0x200208000L) != 0 && (y & 0x208200) != 0 && (x & 0x400102000L) != 0
				&& (x & 0x2100400000L) != 0 && (y & 0x102040) != 0))
				return true;
			break;
		case 3:
			if (!((y & 0x210800) != 0 && (x & 0x10200080000L) != 0 && (x & 0x4100100000L) != 0
				&& (x & 0x100104000L) != 0 && (x & 0x200081000L) != 0 && (y & 0x80210000) != 0 && (y & 0x38000000) != 0
				&& (y & 0x104100) != 0 && (x & 0x1080200000L) != 0))
				return true;
			break;
		case 4:
			if (!((y & 0x108400) != 0 && (x & 0x8100040000L) != 0 && (y & 0x80082000) != 0 && (y & 0x40108000) != 0
				&& (y & 0x82080) != 0 && (y & 0x1C000000) != 0 && (x & 0x2080080000L) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x84200) != 0 && (y & 0xE000000) != 0 && (y & 0x40041000) != 0 && (y & 0x41040) != 0 && (x & 0x1040040000L) != 0))
				return true;
			break;
		default:
			break;
		}
		switch (colHeight[5]) {
		case 0:
			if (!((y & 0x4210000) != 0 && (y & 0x20820000) != 0 && (y & 0x820020) != 0))
				return true;
			break;
		case 1:
			if (!((y & 0x10410000) != 0 && (y & 0x410010) != 0 && (y & 0x108020) != 0 && (y & 0x2108000) != 0))
				return true;
			break;
		case 2:
			if (!((y & 0x8208000) != 0 && (y & 0x208008) != 0 && (y & 0x84010) != 0 && (y & 0x810004) != 0 && (y & 0x1084000) != 0))
				return true;
			break;
		case 3:
			if (!((y & 0x20408000) != 0 && (y & 0x4104000) != 0 && (y & 0xE00) != 0 && (y & 0x104004) != 0
				&& (y & 0x42008) != 0 && (y & 0x408002) != 0))
				return true;
			break;
		case 4:
			if (!((y & 0x10204000) != 0 && (y & 0x2082000) != 0 && (y & 0x700) != 0 && (y & 0x82002) != 0 && (y & 0x204001) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x8102000) != 0 && (y & 0x1041000) != 0 && (y & 0x380) != 0 && (y & 0x41001) != 0))
				return true;
			break;
		default:
			break;
		}
		switch (colHeight[1]) {
		case 0:
			if (!((y & 0x10204000) != 0 && (y & 0x20820000) != 0 && (x & 0x20020800000L) != 0))
				return true;
			break;
		case 1:
			if (!((x & 0x10010400000L) != 0 && (x & 0x20008100000L) != 0 && (y & 0x10410000) != 0 && (y & 0x8102000) != 0))
				return true;
			break;
		case 2:
			if (!((x & 0x8008200000L) != 0 && (x & 0x4010800000L) != 0 && (y & 0x8208000) != 0
				&& (x & 0x10004080000L) != 0 && (y & 0x4081000) != 0))
				return true;
			break;
		case 3:
			if (!((y & 0x8420000) != 0 && (x & 0xE00000000L) != 0 && (y & 0x4104000) != 0 && (x & 0x4004100000L) != 0
				&& (x & 0x8002040000L) != 0 && (x & 0x2008400000L) != 0))
				return true;
			break;
		case 4:
			if (!((y & 0x4210000) != 0 && (y & 0x2082000) != 0 && (x & 0x700000000L) != 0 && (x & 0x2002080000L) != 0 && (x & 0x1004200000L) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x2108000) != 0 && (y & 0x1041000) != 0 && (x & 0x380000000L) != 0 && (x & 0x1001040000L) != 0))
				return true;
			break;
		default:
			break;
		}
		switch (colHeight[6]) {
		case 0:
			if (!((y & 0x108400) != 0 && (y & 0x820800) != 0))
				return true;
			break;
		case 1:
			if (!((y & 0x410400) != 0 && (y & 0x84200) != 0))
				return true;
			break;
		case 2:
			if (!((y & 0x208200) != 0 && (y & 0x42100) != 0))
				return true;
			break;
		case 3:
			if (!((y & 0x810200) != 0 && (y & 0x38) != 0 && (y & 0x104100) != 0))
				return true;
			break;
		case 4:
			if (!((y & 0x408100) != 0 && (y & 0x1C) != 0 && (y & 0x82080) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x204080) != 0 && (y & 0xE) != 0 && (y & 0x41040) != 0))
				return true;
			break;
		default:
			break;
		}
		switch (colHeight[0]) {
		case 0:
			if (!((x & 0x408100000L) != 0 && (x & 0x820800000L) != 0))
				return true;
			break;
		case 1:
			if (!((x & 0x410400000L) != 0 && (x & 0x204080000L) != 0))
				return true;
			break;
		case 2:
			if (!((x & 0x208200000L) != 0 && (x & 0x102040000L) != 0))
				return true;
			break;
		case 3:
			if (!((x & 0x38000000000L) != 0 && (x & 0x210800000L) != 0 && (x & 0x104100000L) != 0))
				return true;
			break;
		case 4:
			if (!((x & 0x108400000L) != 0 && (x & 0x1C000000000L) != 0 && (y & 0x82080000) != 0))
				return true;
			break;
		case 5:
			if (!((y & 0x84200000) != 0 && (x & 0xE000000000L) != 0 && (y & 0x41040000) != 0))
				return true;
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * mirror the board for one player at the center-column
	 * 
	 * @param player
	 * @return mirrored BitBoard
	 */
	protected long getMirroredField(int player) {
		// Feld spiegeln. Wird ben�tigt, da Stellungen nur jeweils in einer
		// Variante in der Er�ffnungsdatenbank vorhanden sind
		long temp = (player == PLAYER1 ? fieldP1 : fieldP2);
		long mirroredField = 0L;

		mirroredField |= ((temp & columnMask[0]) >> 36);
		mirroredField |= ((temp & columnMask[1]) >> 24);
		mirroredField |= ((temp & columnMask[2]) >> 12);
		mirroredField |= (temp & columnMask[3]);
		mirroredField |= ((temp & columnMask[4]) << 12);
		mirroredField |= ((temp & columnMask[5]) << 24);
		mirroredField |= ((temp & columnMask[6]) << 36);
		return mirroredField;
	}

	/**
	 * mirror the board for one player at the center-column
	 * 
	 * @return mirrored BitBoard
	 */
	public static long getMirroredField(long field) {
		long mirroredField = 0L;

		mirroredField |= ((field & columnMask[0]) >> 36);
		mirroredField |= ((field & columnMask[1]) >> 24);
		mirroredField |= ((field & columnMask[2]) >> 12);
		mirroredField |= (field & columnMask[3]);
		mirroredField |= ((field & columnMask[4]) << 12);
		mirroredField |= ((field & columnMask[5]) << 24);
		mirroredField |= ((field & columnMask[6]) << 36);
		return mirroredField;
	}

	/**
	 * mirror the board for one player at the center-column
	 * 
	 * @return mirrored board
	 */
	public static int[][] getMirroredField(int[][] board) {
		int[][] mirroredField = new int[COLCOUNT][ROWCOUNT];
		for (int i = 0; i < COLCOUNT; i++)
			mirroredField[i] = board[COLCOUNT - i - 1].clone();
		return mirroredField;
	}

	/**
	 * Convert board into a special huffman-code representation. Is needed for the opening-books.
	 * 
	 * @param f1
	 *            Bitboard of player1
	 * @param f2
	 *            BitBoard of player2
	 * @param mirrored
	 *            true, if f1 and f2 are the mirrored Bitboards of the board
	 * @return
	 */
	protected int fieldToHuffman(long f1, long f2, boolean mirrored) {
		int temp = 0;
		long mask;
		int i, j, inc;

		// Stellung in Huffman-Code codieren
		// 0 -> Keine weiteren Steine in Spalte (1 Bit)
		// 2 -> Stein von dem Anziehenden (2 Bit)
		// 3 -> Stein des Nachziehenden (2 Bit)
		// Stellung wird beginnend von der ersten Spalte codiert
		// Die letzten beiden Bits sind jedoch fr die
		// Stellungsbewertung reserviert
		i = (mirrored ? 6 : 0);
		inc = mirrored ? -1 : 1;
		for (; (mirrored && i >= 0) || (!mirrored && i < 7); i += inc) {
			for (j = 0; j < colHeight[i]; j++) {
				mask = (mirrored ? getMask((6 - i), j) : getMask(i, j));
				if ((f1 & mask) != 0L) {
					temp <<= 2;
					temp |= 3;
				} else if ((f2 & mask) != 0L) {
					temp <<= 2;
					temp |= 2;
				}
			}
			temp <<= 1;
		}
		return temp << 1;
	}

	/**
	 * Get all legal moves for the player. Order the most promising moves to the front of the board.
	 * 
	 * @param player
	 * @return List of all possible moves
	 */
	public int[] generateMoves(int player) {
		// Vergleich mit Spielfeld des Anderen
		long temp = (player == PLAYER1 ? fieldP2 : fieldP1);
		int cn[] = new int[7], count, i, j = 0, index = 0;

		switch (colHeight[0]) {
		case 0:
			count = 7;
			if ((temp & 0x20408100000L) != 0L)
				count--;
			if ((temp & 0x20820800000L) != 0L)
				count--;
			if ((temp & 0x3C000000000L) != 0L)
				count--;
			break;
		case 1:
			count = 9;
			if ((temp & 0x10204080000L) != 0L)
				count--;
			if ((temp & 0x10410400000L) != 0L)
				count--;
			if ((temp & 0x3C000000000L) != 0L)
				count--;
			if ((temp & 0x1E000000000L) != 0L)
				count--;
			break;
		case 2:
			count = 11;
			if ((temp & 0x8102040000L) != 0L)
				count--;
			if ((temp & 0x8208200000L) != 0L)
				count--;
			if ((temp & 0x3C000000000L) != 0L)
				count--;
			if ((temp & 0x1E000000000L) != 0L)
				count--;
			if ((temp & 0xF000000000L) != 0L)
				count--;
			break;
		case 3:
			count = 10;
			if ((temp & 0x4104100000L) != 0L)
				count--;
			if ((temp & 0x4210800000L) != 0L)
				count--;
			if ((temp & 0x3C000000000L) != 0L)
				count--;
			if ((temp & 0x1E000000000L) != 0L)
				count--;
			if ((temp & 0xF000000000L) != 0L)
				count--;
			break;
		case 4:
			count = 8;
			if ((temp & 0x2082080000L) != 0L)
				count--;
			if ((temp & 0x2108400000L) != 0L)
				count--;
			if ((temp & 0x1E000000000L) != 0L)
				count--;
			if ((temp & 0xF000000000L) != 0L)
				count--;
			break;
		case 5:
			count = 6;
			if ((temp & 0x1041040000L) != 0L)
				count--;
			if ((temp & 0x1084200000L) != 0L)
				count--;
			if ((temp & 0xF000000000L) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[0] = count;

		switch (colHeight[1]) {
		case 0:
			count = 9;
			if ((temp & 0x810204000L) != 0L)
				count--;
			if ((temp & 0x20820800000L) != 0L)
				count--;
			if ((temp & 0x820820000L) != 0L)
				count--;
			if ((temp & 0xF00000000L) != 0L)
				count--;
			break;
		case 1:
			count = 13;
			if ((temp & 0x408102000L) != 0L)
				count--;
			if ((temp & 0x20408100000L) != 0L)
				count--;
			if ((temp & 0x10410400000L) != 0L)
				count--;
			if ((temp & 0x410410000L) != 0L)
				count--;
			if ((temp & 0xF00000000L) != 0L)
				count--;
			if ((temp & 0x780000000L) != 0L)
				count--;
			break;
		case 2:
			count = 17;
			if ((temp & 0x204081000L) != 0L)
				count--;
			if ((temp & 0x10204080000L) != 0L)
				count--;
			if ((temp & 0x8208200000L) != 0L)
				count--;
			if ((temp & 0x208208000L) != 0L)
				count--;
			if ((temp & 0x4210800000L) != 0L)
				count--;
			if ((temp & 0xF00000000L) != 0L)
				count--;
			if ((temp & 0x780000000L) != 0L)
				count--;
			if ((temp & 0x3C0000000L) != 0L)
				count--;
			break;
		case 3:
			count = 16;
			if ((temp & 0x8102040000L) != 0L)
				count--;
			if ((temp & 0x4104100000L) != 0L)
				count--;
			if ((temp & 0x104104000L) != 0L)
				count--;
			if ((temp & 0x2108400000L) != 0L)
				count--;
			if ((temp & 0x108420000L) != 0L)
				count--;
			if ((temp & 0xF00000000L) != 0L)
				count--;
			if ((temp & 0x780000000L) != 0L)
				count--;
			if ((temp & 0x3C0000000L) != 0L)
				count--;
			break;
		case 4:
			count = 12;
			if ((temp & 0x2082080000L) != 0L)
				count--;
			if ((temp & 0x82082000L) != 0L)
				count--;
			if ((temp & 0x1084200000L) != 0L)
				count--;
			if ((temp & 0x84210000L) != 0L)
				count--;
			if ((temp & 0x780000000L) != 0L)
				count--;
			if ((temp & 0x3C0000000L) != 0L)
				count--;
			break;
		case 5:
			count = 8;
			if ((temp & 0x1041040000L) != 0L)
				count--;
			if ((temp & 0x41041000L) != 0L)
				count--;
			if ((temp & 0x42108000L) != 0L)
				count--;
			if ((temp & 0x3C0000000L) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[1] = count;

		switch (colHeight[2]) {
		case 0:
			count = 11;
			if ((temp & 0x20408100L) != 0L)
				count--;
			if ((temp & 0x20820800000L) != 0L)
				count--;
			if ((temp & 0x820820000L) != 0L)
				count--;
			if ((temp & 0x20820800L) != 0L)
				count--;
			if ((temp & 0x3C000000L) != 0L)
				count--;
			break;
		case 1:
			count = 17;
			if ((temp & 0x10204080L) != 0L)
				count--;
			if ((temp & 0x4210800000L) != 0L)
				count--;
			if ((temp & 0x810204000L) != 0L)
				count--;
			if ((temp & 0x10410400000L) != 0L)
				count--;
			if ((temp & 0x410410000L) != 0L)
				count--;
			if ((temp & 0x10410400L) != 0L)
				count--;
			if ((temp & 0x3C000000L) != 0L)
				count--;
			if ((temp & 0x1E000000L) != 0L)
				count--;
			break;
		case 2:
			count = 23;
			if ((temp & 0x8102040L) != 0L)
				count--;
			if ((temp & 0x2108400000L) != 0L)
				count--;
			if ((temp & 0x408102000L) != 0L)
				count--;
			if ((temp & 0x8208200000L) != 0L)
				count--;
			if ((temp & 0x208208000L) != 0L)
				count--;
			if ((temp & 0x8208200L) != 0L)
				count--;
			if ((temp & 0x108420000L) != 0L)
				count--;
			if ((temp & 0x20408100000L) != 0L)
				count--;
			if ((temp & 0x3C000000L) != 0L)
				count--;
			if ((temp & 0x1E000000L) != 0L)
				count--;
			if ((temp & 0xF000000L) != 0L)
				count--;
			break;
		case 3:
			count = 22;
			if ((temp & 0x1084200000L) != 0L)
				count--;
			if ((temp & 0x204081000L) != 0L)
				count--;
			if ((temp & 0x4104100000L) != 0L)
				count--;
			if ((temp & 0x104104000L) != 0L)
				count--;
			if ((temp & 0x4104100L) != 0L)
				count--;
			if ((temp & 0x84210000L) != 0L)
				count--;
			if ((temp & 0x10204080000L) != 0L)
				count--;
			if ((temp & 0x4210800L) != 0L)
				count--;
			if ((temp & 0x3C000000L) != 0L)
				count--;
			if ((temp & 0x1E000000L) != 0L)
				count--;
			if ((temp & 0xF000000L) != 0L)
				count--;
			break;
		case 4:
			count = 16;
			if ((temp & 0x2082080000L) != 0L)
				count--;
			if ((temp & 0x82082000L) != 0L)
				count--;
			if ((temp & 0x2082080L) != 0L)
				count--;
			if ((temp & 0x42108000L) != 0L)
				count--;
			if ((temp & 0x8102040000L) != 0L)
				count--;
			if ((temp & 0x2108400L) != 0L)
				count--;
			if ((temp & 0x1E000000L) != 0L)
				count--;
			if ((temp & 0xF000000L) != 0L)
				count--;
			break;
		case 5:
			count = 10;
			if ((temp & 0x1041040000L) != 0L)
				count--;
			if ((temp & 0x41041000L) != 0L)
				count--;
			if ((temp & 0x1041040L) != 0L)
				count--;
			if ((temp & 0x1084200L) != 0L)
				count--;
			if ((temp & 0xF000000L) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[2] = count;

		switch (colHeight[3]) {
		case 0:
			count = 15;
			if ((temp & 0x4210800000L) != 0L)
				count--;
			if ((temp & 0x810204L) != 0L)
				count--;
			if ((temp & 0x20820800000L) != 0L)
				count--;
			if ((temp & 0x820820000L) != 0L)
				count--;
			if ((temp & 0x20820800L) != 0L)
				count--;
			if ((temp & 0x820820L) != 0L)
				count--;
			if ((temp & 0xF00000L) != 0L)
				count--;
			break;
		case 1:
			count = 21;
			if ((temp & 0x2108400000L) != 0L)
				count--;
			if ((temp & 0x408102L) != 0L)
				count--;
			if ((temp & 0x108420000L) != 0L)
				count--;
			if ((temp & 0x20408100L) != 0L)
				count--;
			if ((temp & 0x10410400000L) != 0L)
				count--;
			if ((temp & 0x410410000L) != 0L)
				count--;
			if ((temp & 0x10410400L) != 0L)
				count--;
			if ((temp & 0x410410L) != 0L)
				count--;
			if ((temp & 0xF00000L) != 0L)
				count--;
			if ((temp & 0x780000L) != 0L)
				count--;
			break;
		case 2:
			count = 27;
			if ((temp & 0x1084200000L) != 0L)
				count--;
			if ((temp & 0x204081L) != 0L)
				count--;
			if ((temp & 0x84210000L) != 0L)
				count--;
			if ((temp & 0x10204080L) != 0L)
				count--;
			if ((temp & 0x8208200000L) != 0L)
				count--;
			if ((temp & 0x208208000L) != 0L)
				count--;
			if ((temp & 0x8208200L) != 0L)
				count--;
			if ((temp & 0x208208L) != 0L)
				count--;
			if ((temp & 0x4210800L) != 0L)
				count--;
			if ((temp & 0x810204000L) != 0L)
				count--;
			if ((temp & 0xF00000L) != 0L)
				count--;
			if ((temp & 0x780000L) != 0L)
				count--;
			if ((temp & 0x3C0000L) != 0L)
				count--;
			break;
		case 3:
			count = 26;
			if ((temp & 0x42108000L) != 0L)
				count--;
			if ((temp & 0x8102040L) != 0L)
				count--;
			if ((temp & 0x4104100000L) != 0L)
				count--;
			if ((temp & 0x104104000L) != 0L)
				count--;
			if ((temp & 0x4104100L) != 0L)
				count--;
			if ((temp & 0x104104L) != 0L)
				count--;
			if ((temp & 0x2108400L) != 0L)
				count--;
			if ((temp & 0x408102000L) != 0L)
				count--;
			if ((temp & 0x108420L) != 0L)
				count--;
			if ((temp & 0x20408100000L) != 0L)
				count--;
			if ((temp & 0xF00000L) != 0L)
				count--;
			if ((temp & 0x780000L) != 0L)
				count--;
			if ((temp & 0x3C0000L) != 0L)
				count--;
			break;
		case 4:
			count = 20;
			if ((temp & 0x2082080000L) != 0L)
				count--;
			if ((temp & 0x82082000L) != 0L)
				count--;
			if ((temp & 0x2082080L) != 0L)
				count--;
			if ((temp & 0x82082L) != 0L)
				count--;
			if ((temp & 0x1084200L) != 0L)
				count--;
			if ((temp & 0x204081000L) != 0L)
				count--;
			if ((temp & 0x84210L) != 0L)
				count--;
			if ((temp & 0x10204080000L) != 0L)
				count--;
			if ((temp & 0x780000L) != 0L)
				count--;
			if ((temp & 0x3C0000L) != 0L)
				count--;
			break;
		case 5:
			count = 14;
			if ((temp & 0x1041040000L) != 0L)
				count--;
			if ((temp & 0x41041000L) != 0L)
				count--;
			if ((temp & 0x1041040L) != 0L)
				count--;
			if ((temp & 0x41041L) != 0L)
				count--;
			if ((temp & 0x42108L) != 0L)
				count--;
			if ((temp & 0x8102040000L) != 0L)
				count--;
			if ((temp & 0x3C0000L) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[3] = count;

		switch (colHeight[4]) {
		case 0:
			count = 11;
			if ((temp & 0x108420000L) != 0L)
				count--;
			if ((temp & 0x820820000L) != 0L)
				count--;
			if ((temp & 0x20820800L) != 0L)
				count--;
			if ((temp & 0x820820L) != 0L)
				count--;
			if ((temp & 0x3C000L) != 0L)
				count--;
			break;
		case 1:
			count = 17;
			if ((temp & 0x84210000L) != 0L)
				count--;
			if ((temp & 0x4210800L) != 0L)
				count--;
			if ((temp & 0x810204L) != 0L)
				count--;
			if ((temp & 0x410410000L) != 0L)
				count--;
			if ((temp & 0x10410400L) != 0L)
				count--;
			if ((temp & 0x410410L) != 0L)
				count--;
			if ((temp & 0x3C000L) != 0L)
				count--;
			if ((temp & 0x1E000L) != 0L)
				count--;
			break;
		case 2:
			count = 23;
			if ((temp & 0x42108000L) != 0L)
				count--;
			if ((temp & 0x2108400L) != 0L)
				count--;
			if ((temp & 0x408102L) != 0L)
				count--;
			if ((temp & 0x208208000L) != 0L)
				count--;
			if ((temp & 0x8208200L) != 0L)
				count--;
			if ((temp & 0x208208L) != 0L)
				count--;
			if ((temp & 0x108420L) != 0L)
				count--;
			if ((temp & 0x20408100L) != 0L)
				count--;
			if ((temp & 0x3C000L) != 0L)
				count--;
			if ((temp & 0x1E000L) != 0L)
				count--;
			if ((temp & 0xF000L) != 0L)
				count--;
			break;
		case 3:
			count = 22;
			if ((temp & 0x1084200L) != 0L)
				count--;
			if ((temp & 0x204081L) != 0L)
				count--;
			if ((temp & 0x104104000L) != 0L)
				count--;
			if ((temp & 0x4104100L) != 0L)
				count--;
			if ((temp & 0x104104L) != 0L)
				count--;
			if ((temp & 0x84210L) != 0L)
				count--;
			if ((temp & 0x10204080L) != 0L)
				count--;
			if ((temp & 0x810204000L) != 0L)
				count--;
			if ((temp & 0x3C000L) != 0L)
				count--;
			if ((temp & 0x1E000L) != 0L)
				count--;
			if ((temp & 0xF000L) != 0L)
				count--;
			break;
		case 4:
			count = 16;
			if ((temp & 0x82082000L) != 0L)
				count--;
			if ((temp & 0x2082080L) != 0L)
				count--;
			if ((temp & 0x82082L) != 0L)
				count--;
			if ((temp & 0x42108L) != 0L)
				count--;
			if ((temp & 0x8102040L) != 0L)
				count--;
			if ((temp & 0x408102000L) != 0L)
				count--;
			if ((temp & 0x1E000L) != 0L)
				count--;
			if ((temp & 0xF000L) != 0L)
				count--;
			break;
		case 5:
			count = 10;
			if ((temp & 0x41041000L) != 0L)
				count--;
			if ((temp & 0x1041040L) != 0L)
				count--;
			if ((temp & 0x41041L) != 0L)
				count--;
			if ((temp & 0x204081000L) != 0L)
				count--;
			if ((temp & 0xF000L) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[4] = count;

		switch (colHeight[5]) {
		case 0:
			count = 9;
			if ((temp & 0x4210800L) != 0L)
				count--;
			if ((temp & 0x20820800L) != 0L)
				count--;
			if ((temp & 0x820820L) != 0L)
				count--;
			if ((temp & 0xF00L) != 0L)
				count--;
			break;
		case 1:
			count = 13;
			if ((temp & 0x2108400L) != 0L)
				count--;
			if ((temp & 0x108420L) != 0L)
				count--;
			if ((temp & 0x10410400L) != 0L)
				count--;
			if ((temp & 0x410410L) != 0L)
				count--;
			if ((temp & 0xF00L) != 0L)
				count--;
			if ((temp & 0x780L) != 0L)
				count--;
			break;
		case 2:
			count = 17;
			if ((temp & 0x1084200L) != 0L)
				count--;
			if ((temp & 0x84210L) != 0L)
				count--;
			if ((temp & 0x8208200L) != 0L)
				count--;
			if ((temp & 0x208208L) != 0L)
				count--;
			if ((temp & 0x810204L) != 0L)
				count--;
			if ((temp & 0xF00L) != 0L)
				count--;
			if ((temp & 0x780L) != 0L)
				count--;
			if ((temp & 0x3C0L) != 0L)
				count--;
			break;
		case 3:
			count = 16;
			if ((temp & 0x42108L) != 0L)
				count--;
			if ((temp & 0x4104100L) != 0L)
				count--;
			if ((temp & 0x104104L) != 0L)
				count--;
			if ((temp & 0x408102L) != 0L)
				count--;
			if ((temp & 0x20408100L) != 0L)
				count--;
			if ((temp & 0xF00L) != 0L)
				count--;
			if ((temp & 0x780L) != 0L)
				count--;
			if ((temp & 0x3C0L) != 0L)
				count--;
			break;
		case 4:
			count = 12;
			if ((temp & 0x2082080L) != 0L)
				count--;
			if ((temp & 0x82082L) != 0L)
				count--;
			if ((temp & 0x204081L) != 0L)
				count--;
			if ((temp & 0x10204080L) != 0L)
				count--;
			if ((temp & 0x780L) != 0L)
				count--;
			if ((temp & 0x3C0L) != 0L)
				count--;
			break;
		case 5:
			count = 8;
			if ((temp & 0x1041040L) != 0L)
				count--;
			if ((temp & 0x41041L) != 0L)
				count--;
			if ((temp & 0x8102040L) != 0L)
				count--;
			if ((temp & 0x3C0L) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[5] = count;

		switch (colHeight[6]) {
		case 0:
			count = 7;
			if ((temp & 0x108420L) != 0L)
				count--;
			if ((temp & 0x820820L) != 0L)
				count--;
			if ((temp & 0x3CL) != 0L)
				count--;
			break;
		case 1:
			count = 9;
			if ((temp & 0x84210L) != 0L)
				count--;
			if ((temp & 0x410410L) != 0L)
				count--;
			if ((temp & 0x3CL) != 0L)
				count--;
			if ((temp & 0x1EL) != 0L)
				count--;
			break;
		case 2:
			count = 11;
			if ((temp & 0x42108L) != 0L)
				count--;
			if ((temp & 0x208208L) != 0L)
				count--;
			if ((temp & 0x3CL) != 0L)
				count--;
			if ((temp & 0x1EL) != 0L)
				count--;
			if ((temp & 0xFL) != 0L)
				count--;
			break;
		case 3:
			count = 10;
			if ((temp & 0x104104L) != 0L)
				count--;
			if ((temp & 0x810204L) != 0L)
				count--;
			if ((temp & 0x3CL) != 0L)
				count--;
			if ((temp & 0x1EL) != 0L)
				count--;
			if ((temp & 0xFL) != 0L)
				count--;
			break;
		case 4:
			count = 8;
			if ((temp & 0x82082L) != 0L)
				count--;
			if ((temp & 0x408102L) != 0L)
				count--;
			if ((temp & 0x1EL) != 0L)
				count--;
			if ((temp & 0xFL) != 0L)
				count--;
			break;
		case 5:
			count = 6;
			if ((temp & 0x41041L) != 0L)
				count--;
			if ((temp & 0x204081L) != 0L)
				count--;
			if ((temp & 0xFL) != 0L)
				count--;
			break;
		default:
			count = 0;
			break;
		}
		cn[6] = count;

		// Sortiere
		int moves[] = new int[8];
		do {
			count = 0;
			for (i = 6; i >= 0; i--) {
				if (cn[i] > count) {
					count = cn[i];
					index = i;
				}
			}
			cn[index] = 0;
			moves[j++] = index;
		} while (count != 0);
		moves[j - 1] = -1;
		return moves;
	}

	/**
	 * @param player
	 * @param colHeight
	 *            Array with the Height of all Columns
	 * @param vTable
	 *            Values of each Column
	 * @return Move, that gets the best Value from vTable
	 */
	public int getBestMove(int player, int[] colHeight, double[] vTable, RandomDataGenerator random) {
		int fak = (player == PLAYER1 ? 1 : -1);
		int bestMove = -1;
		double bestVal = -9999.99;
		for (int i = 0; i < COLCOUNT; i++) {
			if (colHeight[i] < ROWCOUNT && vTable[i] * fak > bestVal) {
				bestMove = i;
				bestVal = vTable[i] * fak;
			}
		}
		return bestMove;
	}

}