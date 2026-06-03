package eu.fays.rockbox.ortools;

import static java.io.File.pathSeparator;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isExecutable;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.walk;
import static java.text.MessageFormat.format;
import static java.util.logging.Level.SEVERE;

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
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import eu.fays.rockbox.premain.PremainAgent;

// Unziping the OR-Tools libraries from the jar file is NOT required,
// because the method Loader.loadNativeLibraries() unzip them in a temporary folder, before loading them.
// Therefore it is not required to have them present on the java.library.path as well.

// Google OR-Tools requires the installation of the Microsoft Visual C++ Redistributable libraries when running on Windows
// [Microsoft Latest Supported Visual C++ Downloads page](https://learn.microsoft.com/en-us/cpp/windows/latest-supported-vc-redist?view=msvc-170)

// Zsh

// mvn clean dependency:copy-dependencies package
// mvn exec:java@ORToolsEssay

// java -cp "$(ls -1 target/*.jar target/dependency/*.jar | paste -s -d ':' -)" -Djava.util.logging.SimpleFormatter.format='%5$s%6$s%n' eu.fays.rockbox.ortools.ORToolsEssay & sudo fs_usage $!
// java -cp "$(ls -1 target/*.jar target/dependency/*.jar | paste -s -d ':' -)" -Djava.util.logging.SimpleFormatter.format='%5$s%6$s%n' -Djava.library.path="target/ortools-$(uname -s|sed 's/MINGW.*/win32/;s/Linux/linux/;s/Darwin/darwin/')-$(uname -m | sed s/arm/aarch/ | tr '_' '-')" eu.fays.rockbox.ortools.ORToolsEssay & sudo fs_usage $!
// java -cp "$(ls -1 target/*.jar target/dependency/*.jar | paste -s -d ':' -)" -Djava.util.logging.SimpleFormatter.format='%5$s%6$s%n' -Djava.library.path="target/ortools-$(uname -s|sed 's/MINGW.*/win32/;s/Linux/linux/;s/Darwin/darwin/')-$(uname -m | sed s/arm/aarch/ | tr '_' '-')" -javaagent:target/eu.fays.rockbox-1.0.0.jar eu.fays.rockbox.ortools.ORToolsEssay
//

// find ~/.m2/repository/com/google/ortools -type f -name '*64-9.14.6206.jar' -exec sh -c 'cd $(dirname $1) && jar xvf $1' _ {} \;
// find ~/.m2/repository/com/google/ortools -type f -name '*jniortools*' -exec dirname {} \; | cut -c $((${#HOME}+2))- | sed 's|^|${system_property:user.home}/|' | paste -s -d ":" -

/**
 * OR-Tools essay 
 */
@SuppressWarnings("nls")
public class ORToolsEssay {

	/** Java command */
	public static final String JAVA_COMMAND = """
java -cp "$(ls -1 target/**/*.jar | paste -s -d ':' -)" \
  -Djava.util.logging.SimpleFormatter.format='%5$s%6$s%n' \
  eu.fays.rockbox.ortools.ORToolsEssay
""";
	
	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(ORToolsEssay.class.getName());

	/** java.library.path */
	private static final String JAVA_LIBRARY_PATH = "java.library.path";

	/** jniortools.dll */
	private static final String JNIORTOOLS_DLL = "jniortools.dll";

	/** rabbits */
	private static final String RABBITS = "rabbits";

	/** pheasants */
	private static final String PHEASANTS = "pheasants";

	/**
	 * Main
	 * @param args unused
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final String javaLibraryPath = System.getProperty(JAVA_LIBRARY_PATH);
		LOGGER.info(JAVA_LIBRARY_PATH + "=" + javaLibraryPath);
		if (javaLibraryPath != null && !javaLibraryPath.isEmpty()) {
			final boolean isWindowsOperatingSystem = System.getProperty("os.name").indexOf("Windows") != -1;
			final String[] javaLibraryPathElements = javaLibraryPath.split(pathSeparator);
			Path orToolsLibraryPath = null;
			if(isWindowsOperatingSystem) {
				for (final String javaLibraryPathElement : javaLibraryPathElements) {
					final Path path = Path.of(javaLibraryPathElement);
					if(exists(path) && isDirectory(path)) {
						try {
							orToolsLibraryPath = walk(path, FOLLOW_LINKS).filter(p -> JNIORTOOLS_DLL.equals(p.getFileName().toString())).findFirst().orElse(null);
							if(orToolsLibraryPath != null) {
								break;
							}
						} catch(final IOException e) {
							LOGGER.log(SEVERE, e.getMessage(), e);
						}
					}
				}
			}

			if (isWindowsOperatingSystem && orToolsLibraryPath != null) {
				final Path orToolsLibraryFolder = orToolsLibraryPath.getParent();
				// libraries order does matter !
				// git clone --depth 1 --branch 'v9.14' https://github.com/google/or-tools.git
				// cf. https://github.com/google/or-tools/blob/v9.14/ortools/java/com/google/ortools/Loader.java#L147
				
				// On Windows, use Bash command from Cigwin that comes with the Git installation 
				//   cd  ~/workspace-mangogem-aps/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool/plugins/com.google.or-tools.win32.win32.x86_64_9.14.6206/ortools-win32-x86-64
				//   ldd *.dll
				
				// @formatter:off
				final String[] orToolsLibraryBasenames = {
					"zlib1",
					"bz2",
					"abseil_dll",
					"re2", // requires "abseil_dll.dll"
					"libutf8_validity",
					"libprotobuf", // requires "abseil_dll.dll", "utf8_validity.dll", "zlib1.dll"
					"highs",
					"libscip", // requires "zlib1"
					"ortools", // requires "abseil_dll.dll", "utf8_validity.dll", "zlib1.dll", "highs.dll"
					JNIORTOOLS_DLL // requires "abseil_dll.dll", "utf8_validity.dll", "zlib1.dll", "highs.dll", "ortools.dll"
				};
				// @formatter:on
				for(final String basename : orToolsLibraryBasenames) {
					final String filename = basename + ".dll";
					final Path file = orToolsLibraryFolder.resolve(filename);
					final String absolutePath = file.toAbsolutePath().toString();
					
					if(exists(file) && isRegularFile(file) && isReadable(file) && isExecutable(file)) {
	//					System.loadLibrary(basename);
						Runtime.getRuntime().load(absolutePath);
						LOGGER.info("Loaded: " + absolutePath);
					} else {
						LOGGER.warning(format("Library: {0}, exists: {1}, regularFile: {2}, readable: {3}, executable: {4}!", absolutePath, exists(file), isRegularFile(file), isReadable(file), isExecutable(file)));
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
			
			linearProgrammingExample();
			PremainAgent.logLoadedClasses(ORToolsEssay.class.getClassLoader());
//			System.out.println("Press any key to continue");
//			System.in.read();
		}
	}
	
	 public static void linearProgrammingExample() {
	    // [START solver]
	    MPSolver solver = MPSolver.createSolver("GLOP");
	    // [END solver]

	    // [START variables]
	    double infinity = java.lang.Double.POSITIVE_INFINITY;
	    // x and y are continuous non-negative variables.
	    MPVariable x = solver.makeNumVar(0.0, infinity, "x");
	    MPVariable y = solver.makeNumVar(0.0, infinity, "y");
	    LOGGER.info("Number of variables = " + solver.numVariables());
	    // [END variables]

	    // [START constraints]
	    // x + 2*y <= 14.
	    MPConstraint c0 = solver.makeConstraint(-infinity, 14.0, "c0");
	    c0.setCoefficient(x, 1);
	    c0.setCoefficient(y, 2);

	    // 3*x - y >= 0.
	    MPConstraint c1 = solver.makeConstraint(0.0, infinity, "c1");
	    c1.setCoefficient(x, 3);
	    c1.setCoefficient(y, -1);

	    // x - y <= 2.
	    MPConstraint c2 = solver.makeConstraint(-infinity, 2.0, "c2");
	    c2.setCoefficient(x, 1);
	    c2.setCoefficient(y, -1);
	    LOGGER.info("Number of constraints = " + solver.numConstraints());
	    // [END constraints]

	    // [START objective]
	    // Maximize 3 * x + 4 * y.
	    MPObjective objective = solver.objective();
	    objective.setCoefficient(x, 3);
	    objective.setCoefficient(y, 4);
	    objective.setMaximization();
	    // [END objective]

	    // [START solve]
	    final MPSolver.ResultStatus resultStatus = solver.solve();
	    // [END solve]

	    // [START print_solution]
	    if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
	      LOGGER.info("Solution:");
	      LOGGER.info("Objective value = " + objective.value());
	      LOGGER.info("x = " + x.solutionValue());
	      LOGGER.info("y = " + y.solutionValue());
	    } else {
	      System.err.println("The problem does not have an optimal solution!");
	    }
	    // [END print_solution]

	    // [START advanced]
	    LOGGER.info("Advanced usage:");
	    LOGGER.info("Problem solved in " + solver.wallTime() + " milliseconds");
	    LOGGER.info("Problem solved in " + solver.iterations() + " iterations");
	    // [END advanced]

	 }

}
