import React, { useState } from "react";
import { Table, Button, Modal } from "react-bootstrap";
import { apiRequestsDelete } from "../../api/apiRequests";
import { API_DELETE_ENDPOINT } from "../../constants";
import "../../assets/css/styles.css";

interface Props {
  data: string[];
}

const ModalContentLoad = ({ data }: Props) => {
  const [showModal, setShowModal] = useState(false);
  const [modalContent, setModalContent] = useState("");
  const [modalTitle, setModalTitle] = useState("");
  const [searchQuery, setSearchQuery] = useState("");

  const handleModalClose = () => {
    setShowModal(false);
    setModalContent("");
  };

  const handleModalShow = (content: any, title: string) => {
    setShowModal(true);
    setModalContent(content);
    setModalTitle(title);
  };

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(event.target.value);
  };

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  const totalPages = Math.ceil(data.length / itemsPerPage);

  const handleClick = (page: number) => {
    setCurrentPage(page);
  };

  const handleRemoveItem = (id: number) => {
    apiRequestsDelete(API_DELETE_ENDPOINT, id);
  };

  const renderData = () => {
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;

    const filteredData = data.filter((item) =>
      Object.values(item).some((value) =>
        JSON.stringify(value).toLowerCase().includes(searchQuery.toLowerCase())
      )
    );

    return (
      <div style={{ maxHeight: "400px", overflowY: "auto", fontSize: "12px" }}>
        <div>
          <input
            type="text"
            placeholder="type to search"
            style={{ width: "100%" }}
            value={searchQuery}
            onChange={handleSearchChange}
          />
        </div>
        Total items: {filteredData.length}
        <Table striped bordered hover>
          <thead>
            <tr>
              <th>ID.</th>
              <th>Resource/Verb</th>
              <th>Http Code</th>
              <th>Params</th>
              <th>OperationId</th>
              <th>Script/Rule/Params</th>
              <th>Input</th>
              <th>Output</th>
              <th>HeaderParam</th>
              <th>Excludes</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {filteredData.slice(start, end).map((item: any, index: any) => {
              let inputText = "";
              try {
                const inputObj = JSON.parse(item.input);
                inputText = JSON.stringify(inputObj, null, 2);
              } catch (e) {
                inputText = JSON.stringify(item.input);
              }

              let outputText = "";
              try {
                const outputObj = JSON.parse(item.output);
                outputText = JSON.stringify(outputObj, null, 2);
              } catch (e) {
                outputText = JSON.stringify(item.output);
              }
              return (
                <tr key={item.id}>
                  <td key={1}>{item.id}</td>
                  <td key={2}>
                    {item.url}
                    <br />
                    {item.method}
                  </td>
                  <td key={3}>{item.httpStatusCode}</td>
                  <td key={4}>
                    {item.availableParams.map((subitem: any, subindex: any) => {
                      return (
                        <p key={subindex}>
                          {subitem.key}={subitem.value}
                        </p>
                      );
                    })}
                  </td>
                  <td key={5}>{item.operationId}</td>
                  <td key={6}>
                    {item.rule ? (
                      <div
                        key={item.rule}
                        onClick={() =>
                          handleModalShow(item.rule, "Parameterized")
                        }
                      >
                        <span className="form-button button-table-blue">
                          Parameterized!
                        </span>
                      </div>
                    ) : (
                      ""
                    )}
                  </td>
                  <td key={7}>
                    {item.input ? (
                      <div
                        key={item.input}
                        onClick={() => handleModalShow(inputText, "Request")}
                      >
                        <span className="form-button button-table-blue">
                          Request
                        </span>
                      </div>
                    ) : (
                      ""
                    )}
                  </td>
                  <td key={8}>
                    {item.output ? (
                      <div
                        key={item.output}
                        onClick={() => handleModalShow(outputText, "Response")}
                      >
                        <span className="form-button button-table-blue">
                          Response
                        </span>
                      </div>
                    ) : (
                      ""
                    )}
                  </td>
                  <td key={9}>
                    {item.headerParams.map((subitem: any, index: any) => {
                      return (
                        <p key={index}>
                          {subitem.key}={subitem.value}
                        </p>
                      );
                    })}
                  </td>
                  <td key={10}></td>
                  <td key={11}>
                    <div onClick={() => handleRemoveItem(item.id)}>
                      <span className="form-button button-table-red">
                        Remove
                      </span>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </Table>
        <Modal show={showModal} onHide={handleModalClose}>
          <Modal.Header closeButton>
            <Modal.Title>{modalTitle}</Modal.Title>
          </Modal.Header>
          <Modal.Body>{modalContent}</Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleModalClose}>
              Close
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    );
  };

  const renderPagination = () => {
    const pages = [];
    for (let i = 1; i <= totalPages; i++) {
      pages.push(
        <li
          key={i}
          className={`page-item ${currentPage === i ? "active" : ""}`}
        >
          <a
            key={i}
            className="page-link"
            href="#"
            onClick={() => handleClick(i)}
          >
            {i}
          </a>
        </li>
      );
    }
    return pages;
  };

  return (
    <>
      <div>{renderData()}</div>
      <nav aria-label="Page navigation example">
        <ul className="pagination">
          <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
            <a
              className="page-link"
              href="#"
              onClick={() => handleClick(currentPage - 1)}
            >
              Previous
            </a>
          </li>
          {renderPagination()}
          <li
            className={`page-item ${
              currentPage === totalPages ? "disabled" : ""
            }`}
          >
            <a
              className="page-link"
              href="#"
              onClick={() => handleClick(currentPage + 1)}
            >
              Next
            </a>
          </li>
        </ul>
      </nav>
    </>
  );
};

export default ModalContentLoad;
