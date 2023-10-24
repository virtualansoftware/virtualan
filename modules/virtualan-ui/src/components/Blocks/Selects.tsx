import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";

interface Props {
  selectRefs: any;
  http_status: any;
}

const Selects = ({ selectRefs, http_status }: Props) => {
  return (
    <Row key={uuidv4()}>
      <Col xs={3}>
        <Form.Label className="head-text-black">
          Select HTTP Status/ Type:
        </Form.Label>
      </Col>
      <Col xs={7} className="d-flex justify-content-between">
        <Form.Select
          aria-label="Default select example"
          ref={selectRefs.status}
          required
        >
          <option value="?"></option>
          {Object.entries(http_status).map(([key, value]) => (
            <option key={key} value={key}>
              {(value as string)}
            </option>
          ))}
        </Form.Select>
        <Form.Select
          aria-label="Default select example"
          ref={selectRefs.type}
          required
        >
          {Object.entries({
            "?": "",
            Response: "Response",
            Params: "Params",
          }).map(([key, value]) => (
            <option key={key} value={key}>
              {(value as string)}
            </option>
          ))}
        </Form.Select>
        <Form.Select
          aria-label="Default select example"
          ref={selectRefs.isJson}
          required
        >
          {Object.entries({
            "? undefined:undefined ?": "",
            JSON: "JSON",
          }).map(([key, value]) => (
            <option key={key} value={key}>
              {(value as string)}
            </option>
          ))}
        </Form.Select>
      </Col>
    </Row>
  );
};

export default Selects;
