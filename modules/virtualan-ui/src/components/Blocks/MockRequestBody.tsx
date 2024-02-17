import { useState, useEffect } from "react";
import { Row, Col, Form } from "react-bootstrap";

interface Props {
  formId: string;
  resetKey: any;
  onMockRequestChange: (value: string) => void;
}

const MockRequest = ({ resetKey, formId, onMockRequestChange }: Props) => {
  const [mockRequest, setMockRequest] = useState("");

  const handleMockRequestChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setMockRequest(e.target.value);
    onMockRequestChange(e.target.value);
  };
  
  useEffect(() => {
    setMockRequest("");
  }, [resetKey]);


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

export default MockRequest;
