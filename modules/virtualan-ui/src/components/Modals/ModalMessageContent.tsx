import React, { useState } from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import "../../assets/css/styles.css";
import { API_MESSAGE } from "../../constants";
import GetMessageForm from "../Forms/GetMessageForm";
import PostMessageForm from "../Forms/PostMessageForm";

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
            {items.map((item:any) => (
              <Nav.Link
                href="#features"
                key={item}
                style={{ margin: "0 10px" }}
                className={selectedItem === item ? "modal-navbar-selected" : ""}
                onClick={() => handleSelectItem(item)}
              >
                {item["broker"].toUpperCase()}
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
  topics: any; 
  broker: any;
}

const Content = ({ topics, broker }: Props) => {

  let form: any[] = [];

  Object.keys(topics).map((item, index) => {
    if (index % 2 == 1) {
      form.push(
        <GetMessageForm 
          key={item}
          topics={topics[item]}
          broker={broker}
          apiEntryPointPost={API_MESSAGE}
        />
      );
    } else if (index % 2 == 0) {
      form.push(
        <PostMessageForm
          key={item}
          topics={topics[item]}
          broker={broker}
          apiEntryPointPost={API_MESSAGE}          
        />
      );
    } else {
      form.push(<p key={index}>Method not found</p>);
    }
  });

  return <>{form}</>;
};

const ModalMessageContent = ({ data }: Props) => {
  const [item, setItem] = useState("");

  if (!data || data.length === 0) {
    return <p>No data available...</p>;
  }

  const handleItemClick = (item: any) => {
    setItem(item["broker"]);
  };

  const modalmenu = NavBarModal((data), handleItemClick);
  
  const selectedData = data[0]["topics"];
  const broker = data[0]["broker"];

  return (
    <>
      {modalmenu}

      {item && <Content data={data} topics={selectedData} broker={broker}/>}
    </>
  );
};

export default ModalMessageContent;