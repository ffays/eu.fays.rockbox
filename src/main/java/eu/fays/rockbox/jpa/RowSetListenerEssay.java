package eu.fays.rockbox.jpa;

import static java.lang.System.getProperty;
import static java.sql.DriverManager.getConnection;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;
import static java.text.MessageFormat.format;
import static java.time.LocalDateTime.now;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

/**
 * Rowset listener essay (not working)<br>
 * Cf.
 * <ul>
 * <li><a href="https://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html">Using JdbcRowSet Objects</a>
 * <li><a href="https://www.demo2s.com/java/java-rowsetlistener-tutorial-with-examples.html">Java RowSetListener tutorial with examples</a>
 * </ul>
 */
public class RowSetListenerEssay extends Thread implements UncaughtExceptionHandler, RowSetListener {

	/** Standard logger */
	private static final Logger LOGGER = Logger.getLogger(RowSetListenerEssay.class.getName());

	private final String jdbcUrl;
	private final String user;
	private final String password;
	private final String mode;
	private final UUID uuid;

	private static final String LISTEN = "listen";
	private static final String PULL = "pull";
	private static final String SELECT_FROM_LOCKS_SQL = "SELECT lock_timestamp, lock_owner FROM locks WHERE uuid = ?";
	private static final String INSERT_INTO_LOCKS_SQL = "INSERT INTO locks (uuid) VALUES (?)";
	private static final String UPDATE_LOCKS_SQL = "UPDATE locks SET lock_timestamp = ?, lock_owner = ? WHERE uuid = ?";
	/**
	 * VM Args
	 * 
	 * Listen:
	 * <pre>
	 * -Dmode="listen" -Duuid="3286502f-67b7-4a81-98cc-433fedbc61db" -ea -Djava.util.logging.SimpleFormatter.format="%1$tF %1$tT	%4$s	%3$s	%5$s%6$s%n"
	 * </pre>
	 * 
	 * Pull
	 * <pre>
	 * -Dmode="pull" -Duuid="3286502f-67b7-4a81-98cc-433fedbc61db" -ea -Djava.util.logging.SimpleFormatter.format="%1$tF %1$tT	%4$s	%3$s	%5$s%6$s%n"
	 * </pre>
	 * 
	 * Emit:
	 * <pre>
	 * -Dmode="emit" -Duuid="3286502f-67b7-4a81-98cc-433fedbc61db" -ea -Djava.util.logging.SimpleFormatter.format="%1$tF %1$tT	%4$s	%3$s	%5$s%6$s%n"
	 * </pre>
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) throws Exception {
		// https://dba.stackexchange.com/questions/224338/keep-h2-in-memory-database-between-connections

		// RowSetListener are not working with named in-memory database !?
		// final String defaultJdbcUrl = "jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1";
		
		// Create DB: java -cp h2-1.4.200.jar org.h2.tools.Shell
		// Start  H2: java -jar h2-1.4.200.jar

		final String defaultJdbcUrl = "jdbc:h2:tcp://localhost/~/sandbox";
		final String jdbcUrl = getProperty("jdbcUrl", defaultJdbcUrl);
		final String user = getProperty("user", "sa");
		final String password = getProperty("password", "sa");
		// "locks" table: CREATE TABLE locks (uuid UUID NOT NULL, lock_timestamp TIMESTAMP, lock_owner VARCHAR(64))
		final String mode = System.getProperty("mode", LISTEN);
		final UUID uuid = System.getProperty("uuid") != null ? UUID.fromString(System.getProperty("uuid")) : UUID.randomUUID();

		try (final Connection connection = getConnection(jdbcUrl, user, password)) {
			try (final Statement statement = connection.createStatement()) {
				statement.execute("CREATE TABLE IF NOT EXISTS locks (uuid UUID NOT NULL, lock_timestamp TIMESTAMP, lock_owner VARCHAR(64))");
			}
			
			try (final PreparedStatement selectPrepareStatement = connection.prepareStatement(SELECT_FROM_LOCKS_SQL)) {
				selectPrepareStatement.setObject(1, uuid);
				boolean found = false;
				try(final ResultSet resultSet = selectPrepareStatement.executeQuery()) {
					if(resultSet.next()) {
						found = true;
					}
				}
				if(found) {
					// unlock
					try (final PreparedStatement updatePrepareStatement = connection.prepareStatement(UPDATE_LOCKS_SQL)) {
						updatePrepareStatement.setNull(1, TIMESTAMP);
						updatePrepareStatement.setNull(2, VARCHAR);
						updatePrepareStatement.setObject(3, uuid);
						updatePrepareStatement.execute();
					}
				} else {
					try (final PreparedStatement insertPrepareStatement = connection.prepareStatement(INSERT_INTO_LOCKS_SQL)) {
						insertPrepareStatement.setObject(1, uuid);
						insertPrepareStatement.execute();
					}
				}
				connection.commit();

			}
		}

		LOGGER.info(format("{0}\t{1}", mode, uuid));
		if(LISTEN.equals(mode) || PULL.equals(mode)) {
			final RowSetListenerEssay rowSetListener = new RowSetListenerEssay(jdbcUrl, user, password, mode, uuid);
			rowSetListener.start();
		} else {
			try (final Connection connection = getConnection(jdbcUrl, user, password)) {
				try (final PreparedStatement updatePrepareStatement = connection.prepareStatement(UPDATE_LOCKS_SQL)) {
					updatePrepareStatement.setTimestamp(1, Timestamp.valueOf(now()));
					updatePrepareStatement.setString(2, System.getProperty("user.name"));
					updatePrepareStatement.setObject(3, uuid);
					updatePrepareStatement.execute();
					connection.commit();
				}
			}
		}
	}

	public RowSetListenerEssay(final String jdbcUrl, final String user, final String password, final String mode, final UUID uuid) {
		setName(getClass().getSimpleName());
		setUncaughtExceptionHandler(this);
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.mode = mode;
		this.uuid = uuid;
	}

	public void run() {
		try {
			if(LISTEN.equals(mode)) {
				final RowSetFactory rowSetFactory = RowSetProvider.newFactory();
				try (final JdbcRowSet jdbcRowSet = rowSetFactory.createJdbcRowSet()) {
					jdbcRowSet.setUrl(jdbcUrl);
					jdbcRowSet.setUsername(user);
					jdbcRowSet.setPassword(password);
					jdbcRowSet.setCommand(SELECT_FROM_LOCKS_SQL.replace("?", "'" + uuid.toString() + "'"));
					jdbcRowSet.addRowSetListener(this);
					do {
						jdbcRowSet.execute();
						sleep(100L);
					} while (!isInterrupted());
				}
			} else {
				try (final Connection connection = getConnection(jdbcUrl, user, password)) {
					try (final PreparedStatement selectPrepareStatement = connection.prepareStatement(SELECT_FROM_LOCKS_SQL)) {
						selectPrepareStatement.setObject(1, uuid);
						boolean locked = false;
						do {
							try(final ResultSet resultSet = selectPrepareStatement.executeQuery()) {
								if(resultSet.next()) {
									final Timestamp lockTimestamp = resultSet.getTimestamp(1);
									final String lockOwner = resultSet.getString(2);
									if(lockTimestamp != null) {
										LOGGER.info(format("LOCKED {0}\t{1,date,yyyy-mm-dd HH:mm:ss}\t{2}", uuid, lockTimestamp, lockOwner));
										locked = true;
									}
								}
							}
							sleep(100L);
						} while (!isInterrupted() && !locked);
					}
				}
			}
		} catch (final InterruptedException e) {
			// Do nothing
		} catch (final SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		LOGGER.log(Level.SEVERE, e.getMessage(), e);
	}

	@Override
	public void rowSetChanged(RowSetEvent event) {
//		final RowSet rowSet = (RowSet) event.getSource();
//		LOGGER.info("rowSetChanged: " + rowSet.toString());

	}

	@Override
	public void rowChanged(RowSetEvent event) {
		final RowSet rowSet = (RowSet) event.getSource();
		LOGGER.info("rowChanged: " + rowSet.toString());
	}

	@Override
	public void cursorMoved(RowSetEvent event) {
		final RowSet rowSet = (RowSet) event.getSource();
		LOGGER.info("cursorMoved: " + rowSet.toString());
	}
}
