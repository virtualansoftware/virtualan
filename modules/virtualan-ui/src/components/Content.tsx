import React from "react";

interface Props {
  show: boolean;
  htmlContent: React.ReactNode
}

const Content = ({ show, htmlContent }: Props) => {
  if (!show) {
    return null;
  }

  return (
    <div>
      { htmlContent }
    </div>
  );
};

export default Content;
