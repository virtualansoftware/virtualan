import React, { useState, useRef } from "react";

import Form from "react-bootstrap/Form";
import Stack from "react-bootstrap/Stack";
import Alert from "react-bootstrap/Alert";
import Collapse from "react-bootstrap/Collapse";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";

import HttpStatusList from "../../api/HttpStatusList.json";
import { apiRequestsPost } from "../../api/apiRequests";
import Selects from "../Blocks/Selects";
import HeaderParams from "../Blocks/HeaderParams";
import AdditionalParams from "../Blocks/AdditionalParams";
import MockResponse from "../Blocks/MockResponse";
import RespHeaderParams from "../Blocks/RespHeaderParams";
import ExcludeList from "../Blocks/ExcludeList";
import FormButtons from "../Blocks/FormButtons";
import { v4 as uuidv4 } from "uuid";

interface Props {
  path: string;
  availableParams: string[];
  apiEntryPointPost: string;
}

const DeleteForm = ({ path, availableParams, apiEntryPointPost }: Props) => {
  const [showForm, setShowForm] = useState(false);
  const [queryParams, setQueryParams] = useState<{ [key: string]: string }>({});
  const [reqParams, setReqParams] = useState([]);
  const [respParams, setRespParams] = useState([]);
  const [flashMessage, setFlashMessage] = useState("");

  const mockResponseRef = useRef(null);
  const excludeListRef = useRef(null);
  const selectRefs = {
    status: useRef(null),
    type: useRef(null),
    isJson: useRef(null),
  };

  const contentStyle = {
    height: "auto",
    border: "none",
    margin: "0px",
    padding: "0px",
  };
  const formId = uuidv4();

  const http_status = HttpStatusList;

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    // console.log("selectRefs", selectRefs);
    // console.log('queryParams', queryParams); // ok
    // console.log('reqParams', reqParams); // ok
    // console.log('respParams', respParams); // ok
    // console.log('mockResponse', mockResponseRef.current.value);
    // console.log('excludeList', excludeListRef.current.value);

    const dataToSubmit = {
      //   "id": 22,
      //   "operationId": "personsGet",
      httpStatusCode: selectRefs.status.current.value,
      url: path,
      type: selectRefs.type.current.value,
      //   "requestType": "REST",
      //   "usageCount": 0,
      //   "priority": 0,
      method: "GET",
      output: mockResponseRef.current.value,
      availableParams: [
        Object.entries(queryParams).map(([key, value]) => ({ key, value })),
      ],
      //   "headerParams": [],
      //   "resource": "persons",
    };

    // console.log("dataToSubmit", dataToSubmit);
    try {
      apiRequestsPost(apiEntryPointPost, dataToSubmit);
      setFlashMessage("Data added successfully.");
    } catch (error) {
      console.error("Error making POST request:", error);
      setFlashMessage("Error making POST request.");
    }
    setTimeout(() => {
      setFlashMessage("");
    }, 5000);
    handleResetForm();
  };

  const handleResetForm = () => {
    const mockResponseField = document.getElementById(
      "mockResponse" + formId
    ) as HTMLInputElement;
    const excludeListField = document.getElementById(
      "excludeList" + formId
    ) as HTMLInputElement;
    mockResponseField.value = "";
    excludeListField.value = "";
    setReqParams([]);
    setRespParams([]);
    setQueryParams({});
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
          {flashMessage && (
            <Alert variant="success" className="fade-out">
              {flashMessage}
            </Alert>
          )}
          <Form onSubmit={handleSubmit}>
            <Stack gap={3}>
              {/*  */}
              <Selects selectRefs={selectRefs} http_status={http_status} />
              {/*  */}
              <HeaderParams
                availableParams={availableParams}
                queryParams={queryParams}
                handleAddQueryParams={handleAddQueryParams}
              />
              {/*  */}
              <AdditionalParams
                reqParams={reqParams}
                setReqParams={setReqParams}
                handleAddParams={handleAddParams}
                handleDelParams={handleDelParams}
              />
              {/*  */}
              <MockResponse formId={formId} mockResponseRef={mockResponseRef} />
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
        </div>
      </Collapse>
    </div>
  );
};

export default DeleteForm;
