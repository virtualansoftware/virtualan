import React, { useEffect, useState } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import HttpStatusList from "../../api/HttpStatusList.json";
import RequestType from "../../api/RequestType.json";
import ResponseList from "../../api/ResponseList.json";
import { handle } from "hast-util-to-html/lib/handle";

interface Props {
  selectRefs: any;
  onSelectionChange: (data: any) => void;
  resetKey: string;
}

const Selects = ({ selectRefs, onSelectionChange, resetKey }: Props) => {
  const [selectorType, setSelectorType] = useState("");
  const [status, setStatus] = useState("");
  const [content, setContent] = useState("");

  useEffect(() => {
    setSelectorType("");
    setStatus("");
    setContent("");
  }, [resetKey]);

  // from json files
  const http_status = HttpStatusList;
  const request_type = RequestType;
  const response_list = ResponseList;

  useEffect(() => {
    const dataSelected = {httpStatusCode: status, type: selectorType, contentType: content}
    onSelectionChange(dataSelected);
  }, [selectorType, status, content]);

  const handleSelectChange = (e: any, setState: (value: string) => void) => {
    setState(e.target.value);
  };

  return (
    <Row key={uuidv4()}>
      <Col xs={3}>
        <Form.Label className="head-text-black">
          Select HTTP Status/ Type:
        </Form.Label>
      </Col>
      <Col xs={7} className="d-flex justify-content-between">
        <Form.Select
          value={selectorType}
          aria-label="Default select example"
          ref={selectRefs.type}
          required
          onChange={(e) => handleSelectChange(e, setSelectorType)}
        >
          <option value="?"></option>
          {Object.entries(response_list).map(([key, value]) => (
            <option key={key} value={key}>
              {value as string}
            </option>
          ))}
        </Form.Select>

        <Form.Select
          value={status}
          aria-label="Default select example"
          ref={selectRefs.status}
          required
          onChange={(e) => handleSelectChange(e, setStatus)}
        >
          <option value="?"></option>
          {Object.entries(http_status).map(([key, value]) => (
            <option key={key} value={key}>
              {value as string}
            </option>
          ))}
        </Form.Select>

        <Form.Select
          value={content}
          aria-label="Default select example"
          ref={selectRefs.requestType}
          required
          onChange={(e) => handleSelectChange(e, setContent)}
        >
          <option value="?"></option>
          {Object.entries(request_type).map(([key, value]) => (
            <option key={key} value={key}>
              {value as string}
            </option>
          ))}
        </Form.Select>
      </Col>
    </Row>
  );
};

export default Selects;
