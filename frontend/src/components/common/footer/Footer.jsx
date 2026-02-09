import React from "react";
import "./Footer.css";
import { COMPANY_INFO } from "../../../config/Config";
/**
 * Footer component
 * Used to display footer text or custom content
 */
const Footer = ({ text, children }) => {
  // Prefer children content if provided, otherwise use text, otherwise use default
  const content = children ?? text ?? `v1.0.0 | ${COMPANY_INFO.name} © 2025. All rights reserved.`;  // Footer container
  return <div className="footer">{content}</div>;
};

export default Footer;
