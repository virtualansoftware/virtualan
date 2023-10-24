import React, { useState, useEffect } from "react";
import ModalAppAdd from "./ModalAdd";
import ModalAppLoad from "./ModalLoad";
import { MouseEvent } from "react";
import logoVirtualan from "../assets/images/logo_image.png";
import { apiRequestsGet } from "../api/apiRequests";
import { API_GET_ENDPOINT_ADD, API_GET_ENDPOINT_LOAD } from "../constants";

// interface Props {
//   key: string;
// }

const NavBar = () => {
  const [showModalAdd, setShowModalAdd] = useState(false);
  const [showModalLoad, setShowModalLoad] = useState(false);
  const [modalTitle, setModalTitle] = useState("");

  const MockDataAdd = apiRequestsGet(API_GET_ENDPOINT_ADD);
  const MockDataLoad = apiRequestsGet(API_GET_ENDPOINT_LOAD);

  const handleClick = (title: string, modal: string) => {
    setModalTitle(title);
    if (modal === "Modal1") {
      setShowModalAdd(true);
      setShowModalLoad(false);
    } else if (modal === "Modal2") {
      setShowModalAdd(false);
      setShowModalLoad(true);
    } else {
      setShowModalAdd(false);
      setShowModalLoad(false);
    }
  };

  const menuItems = {
    Home: {},
    "Virtual Service": {
      "Add Mock Data": "Modal1",
      "Load Mock Data": "Modal2",
    },
    Catalog: {
      "View Service": "data",
    },
    Utility: {
      "Overall Catalog": "data",
      "OpenAPI Editor": "data",
      "JSON Formatter": "data",
      "-": "-",
      "v2.5.2": "data",
      Help: "data",
    },
  };
  return (
    <>
      <nav className="navbar navbar-expand-lg bg-body-tertiary">
        <div className="container-fluid">
          <a className="navbar-brand" href="#">
            <img src={logoVirtualan} alt="VT" width="50" height="50" />
            <span style={{ color: "#800040", fontFamily: "Impact" }}>
              Virtualan
            </span>
          </a>
          <button
            className="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarScroll"
            aria-controls="navbarScroll"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarScroll">
            <ul className="navbar-nav me-auto my-2 my-lg-0 navbar-nav-scroll">
              {Object.entries(menuItems).map(([key, value]) => (
                <li className="nav-item dropdown" key={key}>
                  {Object.keys(value).length > 0 ? (
                    <>
                      <a
                        className="nav-link dropdown-toggle"
                        href="#"
                        role="button"
                        data-bs-toggle="dropdown"
                        aria-expanded="false"
                      >
                        {key}
                      </a>
                      <ul className="dropdown-menu">
                        {Object.entries(value).map(([subkey, subvalue]) =>
                          subkey === "-" ? (
                            <hr className="dropdown-divider" key={key} />
                          ) : (
                            <li key={subkey}>
                              <a
                                className="dropdown-item"
                                key={subkey}
                                onClick={() => handleClick(subkey, subvalue)}
                              >
                                {subkey}
                              </a>
                            </li>
                          )
                        )}
                      </ul>
                    </>
                  ) : (
                    <a className="nav-link active" aria-current="page" href="#">
                      {key}
                    </a>
                  )}
                </li>
              ))}
            </ul>
            <span
              className="navbar-text ml-auto"
              style={{ color: "#004080", fontFamily: "Impact" }}
            >
              The Service Virtualization Product
            </span>
          </div>
        </div>
      </nav>

      <hr />

      {showModalAdd && (
        <ModalAppAdd
          title={modalTitle}
          onClose={() => setShowModalAdd(false)}
          show={showModalAdd}
          dataApi={MockDataAdd}
        />
      )}
      {showModalLoad && (
        <ModalAppLoad
          title={modalTitle}
          onClose={() => setShowModalLoad(false)}
          show={showModalLoad}
          dataApi={MockDataLoad}
        />
      )}
    </>
  );
};

export default NavBar;
