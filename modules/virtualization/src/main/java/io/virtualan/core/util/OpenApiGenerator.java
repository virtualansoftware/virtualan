package io.virtualan.core.util;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.virtualan.core.model.VirtualServiceKeyValue;
import io.virtualan.core.model.VirtualServiceRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openapitools.codegen.serializer.SerializerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenApiGenerator {

  private static Logger logger = LoggerFactory.getLogger(OpenApiGenerator.class);

  private static Parameter getType(String type) {
    if ("PATH_PARAM".equalsIgnoreCase(type)) {
      return new PathParameter();
    } else if ("QUERY_PARAM".equalsIgnoreCase(type)) {
      return new QueryParameter();
    } else if ("HEADER_PARAM".equalsIgnoreCase(type)) {
      return new HeaderParameter();
    }
    return null;
  }

  private static String getTypeValue(String type) {
    if ("PATH_PARAM".equalsIgnoreCase(type)) {
      return "path";
    } else if ("QUERY_PARAM".equalsIgnoreCase(type)) {
      return "query";
    } else if ("HEADER_PARAM".equalsIgnoreCase(type)) {
      return "header";
    }
    return null;
  }

  public static OpenAPI generateAPI(VirtualServiceRequest request) throws IOException {
    String resource = getResource(request);
    OpenAPI openAPI = new OpenAPIParser()
        .readLocation(
            VirtualanConfiguration.getYamlPath().getAbsolutePath() + File.separator + resource
                + File.separator + resource + ".yaml",
            null,
            new ParseOptions()).getOpenAPI();
    Paths paths = openAPI != null ? openAPI.getPaths() : null;
    if (openAPI == null) {
      openAPI = new OpenAPI();
      Contact contact =
          new Contact()
              .email("info@virtualan.io")
              .name(
                  Stream.of(
                      "Elan Thangamani",
                      "Virtualan Software")
                      .filter(s -> s != null)
                      .collect(Collectors.joining(" - ")))
              .url("https://virtualan.io");
      String title = "Virtualan OnDemand Contract";
      String version = "3.0.0";
      Info info =
          new Info()
              .contact(contact)
              .title(title)
              .description("Virtualan generated Contract")
              .version(version);
      openAPI.info(info);
      // the servers
      openAPI
          .servers(Arrays.asList(new Server().url("http://localhost:8080/api/")));
      paths = new Paths();
    }
    PathItem pathItem = new PathItem();
    Operation operation = new Operation();
    ApiResponse apiResponse = new ApiResponse();
    Content content = new Content();
    MediaType mediaType = new MediaType();
    mediaType.schema(new StringSchema());
    content.addMediaType("application/json", mediaType);
    apiResponse.setContent(content);
    apiResponse.setDescription(request.getHttpStatusCode() + "  response object generated.");
    ApiResponses apiResponses = new ApiResponses();
    apiResponses.addApiResponse(request.getHttpStatusCode(), apiResponse);
    operation.setResponses(apiResponses);

    List<Parameter> parameterList = new ArrayList<>();
    for (VirtualServiceKeyValue vkey : request.getAvailableParams()) {
      Parameter parameter = getType(vkey.getParameterType());
      parameter.setName(vkey.getKey());
      parameter.schema(new StringSchema());
      parameter.setIn(getTypeValue(vkey.getParameterType()));
      parameterList.add(parameter);
    }
    operation.setParameters(parameterList);
    if (request.getInput() != null) {
      JsonObject object = (JsonObject) JsonReader.jsonToJava(request.getInput().toString());
      RequestBody body = new RequestBody();
      Content content1 = new Content();
      MediaType mediaType1 = new MediaType();
      mediaType1.schema(new StringSchema());
      content1.addMediaType("application/json", mediaType1);
      body.setContent(content1);
      operation.setRequestBody(body);
    }

    String oper = request.getUrl().replaceAll("}", "").replaceAll("\\{", "");
    String[] operationArray = oper.split("/");
    StringBuilder builder = new StringBuilder();
    for (String array : operationArray) {
      if (!array.isEmpty()) {
        builder.append(array.substring(0, 1).toUpperCase() + array.substring(1));
      }
    }
    operation.setOperationId(
        builder.toString().substring(0, 1).toLowerCase() + builder.toString().substring(1) + request
            .getMethod());
    request.setOperationId(operation.getOperationId());
    if ("GET".equalsIgnoreCase(request.getMethod())) {
      pathItem.setGet(operation);
    } else if ("POST".equalsIgnoreCase(request.getMethod())) {
      pathItem.setPost(operation);
    } else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
      pathItem.setDelete(operation);
    } else if ("PUT".equalsIgnoreCase(request.getMethod())) {
      pathItem.setPut(operation);
    } else if ("PATCH".equalsIgnoreCase(request.getMethod())) {
      pathItem.setPatch(operation);
    }
    paths.addPathItem(request.getUrl(), pathItem);
    openAPI.setPaths(paths);
    String yaml = SerializerUtils.toYamlString(openAPI);
    logger.info(yaml);
    File newFile = new File(VirtualanConfiguration.getYamlPath()
        + File.separator + resource);
    if (!newFile.exists()) {
      newFile.mkdir();
    }
    VirtualanConfiguration.writeYaml(newFile+ File.separator + resource + ".yaml",
        new ByteArrayInputStream(yaml.getBytes()));
    return openAPI;
  }

  public static String getResource(VirtualServiceRequest request) {
    String resource = "default";
    if (request.getUrl().length() > 1) {
      if (request.getUrl().indexOf("/") == -1 ) {
        resource = request.getUrl();
      } else if (request.getUrl().indexOf("/", 1) != -1) {
        resource = request.getUrl().substring(1, request.getUrl().indexOf("/", 1));
      } else {
        resource = request.getUrl().substring(1);
      }
    }
    return resource;
  }

}
