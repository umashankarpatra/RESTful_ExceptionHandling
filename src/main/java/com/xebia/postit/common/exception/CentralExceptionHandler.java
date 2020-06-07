package com.xebia.postit.common.exception;

import static com.xebia.postit.common.i18n.MessageProvider.getMessage;
import static com.xebia.postit.common.i18n.MessageProvider.getMessageForFieldError;

import java.text.StringCharacterIterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import com.xebia.postit.common.i18n.MessageKey;
import com.xebia.postit.infra.config.ApplicationProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller advice to translate the server side exceptions to client-friendly
 * json structures. The error response follows RFC7807 - Problem Details for
 * HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */
@Slf4j
@ControllerAdvice
public class CentralExceptionHandler implements ProblemHandling {

	private static final String FIELD_ERRORS_KEY = "fieldErrors";

	private static final String MESSAGE_KEY = "message";

	private static final String PATH_KEY = "path";

	private static final String VIOLATIONS_KEY = "violations";

	private static final String STACKTRACE_KEY = "stacktrace";

	private static final String REQUEST_VALIDATION_ERROR_TITLE = "error.request.validation.title";

	private static final String METHOD_ARGUMENT_VALIDATION_ERROR_TITLE = "error.method.argument.validation.title";

	private static final String INTERNAL_SERVER_ERROR_TITLE = "error.internal.server.title";

	private static final String INTERNAL_SERVER_ERROR_MESSAGE = "error.internal.server.message";

	private boolean problemStacktrace;

	public CentralExceptionHandler(final ApplicationProperties applicationProperties) {
		this.problemStacktrace = applicationProperties.isProblemStacktrace();
	}

	/**
	 * Post-process Problem payload to add the message key for front-end if needed
	 */
	@Override
	public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
		if (entity == null || entity.getBody() == null) {
			return entity;
		}
		Problem problem = entity.getBody();
		if (!(problem instanceof ConstraintViolationProblem)) {
			return entity;
		}
		ProblemBuilder problemBuilder = Problem.builder()
				.withType(Problem.DEFAULT_TYPE.equals(problem.getType()) ? ErrorConstants.DEFAULT_TYPE
						: problem.getType())
				.withStatus(problem.getStatus()).withTitle(problem.getTitle())
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());

		if (problem instanceof ConstraintViolationProblem) {
			problemBuilder.with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations()).with(
					MESSAGE_KEY,
					getMessage(MessageKey.of(ErrorConstants.ERR_VALIDATION, "Validation errors in request")));
			return new ResponseEntity<>(problemBuilder.build(), entity.getHeaders(), entity.getStatusCode());
		} else {
			problemBuilder.withCause(((DefaultProblem) problem).getCause()).withDetail(problem.getDetail())
					.withInstance(problem.getInstance());
			problem.getParameters().forEach(problemBuilder::with);
			if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
				problemBuilder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
			}
			if (this.problemStacktrace) {
				problemBuilder.with(STACKTRACE_KEY, getStacktrace((Throwable) problem));
			}
			return new ResponseEntity<>(problemBuilder.build(), entity.getHeaders(), entity.getStatusCode());
		}
	}

	@Override
	public ResponseEntity<Problem> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			@Nonnull NativeWebRequest request) {
		Problem problem = Problem.builder().withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
				.withTitle(getMessage(REQUEST_VALIDATION_ERROR_TITLE)).withStatus(defaultConstraintViolationStatus())
				.with(MESSAGE_KEY,
						getMessage(MessageKey.of("missing." + ex.getParameterName(), ex.getLocalizedMessage())))
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI()).build();
		return create(ex, problem, request);
	}

	@Override
	public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			@Nonnull NativeWebRequest request) {
		BindingResult result = ex.getBindingResult();
		List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
				.map(f -> new FieldErrorVM(f.getObjectName(), f.getField(), getMessageForFieldError(f)))
				.collect(Collectors.toList());

		Problem problem = Problem.builder().withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
				.withTitle(getMessage(METHOD_ARGUMENT_VALIDATION_ERROR_TITLE))
				.withStatus(defaultConstraintViolationStatus())
				.with(MESSAGE_KEY,
						getMessage(MessageKey.of(ErrorConstants.ERR_VALIDATION, "Validation errors in request")))
				.with(FIELD_ERRORS_KEY, fieldErrors).build();
		return create(ex, problem, request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Problem> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			NativeWebRequest request) {
		String exMessage = ex.getMostSpecificCause().getMessage().trim();
		String constraintName = exMessage.substring(exMessage.lastIndexOf(".") + 1, exMessage.lastIndexOf(")"));
		ProblemBuilder problemBuilder = Problem.builder().withTitle(getMessage(INTERNAL_SERVER_ERROR_TITLE))
				.withStatus(Status.INTERNAL_SERVER_ERROR)
				.with(MESSAGE_KEY, getMessage(MessageKey.of(constraintName, exMessage)))
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());
		if (this.problemStacktrace) {
			problemBuilder.with(STACKTRACE_KEY, getStacktrace(ex));
		}
		return create(ex, problemBuilder.build(), request);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Problem> handleNoSuchElementException(NoSuchElementException ex, NativeWebRequest request) {
		ProblemBuilder problemBuilder = Problem.builder().withTitle(getMessage(INTERNAL_SERVER_ERROR_TITLE))
				.withStatus(Status.INTERNAL_SERVER_ERROR).with(MESSAGE_KEY, ErrorConstants.ENTITY_NOT_FOUND_TYPE)
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());
		if (this.problemStacktrace) {
			problemBuilder.with(STACKTRACE_KEY, getStacktrace(ex));
		}
		return create(ex, problemBuilder.build(), request);
	}

	@ExceptionHandler(ConcurrencyFailureException.class)
	public ResponseEntity<Problem> handleConcurrencyFailure(ConcurrencyFailureException ex, NativeWebRequest request) {
		ProblemBuilder problemBuilder = Problem.builder().withTitle(getMessage(INTERNAL_SERVER_ERROR_TITLE))
				.withStatus(Status.CONFLICT).with(MESSAGE_KEY, ErrorConstants.ERR_CONCURRENCY_FAILURE)
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());
		if (this.problemStacktrace) {
			problemBuilder.with(STACKTRACE_KEY, getStacktrace(ex));
		}
		return create(ex, problemBuilder.build(), request);
	}

	@ExceptionHandler(GenericException.class)
	public ResponseEntity<Problem> handleGenericException(GenericException ex, NativeWebRequest request) {
		log.error("Cause: " + getMessage(ex.exceptionType().errorCode(), ex.getMessageParameters()));
		String stacktrace = getStacktrace(ex);
		log.error(stacktrace);
		ProblemBuilder problemBuilder = Problem.builder().withTitle(ex.getTitle()).withStatus(ex.getStatus())
				.with(MESSAGE_KEY,
						getMessage(MessageKey.of(ex.exceptionType().errorCode(), ex.exceptionType().errorCode()),
								ex.getMessageParameters()))
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());
		if (this.problemStacktrace) {
			problemBuilder.with(STACKTRACE_KEY, stacktrace);
		}
		return create(ex, problemBuilder.build(), request);
	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<Problem> handleThrowable(final Throwable ex, final NativeWebRequest request) {
		ProblemBuilder problemBuilder = Problem.builder().withType(ErrorConstants.DEFAULT_TYPE)
				.withTitle(getMessage(INTERNAL_SERVER_ERROR_TITLE)).withStatus(Status.INTERNAL_SERVER_ERROR)
				.with(MESSAGE_KEY, getMessage(INTERNAL_SERVER_ERROR_MESSAGE))
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());
		if (this.problemStacktrace) {
			problemBuilder.with(STACKTRACE_KEY, getStacktrace(ex));
		}
		return create(ex, problemBuilder.build(), request);
	}

	@ExceptionHandler(ClientAbortException.class) // When SSE channel is broken
	public ResponseEntity<Problem> handleClientAbortException(final ClientAbortException ex,
			final NativeWebRequest request) {
		ProblemBuilder problemBuilder = Problem.builder().withType(ErrorConstants.DEFAULT_TYPE)
				.withTitle(getMessage(INTERNAL_SERVER_ERROR_TITLE)).withStatus(Status.INTERNAL_SERVER_ERROR)
				.with(MESSAGE_KEY, getMessage(INTERNAL_SERVER_ERROR_MESSAGE))
				.with(PATH_KEY, request.getNativeRequest(HttpServletRequest.class).getRequestURI());
		if (this.problemStacktrace) {
			problemBuilder.with(STACKTRACE_KEY, getStacktrace(ex));
		}
		return create(ex, problemBuilder.build(), request);
	}

	public static String getStacktrace(final Throwable ex) {
		String stacktrace = ExceptionUtils.getStackTrace(ex);
		StringBuilder escapedStacktrace = new StringBuilder(stacktrace.length() + 100);
		StringCharacterIterator scitr = new StringCharacterIterator(stacktrace);

		char current = scitr.first();
		// DONE = \\uffff (not a character)
		while (current != StringCharacterIterator.DONE) {
			if (current == '\t') {
				escapedStacktrace.append("  ");
			} else if (current == '\n') {
				escapedStacktrace.append("");
			} else if (current == '\r') {
				escapedStacktrace.append("  ");
			} else {
				// nothing matched - just text as it is.
				escapedStacktrace.append(current);
			}
			current = scitr.next();
		}
		return escapedStacktrace.toString();
	}
}
