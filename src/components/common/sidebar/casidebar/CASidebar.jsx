/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * author                version        date        change description
 * Neha Tembhe           1.0.0         13/01/2026   CA sidebar component
 *
 **/
import { useState, useRef, useEffect } from "react";
import "../Sidebar.css";
import { Link, useLocation, useNavigate } from "react-router-dom";
import UserProfileMenu from "../sidebarProfile/SidebarProfile";
import { APP_NAME } from "../../../../config/Config";

const CASidebar = ({ isCollapsed, setIsCollapsed }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const [openMenu, setOpenMenu] = useState(false);
  const menuRef = useRef(null);

  // Close menu on outside click
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const menuItems = [
    {
      path: "/cadashboard",
      icon: <i className="bi bi-house"></i>,
      label: "Dashboard",
    },
    {
      path: "/redemption",
      icon: <i className="bi bi-arrow-repeat"></i>,
      label: "Redemption",
    },
    {
      path: "/settlement",
      icon: <i className="bi bi-cash-stack"></i>,
      label: "Settlement",
    },
  ];

  return (
    <aside className={`sidebar ${isCollapsed ? "collapsed" : ""}`}>
      {/* Header */}
      <div className="sidebar-header">
        <div className="header-content">
          {!isCollapsed ? (
            <>
              <i className="bi bi-lightning-charge-fill logo-icon"></i>
              <span className="logo">{APP_NAME}</span>
            </>
          ) : (
            <div
              className="collapsed-logo-container"
              onClick={() => setIsCollapsed(false)}
            >
              <i className="bi bi-layout-sidebar-reverse logo-icon clickable"></i>
              <i className="bi bi-lightning-charge-fill logo-icon hover-icon"></i>
            </div>
          )}
        </div>

        <button
          className="collapse-btn"
          onClick={() => setIsCollapsed(!isCollapsed)}
        >
          <i
            className={`bi ${
              isCollapsed ? "" : "bi-layout-sidebar-reverse"
            }`}
          ></i>
        </button>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        <ul>
          {menuItems.map((item) => (
            <li
              key={item.path}
              className={location.pathname === item.path ? "active" : ""}
              title={isCollapsed ? item.label : ""}
            >
              <Link to={item.path}>
                <span className="icon">{item.icon}</span>
                <span className="label">{item.label}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* Footer */}
      <UserProfileMenu
       showUpgrade = {false}
       showPersonalization = {true}
       showForgotPassword = {true}
      />
    </aside>
  );
};

export default CASidebar;
