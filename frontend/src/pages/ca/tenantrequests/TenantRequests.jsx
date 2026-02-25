import React, { useState, useEffect } from "react";
import PageHeader from "../../../components/common/pageheader/PageHeader";
import CALayout from "../../../layouts/CALayout";
import DataTable from "../../../components/common/table/DataTable";
import PopUp from "../../../components/common/popups/PopUp";
import Button from "../../../components/common/button/Button";
import { caTenantServices } from "../../../services/apiService";
import { toast } from "react-toastify";
import { CA_TENANT_REQUESTS_COLUMNS } from "../../../config/columnConfig";

const TenantRequests = () => {
  const [data, setData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = useState(false);
  const [actionPopup, setActionPopup] = useState(false);
  const [selectedTenant, setSelectedTenant] = useState(null);

  const fetchData = async ({ page, size, sortField, sortOrder, filters }) => {
    setLoading(true);
    try {
      const caUserId = localStorage.getItem("user_id");
      const params = {
        page,
        size,
        caUserId,
      };

      if (sortField && sortOrder) {
        params.sortBy = sortField;
        params.sortDir = sortOrder;
      }

      if (filters?.search) {
        params.search = filters.search;
      }

      const response = await caTenantServices.getTenantsForCa(params);

      if (response.data && response.data.content) {
        setData({
          content: response.data.content,
          totalElements: response.data.totalElements || 0,
          totalPages: response.data.totalPages || 0,
          number: response.data.number || 0,
          size: response.data.size || 10,
        });
      } else {
        setData({
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: 10,
        });
      }
    } catch (error) {
      console.error("Error fetching tenants:", error);
      toast.error("Failed to fetch tenant requests");
      setData({
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 10,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleOpenActionPopup = (tenantData) => {
    setSelectedTenant(tenantData);
    setActionPopup(true);
  };

  const handleConfirmApprove = async () => {
    try {
      const caUserId = localStorage.getItem("user_id");
      const payload = {
        caUserId: caUserId,
        tenantId: selectedTenant.id,
        isView: 1
      };

      await caTenantServices.updateTenantStatus(payload);
      toast.success("Tenant approved successfully");
      setActionPopup(false);
      setSelectedTenant(null);
      fetchData({ page: 0, size: 10 });
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.message || "Failed to approve tenant";
      toast.error(errorMessage);
    }
  };

  const handleConfirmReject = async () => {
    try {
      const caUserId = localStorage.getItem("user_id");
      const payload = {
        caUserId: caUserId,
        tenantId: selectedTenant.id,
        isView: 2
      };

      await caTenantServices.updateTenantStatus(payload);
      toast.success("Tenant rejected successfully");
      setActionPopup(false);
      setSelectedTenant(null);
      fetchData({ page: 0, size: 10 });
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.message || "Failed to reject tenant";
      toast.error(errorMessage);
    }
  };

  const handleCloseActionPopup = () => {
    setActionPopup(false);
    setSelectedTenant(null);
  };

  useEffect(() => {
    fetchData({ page: 0, size: 10 });
  }, []);

  return (
    <CALayout>
      <div className="license-inventory-content">
        <PageHeader
          title="Tenant Requests"
        />

        <DataTable
          data={data}
          columns={CA_TENANT_REQUESTS_COLUMNS}
          fetchData={fetchData}
          loading={loading}
          showActions={true}
          showApproveButton={true}
          showRejectButton={false}
          onApprove={handleOpenActionPopup}
          approveButtonDisabled={(row) => row.isView !== 0}
        />

        <PopUp
          isOpen={actionPopup}
          onClose={handleCloseActionPopup}
          title="Tenant Request Action"
          size="small"
        >
          <div className="action-popup-content">
            {selectedTenant && (
              <div className="tenant-details" style={{ marginBottom: '20px', padding: '10px', backgroundColor: 'var(--bg-secondary)', borderRadius: '8px' }}>
                <p style={{ margin: '5px 0', fontSize: '14px' }}><strong>Name:</strong> {selectedTenant.name}</p>
                <p style={{ margin: '5px 0', fontSize: '14px' }}><strong>Email:</strong> {selectedTenant.email}</p>
                <p style={{ margin: '5px 0', fontSize: '14px' }}><strong>Phone:</strong> {selectedTenant.phone}</p>
              </div>
            )}
            <div className="popup-actions" style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
              <Button
                text="Approve"
                variant="primary"
                onClick={handleConfirmApprove}
              />
              <Button
                text="Reject"
                variant="red"
                onClick={handleConfirmReject}
              />
            </div>
          </div>
        </PopUp>
      </div>
    </CALayout>
  );
};

export default TenantRequests;
