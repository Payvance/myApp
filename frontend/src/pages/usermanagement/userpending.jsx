import React, { useCallback, useEffect, useState } from 'react';
import SuperAdminLayout from '../../layouts/SuperAdminLayout';
import PageHeader from '../../components/common/pageheader/PageHeader';
import { DataTable } from '../../components/common/table';
import { USERS_COLUMNS } from '../../config/columnConfig';
import { userServices } from '../../services/apiService';
import './userpending.css';
import { toast } from 'react-toastify';

// user pending component
const UserPending = () => {
  const [tableData, setTableData] = useState({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
  const [loading, setLoading] = useState(false);
  const [selectedRows, setSelectedRows] = useState([]);
    // Fetch pending users data with pagination, sorting, and filters
  const fetchData = useCallback(async (params = {}) => {
    try {
      setLoading(true);
      const p = {
        page: params.page || 0,
        size: params.size || 10,
        sortBy: params.sortBy,
        sortDir: params.sortDir,
        search: params.filters?.search,
      };

      // API call for inactive/pending users
      const response = await userServices.getInactiveUsersPagination(p);
      const data = response?.data || {};
      // Update table data
      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || 0,
        size: data.size || 10,
      });
    } catch (err) {
      toast.error('Failed to fetch pending users', err);
      setTableData({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
    } finally {
      setLoading(false);
    }
  }, []);
  // Initial data fetch 
  useEffect(() => {
    fetchData({ page: 0, size: 10 });
  }, [fetchData]);
  // Render component
  return (
    <SuperAdminLayout>
      <div className="user-page">
        <PageHeader title="Pending Approval" subtitle="View users For pending approval" />

        <div className="user-table-container">
          <DataTable
            data={tableData}
            columns={USERS_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            basePath="/users/pending"
            primaryKeys={["id"]}
            selectableRows={false}
            onSelectionChange={setSelectedRows}
            showActions={true}
            showEditButton={true}
            showViewButton={false}
            className="user-data-table"
          />          
        </div>
      </div>
    </SuperAdminLayout>
  );
};

export default UserPending;
