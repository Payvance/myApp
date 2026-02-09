import React, { useCallback, useEffect, useState } from 'react';
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import { DataTable } from '../../../components/common/table';
import { CA_REDEMPTION_APPROVALS_COLUMNS } from '../../../config/columnConfig';
import { caServices } from '../../../services/apiService';
import './CaRedamptionsApprovals.css';

const CaRedamptionaApprovals = () => {
  // State for table data
  const [tableData, setTableData] = useState({
    content: [
      {
        id: 1,
        caName: 'John Smith',
        referrals: 15,
        amount: 7500.00,
        bankDetails: 'HDFC Bank - ****1234',
        status: 'PENDING',
        createdAt: '2024-01-15T10:30:00Z'
      },
      {
        id: 2,
        caName: 'Sarah Johnson',
        referrals: 8,
        amount: 4000.00,
        bankDetails: 'ICICI Bank - ****5678',
        status: 'PENDING',
        createdAt: '2024-01-14T15:45:00Z'
      },
      {
        id: 3,
        caName: 'Michael Brown',
        referrals: 12,
        amount: 6000.00,
        bankDetails: 'SBI Bank - ****9012',
        status: 'PENDING',
        createdAt: '2024-01-13T09:20:00Z'
      },
      {
        id: 4,
        caName: 'Emily Davis',
        referrals: 6,
        amount: 3000.00,
        bankDetails: 'Axis Bank - ****3456',
        status: 'PENDING',
        createdAt: '2024-01-12T14:15:00Z'
      },
      {
        id: 5,
        caName: 'Robert Wilson',
        referrals: 20,
        amount: 10000.00,
        bankDetails: 'PNB Bank - ****7890',
        status: 'PENDING',
        createdAt: '2024-01-11T11:30:00Z'
      }
    ],
    totalElements: 5,
    totalPages: 1,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = useState(false);

  // Fetch data function
  const fetchData = useCallback(async ({ page = 0, size = 10, sortField, sortOrder }) => {
    try {
      setLoading(true);

      const params = {
        page,
        size,
        sortBy: sortField || 'createdAt',
        sortDir: sortOrder || 'desc',
      };

      const response = await caServices.redemptionpending(params);
      const data = response?.data || {};

      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || 0,
        size: data.size || size,
      });
    } catch (error) {
      console.error('Error fetching CA redemption approvals:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  // Initial data fetch
  useEffect(() => {
    fetchData({ page: 0, size: 10 });
  }, [fetchData]);

  return (
    <SuperAdminLayout>
      <div className="approvals-container">
        <PageHeader
          title="CA Redemption Approvals"
          subtitle="Review and manage CA redemption requests"
        />
        
        <div className="approvals-content">
          <div className="ca-redemptions-table">
            <DataTable
              data={tableData}
              columns={CA_REDEMPTION_APPROVALS_COLUMNS}
              fetchData={fetchData}
              loading={loading}
              showEditButton={false}
              showViewButton={false}
              showApproveButton={true}
              showRejectButton={true}
              className="ca-redemptions-data-table"
            />
          </div>
        </div>
      </div>
    </SuperAdminLayout>
  );
};

export default CaRedamptionaApprovals;