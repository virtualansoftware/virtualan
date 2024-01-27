import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import CodeEditor from "@uiw/react-textarea-code-editor";

interface Props {
  selector: any;
  scriptRef: any;
}

const Script = ({
  selector,
  scriptRef,
}: Props) => {

  if (selector == "Rule") {
    return (
      <Row key={uuidv4()}>
        <Col xs={3}>
          <Form.Label className="head-text-black" htmlFor="scriptBody">
            Rule(SPEL):
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
                backgroundColor: "#f5f5f5",
                fontFamily:
                  "ui-monospace,SFMono-Regular,SF Mono,Consolas,Liberation Mono,Menlo,monospace",
                fontSize: 12,
              }}
            />
          </div>
        </Col>
      </Row>
    );
  }

  if (selector == "Script") {
    return (
      <Row key={uuidv4()}>
        <Col xs={3}>
          <Form.Label className="head-text-black" htmlFor="scriptBody">
            Groovy:
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
                backgroundColor: "#f5f5f5",
                fontFamily:
                  "ui-monospace,SFMono-Regular,SF Mono,Consolas,Liberation Mono,Menlo,monospace",
                fontSize: 12,
              }}
            />
          </div>
        </Col>
      </Row>
    );
  }
};

export default Script;
