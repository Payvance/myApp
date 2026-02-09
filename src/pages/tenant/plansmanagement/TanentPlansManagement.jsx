import React, { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { TANENT_PLAN, TENANT_PLAN_USAGE_COLUMNS } from '../../../config/columnConfig';
import PageHeader from "../../../components/common/pageheader/PageHeader";
import { DataTable } from "../../../components/common/table";
import TenantLayout from "../../../layouts/TenantLayout";
import { tenantServices } from "../../../services/apiService";
import { toast } from 'react-toastify';
import Button from "../../../components/common/button/Button";

const TanentPlansManagement = () => {
    const navigate = useNavigate();

    // Table State
    const [tableData, setTableData] = React.useState({
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 10,
    });
    const [loading, setLoading] = React.useState(false);

    // Fetch Data
    const fetchPlanUsage = useCallback(async () => {
  const userId = localStorage.getItem("user_id");
  setLoading(true);

  try {
    const response = await tenantServices.getPerticularTanentInfo(userId);

    // API returns ARRAY directly
    const rows = response.data.map((item) => ({
      ...item,
      uniqueKey: item.id, // required for DataTable
    }));

    setTableData({
      content: rows,
      totalElements: rows.length,
      totalPages: 1,
      number: 0,
      size: rows.length,
    });
  } catch (error) {
    toast.error("Failed to load plan usage data.");
  } finally {
    setLoading(false);
  }
}, []);


    return (
        <TenantLayout>
            <div className="license-inventory-content">
                <PageHeader
                    title="Plan Management"
                    subtitle="Manage your Plans"
                    button={
                       <div style={{ paddingTop: '15px' }}> 
                       <Button 
                       text="Upgrade Plans"
                       onClick={() => navigate('/tenantplanss')}
                       />
                       </div>
                    }
                />

                <div className="license-inventory-table">
                    <DataTable
                        data={tableData}
                        columns={TANENT_PLAN}
                        fetchData={fetchPlanUsage}
                        loading={loading}
                        primaryKeys={['uniqueKey']}
                        showActions={false}
                        showEditButton={false}
                        showViewButton={false}
                        showIssueButton={false}
                        className="license-table"
                    />
                </div>
            </div>
        </TenantLayout>
    )
};

export default TanentPlansManagement;