package org.lcmmun.kiosk.motions;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A way of comparing motions based on their precedence: the most disruptive
 * motions have higher predence. Less disruptive motions are considered
 * "smaller" than more disruptive motions and will come first when sorted; you
 * may with to use the {@link Collections#reverseOrder(Comparator)} function to
 * sort from most to least disruptive.
 * 
 * @author William Chargin
 * 
 */
public class PrecedenceComparator implements Comparator<Motion> {

	/**
	 * The list of precedence, from least disruptive to most disruptive.
	 */
	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Motion>> PRECEDENCE = Collections
			.unmodifiableList(Arrays.<Class<? extends Motion>> asList(
					AgendaMotion.class, DivisionOfTheQuestionMotion.class,
					TableDebateMotion.class, CloseDebateMotion.class,
					IntroduceWorkingPaperMotion.class,
					SpeakingTimeMotion.class, ModeratedCaucusMotion.class,
					FormalCaucusMotion.class, UnmoderatedCaucusMotion.class));

	@Override
	public int compare(Motion m1, Motion m2) {
		int i1 = PRECEDENCE.indexOf(m1.getClass());
		int i2 = PRECEDENCE.indexOf(m2.getClass());

		if (i1 != -1 && i2 != -1) {
			// Both in the list.
			if (i1 < i2) {
				return -1;
			} else if (i1 > i2) {
				return 1;
			} else {
				if (m1 instanceof CaucusMotion && m2 instanceof CaucusMotion) {
					int t1 = ((CaucusMotion) m1).getTotalTime()
							.getTotalSeconds();
					int t2 = ((CaucusMotion) m2).getTotalTime()
							.getTotalSeconds();

					if (t1 < t2) {
						return -1; // less disruptive
					} else if (t1 > t2) {
						return 1; // more disruptive
					} else {
						return 0; // equal
					}
				} else {
					return 0;
				}
			}
		} else {
			// We don't really know.
			return 0;
		}
	}

}
