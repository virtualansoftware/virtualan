package org.openapitools.virtualan.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-07-12T09:46:05.173-05:00[America/Chicago]")

@Controller
@RequestMapping("${openapi.employeeSampleSpecification.base-path:/api}")
public class EmployeesApiController implements EmployeesApi {

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public EmployeesApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
