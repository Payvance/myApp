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
 * Neha Tembhe           1.0.0         13/01/2026   CA layout component
 *
 **/
import { useState, useEffect } from 'react';
import Navbar from '../components/common/navbar/navbar';
import PageContainer from '../components/common/pagecontainer/PageContainer';
import MainScreenContainer from '../components/common/mainscreencontainer/MainScreenContainer';
import Footer from '../components/common/footer/Footer';
import './CommonLayout.css';
import CASidebar from '../components/common/sidebar/casidebar/CASidebar';

const CALayout = ({ children }) => {
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
      <CASidebar
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

export default CALayout;
