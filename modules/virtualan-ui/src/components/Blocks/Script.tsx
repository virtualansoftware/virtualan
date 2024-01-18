import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import CodeEditor from "@uiw/react-textarea-code-editor";


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
    <div data-color-mode="light">
     <CodeEditor
        ref={scriptRef}
        language="groovy"
        placeholder="Please enter groovy/spel code."
        padding={15}
        minHeight={180}
        style={{
          fontFamily:
            "ui-monospace,SFMono-Regular,SF Mono,Consolas,Liberation Mono,Menlo,monospace",
          fontSize: 12
        }}
      />
      </div>
    </Col>
  </Row>
  );
};

export default Script;
