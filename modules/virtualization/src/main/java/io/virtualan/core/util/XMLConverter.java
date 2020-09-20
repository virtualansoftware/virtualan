/*
 *
 * Copyright 2018 Virtualan Contributors (https://virtualan.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.virtualan.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * xml response converter.
 *
 * @author Elan Thangamani
 *
 **/
@Service("xmlConverter")
public class XMLConverter {

    private static final Logger log = LoggerFactory.getLogger(XMLConverter.class);

    @Autowired
    private ObjectMapper objectMapper;

    public String returnAsXml(Method method, ResponseEntity responseEntity, String response) {
        String responseOut = null;
        if (!responseEntity.getHeaders().isEmpty() && MediaType.APPLICATION_XML
                .compareTo(responseEntity.getHeaders().getContentType()) == 0) {
            responseOut = convertAsXml(method, responseEntity, response);
        } else {
            responseOut = response;
        }
        return responseOut;
    }



    public static Object xmlToObject(Class type, String xmlString) throws JAXBException {
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);
        try {
            final JAXBContext jxbContext = JAXBContext.newInstance(type);
            final Unmarshaller jaxbUnmarshaller = jxbContext.createUnmarshaller();
            Object object = jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            return object;
        } catch (final JAXBException e) {
            XMLConverter.log.error("Unable to convert as xml :" + e.getMessage());
            throw e;
        }
    }

    public static String objectToXML(Class type, Object obj) {
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);
        try {
            final JAXBContext jxbContext = JAXBContext.newInstance(type);
            final JAXBElement jxbElement =
                    new JAXBElement(new QName(type.getSimpleName().toLowerCase()), type, obj);
            outWriter = new StringWriter();
            result = new StreamResult(outWriter);
            final Marshaller marshaller = jxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jxbElement, result);
        } catch (final JAXBException e) {
            XMLConverter.log.error("Unable to convert as xml :" + e.getMessage());
        }
        return outWriter.toString();
    }



    public String convertAsXml(Method method, ResponseEntity responseEntity, String json) {
        String resposeXML = null;
        Class mySuperclass = null;
        mySuperclass = method.getReturnType();
        final Class type = getReturnType(method);
        XMLConverter.log.info("response type" + type + " : mySuperclass :: " + mySuperclass);
        if ((mySuperclass.equals(ResponseEntity.class) || mySuperclass.equals(Response.class))
                && type != null) {
            mySuperclass = type;
        }
        try {
            if (json == null) {
                json = responseEntity.getBody().toString();
            }
            final Object object =
                    objectMapper.readValue(json, objectMapper.constructType(mySuperclass));
            resposeXML = XMLConverter.objectToXML(mySuperclass, object);
        } catch (JsonParseException | JsonMappingException e) {
            XMLConverter.log.error("Unable to convert as object :" + e.getMessage());
        } catch (final IOException e) {
            XMLConverter.log.error("Unable to convert as object :" + e.getMessage());
        }
        return resposeXML;
    }


    public Class getReturnType(Method method) {
        try {
            return (Class) ((ParameterizedType) method.getGenericReturnType())
                    .getActualTypeArguments()[0];
        } catch (final Exception e) {
            XMLConverter.log.warn("Unable to convert as class : " + method.getGenericReturnType());
        }
        return null;
    }

}
