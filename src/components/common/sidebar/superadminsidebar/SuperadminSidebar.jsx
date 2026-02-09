import React, { useState, useRef, useEffect } from 'react';
import '../Sidebar.css';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import UserProfileMenu from '../sidebarProfile/SidebarProfile';
import { APP_NAME } from '../../../../config/Config';

const SuperAdminSidebar = ({ isCollapsed, setIsCollapsed }) => {
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
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const menuItems = [
    { path: '/dashboard', icon: <i className="bi bi-house"></i>, label: 'Dashboard' },
    { path: '/subscriptionplan', icon: <i className="bi-card-checklist"></i>, label: 'Plans' },
    { path: '/vendordiscount', icon: <i className="bi bi-percent"></i>, label: 'Discount and Offers' },
    // change the pathApprovals and Audits 
    { path: '/users', icon: <i className="bi bi-people"></i>, label: 'Users' },
    { path: '/licenseinventory', icon: <i className="bi bi-check2"></i>, label: 'Approvals' },
    { path: '/audits', icon: <i className="bi bi-bar-chart"></i>, label: 'Audits' },
  ];

  // Check if current route is plan-related
  const isPlanRelatedRoute = location.pathname.startsWith('/subscriptionplan') || location.pathname.startsWith('/addonplan');

  // Check if current route is discount/offer related
  const isDiscountOfferRelatedRoute = location.pathname === '/vendordiscount' || location.pathname === '/offermanagement' || location.pathname === '/referralconfiguration';

  // Check if current route is approval-related
  const isApprovalRelatedRoute = location.pathname === '/licenseinventory' || location.pathname === '/ca-redemption-approvals';
  // Check if current route is users related
  const isUserRelatedRoute = location.pathname === '/users' || location.pathname.startsWith('/users/');

  return (
    <aside className={`sidebar ${isCollapsed ? 'collapsed' : ''}`}>
      {/* Header */}
      <div className="sidebar-header">
        <div className="header-content">
          {/* sidebar header now has a professional logo that provides visual branding */}
          {!isCollapsed ? (
            <>
              <i className="bi bi-lightning-charge-fill logo-icon"></i>
              <span className="logo">{APP_NAME}</span>
            </>
          ) : (
            // an elegant hover effect that reveals the brand logo
            <div className="collapsed-logo-container" onClick={() => setIsCollapsed(false)}>
              <i className="bi bi-layout-sidebar-reverse logo-icon clickable"></i>
              <i className="bi bi-lightning-charge-fill logo-icon hover-icon"></i>
            </div>
          )}
        </div>
        <button
          className="collapse-btn"
          onClick={() => setIsCollapsed(!isCollapsed)}
        >
          {/* when the sidebar collapse at that time the icon like chatgpt show  */}
          <i className={`bi ${isCollapsed ? '' : 'bi-layout-sidebar-reverse'}`}></i>
        </button>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        <ul>
          {menuItems.map((item) => (
            <li
              key={item.path}
              // it checks the current path and if it matches the item path then it will add active class
              className={(location.pathname === item.path) || (item.label === 'Plans' && isPlanRelatedRoute) || (item.label === 'Discount and Offers' && isDiscountOfferRelatedRoute) || (item.label === 'Approvals' && isApprovalRelatedRoute) || (item.label === 'Users' && isUserRelatedRoute) ? 'active' : ''}
              title={isCollapsed ? item.label : ''}
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

export default SuperAdminSidebar;
