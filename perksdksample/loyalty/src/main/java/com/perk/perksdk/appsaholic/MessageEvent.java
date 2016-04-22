package com.perk.perksdk.appsaholic;

/**
 * <h1>MessageEvent</h1>
 */
public class MessageEvent extends PerkEvent {
    private final String mMessage;
    public static final int CODE = MessageEvent.class.hashCode();

    public MessageEvent(String message) {
        super(MessageEvent.class);
        mMessage = message;
    }

    @Override
    public int getCode() {
        return CODE;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    /**
     * No data is associated with this event.
     *
     * @return null
     */
    @Override
    public Object getData() {
        return null;
    }
}
