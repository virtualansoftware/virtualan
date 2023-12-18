// Footer.tsx
import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faTwitter, faGoogle, faLinkedin, faYoutube } from '@fortawesome/free-brands-svg-icons'
import logoVirtualanShort from "../assets/images/logo_short.png";


const Footer = () => {
    return (
        <div id="copyright" className="footer">
            <a className="left  impact" target="_blank" href="https://www.virtualan.io">Virtualan Software</a> 
            <a className="center impact" target="_blank" href="https://www.virtualansoftware.com">
                <span style={{ color: '#89bf04' }} className="impact">Virtualan </span>
                <span style={{ color: '#800040' }} className="impact">Developed </span> by
                <img src={logoVirtualanShort} alt="Virtualan Logo" width="20px" height="20px" />
            </a>
            <div className="right">
                <a href="https://twitter.com/VirtualanS" target="_blank" rel="noreferrer">
                    <FontAwesomeIcon icon={faTwitter} style={{fontSize: "18px", color: "#1DA1F2"}} />
                </a>
                &nbsp; &nbsp;&nbsp;&nbsp;
                <a href="mailto:info@virtualan.io" target="_blank" rel="noreferrer">
                    <FontAwesomeIcon icon={faGoogle} style={{fontSize: "18px", color: "#c71610"}} />
                </a>
                &nbsp; &nbsp;&nbsp;&nbsp;
                <a href="https://www.linkedin.com/company/virtualan-software" target="_blank" rel="noreferrer">
                    <FontAwesomeIcon icon={faLinkedin} style={{fontSize: "18px", color: "#0077b5"}} />
                </a>
                &nbsp; &nbsp;&nbsp;&nbsp;
                <a href="https://www.youtube.com/channel/UCny1F1GiYoo2pARDaSDIzYw" target="_blank" rel="noreferrer">
                    <FontAwesomeIcon icon={faYoutube} style={{fontSize: "18px", color: "#FF0000"}} />
                </a>
            </div>
        </div>
    );
};

export default Footer;