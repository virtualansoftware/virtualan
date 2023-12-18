import { useState } from "react";
import Modal from "react-bootstrap/Modal";
import logoVirtualan from "../assets/images/logo_image.png";
import beautify from "json-beautify";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import {
  JsonView,
  allExpanded,
  darkStyles,
  defaultStyles,
} from "react-json-view-lite";
import "react-json-view-lite/dist/index.css";

interface Props {
  title: string;
  onClose: () => void;
  show: boolean;
}

const ModalJSON = ({ title, onClose, show }: Props) => {
  const [inputJSON, setInputJSON] = useState("");
  const [formattedJSON, setFormattedJSON] = useState("");
  const [errorFormat, setErrorFormat] = useState("");

  const handleClose = () => onClose();

  const handleFormatClick = () => {
    try {
      const inputObj = JSON.parse(inputJSON);
      const formatted = beautify(inputObj, null, 2);
      setFormattedJSON(formatted);
      setInputJSON(formatted);
      setErrorFormat("");
    } catch (error) {
      setErrorFormat(error.message);
    }
  };

  const renderJsonView = (jsonString: any) => {
    try {
      const data = JSON.parse(jsonString);
      return (
        <JsonView
          data={data}
          shouldExpandNode={allExpanded}
          style={defaultStyles}
        />
      );
    } catch (error) {
      // console.error("Error parsing JSON:", error);
      return null;
    }
  };

  const handleCopyClick = () => {
    navigator.clipboard.writeText(formattedJSON);
  };

  return (
    <>
      <Modal show={show} onHide={handleClose} size="xl">
        <Modal.Header>
          <Modal.Title>
            <img src={logoVirtualan} alt="VT" width="50" height="50" />
            {" " + title + "!"}
          </Modal.Title>
          <span
            style={{
              color: "white",
              cursor: "pointer",
              fontFamily: "initial",
              fontSize: "100%",
              opacity: "0.2",
            }}
            onClick={handleClose}
          >
            ✗
          </span>
        </Modal.Header>
        <Modal.Body style={{ fontSize: "12px !important" }}>
          <div className="panel panel-default">
            <div
              className={`alert ${errorFormat ? "alert-warning" : ""}`}
              style={{ margin: "10px 0" }}
            >
              {errorFormat}
            </div>
            <Container style={{ marginTop: "20px 0" }}>
              <Row style={{ minHeight: "300px" }}>
                <Col className="json-formatter-col">
                  <textarea
                    rows={10}
                    cols={40}
                    value={inputJSON}
                    onChange={(e) => setInputJSON(e.target.value)}
                    className="textarea-custom"
                  />
                </Col>
                <Col className="json-formatter-col">
                  {inputJSON && renderJsonView(inputJSON)}
                </Col>
              </Row>
            </Container>
          </div>
        </Modal.Body>
        <Modal.Footer style={{ borderTop: "None" }}>
          <button className="btn-info btn" onClick={handleFormatClick}>
            Format
          </button>
          <button className="btn-info btn" onClick={handleCopyClick}>
            Copy
          </button>
          <button className="btn" onClick={handleClose}>
            Close
          </button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalJSON;
