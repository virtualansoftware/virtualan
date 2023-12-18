import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom/client";
import NavBar from "./components/NavBar";
// import Content from "./components/Content";
import Footer from "./components/Footer";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap/dist/js/bootstrap.bundle";
import { RouterProvider, createBrowserRouter } from "react-router-dom";

// const [contentSrc, setContentSrc] = useState("");
// const [showContent, setShowContent] = useState(false);

const router = createBrowserRouter([
  {
    path: "/virtualan-ui",
    element: (
      <>
        <NavBar />
      </>
    ),
  },
  {
    path: "/notutorials",
    element: (
      <h4 style={{ textAlign: "center" }}>
        Either you might not have access to the internet or This page is
        restricted in your organisation{" "}
        <a href="https://tutorials.virtualan.io/#/Virtualan">
          https://tutorials.virtualan.io/#/Virtualan
        </a>
      </h4>
    ),
  },
  {
    path: "/welcome",
    element: <h2 style={{ textAlign: "center" }}>Welcome to Virtualan!!! </h2>,
  },
  {
    path: "/",
    element: (
      <>
        <h2 style={{ textAlign: "center" }}>Root </h2>
      </>
    ),
  },
]);

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <RouterProvider router={router} />
    {/* <Content show={showContent} src={contentSrc} /> */}
    <Footer />
  </React.StrictMode>
);
