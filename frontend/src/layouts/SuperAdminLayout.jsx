import React, { useState, useEffect } from 'react';
import SuperAdminSidebar from '../components/common/sidebar/superadminsidebar/SuperadminSidebar';
import Navbar from '../components/common/navbar/navbar';
import PageContainer from '../components/common/pagecontainer/PageContainer';
// Imported the MainScreenContainer component
import MainScreenContainer from '../components/common/mainscreencontainer/MainScreenContainer';
import Footer from '../components/common/footer/Footer';

// Import the common layout styles
import './CommonLayout.css';

const SuperAdminLayout = ({ children }) => {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);

  // Auto-collapse sidebar on width < 1024px
  useEffect(() => {
    const handleResize = () => {
      const shouldCollapse = window.innerWidth < 1024;
      setIsSidebarCollapsed(shouldCollapse);
    };
    // Set initial state
    handleResize();
    // Add resize listener
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

// Return the layout with the common grid structure
  return (
    // PageContainer provides the main layout grid structure
    <PageContainer
      className="layout-grid"
      style={{
        '--sidebar-width': isSidebarCollapsed ? '3.75rem' : '13rem',
        '--navbar-height': '56px'
      }}
    >
      {/* SuperAdminSidebar - occupies the sidebar area */}
      <SuperAdminSidebar
        isCollapsed={isSidebarCollapsed}
        setIsCollapsed={setIsSidebarCollapsed}
      />
      {/* Navbar - occupies the navbar area */}
      <Navbar isSidebarCollapsed={isSidebarCollapsed} />

      <MainScreenContainer className={`with-navbar with-sidebar ${isSidebarCollapsed ? 'collapsed' : ''}`}>
        <div className="main-content">
          {children}
        </div>
        <Footer />
      </MainScreenContainer>
    </PageContainer>
  );
};

export default SuperAdminLayout;
