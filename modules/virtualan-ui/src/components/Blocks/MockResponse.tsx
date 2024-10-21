import { useState, useEffect } from "react";
import { Row, Col, Form } from "react-bootstrap";

interface Props {
  resetKey: string;
  formId: string;
  onMockResponseChange: (value: string) => void;
}

const MockResponse = ({ resetKey, formId, onMockResponseChange }: Props) => {
  const [mockResponse, setMockResponse] = useState("");

  const handleMockResponseChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setMockResponse(e.target.value);
    onMockResponseChange(e.target.value); 
  };

  useEffect(() => {
    setMockResponse("");
  }, [resetKey]);

  return (
    <Row key={"mockResponse"}>
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
          value={mockResponse}
          onChange={handleMockResponseChange}
        />
      </Col>
    </Row>
  );
};

export default MockResponse;
