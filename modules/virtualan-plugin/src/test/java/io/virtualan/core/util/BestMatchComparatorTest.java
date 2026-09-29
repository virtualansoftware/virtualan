package io.virtualan.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.VirtualServiceKeyValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BestMatchComparatorTest {

    private final BestMatchComparator comparator = new BestMatchComparator();

    private static ReturnMockResponse match(String name, int numberAttrMatch, String... params) {
        List<VirtualServiceKeyValue> availableParams = new ArrayList<>();
        for (int i = 0; i < params.length; i += 2) {
            availableParams.add(new VirtualServiceKeyValue(params[i], params[i + 1]));
        }
        MockRequest mockRequest = new MockRequest();
        mockRequest.setAvailableParams(availableParams);
        mockRequest.setRule(name);
        ReturnMockResponse returnMockResponse = new ReturnMockResponse();
        returnMockResponse.setMockRequest(mockRequest);
        returnMockResponse.setNumberAttrMatch(numberAttrMatch);
        return returnMockResponse;
    }

    private final ReturnMockResponse general = match("general", 1, "status", "available");
    private final ReturnMockResponse specific =
        match("specific", 2, "status", "available", "tag", "x");
    private final ReturnMockResponse otherSpecific =
        match("otherSpecific", 2, "status", "available", "name", "q");
    private final ReturnMockResponse partial =
        match("partial", 2, "status", "available", "tag", "x", "name", "q");
    private final ReturnMockResponse weakPartial =
        match("weakPartial", 1, "status", "available", "tag", "y");

    @Test
    void isAntisymmetric() {
        List<ReturnMockResponse> all =
            Arrays.asList(general, specific, otherSpecific, partial, weakPartial);
        for (ReturnMockResponse a : all) {
            for (ReturnMockResponse b : all) {
                assertEquals(Integer.signum(comparator.compare(a, b)),
                    -Integer.signum(comparator.compare(b, a)),
                    a.getMockRequest().getRule() + " vs " + b.getMockRequest().getRule());
            }
        }
    }

    @Test
    void ranksFullMatchAheadOfPartialMatchWithMoreMatchedParams() {
        assertEquals(-1, Integer.signum(comparator.compare(general, partial)));
        assertEquals(1, Integer.signum(comparator.compare(partial, general)));
    }

    @Test
    void ranksMoreSpecificFullMatchFirst() {
        assertEquals(1, Integer.signum(comparator.compare(general, specific)));
        assertEquals(-1, Integer.signum(comparator.compare(specific, general)));
    }

    @Test
    void ranksPartialMatchesByMatchedParamCount() {
        assertEquals(-1, Integer.signum(comparator.compare(partial, weakPartial)));
        assertEquals(1, Integer.signum(comparator.compare(weakPartial, partial)));
    }
}
