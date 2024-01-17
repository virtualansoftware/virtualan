import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import Image from "react-bootstrap/Image";
import plusImage from "../../assets/images/plus-img.png";
import minusImage from "../../assets/images/minus-img.png";

interface Props {
  respParams: any;
  setRespParams: any;
  handleAddParams: Function;
  handleDelParams: Function;
}

const RespHeaderParams = ({
  respParams,
  setRespParams,
  handleAddParams,
  handleDelParams,
}: Props) => {
  const randomIdAdditionalParams = uuidv4();

  return (
    <div>
      <Row key={uuidv4()} >
        <Col xs={3}>
          <Form.Label className="head-text-black">
            Response Header Params:
          </Form.Label>
        </Col>
      </Row>

      <Row key={uuidv4()} style={{ padding: 0 }}>
        <Col xs="3"></Col>
        <Col xs={7}>
          <Row className="d-flex">
            <Col xs={6}>
              <Form.Label
                className="head-text-black"
                htmlFor="inputRespParamValue"
              >
                Param Value:
              </Form.Label>
            </Col>
            <Col xs={6}>
              <Form.Label
                className="head-text-black"
                htmlFor="inputRespParamKey"
              >
                Param Key
              </Form.Label>
            </Col>
          </Row>
        </Col>
      </Row>

      <Row key={uuidv4()} style={{ padding: 0 }}>
        <Col xs={3}></Col>
        <Col xs={7}>
          <Row className="d-flex">
            <Col>
              <hr />
            </Col>
          </Row>
        </Col>
      </Row>

      <Row key={uuidv4()} style={{ padding: 0 }}>
        <Col xs={3}></Col>
        <Col xs={7}>
          {respParams.map((param: any, index: any) => {
            const key = param["key"];
            return (
              <Row className="d-flex" key={index}>
                <Col>
                  <Form.Control
                    value={param["key"]}
                    key={index}
                    type="text"
                    id={"inputRespParamValue" + index}
                    className="form_readonly"
                    readOnly
                  />
                </Col>
                <Col>
                  <Form.Control
                    value={param["value"]}
                    key={index}
                    type="text"
                    id={"inputRespParamKey" + index}
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
                      handleDelParams(key, respParams, setRespParams)
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
                id={"inputRespAddParamsKey-" + randomIdAdditionalParams}
              />
            </Col>
            <Col>
              <Form.Control
                type="text"
                id={"inputRespAddParamsValue-" + randomIdAdditionalParams}
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
                    "inputRespAddParamsKey-" + randomIdAdditionalParams,
                    "inputRespAddParamsValue-" + randomIdAdditionalParams,
                    respParams,
                    setRespParams
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

export default RespHeaderParams;
