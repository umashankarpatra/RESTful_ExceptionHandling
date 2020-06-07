package com.xebia.postit.common.exception;

import org.zalando.problem.Status;

public class PostItException extends GenericException {

    private static final long serialVersionUID = 1L;

    protected static final String POST_IT_EXCEPTION_TITLE = "Post it exception";
    
    private ErrorCodeType exceptionType;

    public enum PostItExceptionType implements ErrorCodeType {

        //@formatter:off
        ACTIVE_RECORD_BY_ID_NOT_FOUND("active.record.by.id.not.found", POST_IT_EXCEPTION_TITLE),
        MALFORMED_URL("malformed.url", POST_IT_EXCEPTION_TITLE),
        URL_HOST_NOT_AVALABLE("url.host.not.avalable", POST_IT_EXCEPTION_TITLE),
        DUPLICATE_URL("duplicate.url", POST_IT_EXCEPTION_TITLE);
        //@formatter:on

        private final String errorCode;

        private final String title;

        private PostItExceptionType(final String errorCode, final String title) {
            this.errorCode = errorCode;
            this.title = title;
        }

        private PostItExceptionType(final String errorCode) {
            this.errorCode = errorCode;
            this.title = POST_IT_EXCEPTION_TITLE;
        }

        @Override
        public String errorCode() {
            return this.errorCode;
        }

        @Override
        public String title() {
            return this.title;
        }
    }

    public PostItException(PostItExceptionType exceptionType, Object... parameters) {
        super(exceptionType.title(), exceptionType.errorCode(), parameters);
        setExceptionType(exceptionType);
    }

    public PostItException(PostItExceptionType exceptionType, Status status, Object... parameters) {
        super(exceptionType.title(), exceptionType.errorCode(), status, parameters);
        setExceptionType(exceptionType);
    }

    private void setExceptionType(PostItExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public ErrorCodeType exceptionType() {
        return this.exceptionType;
    }
}
