/**
 *
 */
package playground.yu.utils.math;

/**
 * just a naive method to judge whether a double array would probably be
 * convergent a foreseeable while, this class might be used after preparatory
 * iterations (warm up). Only the necessary conditions for convergence is
 * described here.
 *
 * @author yu
 *
 */
public class SoonConvergent {
	/**
	 * @param amplitudeCriterion
	 *            criterion for the difference between the highest and lowest
	 *            value in each half of the array, the absolute value of the
	 *            second difference may be smaller than the absolute value of
	 *            the first difference * this amplitudeCriterion, z.B. 0.7, 0.6
	 *            ...
	 * @param avgValueCriterion
	 *            the average value of the second half of the array may not
	 *            exceed a rang of +/- avgValueCriterion with the average value
	 *            of the first half as the center, should stand in the range of
	 *            (0,1), should be a positive value <0.2, e.g. 0.1, 0.05
	 * @param values
	 *            a double array, please had better ensure that the array length
	 *            is a even number
	 * @return boolean value, whether the array values would be soon convergent
	 */
	public static boolean wouldBe(double amplitudeCriterion,
			double avgValueCriterion, double[] values) {

		int size1 = values.length / 2;

		double min1 = SimpleStatistics.min(values, 0, size1 - 1)//
		, max1 = SimpleStatistics.max(values, 0, size1 - 1)//
		, min2 = SimpleStatistics.min(values, size1, values.length - 1)//
		, max2 = SimpleStatistics.max(values, size1, values.length - 1);

		boolean firstCondition = Math.abs(max2 - min2) <= amplitudeCriterion
				* Math.abs(max1 - min1);

		if (firstCondition) {
			return true;
		} else {
			double avg1 = SimpleStatistics.average(values, 0, size1 - 1)//
			, avg2 = SimpleStatistics.average(values, size1, values.length - 1);

			boolean secondCondition = false;
			if (avg1 != 0d) {
				if (Math.abs(avg1) < 0.5 || Math.abs(avg2) < 0.5) {
					avgValueCriterion *= 2d / (Math.abs(avg1) + Math.abs(avg2));
				}
				secondCondition = Math.abs((avg2 - avg1) / avg1) <= avgValueCriterion;
				if (Math.abs((avg2 - avg1) / avg1) > 0.7) {
					System.err
							.println("Math.abs((avg2 - avg1) / avg1)>0.7 --> 1. maybe the proparatoryIteration too short, or 2. the initialStepSize to small, or 3 the calibrated parameter stands next to 0.");
				}
			} else {
				secondCondition = Math.abs(avg2) <= avgValueCriterion;
			}

			return secondCondition;
		}
	}
}
