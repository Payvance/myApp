import React from 'react';
import TenantLayout from '../../../layouts/TenantLayout';
import CommonDashboard from '../../../components/dashboard/CommonDashboard';
import '../../../theme/LightTheme.css';

const TenantDashboard = () => {
    return (
        <TenantLayout>
            <CommonDashboard />
        </TenantLayout>
    );
};

export default TenantDashboard;