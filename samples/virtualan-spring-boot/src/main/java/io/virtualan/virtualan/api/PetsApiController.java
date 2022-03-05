package io.virtualan.virtualan.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-03-04T23:51:59.733218500-06:00[America/Chicago]")
@Controller
@RequestMapping("${openapi.openAPIVirtualanPetstore.base-path:/api}")
public class PetsApiController implements PetsApi {

    private final NativeWebRequest request;

    @Autowired
    public PetsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
