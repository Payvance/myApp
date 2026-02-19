// src/pages/superadmin/discount&offers/RefferalConfig.jsx
import React, { useState, useCallback } from 'react';
// css file import
import './VenderDiscount.css';
// super admin layout import
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
// page header import
import PageHeader from '../../../components/common/pageheader/PageHeader';
// data table import
import { DataTable } from '../../../components/common/table';
import { REFERRAL_PROGRAM_COLUMNS } from '../../../config/columnConfig';
// referral program services import
import { referralProgramServices } from '../../../services/apiService';
// Theme CSS
import '../../../theme/LightTheme.css';

// referral config component 
const RefferalConfig = () => {
  // State for table data
  const [tableData, setTableData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = useState(false);

   // Fetch data function - matches Spring Boot API contract
  const fetchData = useCallback(async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);
 
      // Since getAllReferrals doesn't support pagination yet, we'll fetch all and paginate locally
      const response = await referralProgramServices.getAllReferrals({
        page,
        size,
        sortBy: sortField,
        sortDir: sortOrder,
        search: filters?.search,
      });

      const data = response.data || {};

      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || page || 0,
        size: data.size || size || 10,
      });

    } catch (error) {
      console.error('Error fetching referral programs:', error);
      setTableData({
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 10,
      });
    } finally {
      setLoading(false);
    }
  }, []);

  return (
    <SuperAdminLayout>
      <div className="referral-config-page">
        {/* Page Header Component */}
        <PageHeader
          title="Referral Configuration"
          subtitle="Manage referral codes and offers"
        />

        {/* Data Table */}
        <div className="referral-config-table">
          <DataTable
            data={tableData}
            columns={REFERRAL_PROGRAM_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            basePath="/superadmin/referral-config"
            primaryKeys={['id']}
            showActions={false}
            showEditButton={true}
            showViewButton={false}
            editButtonDisabled={false}
            viewButtonDisabled={false}
            className="referral-config-data-table"
          />
        </div>
      </div>

    </SuperAdminLayout>
  );
};

export default RefferalConfig;