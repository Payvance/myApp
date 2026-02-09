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
 * Neha Tembhe           1.0.0         13/01/2026   Integrated redemption api
 *
 **/
import { useCallback, useState } from 'react';
import CALayout from '../../../layouts/CALayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import { DataTable } from '../../../components/common/table';
import { caServices } from '../../../services/apiService';
import { REDEMPTION_COLUMNS } from '../../../config/columnConfig';
import './Redemption.css';

const Redemption = () => {
  const [loading, setLoading] = useState(false);

  const [tableData, setTableData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });

  /* ===============================
     INDEX API CALL
     =============================== */
 const fetchData = useCallback(
  async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);

      const apiParams = {
        page: page || 0,
        size: size || 10,
        sortBy: sortField || 'createdAt',
        sortDir: sortOrder || 'desc',

        // Map DataTable filters → API filters
        ...(filters?.search && { status: filters.status }),
        ...(filters?.search && { referredTenantName: filters.search }),
      };

      const response = await caServices.redemption(apiParams);
      const data = response?.data;

      setTableData({
        content: data?.content || [],
        totalElements: data?.totalElements || 0,
        totalPages: data?.totalPages || 0,
        number: data?.number || 0,
        size: data?.size || 10,
      });
    } catch (error) {
      toast.error('Error fetching redemption list:', error);
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
  },
  []
);

  return (
    <CALayout>
      <div className="redemption-content">
        <PageHeader
          title="Redemption"
          subtitle="View your referral reward redemptions"
        />

        <div className="redemption-table">
          <DataTable
            data={tableData}
            columns={REDEMPTION_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            primaryKeys={['referredTenantId']}
            showActions={false}
            showEditButton={false}
            showViewButton={false}
            className="redemption-table"
          />
        </div>
      </div>
    </CALayout>
  );
};

export default Redemption;
