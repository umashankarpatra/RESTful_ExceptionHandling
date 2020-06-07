package com.xebia.postit.common.exception;

import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

/**
 * Custom, parameterized exception, which can be translated on the client side. For example:
 *
 * <pre>
 * throw new ParameterizedException(&quot;myCustomError&quot;, &quot;hello&quot;, &quot;world&quot;);
 * </pre>
 *
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{param0}} to {{param1}}"
 * </pre>
 */
public abstract class GenericException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;


    private Object[] parameters;

    public GenericException(String title, String messageKey) {
        super(ErrorConstants.PARAMETERIZED_TYPE, title, INTERNAL_SERVER_ERROR, messageKey, null, null, null);
    }

    public GenericException(String title, String messageKey, final Exception cause) {
        super(ErrorConstants.PARAMETERIZED_TYPE, title, INTERNAL_SERVER_ERROR, messageKey, null,
                (ThrowableProblem) cause, null);
    }

    public GenericException(String title, String messageKey, Object... parameters) {
        this(title, messageKey, toParamMap(parameters));
        setParameters(parameters);
    }

    public GenericException(String title, String messageKey, final Exception cause, Object... parameters) {
        this(title, messageKey, cause, toParamMap(parameters));
        setParameters(parameters);
    }

    public GenericException(String title, String messageKey, Status status, Object... parameters) {
        this(title, messageKey, toParamMap(parameters), status);
        setParameters(parameters);
    }

    public GenericException(String title, String messageKey, Status status, final Exception cause,
            Object... parameters) {
        this(title, messageKey, cause, toParamMap(parameters), status);
        setParameters(parameters);
    }

    private GenericException(String title, String messageKey, Map<String, Object> paramMap) {
        super(ErrorConstants.PARAMETERIZED_TYPE, title, INTERNAL_SERVER_ERROR, messageKey, null, null,
                toProblemParameters(messageKey, paramMap));
    }

    private GenericException(String title, String messageKey, final Exception cause,
            Map<String, Object> paramMap) {
        super(ErrorConstants.PARAMETERIZED_TYPE, title, INTERNAL_SERVER_ERROR, messageKey, null,
                (ThrowableProblem) cause, toProblemParameters(messageKey, paramMap));
    }

    private GenericException(String title, String messageKey, Map<String, Object> paramMap, Status status) {
        super(ErrorConstants.PARAMETERIZED_TYPE, title, status, messageKey, null, null,
                toProblemParameters(messageKey, paramMap));
    }

    private GenericException(String title, String messageKey, final Exception cause, Map<String, Object> paramMap,
            Status status) {
        super(ErrorConstants.PARAMETERIZED_TYPE, title, status, messageKey, null, (ThrowableProblem) cause,
                toProblemParameters(messageKey, paramMap));
    }

    private void setParameters(Object... parameters) {
        this.parameters = parameters;
    }

    public static Map<String, Object> toParamMap(Object... params) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                paramMap.put("" + i, params[i]);
            }
        }
        return paramMap;
    }

    public static Map<String, Object> toProblemParameters(String messageKey, Map<String, Object> paramMap) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("message", messageKey);
        parameters.put("params", paramMap);
        return parameters;
    }

    public abstract ErrorCodeType exceptionType();

    public Object[] getMessageParameters() {
        return this.parameters;
    }
}
