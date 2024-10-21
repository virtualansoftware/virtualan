import React, { useEffect, useState } from "react";
import { Table, Button, Modal } from "react-bootstrap";
import { apiRequestsDelete, apiRequestsGet } from "../../api/apiRequests";
import { API_DELETE_ENDPOINT, API_GET_ENDPOINT_LOAD } from "../../constants";
import { CodeBlock, dracula } from "react-code-blocks";
import { JsonToTable } from "react-json-to-table";
import axios from "axios";

import "../../assets/css/styles.css";

import ReactMarkdown from "react-markdown";
import JSONPretty from "react-json-pretty";
import JSONPrettyMon from "react-json-pretty/dist/monikai";
import Stack from "react-bootstrap/Stack";
import { JsonView, allExpanded, defaultStyles } from "react-json-view-lite";
import "react-json-view-lite/dist/index.css";

function isJSON(str: any) {
  try {
    JSON.parse(str);
    return true;
  } catch (e) {
    return false;
  }
}

interface Props {
  mainModalClose: () => void;
}

const ModalContentLoad = ({ mainModalClose }: Props) => {
  const [refreshKey, setRefreshKey] = useState(0);
  const [showType, setShowType] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [modalContent, setModalContent] = useState("");
  const [modalTitle, setModalTitle] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);
  const [dataset, setDataset] = useState([]);

  const loadData = async () => {
    const result = await axios({
      method: "GET",
      url: API_GET_ENDPOINT_LOAD,
    }).then((res) => {
      setDataset(res.data);
    });
  };

  useEffect(() => {
    loadData();
  }, [refreshKey]);

  const handleModalClose = () => {
    setShowModal(false);
    setModalContent("");
    setShowType("");
  };

  const handleModalShow = (type: any, content: any, title: string) => {
    setShowModal(true);
    setModalContent(content);
    setShowType(type.toUpperCase());
    setModalTitle(title);
  };

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(event.target.value);
  };

  const handleItemsPerPageChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setItemsPerPage(Number(event.target.value));
  };

  const handleClick = (page: number) => {
    setCurrentPage(page);
  };

  const handleRemoveItem = (id: number) => {
    apiRequestsDelete(API_DELETE_ENDPOINT, id);
    setRefreshKey((oldKey) => oldKey + 1);
  };

  const totalPages = Math.ceil(dataset.length / itemsPerPage);

  const handleRefresh = () => {
    setRefreshKey((oldKey) => oldKey + 1);
  };

  const syntaxHighlight = (json : string) => {
    if (typeof json != 'string') {
           json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\\s*:)?|\b(true|false|null)\b|-?\\d+(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
  }

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
      const data = syntaxHighlight(jsonString);;
      return (
        <JsonView
          data={data}
          shouldExpandNode={allExpanded}
          style={defaultStyles}
        />
      );
    }
  };

  const renderData = () => {
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;

    const filteredData = dataset.filter((item) =>
      Object.values(item).some((value) =>
        JSON.stringify(value).toLowerCase().includes(searchQuery.toLowerCase())
      )
    );

    return (
      <div style={{ fontSize: "12px" }}>
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
                          handleModalShow(
                            item.type,
                            item.rule,
                            item.type.toUpperCase()
                          )
                        }
                      >
                        <span className="form-button button-table-blue">
                          {item.type.toLowerCase() === "script" && <>Script!</>}
                          {item.type.toLowerCase() === "rule" && <>Rule!</>}
                          {item.type.toLowerCase() === "params" && (
                            <>Parameterized!</>
                          )}
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
                        onClick={() =>
                          handleModalShow(item.type, inputText, "Request")
                        }
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
                        onClick={() =>
                          handleModalShow(item.type, outputText, "Response")
                        }
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
                  <td key={10}>
                    {item.excludeList}
                  </td>
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
        <Modal show={showModal} onHide={handleModalClose} size="lg">
          {" "}
          {/*  subModal */}
          <Modal.Header closeButton>
            <Modal.Title>{modalTitle}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <div className="row" style={{ padding: "20px" }}>
              {(showType === "PARAMS" && modalTitle === 'PARAMS') ? (
                <JsonToTable json={JSON.parse(modalContent)} />
              ) : isJSON(modalContent) ? (
                <>
                  <div className="col">
                    {
                       <JSONPretty
                        data={JSON.parse(modalContent)}
                        theme={JSONPrettyMon}
                      />
                    }
                  </div>
                  <div className="col">
                    {/* <pre style={{ color: "green", padding: "10px" }}> */}
                    {/* {yaml.dump(JSON.parse(modalContent))} */}
                    {renderJsonView(modalContent)}
                    {/* </pre> */}
                  </div>
                </>
              ) : showType == "SCRIPT" || showType == "RULE" ? (
                <CodeBlock
                  text={modalContent}
                  language="groovy"
                  showLineNumbers={true}
                  theme={dracula}
                />
              ) : (
                <div className="col">
                  <ReactMarkdown>{ syntaxHighlight(modalContent)}</ReactMarkdown>
                </div>
              )}
            </div>
          </Modal.Body>
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
      pages.push(i);
    }

    const activeStyle = {
      backgroundColor: "#39b3d7",
      color: "#fff",
      cursor: "default",
    };

    const defaultStyle = {
      color: "#999",
      fontSize: "0.8rem",
    };

    return (
      <ul className="pagination-sm pagination">
        <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
          <a
            className="page-link"
            href="#"
            onClick={() => handleClick(1)}
            style={defaultStyle}
          >
            First
          </a>
        </li>
        <li className={`page-item ${currentPage === 1 ? "disabled" : ""}`}>
          <a
            className="page-link"
            href="#"
            onClick={() => handleClick(currentPage - 1)}
            style={defaultStyle}
          >
            Previous
          </a>
        </li>
        {pages.map((page, index) => (
          <li
            key={index}
            className={`page-item ${currentPage === page ? "active" : ""}`}
          >
            <a
              className="page-link"
              href="#"
              onClick={() => handleClick(page)}
              style={currentPage === page ? activeStyle : defaultStyle}
            >
              {page}
            </a>
          </li>
        ))}
        <li
          className={`page-item ${
            currentPage === totalPages ? "disabled" : ""
          }`}
        >
          <a
            className="page-link"
            href="#"
            onClick={() => handleClick(currentPage + 1)}
            style={defaultStyle}
          >
            Next
          </a>
        </li>
        <li
          className={`page-item ${
            currentPage === totalPages ? "disabled" : ""
          }`}
        >
          <a
            className="page-link"
            href="#"
            onClick={() => handleClick(totalPages)}
            style={defaultStyle}
          >
            Last
          </a>
        </li>
      </ul>
    );
  };

  return (
    <>
      <div key={refreshKey}>{renderData()}</div>

      <Stack direction="horizontal">
        <div className="p-2 ms-auto">
          View{" "}
          <select value={itemsPerPage} onChange={handleItemsPerPageChange}>
            <option value={5}>5</option>
            <option value={10}>10</option>
            <option value={20}>20</option>
            <option value={50}>50</option>
          </select>{" "}
          records at a time.
        </div>
        <div className="pb-4 mb-3">
          <nav aria-label="Page navigation example">{renderPagination()}</nav>
        </div>
        <div className="p-2">
          <button
            type="button"
            className="btn btn-refresh"
            onClick={handleRefresh}
          >
            Refresh
          </button>
          <button
            type="button"
            className="btn"
            style={{ marginLeft: "5px" }}
            onClick={() => mainModalClose()}
          >
            Close
          </button>
        </div>
      </Stack>
    </>
  );
};

export default ModalContentLoad;
