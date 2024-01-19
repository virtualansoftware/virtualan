// import React from "react";
import { useState, useEffect } from "react";
import ModalAppAdd from "./ModalAdd";
import ModalAppLoad from "./ModalLoad";
import ModalAppCatalog from "./ModalCatalog";
import ModalAppJSON from "./ModalJsonFormatter";

// import { MouseEvent } from "react";
import logoVirtualan from "../assets/images/logo_image.png";
import { apiRequestsGet } from "../api/apiRequests";
import { API_GET_CATALOGS,API_GET_ENDPOINT_ADD } from "../constants";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faList } from "@fortawesome/free-solid-svg-icons";
import Content from "./Content";
import axios from "axios";



const NavBar = () => {
  const [showModalAdd, setShowModalAdd] = useState(false);
  const [showModalLoad, setShowModalLoad] = useState(false);
  const [showModalCatalog, setShowModalCatalog] = useState(false);
  const [showModalJson, setShowModalJson] = useState(false);
  // const [catalogItems, setCatalogItems] = useState([]);


  const apiLoad =  apiRequestsGet(API_GET_ENDPOINT_ADD);


  const [contentSrc, setContentSrc] = useState(
    <h2 style={{ textAlign: "center" }}>Welcome to Virtualan!!!</h2>
  );
  const [showContent, setShowContent] = useState(true);

  const [modalTitle, setModalTitle] = useState("");

  // const loadData = async () => {
  //   await axios({
  //     method: "GET",
  //     url: API_GET_CATALOGS,
  //   }).then((res) => {
  //     setCatalogItems(res.data);
  //   });
  // };

  // useEffect(() => {
  //   loadData();
  // }, []);

  // const catalogApi = catalogItems.reduce((obj, item) => {
  //   obj[item] = {
  //     modal: "Modal3",
  //     icon: <FontAwesomeIcon icon={faList} />,
  //   };
  //   return obj;
  // }, {});

  const [refreshKey, setRefreshKey] = useState(0);

  const handleClick = (title: string, modal: string, link: string) => {
    setModalTitle(title);
    setShowContent(true);
    setContentSrc(
      <h2 style={{ textAlign: "center" }}>Welcome to Virtualan!!!</h2>
    );
    setShowModalAdd(false);
    setShowModalLoad(false);
    setShowModalCatalog(false);
    setShowModalJson(false);
    if (modal === "Modal1") {
      // Add
      setShowModalAdd(true);
    } else if (modal === "Modal2") {
      // Load
      setModalTitle("List of Mock Response!");
      setShowModalLoad(true);
    // } else if (modal === "Modal3") {
    //   // Catalog
    //   setShowModalCatalog(true);
      // setModalYaml(yaml_file);
    } else if (modal === "Modal4") {
      // JSON Formatter
      setShowModalJson(true);
    } else if (modal === "help") {
      // Help
      setContentSrc(
        <iframe
          src={link}
          style={{ width: "100%", height: "720px", borderStyle: "none" }}
        ></iframe>
      );
    } else if (modal === "popup") {
      // Popup
      window.open(link, "_blank", "height=600,width=800");
    } else {
      // Default
    }
  };

  type SubMenuItem = {
    modal?: string;
    icon?: JSX.Element;
    yaml_file?: string;
    link?: string;
  };

  type MenuItem = {
    [key: string]: SubMenuItem | {};
  };

  const menuItems: Record<string, MenuItem> = {
    Home: {},
    "Virtual Service": {
      "Add Mock Data": {
        modal: "Modal1",
        icon: <FontAwesomeIcon icon={faPlus} />,
      },
      "Load Mock Data": {
        modal: "Modal2",
        icon: <FontAwesomeIcon icon={faList} />,
      },
    },
    //Catalog: catalogApi,
    Utility: {
      "Overall Catalog": { modal: "popup", link: "/swagger-ui/index.html" },
      "OpenAPI Editor": { modal: "popup", link: "/swagger-editor/index.html" },
      "JSON Formatter": { modal: "Modal4" },
      "-": "-",
      "v3.0.0": { modal: false, link: "/swagger-ui/index.html" },
      Help: {
        modal: "help",
        link: "https://tutorials.virtualan.io/#/Virtualan?downloaded=plugin&amp;version=v2.5.2",
      },
    },
  };

  return (
    <>
      <nav
        className="navbar navbar-expand-lg bg-body-tertiary"
        style={{ borderBottom: "5px solid black", marginBottom: "35px" }}
      >
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
                        {Object.entries(value).map(([subkey, subvalue]) => {
                          const item = subvalue as SubMenuItem;
                          return subkey === "-" ? (
                            <hr className="dropdown-divider" key={key} />
                          ) : (
                            <li key={subkey}>
                              <a
                                className="dropdown-item"
                                key={subkey}
                                onClick={() =>
                                  handleClick(subkey, item.modal, item.link)
                                }
                              >
                                {item.icon} {subkey}
                              </a>
                            </li>
                          );
                        })}
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

      {showModalAdd && (
        <ModalAppAdd
          title={modalTitle}
          onClose={() => setShowModalAdd(false)}
          show={showModalAdd}
          dataApi={apiLoad}
        />
      )}
      {showModalLoad && (
        <ModalAppLoad
          title={modalTitle}
          onClose={() => setShowModalLoad(false)}
          show={showModalLoad}
          refreshKey={refreshKey}
        />
      )}
      {showModalCatalog && (
        <ModalAppCatalog
          title={modalTitle}
          onClose={() => setShowModalCatalog(false)}
          show={showModalCatalog}
        />
      )}
      {showModalJson && (
        <ModalAppJSON
          title={modalTitle}
          onClose={() => setShowModalJson(false)}
          show={showModalJson}
        />
      )}
      <Content show={showContent} htmlContent={contentSrc} />
    </>
  );
};

export default NavBar;
