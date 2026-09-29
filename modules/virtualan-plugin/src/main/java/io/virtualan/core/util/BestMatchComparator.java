package io.virtualan.core.util;

import java.util.Comparator;

public class BestMatchComparator implements Comparator<ReturnMockResponse> {

    /**
     * Orders mock responses best first: a mock whose every param matched the request ranks ahead
     * of one that only partially matched, and within each group the mock with more matched params
     * ranks first. Ties keep their original order (the sort is stable).
     */
    @Override
    public int compare(ReturnMockResponse returnMockResponse1,
        ReturnMockResponse returnMockResponse2) {
        boolean isBestMatch1 = isBestMatch(returnMockResponse1);
        boolean isBestMatch2 = isBestMatch(returnMockResponse2);
        if (isBestMatch1 != isBestMatch2) {
            return isBestMatch1 ? -1 : 1;
        }
        return Integer.compare(returnMockResponse2.getNumberAttrMatch(),
            returnMockResponse1.getNumberAttrMatch());
    }

    private boolean isBestMatch(ReturnMockResponse returnMockResponse) {
        return returnMockResponse.getMockRequest().getAvailableParams() != null
            && returnMockResponse.getMockRequest().getAvailableParams()
            .size() == returnMockResponse.getNumberAttrMatch();
    }

}
