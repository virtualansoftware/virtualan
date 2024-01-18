import Modal from "react-bootstrap/Modal";
import ModalContent from "./Modals/ModalDataAdd";
import logoVirtualan from "../assets/images/logo_image.png";
import { useEffect, useState } from "react";
import { apiRequestsGet } from "../api/apiRequests";
import { API_GET_ENDPOINT_ADD } from "../constants";

interface Props {
  title: string;
  onClose: () => void;
  show: boolean;
}

const ModalApp = ({ title, onClose, show }: Props) => {
  const handleClose = () => onClose();

  const [dataApi, setDataApi] = useState("");
  
  useEffect(() => {
    const fetchData = async () => {
      const data = await apiRequestsGet(API_GET_ENDPOINT_ADD);
      setDataApi(data);
    };

    fetchData();
  });

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
          <ModalContent data={dataApi} />
        </Modal.Body>
        <Modal.Footer></Modal.Footer>
      </Modal>
    </>
  );
};

export default ModalApp;
