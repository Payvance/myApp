import { useState } from 'react';
import PageContainer from '../components/common/pagecontainer/PageContainer';
import TenantSidebar from '../components/common/sidebar/tenantsidebar/TenantSidebar';
import Navbar from '../components/common/navbar/navbar';
import MainScreenContainer from '../components/common/mainscreencontainer/MainScreenContainer';
import Footer from '../components/common/footer/Footer';

const TenantLayout = ({ children }) => {
    const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
    return (
    // PageContainer provides the main layout grid structure
    <PageContainer
      className="layout-grid"
      style={{
        '--sidebar-width': isSidebarCollapsed ? '3.75rem' : '13rem',
        '--navbar-height': '56px'
      }}
    >
        <TenantSidebar
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
      
    </PageContainer>
  );
};

export default TenantLayout;