import React, { useRef } from "react";
import { Row, Col, Form } from "react-bootstrap";

interface Props {
  availableParams: string[];
  queryParams: any;
  setQueryParams: Function;
}

const HeaderParams = ({
  availableParams,
  queryParams,
  setQueryParams,
}: Props) => {
  return (
    <div>
      <Row key={"HeaderParams"} style={{ padding: 0 }}>
        <Col xs={3}>
          <Form.Label className="head-text-black">
            Query/Path/Header Params:
          </Form.Label>
        </Col>
        <Col xs={7}>
          {availableParams.map((param: any, index: any) => (
            <Row key={param["key"]} className="d-flex" style={{ padding: 0 }}>
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
                  key={param["key"]}
                  type="text"
                  id={"inputQueryParamValue" + param["key"]}
                  value={queryParams[param["key"]] || ""}
                  onChange={(e) =>
                    setQueryParams((prevState: any) => ({
                      ...prevState,
                      [param["key"]]: e.target.value,
                    }))
                  }
                />
              </Col>
            </Row>
          ))}
        </Col>
      </Row>
    </div>
  );
};

export default HeaderParams;
