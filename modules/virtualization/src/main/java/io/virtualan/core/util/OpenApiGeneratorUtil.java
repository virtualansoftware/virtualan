package io.virtualan.core.util;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.virtualan.annotation.VirtualService;
import io.virtualan.autoconfig.ApplicationContextProvider;
import io.virtualan.core.VirtualServiceInfo;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.VirtualServiceRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.apache.commons.io.FileUtils;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.SpringCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class OpenApiGeneratorUtil {

  private static Logger logger = LoggerFactory.getLogger(OpenApiGeneratorUtil.class);
  private File srcFolder = VirtualanConfiguration.getSrcPath();
  private File destFolder = VirtualanConfiguration.getDestPath();
  private File dependencyFolder = VirtualanConfiguration.getDependencyPath();
  private File yamlFolder = VirtualanConfiguration.getYamlPath();

  @PostConstruct
  public void loadInitialYamlFiles() {
    File file = VirtualanConfiguration.getYamlPath();
    getYaml(file);
  }

  public void getYaml(File file) {
    if (file != null &&  file.listFiles() != null &&file.listFiles().length > 0) {
      for (File subFile : file.listFiles()) {
        if (subFile.isDirectory()) {
          getYaml(subFile);
        } else {
          generateRestApi(subFile.getName(), null);
        }
      }
    }
  }

  @Autowired
  private ApplicationContextProvider applicationContext;

  @Autowired
  private VirtualServiceUtil virtualServiceUtil;

  @Autowired
  private RequestMappingHandlerMapping requestMappingHandlerMapping;

  private static void deleteFolder(File file) {
    if (file != null && file.listFiles().length > 0) {
      for (File subFile : file.listFiles()) {
        if (subFile.isDirectory()) {
          deleteFolder(subFile);
          subFile.delete();
        } else {
          subFile.delete();
        }
      }
    }
  }

  private void compile(File dest) throws IOException {
    StringBuilder sb = new StringBuilder();
    File[] listOfFiles = dependencyFolder.listFiles();
    for (File file : listOfFiles) {
      if (file.isFile()) {
        sb.append(file.getAbsoluteFile()).append(":");
      }
    }
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null,
        StandardCharsets.UTF_8);
    Iterable<File> fileList = Arrays.asList(dest);
    fileManager.setLocation(StandardLocation.CLASS_PATH, Arrays.asList(listOfFiles));

    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, fileList);
    Collection allFiles = FileUtils.listFiles(srcFolder, new String[]{"java"}, true);
    Iterable<? extends JavaFileObject> compilationUnits = fileManager
        .getJavaFileObjectsFromFiles(allFiles);
    JavaCompiler.CompilationTask task = compiler
        .getTask(null, fileManager, diagnostics, null, null, compilationUnits);
    boolean status = task.call();
    if (!status) {
      /*Iterate through each compilation problem and print it*/
      for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
        logger.warn("Error on line {} in {}" + diagnostic.getLineNumber() + diagnostic);
      }
    }
    fileManager.close();
    return;
  }

  private void collectFiles(File file, List<String> list, String fileName) {
    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        collectFiles(child, list, fileName);
      }
    } else {
      if (file.getName().endsWith(fileName)) {
        list.add(file.getAbsolutePath());
      }
    }
  }

  private void openApiGenerator(String apiFile, VirtualServiceRequest request) throws IOException {
    OpenAPI openAPI = null;
    String fileName = null;
    if(apiFile != null) {
      fileName = apiFile.substring(0, apiFile.lastIndexOf("."));
       openAPI = new OpenAPIParser()
          .readLocation(
              yamlFolder.getAbsolutePath() + File.separator + fileName + File.separator + apiFile,
              null,
              new ParseOptions()).getOpenAPI();
      File newFile = new File(srcFolder.getAbsolutePath() + File.separator + fileName);
      if (!newFile.exists()) {
        newFile.mkdir();
      } else {
        deleteFolder(newFile);
      }
    } else {
      openAPI = OpenApiGenerator.generateAPI(request);
      fileName = request.getOperationId();
    }
    File newFile = new File(srcFolder.getAbsolutePath() + File.separator + fileName);
    SpringCodegen codegen = new SpringCodegen();
    codegen.setOutputDir(newFile.getAbsolutePath());
    ClientOptInput input = new ClientOptInput();
    DefaultGenerator generator = new DefaultGenerator();
    generator.setGeneratorPropertyDefault(CodegenConstants.MODELS, "true");
    generator.setGeneratorPropertyDefault(CodegenConstants.MODEL_TESTS, "false");
    generator.setGeneratorPropertyDefault(CodegenConstants.MODEL_DOCS, "false");
    generator.setGeneratorPropertyDefault(CodegenConstants.APIS, "true");
    generator.setGeneratorPropertyDefault(CodegenConstants.SUPPORTING_FILES, "true");
    codegen.setApiPackage("io.virtualan.api."+fileName);
    codegen.setModelPackage("io.virtualan.model."+fileName);
    codegen.additionalProperties().put(SpringCodegen.VIRTUAL_SERVICE, true);
    codegen.additionalProperties().put(SpringCodegen.JAVA_8, true);
    codegen.additionalProperties().put(SpringCodegen.SKIP_DEFAULT_INTERFACE, false);
    GlobalSettings.setProperty(CodegenConstants.SUPPORTING_FILES, "ApiUtil.java");
    input.openAPI(openAPI);
    input.config(codegen);
    generator.opts(input).generate();
  }

  public Map<String, Class> generateRestApi(String yamlFile, VirtualServiceRequest request) {
    try {
      Map<String, Class> loadedController = getVirtualServiceInfo()
          .findVirtualServices(applicationContext.getClassLoader());
      Map<String, Class> currentController = new HashMap<>();
      openApiGenerator(yamlFile, request);
      List<String> fileNames = new ArrayList<String>();
      File newFile = null;
      if (yamlFile != null){
        newFile = new File(destFolder.getAbsolutePath() + File.separator + yamlFile
            .substring(0, yamlFile.lastIndexOf(".")));
      }else {
        newFile = new File(destFolder.getAbsolutePath() + File.separator + request.getOperationId());
      }
      if(!newFile.exists()){
        newFile.mkdir();
      } else {
        deleteFolder(newFile);
      }
      compile(newFile);
      collectFiles(newFile, fileNames, ".class");
      ClassLoader classLoader = applicationContext.getClassLoader();
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
      method.setAccessible(true);
      method.invoke(classLoader, new Object[]{newFile.toURI().toURL()});
      for (String className : fileNames) {
        className = className.replace(newFile.getAbsolutePath(), "");
        className = className.replace('/', '.');
        className = className.replace('\\', '.');
        className = className.startsWith(".") ? className.substring(1) : className;
        className = className.substring(0, className.lastIndexOf("."));
        Class myController = classLoader.loadClass(className);
        String interfaceName = myController.getTypeName();
        if (interfaceName.endsWith("Controller")) {
          String name = myController.getTypeName();
          name = name.substring(name.lastIndexOf('.') + 1);
          name = name.substring(0, 1).toLowerCase() + name.substring(1);
          GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
          beanDefinition.setBeanClass(myController);
          beanDefinition.setAutowireCandidate(true);
          beanDefinition.setLazyInit(false);
          beanDefinition.setAbstract(false);
          logger.info(myController.getTypeName() + " : " + name);
          for (Class classssss : myController.getInterfaces()) {
            if (classssss.isAnnotationPresent(VirtualService.class)) {
              currentController.put(name, classssss);
              logger.info(myController.getTypeName() + " : " + name);
              if (applicationContext.containsBean(name)) {
                removeMapping(classssss);
                applicationContext.removeBean(name);
              }
              applicationContext.addBean(name, beanDefinition);
              addMapping(name, classssss);
            }
          }
        }
      }

      for (Map.Entry<String, Class> entry : loadedController.entrySet()) {
        if (entry.getValue().isAnnotationPresent(VirtualService.class)) {
          String controllerName = entry.getKey().replace("api", "Api")+"Controller";
          if (!currentController.containsKey(controllerName)) {
            removeMapping(entry.getValue());
            applicationContext.removeBean(controllerName);
          }
        }
      }
      getVirtualServiceInfo().loadVirtualServices(applicationContext.getClassLoader());
      return getVirtualServiceInfo().findVirtualServices(applicationContext.getClassLoader());
    } catch (Exception e) {
      logger.warn("Unable to process :" + e.getMessage());
    }
    return null;
  }

  private void removeMapping(Class myControllerClzzz) {
    for (Method method : myControllerClzzz.getDeclaredMethods()) {
      boolean isPost = method.isAnnotationPresent(PostMapping.class);
      if (isPost) {
        PostMapping annotation = method.getAnnotation(PostMapping.class);
        Builder requestMappingInfo = RequestMappingInfo
            .paths(annotation.value()[0])
            .methods(RequestMethod.POST);
        if (annotation.consumes().length > 0) {
          requestMappingInfo = requestMappingInfo.consumes(annotation.consumes()[0]);
        }

        if (annotation.produces().length > 0) {
          requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
        }
        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo.build());
        logger.info("Unregistered: " + requestMappingInfo.build());
      }
      boolean isGet = method.isAnnotationPresent(GetMapping.class);
      if (isGet) {
        GetMapping annotation = method.getAnnotation(GetMapping.class);
        Builder requestMappingInfo = RequestMappingInfo
            .paths(annotation.value()[0])
            .methods(RequestMethod.GET);
        if (annotation.produces().length > 0) {
          requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
        }
        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo.build());
        logger.info("Unregistered: " + requestMappingInfo.build());
      }

      boolean isPut = method.isAnnotationPresent(PutMapping.class);
      if (isPut) {
        PutMapping annotation = method.getAnnotation(PutMapping.class);
        Builder requestMappingInfo = RequestMappingInfo
            .paths(annotation.value()[0])
            .methods(RequestMethod.PUT);
        if (annotation.produces().length > 0) {
          requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
        }
        if (annotation.consumes().length > 0) {
          requestMappingInfo = requestMappingInfo.consumes(annotation.consumes()[0]);
        }
        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo.build());
        logger.info("Unregistered: " + requestMappingInfo.build());
      }

      boolean isDelete = method.isAnnotationPresent(DeleteMapping.class);
      if (isDelete) {
        DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
        Builder requestMappingInfo = RequestMappingInfo
            .paths(annotation.value()[0])
            .methods(RequestMethod.DELETE);
        if (annotation.produces().length > 0) {
          requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
        }
        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo.build());
        logger.info("Unregistered: " + requestMappingInfo.build());
      }
    }
  }


  private void addMapping(String name, Class myControllerClzzz) {
    for (Method method : myControllerClzzz.getDeclaredMethods()) {
       boolean isPost = method.isAnnotationPresent(PostMapping.class);
        if (isPost) {
          PostMapping annotation = method.getAnnotation(PostMapping.class);
          Builder requestMappingInfo = RequestMappingInfo
              .paths(annotation.value()[0])
              .methods(RequestMethod.POST);
          if (annotation.consumes().length > 0) {
            requestMappingInfo = requestMappingInfo.consumes(annotation.consumes()[0]);
          }

          if (annotation.produces().length > 0) {
            requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
          }
        requestMappingHandlerMapping.registerMapping(requestMappingInfo.build(), name, method);
        logger.info("Registered: " + requestMappingInfo.build().toString());
        }
        boolean isGet = method.isAnnotationPresent(GetMapping.class);
        if (isGet) {
          GetMapping annotation = method.getAnnotation(GetMapping.class);
          Builder requestMappingInfo = RequestMappingInfo
              .paths(annotation.value()[0])
              .methods(RequestMethod.GET);
          if (annotation.produces().length > 0) {
            requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
          }
          requestMappingHandlerMapping.registerMapping(requestMappingInfo.build(), name, method);
          logger.info("Registered: " + requestMappingInfo.build().toString());
        }

        boolean isPut = method.isAnnotationPresent(PutMapping.class);
        if (isPut) {
          PutMapping annotation = method.getAnnotation(PutMapping.class);
          Builder requestMappingInfo = RequestMappingInfo
              .paths(annotation.value()[0])
              .methods(RequestMethod.PUT);
          if (annotation.produces().length > 0) {
            requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
          }
          if (annotation.consumes().length > 0) {
            requestMappingInfo = requestMappingInfo.consumes(annotation.consumes()[0]);
          }
          requestMappingHandlerMapping.registerMapping(requestMappingInfo.build(), name, method);
          logger.info("Registered: " + requestMappingInfo.build().toString());
        }

        boolean isDelete = method.isAnnotationPresent(DeleteMapping.class);
        if (isDelete) {
          DeleteMapping annotation = method.getAnnotation(DeleteMapping.class);
          Builder requestMappingInfo = RequestMappingInfo
              .paths(annotation.value()[0])
              .methods(RequestMethod.DELETE);
          if (annotation.produces().length > 0) {
            requestMappingInfo = requestMappingInfo.produces(annotation.produces()[0]);
          }
          requestMappingHandlerMapping.registerMapping(requestMappingInfo.build(), name, method);
          logger.info("Registered: " + requestMappingInfo.build().toString());
        }
      }
    }

  private String getName(Class classzz) {
    String interfaceName = classzz.getTypeName();
    interfaceName = interfaceName.substring(interfaceName.lastIndexOf('.') + 1);
    interfaceName = interfaceName.toLowerCase();
    return interfaceName;
  }

  /**
   * Gets virtual service info.
   *
   * @return the virtual service info
   */
  private VirtualServiceInfo getVirtualServiceInfo() {
    return virtualServiceUtil.getVirtualServiceInfo();
  }

}