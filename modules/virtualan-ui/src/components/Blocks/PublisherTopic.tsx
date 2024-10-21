import { useState, useEffect } from "react";
import { Row, Col, Form } from "react-bootstrap";

interface Props {
  formId: string;
  resetKey: any;
  onPublisherTopicChange: (value: string) => void;
}

const PublisherTopic = ({ resetKey, onPublisherTopicChange }: Props) => {
  const [publisherTopic, setPublisherTopic] = useState("");

  const handlePublisherTopicChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setPublisherTopic(e.target.value);
    onPublisherTopicChange(e.target.value);
  };
  
  useEffect(() => {
    setPublisherTopic("");
  }, [resetKey]);


  return (
    <Row key={"publisherTopic"}>
      <Col xs={3}>
        <Form.Label className="head-text-black" htmlFor="publisherTopic">
          Publisher-Topic:
        </Form.Label>
      </Col>
      <Col xs={7}>
        <Form.Control
          id={"publisherTopic"}
          type="text"
          value={publisherTopic}
          onChange={handlePublisherTopicChange}
        />
      </Col>
    </Row>
  );
};

export default PublisherTopic;
