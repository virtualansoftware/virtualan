import Modal from "react-bootstrap/Modal";
import logoVirtualan from "../assets/images/logo_image.png";
import ModalMQTTContent from "./Modals/ModalSOAPContent";

interface Props {
  title: string;
  onClose: () => void;
  show: boolean;
  dataApi: any;
}
const ModalApp = ({ title, onClose, show, dataApi }: Props) => {
  const handleClose = () => onClose();

  return (
    <>
      <Modal show={show} onHide={handleClose} size="xl">
        <Modal.Header>

          <Modal.Title>
            <img src={logoVirtualan} alt="VT" width="50" height="50" />
            {" " + title}
          </Modal.Title>
          <span style={{ color: "white", cursor: "pointer", fontFamily: "initial", fontSize: "100%", opacity: "0.2" }} onClick={handleClose}>✗</span>

        </Modal.Header>
        <Modal.Body>
          <ModalMQTTContent data={dataApi} topics={[]} broker={[]}/>
        </Modal.Body>
        <Modal.Footer></Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalApp;
