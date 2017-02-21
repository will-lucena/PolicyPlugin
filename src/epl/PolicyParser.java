package epl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.ws.util.StringUtils;

import epl.model.Compartment;
import epl.model.Policy;
import epl.model.Rule;
import epl.model.Rule.DependencyType;
import epl.model.Rule.RuleType;
import epl.util.EPLConstants;

public class PolicyParser
{
	private static Compartment buildCompartment(final String line)
	{
		final Matcher matcher = COMPARTMENT_PATTERN.matcher(line);
		if (matcher.find())
		{
			final String expression = matcher.group(1);
			final List<String> expressions = new ArrayList<String>();
			for (final String exp : expression.split(",\\s+"))
			{
				expressions.add(convertToRegex(exp));
			}
			final String id = matcher.group(2);
			final Compartment compartment = new Compartment(id, expressions);
			return compartment;
		}
		else
		{
			throw new IllegalStateException(String.format("Could not parse Compartment Definition from the line '%s'.", line));
		}
	}

	private static Policy buildPolicy(final File policyFile) throws IOException
	{
		final Map<String, List<String>> specificationMap = processSpecificationAsMap(policyFile);

		final Policy policy = new Policy();

		final List<String> comparmentsDefs = specificationMap.get(EPLConstants.COMPARTMENT_DEF_ID);
		for (final String compartmentDef : comparmentsDefs)
		{
			final Compartment compartment = buildCompartment(compartmentDef);
			policy.addCompartment(compartment);
			map.put(compartment.getId(), compartment);
		}

		final List<String> rulesDefs = specificationMap.get(EPLConstants.RULE_DEF_ID);
		for (final String ruleDef : rulesDefs)
		{
			final Rule rule = buildRule(ruleDef);
			policy.addRule(rule);
		}

		for (final Rule rule : policy.getRules())
		{
			final String compartmentId = rule.getCompartmentId();
			final Compartment compartment = map.get(compartmentId);

			if (compartment == null)
			{
				throw new IllegalStateException(String.format("Rule '%s' refers to the unknown compartment named '%s'.", rule, compartmentId));
			}

			rule.setCompartment(compartment);
			compartment.addRule(rule);
		}

		return policy;
	}

	private static Rule buildRule(final String line)
	{
		RuleType ruleType = null;
		String compartmentId = null;
		List<String> exceptionExpressions = null;
		DependencyType dependencyType = null;

		if (line.matches(EPLConstants.REGULAR_RULE_REGEX))
		{
			final Matcher matcher = REGULAR_RULE_PATTERN.matcher(line);
			if (matcher.find())
			{
				compartmentId = matcher.group(1);
				ruleType = getRuleType(matcher.group(2));
				dependencyType = getDependencyType(matcher.group(3));
				exceptionExpressions = Arrays.asList(matcher.group(4).split(",\\s+"));

			}
		}
		else if (line.matches(EPLConstants.ONLY_MAY_REGULAR_RULE_REGEX))
		{
			final Matcher matcher = ONLY_MAY_REGULAR_RULE_PATTERN.matcher(line);
			if (matcher.find())
			{
				compartmentId = matcher.group(1);
				ruleType = RuleType.OnlyMay;
				dependencyType = getDependencyType(matcher.group(2));
				exceptionExpressions = Arrays.asList(matcher.group(3).split(",\\s+"));
			}
		}
		else if (line.matches(EPLConstants.REGULAR_REMAP_RULE_REGEX))
		{
			final Matcher matcher = REGULAR_REMAP_RULE_PATTERN.matcher(line);
			if (matcher.find())
			{
				compartmentId = matcher.group(1);
				ruleType = getRuleType(matcher.group(2));
				dependencyType = DependencyType.Remap;
				exceptionExpressions = Arrays.asList(matcher.group(3).split(",\\s+"));
			}
		}
		else if (line.matches(EPLConstants.ONLY_MAY_REMAP_RULE_REGEX))
		{
			final Matcher matcher = ONLY_MAY_REMAP_RULE_PATTERN.matcher(line);
			if (matcher.find())
			{
				compartmentId = matcher.group(1);
				ruleType = RuleType.OnlyMay;
				dependencyType = DependencyType.Remap;
				exceptionExpressions = Arrays.asList(matcher.group(2).split(",\\s+"));
			}
		}
		else
		{
			throw new IllegalStateException(String.format("Could not parse Rule Definition from the line '%s'.", line));
		}

		final List<String> expressions = new ArrayList<String>();
		for (final String exp : exceptionExpressions)
		{
			expressions.add(convertToRegex(exp));
		}

		return new Rule(ruleType, compartmentId, expressions, dependencyType);
	}

	private static String convertToRegex(final String exp)
	{
		return exp.trim().replace(".", "\\.").replace("*", EPLConstants.WILDCARD_REPLACEMENT_REGEX);
	}

	private static DependencyType getDependencyType(final String group)
	{
		final String capitalize = StringUtils.capitalize(group);
		return DependencyType.valueOf(capitalize);
	}

	private static RuleType getRuleType(final String group)
	{
		if ("must".equals(group))
		{
			return RuleType.Must;
		}
		else if ("cannot".equals(group))
		{
			return RuleType.Cannot;
		}
		else if (group.matches("may\\s+only"))
		{
			return RuleType.MayOnly;
		}

		throw new IllegalStateException(String.format("Unknown %s rule type.", group));
	}

	public static Policy parseSpecification(final File policyFile)
	{
		try
		{
			final Policy policy = buildPolicy(policyFile);

			assert policy.check();

			return policy;
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public static Map<String, List<String>> processSpecificationAsMap(final File policyFile) throws IOException
	{
		final Map<String, List<String>> result = new HashMap<>();
		result.put(EPLConstants.COMPARTMENT_DEF_ID, new ArrayList<String>());
		result.put(EPLConstants.RULE_DEF_ID, new ArrayList<String>());

		try (final BufferedReader br = new BufferedReader(new FileReader(policyFile)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.isEmpty())
				{
					continue;
				}

				List<String> list;
				if (line.matches(EPLConstants.COMPARTMENT_DEFINITION_REGEX))
				{
					list = result.get(EPLConstants.COMPARTMENT_DEF_ID);
					assert list != null : "Error while processing specification: list for compartments is null.";
				}
				else
				{
					list = result.get(EPLConstants.RULE_DEF_ID);
					assert list != null : "Error while processing specification: list for rules is null.";
				}
				list.add(line);
			}
		}

		return result;
	}

	private static final Map<String, Compartment> map = new TreeMap<String, Compartment>();

	private static final Pattern COMPARTMENT_PATTERN = Pattern.compile(EPLConstants.COMPARTMENT_DEFINITION_REGEX);

	private static final Pattern ONLY_MAY_REMAP_RULE_PATTERN = Pattern.compile(EPLConstants.ONLY_MAY_REMAP_RULE_REGEX);

	private static final Pattern REGULAR_RULE_PATTERN = Pattern.compile(EPLConstants.REGULAR_RULE_REGEX);

	private static final Pattern ONLY_MAY_REGULAR_RULE_PATTERN = Pattern.compile(EPLConstants.ONLY_MAY_REGULAR_RULE_REGEX);

	private static final Pattern REGULAR_REMAP_RULE_PATTERN = Pattern.compile(EPLConstants.REGULAR_REMAP_RULE_REGEX);
}
