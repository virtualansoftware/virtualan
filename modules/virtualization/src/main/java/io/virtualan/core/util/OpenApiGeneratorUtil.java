package io.virtualan.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.virtualan.annotation.VirtualService;
import io.virtualan.autoconfig.ApplicationContextProvider;
import io.virtualan.core.VirtualServiceInfo;
import io.virtualan.core.VirtualServiceUtil;
import io.virtualan.core.model.VirtualServiceRequest;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.GlobalSettings;
import org.openapitools.codegen.languages.SpringCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo.Builder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * The type Open api generator util.
 */
@Component
public class OpenApiGeneratorUtil {


  @Value("${virtualan.script.enabled:false}")
  private boolean scriptEnabled;

  private static Logger logger = LoggerFactory.getLogger(OpenApiGeneratorUtil.class);
  private File srcFolder = VirtualanConfiguration.getSrcPath();
  private File dependencyFolder = VirtualanConfiguration.getDependencyPath();
  private File yamlFolder = VirtualanConfiguration.getYamlPath();
  @Autowired
  private ApplicationContextProvider applicationContext;

  @Autowired
  private ApplicationContext  appContext;

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

  /**
   * Load initial yaml files.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void loadInitialYamlFiles()
          throws MalformedURLException, IntrospectionException {
    addURLToClassLoader(VirtualanConfiguration.getPath().toURI().toURL(), applicationContext.getClassLoader());
    File file = VirtualanConfiguration.getYamlPath();
    getYaml( file);
  }

  /**
   * Gets yaml.
   *
   * @param file the file
   */
  public void getYaml( File file) {
    if (file != null && file.listFiles() != null && file.listFiles().length > 0) {
      for (File subFile : file.listFiles()) {
        if (subFile.isDirectory()) {
          getYaml(subFile);
        } else {
          try {
            generateRestApi(scriptEnabled, subFile.getName(), null, appContext.getClassLoader());
          }catch (Exception e){
            logger.warn("Unable to process : " + e.getMessage());
          }
        }
      }
    }
  }

  private void compile(File src, File dest) throws IOException {
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
    Collection allFiles = FileUtils.listFiles(src, new String[]{"java"}, true);
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
    if (apiFile != null) {
      fileName = apiFile.substring(0, apiFile.lastIndexOf("."));
      openAPI = new OpenAPIParser()
          .readLocation(
              yamlFolder.getAbsolutePath() + File.separator + fileName + File.separator + apiFile,
              null,
              new ParseOptions()).getOpenAPI();
     } else {
      openAPI = OpenApiGenerator.generateAPI(request);
      fileName = OpenApiGenerator.getResource(request);
    }
    File newFile = new File(srcFolder.getAbsolutePath() + File.separator + fileName);
    if (!newFile.exists()) {
      newFile.mkdir();
    } else {
      deleteFolder(newFile);
    }
    SpringCodegen codegen = new SpringCodegen();
    codegen.setOutputDir(newFile.getAbsolutePath());
    ClientOptInput input = new ClientOptInput();
    DefaultGenerator generator = new DefaultGenerator();
    generator.setGeneratorPropertyDefault(CodegenConstants.MODELS, "true");
    generator.setGeneratorPropertyDefault(CodegenConstants.MODEL_TESTS, "false");
    generator.setGeneratorPropertyDefault(CodegenConstants.MODEL_DOCS, "false");
    generator.setGeneratorPropertyDefault(CodegenConstants.APIS, "true");
    generator.setGeneratorPropertyDefault(CodegenConstants.SUPPORTING_FILES, "true");
    Random rand = new Random(); //instance of random class
    int upperbound = 5000;
    int int_random = rand.nextInt(upperbound);
    codegen.setApiPackage("io.virtualan.api." + fileName + int_random);
    codegen.setModelPackage("io.virtualan.model." + fileName + int_random);
    codegen.additionalProperties().put(SpringCodegen.VIRTUAL_SERVICE, true);
    codegen.additionalProperties().put(SpringCodegen.JAVA_8, true);
    codegen.additionalProperties().put(SpringCodegen.SKIP_DEFAULT_INTERFACE, false);
    GlobalSettings.setProperty(CodegenConstants.SUPPORTING_FILES, "ApiUtil.java");
    input.openAPI(openAPI);
    input.config(codegen);
    generator.opts(input).generate();
  }

  /**
   * Remove rest api map.
   *
   * @param scriptEnabled the script enabled
   * @param yamlFile      the yaml file
   * @param request       the request
   * @param contextLoader the context loader
   * @return the map
   * @throws ClassNotFoundException the class not found exception
   * @throws InstantiationException the instantiation exception
   * @throws IllegalAccessException the illegal access exception
   * @throws IOException            the io exception
   * @throws IntrospectionException the introspection exception
   */
  public  Map<String, Map<String, VirtualServiceRequest>> removeRestApi(boolean scriptEnabled, String yamlFile, VirtualServiceRequest request, ClassLoader contextLoader)
          throws Exception {
    List<String> beans = new ArrayList<>();
    try {
      List<String> fileNames = new ArrayList<String>();
      List<String> fileNameAll = new ArrayList<String>();
      File destFile = null;
      destFile = new File(VirtualanConfiguration.getDestPath().getAbsolutePath() + File.separator + yamlFile);
      if (destFile.exists()) {
        collectFiles(destFile, fileNameAll, ".class");
        collectFiles(VirtualanConfiguration.getDestPath(), fileNames, ".class");
        for (String classNameRaw : fileNames) {
          String className = classNameRaw.replace(VirtualanConfiguration.getDestPath().getAbsolutePath(), "");
          className = className.substring(className.indexOf(File.separator, 1) + 1);
          className = className.replaceAll("/", ".");
          className = className.replaceAll("\\\\", ".");
          className = className.substring(0, className.lastIndexOf("."));
          String name = className;
          name = name.substring(name.lastIndexOf('.') + 1);
          name = name.substring(0, 1).toLowerCase() + name.substring(1);
          Class myController = applicationContext.getClassLoader().loadClass(className);
          if (fileNameAll.contains(classNameRaw) && name.endsWith("Controller")) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(myController);
            beanDefinition.setAutowireCandidate(true);
            beanDefinition.setLazyInit(false);
            beanDefinition.setAbstract(false);
            for (Class classssss : myController.getInterfaces()) {
              if (classssss.isAnnotationPresent(VirtualService.class)) {
                if (applicationContext.containsBean(name)) {
                  removeMapping(classssss);
                  applicationContext.removeBean(name);
                  beans.add(name);
                }
              }
            }
          }
        }
        deleteFolder(destFile);
        destFile.delete();
        File srcFile = new File(VirtualanConfiguration.getSrcPath().getAbsolutePath() + File.separator + yamlFile);
        deleteFolder(srcFile);
        srcFile.delete();
        File yaml = new File(VirtualanConfiguration.getYamlPath().getAbsolutePath() + File.separator + yamlFile);
        deleteFolder(yaml);
        yaml.delete();
        return getActiveServices(scriptEnabled, beans);
      } else {
        throw new Exception("This resource does not exists");
      }
    } catch (Exception e) {
      logger.warn("Unable to process :" + e.getMessage());
      getActiveServices(scriptEnabled, beans);
      if(e.getMessage() != null) {
        throw new Exception(e.getMessage());
      } else {
        throw new Exception("Unexpected error Check the logs");
      }
    }
  }

  @NotNull
  private Map<String, Map<String, VirtualServiceRequest>> getActiveServices(boolean scriptEnabled, List<String> list) throws ClassNotFoundException, JsonProcessingException, InstantiationException, IllegalAccessException {
    Map<String, Map<String, VirtualServiceRequest>> map = getVirtualServiceInfo().loadVirtualServices(scriptEnabled, applicationContext.getClassLoader().getParent());
    getVirtualServiceInfo().setResourceParent(getVirtualServiceInfo().loadMapper());
    Map<String, Map<String, VirtualServiceRequest>> response = new HashMap<>();
    for(String beans : list){
      String bean  = beans.replace("ApiController", "");
      if(map.containsKey(bean)){
        response.put(bean, map.get(bean));
      }
    }
    map.entrySet().removeIf(entry -> !applicationContext.containsBean(entry.getKey()+"ApiController"));
    return response;
  }

  private Resource[] getCatalogList(String path) throws IOException {
    final PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver(applicationContext.getClassLoader().getParent().getParent());
    return resolver.getResources(path);
  }

  /**
   * Add url to class loader.
   *
   * @param url         the url
   * @param classLoader the class loader
   * @throws IntrospectionException the introspection exception
   */
  public void addURLToClassLoader(URL url, ClassLoader classLoader) throws IntrospectionException {
    URLClassLoader systemClassLoader = null;
    if(classLoader instanceof  VirtualanClassLoader) {
      systemClassLoader
              = new URLClassLoader(new URL[]{url}, classLoader);
    } else  {
      systemClassLoader = (URLClassLoader) classLoader;
    }    Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
    try {
      Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
      method.setAccessible(true);
      method.invoke(systemClassLoader, new Object[]{url});
    } catch (Throwable t) {
      throw new IntrospectionException("Error when adding url to system ClassLoader ");
    }
  }

  /**
   * Generate rest api map.
   *
   * @param scriptEnabled the script enabled
   * @param yamlFile      the yaml file
   * @param request       the request
   * @param contextLoader the context loader
   * @return the map
   * @throws ClassNotFoundException  the class not found exception
   * @throws InstantiationException  the instantiation exception
   * @throws IllegalAccessException  the illegal access exception
   * @throws JsonProcessingException the json processing exception
   */
  public Map<String, Map<String, VirtualServiceRequest>> generateRestApi(boolean scriptEnabled, String yamlFile, VirtualServiceRequest request, ClassLoader contextLoader)
          throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException {
    List<String> addedBeans = new ArrayList();
    try {
      openApiGenerator(yamlFile, request);
      List<String> fileNames = new ArrayList<String>();
      List<String> fileNameAll = new ArrayList<String>();
      File destFile = null;
      File srcFile = null;
      if (yamlFile != null) {
        destFile = new File(VirtualanConfiguration.getDestPath().getAbsolutePath() + File.separator + yamlFile
                .substring(0, yamlFile.lastIndexOf(".")));
        srcFile = new File(VirtualanConfiguration.getSrcPath().getAbsolutePath() + File.separator + yamlFile
                .substring(0, yamlFile.lastIndexOf(".")));
      } else {
        destFile = new File(
                VirtualanConfiguration.getDestPath().getAbsolutePath() + File.separator + OpenApiGenerator.getResource(request));
        srcFile = new File(
                VirtualanConfiguration.getSrcPath().getAbsolutePath() + File.separator + OpenApiGenerator.getResource(request));
      }
      if (!destFile.exists()) {
        destFile.mkdir();
      } else {
        deleteFolder(destFile);
      }
      compile(srcFile, destFile);
      collectFiles(destFile, fileNameAll, ".class");
      collectFiles(destFile, fileNames, ".class");
      addURLToClassLoader(destFile.toURI().toURL(), applicationContext.getClassLoader().getParent());
      for (String classNameRaw : fileNames) {
        String className = classNameRaw.replace(VirtualanConfiguration.getDestPath().getAbsolutePath(), "");
        className = className.substring(className.indexOf(File.separator, 1) + 1);
        className = className.replaceAll("/", ".");
        className = className.replaceAll("\\\\", ".");
        className = className.substring(0, className.lastIndexOf("."));
        String name = className;
        name = name.substring(name.lastIndexOf('.') + 1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        Class myController = applicationContext.getClassLoader().loadClass(className);
        if (fileNameAll.contains(classNameRaw) && name.endsWith("Controller")) {
          GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
          beanDefinition.setBeanClass(myController);
          beanDefinition.setAutowireCandidate(true);
          beanDefinition.setLazyInit(false);
          beanDefinition.setAbstract(false);
          for (Class classssss : myController.getInterfaces()) {
            if (classssss.isAnnotationPresent(VirtualService.class)) {
              if (applicationContext.containsBean(name)) {
                removeMapping(classssss);
                applicationContext.removeBean(name);
              }
              applicationContext.addBean(name, beanDefinition);
              addMapping(name, classssss);
              addedBeans.add(name);
            }
          }
        }
      }

    } catch (Exception e) {
      logger.warn("Unable to process :" + e.getMessage());
    }
    return getActiveServices(scriptEnabled, addedBeans);
  }


  private void removeMapping(Class myControllerClzzz) throws IOException, ClassNotFoundException {
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


  private void addMapping(String name, Class myControllerClzzz)
      throws IOException, ClassNotFoundException {
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