package com.xebia.postit.common.i18n;

public enum PostItMessageCode implements MessageCodeType {

    // @formatter:off
    RECORD_NOT_FOUND("record.not.found", "No such record exists"),
    RECORDS_NOT_FOUND("records.not.found", "No records found matching the given parameters"),
    WEB_LINK_CEATED("weblink.created.successfully", "A New Web Link resource created successfully"),
    WEB_LINK_MARKED_FAVOURITE("weblink.marked.favourite.successfully", "Web Link is marked favourite successfully"),
    WEB_LINK_DE_ACTIVATED("weblink.deactivated.successfully", "Web Link de activated successfully");
    
	// @formatter:on

    private String key;

    private String defaultMessage;

    private PostItMessageCode(final String messageCode, final String defaultMessage) {
        this.key = messageCode;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public String defaultMessage() {
        return this.defaultMessage;
    }
}
