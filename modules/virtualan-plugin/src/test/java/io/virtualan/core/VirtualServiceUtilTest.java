package io.virtualan.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.virtualan.api.VirtualServiceType;
import io.virtualan.core.model.MockRequest;
import io.virtualan.core.model.MockResponse;
import io.virtualan.core.model.MockServiceRequest;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import io.virtualan.core.util.ReturnMockResponse;
import io.virtualan.core.util.VirtualServiceParamComparator;
import io.virtualan.core.util.VirtualServiceValidRequest;
import io.virtualan.core.util.XMLConverter;
import io.virtualan.custom.message.ResponseException;
import io.virtualan.service.VirtualService;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

class VirtualServiceUtilTest {

    interface PetApi {
        ResponseEntity<String> findPets(String status, String tag);
    }

    private VirtualServiceUtil virtualServiceUtil;
    private VirtualServiceValidRequest virtualServiceValidRequest;
    private Method method;

    @BeforeEach
    void setUp() throws Exception {
        virtualServiceUtil = new VirtualServiceUtil();
        virtualServiceValidRequest = mock(VirtualServiceValidRequest.class);
        VirtualService virtualService = mock(VirtualService.class);
        when(virtualService.readByOperationId(any(), any())).thenReturn(Collections.emptyList());
        VirtualParameterizedUtil virtualParameterizedUtil = mock(VirtualParameterizedUtil.class);
        when(virtualParameterizedUtil.getParameterizedResponse(any(), any()))
            .thenReturn(new HashMap<>());
        when(virtualServiceValidRequest.validBusinessRuleForInputObject(any(), any()))
            .thenReturn(new HashMap<>());
        when(virtualServiceValidRequest.checkScriptResponse(any(), any()))
            .thenReturn(new HashMap<>());

        ReflectionTestUtils.setField(virtualServiceUtil, "virtualServiceValidRequest",
            virtualServiceValidRequest);
        ReflectionTestUtils.setField(virtualServiceUtil, "virtualService", virtualService);
        ReflectionTestUtils.setField(virtualServiceUtil, "xmlConverter", new XMLConverter());
        ReflectionTestUtils.setField(virtualServiceUtil, "objectMapper", new ObjectMapper());
        virtualServiceUtil.setVirtualParameterizedUtil(virtualParameterizedUtil);
        method = PetApi.class.getMethod("findPets", String.class, String.class);
    }

    private static ReturnMockResponse match(String output, int numberAttrMatch,
        boolean exactMatch, String... params) {
        List<VirtualServiceKeyValue> availableParams = new ArrayList<>();
        for (int i = 0; i < params.length; i += 2) {
            availableParams.add(new VirtualServiceKeyValue(params[i], params[i + 1]));
        }
        MockRequest mockRequest = new MockRequest();
        mockRequest.setAvailableParams(availableParams);
        ReturnMockResponse returnMockResponse = new ReturnMockResponse();
        returnMockResponse.setMockRequest(mockRequest);
        returnMockResponse.setMockResponse(new MockResponse(output, "200"));
        returnMockResponse.setNumberAttrMatch(numberAttrMatch);
        returnMockResponse.setExactMatch(exactMatch);
        return returnMockResponse;
    }

    private Object respond(ReturnMockResponse... matches) throws Exception {
        Map<Integer, ReturnMockResponse> matchMap = new LinkedHashMap<>();
        for (int i = 0; i < matches.length; i++) {
            matchMap.put(i + 1, matches[i]);
        }
        when(virtualServiceValidRequest.isResponseExists(any(), any())).thenReturn(matchMap);
        MockServiceRequest mockServiceRequest = new MockServiceRequest();
        Map<String, String> params = new HashMap<>();
        params.put("status", "available");
        params.put("tag", "x");
        mockServiceRequest.setParams(params);
        mockServiceRequest.setHeaderParams(Collections.singletonMap("accept", "application/json"));
        return virtualServiceUtil.returnResponse(method, mockServiceRequest);
    }

    private static String body(Object response) {
        return String.valueOf(((ResponseEntity<?>) response).getBody());
    }

    // request: status=available&tag=x
    // "exact"   mock: status=available              -> every mock param matches
    // "partial" mock: status=available&tag=x&name=q -> 2 of its 3 params match

    @Test
    void servesExactMatchWhenPartialMatchComesSecond() throws Exception {
        Object response = respond(
            match("exact", 1, true, "status", "available"),
            match("partial", 2, false, "status", "available", "tag", "x", "name", "q"));
        assertEquals("exact", body(response));
    }

    @Test
    void servesExactMatchWhenPartialMatchComesFirst() throws Exception {
        Object response = respond(
            match("partial", 2, false, "status", "available", "tag", "x", "name", "q"),
            match("exact", 1, true, "status", "available"));
        assertEquals("exact", body(response));
    }

    @Test
    void servesMostSpecificExactMatchRegardlessOfOrder() throws Exception {
        ReturnMockResponse general = match("general", 1, true, "status", "available");
        ReturnMockResponse partial =
            match("partial", 2, false, "status", "available", "tag", "x", "name", "q");
        ReturnMockResponse specific =
            match("specific", 2, true, "status", "available", "tag", "x");
        List<ReturnMockResponse> all = List.of(general, partial, specific);
        for (List<ReturnMockResponse> order : permutations(all)) {
            Object response = respond(order.toArray(new ReturnMockResponse[0]));
            assertEquals("specific", body(response), "order " + order);
        }
    }

    @Test
    void servesExactMatchThatHasNoParamsAheadOfPartialMatch() throws Exception {
        // an exact match with no params (e.g. matched on the request body) is not a full param
        // match, so it does not sort first; it must still be the one served
        Object response = respond(
            match("partial", 2, false, "status", "available", "tag", "x", "name", "q"),
            match("exact", 1, true));
        assertEquals("exact", body(response));
    }

    @Test
    void servesNotFoundWhenThereIsNoExactMatch() throws Exception {
        ReflectionTestUtils.setField(virtualServiceUtil, "virtualServiceType",
            VirtualServiceType.SPRING);
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("not found");
        ReflectionTestUtils.setField(virtualServiceUtil, "messageSource", messageSource);
        ResponseException thrown = assertThrows(ResponseException.class, () -> respond(
            match("partial", 2, false, "status", "available", "tag", "x", "name", "q")));
        assertEquals("not found", thrown.getResponseEntity().getBody());
    }

    @Test
    void getResponseForListStillServesHeadOfList() throws Exception {
        Object response = virtualServiceUtil.getResponse(method, Arrays.asList(
            match("first", 1, true, "status", "available"),
            match("second", 2, true, "status", "available", "tag", "x")));
        assertEquals("first", body(response));
    }

    private static VirtualServiceRequest storedMock(long id, String output, String... params) {
        VirtualServiceRequest mock = new VirtualServiceRequest();
        mock.setId(id);
        mock.setType("Response");
        mock.setHttpStatusCode("200");
        mock.setOutput(output);
        List<VirtualServiceKeyValue> availableParams = new ArrayList<>();
        for (int i = 0; i < params.length; i += 2) {
            availableParams.add(new VirtualServiceKeyValue(params[i], params[i + 1]));
        }
        mock.setAvailableParams(availableParams);
        return mock;
    }

    @Test
    void servesExactMatchThroughRealMatcher() throws Exception {
        VirtualServiceValidRequest realValidRequest = new VirtualServiceValidRequest();
        ReflectionTestUtils.setField(realValidRequest, "virtualServiceParamComparator",
            new VirtualServiceParamComparator());
        ReflectionTestUtils.setField(realValidRequest, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(virtualServiceUtil, "virtualServiceValidRequest",
            realValidRequest);
        VirtualService virtualService =
            (VirtualService) ReflectionTestUtils.getField(virtualServiceUtil, "virtualService");

        // The mocks are held in a HashMap keyed by identity-hashed values, so the order the
        // matcher sees them in changes from one load to the next; repeat to cover both orders.
        for (int attempt = 0; attempt < 50; attempt++) {
            when(virtualService.readByOperationId(any(), any())).thenReturn(Arrays.asList(
                storedMock(1, "exact", "status", "available"),
                storedMock(2, "partial", "status", "available", "tag", "x", "name", "q")));
            MockServiceRequest mockServiceRequest = new MockServiceRequest();
            Map<String, String> params = new HashMap<>();
            params.put("status", "available");
            params.put("tag", "x");
            Map<String, Class> paramsType = new HashMap<>();
            paramsType.put("status", String.class);
            paramsType.put("tag", String.class);
            mockServiceRequest.setParams(params);
            mockServiceRequest.setParamsType(paramsType);
            mockServiceRequest.setHeaderParams(Collections.singletonMap("accept", "application/json"));
            Object response = virtualServiceUtil.returnResponse(method, mockServiceRequest);
            assertEquals("exact", body(response), "attempt " + attempt);
        }
    }

    private static List<List<ReturnMockResponse>> permutations(List<ReturnMockResponse> items) {
        if (items.isEmpty()) {
            List<List<ReturnMockResponse>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        List<List<ReturnMockResponse>> result = new ArrayList<>();
        for (ReturnMockResponse item : items) {
            List<ReturnMockResponse> rest = new ArrayList<>(items);
            rest.remove(item);
            for (List<ReturnMockResponse> tail : permutations(rest)) {
                tail.add(0, item);
                result.add(tail);
            }
        }
        return result;
    }
}
