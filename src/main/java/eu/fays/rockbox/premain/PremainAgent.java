package eu.fays.rockbox.premain;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

public class PremainAgent {

	private static volatile Instrumentation instrumentation;

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(PremainAgent.class.getName());

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		PremainAgent.instrumentation = instrumentation;
	}

	public static void logLoadedClasses(final ClassLoader classLoader) {
		if (instrumentation != null) {
			for(ClassLoader loader = classLoader; loader != null; loader = loader.getParent()) {
				for (final Class<?> clazz : instrumentation.getInitiatedClasses(loader)) {
					LOGGER.info(loader.getName() + ":" + clazz.getName());
				}
			}
		} else {
			LOGGER.severe("Agent not initialized!");
		}
	}
}
