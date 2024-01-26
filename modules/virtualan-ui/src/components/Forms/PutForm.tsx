import React, { useState, useRef, useEffect } from "react";

import Form from "react-bootstrap/Form";
import Stack from "react-bootstrap/Stack";
import Alert from "react-bootstrap/Alert";
import Collapse from "react-bootstrap/Collapse";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";

import HttpStatusList from "../../api/HttpStatusList.json";
import RequestType from "../../api/RequestType.json";
import ResponseList from "../../api/ResponseList.json";
import { apiRequestsPost } from "../../api/apiRequests";
import Selects from "../Blocks/Selects";
import HeaderParams from "../Blocks/HeaderParams";
import AdditionalParams from "../Blocks/AdditionalParams";
import MockResponse from "../Blocks/MockResponse";
import MockRequestBody from "../Blocks/MockRequestBody";
import Script from "../Blocks/Script";
import RespHeaderParams from "../Blocks/RespHeaderParams";
import ExcludeList from "../Blocks/ExcludeList";
import FormButtons from "../Blocks/FormButtons";
import { v4 as uuidv4 } from "uuid";

interface Props {
  operationId: string;
  resource: string;
  path: string;
  availableParams: string[];
  apiEntryPointPost: string;
}

const PutForm = ({  operationId, resource, path, availableParams, apiEntryPointPost }: Props) => {
  const [showForm, setShowForm] = useState(false);
  const [queryParams, setQueryParams] = useState<{ [key: string]: string }>({});
  const [reqParams, setReqParams] = useState([]);
  const [respParams, setRespParams] = useState([]);
  const [flashMessage, setFlashMessage] = useState("");
  // const [showRuleBlock, setShowRuleBlock] = useState("");
  const [selectorType, setSelectorType] = useState("");
  const [httpStatusCode, setHttpStatusCode] = useState("");
  const [contentType, setContentType] = useState("");
  const [resetKey, setResetKey] = useState(uuidv4());
  const [paramsKeys, setParamsKeys] = useState([]);
  const [mockResponse, setMockResponse] = useState("");
  const [paramsSamples, setParamsSamples] = useState([]);
  const [flashErrorMessage, setFlashErrorMessage] = useState("");
  const [mockRequest, setMockRequest] = useState("");

  const scriptRef = useRef(null);
  const excludeListRef = useRef(null);

  useEffect(() => {
    if (selectorType !== "Params") {
      return;
    }

    const regex = /<([^>]+)>/g;
    let queryParamsMatches: string[] = [];
    Object.values(queryParams).forEach((value) => {
      const matches = value.match(regex);
      if (matches) {
        queryParamsMatches = [...queryParamsMatches, ...matches];
      }
    });
    if (queryParamsMatches.length > 0) {
      queryParamsMatches = queryParamsMatches.map((match: string) =>
        match.slice(1, -1)
      );
    }

    let mockRequestMatches: any = mockRequest.match(regex);
    if (mockRequestMatches) {
      mockRequestMatches = mockRequestMatches.map((match: string) =>
        match.slice(1, -1)
      );
    }

    let mockResponseMatches: any = mockResponse.match(regex);
    if (mockResponseMatches) {
      mockResponseMatches = mockResponseMatches.map((match: string) =>
        match.slice(1, -1)
      );
    }

    setParamsKeys([
      ...new Set([
        ...(queryParamsMatches || []),
        ...(mockRequestMatches || []),
        ...(mockResponseMatches || []),
      ]),
    ]);
  }, [queryParams, mockRequest, mockResponse, selectorType]);


  const selectRefs = {
    status: useRef(null),
    type: useRef(null),
    requestType: useRef(null),
  };

  const contentStyle = {
    height: "auto",
    border: "none",
    margin: "0px",
    padding: "0px",
  };

  const handleSelectChange = (data: any) => {
    setSelectorType(data.type);
    setHttpStatusCode(data.status);
    setContentType(data.requestType);
  };

  const formId = uuidv4();

  const http_status = HttpStatusList;
  const request_type = RequestType;
  const response_list = ResponseList;

  const createMockRequest = (apiEntryPointPost: any, dataToSubmit : any) =>{
    const output = apiRequestsPost(apiEntryPointPost, dataToSubmit);
    output.then((response: any) => JSON.stringify(response)) //2
    .then((data : any) => {
      const jsondata  = JSON.parse(data);
        setFlashMessage( "Success: " + jsondata.data.mockStatus.code)
        setFlashErrorMessage("");
    }).catch(error =>             {
      console.log(error.response);
      const jsondata  = JSON.parse(JSON.stringify(error.response));
      setFlashErrorMessage( "Fail: " + jsondata.data.code);
      setFlashMessage("");
    }
    );    
  }

  const handleMockResponseChange = (value: string) => {
    setMockResponse(value);
    // console.log('mockResponse', value);
  };

  const handleMockRequestChange = (value: string) => {
    setMockRequest(value);
    // console.log('mockResponse', value);
  };


  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    // console.log("selectRefs", selectRefs);
    // console.log("queryParams", queryParams); // ok
    // console.log("reqParams", reqParams); // ok
    // console.log("respParams", respParams); // ok
    // console.log("mockResponse", mockResponseRef.current.value);
    // console.log("excludeList", excludeListRef.current.value);

    const dataToSubmit = {
      operationId: operationId,
      url: path,
      httpStatusCode: httpStatusCode,
      type: selectorType,
      contentType: contentType,
      method: "PUT",
      rule:  scriptRef != null && scriptRef.current ? scriptRef.current.value : "",
      input:  mockRequest,
      output: mockResponse,
      availableParams:  Object.entries(reqParams.push(queryParams)).map(([key, value]) => (({ key, value }))),
      headerParams: Object.entries(respParams).map(([key, value]) => ({ key, value })),
      resource: resource,
      excludeList: excludeListRef.current.value
    };

    try {
      createMockRequest(apiEntryPointPost, dataToSubmit);
    } catch (error) {
      console.error("Error making POST request:", error);
      setFlashErrorMessage("Error making POST request." + error);
    }
    setTimeout(() => {
      setFlashMessage("");
      setFlashErrorMessage("")
    }, 5000);
    handleResetForm();
  };

  const handleResetForm = () => {
    setMockRequest("");
    setMockResponse("");
    const excludeListField = document.getElementById(
      "excludeList" + formId
    ) as HTMLInputElement;
    excludeListField.value = "";
    setReqParams([]);
    setRespParams([]);
    setQueryParams({});
    setResetKey(uuidv4());
    setFlashMessage("");
    setFlashErrorMessage("")
  };

  const handleDelParams = (key: string, params: any, setParams: any) => {
    setParams(params.filter((item: any) => item.key !== key));
  };

  const handleAddParams = (
    keyInputId: string,
    valueInputId: string,
    paramsArray: any,
    setParamsArray: any
  ) => {
    const keyInput = document.getElementById(keyInputId) as HTMLInputElement;
    const valueInput = document.getElementById(
      valueInputId
    ) as HTMLInputElement;
    const key = keyInput.value.trim();
    const value = valueInput.value.trim();

    // // test with valid invalid valid characters
    // const validPattern = /^[a-zA-Z0-9]+$/;
    // if (key.match(validPattern) && value.match(validPattern))

    if (key !== "" && value !== "") {
      const index = paramsArray.findIndex((item: any) => item.key === key);
      if (index !== -1) {
        const updatedParams = [...paramsArray];
        updatedParams[index].value = value;
        setParamsArray(updatedParams);
      } else {
        setParamsArray([...paramsArray, { key, value }]);
      }
      keyInput.value = "";
      valueInput.value = "";
      keyInput.focus();
    }
  };

  const handleAddQueryParams = (key: string, value: string) => {
    queryParams[key] = value;
    setQueryParams(queryParams);
  };

  return (
    <div className="button-put-box button-box">
      <div
        className="button-put-path button-path"
        onClick={() => setShowForm(!showForm)}
      >
        <span className="form-button button-put">PUT</span>
        {" " + path}
      </div>

      <Collapse in={showForm}>
        <div style={contentStyle}>
         
          <Form onSubmit={handleSubmit}>
            <Stack gap={3}>
              {/*  */}
               <Selects
                onSelectionChange={handleSelectChange}
                resetKey={resetKey}
              />
              {/*  */}
             <HeaderParams
                availableParams={availableParams}
                queryParams={queryParams}
                setQueryParams={setQueryParams}
              />
              {/*  */}
              <AdditionalParams
                reqParams={reqParams}
                setReqParams={setReqParams}
                handleAddParams={handleAddParams}
                handleDelParams={handleDelParams}
              />
              {/*  */}
              <Script
                selector={selectorType}
                scriptRef={scriptRef}
                paramsKeys={paramsKeys}
                paramsSamples={paramsSamples}
                setParamsSamples={setParamsSamples}
              />
              {/* Text area */}
              <MockRequestBody 
                  resetKey={resetKey}
                  formId={formId} 
                  onMockRequestChange={handleMockRequestChange} 
              />
              {/*  */}
              <MockResponse
                resetKey={resetKey}              
                formId={formId}
                onMockResponseChange={handleMockResponseChange}
              />
              {/*  */}
              <RespHeaderParams respParams={respParams} setRespParams={setRespParams} handleAddParams={handleAddParams} handleDelParams={handleDelParams} />
              {/*  */}
              <ExcludeList formId={formId} excludeListRef={excludeListRef} />
              {/*  */}
              <FormButtons handleResetForm={handleResetForm} setShowForm={setShowForm} showForm={showForm} />
            </Stack>
          </Form>
          {flashMessage && (
            <Alert variant="success" className="fade-out">
              {flashMessage}
            </Alert>
          )}
          {flashErrorMessage && (
            <Alert variant='warning' className="fade-out">
              {flashErrorMessage}
            </Alert>
          )}
        </div>
      </Collapse>
    </div>
  );
};

export default PutForm;
