import React, { useState } from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import GetForm from "../Forms/GetForm";
import DeleteForm from "../Forms/DeleteForm";
import PostForm from "../Forms/PostForm";
import PutForm from "../Forms/PutForm";
import PatchForm from "../Forms/PatchtForm";
import "../../assets/css/styles.css";
import { API_POST_ENDPOINT } from "../../constants";

const NavBarModal = (items: string[], onItemClick: (item: string) => void) => {
  const [selectedItem, setSelectedItem] = useState("");

  const handleSelectItem = (item: string) => {
    setSelectedItem(item);
    onItemClick(item);
  };

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Toggle aria-controls="responsive-navbar-nav" />
        <Navbar.Collapse id="responsive-navbar-nav">
          <Nav className="me-auto" style={{ display: "flex" }}>
            {items.map((item) => (
              <Nav.Link
                href="#features"
                key={item}
                style={{ margin: "0 10px" }}
                className={selectedItem === item ? "modal-navbar-selected" : ""}
                onClick={() => handleSelectItem(item)}
              >
                {item.toUpperCase()}
              </Nav.Link>
            ))}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

interface Props {
  data: any;
}

const Content = ({ data }: Props) => {
  let form: any[] = [];

  Object.keys(data).map((item) => {
    if (data[item]["method"] === "GET") {
      form.push(
        <GetForm
          key={item}
          path={data[item]["url"]}
          availableParams={data[item]["availableParams"]}
          apiEntryPointPost={API_POST_ENDPOINT}
        />
      );
    } else if (data[item]["method"] === "DELETE") {
      form.push(
        <DeleteForm
          key={item}
          path={data[item]["url"]}
          availableParams={data[item]["availableParams"]}
          apiEntryPointPost={API_POST_ENDPOINT}
        />
      );
    } else if (data[item]["method"] === "POST") {
      form.push(
        <PostForm
          key={item}
          path={data[item]["url"]}
          availableParams={data[item]["availableParams"]}
          apiEntryPointPost={API_POST_ENDPOINT}
        />
      );
    } else if (data[item]["method"] === "PUT") {
      form.push(
        <PutForm
          key={item}
          path={data[item]["url"]}
          availableParams={data[item]["availableParams"]}
          apiEntryPointPost={API_POST_ENDPOINT}
        />
      );
    } else if (data[item]["method"] === "PATCH") {
      form.push(
        <PatchForm
          key={item}
          path={data[item]["url"]}
          availableParams={data[item]["availableParams"]}
          apiEntryPointPost={API_POST_ENDPOINT}
        />
      );
    } else {
      form.push(<p>Method not found</p>);
    }
  });

  return <>{form}</>;
};

const ModalContent = ({ data }: Props) => {
  const [item, setItem] = useState("");

  const handleItemClick = (item: string) => {
    setItem(item);
  };

  const modalmenu = NavBarModal(Object.keys(data), handleItemClick);
  return (
    <>
      {modalmenu}
      {item && <Content data={data[item]} />}
    </>
  );
};
export default ModalContent;
