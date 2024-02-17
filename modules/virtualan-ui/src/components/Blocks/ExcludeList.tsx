import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  excludeListRef: any;
  formId: string;
}

const ExcludeList = ({ formId, excludeListRef }: Props) => {
  return (
    <>
      <Row key={uuidv4()}>
        <Col xs={3}>
          <Form.Label className="head-text-black" htmlFor="excludeList">
            Exclude List:
          </Form.Label>
        </Col>
        <Col xs={7}>
          <Row className="d-flex">
            <Col>
              <Form.Control
                type="text"
                id={"excludeList" + formId}
                ref={excludeListRef}
              />
            </Col>
          </Row>
        </Col>
      </Row>

      <Row key={uuidv4()}>
        <Col xs={3}></Col>
        <Col xs={7}>
          <Form.Text>
            **Exclude parameters in comma separated value which would not
            required to be considered. Example: createDate,receiveDate**
          </Form.Text>
        </Col>
      </Row>
    </>
  );
};

export default ExcludeList;
