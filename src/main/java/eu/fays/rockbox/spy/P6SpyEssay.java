package eu.fays.rockbox.spy;

import static java.lang.System.out;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class P6SpyEssay {

	/**
	 * -Durl="jdbc:p6spy:h2:tcp://localhost/~/test_database" -Duser=sa -Dpassword=sa "SELECT * FROM INFORMATION_SCHEMA.COLUMNS"
	 * 
	 * -Durl="jdbc:h2:tcp://localhost/~/test_database"
	 * @param args sql queries
	 * @throws SQLException
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) throws SQLException {
		final String url = System.getProperty("url");
		final String user = System.getProperty("user");
		final String password = System.getProperty("password");
		try (final Connection connection = DriverManager.getConnection(url, user, password)) {
			for (final String sql : args) {
				if (sql.startsWith("SELECT")) {
					try (final Statement statement = connection.createStatement(); final ResultSet rs = statement.executeQuery(sql)) {
						final ResultSetMetaData metaData = rs.getMetaData();
						final int n = metaData.getColumnCount();
						while (rs.next()) {
							for (int c = 1; c <= n; c++) {
								if (c > 1) {
									out.print('\t');
								}
								final Object value = rs.getObject(c);
								if (value != null) {
									out.print(value.toString());
								}
							}
							out.println();
						}
					}
				} else {
					out.print(sql);
					out.print('\u21D2');
					try (final Statement statement = connection.createStatement()) {
						if (sql.startsWith("UPDATE") || sql.startsWith("INSERT") || sql.startsWith("DELETE")) {
							final int updateCount = statement.executeUpdate(sql);
							out.println(updateCount);
						} else {
							boolean rc = statement.execute(sql);
							out.println(rc);
						}
					}
				}
			}
		}
	}
}
