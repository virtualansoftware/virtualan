import { useState, useEffect } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import CodeEditor from "@uiw/react-textarea-code-editor";

interface Props {
  selector: any;
  resetKey: any;
  onScriptChange: (value: string) => void;

}

const Script = ({
  selector,
  onScriptChange,
  resetKey,
}: Props) => {

  useEffect(() => {
    setScript("");
  }, [resetKey]);

  const [script, setScript] = useState("");

  const handleScriptChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setScript(e.target.value);
    onScriptChange(e.target.value);
  };
  

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
              language="groovy"
              value={script}
              onChange={handleScriptChange}
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
              value={script}
              onChange={handleScriptChange}
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
