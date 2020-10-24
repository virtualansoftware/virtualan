package io.virtualan.core.util;

import java.util.Comparator;

import io.virtualan.core.model.VirtualServiceKeyValue;

public class BestMatchComparator implements Comparator<ReturnMockResponse> {
    @Override
    public int compare(ReturnMockResponse returnMockResponse1,
            ReturnMockResponse returnMockResponse2) {
        if (returnMockResponse2.getNumberAttrMatch() == returnMockResponse1.getNumberAttrMatch()
                && isSame(returnMockResponse1, returnMockResponse2)) {
            return 0;
        } else if (returnMockResponse2.getNumberAttrMatch() == returnMockResponse1
                .getNumberAttrMatch() && isBestMatch(returnMockResponse1)) {
            return -1;
        } else if (returnMockResponse2.getNumberAttrMatch() >= returnMockResponse1
                .getNumberAttrMatch() && isBestMatch(returnMockResponse2)) {
            return 1;
        } else {
            return -1;
        }
    }

    private boolean isBestMatch(ReturnMockResponse returnMockResponse) {
        return returnMockResponse.getMockRequest().getAvailableParams() != null
                && returnMockResponse.getMockRequest().getAvailableParams()
                        .size() == returnMockResponse.getNumberAttrMatch();
    }

    private boolean isSame(ReturnMockResponse returnMockResponse1,
            ReturnMockResponse returnMockResponse2) {
        boolean isSame = false;
        if (isBestMatch(returnMockResponse1) && isBestMatch(returnMockResponse2) &&
            returnMockResponse1.getMockRequest().getAvailableParams()
                    .size() == returnMockResponse2.getMockRequest().getAvailableParams().size()
                    && returnMockResponse1.getNumberAttrMatch() == returnMockResponse2
                            .getNumberAttrMatch()) {
                isSame = true;
            if (getMatch(returnMockResponse1, returnMockResponse2)) {
                return false;
            }
        }
        return isSame;
    }

    private boolean getMatch(ReturnMockResponse returnMockResponse1,
        ReturnMockResponse returnMockResponse2) {
        for (final VirtualServiceKeyValue virtualServiceKeyValue1 : returnMockResponse1
                .getMockRequest().getAvailableParams()) {
            for (final VirtualServiceKeyValue virtualServiceKeyValue2 : returnMockResponse2
                    .getMockRequest().getAvailableParams()) {
                if (!(virtualServiceKeyValue1.getKey()
                        .equals(virtualServiceKeyValue2.getKey())
                        && virtualServiceKeyValue1.getValue()
                                .equals(virtualServiceKeyValue2.getValue()))) {
                    return true;
                }
            }
        }
        return false;
    }

}
