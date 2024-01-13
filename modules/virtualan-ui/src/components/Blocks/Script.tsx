import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  scriptRef: any;
  formId: string;
}

const Script = ({ formId, scriptRef }: Props) => {
  return (

    <Row key={uuidv4()}>
    <Col xs={3}>
      <Form.Label
        className="head-text-black"
        htmlFor="scriptBody"
      >
       Rule(SPEL)/Groovy:
      </Form.Label>
    </Col>
    <Col xs={7}>
      <Form.Control id="script" as="textarea" rows={6} ref={scriptRef} />
    </Col>
  </Row>
  );
};

export default Script;
