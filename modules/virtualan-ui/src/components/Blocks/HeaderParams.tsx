import React, { useRef } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  availableParams: string[];
  queryParams: any;
  handleAddQueryParams: Function;
}

const HeaderParams = ({
  availableParams,
  queryParams,
  handleAddQueryParams,
}: Props) => {
  return (
    <Row key={uuidv4()}>
      <Col xs={3}>
        <Form.Label className="head-text-black">
          Query/Path/Header Params:
        </Form.Label>
      </Col>
      <Col xs={7}>
        {availableParams.map((param: any, index: any) => (
          <Row key={index} className="d-flex">
            <Col xs={4}>
              <Form.Label
                name={param["key"]}
                id={"inputQueryParamKey" + param["key"]}
                key={param + index}
                htmlFor={"inputQueryParam" + param["key"]}
              >
                {param["key"]}
              </Form.Label>
            </Col>
            <Col xs={8}>
              <Form.Control
                key={param}
                type="text"
                id={"inputQueryParamValue" + param["key"]}
                value={queryParams[param["key"]]}
                onChange={(e) =>
                  handleAddQueryParams(param["key"], e.target.value)
                }
              />
            </Col>
          </Row>
        ))}
      </Col>
    </Row>
  );
};

export default HeaderParams;
