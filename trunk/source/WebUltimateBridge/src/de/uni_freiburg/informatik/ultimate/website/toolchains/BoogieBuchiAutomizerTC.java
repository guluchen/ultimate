package de.uni_freiburg.informatik.ultimate.website.toolchains;

import java.util.ArrayList;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.website.Setting;
import de.uni_freiburg.informatik.ultimate.website.Tasks.TaskNames;
import de.uni_freiburg.informatik.ultimate.website.Tool;
import de.uni_freiburg.informatik.ultimate.website.WebToolchain;


public class BoogieBuchiAutomizerTC extends WebToolchain {

	@Override
	protected String defineDescription() {
		return "Büchi Automizer toolchain";
	}

	@Override
	protected String defineName() {
		return "Büchi Automizer";
	}

	@Override
	protected String defineId() {
		return "boogieBuchiAutomizer";
	}

	@Override
	protected TaskNames[] defineTaskName() {
		return new TaskNames[] { TaskNames.TERMINATION_BOOGIE };
	}

    @Override
    protected String defineLanguage() {
        return "boogie";
    }

	@Override
	protected List<Tool> defineTools() {
		return boogieBuchiAutomizerToolchain();
	}
	
	@Override
	protected List<Setting> defineAdditionalSettings() {
		return boogieBuchiAutomizerAdditionalSettings();
	}
	
	static List<Tool> boogieBuchiAutomizerToolchain() {
		List<Tool> tools = new ArrayList<Tool>();
		
		tools.add(new Tool(PrefStrings.s_boogiePreprocessor));
		tools.add(new Tool(PrefStrings.s_rcfgBuilder));
		tools.add(new Tool(PrefStrings.s_blockencoding));
		tools.add(new Tool(PrefStrings.s_buchiautomizer));
		
		return tools;
	}
	
	static List<Setting> boogieBuchiAutomizerAdditionalSettings() {
		List<Setting> rtr = new ArrayList<>();
		rtr.add(new Setting(PrefStrings.s_BE_LABEL_STRATEGY, PrefStrings.s_BE_LABEL_STRATEGY,
        		new String[] { PrefStrings.s_BE_VALUE_DisjunctiveRating }, false, new String[] {
				PrefStrings.s_BE_VALUE_DisjunctiveRating, PrefStrings.s_BE_VALUE_LargeBlock }, true));

		return rtr;
	}
}
