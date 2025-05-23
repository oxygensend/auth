package com.oxygensend.common;

import java.util.Collection;

public final class AssertionConcern {

    private AssertionConcern() {
    }

    public static void assertArgumentEquals(Object anObject1, Object anObject2, String aMessage) {
        if (!anObject1.equals(anObject2)) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentFalse(boolean aBoolean, String aMessage) {
        if (aBoolean) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentLength(String aString, int aMaximum, String aMessage) {
        int length = aString.trim().length();
        if (length > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentLength(String aString, int aMinimum, int aMaximum, String aMessage) {
        int length = aString.trim().length();
        if (length < aMinimum || length > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentNotEmpty(String aString, String aMessage) {
        if (aString == null || aString.isEmpty()) {
            throw new IllegalArgumentException(aMessage);
        }
    }
    public static void assertArgumentNotEmpty(Collection<?> aCollection, String aMessage) {
        if (aCollection == null || aCollection.isEmpty()) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentNotEquals(Object anObject1, Object anObject2, String aMessage) {
        if (anObject1.equals(anObject2)) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentNotNull(Object anObject, String aMessage) {
        if (anObject == null) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentRange(double aValue, double aMinimum, double aMaximum, String aMessage) {
        if (aValue < aMinimum || aValue > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentRange(float aValue, float aMinimum, float aMaximum, String aMessage) {
        if (aValue < aMinimum || aValue > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentRange(int aValue, int aMinimum, int aMaximum, String aMessage) {
        if (aValue < aMinimum || aValue > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentRange(long aValue, long aMinimum, long aMaximum, String aMessage) {
        if (aValue < aMinimum || aValue > aMaximum) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertArgumentTrue(boolean aBoolean, String aMessage) {
        if (!aBoolean) {
            throw new IllegalArgumentException(aMessage);
        }
    }

    public static void assertStateFalse(boolean aBoolean, String aMessage) {
        if (aBoolean) {
            throw new IllegalStateException(aMessage);
        }
    }

    public static void assertStateTrue(boolean aBoolean, String aMessage) {
        if (!aBoolean) {
            throw new IllegalStateException(aMessage);
        }
    }

    public static void assertArgumentMatches(String aString, String aRegex, String aMessage) {
        if (!aString.matches(aRegex)) {
            throw new IllegalArgumentException(aMessage);
        }
    }
}
