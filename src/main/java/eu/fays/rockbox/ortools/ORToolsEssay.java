package eu.fays.rockbox.ortools;

import static java.io.File.pathSeparator;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.Files.walk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.ortools.Loader;
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
java -cp "$(ls -1 target/**/*.jar | paste -s -d ':' -)" \
  -Djava.util.logging.SimpleFormatter.format='%5$s%6$s%n' \
  -Djava.library.path="$(dirname ~/.m2/repository/com/google/ortools/**/*jniortools* | paste -s -d ':' -)" \
  eu.fays.rockbox.ortools.ORToolsEssay
""";
	
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(ORToolsEssay.class.getName());

	/** java.library.path */
	private static final String JAVA_LIBRARY_PATH = "java.library.path";

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
			LOGGER.fine(JAVA_LIBRARY_PATH + "=" +System.getProperty(JAVA_LIBRARY_PATH));
			final String javaLibraryPath = System.getProperty(JAVA_LIBRARY_PATH);
			LOGGER.info(JAVA_LIBRARY_PATH + "==" + javaLibraryPath);
			final String[] javaLibraryPathElements = javaLibraryPath.split(pathSeparator);
			final Path orToolsLibraryPath = Stream.of(javaLibraryPathElements).flatMap(p -> { try { return walk(Path.of(p), FOLLOW_LINKS); } catch(IOException e) {return Stream.empty();}}).filter(p -> "jniortools.dll".equals(p.getFileName().toString())).findFirst().orElse(null);
			if(orToolsLibraryPath != null) {
				final File orToolsLibraryFolder = orToolsLibraryPath.toFile().getParentFile();
				// the order of the dll is important.
				final String[] orToolsLibraryFilenames = {
					"zlib1.dll",
					"utf8_validity.dll",
					"highs.dll",
					"abseil_dll.dll",
					"re2.dll", // requires "abseil_dll.dll"
					"libprotobuf.dll", // requires "abseil_dll.dll", "utf8_validity.dll", "zlib1.dll"
					"ortools.dll", // requires "abseil_dll.dll", "utf8_validity.dll", "zlib1.dll", "highs.dll"
					"jniortools.dll" // requires "abseil_dll.dll", "utf8_validity.dll", "zlib1.dll", "highs.dll", "ortools.dll"
				};
				for(final String filename : orToolsLibraryFilenames) {
					final File file = new File(orToolsLibraryFolder, filename);
					assert file.exists();
					assert file.isFile();
					assert file.canRead();
					assert file.canExecute();
					System.load(file.getAbsolutePath());
				}
			}
			System.loadLibrary("jniortools");
		} else {
			Loader.loadNativeLibraries();
		}
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
