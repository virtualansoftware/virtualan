/*
 * Copyright 2020 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.virtualan.core.util.rule;


import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

/**
 * This is Rule Evaluator Service .
 *
 * @author  Elan Thangamani
 *
 **/
@Service("ruleEvaluator")
public class RuleEvaluator {
	
	
	public boolean expressionEvaluator(Object object, String rule)  {
			ExpressionParser parser = new SpelExpressionParser();
			EvaluationContext itemContext = new StandardEvaluationContext(object);
			Expression exp4 = parser.parseExpression(rule);
			return exp4.getValue(itemContext, Boolean.class).booleanValue();
	}
	
	public  void expressionEvaluatorForMockCreation(Object object, String rule){
			ExpressionParser parser = new SpelExpressionParser();
			EvaluationContext itemContext = new StandardEvaluationContext(object);
			Expression exp4 = parser.parseExpression(rule);
			exp4.getValue(itemContext);
	}

	
	public String expressionEvaluatorForString(Object object, String rule){
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext itemContext = new StandardEvaluationContext(object);
		Expression exp4 = parser.parseExpression(rule);
		return exp4.getValue(itemContext, String.class);
	}

	
}
