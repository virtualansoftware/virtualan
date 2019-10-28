package io.virtualan.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2018-08-17T17:47:45.300-05:00[America/Chicago]")

@Controller
@RequestMapping("${openapi.openAPIPetstore.base-path:/api}")
public class StoreApiController implements StoreApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public StoreApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
