import React, { useEffect, useState } from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import Image from "react-bootstrap/Image";
import plusImage from "../../assets/images/plus-img.png";
import minusImage from "../../assets/images/minus-img.png";
import "../../assets/css/table.css";

interface Props {
  reqParams: any;
  setReqParams: any;
}

const ParameterizedParams = ({ reqParams, setReqParams }: Props) => {
  
  const ParamValues = ["id", "var1", "var2", "var3"];
  const dataTest = [
    { id: "1", var1: "test1-var1", var2: "test1-var2", var3: "test1-var3" },
    { id: "2", var1: "test2-var1", var2: "test2-var2" },
    { id: "3", var2: "test3-var2", var3: "test3-var3" },
    { id: "4", var1: "test4-var1", var3: "test4-var3" },
  ];

  const [data, setData] = useState([]);
  const [params, setParams] = useState([]);
  const [newValues, setNewValues]= useState(Array(ParamValues.length).fill(''));
  // const randomIdParameterizedParams = uuidv4();


  const handleDelParams = (index: any) => {
    setData(data.filter((item: any, i: number) => i !== index));
  };

  const handleAddParams = () => {
    console.log("handleAddParams: ");
  };

  useEffect(() => {
    console.log("load test data");
    setData(dataTest);
    setParams(ParamValues);
  }, []);


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
                {params.map((param: any, index) => {
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
                  {ParamValues.map((varName) => (
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
              <tr key={2} className="table-row">
                {ParamValues.map((param: any, index) => {
                  // console.log("index: ", index, params);
                  return (
                    <th key={index} className="table-cell">
                      <Form.Control
                        value={newValues[index]}
                        key={index}
                        type="text"
                        id={"inputReqParamValue" + index}
                        className="form_readonly"
                        // onChange={(e) => handleAddParams()}
                      />
                    </th>
                  );
                })}
                <td>
                  <Image
                    src={plusImage}
                    width="30"
                    height="30"
                    roundedCircle
                    onClick={() => handleAddParams()}
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
