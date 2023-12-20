import Modal from "react-bootstrap/Modal";
import ModalContentLoad from "./Modals/ModalDataLoad";
import logoVirtualan from "../assets/images/logo_image.png";
import Button from 'react-bootstrap/Button';


interface Props {
  title: string;
  yaml_file: string;
  onClose: () => void;
  show: boolean;
}

const ModalApp = ({ title, yaml_file, onClose, show }: Props) => {
  const handleClose = () => onClose();

  return (
    <>
      <Modal show={show} onHide={handleClose} size="xl">
        {/* <Modal.Header closeButton> */}
        <Modal.Header>
          <Modal.Title>
            <img src={logoVirtualan} alt="VT" width="50" height="50" />
            {" " + title + " Catalog List"}
          </Modal.Title>
          <span style={{ color: "white", cursor: "pointer", fontFamily: "initial", fontSize: "100%", opacity: "0.2" }} onClick={handleClose}>✗</span>
        </Modal.Header>
        <Modal.Body>
          <div className="panel panel-default" style={{ marginTop: "20px", paddingTop: "10px", borderTop: "1px solid #e5e5e5"}}>
            <table className="table table-striped table-hover"  style={{ fontSize: "12px" }}>
              <thead>
                <tr className="info" style={{ backgroundColor: "#d9edf7" }}>
                  <th scope="col">Service Name</th>
                  <th scope="col">View Catalog</th>
                </tr>
              </thead>
              <tbody>
                  <tr key={11111}>
                    <td>{ yaml_file }</td>
                    <td>
                      <a target="_new" href={`swagger-ui/index.html?url=/yaml/Person/${yaml_file}`}>
                        view
                      </a>
                    </td>
                  </tr>
              </tbody>
            </table>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalApp;
