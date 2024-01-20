import React, { useRef } from "react";
import { Row, Col, Form } from "react-bootstrap";
import HttpStatusList from "../../api/HttpStatusList.json";
import { v4 as uuidv4 } from "uuid";
import Image from "react-bootstrap/Image";
import plusImage from "../../assets/images/plus-img.png";
import minusImage from "../../assets/images/minus-img.png";

interface Props {
  reqParams: any;
  setReqParams: any;
  handleAddParams: Function;
  handleDelParams: Function;
}

const ParameterizedParams = ({reqParams, setReqParams, handleAddParams, handleDelParams, }: Props) => {
  const randomIdParameterizedParams = uuidv4();

  return (
    <div>
      <Row key={uuidv4()}>
        <Col xs={3}>
          <Form.Label className="head-text-black">
            Parameterized Values:
          </Form.Label>
        </Col>
        <Col xs={7}>
          <Row className="d-flex">
          </Row>
          {/* line */}
          <Row className="d-flex" style={{ padding: 0 }}>
          </Row>

          {reqParams.map((param: any, index: string) => {
            const key = param["key"];
            return (
              <Row className="d-flex" key={index}>
                <Col>
                  <Form.Control
                    value={param["value"]}
                    key={index}
                    type="text"
                    id={"inputReqParamKey" + index}
                    className="form_readonly"
                    readOnly
                  />
                </Col>
                <Col xs="1">
                  <Image
                    src={minusImage}
                    key={index}
                    width="30"
                    height="30"
                    roundedCircle
                    onClick={() =>
                      handleDelParams(key, reqParams, setReqParams)
                    }
                  />
                </Col>
              </Row>
            );
          })}
          <Row className="d-flex" style={{ padding: 0 }}>
            <Col>
              <Form.Control
                type="text"
                id={"inputReqAddParamsValue-" + randomIdParameterizedParams}
              />
            </Col>
            <Col xs="1">
              <Image
                src={plusImage}
                width="30"
                height="30"
                roundedCircle
                onClick={() =>
                  handleAddParams(
                    "inputReqAddParamsKey-" + randomIdParameterizedParams,
                    "inputReqAddParamsValue-" + randomIdParameterizedParams,
                    reqParams,
                    setReqParams
                  )
                }
              />
            </Col>
          </Row>
        </Col>
      </Row>
    </div>
  );
};

export default ParameterizedParams;
