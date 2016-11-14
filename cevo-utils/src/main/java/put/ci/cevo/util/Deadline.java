package put.ci.cevo.util;

/**
 * This immutable class represents a point in time (a deadline).
 * 
 * <li><code>new Deadline(123)</code> will expire in 123ms</li> <li>
 * <code>deadline.getShortenedBy(321)</code> will expire 321ms before
 * <code>deadline</code></li>
 * 
 * @author Bartek
 * 
 */
public final class Deadline {
	private final long expirationTime;

	/**
	 * Creates a new deadline which will expire in
	 * <code>timeToDeadlineInMilliseconds</code>
	 */
	public Deadline(long timeToDeadlineExpireInMilliseconds) {
		expirationTime = System.currentTimeMillis() + timeToDeadlineExpireInMilliseconds;
	}

	static public Deadline NoDeadline() {
		return new Deadline(Long.MAX_VALUE - System.currentTimeMillis() - 100000);
	}

	/**
	 * Checks whether the deadline has expired
	 */
	public boolean hasExpired() {
		return System.currentTimeMillis() >= expirationTime;
	}

	/**
	 * Returns a new deadline which will expire <code>milliseconds</code>
	 * earlier than this deadline.
	 */
	public Deadline getShortenedBy(long milliseconds) {
		return new Deadline(getTimeToExpireMilliSeconds() - milliseconds);
	}

	/**
	 * Returns a shortened version of the deadline. The new deadline will expire
	 * no later than in <code>milliseconds</code>
	 * <p>
	 * It is equivalent to
	 * <code>Deadline.min(this, new Deadline(milliseconds))</code>
	 * </p>
	 */
	public Deadline getTrimmedTo(long milliseconds) {
		return new Deadline(Math.min(getTimeToExpireMilliSeconds(), milliseconds));
	}

	/**
	 * Checks if a shortened deadline has expired.
	 * 
	 * <p>
	 * It is equivalent to <code>this.shorten(milliseconds).isExceeded()</code>
	 * 
	 * @deprecated Sorry, ale to jest kiepska funkcja i niezrozumiala dosyc,
	 *             wiec nalezy jej nie uzywac i ja wyrzucic
	 */
	@Deprecated
	public boolean shortenedHasExpired(long milliseconds) {
		return System.currentTimeMillis() >= expirationTime - milliseconds;
	}

	/**
	 * Returns deadline which expires first
	 */
	public static Deadline min(Deadline a, Deadline b) {
		return a.expirationTime <= b.expirationTime ? a : b;
	}

	public long getTimeToExpireMilliSeconds() {
		long dt = expirationTime - System.currentTimeMillis();
		return dt > 0 ? dt : 0;
	}
}
