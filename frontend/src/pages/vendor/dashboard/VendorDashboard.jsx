import React from 'react';
import VendorLayout from '../../../layouts/VendorLayout';
import CommonDashboard from '../../../components/dashboard/CommonDashboard';
import '../../../theme/LightTheme.css';
import './VendorDashboard.css';

// Dashboard component
const VendorDashboard = () => {
  return (
    <VendorLayout>
      <CommonDashboard />
    </VendorLayout>
  );
};

export default VendorDashboard;