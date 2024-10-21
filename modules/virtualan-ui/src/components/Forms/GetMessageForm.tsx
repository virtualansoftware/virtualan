import React, { useState, useRef, useEffect } from "react";
import Form from "react-bootstrap/Form";
import Stack from "react-bootstrap/Stack";
import Alert from "react-bootstrap/Alert";
import Collapse from "react-bootstrap/Collapse";
import ParameterizedParams from "../Blocks/ParameterizedParams";
import { apiRequestsPost } from "../../api/apiRequests";
import MockRequestBody from "../Blocks/MockRequestBody";
import MockResponse from "../Blocks/MockResponse";
import Script from "../Blocks/Script";
import RespHeaderParams from "../Blocks/RespHeaderParams";
import FormButtons from "../Blocks/FormButtons";
import { v4 as uuidv4 } from "uuid";
import PublisherTopic from "../Blocks/PublisherTopic";
import MessageHeaderParams from "../Blocks/MessageHeaderParams";
import ExcludeList from "../Blocks/ExcludeList";

interface Props {
  topics: string;
  broker: string;
  apiEntryPointPost: string;
}

const GetMessageForm = ({ topics, broker, apiEntryPointPost }: Props) => {
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
  const [script, setScript] = useState(null);
  const [paramsKeys, setParamsKeys] = useState([]);
  const [paramsData, setParamsData] = useState([]);
  const formId = uuidv4();
  const [mockRequest, setMockRequest] = useState("");
  const [mockResponse, setMockResponse] = useState("");
  const [publisherTopic, setPublisherTopic] = useState("");

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

    setParamsKeys([
      ...new Set([
        ...(queryParamsMatches || []),
        ...(mockResponseMatches || []),
      ]),
    ]);
  }, [queryParams, mockResponse, selectorType]);

  const contentStyle = {
    height: "auto",
    border: "none",
    margin: "0px",
    padding: "0px",
  };

  const handleSelectChange = (data: any) => {
    console.log(data.type, data.status, data.contentType)
    setSelectorType(data.type);
    setHttpStatusCode(data.status);
    setContentType(data.contentType);
  };

  const createMockRequest = (apiEntryPointPost: any, dataToSubmit : any) => {
    const output = apiRequestsPost(apiEntryPointPost, dataToSubmit);
    output.then((response : any) => JSON.stringify(response))
    .then((data : any) => {
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
    reqParams.map((item) => {
      queryParams[item.key] = item.value;
    });
    const dataToSubmit = {
      resource: topics,
      brokerUrl: broker,
      requestTopicOrQueueName: topics,
      rule: script != null  ? script : (paramsData != null && paramsData.length > 0)? JSON.stringify(paramsData) : undefined,
      input: mockRequest,
      output: mockResponse,
      responseTopicOrQueueName: publisherTopic,
      availableParams: reqParams,
      headerParams: respParams,
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
    //handleResetForm();
  };

  const handleResetForm = () => {
    setParamsData([]);
    setMockResponse("");
    setReqParams([]);
    setRespParams([]);
    setQueryParams({});
    setParamTypes({});
    setResetKey(uuidv4());
    setFlashMessage("");
    setFlashErrorMessage("");
    setSelectorType("");
    setHttpStatusCode("");
    setContentType("");
    setScript("");  
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

  const handleScriptChange = (value: string) => {
    setScript(value);
  };

  const handleMockRequestChange = (value: string) => {
    setMockRequest(value);
  };

  const handleMockResponseChange = (value: string) => {
    setMockResponse(value);
  };

  const handlePublisherTopicChange = (value: string) => {
    setPublisherTopic(value);
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
        <span className="form-button button-get">{topics}</span>
      </div>

      <Collapse in={showForm}>
        <div style={contentStyle}>
          <Form onSubmit={handleSubmit}>
            <Stack gap={3}>
              <PublisherTopic
                formId={formId}
                onPublisherTopicChange={handlePublisherTopicChange}
                resetKey={resetKey}
              />
              {/*  */}
              <MessageHeaderParams
                reqParams={reqParams}
                setReqParams={setReqParams}
                handleAddParams={handleAddParams}
                handleDelParams={handleDelParams}
              />
              {/*  */}
              <ParameterizedParams
                selector={selectorType}
                paramsValues={paramsKeys}
                data={paramsData}
                setData={setParamsData}
              />
              {/*  */}
              <Script
                selector={selectorType}
                onScriptChange={handleScriptChange}
                resetKey={resetKey}
              />
              {/*  */}
              <MockRequestBody
                formId={formId} 
                onMockRequestChange={handleMockRequestChange} 
                resetKey={resetKey}
              />
              {/*  */}
              <MockResponse
                formId={formId}
                resetKey={resetKey}
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
              <ExcludeList formId={formId} excludeListRef={excludeListRef} />
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

export default GetMessageForm;