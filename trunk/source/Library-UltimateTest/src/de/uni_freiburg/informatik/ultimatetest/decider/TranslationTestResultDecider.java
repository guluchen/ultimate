package de.uni_freiburg.informatik.ultimatetest.decider;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;

import de.uni_freiburg.informatik.ultimate.core.services.IResultService;
import de.uni_freiburg.informatik.ultimate.result.ExceptionOrErrorResult;
import de.uni_freiburg.informatik.ultimate.result.IResult;
import de.uni_freiburg.informatik.ultimate.result.SyntaxErrorResult;
import de.uni_freiburg.informatik.ultimate.result.TypeErrorResult;
import de.uni_freiburg.informatik.ultimatetest.util.Util;

/**
 * 
 * @author dietsch
 * 
 */
public class TranslationTestResultDecider extends TestResultDecider {

	private String mInputFile;

	public TranslationTestResultDecider(String inputFile) {
		mInputFile = inputFile;
	}

	@Override
	public TestResult getTestResult(IResultService resultService) {

		setResultCategory("");
		setResultMessage("");

		Logger log = Logger.getLogger(TranslationTestResultDecider.class);
		Collection<String> customMessages = new LinkedList<String>();
		customMessages.add("Expecting results to have a counterexample that matches the .bpl file, "
				+ "and no generic result \"Unhandled Backtranslation\"");
		boolean fail = false;
		Set<Entry<String, List<IResult>>> resultSet = resultService.getResults().entrySet();
		if (resultSet.size() == 0) {
			setResultCategory("No results");
			customMessages.add("There were no results (this is good for this test)");
		} else {
			for (Entry<String, List<IResult>> x : resultSet) {
				for (IResult result : x.getValue()) {
					if (result instanceof TypeErrorResult || result instanceof SyntaxErrorResult
							|| result instanceof ExceptionOrErrorResult) {
						setResultCategory(result.getShortDescription());
						fail = true;
						break;
					}
				}
			}
		}

		if (!fail) {
			// There were no exceptions.
			// We need to compare the existing .bpl file against the one
			// generated by Boogie Printer.
			// If there are no existing files, we just assume it was only a test
			// against syntax errors.

			File inputFile = new File(mInputFile);
			String inputFileNameWithoutEnding = inputFile.getName().replaceAll("\\.c", "");
			File desiredBplFile = new File(String.format("%s%s%s%s", inputFile.getParentFile().getAbsolutePath(),
					Path.SEPARATOR, inputFileNameWithoutEnding, ".bpl"));
			File actualBplFile = Util.getFilesRegex(inputFile.getParentFile(),
					new String[] { String.format(".*%s\\.bpl", inputFileNameWithoutEnding) }).toArray(new File[1])[0];
			if (actualBplFile != null) {

				try {
					String desiredContent = de.uni_freiburg.informatik.ultimate.core.util.Util.readFile(desiredBplFile);
					String actualContent = de.uni_freiburg.informatik.ultimate.core.util.Util.readFile(actualBplFile);

					if (!desiredContent.equals(actualContent)) {
						String message = "Desired content does not match actual content.";
						setResultCategory("Mismatch between .bpl and .c");
						setResultMessage(message);
						customMessages.add(message);
						customMessages.add("Desired content:");
						for (String s : desiredContent.split("\n")) {
							customMessages.add(s);
						}
						customMessages.add("Actual content:");
						for (String s : actualContent.split("\n")) {
							customMessages.add(s);
						}
						fail = true;
					} else {
						setResultCategory(".bpl file equals expected .bpl file");
					}

				} catch (IOException e) {
					setResultCategory(e.getMessage());
					setResultMessage(e.toString());
					e.printStackTrace();
					fail = true;
				}
			} else {
				if (getResultCategory().isEmpty() && !fail) {
					setResultCategory("no .bpl file for comparison, but no reason to fail");
				}
				customMessages.add(String.format("There is no .bpl file for %s!", mInputFile));
			}

		}

		Util.logResults(log, mInputFile, fail, customMessages, resultService);
		return fail ? TestResult.FAIL : TestResult.SUCCESS;
	}

	@Override
	public TestResult getTestResult(IResultService resultService, Throwable e) {
		return TestResult.FAIL;
	}

}