package com.perk.perksdk.appsaholic;

/**
 * <h1>PerkEvent</h1>
 */
public abstract class PerkEvent {

    /**
     * {@link PerkEvent} subclass' Class.hashCode() (set in base class constructor)
     */
    protected final int code;

    public PerkEvent(Class<? extends PerkEvent> clazz) {
        code = clazz.hashCode();
    }

    /**
     * PerkEvent unique code used for processing this event separate from other event types.
     *
     * @return the event code
     */
    public abstract int getCode();

    /**
     * A specific message regarding this event.
     *
     * @return message string
     */
    public abstract String getMessage();

    /**
     * Event data. Could be anything so subclass events must clearly document what data will be returned.
     *
     * @return null OR specifically defined data of a particular type.
     */
    public abstract Object getData();
}
