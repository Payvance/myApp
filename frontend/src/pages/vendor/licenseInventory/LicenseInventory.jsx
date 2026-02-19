import React, { useCallback, useEffect } from 'react';
import VendorLayout from '../../../layouts/VendorLayout';
import { DataTable } from '../../../components/common/table';
import { LICENSE_BATCH_COLUMNS, VENDOR_BATCH_APPROVALS_COLUMNS } from '../../../config/columnConfig';
import { vendorLicenseServices, planServices } from '../../../services/apiService';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import PopUp from '../../../components/common/popups/PopUp';
import InputField from '../../../components/common/inputfield/InputField';
import Button from '../../../components/common/button/Button';
import OptionInputBox from '../../../components/common/optioninputbox/OptionInputBox';
import { useSelector } from 'react-redux';
import { useLocation, useNavigate, useParams, NavLink } from "react-router-dom";
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';

import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
// Theme CSS
import '../../../theme/LightTheme.css';
import './LicenseInventory.css';

const LicenseInventory = () => {

  // Helper to standardise HTML date input format
  const normalizeDateForInput = (dateValue) => {
    if (!dateValue) return '';
    const parsedDate = new Date(dateValue);
    if (isNaN(parsedDate.getTime())) return '';
    return parsedDate.toISOString().split('T')[0];
  };

  // ==========================================
  // 1. ROUTING & MODE DETECTION
  // ==========================================
  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams();

  const MODE = {
    CREATE: 'create',
    EDIT: 'edit',
    VIEW: 'view',
  };

  const [mode, setMode] = React.useState(MODE.CREATE);

  // Derived helpers
  const isCreateMode = mode === MODE.CREATE;
  const isEditMode = mode === MODE.EDIT;
  const isViewMode = mode === MODE.VIEW;
  const isEditOrViewMode = isEditMode || isViewMode;

  // Detect mode from URL (Single Source of Truth)
  useEffect(() => {
    if (location.pathname.endsWith('/edit') && id) {
      setMode(MODE.EDIT);
    } else if (location.pathname.endsWith('/view') && id) {
      setMode(MODE.VIEW);
    } else {
      setMode(MODE.CREATE);
    }
  }, [location.pathname, id]);


  // ==========================================
  // 2. STATE MANAGEMENT
  // ==========================================
  const [isCreatePopupOpen, setIsCreatePopupOpen] = React.useState(false);
  const [isSubmitting, setIsSubmitting] = React.useState(false);

  // User Context
  const roleId = useSelector((state) => state.auth.roleId);
  const getUserId = useSelector((state) => state.auth.userId);
  const isSuperAdmin = roleId === 1;

  // Layout selection
  const Layout = isSuperAdmin ? SuperAdminLayout : VendorLayout;

  // Status for SuperAdmin
  const queryParams = new URLSearchParams(location.search);
  const isApproving = queryParams.get('approving') === 'true' && isSuperAdmin;
  const [selectedBatchId, setSelectedBatchId] = React.useState(null);

  // Metadata State
  const [gstRate, setGstRate] = React.useState(null);
  const [vendorDiscount, setVendorDiscount] = React.useState(null);

  // Dropdown Options
  const [softwareTypeOptions] = React.useState([{ code: 'tally', value: 'Tally' }]);
  const [planOptions, setPlanOptions] = React.useState([]);
  const [plansLoaded, setPlansLoaded] = React.useState(false);
  const [loadingPlans, setLoadingPlans] = React.useState(false);

  // Form Data
  const initialFormState = {
    softwareType: 'Tally',
    planId: '',
    batchSize: '',
    basePrice: '',
    discountValue: '',
    totalCost: '',
    costPrice: '',
    status: 'Pending payment',
    paymentMode: '',
    paymentDate: '',
    utrTrnNo: '',
    remark: '',
  };
  const [formData, setFormData] = React.useState(initialFormState);
  const [paymentBase64, setPaymentBase64] = React.useState("");
  const [uploadedFileName, setUploadedFileName] = React.useState('');

  // Table State
  const [tableData, setTableData] = React.useState({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 10,
  });
  const [loading, setLoading] = React.useState(false);

const REQUIRED_FIELDS = [
  'planId',
  'batchSize',
  'basePrice',
  'totalCost',
  'costPrice',
  'paymentMode',
  'paymentDate',
  'utrTrnNo',
];


  // State for issue popup
  const [isIssuePopupOpen, setIsIssuePopupOpen] = React.useState(false);
  const [issueBatchData, setIssueBatchData] = React.useState(null);
  const [isIssuing, setIsIssuing] = React.useState(false);
  const isIssuingRef = React.useRef(false);
  const [isCheckingEligibility, setIsCheckingEligibility] = React.useState(false);
  const [eligibilityData, setEligibilityData] = React.useState(null);

  // State for success popup
  const [isSuccessPopupOpen, setIsSuccessPopupOpen] = React.useState(false);
  const [generatedLicenseData, setGeneratedLicenseData] = React.useState(null);

  // Issue form data
  const [issueFormData, setIssueFormData] = React.useState({
    email: '',
    phone: '',
    name: ''
  });

  const [paymentFile, setPaymentFile] = React.useState(null);


  // Fetch Active Plans (Always needed for dropdown)
  const fetchActivePlans = useCallback(async () => {
    try {
      setLoadingPlans(true);
      // SuperAdmins should see all plans, Vendors only active ones
      const response = isSuperAdmin
        ? await planServices.getAllPlans()
        : await vendorLicenseServices.getActivePlans();

      const plans = (response.data || []).map(plan => ({
        code: String(plan.id || plan.code || plan.planId), // Support multiple ID formats
        value: plan.name || plan.value || plan.planName || `Plan ${plan.id}`
      }));
      setPlanOptions(plans);
      setPlansLoaded(true); // Flag success
      return plans;
    } catch (error) {
      console.error("Error fetching plans", error);
      return [];
    } finally {
      setLoadingPlans(false);
    }
  }, [isSuperAdmin]);


  // ==========================================
  // 4. CREATE MODE LOGIC
  // ==========================================

  // ONLY used when clicking "Create License Batch" button
  const handleOpenCreatePopup = () => {
    // Force Create Mode ONLY here
    if (!isEditOrViewMode) {
      setMode(MODE.CREATE);
      setFormData(initialFormState);
      setUploadedFileName('');
      setVendorDiscount(null);
      setGstRate(null);

      // Fetch dynamic pricing data for new request
      // Non-blocking fetches for better UX responsiveness
      fetchActivePlans().catch(e => console.warn("Plans fetch failed", e));

      if (getUserId) {
        vendorLicenseServices.getVendorDiscount(getUserId)
          .then(res => {
            if (res.data) {
              setVendorDiscount(res.data);
              setFormData(prev => ({ ...prev, discountValue: String(res.data.value) }));
            }
          }).catch(e => console.warn("Discount fetch failed", e));
      }

      vendorLicenseServices.getLatestGstRate()
        .then(res => {
          if (res.data) setGstRate(res.data.rate);
        }).catch(e => console.warn("GST fetch failed", e));
    }
    setIsCreatePopupOpen(true);
  };

  const handleClosePopup = () => {
    setIsCreatePopupOpen(false);
    if (isEditOrViewMode) {
      navigate('/licenseinventory')
    }
  };

  // Handle issue license batch
  const handleIssueBatch = (rowData) => {
    setIssueBatchData(rowData);
    setIsIssuePopupOpen(true);
    // Reset form data and eligibility
    setIssueFormData({
      email: '',
      phone: '',
      name: ''
    });
    setEligibilityData(null);
  };

  // Handle close issue popup
  const handleCloseIssuePopup = () => {
    setIsIssuePopupOpen(false);
    setIssueBatchData(null);
    setIssueFormData({
      email: '',
      phone: '',
      name: ''
    });
    setEligibilityData(null);
  };

  // Handle issue form input change
  const handleIssueInputChange = (e) => {
    const { name, value } = e.target;
    setIssueFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Handle eligibility check
  const handleCheckEligibility = async () => {
    const { email, phone } = issueFormData;

    // Check if either email or phone is filled
    if (!email && !phone) {
      toast.info('Please enter either email or phone to check eligibility');
      return;
    }

    setIsCheckingEligibility(true);
    try {
      console.log('Checking eligibility for:', { email, phone });
      // Send both email and phone to API, backend will use whichever is available
      const response = await vendorLicenseServices.checkEligibility(email, phone);
      const data = response.data;
      console.log('Eligibility response:', data);
      console.log('Is eligible?', data.eligible);

      setEligibilityData(data);

      if (data.eligible) {
        // Fill name field from API response using tenantName
        setIssueFormData(prev => ({
          ...prev,
          name: data.tenantName || ''
        }));

        setTimeout(() => {
          toast.success('Tenant eligible for license generation');
        }, 10); // disappear after 3 seconds


      } else {
        // Clear name field if not eligible
        setIssueFormData(prev => ({
          ...prev,
          name: ''
        }));
        toast.error('Tenant not eligible for license generation');
      }
    } catch (error) {
      console.error('Error checking eligibility:', error);
      console.error('Error response:', error.response?.data);
      setEligibilityData(null);
      const errorMessage = error?.response?.data?.message || 'Failed to check eligibility. Please try again.';
      toast.error(errorMessage);
    } finally {
      setIsCheckingEligibility(false);
    }
  };

  // Handle submit issue license batch
 const handleSubmitIssueBatch = async () => {
  if (!issueBatchData || isIssuingRef.current) return;

  // If eligibility was never checked
  if (!eligibilityData) {
    toast.error("Please check eligibility first by filling the email field.");
    return;
  }

  //  If message is not eligible
  if (eligibilityData.message !== "Eligible to get license") {
    toast.error("Tenant is not eligible to get license.");
    return;
  }

  isIssuingRef.current = true;
  setIsIssuing(true);

  try {
    const payload = {
      batchId: issueBatchData.id,
      issuedToEmail: eligibilityData.tenantEmail,
      issuedToPhone: eligibilityData.tenantPhone,
      redeemedTenantId: eligibilityData.tenantId,
      vendorId: eligibilityData.vendorId,
    };

    const response = await vendorLicenseServices.issueLicenseBatch(payload);

    toast.success('License generated successfully!');

    setGeneratedLicenseData(response.data);
    setIsSuccessPopupOpen(true);

    handleCloseIssuePopup();
    fetchData({ page: tableData.number, size: tableData.size });

  } catch (error) {
    toast.error(error?.response?.data?.message || "Tenant already has an active license. Cannot issue another key");
  } finally {
    setIsIssuing(false);
    isIssuingRef.current = false;
  }
};


  // Handle close success popup
  const handleCloseSuccessPopup = () => {
    setIsSuccessPopupOpen(false);
    setGeneratedLicenseData(null);
  };

  // Handle download payment proof
  const handleDownloadPaymentProof = () => {
    if (!formData.imageUpload) return;
    
    const imageData = formData.imageUpload.startsWith('data:') 
      ? formData.imageUpload 
      : `data:image/png;base64,${formData.imageUpload}`;
    
    const link = document.createElement('a');
    link.href = imageData;
    link.download = `payment_proof_${selectedBatchId || 'batch'}_${Date.now()}.${imageData.startsWith('data:application/pdf') ? 'pdf' : imageData.startsWith('data:image/') ? imageData.split('/')[1].split(';')[0] : 'png'}`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  /* =========================
     HELPERS
  ========================== */
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };


  // Fetch Common Metadata (Plans, GST, Discount)
  useEffect(() => {
    if (!isCreatePopupOpen) return;

    const fetchMetadata = async () => {
      // 1. Plans (All Modes)
      fetchActivePlans();

      // 2. GST (All Modes)
      try {
        const gstRes = await vendorLicenseServices.getLatestGstRate();
        setGstRate(gstRes.data.rate);
      } catch (e) { console.error("GST Fetch Error", e); }

      // 3. Discount (All Modes)
      if (getUserId) {
        try {
          const discountRes = await vendorLicenseServices.getVendorDiscount(getUserId);
          if (discountRes.data) {
            setVendorDiscount(discountRes.data);

            // AUTO-FILL only in CREATE mode
            if (isCreateMode) {
              setFormData(prev => ({
                ...prev,
                discountValue: String(discountRes.data.value)
              }));
            }
          }
        } catch (e) { console.error("Discount Fetch Error", e); }
      }
    };

    fetchMetadata();
  }, [isCreatePopupOpen, getUserId, fetchActivePlans, isCreateMode]);


  // Fetch Plan Price (CREATE & EDIT MODE)
  const handlePlanChange = async (e) => {
    const planId = e.target.value;
    setFormData(prev => ({ ...prev, planId, basePrice: '' }));

    // Allow price fetch in Create AND Edit modes
    if ((isCreateMode || isEditMode) && planId) {
      try {
        const res = await vendorLicenseServices.getPlanPrice(planId);
        setFormData(prev => ({ ...prev, basePrice: res.data?.amount ?? '' }));
      } catch (err) { console.error("Plan Price Error", err); }
    }
  };

  // Auto Calculate Cost (CREATE & EDIT MODE)
  useEffect(() => {
    // STRICT GUARD: DO NOT RUN IN VIEW
    if (isViewMode) return;

    const { basePrice, batchSize } = formData;
    if (!basePrice || !batchSize || !gstRate || !vendorDiscount) return;

    const base = Number(basePrice);
    const qty = Number(batchSize);
    const discVal = Number(vendorDiscount.value || 0);
    const type = vendorDiscount.type?.toUpperCase()?.replace(/\s+/g, "_");

    let discountedBase = base;
    if (type === "PERCENTAGE" || type === "SLAB_RATE") {
      discountedBase -= (base * discVal) / 100;
    } else if (type === "FLAT") {
      discountedBase -= discVal;
    }
    discountedBase = Math.max(discountedBase, 0);

    let actualPrice = discountedBase * qty;
    const gstAmount = (actualPrice * gstRate) / 100;
    const finalPrice = actualPrice + gstAmount;

    setFormData(prev => ({
      ...prev,
      totalCost: actualPrice.toFixed(2),
      costPrice: finalPrice.toFixed(2)
    }));

  }, [isViewMode, formData.basePrice, formData.batchSize, gstRate, vendorDiscount]);


  // ==========================================
  // 5. EDIT & VIEW MODE LOGIC
  // ==========================================

  useEffect(() => {
    if (!isEditOrViewMode || !id) return; // Strict Guard

    const fetchBatchData = async () => {
      setIsCreatePopupOpen(true);
      // Fetch plans first (needed for OptionInputBox mapping)
      await fetchActivePlans();

      try {
        const { data } = await vendorLicenseServices.getBatchById(id);
        if (!data) return;

        // Map fields supporting both camelCase and snake_case
        const mappedData = {
          softwareType: data.softwareType || data.software_type || 'Tally',
          planId: (data.licenseModelId || data.license_model_id || data.planId || data.plan_id)
            ? String(data.licenseModelId || data.license_model_id || data.planId || data.plan_id)
            : '',
          batchSize: data.totalActivations ?? data.total_activations ?? '',
          basePrice: data.basePrice ?? data.base_price ?? '',
          discountValue: data.discountValue ?? data.discount_value ?? '',
          totalCost: data.totalCost ?? data.total_cost ?? '',
          costPrice: data.costPrice ?? data.cost_price ?? '',
          status: data.status || 'Pending payment',
          paymentMode: data.paymentMode || data.payment_mode || '',
          paymentDate: normalizeDateForInput(data.paymentDate || data.payment_date),
          utrTrnNo: data.utrTrnNo || data.utr_trn_no || '',
          remark: data.remark || '',
          imageUpload: data.imageUpload || data.image_upload || '',
        };

        // Fallback calculation for totalCost in view mode if missing
        if (!mappedData.totalCost && mappedData.costPrice && gstRate) {
          const cp = Number(mappedData.costPrice);
          const tr = 1 + (Number(gstRate) / 100);
          mappedData.totalCost = (cp / tr).toFixed(2);
        }

        setFormData(mappedData);

        //If batch is already processed for approved or reject or completed then the edit mode will be diable
        const isProcessed = ['APPROVED', 'REJECTED', 'COMPLETED'].includes(mappedData.status?.toUpperCase());
        if (isProcessed && mode === MODE.EDIT && !isSuperAdmin) {
          setMode(MODE.VIEW);
          toast.info("This batch is processed and cannot be edited.");
        }

        // UI state for file
        if (mappedData.imageUpload) {
          setUploadedFileName('Previously uploaded file');
        } else {
          setUploadedFileName('');
        }

        // --- Auxiliary non-blocking fetches ---

        // 1. Base Price (if missing)
        if (!mappedData.basePrice && mappedData.planId) {
          vendorLicenseServices.getPlanPrice(mappedData.planId)
            .then(res => {
              if (res.data?.amount) setFormData(prev => ({ ...prev, basePrice: res.data.amount }));
            }).catch(e => console.warn("Base Price fetch failed", e));
        }

        // 2. Vendor Discount
        const targetUserId = isSuperAdmin ? (data.vendorId || data.vendor_id) : getUserId;
        if (targetUserId) {
          vendorLicenseServices.getVendorDiscount(targetUserId)
            .then(res => {
              if (res.data) {
                setVendorDiscount(res.data);
                if (!mappedData.discountValue) {
                  setFormData(prev => ({ ...prev, discountValue: String(res.data.value) }));
                }
              }
            }).catch(e => console.warn("Discount fetch failed", e));
        }

        // 3. GST (if missing)
        if (!gstRate) {
          vendorLicenseServices.getLatestGstRate()
            .then(res => setGstRate(res.data.rate))
            .catch(e => console.warn("GST fetch failed", e));
        }

      } catch (err) {
        console.error("Failed to fetch batch details", err);
      }
    };

    fetchBatchData();
  }, [isEditOrViewMode, id, fetchActivePlans, getUserId]);


  // ==========================================
  // 6. FORM HANDLERS
  // ==========================================

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploadedFileName(file.name);

    // ... file validation logic ...
    const reader = new FileReader();
    reader.onload = () => setPaymentBase64(reader.result.split(",")[1]);
    reader.readAsDataURL(file);
  };

  const handleSubmit = async () => {
    // URL-based detection (Single Source of Truth)
    const isEditRoute = location.pathname.endsWith('/edit') && id;
    const isViewRoute = location.pathname.endsWith('/view');

    if (isViewRoute) return;
    setIsSubmitting(true);

    try {
      const payload = {
        vendorId: Number(getUserId),
        licenseModelId: Number(formData.planId),
        vendorDiscountId: vendorDiscount?.id ?? null,
        totalActivations: Number(formData.batchSize),
        costPrice: Number(formData.costPrice),
        resalePrice: 0,
        currency: "INR",
        paymentMode: formData.paymentMode,
        paymentDate: formData.paymentDate,
        utrTrnNo: formData.utrTrnNo,
        remark: formData.remark,
        imageUpload: paymentBase64 // Optional in Edit
      };

      if (isEditRoute) {
        await vendorLicenseServices.updateBatch(id, payload);
      } else {
        await vendorLicenseServices.createBatch(payload);
      }

      handleClosePopup();
      fetchData({ page: 0, size: 10 });

    } catch (error) {
      console.error("Submit failed", error);
    } finally {
      setIsSubmitting(false);
    }
  };




  // ==========================================
  // 7. SUPERADMIN ACTIONS
  // ==========================================

  const handleApprove = (rowData) => {
    setSelectedBatchId(rowData.id);
    setMode(MODE.VIEW);
    navigate(`/vendor/license-batches/${rowData.id}/view?approving=true`);
  };

  const handleReject = (rowData) => {
    setSelectedBatchId(rowData.id);
    setMode(MODE.VIEW);
    navigate(`/vendor/license-batches/${rowData.id}/view?approving=true`);
  };

  const handleViewBatch = (rowData) => {
    setMode(MODE.VIEW);
    navigate(`/vendor/license-batches/${rowData.id}/view`);
  };

  const handleSubmitDecision = async () => {
    if (!selectedBatchId || !formData.status) return;
    // Normalize status to uppercase for API
    const decision = formData.status.toUpperCase() === 'APPROVED' ? 'APPROVED' : 'REJECTED';

    try {
      setLoading(true);
      const response = await vendorLicenseServices.updateBatchStatus(selectedBatchId, decision);
      await fetchData({ page: 0, size: 10 });
    
      if (decision === 'APPROVED') {
        toast.success("Batch Approved successfully", {
          autoClose: 1000 });
      } else {
        toast.error('Batch Rejected',{autoClose: 1000});
      }
      handleClosePopup();
    }
    catch (error) {
      console.error("Error submitting decision:", error);
      const errorMessage = error?.response?.data?.message || error?.message || "Failed to update batch status";
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };


  // ==========================================
  // 8. TABLE LOGIC
  // ==========================================
  const fetchData = useCallback(async ({ page, size, sortField, sortOrder, filters }) => {
    try {
      setLoading(true);
      const apiParams = {
        page: page || 0,
        size: size || 10,
        sortBy: sortField || 'createdAt',
        sortDir: sortOrder || 'desc',
        ...(filters?.search && { status: filters.search }),
      };

      const response = await vendorLicenseServices.getLicenseBatches(apiParams);
      const data = response.data;

      let transformedContent = data.content || [];

      // Fetch all plans to map names (role-specific endpoint handled in fetchActivePlans)
      const allPlans = await fetchActivePlans();
      let planMap = {};
      allPlans.forEach(p => {
        planMap[p.code] = p.value;
      });

      transformedContent = transformedContent.map(batch => {
        const batchPlanId = String(batch.planId || batch.licenseModelId);
        const planName = planMap[batchPlanId] || `Plan ${batchPlanId}`;

        const usedActivations = batch.usedActivations ?? batch.used_activations ?? 0;

        if (isSuperAdmin) {
          const vendorName = batch.vendorName || batch.vendor_name || `Vendor ${batch.vendorId || batch.vendor_id}`;

          const paymentMode = (() => {
            const pMode = batch.paymentMode || batch.payment_mode;
            const utr = batch.utrTrnNo || batch.utr_trn_no;
            return pMode && utr ? `${pMode} (${utr})` : (pMode || 'N/A');
          })();

          const totalActivations = batch.totalActivations ?? batch.total_activations ?? 0;
          const remainingActivations = totalActivations - usedActivations;
          return { ...batch, planName, vendorName, paymentMode, usedActivations, remainingActivations };
        }

        // Unified Action Logic for Vendors (Local to this page to avoid common component changes)
        const isProcessed = ['APPROVED', 'REJECTED', 'COMPLETED'].includes(batch.status?.toUpperCase());
        const actionList = (
          <div className="table-actions">
            <NavLink
              to={`/vendor/license-batches/${batch.id}/view`}
              className="action-button view-button"
              title="View"
            >
              <i className="bi bi-eye"></i>
            </NavLink>

            {isProcessed ? (
              <button
                disabled
                className="action-button edit-button"
                title="Edit (Processed)"
              >
                <i className="bi bi-pencil"></i>
              </button>
            ) : (
              <NavLink
                to={`/vendor/license-batches/${batch.id}/edit`}
                className="action-button edit-button"
                title="Edit"
              >
                <i className="bi bi-pencil"></i>
              </NavLink>
            )}

            <button
              onClick={() => handleIssueBatch(batch)}
              className="action-button issue-button"
              title="Issue"
            >
              <i className="bi bi-key"></i>
            </button>
          </div>
        );

        const totalActivations = batch.totalActivations ?? batch.total_activations ?? 0;
        const remainingActivations = totalActivations - usedActivations;
        return { ...batch, planName, usedActivations, actionList, remainingActivations };
      });

      setTableData({
        content: transformedContent,
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0,
        number: data.number || 0,
        size: data.size || 10,
      });
    } catch (error) {
      console.error("Table Fetch Error", error);
    } finally {
      setLoading(false);
    }
  }, [isSuperAdmin]);


  const isFormValid = React.useMemo(() => {
  // View mode never needs submit
  if (isViewMode) return true;

  // Approving mode (SuperAdmin) only needs status
  if (isApproving) {
    return Boolean(formData.status);
  }

  // Create / Edit validation
  return REQUIRED_FIELDS.every(field => {
    const value = formData[field];
    return value !== undefined && value !== null && String(value).trim() !== '';
  });
}, [
  formData,
  isViewMode,
  isApproving
]);


  return (
    <Layout>
      <div className="license-inventory-content">
        <PageHeader
          title={isSuperAdmin ? "Vendor Batch Approvals" : "License Batches"}
          subtitle={isSuperAdmin ? "Review and manage vendor batch requests" : "Manage your license batch requests"}
          button={!isSuperAdmin && (
            <button className="create-plan-btn" onClick={handleOpenCreatePopup}>
              Request License Batch
            </button>
          )}
        />

        <div className="license-inventory-table">
          <DataTable
            data={tableData}
            columns={isSuperAdmin ? VENDOR_BATCH_APPROVALS_COLUMNS : [
              ...LICENSE_BATCH_COLUMNS,
              { accessorKey: 'actionList', header: 'Actions', width: 120}
            ]}
            fetchData={fetchData}
            loading={loading}
            primaryKeys={['id']}
            showActions={isSuperAdmin}
            showViewButton={isSuperAdmin}
            showEditButton={false}
            showApproveButton={isSuperAdmin}
            showRejectButton={false}
            showIssueButton={false}
            approveButtonDisabled={isSuperAdmin ? (rowData) => ['APPROVED', 'REJECTED'].includes(rowData.status?.toUpperCase()) : false}
            onApprove={handleApprove}
            onReject={handleReject}
            onView={handleViewBatch}
            onIssue={handleIssueBatch}
            className="license-table"
          />
        </div>

        <PopUp
          isOpen={isCreatePopupOpen}
          onClose={handleClosePopup}
          title={
            isCreateMode ? 'Request License Batch' :
              isEditMode ? 'Edit License Batch' : 'View License Batch'
          }
          size="large"
        >
          <div className="create-plan-form">
            <div className="form-grid">
              <OptionInputBox
                label="Software Type"
                name="softwareType"
                value={formData.softwareType}
                onChange={handleInputChange}
                options={softwareTypeOptions}
                required
                disabled={isViewMode} // View: Disabled
                classN="large"
              />

              <OptionInputBox
                label="Plan"
                name="planId"
                value={formData.planId}
                onChange={handlePlanChange}
                options={planOptions}
                required
                classN="large"
                disabled={isViewMode || loadingPlans}
              />

              <InputField
                label="Batch Size"
                name="batchSize"
                value={formData.batchSize}
                onChange={handleInputChange}
                validationType="NUMBER_ONLY"
                required
                classN="large"
                disabled={isViewMode} // Editable in Edit
                min={1}
                max={1000}
              />

              <InputField
                label="Base Price (INR)"
                name="basePrice"
                value={formData.basePrice}
                validationType="NUMBER_ONLY"
                required
                disabled={true}
                classN="large"
              />

              <InputField
                label="Discount Value"
                name="discountValue"
                value={formData.discountValue}
                onChange={handleInputChange}
                validationType="NUMBER_ONLY"
                required
                disabled={true}
                classN="large"
              />

              <InputField
                label="Total Cost (Excl. GST)"
                name="totalCost"
                value={formData.totalCost}
                validationType="NUMBER_ONLY"
                required
                disabled={true}
                classN="large"
              />

              <InputField
                label="GST (%)"
                name="gstPercentage"
                value={gstRate ?? ""}
                disabled={true}
                classN="large"
              />

              <InputField
                label="Cost Price (INR)"
                name="costPrice"
                value={formData.costPrice}
                onChange={handleInputChange}
                validationType="NUMBER_ONLY"
                required
                disabled={true}
                classN="large"
              />
            </div>

            <div style={{paddingTop: "20px"}}>
              <h3 className="section-title">Payment Details</h3>
              <div className="form-grid">
                <OptionInputBox
                  label="Status"
                  name="status"
                  value={formData.status}
                  onChange={handleInputChange}
                  options={[
                    { code: 'Pending payment', value: 'Pending payment' },
                    { code: 'Approved', value: 'Approved' },
                    { code: 'Rejected', value: 'Rejected' },
                  ]}
                  disabled={!(isSuperAdmin && isApproving)}
                  classN="large"
                />

                <OptionInputBox
                  label="Payment Mode"
                  name="paymentMode"
                  value={formData.paymentMode}
                  onChange={handleInputChange}
                  options={[
                    { code: 'NEFT', value: 'NEFT' },
                    { code: 'RTGS', value: 'RTGS' },
                    { code: 'IMPS', value: 'IMPS' },
                    { code: 'UPI', value: 'UPI' },
                  ]}
                  required
                  disabled={isViewMode}
                  classN="large"
                />

                <InputField
                  label="UTR Number"
                  name="utrTrnNo"
                  value={formData.utrTrnNo}
                  onChange={handleInputChange}
                  required
                  disabled={isViewMode}
                  validationType='UTR'
                  classN="large"
                  max={22}
                />

                <InputField
                  label="Remark"
                  name="remark"
                  value={formData.remark}
                  onChange={handleInputChange}
                  required
                  classN="large"
                  disabled={isViewMode}
                  validationType='ALPHANUMERIC'
                  max={50}
                />

                <InputField
                  label="Payment Date"
                  name="paymentDate"
                  value={formData.paymentDate}
                  type="date"
                  onChange={handleInputChange}
                  required
                  disabled={isViewMode}
                  classN="large"
                />
              </div>

              <div className="payment-proof">
                <label className="input-label">Payment Proof</label>
                <input
                  type="file"
                  className="file-input"
                  accept="image/png,image/jpeg,application/pdf"
                  onChange={handleFileChange}
                  disabled={isViewMode}
                />
                {uploadedFileName && (
                  <div className="uploaded-file-info" style={{ marginTop: '10px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' }}>
                      <p className="uploaded-file-name" style={{ fontSize: '0.9rem', color: '#666', margin: 0 }}>
                        <strong>File:</strong> {uploadedFileName}
                      </p>
                      {isSuperAdmin && formData.imageUpload && (
                        <button
                          onClick={handleDownloadPaymentProof}
                          style={{
                            padding: '4px 8px',
                            fontSize: '0.8rem',
                            backgroundColor: '#6366f1',
                            color: 'white',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer'
                          }}
                        >
                          Download
                        </button>
                      )}
                    </div>
                    {/* If we have the image data (base64) or it was previously uploaded, provide a way to see it */}
                    {formData.imageUpload && (
                      <div className="image-preview" style={{ marginTop: '10px' }}>
                        <img
                          src={formData.imageUpload.startsWith('data:') ? formData.imageUpload : `data:image/png;base64,${formData.imageUpload}`}
                          alt="Payment Proof"
                          style={{ maxWidth: '100%', maxHeight: '200px', borderRadius: '4px', border: '1px solid #ddd' }}
                          onError={(e) => {
                            e.target.style.display = 'none';
                            e.target.nextSibling.style.display = 'block';
                          }}
                        />
                        <a
                          href={formData.imageUpload.startsWith('data:') ? formData.imageUpload : `data:image/png;base64,${formData.imageUpload}`}
                          download="payment_proof"
                          className="view-proof-link"
                          style={{ display: 'none', color: '#6366f1', textDecoration: 'underline', fontSize: '0.9rem' }}
                        >
                          View Payment Proof
                        </a>
                      </div>
                    )}
                  </div>
                )}
              </div>
            </div>

            <div className="popup-footer">
              {!isViewMode && !isApproving && (
                <button
                  className="btn-full btn-primary"
                  onClick={handleSubmit}
                  disabled={isSubmitting || !isFormValid}
                >
                  {isCreateMode ? 'Create License Batch' : 'Edit License Batch'}
                </button>
              )}
              {isApproving && (
                <button
                  className="btn-full btn-primary"
                  onClick={handleSubmitDecision}
                  disabled={loading}
                >
                  Submit Decision
                </button>
              )}
              {(isViewMode && !isApproving) && (
                <button
                  className="btn-full btn-primary"
                  onClick={handleClosePopup}
                >
                  Close
                </button>
              )}
            </div>
          </div>
        </PopUp>

        {/* Issue License Batch Popup */}
        <PopUp
          isOpen={isIssuePopupOpen}
          onClose={handleCloseIssuePopup}
          title="Issue License Batch To Tenant"
          size="medium"
        >
          <div className="issue-batch-form">
            <div className="issue-form-fields">
              <InputField
                label="Email"
                name="email"
                value={issueFormData.email}
                onChange={handleIssueInputChange}
                onBlur={handleCheckEligibility}
                validationType="EMAIL"
                classN="large"
              />

              <div className="or-divider">
                <span className="or-text">OR</span>
              </div>

              <InputField
                label="Phone"
                name="phone"
                value={issueFormData.phone}
                onChange={handleIssueInputChange}
                onBlur={handleCheckEligibility}
                validationType="PHONE"
                classN="large"
              />

              {/* <InputField
                label="Name"
                name="name"
                value={issueFormData.name}
                onChange={handleIssueInputChange}
                required
                classN="large"
                disabled={!!eligibilityData?.name}
              /> */}
            </div>

            <div className="issue-batch-actions">
              <Button
                text="Generate"
                onClick={handleSubmitIssueBatch}
                disabled={isIssuing}

              />
            </div>
          </div>
        </PopUp>

        {/* License Generated Success Popup */}
        <PopUp
          isOpen={isSuccessPopupOpen}
          onClose={handleCloseSuccessPopup}
          title="License Generated Successfully"
          size="medium"
        >
          <div className="license-success-form">
            <div className="success-form-fields">
              <div className="client-details-box">
                <h4 className="client-details-title">Client Details</h4>
                <div className="client-details-content">
                  <div className="client-detail-item">
                    <span className="detail-label">Name:</span>
                    <span className="detail-value">{generatedLicenseData?.redeemedTenantName || 'N/A'}</span>
                  </div>

                  <div className="client-detail-item">
                    <span className="detail-label">Email:</span>
                    <span className="detail-value">{generatedLicenseData?.issuedToEmail || 'N/A'}</span>
                  </div>

                  <div className="client-detail-item">
                    <span className="detail-label">Mobile:</span>
                    <span className="detail-value">{generatedLicenseData?.issuedToPhone || 'N/A'}</span>
                  </div>
                </div>
              </div>

              <div className="activation-key-box">
                <label className="field-label">Activation Key:</label>
                <div className="field-value activation-key">XXXX-XXXX-XXXX-{generatedLicenseData?.plainCodeLast4 || 'N/A'}</div>
              </div>
            </div>

            <div className="success-actions">
              <Button
                text="Close"
                onClick={handleCloseSuccessPopup}
              />
            </div>
          </div>
        </PopUp>
      </div>
    </Layout>
  );
};

export default LicenseInventory;