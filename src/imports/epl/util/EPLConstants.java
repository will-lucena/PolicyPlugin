package epl.util;

public class EPLConstants
{
	public final static String APP_ARGS_KEY = "application.args";

	public final static String METHOD_NAME_WITH_PARAMETER_FORMAT = "%s(%s)";

	public final static String METHOD_FULLY_QUALIFIED_NAME_FORMAT = "%s.%s.%s";

	public final static String VARIABLE_DECLARATION_TOKEN = "Var";

	public final static String INVOCATION_TOKEN = "Inv";

	public final static String SINGLE_VARIABLE_TOKEN = "SVr";

	public final static String SIMPLE_TYPE_TOKEN = "Typ";

	public final static String LOOP_TOKEN = "Lop";

	public final static String DECISION_TOKEN = "Dec";

	public final static String TRY_TOKEN = "Try";

	public final static String CATCH_TOKEN = "Cat";

	public final static String THROW_TOKEN = "Thr";

	public final static String FINALLY_TOKEN = "Fin";

	public final static String CONTINUE_TOKEN = "Con";

	public final static String BREAK_TOKEN = "Bre";

	public final static String COMPARTMENT_DEF_ID = "COMPARTMENT_DEF_ID";

	public final static String RULE_DEF_ID = "RULE_DEF_ID";

	/**
	 * Covers the definition of compartments
	 */
	public final static String COMPARTMENT_DEFINITION_REGEX = "\\s*define\\s+([a-zA-Z0-9_,\\.\\*\\s+]+)\\s*as\\s+compartment\\s+([a-zA-Z0-9_\\-]+)\\s*;\\s*";

	/**
	 * Covers the combination of rules: (cannot|must|may-only) x (handle|raise|propagate|rethrow)
	 */
	public final static String REGULAR_RULE_REGEX = "([a-zA-Z0-9_\\-]+)\\s+(cannot|must|may\\s+only)\\s+(handle|raise|propagate|rethrow)\\s+([a-zA-Z0-9_\\.\\*,\\s+]+)\\s*;\\s*";

	/**
	 * Covers the combination of rules: (only-may) x (handle|raise|propagate|rethrow)
	 */
	public final static String ONLY_MAY_REGULAR_RULE_REGEX = "only\\s+([a-zA-Z0-9_\\-]+)\\s+may\\s+(handle|raise|propagate|rethrow)\\s+([a-zA-Z0-9_\\.\\*,\\s+]+)\\s*;\\s*";

	/**
	 * Covers the combination of rules: (cannot|must|may-only) x (remap)
	 */
	public final static String REGULAR_REMAP_RULE_REGEX = "([a-zA-Z0-9_\\-]+)\\s+(cannot|must|may\\s+only)\\s+remap\\s+(from\\s+([a-zA-Z0-9_\\-\\.\\*\\s+,]+)\\s+to\\s+([a-zA-Z0-9_\\-\\.\\*\\s+,]+))\\s*;\\s*";

	/**
	 * Covers the combination of rules: (cannot|must|may-only) x (remap)
	 */
	public final static String ONLY_MAY_REMAP_RULE_REGEX = "only\\s+([a-zA-Z0-9_\\-]+)\\s+may\\s+remap\\s+(from\\s+([a-zA-Z0-9_\\-\\.\\*\\s+,]+)\\s+to\\s+([a-zA-Z0-9_\\-\\.\\*\\s+,]+))\\s*;\\s*";

	/**
	 * Regex to replace '*' in input strings
	 */
	public final static String WILDCARD_REPLACEMENT_REGEX = "([a-zA-Z0-9_\\-\\.<>]*)";

	public final static String REMAP_EXCEPTION_PAIR_REGEX = "from\\s+([a-zA-Z0-9_\\-\\.\\*\\s+,\\\\]+)\\s+to\\s+([a-zA-Z0-9_\\-\\.\\*\\s+,\\\\]+)";
}
