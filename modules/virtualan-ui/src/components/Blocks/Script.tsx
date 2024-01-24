import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import CodeEditor from "@uiw/react-textarea-code-editor";
import AdditionalParams from "../Blocks/AdditionalParams";
import ParameterizedParams from "../Blocks/ParameterizedParams";
import { useState } from "react";

interface Props {
  selector: any;
  scriptRef: any;
  paramsKeys: string[];
  setParamsKeys: any;
  paramsSamples: string[];
  setParamsSamples: any;
}

const Script = ({
  paramsKeys,
  setParamsKeys,
  paramsSamples,
  setParamsSamples,
  selector,
  scriptRef,
}: Props) => {
  // selector in ['Response', 'Params', 'Rule', 'Script']
  // const [reqParams, setReqParams] = useState([]);
  // const [paramsKeys, setParamsKeys] = useState(["id", "var1", "var2", "var3"]);
  // const [paramsSamples, setParamsSamples] = useState([]);
  // console.log("selector: ", selector);

  // const handleDelParams = (key: string, params: any, setParams: any) => {
  //   setParams(params.filter((item: any) => item.key !== key));
  // };

  // const handleAddParams = (
  //   keyInputId: string,
  //   valueInputId: string,
  //   paramsArray: any,
  //   setParamsArray: any
  // ) => {
  //   const keyInput = document.getElementById(keyInputId) as HTMLInputElement;
  //   const valueInput = document.getElementById(
  //     valueInputId
  //   ) as HTMLInputElement;
  //   const key = keyInput.value.trim();
  //   const value = valueInput.value.trim();

  //   // // test with valid invalid valid characters
  //   // const validPattern = /^[a-zA-Z0-9]+$/;
  //   // if (key.match(validPattern) && value.match(validPattern))

  //   if (key !== "" && value !== "") {
  //     const index = paramsArray.findIndex((item: any) => item.key === key);
  //     if (index !== -1) {
  //       const updatedParams = [...paramsArray];
  //       updatedParams[index].value = value;
  //       setParamsArray(updatedParams);
  //     } else {
  //       setParamsArray([...paramsArray, { key, value }]);
  //     }
  //     keyInput.value = "";
  //     valueInput.value = "";
  //     keyInput.focus();
  //   }
  // };

  if (selector == "Response") {
    return null;
  }

  if (selector == "Params") {
    // let paramsKeys = ["id", "var1", "var2", "var3"];
    // let paramsSamples = [
    //   { id: "1", var1: "test1-var1", var2: "test1-var2", var3: "test1-var3" },
    //   { id: "2", var1: "test2-var1", var2: "test2-var2" },
    //   { id: "3", var2: "test3-var2", var3: "test3-var3" },
    //   { id: "4", var1: "test4-var1", var3: "test4-var3" },
    // ];
    return (
      <ParameterizedParams
        // reqParams={reqParams}
        // setReqParams={setReqParams}
        // handleAddParams={handleAddParams}
        // handleDelParams={handleDelParams}
        paramsValues={paramsKeys}
        setParamsValues={setParamsKeys}
        data={paramsSamples}
        setData={setParamsSamples}
      />
    );
  }

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
