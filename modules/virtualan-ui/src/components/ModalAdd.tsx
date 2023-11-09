import Modal from "react-bootstrap/Modal";
import ModalContent from "./Modals/ModalDataAdd";
import logoVirtualan from "../assets/images/logo_image.png";

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
        <Modal.Header closeButton>
          <Modal.Title>
            <img src={logoVirtualan} alt="VT" width="50" height="50" />
            {" " + title}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <ModalContent data={dataApi} />
        </Modal.Body>
        <Modal.Footer></Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalApp;
