package com.allya.kerilia.exception;

public final class StreamableExceptionUtils {
    private StreamableExceptionUtils() {
        // utility class
    }

    public static void uncheck(final RunnableWithExceptions t) {
        try {
            t.accept();
        }
        catch (final Exception e) {
            throwAsUnchecked(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(final Exception e) throws E {
        throw (E) e;
    }
}
