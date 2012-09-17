package com.logus.athena;


public final class Environment {
	
	/**
	 * Environment id. 
	 */
	private Long id;
	
	/**
	 * Environment name.
	 */
	private String name;
	
	/**
	 * Set of variables.
	 */
	private final Variables variables = new Variables();

	
	/**
	 * Client name 
	 */
	private String client;
	
	public Environment(Long id, String name, String client, Variables variables) {
		this.id = id;
		this.name = name;
		this.client = client;
		this.variables.copy(variables);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the client
	 */
	public String getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * @return the variables
	 */
	public Variables getVariables() {
		return variables;
	}
}