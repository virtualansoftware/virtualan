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

const AdditionalParams = ({reqParams, setReqParams, handleAddParams, handleDelParams, }: Props) => {

	const randomIdAdditionalParams = uuidv4();

  return (
    <div>
      <Row key={uuidv4()}>
        <Col xs={3}>
          <Form.Label className="head-text-black">
            Additional Params:
          </Form.Label>
        </Col>
        <Col xs={7}>
        <Row className="d-flex">
          <Col xs={6}>
            <Form.Label
              className="head-text-black"
              htmlFor="inputReqParamKey"
            >
              Param Key
            </Form.Label>
          </Col>
          <Col xs={6}>
            <Form.Label
              className="head-text-black"
              htmlFor="inputReqParamValue"
            >
              Param Value:
            </Form.Label>
          </Col>
        </Row>

          {/* line */}
          <Row className="d-flex" style={{ padding: 0 }}>
            <Col>
              <hr className="hrx" />
            </Col>
          </Row>
      
          {reqParams.map((param: any, index: string) => {
            const key = param["key"];
            return (
              <Row className="d-flex" key={index}>
                <Col>
                  <Form.Control
                    value={param["key"]}
                    key={index}
                    type="text"
                    id={"inputReqParamValue" + index}
                    className="form_readonly"
                    readOnly
                  />
                </Col>
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
              <Form.Control type="text" id={"inputReqAddParamsKey-" + randomIdAdditionalParams} />
            </Col>
            <Col>
              <Form.Control type="text" id={"inputReqAddParamsValue-" + randomIdAdditionalParams} />
            </Col>
            <Col xs="1">
              <Image
                src={plusImage}
                width="30"
                height="30"
                roundedCircle
                onClick={() =>
                  handleAddParams(
                    "inputReqAddParamsKey-" + randomIdAdditionalParams,
                    "inputReqAddParamsValue-" + randomIdAdditionalParams,
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

export default AdditionalParams;
