import React, { useState, useRef, useEffect } from "react";

import Form from "react-bootstrap/Form";
import Stack from "react-bootstrap/Stack";
import Alert from "react-bootstrap/Alert";
import Collapse from "react-bootstrap/Collapse";

import { apiRequestsPost } from "../../api/apiRequests";
import Selects from "../Blocks/Selects";
import HeaderParams from "../Blocks/HeaderParams";
import AdditionalParams from "../Blocks/AdditionalParams";
import MockResponse from "../Blocks/MockResponse";
import Script from "../Blocks/Script";
import RespHeaderParams from "../Blocks/RespHeaderParams";
import FormButtons from "../Blocks/FormButtons";
import { v4 as uuidv4 } from "uuid";


interface Props {
  operationId: string;
  resource: string;
  path: string;
  availableParams: string[];
  apiEntryPointPost: string;
}

const GetForm = ({operationId, resource, path, availableParams, apiEntryPointPost}: Props) => {
  const [showForm, setShowForm] = useState(false);
  const [queryParams, setQueryParams] = useState<{ [key: string]: string }>({});
  const [paramTypes, setParamTypes] = useState<{ [key: string]: string }>({});
  const [reqParams, setReqParams] = useState([]);
  const [respParams, setRespParams] = useState([]);
  const [flashMessage, setFlashMessage] = useState("");
  const [flashErrorMessage, setFlashErrorMessage] = useState("");
  // const [showRuleBlock, setShowRuleBlock] = useState(false);
  const [selectorType, setSelectorType] = useState("");
  const [httpStatusCode, setHttpStatusCode] = useState("");
  const [contentType, setContentType] = useState("");
  const [resetKey, setResetKey] = useState(uuidv4());

  const formId = uuidv4();
  const mockResponseRef = useRef(null);
  const scriptRef = useRef(null);

  const selectRefs = {
    status: useRef(null),
    type: useRef(null), // Response, Params, Rule, Script
    requestType: useRef(null), // JSON
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

  const createMockRequest = (apiEntryPointPost: any, dataToSubmit: any) => {
    const output = apiRequestsPost(apiEntryPointPost, dataToSubmit);
    output
      .then((response: any) => JSON.stringify(response)) //2
      .then((data: any) => {
        const jsondata = JSON.parse(data);
        setFlashMessage("Success: " + jsondata.data.mockStatus.code);
        setFlashErrorMessage("");
      })
      .catch((error) => {
        console.log(error.response);
        const jsondata = JSON.parse(JSON.stringify(error.response));
        setFlashErrorMessage("Fail: " + jsondata.data.code);
        setFlashMessage("");
      });
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const dataToSubmit = {
      operationId: operationId,
      url: path,
      httpStatusCode: httpStatusCode,
      type: selectorType,
      contentType: contentType,
      method: "GET",
      rule: scriptRef != null && scriptRef.current ? scriptRef.current.value : "",
      output: mockResponseRef != null ? mockResponseRef.current.value : "",
      availableParams: Object.entries(queryParams).map(([key, value]) => ({
        key: key,
        value: value,
        parameterType: paramTypes[key],
      })),
      headerParams: respParams,
      resource: resource,
    };

    try {
      createMockRequest(apiEntryPointPost, dataToSubmit);
    } catch (error) {
      console.error("Error making POST request:", error);
      setFlashErrorMessage("Error making POST request." + error);
    }
    setTimeout(() => {
      setFlashMessage("");
      setFlashErrorMessage("");
    }, 5000);
    handleResetForm();
  };

  const handleResetForm = () => {
    const mockResponseField = document.getElementById(
      "mockResponse" + formId
    ) as HTMLInputElement;
    mockResponseField.value = "";
    setReqParams([]);
    setRespParams([]);
    setQueryParams({});
    setParamTypes({});
    setResetKey(uuidv4());
    setFlashMessage("");
    setFlashErrorMessage("");
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

  const handleAddQueryParams = (
    paramType: string,
    key: string,
    value: string
  ) => {
    queryParams[key] = value;
    setQueryParams(queryParams);
    paramTypes[key] = paramType;
    setParamTypes(paramTypes);
  };

  return (
    <div className="button-get-box button-box">
      <div
        className="button-get-path button-path"
        onClick={() => setShowForm(!showForm)}
      >
        <span className="form-button button-get">GET</span>
        {" " + path}
      </div>

      <Collapse in={showForm}>
        <div style={contentStyle}>
          <Form onSubmit={handleSubmit}>
            <Stack gap={3}>
              <Selects selectRefs={selectRefs} onSelectionChange={handleSelectChange} resetKey={resetKey}/>
              {/*  */}
              <HeaderParams availableParams={availableParams} queryParams={queryParams} handleAddQueryParams={handleAddQueryParams} />
              {/*  */}
              <AdditionalParams reqParams={reqParams} setReqParams={setReqParams} handleAddParams={handleAddParams} handleDelParams={handleDelParams} />
              {/*  */}
              <Script formId={formId} selector={selectorType} scriptRef={scriptRef} />  {/* WIP */}
              {/*  */}
              <MockResponse formId={formId} mockResponseRef={mockResponseRef} />
              {/*  */}
              <RespHeaderParams respParams={respParams} setRespParams={setRespParams} handleAddParams={handleAddParams} handleDelParams={handleDelParams} />
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
            <Alert variant="warning" className="fade-out">
              {flashErrorMessage}
            </Alert>
          )}
        </div>
      </Collapse>
    </div>
  );
};

export default GetForm;
