/*
 * Copyright (C) 2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE Test Library.
 * 
 * The ULTIMATE Test Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE Test Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Test Library. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Test Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Test Library grant you additional permission
 * to convey the resulting work.
 */
/**
 * 
 */
package de.uni_freiburg.informatik.ultimate.ultimatetest.suites.traceabstraction;

import java.util.Collection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import de.uni_freiburg.informatik.ultimate.test.UltimateRunDefinition;
import de.uni_freiburg.informatik.ultimate.test.UltimateTestCase;
import de.uni_freiburg.informatik.ultimate.test.decider.ITestResultDecider;
import de.uni_freiburg.informatik.ultimate.test.decider.SomeVerificationResultTestResultDecider;
import de.uni_freiburg.informatik.ultimate.test.util.DirectoryFileEndingsPair;
import de.uni_freiburg.informatik.ultimate.test.util.UltimateRunDefinitionGenerator;
import de.uni_freiburg.informatik.ultimate.ultimatetest.suites.buchiautomizer.AbstractBuchiAutomizerTestSuite;

/**
 * @author heizmann@informatik.uni-freiburg.de
 * 
 */
public class TerminationNcsbComparisonAllSvcompSunflower extends AbstractBuchiAutomizerTestSuite {

	/** Limit the number of files per directory. */

	private static int FILES_PER_DIR_LIMIT = Integer.MAX_VALUE;

//	 private static int mFilesPerDirectoryLimit = 1;
	 private static final int FILE_OFFSET = 0;

	// @formatter:off
	private static final String STANDARD_DOT_C_PATTERN = ".*.i|.*.c";
	

	private static final String ALL_C = ".*_false-unreach-call.*\\.c|.*_true-unreach-call.*\\.c|.*_false-no-overflow.*\\.c|.*_true-no-overflow.*\\.c|.*_false-valid-deref.*\\.c|.*_false-valid-free.*\\.c|.*_false-valid-memtrack.*\\.c|.*_true-valid-memsafety.*\\.c";
	private static final String ALL_I = ".*_false-unreach-call.*\\.i|.*_true-unreach-call.*\\.i|.*_false-no-overflow.*\\.i|.*_true-no-overflow.*\\.i|.*_false-valid-deref.*\\.i|.*_false-valid-free.*\\.c|.*_false-valid-memtrack.*\\.c|.*_true-valid-memsafety.*\\.i";

	private static final String BITVECTOR_FOLDER_DOT_C_PATTERN =
			".*_false-unreach-call.*\\.c|.*_true-unreach-call.*\\.c.cil.c";

	// private static final String STANDARD_DOT_C_PATTERN = ".*_false-unreach-call.*\\.c";
	// private static final String STANDARD_DOT_I_PATTERN = ".*_false-unreach-call.*\\.i";

	// @formatter:off
	private static final DirectoryFileEndingsPair[] mDirectoryFileEndingsPairs = {
//		/***** Category 1. ReachSafety *****/
//		/*** Subcategory    ReachSafety-Arrays ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/array-examples/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/array-industry-pattern/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/reducercommutativity/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//
//		/*** Subcategory   ReachSafety-BitVectors ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/bitvector/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/bitvector-regression/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/bitvector-loops/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//			
//		/*** Subcategory   ReachSafety-ControlFlow ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ntdrivers-simplified/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ssh-simplified/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/locks/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ntdrivers/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ssh/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory   ReachSafety-ReachSafety-ECA ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/eca-rers2012/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/psyco/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory   ReachSafety-Heap ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/heap-manipulation/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/list-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ldv-regression/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		// TODO: add ldv-regression/test[0-9][0-9]_false-unreach-call*.c ldv-regression/test[0-9][0-9]_true-unreach-call*.c
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ddv-machzwd/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/forester-heap/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/list-ext-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/list-ext2-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory    ReachSafety-Floats ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/floats-cdfpl/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/floats-cbmc-regression/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/float-benchs/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/floats-esbmc-regression/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//
//		/*** Subcategory   ReachSafety-Loops ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/loops/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/loop-acceleration/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/loop-invgen/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/loop-lit/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/loop-new/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/loop-industry-pattern/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		
//		
//		/***** Category 2. MemSafety *****/
//		/*** Subcategory    MemSafety-Arrays ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/array-memsafety/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/array-examples/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/reducercommutativity/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//
//		/*** Subcategory   MemSafety-Heap ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/memsafety/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/list-ext-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/memory-alloca/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ldv-memsafety/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ldv-memsafety/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ldv-memsafety-bitfields/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/bitvector-loops/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory    MemSafety-LinkedLists ***/
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/heap-manipulation/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/forester-heap/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/list-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		new DirectoryFileEndingsPair("examples/sv-benchmarks/c/ddv-machzwd/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),

	};
	

	// @formatter:off
	private static final DirectoryFileEndingsPair[] mDirectorySVCompNoCallReturn = {
//		/***** Category 1. ReachSafety *****/
//		/*** Subcategory    ReachSafety-Arrays ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/array-examples/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/reducercommutativity/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//
//		/*** Subcategory   ReachSafety-BitVectors ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/bitvector/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/bitvector-regression/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/bitvector-loops/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//			
//		/*** Subcategory   ReachSafety-ControlFlow ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ntdrivers-simplified/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ssh-simplified/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/locks/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ssh/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory   ReachSafety-ReachSafety-ECA ***/
//		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/eca-rers2012/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/psyco/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory   ReachSafety-Heap ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/heap-manipulation/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/list-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ldv-regression/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		// TODO: add ldv-regression/test[0-9][0-9]_false-unreach-call*.c ldv-regression/test[0-9][0-9]_true-unreach-call*.c
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ddv-machzwd/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/forester-heap/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/list-ext-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/list-ext2-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory    ReachSafety-Floats ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/floats-cdfpl/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/floats-cbmc-regression/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/float-benchs/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/floats-esbmc-regression/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//
//		/*** Subcategory   ReachSafety-Loops ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/loops/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/loop-acceleration/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/loop-invgen/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/loop-lit/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/loop-new/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/loop-industry-pattern/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		
//		
//		/***** Category 2. MemSafety *****/
//		/*** Subcategory    MemSafety-Arrays ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/array-memsafety/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/array-examples/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/reducercommutativity/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//
//		/*** Subcategory   MemSafety-Heap ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/memsafety/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/list-ext-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/memory-alloca/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ldv-memsafety/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ldv-memsafety/", new String[]{ ALL_C }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ldv-memsafety-bitfields/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/bitvector-loops/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
//		
//		/*** Subcategory    MemSafety-LinkedLists ***/
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/heap-manipulation/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/forester-heap/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/list-properties/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),
		new DirectoryFileEndingsPair("examples/svcomp-no-call-return/ddv-machzwd/", new String[]{ ALL_I }, FILE_OFFSET,  FILES_PER_DIR_LIMIT),

	};

	private static final String[] mCurrentBugs = {};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTimeout() {
		return 900 * 1000;
	}

	/**
	 * List of path to setting files. Ultimate will run on each program with each setting that is defined here. The path
	 * are defined relative to the folder "trunk/examples/settings/", because we assume that all settings files are in
	 * this folder.
	 * 
	 */
	private static final String[] mSettings = { 
			
//			"buchiAutomizer/ncsb/SUNFLOWER-ORIGINAL-SAVE-AUT.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_LAZY2-SAVE-AUT.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_LAZY3-SAVE-AUT.epf",
//			"buchiAutomizer/ncsb/ORIGINAL-SAVE-AUT.epf",
//			"buchiAutomizer/ncsb/INTSET_LAZY2-SAVE-AUT.epf",
//			"buchiAutomizer/ncsb/INTSET_LAZY3-SAVE-AUT.epf",
//			"buchiAutomizer/ncsb/INTSET_LAZY2.epf",
//			"buchiAutomizer/ncsb/INTSET_LAZY.epf",
//			"buchiAutomizer/ncsb/INTSET.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_LAZY.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_LAZY2.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_LAZY3.epf",
//			"buchiAutomizer/ncsb/INTSET_GBA.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_GBA.epf",
//			"buchiAutomizer/ncsb/INTSET_GBA_ANTICHAIN.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_GBA_ANTICHAIN.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-INTSET_LAZY3.epf",
//			"buchiAutomizer/ncsb/ORIGINAL.epf",
//			"buchiAutomizer/ncsb/SUNFLOWER-ORIGINAL.epf", //
//			"buchiAutomizer/ncsb/A-ORIGINAL.epf", // svcomp
//			"buchiAutomizer/ncsb/ROSE-ORIGINAL.epf", //FA, NBA
//			"buchiAutomizer/ncsb/DAISY-ORIGINAL.epf", //CAV 14
				       // sunflower setting
		       "buchiAutomizer/ncsb/SUNFLOWER_BA.epf",
		       "buchiAutomizer/ncsb/SUNFLOWER_BA_LAZYS.epf",
		       "buchiAutomizer/ncsb/SUNFLOWER_GBA_LAZYS.epf",
		       "buchiAutomizer/ncsb/SUNFLOWER_GBA_LAZYS_ANTICHAIN.epf",
		
		       // svcomp setting
//		       "buchiAutomizer/ncsb/RHODODENDRON_BA.epf",
//		       "buchiAutomizer/ncsb/RHODODENDRON_BA_LAZYS.epf",
//		       "buchiAutomizer/ncsb/RHODODENDRON_GBA_LAZYS.epf",
//		       "buchiAutomizer/ncsb/RHODODENDRON_GBA_LAZYS_ANTICHAIN.epf",
	};

	private static final String[] mCToolchains = {
			 "BuchiAutomizerCInlineWithBlockEncoding.xml",
//			"BuchiAutomizerCInline.xml", 
		};

	
    @Override
    public ITestResultDecider constructITestResultDecider(final UltimateRunDefinition ultimateRunDefinition) {
            return new SomeVerificationResultTestResultDecider(ultimateRunDefinition);
    }
    
    
	@Override
	public Collection<UltimateTestCase> createTestCases() {
		int mNumberOfMachines = 1;
                int mCurrentMachineNumber = 0;

                try(BufferedReader br = new BufferedReader(new FileReader("machine.conf"))) {
                    String line = br.readLine();
                    mNumberOfMachines = Integer.parseInt(line.substring(0, line.indexOf(' ')));
                    line = br.readLine();
                    mCurrentMachineNumber = Integer.parseInt(line.substring(0, line.indexOf(' ')));

                } catch (Exception e) {
                        //use single machine
                        mNumberOfMachines = 1;
                        mCurrentMachineNumber = 0;
                }

                File infoFile = new File("Info.log");
                if(infoFile.exists()) {
                        infoFile.delete();
                }
		DirectoryFileEndingsPair[] mPairsToTry=mDirectorySVCompNoCallReturn;
		
		for (final DirectoryFileEndingsPair dfep : mPairsToTry) {
			for (final String toolchain : mCToolchains) {
				addTestCase(UltimateRunDefinitionGenerator.getRunDefinitionsFromTrunkRegex(
						new String[] { dfep.getDirectory() }, dfep.getFileEndings(), mSettings, toolchain, getTimeout(),
						dfep.getOffset(), dfep.getLimit()));
			}
		}
		return super.createTestCasesMultipleMachine(mNumberOfMachines,mCurrentMachineNumber,mSettings.length);		
	}
	// @formatter:on
}

