package io.virtualan.core.util.rule;


import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

@Service("ruleEvaluator")
public class RuleEvaluator {
	
	
	public boolean expressionEvaluator(Object object, String rule)  {
			ExpressionParser parser = new SpelExpressionParser();
			EvaluationContext itemContext = new StandardEvaluationContext(object);
			Expression exp4 = parser.parseExpression(rule);
			boolean obj = exp4.getValue(itemContext, Boolean.class);
			return obj;
	}
	
	public  void expressionEvaluatorForMockCreation(Object object, String rule) throws SpelParseException{
		try {
			ExpressionParser parser = new SpelExpressionParser();
			EvaluationContext itemContext = new StandardEvaluationContext(object);
			Expression exp4 = parser.parseExpression(rule);
			Object obj = exp4.getValue(itemContext);
		} catch (SpelParseException e){
			throw e;
		} catch (Exception e){
		}
	}

	/*public long expressionEvaluatorForNumber(Object object, String rule){
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext itemContext = new StandardEvaluationContext(object);
		Expression exp4 = parser.parseExpression(rule);
		long number = exp4.getValue(itemContext, Long.class);
		return number;
	}
	
	public String expressionEvaluatorForString(Object object, String rule){
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext itemContext = new StandardEvaluationContext(object);
		Expression exp4 = parser.parseExpression(rule);
		String str = exp4.getValue(itemContext, String.class);
		return str;
	}*/
	
	
	
	
	
}
