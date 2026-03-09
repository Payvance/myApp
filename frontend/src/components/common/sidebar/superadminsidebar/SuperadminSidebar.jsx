import React, { useState, useRef, useEffect, useCallback, memo } from 'react';
import '../Sidebar.css';
import { Link, useLocation } from 'react-router-dom';
import UserProfileMenu from '../sidebarProfile/SidebarProfile';
import { APP_NAME } from '../../../../config/Config';

/* ───────────── Static Data ───────────── */

const userSubItems = [
  { path: '/partners', icon: 'bi bi-person-lines-fill', label: 'All Partners' },
  { path: '/users/pending', icon: 'bi bi-hourglass-split', label: 'Pending Approval' },
  { path: '/users/reject', icon: 'bi bi-person-x-fill', label: 'Rejected Partners' },
  { path: '/vendordiscount', icon: 'bi bi-percent', label: 'Discount' },
];

const tenantSubItems = [
  { path: '/tenantmanagement', icon: 'bi bi-building', label: 'All Tenants' },
  { path: '/offermanagement', icon: 'bi bi-percent', label: 'Offers' },
];

const approvalSubItems = [
  { path: '/licenseinventory', icon: 'bi bi-file-earmark-check', label: 'Vendor Batch Requests' },
  { path: '/ca-redemption-approvals', icon: 'bi bi-patch-check-fill', label: 'CA Redemption Approvals' },
];

const plansSubItems = [
  { path: '/subscriptionplan', icon: 'bi bi-card-checklist', label: 'Subscription Plans' },
  { path: '/addonplans', icon: 'bi bi-plus-circle', label: 'Add-on Plans' },
];

const menuItems = [
  { path: '/dashboard', icon: 'bi bi-house', label: 'Dashboard' },
  {
    label: 'Plans',
    icon: 'bi bi-card-checklist',
    hasSubMenu: true,
    subItems: plansSubItems,
  },
   {
    label: 'Tenant',
    icon: 'bi bi-buildings',
    hasSubMenu: true,
    subItems: tenantSubItems,
  },
  {
    label: 'Partners',
    icon: 'bi bi-people',
    hasSubMenu: true,
    subItems: userSubItems,
  },
  { path: '/referralconfiguration', icon: 'bi bi-share-fill', label: 'Referral' },
  {
    label: 'Approvals',
    icon: 'bi bi-check-circle',
    hasSubMenu: true,
    subItems: approvalSubItems,
  },
  { path: '/audits', icon: 'bi bi-bar-chart', label: 'Audits' },
];

/* ───────────── Active Route Logic ───────────── */

function getIsActive(item, pathname) {
  if (item.hasSubMenu) {
    return item.subItems.some((sub) =>
      pathname.startsWith(sub.path)
    );
  }
  return pathname === item.path;
}

/* ───────────── Sub Menu Item ───────────── */

const SubMenuItem = memo(({ sub, isActive }) => (
  <li className={isActive ? 'active' : ''}>
    <Link to={sub.path}>
      <span className="icon sub-icon">
        <i className={sub.icon}></i>
      </span>
      <span className="label">{sub.label}</span>
    </Link>
  </li>
));

/* ───────────── Nav Item ───────────── */

const NavItem = memo(({
  item,
  isActive,
  isCollapsed,
  isExpanded,
  toggleMenu,
  currentPath
}) => {
  return (
    <>
      <li
        className={[
          isActive ? 'active' : '',
          item.hasSubMenu ? 'has-submenu' : ''
        ].filter(Boolean).join(' ')}
        title={isCollapsed ? item.label : ''}
      >
        {item.hasSubMenu && !isCollapsed ? (
          <button className="submenu-trigger" onClick={toggleMenu}>
            <span className="icon">
              <i className={item.icon}></i>
            </span>
            <span className="label">{item.label}</span>
            <span className={`submenu-chevron ${isExpanded ? 'open' : ''}`}>
              <i className="bi bi-chevron-down"></i>
            </span>
          </button>
        ) : (
          <Link to={item.path || '#'}>
            <span className="icon">
              <i className={item.icon}></i>
            </span>
            <span className="label">{item.label}</span>
          </Link>
        )}
      </li>

      {item.hasSubMenu && !isCollapsed && (
        <div className={`submenu-wrapper ${isExpanded ? 'expanded' : ''}`}>
          <ul className="submenu">
            {item.subItems.map((sub) => (
              <SubMenuItem
                key={sub.path}
                sub={sub}
                isActive={currentPath === sub.path}
              />
            ))}
          </ul>
        </div>
      )}
    </>
  );
});

/* ───────────── Main Sidebar ───────────── */

const SuperAdminSidebar = ({ isCollapsed, setIsCollapsed }) => {
  const location = useLocation();
  const menuRef = useRef(null);

  // 🔥 Only ONE active dropdown at a time
  const [activeMenu, setActiveMenu] = useState(() => {
    const current = menuItems.find(
      (item) =>
        item.hasSubMenu &&
        item.subItems.some((sub) =>
          location.pathname.startsWith(sub.path)
        )
    );
    return current?.label || null;
  });

  const toggleMenu = useCallback((label) => {
    setActiveMenu((prev) => (prev === label ? null : label));
  }, []);

  // Close dropdowns when sidebar collapses
  useEffect(() => {
    if (isCollapsed) {
      setActiveMenu(null);
    }
  }, [isCollapsed]);

  return (
    <aside
      className={`sidebar ${isCollapsed ? 'collapsed' : ''}`}
      ref={menuRef}
    >
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
          <i className={`bi ${isCollapsed ? '' : 'bi-layout-sidebar-reverse'}`}></i>
        </button>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        <ul>
          {menuItems.map((item) => (
            <NavItem
              key={item.label}
              item={item}
              isActive={getIsActive(item, location.pathname)}
              isCollapsed={isCollapsed}
              isExpanded={activeMenu === item.label}
              toggleMenu={() => toggleMenu(item.label)}
              currentPath={location.pathname}
            />
          ))}
        </ul>
      </nav>

      {/* Footer */}
      <UserProfileMenu
        showUpgrade={false}
        showPersonalization={true}
        showForgotPassword={true}
      />
    </aside>
  );
};

export default SuperAdminSidebar;