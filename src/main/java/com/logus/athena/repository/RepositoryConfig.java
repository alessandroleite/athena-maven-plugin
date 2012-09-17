package com.logus.athena.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.common.base.Preconditions;

public final class RepositoryConfig {

	private final Class<?> driverClass;

	private final String username;

	private final String password;

	private final String url;

	/**
	 * @param driverClass
	 * @param username
	 * @param password
	 * @param url
	 */
	public RepositoryConfig(Class<?> driverClass, String username,
			String password, String url) {
		this.driverClass = Preconditions.checkNotNull(driverClass);
		this.username = username;
		this.password = password;
		this.url = url;

		try {
			this.driverClass.newInstance();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.url, this.username,
				this.password);
	}
}