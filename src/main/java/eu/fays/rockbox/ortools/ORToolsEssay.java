package eu.fays.rockbox.ortools;

import java.io.File;
import java.util.logging.Logger;

import com.google.ortools.constraintsolver.ConstraintSolverParameters;
import com.google.ortools.constraintsolver.DecisionBuilder;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.Solver;
import com.google.ortools.init.OrToolsVersion;

// Zsh
/*
mvn clean dependency:copy-dependencies package
find ~/.m2/repository/com/google/ortools -type f -name '*64-9.12.4544.jar' -exec sh -c 'cd $(dirname $1) && jar xvf $1' _ {} \;
find ~/.m2/repository/com/google/ortools -type f -name '*jniortools*' -exec dirname {} \; | cut -c $((${#HOME}+2))- | sed 's|^|${system_property:user.home}/|' | paste -s -d ":" -
 */

// VM Args
/*
-Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"
-Djava.library.path=${system_property:user.home}/.m2/repository/com/google/ortools/ortools-win32-x86-64/9.12.4544/ortools-win32-x86-64:${system_property:user.home}/.m2/repository/com/google/ortools/ortools-darwin-aarch64/9.12.4544/ortools-darwin-aarch64:${system_property:user.home}/.m2/repository/com/google/ortools/ortools-linux-x86-64/9.12.4544/ortools-linux-x86-64:${system_property:user.home}/.m2/repository/com/google/ortools/ortools-linux-aarch64/9.12.4544/ortools-linux-aarch64:${system_property:user.home}/.m2/repository/com/google/ortools/ortools-darwin-x86-64/9.12.4544/ortools-darwin-x86-64
*/

/**
 * OR-Tools essay 
 */
@SuppressWarnings("nls")
public class ORToolsEssay {

	/** Java command */
	public static final String JAVA_COMMAND = """
java -cp "target/eu.fays.rockbox-1.0.0.jar:$(ls -1 target/dependency/*.jar | paste -s -d ':' -)" \
  -Djava.util.logging.SimpleFormatter.format='%5$s%6$s%n' \
  -Djava.library.path="$(dirname ~/.m2/repository/com/google/ortools/**/*jniortools* | paste -s -d ':' -)" \
  eu.fays.rockbox.ortools.ORToolsEssay
""";
	
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(ORToolsEssay.class.getName());

	/** rabbits */
	private static final String RABBITS = "rabbits";

	/** pheasants */
	private static final String PHEASANTS = "pheasants";

	/**
	 * Main
	 * @param args unused
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			final File javaLibraryPath = new File(System.getProperty("java.library.path"));
			System.load(new File(javaLibraryPath, "zlib1.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "utf8_validity.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "highs.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "abseil_dll.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "re2.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "libprotobuf.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "ortools.dll").getAbsolutePath());
			System.load(new File(javaLibraryPath, "jniortools.dll").getAbsolutePath());
		}
		
		System.loadLibrary("jniortools");
		LOGGER.info("OR-Tools: " + OrToolsVersion.getVersionString());
		LOGGER.info("Foreword: We are seing 20 heads and 56 legs.");
		LOGGER.info("Question: How many " + RABBITS + " and how many " + PHEASANTS + " are we thus seeing?");

		
		final long t0 = System.nanoTime();
		final ConstraintSolverParameters parameters = ConstraintSolverParameters.newBuilder().mergeFrom(Solver.defaultSolverParameters()).setTraceSearch(false).build();
		final Solver solver = new Solver(RABBITS + "&" + PHEASANTS, parameters);

		final IntVar rabbits = solver.makeIntVar(0, 100, RABBITS);
		final IntVar pheasants = solver.makeIntVar(0, 100, PHEASANTS);
		solver.addConstraint(solver.makeEquality(solver.makeSum(rabbits, pheasants), 20));
		solver.addConstraint(solver.makeEquality(solver.makeSum(solver.makeProd(rabbits, 4), solver.makeProd(pheasants, 2)), 56));
		final DecisionBuilder decisionBuilder = solver.makePhase(rabbits, pheasants, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE);
		solver.newSearch(decisionBuilder);
		solver.nextSolution();
		final long t1 = System.nanoTime();
		LOGGER.info("Solution: there is " + rabbits.value() + " " + RABBITS + " and " + pheasants.value() + " " + PHEASANTS + "!");
		solver.endSearch();
		final long delta = (t1 - t0) / 1_000_000L;
		LOGGER.info("Duration: " + delta + " ms");
	}

}
