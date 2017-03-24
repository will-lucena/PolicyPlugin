package epl.model;

import java.util.List;

public class Rule {

	public enum DependencyType {
		Handle, Propagate, Raise, Remap, Rethrow
	}

	public enum RuleType {
		Cannot, MayOnly, Must, OnlyMay
	}

	private final RuleType ruleType;

	private final String compartmentId;

	private final DependencyType dependencyType;

	private final List<String> exceptionExpressions;

	private Compartment compartment;

	public Rule(final RuleType ruleType, final String compartmentId, final List<String> exceptionExpressions,
			final DependencyType dependencyType) {
		this.ruleType = ruleType;
		this.compartmentId = compartmentId;
		this.exceptionExpressions = exceptionExpressions;
		this.dependencyType = dependencyType;
	}

	public Compartment getCompartment() {
		return this.compartment;
	}

	public String getCompartmentId() {
		return compartmentId;
	}

	public DependencyType getDependencyType() {
		return dependencyType;
	}

	public List<String> getExceptionExpressions() {
		return exceptionExpressions;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public void setCompartment(final Compartment compartment) {
		this.compartment = compartment;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s %s", this.getCompartmentId(), this.getRuleType(), this.getDependencyType(),
				this.getExceptionExpressions());
	}
}
