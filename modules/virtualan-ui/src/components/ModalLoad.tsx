import Modal from "react-bootstrap/Modal";
import ModalContentLoad from "./Modals/ModalDataLoad";
import logoVirtualan from "../assets/images/logo_image.png";
import "../assets/css/styles.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTimes } from "@fortawesome/free-solid-svg-icons";


interface Props {
  title: string;
  onClose: () => void;
  show: boolean;
  refreshKey: any;
}

const ModalApp = ({ refreshKey, title, onClose, show }: Props) => {

  const handleClose = () => onClose();

  return (
    <>
      <Modal key={refreshKey} show={show} onHide={handleClose} size="xl">
        <Modal.Header>

          <Modal.Title>
            <img src={logoVirtualan} alt="VT" width="50" height="50" />
            {" " + title}
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
        <Modal.Body className="modal-body-custom">
          <ModalContentLoad mainModalClose={onClose} />
        </Modal.Body>
        <Modal.Footer></Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalApp;
