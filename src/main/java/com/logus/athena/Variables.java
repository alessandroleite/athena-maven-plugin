package com.logus.athena;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class Variables implements Iterable<Variable> {

	private final Map<String, Variable> values = new ConcurrentHashMap<String, Variable>();

	@Override
	public Iterator<Variable> iterator() {
		return values().iterator();
	}

	public Variable add(Variable variable) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(Preconditions
				.checkNotNull(variable).getName()));
		return this.values.put(variable.getName(), variable);
	}

	public void copy(Variables variables) {
		for (Variable variable : Preconditions.checkNotNull(variables)) {
			this.values.put(variable.getName(), variable);
		}
	}

	public int size() {
		return this.values.size();
	}

	public Collection<Variable> values() {
		return Collections.unmodifiableCollection(this.values.values());
	}
}