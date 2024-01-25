import { useRef, useState } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import Image from "react-bootstrap/Image";
import plusImage from "../../assets/images/plus-img.png";
import minusImage from "../../assets/images/minus-img.png";
import "../../assets/css/table.css";

interface Props {
  paramsValues: string[];
  data: string[];
  setData: any;
}


const ParameterizedParams = ({ paramsValues, data, setData }: Props) => {

  const [goodHighlight, setGoodHighlight] = useState(false);
  const [badHighlight, setBadHighlight] = useState(false);

  const formValuesRef: any = useRef({});


  const handleDelParams = (index: any) => {
    setData(data.filter((item: any, i: number) => i !== index));
  };

  const handleAddParams = (formValues: any) => {
    const tagExists = data.some((item: any) => item[paramsValues[0]] === formValues[paramsValues[0]]);
    if (!tagExists) {
      const newData = [...data, formValues];
      setData(newData);
      formValuesRef.current = {};
      setGoodHighlight(true);
      setTimeout(() => setGoodHighlight(false), 150);

    } else {
      setBadHighlight(true);
      setTimeout(() => setBadHighlight(false), 150);
    }
  };

  return (
    <div>
      {/* title */}
      <Row key={uuidv4()}>
        <Col xs={3}>
          <Form.Label className="head-text-black">
            Parameterized Values:
          </Form.Label>
        </Col>
      </Row>

      {/* variables */}
      <Row key={uuidv4()}>
        <Col xs={1}></Col>
        <Col xs={10} className="scrollable-table">
          {/* table */}
          <table className="full-width-table">
            <thead className="table-header">
              {/* Var names */}
              <tr key={1}>
                {paramsValues.map((param: any, index) => {
                  return (
                    <th key={index} className="table-cell">
                      {param}
                    </th>
                  );
                })}
                <th></th>
              </tr>
            </thead>
            <tbody>

              {/* Values Added */}
              {data.map((param: any, index: any) => (
                <tr key={index} className="table-row">
                  {paramsValues.map((varName) => (
                    <td key={varName} className="table-cell">
                      {param[varName] || ""}
                    </td>
                  ))}
                  <td align="right" className="table-img">
                    <Image
                      key={index}
                      src={minusImage}
                      alt="minus"
                      width="30"
                      height="30"
                      roundedCircle
                      onClick={() => handleDelParams(index)}
                    />
                  </td>
                </tr>
              ))}

              {/* Values to Add */}
              <tr key={2} className={`table-row-add ${goodHighlight ? "goodHighlight" : ""} ${badHighlight ? "badHighlight" : ""}`}>
                {paramsValues.map((param: any, index) => {
                  return (
                    <td key={param} className="table-cell">
                      <Form.Control
                        defaultValue={formValuesRef.current[param]}
                        key={param}
                        type="text"
                        onChange={(e) =>{
                          formValuesRef.current[param] = e.target.value;
                        }}
                      />
                    </td>
                  );
                })}
                <td align="right" className="table-img">
                  <Image
                    src={plusImage}
                    width="30"
                    height="30"
                    roundedCircle
                    onClick={() => handleAddParams(formValuesRef.current)}
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </Col>
        <Col xs="1"></Col>
      </Row>
    </div>
  );
};

export default ParameterizedParams;
