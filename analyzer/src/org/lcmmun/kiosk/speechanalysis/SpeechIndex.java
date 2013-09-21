package org.lcmmun.kiosk.speechanalysis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Context;
import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Criterion;

/**
 * The index of speeches and analyses.
 * 
 * @author William Chargin
 * 
 */
public class SpeechIndex extends ArrayList<SpeechAnalysis> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Gets all speech comments for the given delegate.
	 * 
	 * @param delegateName
	 *            the delegate for whom to fetch comments
	 * @return the comments
	 */
	public List<String> getCommentsForDelegate(String delegateName) {
		ArrayList<String> commentList = new ArrayList<String>();
		for (SpeechAnalysis analysis : this) {
			if (analysis.delegateName.equals(delegateName)) {
				String comments = analysis.getComments();
				if (comments != null
						&& (!(comments = comments.trim()).isEmpty())) {
					commentList.add(comments);
				}
			}
		}
		return commentList;
	}

	/**
	 * Gets the CSV text for the speeches of the given delegate. The text
	 * consists of the delegate's name, followed by average values for each
	 * criterion, followed by the number of speeches in the general speakers'
	 * list, and then the number of speeches in moderated caucus.
	 * 
	 * @param delegateName
	 *            the name of the delegate for whom to generate the data
	 * @return the CSV text
	 */
	public String delegateCsv(String delegateName) {
		LinkedHashMap<Criterion, Integer> totals = new LinkedHashMap<Criterion, Integer>();
		int speakersList = 0, caucus = 0;
		int speechesByDelegate = 0;
		for (SpeechAnalysis analysis : this) {
			if (analysis.delegateName.equals(delegateName)) {
				speechesByDelegate++;
				for (Criterion criterion : analysis.scores.keySet()) {
					totals.put(
							criterion,
							(totals.containsKey(criterion) ? totals
									.get(criterion) : 0)
									+ analysis.scores.get(criterion));
				}
				if (analysis.context.equals(Context.SPEAKERS_LIST)) {
					speakersList++;
				} else if (analysis.context.equals(Context.MODERATED_CAUCUS)) {
					caucus++;
				}
			}
		}
		LinkedHashMap<Criterion, Double> averages = new LinkedHashMap<Criterion, Double>();
		for (Criterion criterion : totals.keySet()) {
			averages.put(
					criterion,
					speechesByDelegate == 0 ? 0 : (((double) totals
							.get(criterion)) / ((double) speechesByDelegate)));
		}

		StringBuffer sb = new StringBuffer();
		sb.append(delegateName);
		sb.append(SpeechAnalysis.DELIMITER);
		Criterion[] criteria = Criterion.values();
		DecimalFormat format = new DecimalFormat("##.##"); //$NON-NLS-1$
		for (int i = 0; i < criteria.length; i++) {
			Criterion criterion = criteria[i];
			sb.append(format.format(averages.get(criterion)));
			sb.append(SpeechAnalysis.DELIMITER);
		}
		sb.append(Integer.toString(speakersList));
		sb.append(SpeechAnalysis.DELIMITER);
		sb.append(Integer.toString(caucus));
		return sb.toString();
	}

	/**
	 * Generates CSV for each delegate.
	 * 
	 * @return the final CSV
	 */
	public String generateAverageCsv() {
		StringBuffer sb = new StringBuffer();
		sb.append(Messages.getString("SpeechIndex.CSV_Delegate")); //$NON-NLS-1$
		sb.append(SpeechAnalysis.DELIMITER);
		for (Criterion criterion : Criterion.values()) {
			sb.append(criterion.name);
			sb.append(SpeechAnalysis.DELIMITER);
		}
		sb.append(Messages.getString("SpeechIndex.CSV_GSLCount")); //$NON-NLS-1$
		sb.append(SpeechAnalysis.DELIMITER);
		sb.append(Messages.getString("SpeechIndex.CSV_Caucus")); //$NON-NLS-1$
		sb.append('\n');
		ArrayList<String> names = new ArrayList<String>();
		for (SpeechAnalysis analysis : this) {
			if (!names.contains(analysis.delegateName)) {
				names.add(analysis.delegateName);
			}
		}
		Collections.sort(names);
		for (String delegateName : names) {
			sb.append(delegateCsv(delegateName));
			sb.append('\n');
		}
		return sb.toString();
	}

	public String generateSpeechCsv() {
		StringBuffer sb = new StringBuffer();
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(Messages.getString("SpeechIndex.CSV_Delegate")); //$NON-NLS-1$
		headers.add(Messages.getString("SpeechIndex.CSV_Time")); //$NON-NLS-1$
		for (Criterion criterion : Criterion.values()) {
			headers.add(criterion.toString());
		}
		headers.add(Messages.getString("SpeechIndex.CSV_Comments")); //$NON-NLS-1$
		headers.add(Messages.getString("SpeechIndex.CSV_Context")); //$NON-NLS-1$
		Iterator<String> itHeaders = headers.iterator();
		while (itHeaders.hasNext()) {
			final String header = itHeaders.next();
			sb.append(header);
			if (itHeaders.hasNext()) {
				sb.append(SpeechAnalysis.DELIMITER);
			}
		}
		for (SpeechAnalysis analysis : this) {
			sb.append('\n');
			sb.append(analysis.generateCsv());
		}
		return sb.toString();
	}

	public int getTotalSpeechesByDelegate(String delegateName) {
		int speechesByDelegate = 0;
		for (SpeechAnalysis analysis : this) {
			if (analysis.delegateName.equals(delegateName)) {
				speechesByDelegate++;
			}
		}
		return speechesByDelegate;
	}

	public int getCaucusSpeechesByDelegate(String delegateName) {
		int caucus = 0;
		for (SpeechAnalysis analysis : this) {
			if (analysis.delegateName.equals(delegateName)) {
				if (analysis.context.equals(Context.MODERATED_CAUCUS)) {
					caucus++;
				}
			}
		}
		return caucus;
	}

	public int getGslSpeechesByDelegate(String delegateName) {
		int speakersList = 0;
		for (SpeechAnalysis analysis : this) {
			if (analysis.delegateName.equals(delegateName)) {
				if (analysis.context.equals(Context.SPEAKERS_LIST)) {
					speakersList++;
				}
			}
		}
		return speakersList;
	}

	/**
	 * Gets the average scores of a delegate.
	 * 
	 * @param delegateName
	 *            the delegate
	 * @return the scores
	 */
	public Map<Criterion, Double> getDelegateAverage(String delegateName) {
		LinkedHashMap<Criterion, Integer> totals = new LinkedHashMap<Criterion, Integer>();
		int speechesByDelegate = 0;
		for (SpeechAnalysis analysis : this) {
			if (analysis.delegateName.equals(delegateName)) {
				speechesByDelegate++;
				for (Criterion criterion : analysis.scores.keySet()) {
					totals.put(
							criterion,
							(totals.containsKey(criterion) ? totals
									.get(criterion) : 0)
									+ analysis.scores.get(criterion));
				}
			}
		}
		LinkedHashMap<Criterion, Double> averages = new LinkedHashMap<Criterion, Double>();
		for (Criterion criterion : totals.keySet()) {
			averages.put(
					criterion,
					speechesByDelegate == 0 ? 0 : (((double) totals
							.get(criterion)) / ((double) speechesByDelegate)));
		}
		return averages;
	}

	/**
	 * Gets the date and time of this delegate's most recent speech, or
	 * {@code null} if no delegate by the given name has spoken.
	 * 
	 * @param delegateName
	 *            the delegate's name
	 * @return the last speech time
	 */
	public Date getLastSpeechTime(String delegateName) {
		ArrayList<SpeechAnalysis> backwards = new ArrayList<SpeechAnalysis>(
				this);
		Collections.reverse(backwards); // NOW it's backwards.
		for (SpeechAnalysis analysis : backwards) {
			if (analysis.delegateName.equals(delegateName)) {
				return analysis.creationDate;
			}
		}
		return null;
	}
}
