import React, { useState } from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import "../../assets/css/styles.css";
import { API_MESSAGE } from "../../constants";
import GetMessageForm from "../Forms/GetMessageForm";
import PostMessageForm from "../Forms/PostMessageForm";

// Navbar Component
const NavBarModal = ({ items, onItemClick }: { items: any[], onItemClick: (item: any) => void }) => {
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
                key={item.broker}
                style={{ margin: "0 10px" }}
                className={selectedItem === item.broker ? "modal-navbar-selected" : ""}
                onClick={() => handleSelectItem(item.broker)}
              >
                {item.broker.toUpperCase()}
              </Nav.Link>
            ))}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

// Content Component
const Content = ({ topics, broker }: { topics: any; broker: any }) => {
  let form: any[] = [];

  Object.keys(topics).forEach((item, index) => {
    if (index % 2 === 1) {
      form.push(
        <GetMessageForm 
          key={item}
          topics={topics[item]}
          broker={broker}
          apiEntryPointPost={API_MESSAGE}
        />
      );
    } else {
      form.push(
        <PostMessageForm
          key={item}
          topics={topics[item]}
          broker={broker}
          apiEntryPointPost={API_MESSAGE}          
        />
      );
    }
  });

  return <>{form}</>;
};

// Main Component
const ModalMessageContent = ({ data }: { data: any }) => {
  const [selectedBroker, setSelectedBroker] = useState("");
  const [selectedTopics, setSelectedTopics] = useState<any>({});

  if (!data || data.length === 0) {
    return <p>No data available...</p>;
  }

  const handleItemClick = (broker: string) => {
    setSelectedBroker(broker);
    const selectedData = data.find((item:any) => item.broker === broker);
    setSelectedTopics(selectedData ? selectedData.topics : {});
  };

  const modalMenu = <NavBarModal items={data} onItemClick={handleItemClick} />;

  return (
    <>
      {modalMenu}

      {selectedBroker && <Content topics={selectedTopics} broker={selectedBroker} />}
    </>
  );
};

export default ModalMessageContent;
