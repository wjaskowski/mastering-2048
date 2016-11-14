package put.ci.cevo.games;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.board.BoardUtils;

public class BoardUtilsTest {

	@Test
	public final void testMarginPosToPos() throws Exception {
		int actual = BoardUtils.marginPosToPos(5, 2);
		Assert.assertEquals(0, actual);

		actual = BoardUtils.marginPosToPos(6, 2);
		Assert.assertEquals(1, actual);

		actual = BoardUtils.marginPosToPos(9, 2);
		Assert.assertEquals(2, actual);

		actual = BoardUtils.marginPosToPos(10, 2);
		Assert.assertEquals(3, actual);

		actual = BoardUtils.marginPosToPos(10, 3, 5);
		Assert.assertEquals(2, actual);

		actual = BoardUtils.marginPosToPos(25, 6, 7);
		Assert.assertEquals(13, actual);

	}
}
