package de.uni_freiburg.informatik.ultimate.plugins.generator.automatascriptinterpreter;



import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;



/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String s_PLUGIN_ID = "AutomataScriptInterpreter";

	// The plug-in name
	public static final String s_PLUGIN_NAME = "AutomataScriptInterpreter";
	// The shared instance
	private static Activator m_Plugin;
	
	private ScopedPreferenceStore preferences;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		m_Plugin = this;
		preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE,Activator.s_PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		m_Plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return m_Plugin;
	}
	
	public IPreferenceStore getPreferenceStore() {
		return preferences;
	}

}
