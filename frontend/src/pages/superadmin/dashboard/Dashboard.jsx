import React from 'react';
// PageContainer: common wrapper
import PageContainer from '../../../components/common/pagecontainer/PageContainer';
// SuperAdminLayout: layout component
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
// Theme CSS
import '../../../theme/LightTheme.css';
import './Dashboard.css';
import CommonDashboard from '../../../components/dashboard/CommonDashboard';
import { useNavigate } from "react-router-dom";

// Dashboard component
const Dashboard = () => {
  const navigate = useNavigate();

  return (
    <SuperAdminLayout>
      <CommonDashboard />
    </SuperAdminLayout>
  );
};

export default Dashboard;