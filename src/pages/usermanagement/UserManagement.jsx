import React, { useState, useCallback } from 'react';
import SuperAdminLayout from '../../layouts/SuperAdminLayout';
import PageHeader from '../../components/common/pageheader/PageHeader';
import '../../theme/LightTheme.css';
import './user.css';
import { DataTable } from '../../components/common/table';
import { USERS_COLUMNS } from '../../config/columnConfig';
import { userServices } from '../../services/apiService';
import { toast } from 'react-toastify';
import { useSearchParams } from 'react-router-dom'; // Add this import

// user management component
const UserManagement = () => {
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

  // Fetch users data with pagination, sorting, and filters
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
      const response = await userServices.getUsersPagination(params);
      const data = response.data || {};
      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || page || 0,
        size: data.size || size || 10,
      });
    } catch (error) {
      console.error('Error fetching users:', error);
      setTableData({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 });
    } finally {
      setLoading(false);
    }
  }, [searchParams, setSearchParams]);


  // Add active status checkbox column
const columns = USERS_COLUMNS.map(col => {
  if (col.accessorKey !== 'active') return col;
  
  return {
    ...col,
    cell: ({ row }) => {
      // Get user id and current active status
      const { id, active } = row.original;
        // Handle toggle change
      const handleToggle = async () => {
        try {
          // Update user active status
          await userServices.updateUser(id, { active: !active });
          // Reload table data
          fetchData({
            page: tableData.number,
            size: tableData.size,
          });

          toast.success('User status updated');
        } catch {
          toast.error('Failed to update status');
        }
      };

      return (
        <input
          type="checkbox"
          checked={!!active}
          onChange={handleToggle}
        />
      );
    },
  };
});

  // Render component
  return (
    <SuperAdminLayout>
      <div className="user-page">
        <PageHeader
          title="User Management"
          subtitle="View and manage users in the system"
        />

        <div className="user-table-container">
          <DataTable
            data={tableData}
            columns={USERS_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            basePath="/users"
            primaryKeys={['id']}
            showActions={true}
            showEditButton={false}
            showViewButton={true}
            className="user-data-table"
          />
        </div>
      </div>
    </SuperAdminLayout>
  );
};

export default UserManagement;
