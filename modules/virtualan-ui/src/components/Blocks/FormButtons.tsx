import React from "react";
import { Row, Col, Form } from "react-bootstrap";
import { v4 as uuidv4 } from "uuid";
import Button from "react-bootstrap/Button";

interface Props {
  handleResetForm: React.MouseEventHandler<HTMLButtonElement>;
  setShowForm: any;
  showForm: any;
}

const FormButtons = ({ handleResetForm, setShowForm, showForm }: Props) => {
  return (
    <Row key={uuidv4()}>
      <Col xs={3}></Col>
      <Col xs={7}>
        <Button variant="primary" type="submit">
          Add
        </Button>
        <Button variant="primary" type="button" onClick={handleResetForm}>
          Reset Form
        </Button>
        <Button
          className="btn btn-primary btn-sm"
          type="submit"
          onClick={() => setShowForm(!showForm)}
        >
          Close
        </Button>
      </Col>
    </Row>
  );
};

export default FormButtons;
