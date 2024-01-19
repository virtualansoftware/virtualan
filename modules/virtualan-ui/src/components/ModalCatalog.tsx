import Modal from "react-bootstrap/Modal";
// import ModalContentLoad from "./Modals/ModalDataLoad";
import logoVirtualan from "../assets/images/logo_image.png";
import Button from 'react-bootstrap/Button';
import { apiRequestsGet } from "../api/apiRequests";
import { API_GET_CATALOGS } from "../constants";
import { useEffect, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import axios from "axios";


interface Props {
  title: string;
  onClose: () => void;
  show: boolean;
}


const ModalApp = ({ title, onClose, show }: Props) => {

  const [yamlFile, setYamlFile] = useState(null);
  const handleClose = () => onClose();
  

  const loadData = async () => {
    await axios({
      method: "GET",
      url: API_GET_CATALOGS +"/"+title,
    }).then((res) => {
      setYamlFile(res.data);
    });
  };

  useEffect(() => {
    loadData();
  }, [show]);

  const formId = uuidv4();

  return (
    <>
      <Modal show={show} onHide={handleClose} size="xl">
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
                  <tr key={formId}>
                    <td>{ title }</td>
                    <td>
                      <a target="_new" href={`swagger-ui/index.html?url=/yaml/${title}/${yamlFile}`}>
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
