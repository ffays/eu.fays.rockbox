package eu.fays.rockbox.jpa;

import static java.lang.System.getProperty;
import static java.sql.DriverManager.getConnection;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
 * Rowset listener essay<br>
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
	private final String sql;

	/**
	 * VM Args
	 * 
	 * <pre>
	 * -ea -Djava.util.logging.SimpleFormatter.format="%5$s%6$s%n"
	 * </pre>
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) throws Exception {
		// https://dba.stackexchange.com/questions/224338/keep-h2-in-memory-database-between-connections
		final String defaultJdbcUrl = "jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1";
		final String jdbcUrl = getProperty("jdbcUrl", defaultJdbcUrl);
		final String user = getProperty("user", "sa");
		final String password = getProperty("password", "sa");
		final String sql = System.getProperty("sql", "SELECT * FROM locks");

		final UUID uuid = UUID.randomUUID();

		if (defaultJdbcUrl.equals(jdbcUrl)) {
			try (final Connection connection = getConnection(jdbcUrl, user, password)) {
					try (final Statement statement = connection.createStatement()) {
						statement.execute("CREATE TABLE locks (uuid UUID NOT NULL, lock_timestamp TIMESTAMP, lock_owner VARCHAR(64))");
					}
					
					try (final PreparedStatement prepareStatement = connection.prepareStatement("INSERT INTO locks (uuid) VALUES (?)")) {
						prepareStatement.setObject(1, uuid);
						prepareStatement.execute();
					}
					connection.commit();
			}
		}

		final RowSetListenerEssay rowSetListener = new RowSetListenerEssay(jdbcUrl, user, password, sql);
		rowSetListener.start();

		Thread.sleep(1000L);
		if (defaultJdbcUrl.equals(jdbcUrl)) {
			try (final Connection connection = getConnection(jdbcUrl, user, password); final PreparedStatement prepareStatement = connection.prepareStatement("UPDATE locks SET lock_timestamp = CURRENT_TIMESTAMP, lock_owner = ? WHERE uuid = ?")) {
				prepareStatement.setString(1, System.getProperty("user.name"));
				prepareStatement.setObject(2, uuid);
				prepareStatement.execute();
				connection.commit();
			}
		}
		Thread.sleep(1000L);
		rowSetListener.interrupt();
	}

	public RowSetListenerEssay(final String jdbcUrl, final String user, final String password, final String sql) {
		setName(getClass().getSimpleName());
		setUncaughtExceptionHandler(this);
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.sql = sql;
	}

	public void run() {
		try {
			final RowSetFactory rowSetFactory = RowSetProvider.newFactory();
			try (final JdbcRowSet jdbcRowSet = rowSetFactory.createJdbcRowSet()) {
				jdbcRowSet.setUrl(jdbcUrl);
				jdbcRowSet.setUsername(user);
				jdbcRowSet.setPassword(password);
				jdbcRowSet.setCommand(sql);
				jdbcRowSet.addRowSetListener(this);
				do {
					jdbcRowSet.execute();
					sleep(100L);
				} while (!isInterrupted());
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
