// src/components/common/navbar/Navbar.jsx
import React, { useState } from 'react';
// import location and navigate from react router
import { useLocation, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
// import theme dropdown component
import ThemeDropdown from '../themedropdown/ThemeDropdown';
// import theme context
import { useTheme } from '../../../context/ThemeContext';
// import navbar css
import './navbar.css';

const Navbar = ({ isSidebarCollapsed }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const [isThemeDropdownOpen, setIsThemeDropdownOpen] = useState(false);
  const { theme, setTheme, isDark, isCustom } = useTheme();
  const roleId = useSelector((state) => state.auth.roleId);
  const isSuperAdmin = roleId === 1;

  // Determine page title and menu items based on current path
  const getPageConfig = () => {
    if (
      location.pathname === '/redemption' ||
      location.pathname === '/redemption/pending'
    ) {
      return {
        title: 'Redemption',
        menuItems: ['Redemption', 'Pending'],
      };
    }
    // User page menu items
    if (location.pathname === '/users' || location.pathname === '/users/pending' || location.pathname === '/users/reject') {
      return {
        title: 'Users',
        // menu items for subscription plan
        menuItems: ['Users', 'Pending Approval', 'Reject Users']
      };
    }
    if (location.pathname === '/subscriptionplan') {
      return {
        title: 'Subscription Plans',
        // menu items for subscription plan
        menuItems: ['Subscription Plans', 'Add-on Plans']
      };
    }
    // The dashboard page will now display only the title "Dashboard" without any menu items
    if (location.pathname === '/dashboard') {
      return {
        title: 'Dashboard',
        menuItems: []
      };
    }
    // Vendor Discount page menu items
    if (location.pathname === '/vendordiscount') {
      return {
        title: 'Vendor Discounts',
        menuItems: ['Vendor Discount', 'Offer Management', 'Referral Configuration']
      };
    }
    // Offer Management page menu items
    if (location.pathname === '/offermanagement') {
      return {
        title: 'Offer Management',
        menuItems: ['Vendor Discount', 'Offer Management', 'Referral Configuration']
      };
    }
    // Referral Configuration page menu items
    if (location.pathname === '/referralconfiguration') {
      return {
        title: 'Referral Configuration',
        menuItems: ['Vendor Discount', 'Offer Management', 'Referral Configuration']
      };
    }
    // Approvals page menu items (includes redirected path for Superadmin)
    if (
      location.pathname === '/approvals' ||
      location.pathname === '/ca-redemption-approvals' ||
      (location.pathname === '/licenseinventory' && isSuperAdmin)
    ) {
      return {
        title: 'Approvals',
        menuItems: ['Vendor Batch Request', 'CA Redemption Request']
      };
    }
    // Add-on Plans page menu items
    if (location.pathname === '/addonplans') {
      return {
        title: 'Add-on Plans',
        menuItems: ['Subscription Plans', 'Add-on Plans']
      };
    }
    return {
      title: 'Dashboard',
      menuItems: []
    };
  };

  const { menuItems } = getPageConfig();

  // Determine active menu item based on current path
  const getActiveMenuItem = () => {
    if (location.pathname === '/redemption') return 'Redemption';
    if (location.pathname === '/redemption/pending') return 'Pending';
    if (location.pathname === '/subscriptionplan') return 'Subscription Plans';
    if (location.pathname === '/addonplans') return 'Add-on Plans';
    if (location.pathname === '/users') return 'Users';
    if (location.pathname === '/users/pending') return 'Pending Approval';
    if (location.pathname === '/users/reject') return 'Reject Users';
    if (location.pathname === '/vendordiscount') return 'Vendor Discount';
    if (location.pathname === '/offermanagement') return 'Offer Management';
    if (location.pathname === '/referralconfiguration') return 'Referral Configuration';
    if (
      location.pathname === '/approvals' ||
      (location.pathname === '/licenseinventory' && isSuperAdmin)
    ) return 'Vendor Batch Request';
    if (location.pathname === '/ca-redemption-approvals') return 'CA Redemption Request';
    return '';
  };

  const activeMenuItem = getActiveMenuItem();

  const handleThemeChange = (selectedTheme) => {

    // Set the theme using ThemeContext
    setTheme(selectedTheme);
  };
  // handle menu click function
  const handleMenuClick = (item) => {
    if (item === 'Dashboard') {
      navigate('/dashboard');
    }
    else if (item === 'Redemption') {
      navigate('/redemption');
    }
    else if (item === 'Pending') {
      navigate('/redemption/pending');
    }
    else if (item === 'Users') {
      navigate('/users');
    }
    else if (item === 'Pending Approval') {
      navigate('/users/pending');
    }
    else if (item === 'Reject Users') {
      navigate('/users/reject');
    }
    else if (item === 'Subscription Plans') {
      navigate('/subscriptionplan');
    } else if (item === 'Add-on Plans') {
      navigate('/addonplans');
      // Vendor Discount page menu items 
    } else if (item === 'Vendor Discount') {
      navigate('/vendordiscount');
    } else if (item === 'Offer Management') {
      navigate('/offermanagement');
    } else if (item === 'Referral Configuration') {
      navigate('/referralconfiguration');
    }
    // Approvals page menu items
    else if (item === 'Vendor Batch Request') {
      navigate('/approvals');
    } else if (item === 'CA Redemption Request') {
      navigate('/ca-redemption-approvals');
    }
  };

  return (
    <header className={`app-navbar ${isSidebarCollapsed ? 'sidebar-collapsed' : ''}`}>
      {/* Left section */}
      <div className="navbar-left">
        {menuItems.map((item, index) => (
          // Menu button with click handler and active state
          <button
            key={index}
            className={`navbar-btn ${item === activeMenuItem ? 'active' : ''}`}
            onClick={() => handleMenuClick(item)}
          >
            {item}
          </button>
        ))}
      </div>

      {/* Right section */}
      <div className="navbar-right">
        <div className="theme-button-container">
          {/* Theme button */}
          <button
            className="navbar-btn theme-btn"
            onClick={() => setIsThemeDropdownOpen(!isThemeDropdownOpen)}
          >
            {isCustom ? (
              <i className="bi bi-palette-fill"></i>
            ) : isDark ? (
              <i className="bi bi-moon-fill"></i>
            ) : (
              <i className="bi bi-sun-fill"></i>
            )}
          </button>

          <ThemeDropdown
            isOpen={isThemeDropdownOpen}
            onClose={() => setIsThemeDropdownOpen(false)}
            onThemeChange={handleThemeChange}
          />
        </div>
      </div>
    </header>
  );
};

export default Navbar;
