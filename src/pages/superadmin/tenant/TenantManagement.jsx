import React, { useState, useCallback } from 'react';
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import { DataTable } from '../../../components/common/table';
import { TENANT_USERS_COLUMNS } from '../../../config/columnConfig';
import { tenantUsersServices } from '../../../services/apiService';
import { toast } from 'react-toastify';
import '../../../theme/LightTheme.css';
import './TenantManagement.css';
import { useSearchParams } from 'react-router-dom';

const TenantManagement = () => {
  // URL Params State
  const [searchParams, setSearchParams] = useSearchParams();
  const initialPage = parseInt(searchParams.get('page') || '0', 10);
  const initialSize = parseInt(searchParams.get('size') || '10', 10);

  // Table data state
  const [tableData, setTableData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: initialPage,
    size: initialSize
  });

  const [loading, setLoading] = useState(false);

  // Fetch tenant data with pagination, sorting, and filters
  const fetchData = useCallback(async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);

      // Update URL params
      const newParams = new URLSearchParams(searchParams);
      newParams.set('page', page);
      newParams.set('size', size);
      setSearchParams(newParams, { replace: true });

      const params = {
        page: page || 0,
        size: size || 10,
        sortBy: sortField,
        sortDir: sortOrder,
        search: filters?.search,
      };
      
      // API call
      const response = await tenantUsersServices.getTenantsPagination(params);
      const data = response.data || response;
      
      // Handle different response structures
      let content = [];
      let totalElements = 0;
      let totalPages = 0;
      let number = page || 0;
      let sizeParam = size || 10;
      
      if (data.content) {
        content = data.content;
      } else if (data.tenants) {
        content = data.tenants;
      } else if (Array.isArray(data)) {
        content = data;
        totalElements = data.length;
        totalPages = 1;
      }
      
      // Extract pagination info if available
      totalElements = data.totalElements || totalElements;
      totalPages = data.totalPages || totalPages;
      number = data.number || number;
      sizeParam = data.size || sizeParam;
      
      setTableData({
        content: content,
        totalElements: totalElements,
        totalPages: totalPages,
        number: number,
        size: sizeParam,
      });
    } catch (error) {
      console.error('Error fetching tenant data:', error);
      toast.error('Failed to fetch tenant data');
      setTableData({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
    } finally {
      setLoading(false);
    }
  }, [searchParams, setSearchParams]);

  return (
    <SuperAdminLayout>
      <div className="user-page">
        <PageHeader
          title="Tenant Management"
        />

        <div className="user-table-container">
          <DataTable
            data={tableData}
            columns={TENANT_USERS_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            basePath="/users"
            expandableRows={true}
            primaryKeys={['tenantId']}
            showActions={true}
            showEditButton={true}
            showViewButton={true}
            className="tenant-data-table"
          />
        </div>
      </div>
    </SuperAdminLayout>
  );
};

export default TenantManagement;