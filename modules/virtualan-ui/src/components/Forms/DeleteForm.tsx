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
import RespHeaderParams from "../Blocks/RespHeaderParams";
import Script from "../Blocks/Script";
import FormButtons from "../Blocks/FormButtons";
import { v4 as uuidv4 } from "uuid";

interface Props {
  operationId: string;
  resource: string;
  path: string;
  availableParams: string[];
  apiEntryPointPost: string;
}

const DeleteForm = ({
  operationId,
  resource,
  path,
  availableParams,
  apiEntryPointPost,
}: Props) => {
  const [showForm, setShowForm] = useState(false);
  const [queryParams, setQueryParams] = useState<{ [key: string]: string }>({});
  const [paramTypes, setParamTypes] = useState<{ [key: string]: string }>({});
  const [reqParams, setReqParams] = useState([]);
  const [respParams, setRespParams] = useState([]);
  const [flashMessage, setFlashMessage] = useState("");
  const [flashErrorMessage, setFlashErrorMessage] = useState("");
  const [selectorType, setSelectorType] = useState("");
  const [httpStatusCode, setHttpStatusCode] = useState("");
  const [contentType, setContentType] = useState("");
  const [resetKey, setResetKey] = useState(uuidv4());

  const [paramsKeys, setParamsKeys] = useState([]);
  const [paramsSamples, setParamsSamples] = useState([]);

  const formId = uuidv4();
  const [mockResponse, setMockResponse] = useState("");
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

    let mockResponseMatches: any = mockResponse.match(regex);
    if (mockResponseMatches) {
      mockResponseMatches = mockResponseMatches.map((match: string) =>
        match.slice(1, -1)
      );
    }

    let mockRequestMatches: any = mockRequest.match(regex);
    if (mockRequestMatches) {
      mockRequestMatches = mockRequestMatches.map((match: string) =>
        match.slice(1, -1)
      );
    }

    setParamsKeys([
      ...new Set([
        ...(queryParamsMatches || []),
        ...(mockResponseMatches || []),
        ...(mockRequestMatches || []),
      ]),
    ]);
  }, [queryParams, mockResponse, mockRequest, selectorType]);

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
      method: "PATCH",
      rule:
        scriptRef != null && scriptRef.current ? scriptRef.current.value : "",
      input: mockRequest,
      output: mockResponse,
      availableParams: Object.entries(queryParams).map(([key, value]) => ({
        key: key,
        value: value,
        parameterType: paramTypes[key],
      })),
      headerParams: respParams,
      resource: resource,
      excludeList: excludeListRef.current.value,
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
    const mockRequestField = document.getElementById(
      "mockRequest" + formId
    ) as HTMLInputElement;
    const mockResponseField = document.getElementById(
      "mockResponse" + formId
    ) as HTMLInputElement;
    const excludeListField = document.getElementById(
      "excludeList" + formId
    ) as HTMLInputElement;
    mockRequestField.value = "";
    mockResponseField.value = "";
    excludeListField.value = "";
    setReqParams([]);
    setRespParams([]);
    setQueryParams({});
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

  const handleMockResponseChange = (value: string) => {
    setMockResponse(value);
  };

  return (
    <div className="button-delete-box button-box">
      <div
        className="button-delete-path button-path"
        onClick={() => setShowForm(!showForm)}
      >
        <span className="form-button button-delete">DELETE</span>
        <span className="button-path">{" " + path}</span>
      </div>

      <Collapse in={showForm}>
        <div style={contentStyle}>
          <Form onSubmit={handleSubmit}>
            <Stack gap={3}>
              {/*  */}
              <Selects
                selectRefs={selectRefs}
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
              {/*  */}
              <MockResponse
                formId={formId}
                onMockResponseChange={handleMockResponseChange}
              />
              {/*  */}
              <RespHeaderParams
                respParams={respParams}
                setRespParams={setRespParams}
                handleAddParams={handleAddParams}
                handleDelParams={handleDelParams}
              />
              {/*  */}
              <FormButtons
                handleResetForm={handleResetForm}
                setShowForm={setShowForm}
                showForm={showForm}
              />
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

export default DeleteForm;
