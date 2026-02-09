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
 * Neha Tembhe           1.0.0         13/01/2026   UI of redemption pending with api integration.
 *
 **/
import {useCallback, useEffect, useState} from "react";
import CALayout from "../../../layouts/CALayout";
import PageHeader from "../../../components/common/pageheader/PageHeader";
import { DataTable } from "../../../components/common/table";
import { REDEMPTION_COLUMNS } from '../../../config/columnConfig';
import { caServices } from '../../../services/apiService';
import "bootstrap-icons/font/bootstrap-icons.css";
import "../../../theme/LightTheme.css";
import "./RedemptionPending.css";

const RedemptionPending = () => {
  const [tableData, setTableData] = useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });

  const [loading, setLoading] = useState(false);
  const [selectedRows, setSelectedRows] = useState([]);

  /* ---------------------------
     Fetch data with status=pending query parameter
     Always send status=PENDING as a query parameter
  ---------------------------- */
  const fetchData = useCallback(async (tableParams = {}) => {
    try {
      setLoading(true);
      const params = {
        page: tableParams.page || 0,
        size: tableParams.size || 10,
        status: 'pending', 
      };
      if (tableParams.sortBy) {
        params.sortBy = tableParams.sortBy;
      }
      if (tableParams.sortDir) {
        params.sortDir = tableParams.sortDir;
      }
      const response = await caServices.redemptionpending(params);
      const data = response.data;
      setTableData({
        content: data.content || [],
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || 0,
        size: data.size || 10,
      });
    } catch (error) {
      toast.error("Failed to fetched redemption pending data");
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

  /* ---------------------------
     Initial data fetch on component mount
  ---------------------------- */
  useEffect(() => {
    fetchData({ page: 0, size: 10 });
  }, [fetchData]);

  /* ---------------------------
     Selection summary
  ---------------------------- */
  const totalAmount = selectedRows.reduce(
    (sum, row) => sum + (row.rewardedAmount || 0),
    0
  );

  /* ---------------------------
     Submit redemption
  ---------------------------- */
  const handleSubmitRedemption = async () => {
    const payload = {
      referralIds: selectedRows.map((r) => r.id),
    };
    try {
      await caRedemptionServices.submitRedemption(payload);
      toast.success("Redemption request submitted successfully");
      fetchData({ page: tableData.number, size: tableData.size });
      setSelectedRows([]); 
    } catch (error) {
      toast.error("Failed to submit redemption request");
    }
  };

  return (
    <CALayout>
      <div className="redemption-content">
        <PageHeader
          title="Redemption Pending"
          subtitle="Select referrals to redeem and submit for approval"
        />

        {/* ---------------------------
            Available Referrals Table (pending status only)
        ---------------------------- */}
        <div className="redemption-table-card">
          <DataTable
            data={tableData}
            columns={REDEMPTION_COLUMNS}
            fetchData={fetchData}
            loading={loading}
            selectableRows={true}
            onSelectionChange={setSelectedRows}
            primaryKeys={["referredTenantId"]}
            showActions={false}
            showEditButton={false}
            showViewButton={false}
          />
        </div>

        {/* ---------------------------
            Redemption Summary
        ---------------------------- */}
        <div className="redemption-summary-card">
          <h3 className="section-title">Redemption Summary</h3>
          <p className="section-subtitle">
            Review your selection before submitting
          </p>

          <div className="summary-row">
            <div>
              <span className="summary-label">Selected Referrals</span>
              <div className="summary-value">{selectedRows.length}</div>
            </div>

            <div>
              <span className="summary-label">Total Rewarded Amount</span>
              <div className="summary-value">
                ₹{totalAmount.toLocaleString('en-IN', { 
                  minimumFractionDigits: 2, 
                  maximumFractionDigits: 2 
                })}
              </div>
            </div>
          </div>

          <button
            className="submit-redemption-btn"
            disabled={selectedRows.length === 0}
            onClick={handleSubmitRedemption}
          >
            <i className="bi bi-send"></i> Submit Redemption Request
          </button>

          <p className="approval-note">
            This redemption request will be sent to Super Admin for approval
          </p>
        </div>
      </div>
    </CALayout>
  );
};

export default RedemptionPending;