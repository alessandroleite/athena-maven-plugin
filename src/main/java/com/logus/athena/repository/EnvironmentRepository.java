package com.logus.athena.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.common.base.Preconditions;
import com.logus.athena.Environment;
import com.logus.athena.Variable;
import com.logus.athena.Variables;

public class EnvironmentRepository {

	private static final String SQL_ENVIRONMENT_BY_NAME = "select id, name, client from environment where lower(client) = lower (?) and lower(name) = lower(?)";
	private static final String SQL_VARIABLES_OF_ENVIRONMENT = "select name, value, id_environment from variable "
			+ " where (id_environment = ? or id_environment is null) order by name ";

	private static final Environment NULL_ENVIRONMENT = new Environment(-1L,
			"", "", new Variables());

	private final RepositoryConfig repositoryConfig;

	public EnvironmentRepository(RepositoryConfig repositoryConfig) {
		this.repositoryConfig = Preconditions.checkNotNull(repositoryConfig);
	}

	public Environment getByClientAndName(String client, String name) {

		Connection conn = null;
		PreparedStatement pstt = null;
		ResultSet res = null;

		try {
			conn = repositoryConfig.getConnection();
			pstt = conn.prepareStatement(SQL_ENVIRONMENT_BY_NAME);
			pstt.setString(1, client);
			pstt.setString(2, name);

			res = pstt.executeQuery();

			if (res.next()) {
				return new Environment(res.getLong(1), res.getString(2),
						res.getString(3), getVariablesOfEnvironment(
								res.getLong(1), conn));
			}

		} catch (SQLException exception) {
		} finally {
			closeResourceSilently(res, pstt, conn);
		}

		return NULL_ENVIRONMENT;
	}

	private Variables getVariablesOfEnvironment(Long environmentId,
			Connection conn) throws SQLException {

		PreparedStatement pstt = null;
		ResultSet res = null;

		final Variables variables = new Variables();

		try {
			pstt = conn.prepareStatement(SQL_VARIABLES_OF_ENVIRONMENT);
			pstt.setLong(1, environmentId);
			res = pstt.executeQuery();

			while (res.next()) {
				variables.add(new Variable(res.getString("name"), res
						.getString("value")));
			}

		} finally {
			closeResourceSilently(res, pstt, null);
		}

		return variables;
	}

	private void closeResourceSilently(ResultSet res, PreparedStatement pstt,
			Connection conn) {
		if (res != null)
			try {
				res.close();
			} catch (SQLException ignore) {
			}

		if (pstt != null)
			try {
				pstt.close();
			} catch (SQLException ignore) {
			}

		if (conn != null)
			try {
				conn.close();
			} catch (SQLException ignore) {
			}
	}
}