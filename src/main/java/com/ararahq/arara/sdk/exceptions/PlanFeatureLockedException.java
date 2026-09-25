package com.ararahq.arara.sdk.exceptions;

import com.ararahq.arara.sdk.models.AraraError;

/**
 * Thrown on {@code 403 PLAN_FEATURE_LOCKED}: the feature requires a higher plan.
 */
public class PlanFeatureLockedException extends AraraApiException {
    public static final String CODE = "PLAN_FEATURE_LOCKED";
    private static final int HTTP_FORBIDDEN = 403;

    public PlanFeatureLockedException(AraraError errorDetails) {
        super(HTTP_FORBIDDEN, errorDetails);
    }

    /**
     * @return The locked feature key, from {@code details.feature}.
     */
    public String getFeature() {
        return detailAsString("feature");
    }

    /**
     * @return The organization's current plan, from {@code details.currentPlan}.
     */
    public String getCurrentPlan() {
        return detailAsString("currentPlan");
    }

    /**
     * @return The minimum plan that unlocks the feature, from {@code details.upgradeTo}.
     */
    public String getUpgradeTo() {
        return detailAsString("upgradeTo");
    }

    private String detailAsString(String key) {
        Object value = getDetails().get(key);
        return value instanceof String ? (String) value : null;
    }
}
