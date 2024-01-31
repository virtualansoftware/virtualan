import { useState } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  formId: string;
  onMockRequestChange: (value: string) => void;
}

const MockRequestBody = ({ formId, onMockRequestChange }: Props) => {
  const [mockRequest, setMockRequest] = useState("");

  const handleMockRequestChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setMockRequest(e.target.value);
    onMockRequestChange(e.target.value);
  };

  return (
    <Row key={"mockRequest"}>
      <Col xs={3}>
        <Form.Label className="head-text-black" htmlFor="mockRequest">
          Mock Request:
        </Form.Label>
      </Col>
      <Col xs={7}>
        <Form.Control
          id={"mockRequest" + formId}
          as="textarea"
          rows={6}
          value={mockRequest}
          onChange={handleMockRequestChange}
        />
      </Col>
    </Row>
  );
};

export default MockRequestBody;
