import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  mockRequestRef: any;
  formId: string;
}

const MockRequestBody = ({ formId, mockRequestRef }: Props) => {
  return (

    <Row key={uuidv4()}>
    <Col xs={3}>
      <Form.Label
        className="head-text-black"
        htmlFor="mockRequestBody"
      >
        Mock Request Body:
      </Form.Label>
    </Col>
    <Col xs={7}>
      <Form.Control id="mockRequestBody" as="textarea" rows={6} ref={mockRequestRef} />
    </Col>
  </Row>
  );
};

export default MockRequestBody;
