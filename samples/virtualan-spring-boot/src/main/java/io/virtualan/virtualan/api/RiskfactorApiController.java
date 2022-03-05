package io.virtualan.virtualan.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:59.311440500-06:00[America/Chicago]")
@Controller
@RequestMapping("${openapi.riskfactor.base-path:/api}")
public class RiskfactorApiController implements RiskfactorApi {

    private final NativeWebRequest request;

    @Autowired
    public RiskfactorApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
