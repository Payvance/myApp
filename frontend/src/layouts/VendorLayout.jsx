import React, { useState, useEffect } from 'react';
import VendorSidebar from '../components/common/sidebar/vendorsidebar/VendorSidebar';
// Import the Navbar component
import Navbar from '../components/common/navbar/navbar';
// Import the PageContainer component
import PageContainer from '../components/common/pagecontainer/PageContainer';
// Imported the MainScreenContainer component
import MainScreenContainer from '../components/common/mainscreencontainer/MainScreenContainer';
// Import the Footer component
import Footer from '../components/common/footer/Footer';
// Import the common layout styles
import './CommonLayout.css';

const VendorLayout = ({ children }) => {
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


  return (
    // PageContainer provides the main layout grid structure
    <PageContainer
      className="layout-grid"
      style={{
        '--sidebar-width': isSidebarCollapsed ? '3.75rem' : '13rem',
        '--navbar-height': '56px'
      }}
    >
      {/* VendorSidebar - occupies the sidebar area */}
      <VendorSidebar
        isCollapsed={isSidebarCollapsed}
        setIsCollapsed={setIsSidebarCollapsed}
      />
      {/* Navbar - occupies the navbar area */}
      <Navbar isSidebarCollapsed={isSidebarCollapsed} />

      {/* Main content area */}
      {/* Using the MainScreenContainer component */}
      <MainScreenContainer className={`with-navbar with-sidebar ${isSidebarCollapsed ? 'collapsed' : ''}`}>
        <div className="main-content">
          {children}
        </div>
        <Footer />
        {/* footer */}
      </MainScreenContainer>
      {/* mainscreencontainerend */}
    </PageContainer>
    // /* pagecontainer end */
  );
};

export default VendorLayout;
