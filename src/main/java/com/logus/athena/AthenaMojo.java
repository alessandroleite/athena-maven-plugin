package com.logus.athena;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.maven.execution.RuntimeInformation;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Strings;
import com.logus.athena.repository.EnvironmentRepository;
import com.logus.athena.repository.RepositoryConfig;


/**
 * 
 * @goal environment
 * @execute phase=process-resources
 * @description 
 */
public class AthenaMojo extends AbstractMojo {

	// --------------------------------------------------------------------- //
	// -                      Mojo Parameters                              - //
	// --------------------------------------------------------------------- //

	/**
	 * Properties that must be used in the build. This properties can be used to
	 * specifies the database connection or to specifies some build variables.
	 * 
	 * @parameter expression="${build-properties-file}"
	 */
	private File propertiesFile;

	/**
	 * @parameter expression="${database-username}"
	 */
	private String databaseUsername;

	/**
	 * @parameter expression="${database-password}"
	 */
	private String databasePassword;

	/**
	 * @parameter expression="${database-url}"
	 */
	private String databaseUrl;

	/**
	 * @parameter expression="${database-class}"
	 */
	private String databaseDriverClass;

	/**
	 * @parameter expression="${client-name}"
	 * @required
	 */
	private String clientName;

	/**
	 * @parameter expression="${environment-name}"
	 * @required
	 */
	private String environmentName;
	
	// --------------------------------------------------------------------- //
	// -                   Mojo Runtime Information                        - //
	// --------------------------------------------------------------------- //

	/**
	 * Name of the Maven Project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	@SuppressWarnings("unused")
	private MavenProject project;

	/**
	 * The runtime information for Maven, used to retrieve Maven's version
	 * number.
	 * 
	 * @component
	 */
	@SuppressWarnings("unused")
	private RuntimeInformation runtimeInformation;

	/**
	 * Execute the Mojo.
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Properties properties = new Properties();
		this.loadConfigProperties(properties);
		
		Environment environment = new EnvironmentRepository(
				createRepositoryConfig(properties)).getByClientAndName(
				this.clientName, this.environmentName);
		
		for(Variable variable: environment.getVariables()){
			System.setProperty(variable.getName(), variable.getValue());
		}
	}

	private RepositoryConfig createRepositoryConfig(Properties properties) throws MojoExecutionException{
		
		String driverClass = this.databaseDriverClass;
		String username = this.databaseUsername;
		String password = this.databasePassword;
		String url = this.databaseUrl;
		
		if (Strings.isNullOrEmpty(driverClass) && Strings.isNullOrEmpty((driverClass = properties.getProperty("database.driver.class")))){
			throw new MojoExecutionException("Driver class must not be null");
		}
		
		if (Strings.isNullOrEmpty(username) && Strings.isNullOrEmpty((username = properties.getProperty("database.username")))){
			throw new MojoExecutionException("Database username must not be null");
		}
		
		if (Strings.isNullOrEmpty(password) && Strings.isNullOrEmpty((password = properties.getProperty("database.password")))){
			throw new MojoExecutionException("Database password must not be null");
		}
		
		if (Strings.isNullOrEmpty(url) && Strings.isNullOrEmpty((url = properties.getProperty("database.url")))){
			throw new MojoExecutionException("Database url must not be null");
		}
		
		
		try {
			return new RepositoryConfig(Class.forName(driverClass), username, password, url);
		} catch (ClassNotFoundException exception) {
			throw new MojoExecutionException(exception.getMessage(), exception);
		}
	}

	private void loadConfigProperties(Properties properties) throws MojoExecutionException {
		Reader reader = null;
		try {

			if (propertiesFile != null) {
				properties.load(reader = new FileReader(this.propertiesFile));
			}

		} catch (IOException exception) {
			throw new MojoExecutionException(exception.getMessage(), exception);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException ignore) {
				}
		}
	}
}