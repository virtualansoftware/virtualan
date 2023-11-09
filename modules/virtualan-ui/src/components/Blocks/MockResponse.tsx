import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  mockResponseRef: any;
  formId: string;
}

const MockResponse = ({ formId, mockResponseRef }: Props) => {
  return (
    <Row key={uuidv4()}>
      <Col xs={3}>
        <Form.Label className="head-text-black" htmlFor="mockResponse">
          Mock Response:
        </Form.Label>
      </Col>
      <Col xs={7}>
        <Form.Control
          id={"mockResponse" + formId}
          as="textarea"
          rows={6}
          ref={mockResponseRef}
        />
      </Col>
    </Row>
  );
};

export default MockResponse;
